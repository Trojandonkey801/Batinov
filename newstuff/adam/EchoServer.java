import java.io.*; import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.*;

public class EchoServer implements Runnable{
	Socket indivSocket;
	final static String dir = "./ServerFileDir/";
	static int port = 21839;
	/* 
	 * Files to load in various attributes into program
	 */
	static ArrayList<String> Users = new ArrayList<String>();
	static ArrayList<String> UsersAdmin = new ArrayList<String>();
	static ArrayList<Credential> Creds = new ArrayList<Credential>();
	static ArrayList<Credential> Admins = new ArrayList<Credential>();
	static ArrayList<String> Files = new ArrayList<String>();

	/**
	 * Read in Admin list to arraylist
	 */
	public static void readAdmins() throws FileNotFoundException{
		File f = new File(dir+"AdminList.txt");
		Scanner s = new Scanner(f);
		while(s.hasNextLine()){
			String read = s.nextLine();
			Scanner ss = new Scanner(read);
			String userName = ss.next();
			Users.add(userName);
			Admins.add(new Credential(true, userName,ss.next()));
		}
		s.close();
	}
	/**
	 * Read in User list to arraylist
	 */
	public static void readUsers() throws FileNotFoundException{
		File f = new File(dir+"UserList.txt");
		Scanner s = new Scanner(f);
		while(s.hasNextLine()){
			String read = s.nextLine();
			Scanner ss = new Scanner(read);
			String userName = ss.next();
			Users.add(userName);
			Creds.add(new Credential(false, userName,ss.next()));
		}
		f = new File(dir+"AdminList.txt");
		s = new Scanner(f);
		while(s.hasNextLine()){
			String read = s.nextLine();
			Scanner ss = new Scanner(read);
			String userName = ss.next();
			UsersAdmin.add(userName);
			Admins.add(new Credential(true, userName,ss.next()));
		}
		s.close();
	}
	/**
	 * Read in Files to ArrayList
	 */
	public static void readFiles() throws FileNotFoundException{
		File f = new File(dir + "UserFiles.txt");
		Scanner s = new Scanner(f);
		while(s.hasNextLine()){
			String read = s.nextLine();
			Scanner ss = new Scanner(read);
			String userFile = ss.next();
			Files.add(userFile);
		}
		s.close();
	}
	/**
	 * Main method to open a Serversocket, and accept sockets that connect in a multi-threaded fashion
	 *
	 */
	public static void main(String[] args) throws IOException {
		readUsers();
		ServerSocket ssock = new ServerSocket(port);
		System.out.println("Listening on " + port);
		int count = 0;
		while(true){
			Socket sock = ssock.accept();
			count++;
			Thread t = new Thread(new EchoServer(sock));
			t.start();
		}
	}

	/*
	 * assign socket for thread object
	 */
	public EchoServer(Socket indivSocket)throws IOException{
		this.indivSocket = indivSocket;
	}

