import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.*;

public class EchoServer implements Runnable{
	Socket indivSocket;
	final static String dir = "./ServerFileDir/";
	static int port = 21839;
	static ArrayList<String> Users = new ArrayList<String>();
	static ArrayList<String> UsersAdmin = new ArrayList<String>();
	static ArrayList<Credential> Creds = new ArrayList<Credential>();
	static ArrayList<Credential> Admins = new ArrayList<Credential>();
	static ArrayList<String> Files = new ArrayList<String>();

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

	public EchoServer(Socket indivSocket){
		this.indivSocket = indivSocket;
	}

	private void saveFile(Socket clientSock, int filesize,String fileName) throws IOException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		File F = new File(dir + "received" + fileName);
		FileOutputStream fos = new FileOutputStream(F);
		FileWriter f = new FileWriter(new File(dir+"UserFiles.txt"),true);
		f.write("received"+fileName);
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
		}
		dis.close();
		fos.close();
	}

	public void sendFile(String file) throws IOException {
		PrintWriter p = new PrintWriter(indivSocket.getOutputStream());
		long filesize = (new File(dir + file)).length();
		System.out.println("filesize" + filesize);
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

	public void run(){
		boolean logged = false;
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
				System.out.println("inputLine is "+ inputLine);
				if(!first.equals("login") && !logged){
					break;
				}
				if(first.equals("login")){
					String name = s.next();
					int userInd = Users.indexOf(name);
					System.out.println("user index"+userInd);
					int adminInd = UsersAdmin.indexOf(name);
					System.out.println("admin index"+adminInd);
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
					Thread.sleep(100);
					String fileName = s.next();
					int filesize = Integer.parseInt(s.next());
					saveFile(indivSocket,filesize,fileName);
				}
				if(first.equals("getFile")){
					String fileName = s.next();
					System.out.println("fileName is " + fileName);
					sendFile(fileName);
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
							Creds.add(new Credential(false,name,pw));
							f = new FileWriter(new File(dir + "UserList.txt"),true);
						}
						else{
							Admins.add(new Credential(true,name,pw));
							f = new FileWriter(new File(dir + "AdminList.txt"),true);
						}
						f.write(name);
						f.close();
					}
				}
				inputLine = in.readLine();
			}
		} catch (InterruptedException ex){
			System.out.println(ex.getMessage());
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port "
					+ port + " or listening for a connection");
			System.out.println("error is " + e.getMessage());
		}
	}

}

