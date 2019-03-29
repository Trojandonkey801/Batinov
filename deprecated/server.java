//package batinov;
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
import java.util.*;
public class server extends Thread{
	private ServerSocket ss;
	private ArrayList<Credential> allCred = new ArrayList<Credential>();
	private ArrayList<String> tokens = new ArrayList<String>();
	public server(int port){
		try{
			ss = new ServerSocket(port);
		}
		catch(Exception e){}
	}
	public void run(){
		while(true){
			try{
				Socket clientSocket = ss.accept();
				System.out.println("accepted");
				printInput(clientSocket);
			}catch (Exception e){}
		}
	}
	private void saveFile(Socket clientSocket) throws IOException{
		DataInputStream DIS = new DataInputStream(clientSocket.getInputStream());
		int filesize = 0;;
		int reamining;
		int read = 0;
		int totalRead = 0;
		int remaining = 0;
		byte[] buffer = new byte[4096];
		while((read = DIS.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			if(totalRead == 1){
			}
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			System.out.println(new String(buffer, "UTF-8"));
		}
		DIS.close();
	}
	private void handleConnection(Socket s) throws IOException
	{
		Scanner scanner = new Scanner(s.getInputStream());
		String text = scanner.next();
		System.out.println("received" + text + "end received");
		PrintWriter pw = new PrintWriter(s.getOutputStream());
		if(handleExist(text)!=-1){
			System.out.println("found");
			saveFile(s);
		}
		pw.flush();
		s.close();
	}
	public static void main(String args[]) throws Exception {
		//This newserver runs on port 21839 by default
		//If args[0] is given it replaces the defailt
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 21839;
		ServerSocket ssock = new ServerSocket(port);
		System.out.println("Listening on " + ssock.getLocalPort());

		while (true) {
			Socket sock = ssock.accept();
			count++;
			System.out.println("Connected");
			Thread t = new Thread(new FileServer(sock));//.start();
			map.put(t,Integer.valueOf(count));
			t.start();
		}
	}
}