	/*
	 * Delete file on Server directory
	 *@param topic topic of file(subdirectory file)
	 *@param fileName name of File
	 */
	private void deleteFile(String topic,String fileName)throws FileNotFoundException,IOException{
		System.out.println("inside dlete");
		File directory = new File(dir + topic + "/" + fileName);
		System.out.println("del " + dir + topic + "/" + fileName + " aaaa");
		directory.delete();
		File fileList = new File(dir+"UserFiles.txt");
		fileList.delete();
		fileList.createNewFile();
		readFiles();
	}
	/*
	 *Send list of file names.
	 */
	private void sendFileList(PrintWriter out,BufferedReader in)throws IOException{
		ArrayList<String> topics = new ArrayList<String>();
		ArrayList<String> fileNames = new ArrayList<String>();
		File F = new File(dir+"UserFiles.txt");
		Scanner S = new Scanner(F);
		String toSend = "";
		while(S.hasNextLine()){
			String readLine = S.nextLine();
			Scanner SS = new Scanner(readLine);
			boolean topic = false;
			String fileName = "";
			String fileTopic = "";
			while(SS.hasNext()){
				String nextString = SS.next();
				if(nextString.equals("|")){
					topic = true;
					continue;
				}
				if(!topic)
					fileName += nextString;
				else
					fileTopic += nextString;
			}
			if(topics.contains(fileTopic)){
				int index = topics.indexOf(fileTopic);
				fileNames.set(index,fileNames.get(topics.indexOf(fileTopic)) + "\n" + fileName);
			}
			else{
				topics.add(fileTopic);
				fileNames.add(fileName);
			}
		}
		for (int i = 0; i < topics.size(); i++) {
			toSend += topics.get(i);
			toSend += "\n";
			toSend += fileNames.get(i);
			toSend += "\n";
		}
		System.out.println(toSend);
		out.println(toSend);
		out.println("exit FileList");
	}
	private void saveFile(Socket clientSock, int filesize,String fileName,String topic) throws IOException,InterruptedException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		File dirPath = new File(dir+topic);
		File F = new File(dir + topic + "/received_" + fileName);
		if(!dirPath.exists()){
			dirPath.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(F);
		FileWriter f = new FileWriter(new File(dir+"UserFiles.txt"),true);
		f.write("received_"+fileName + " | " + topic);
		f.write("\n");
		f.close();
		byte[] buffer = new byte[4096];

		int fileSize = filesize;
		int read = 0;
		int totalRead = 0;
		int remaining = fileSize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			fos.write(buffer, 0, read);
			fos.flush();
		}
		fos.close();
		Thread.sleep(100);
	}

	public void sendFile(String file) throws IOException {
		PrintWriter p = new PrintWriter(indivSocket.getOutputStream());
		long filesize = (new File(dir + file)).length();
		DataOutputStream dos = new DataOutputStream(indivSocket.getOutputStream());
		FileInputStream fis = new FileInputStream(dir + file);
		try{
			Thread.sleep(500);
			byte[] buffer = new byte[4096];
			while (fis.read(buffer) > 0) {
				dos.write(buffer);
			}
		}
		catch (InterruptedException ex){
			System.out.println(ex.getMessage());
		}
	}

	private void sendQuestionList(PrintWriter out,BufferedReader in)throws IOException{
		ArrayList<String> questions = new ArrayList<String>();
		File F = new File(dir+"Forum.txt");
		Scanner S = new Scanner(F);
		String toSend = "";
		while(S.hasNext()){
			String s = S.next();
			while (!s.equals("Question:"))
				s = S.next();
			String question = S.nextLine();
			
			if(!questions.contains(question)){
				questions.add(question);
			}
		}	
		for (int i = 0; i < questions.size(); i++) {
			toSend += questions.get(i);
			toSend += "\n";
		}
		System.out.println(toSend);
		out.println(toSend);
		out.println("\n");
		out.println("exit questionList");
	}
	
	private void selectLesson(PrintWriter out,BufferedReader in, String lessonNumber)throws IOException{
		ArrayList<String> lesson = new ArrayList<String>();
		File F = new File(dir+"Lessons.txt");
		Scanner S = new Scanner(F);
		String toSend = "";
		System.out.println(lessonNumber);
		while(S.hasNextLine()){
			String readLine = S.nextLine();
			Scanner SS = new Scanner(readLine);
			while (SS.hasNext()) {
				String nextString = SS.next();
				if (nextString.equals(lessonNumber)) {
					lesson.add(readLine);
					while (S.hasNextLine()) {
						readLine = S.nextLine();
						if(readLine.equals("*")){
							break;
						} else {
						lesson.add(readLine);
						}
					}
				} else {
					continue;
				}
			}
		}
		if (lesson.size() < 1) {
			System.out.println("This is not a lesson number");
			out.println("This is not a lesson number");
			out.println("\n");
			out.println("exit Lessons");
		} else {
			for (int i = 0; i < lesson.size(); i++) {
				toSend += lesson.get(i);
				toSend += " ";
			}

			System.out.println(toSend);
			out.println(toSend);
			out.println("\n");
			out.println("exit Lessons");
		}
	}


