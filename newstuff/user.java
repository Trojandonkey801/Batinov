//package batinov;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.*;
import java.io.File;

public class user{
	final String dir = "./ClientFileDir/";
	protected Socket socket;
	String userName;
	public user(int port,String host,String userName){
		this.userName = userName;
		try{
			socket = new Socket(InetAddress.getByName(host),port);
		}catch (Exception e){
		}
	}

	public void close()throws IOException{
		socket.close();
	}

	public String getToken()throws IOException{
		PrintWriter p = new PrintWriter(socket.getOutputStream());
		p.println(userName);
		p.flush();
		Scanner ss = new Scanner(socket.getInputStream());
		String value = ss.next();
		return value;
	}

	public void sendFile(String file) throws IOException {
		PrintWriter p = new PrintWriter(socket.getOutputStream());
		long filesize = (new File(dir + file)).length();
		System.out.println("filesize" + filesize);
		p.println("file " + file + " " +  filesize);
		p.flush();
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
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

	public void getFile(String fileName) throws IOException,InterruptedException{
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("getFile " + fileName); 
		p.flush();
		Thread.sleep(1000);
		BufferedReader in = new BufferedReader(
			new InputStreamReader(socket.getInputStream()));
		Scanner s = new Scanner(in);
		int fileSize = Integer.parseInt(s.next());
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream("received" + fileName);
		byte[] buffer = new byte[4096];
		int read = 0;
		int totalRead = 0;
		int remaining = fileSize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			fos.write(buffer, 0, read);
		}
		System.out.println("finished receiving");
	}

	public String login(String User, String Password)throws IOException{
		BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		PrintWriter p = new PrintWriter(socket.getOutputStream());
		p.println("login " + User+ " " +Password);
		p.flush();
		String confirm = in.readLine();
		return confirm;
	}

	public static void main(String[] args)throws IOException ,InterruptedException{
		user c = new user(21839,"localhost","jack");
		System.out.println(c.login("aaaa", "aaaa"));
		c.sendFile("cat.jpg");
	}
}
