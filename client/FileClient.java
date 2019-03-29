package client;

import java.nio.ByteBuffer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;

public class FileClient {
	
	private Socket s;
	int port;
	String host;
	
	public FileClient(String host, int port) {
		try {
			this.port = port;
			this.host = host;
			s = new Socket(host, port);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void sendFile(String file) throws IOException {
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		File f = new File(file);
		System.out.println(f.length());
		dos.write(getToken().getBytes());
		dos.write(Long.toString(f.length()).getBytes());
		while (fis.read(buffer) > 0) {
			dos.write(buffer);

		}
		
		fis.close();
		dos.close();	
	}
	
	private void saveFile(Socket clientSock,int token) throws IOException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		FileOutputStream fos = new FileOutputStream("testfile.jpg");
		byte[] buffer = new byte[4096];
		
		int filesize = 992514; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		fos.write(buffer,0,token);
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}
		
		fos.close();
		dis.close();
	}

	private String getToken()throws IOException{
		PrintWriter p = new PrintWriter(s.getOutputStream());
		p.println("batinov");
		p.flush();
		Scanner sss = new Scanner(s.getInputStream());
		String value = sss.next();
		System.out.println(value);
		sss.close();
		return value;
	}

	private void sendMessage(String token)throws IOException,InterruptedException{
		PrintWriter p = new PrintWriter(s.getOutputStream());
		p.println("batinov");
		p.flush();
		Scanner sss = new Scanner(s.getInputStream());
		String value = sss.next();
		if(value.equals("found")){
			System.out.println(value);
			Thread.sleep(5000);
			sendFile("testfile.jpg");
			s.close();
		}
		sss.close();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		FileClient fc = new FileClient("localhost", 1988);
		String token = fc.getToken();
		fc.sendMessage(token);
	}

}
