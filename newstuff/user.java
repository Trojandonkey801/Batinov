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
	PrintWriter p;
	DataOutputStream dos;
	String userName;
	public user(String userName){
		this.userName = userName;
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

	public void sendFile(String file,String topic) throws IOException {
		long filesize = (new File(dir + file)).length();
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		p.println("file " + file + " " + topic + " " +  filesize);
		FileInputStream fis = new FileInputStream(dir + file);
		try{
			Thread.sleep(100);
			byte[] buffer = new byte[4096];
			while (fis.read(buffer) > 0) {
				dos.write(buffer);
				dos.flush();
			}
		}
		catch (InterruptedException ex){
			System.out.println(ex.getMessage());
		}
		dos.flush();
		fis.close();
	}

	public void getFile(String fileName) throws IOException,InterruptedException{
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("getFile " + fileName); 
		p.flush();
		Thread.sleep(100);
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
			fos.flush();
		}
		System.out.println("finished receiving");
	}

	public Socket getSocket(){
		return socket;
	}
	public void clearNotif() throws IOException{
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("clearNotification " + userName);
		p.close();
	}
	public String login(String User, String Password)throws IOException{
		System.out.println("read  " + User + " "+ Password);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("login " + User+ " " +Password);
		String confirm = in.readLine();
		System.out.println(confirm);
		return confirm;
	}

	public void convertFile(String fileName,String topic, String format)throws IOException{
		BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("convertFile " + fileName + " " + topic +  " " + format);
		in.close();
	}

	public void getFileList()throws IOException{
		BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("getFileList ");
		String confirm = "";
		do{
			confirm = in.readLine();
			if(!confirm.equals("exit FileList"))
				System.out.println(confirm);
		}
		while(!confirm.equals("exit FileList"));
	}
	/**
	 * 
	 * @param question
	 * @throws IOException
	 * 
	 * Sends a question to the server 
	 */
	public void addQuestion(String question)throws IOException{
		PrintWriter p = new PrintWriter(socket.getOutputStream());
		p.println("Question " + question);// sends to the server the request to add question to the forum
		p.flush();
	}
	/**
	 * Sends an answer and a question number to the server
	 * @param answer
	 * @param num
	 * @throws IOException
	 */
	public void addAnswer(String answer, int num)throws IOException{
		PrintWriter p = new PrintWriter(socket.getOutputStream());
		p.println("Answer " + Integer.toString(num) + " " + answer); //sends the the answer and the question number to the server
		p.flush();
	}
	
	/**
	 * Sends a request to see the question list
	 * @throws IOException
	 */
	public void listQuestions()throws IOException{
		BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("questionList "); //sends the request to show the question list to the server
		String confirm = "";
			
		while(!confirm.equals("exit questionList")){
			confirm = in.readLine();
			System.out.println(confirm);
		}

		
	}
	/**
	 * Sends a request to see the answer list to a specific question number
	 * @param num
	 * @throws IOException
	 */
	public void listAnswers(int num)throws IOException{
		BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("answerList " + Integer.toString(num)); //sends to the server the request to show the answer list to the question num
		String confirm = "";
		do{
			confirm = in.readLine();
			System.out.println(confirm);
		}
		while(!confirm.equals("exit answerList"));
		
	}

	public void initCon(int port, String host){
		try{
			socket = new Socket(InetAddress.getByName(host),port);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
