package server; 
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.util.Random;

public class FileServer extends Thread {

	private ServerSocket ss;
	private ArrayList<Credential> allCred = new ArrayList<Credential>();
	private ArrayList<String> tokens = new ArrayList<String>();
	Random rand = new Random();

	public FileServer(int port) {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				Socket clientSock = ss.accept();
				System.out.println("accpted");
				saveFile(clientSock);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveFile(Socket clientSock) throws IOException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		FileOutputStream fos = new FileOutputStream("testfile.jpg");
		byte[] buffer = new byte[4096];
		
		int filesize;
		int reamining;
		int read = 0;
		int totalRead = 0;
		int remaining;
		boolean writeFlag = false;
		dis.read(buffer);
		String token = new String(buffer,"UTF-8");
		System.out.println("this is " +token + "token");
		if(tokens.contains(token)){
		writeFlag = true;
		}
		dis.read(buffer);
		filesize = (int)Long.parseLong((new String(buffer, "UTF-8")));
		remaining = filesize;
		System.out.println(remaining);
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			if(totalRead == 1){
			}
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}
		
		fos.close();
		dis.close();
	}
public void sendFile(String file,Socket s) throws IOException {
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		File f = new File(file);
		System.out.println(f.length());
		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}

		fis.close();
		dos.close();	
	}

	private void handleConnection(Socket s) throws IOException
	{
			Scanner scanner = new Scanner(s.getInputStream());
			String text = scanner.next();
			System.out.println(text);
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			if(handleExist(text)!=-1){
				System.out.println("found");
				String token = Integer.toString(rand.nextInt());
				pw.println(token);
				tokens.add(token);
				saveFile(s);
			}
			pw.flush();
			s.close();
	}

	private int handleExist(String S){
		for (int i = 0; i < allCred.size(); i++) {
			if(allCred.get(i).getName().equals(S))
				return i;
		}
		return -1;
	}
	private void addUser(String userName,boolean isAdmin){
		allCred.add(new Credential(isAdmin,userName));
	}
	public static void main(String[] args) {
		FileServer fs = new FileServer(1988);
		fs.addUser("batinov",true);
		fs.start();
	}

}