private void sendLessons(PrintWriter out,BufferedReader in)throws IOException{
		ArrayList<String> titles = new ArrayList<String>();
		File F = new File(dir+"Lessons.txt");
		Scanner S = new Scanner(F);
		String toSend = "";
		while(S.hasNextLine()){
			String readLine = S.nextLine();
			Scanner SS = new Scanner(readLine);
			while(SS.hasNext()){
				String nextString = SS.next();
				if(nextString.equals("|")){	
					while(S.hasNextLine()) {
						readLine = S.nextLine();
						if(readLine.equals("*")) {
							break;
						}
						
					}
					break;
				} else {
					titles.add(nextString);
				}
			}
		titles.add("\n");
		}
		for (int i = 0; i < titles.size(); i++) {
			toSend += titles.get(i);	
			toSend += " ";
		}
		
		System.out.println(toSend);
		out.println(toSend);
		out.println("\n");
		out.println("exit Lessons");
	}
	
	public void run(){
		boolean logged = false;
		String loggedName = "";
		try (
				PrintWriter out =
				new PrintWriter(indivSocket.getOutputStream(),true);                   
				BufferedReader in = new BufferedReader(
					new InputStreamReader(indivSocket.getInputStream()));
			) {
			String inputLine = in.readLine();
			boolean exit = false;
			while (inputLine != null && !exit) {
				Scanner s = new Scanner(inputLine);
				String first = s.next();
				if(!first.equals("login") && !logged){
					System.out.println("broken because " + logged);
					break;
				}
				if(first.equals("login")){
					String name = s.next();
					loggedName = name;
					int userInd = Users.indexOf(name);
					int adminInd = UsersAdmin.indexOf(name);
					if(userInd != -1)
						if(Creds.get(userInd).equals(new Credential(false,name,s.next()))){
							System.out.println("Log in successful");
							Thread.sleep(100);
							out.println("Student");
							logged = true;
						}
					if(adminInd != -1)
						if(Admins.get(adminInd).equals(new Credential(true,name,s.next()))){
							System.out.println("Log in successful");
							Thread.sleep(100);
							out.println("Admin");
							logged = true;
						}
						else{
							Thread.sleep(100);
							out.println("failed");
							break;
						}
				}
				if(first.equals("file")){
					String fileName = s.next();
					String topic = s.next();
					int filesize = Integer.parseInt(s.next());
					saveFile(indivSocket,filesize,fileName,topic);
				}
				if(first.equals("showLessons")) {
					sendLessons(out, in);
				}
				if(first.equals("selectLesson")) {
					String lessonNumber = s.next();
					selectLesson(out, in, lessonNumber);
				}
				if(first.equals("getFile")){
					String fileName = s.next();
					String topic = s.next();
					deleteFile(fileName,topic);
				}
				if(first.equals("getFileList")){
					sendFileList(out,in);
				}
				if(first.equals("questionList")){
					sendQuestionList(out,in);
					if(first.equals("delete")){
						String fileName = s.next();
						String topic = s.next();
						deleteFile(topic,fileName);
					}
					if(first.equals("AddUser")){
						String name = s.next();
						String pw = s.next();
						String isAdmin = s.next();
						Thread.sleep(100);
						if(!Users.contains(name)){
							Users.add(name);
							FileWriter f;
							if(isAdmin.equals("true")){
								Admins.add(new Credential(true,name,pw));
								f = new FileWriter(new File(dir + "AdminList.txt"),true);
							}
							else{
								Creds.add(new Credential(false,name,pw));
								f = new FileWriter(new File(dir + "UserList.txt"),true);
							}
							f.write(name);
							f.write(" ");
							f.write(pw);
							f.write("\n");
							f.close();
						}
					}
					if (first.equals("Question")){
						String q = s.nextLine();
						FileWriter f = new FileWriter(new File(dir + "Forum.txt"), true);
						f.write("Question: ");
						f.write(q);
						f.write("\n");
						f.close();
					}
					inputLine = in.readLine();
					System.out.println("inputLin" + inputLine);
				}
			} catch (InterruptedException ex){
				System.out.println(ex.getMessage());
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+ port + " or listening for a connection");
				System.out.println("error is " + e.getMessage());
			}
			System.out.println("Terminated connection with user:" + loggedName);
			}
	}

