import java.io.*; import java.math.BigInteger;
import java.awt.image.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.*;
import javax.imageio.ImageIO;

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
	public static void deleteLine(String f, String fileName, String topic) throws FileNotFoundException,IOException{
		String temp = "";
		Scanner s = new Scanner(new File(f));
		while(s.hasNextLine()){
			String line = s.nextLine();
			if(!(line.equals( fileName + " | " + topic))){
				temp += line;
				temp += "\n";
			}
		}
		File ff = new File(f);
		ff.delete();
		File fff = new File(f);
		fff.createNewFile();
		PrintWriter pw = new PrintWriter(fff);
		pw.println(temp);
		pw.close();
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
		directory.delete();
		File fileList = new File(dir+"UserFiles.txt");
		deleteLine(dir+"UserFiles.txt",fileName,topic);
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
	
	private void sendQuestionList(PrintWriter out,BufferedReader in)throws IOException{
		ArrayList<String> questions = new ArrayList<String>();
		File F = new File(dir+"Forum.txt");
		Scanner S = new Scanner(F);
		String toSend = "";
		int count = 1;
		while(S.hasNext()){
			String s = S.next();
			while (!s.equals("Question:") && S.hasNext())
				s = S.next();
			if (!S.hasNext())
				break;
			String question = Integer.toString(count) + " : " + S.nextLine();
			
			if(!questions.contains(question)){
				questions.add(question);
			}
		count ++;
		}	
		if (questions.isEmpty()){
			toSend += "There is no questions yet, please post one";
			toSend += "\n";
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
	
	private void sendAnswerList(PrintWriter out,BufferedReader in, int num)throws IOException{
		ArrayList<String> answers = new ArrayList<String>();
		File F = new File(dir+"Forum.txt");
		Scanner S = new Scanner(F);
		Scanner S2 = new Scanner(F);
		String toSend = "";
		int count = 1;
		int anscount = 1;
			
		while(S.hasNext()){
			
			while (!S.next().equals("Question:")){
				
			}
			if (count == num){
				String question = Integer.toString(count) + " : " + S.nextLine();
			
				if(!answers.contains(question)){
					answers.add(question);
				}
				break;
			}
			count ++;
		}
		while (S2.hasNext()){
			String ans = S2.next();
			while(!ans.equals("Answer"+Integer.toString(num)+":") && S2.hasNext()){			
				ans = S2.next();
			}
			if (!S2.hasNext())
				break;
			answers.add("	" + Integer.toString(anscount) + " : " + S2.nextLine());
			anscount ++;
		}
		
		
		
		for (int i = 0; i < answers.size(); i++) {
			toSend += answers.get(i);
			toSend += "\n";
		}
		if(answers.size() == 1){
			toSend += "		No answers yet";
			toSend += "\n";
		}
			
		System.out.println(toSend);
		out.println(toSend);
		out.println("\n");
		out.println("exit answerList");
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

	public void clearNotification(String userName) throws FileNotFoundException,IOException{
		String temp = "";
		String f = dir + "/Notifications.txt";
		Scanner s = new Scanner(new File(f));
		while(s.hasNextLine()){
			String line = s.nextLine();
			if(!line.substring(0,line.indexOf(" ")).equals(userName)){
				temp += line;
				temp += "\n";
			}
		}
		File ff = new File(f);
		ff.delete();
		File fff = new File(f);
		fff.createNewFile();
		PrintWriter pw = new PrintWriter(fff);
		pw.println(temp);
		pw.close();
	}
	public String getNotification(String userName) throws FileNotFoundException{
		File f = new File(dir+"/Notifications.txt");
		Scanner S = new Scanner(f);
		String toreturn = "";
		while(S.hasNextLine()){
			String lineRead = S.nextLine();
			Scanner lineScanner = new Scanner(lineRead);
			while(lineScanner.hasNext()){
				String name = lineScanner.next();
				if(userName.equals(name)){
					while(lineScanner.hasNext())
						toreturn += lineScanner.next();
					toreturn += "\n";
				}
			}
		}
		if(toreturn.equals(""))
			return "There are no notifications";
		return toreturn;
	}
	public void convertFile(String filename,String topic,String format)throws IOException{
		String inputFileName = dir + "/" + topic + "/" + filename;
		System.out.println(inputFileName);
		String outputFileName = dir + "/" + topic + "/" + filename.substring(0,filename.indexOf('.')) + "." + format;
		System.out.println(outputFileName);
		FileInputStream fis = new FileInputStream(inputFileName);
		FileOutputStream fos = new FileOutputStream(outputFileName);
		BufferedImage inputImage = ImageIO.read(fis);
		ImageIO.write(inputImage,format,fos);
		fis.close();
		fos.close();
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
				System.out.println(first);
				if(!first.equals("login") && !logged){
					System.out.println("broken because " + logged);
					break;
				}
				if(first.equals("login")){
					String name = s.next();
					loggedName = name;
					int userInd = Users.indexOf(name);
					int adminInd = UsersAdmin.indexOf(name);
					if(userInd != -1){
						if(Creds.get(userInd).equals(new Credential(false,name,s.next()))){
							System.out.println("Log in successful");
							Thread.sleep(100);
							out.println("Student " + getNotification(name));
							logged = true;
						}
					}
					else if(adminInd != -1){
						if(Admins.get(adminInd).equals(new Credential(true,name,s.next()))){
							System.out.println("Log in successful");
							Thread.sleep(100);
							out.println("Admin " + getNotification(name));
							logged = true;
						}
					}
					else{
						System.out.println("failed login");
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
				}
				if (first.equals("answerList")){
					int num = s.nextInt();
					sendAnswerList(out, in, num);
				}
				if(first.equals("convertFile")){
					convertFile(s.next(),s.next(),s.next());
				}
				if(first.equals("delete")){
					String fileName = s.next();
					String topic = s.next();
					deleteFile(topic,fileName);
				}
				if(first.equals("clearNotification")){
					clearNotification(s.next());
				}
				if(first.equals("AddUser")){
					String name = s.next();
					String pw = s.next();
					String isAdmin = s.next();
					Thread.sleep(100);
					if(!Users.contains(name)){
						Users.add(name);
						FileWriter f;
						if(isAdmin.equals("true") || isAdmin.equals("yes") || isAdmin.equals("y") || isAdmin.equals("t")){
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
				if (first.equals("Answer")){
					int num = s.nextInt();
					String a = s.nextLine();
					FileWriter fw = new FileWriter(new File(dir + "Forum.txt"), true);
					fw.write("Answer");
					fw.write(Integer.toString(num) + ": ");
					fw.write(a);
					fw.write("\n");
					fw.close();
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

