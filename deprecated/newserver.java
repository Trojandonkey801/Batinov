//package batinov;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.*;

/*
 * @author wmabebe
 * This newserver class echoes a message from a client
 */

public class newserver implements Runnable {

	private ArrayList<Credential> allCred = new ArrayList<Credential>();
	private ArrayList<String> tokens = new ArrayList<String>();
	Random rand = new Random();

	/*
	 * This variable counts the number of clients that join
	 */
	private static int count = 0;
	/*
	 * This socket variable connects to a host and a port
	 */
	private static HashMap<Thread,Integer> map;

	Socket csocket;

	public newserver(Socket csocket) {
		csocket = csocket;
		map = new HashMap<Thread,Integer>();
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
			Thread t = new Thread(new newserver(sock));//.start();
			map.put(t,Integer.valueOf(count));
			t.start();
		}
	}

	private int handleExist(String S){
		for (int i = 0; i < allCred.size(); i++) {
			if(allCred.get(i).getName().equals(S))
				return i;
		}
		return -1;
	}

	private void addUser(String userName,boolean isAdmin,String Password){
		allCred.add(new Credential(isAdmin,userName,Password.hashCode()));
	}

	private void saveFile(Socket clientSock) throws IOException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		FileOutputStream fos = new FileOutputStream("testfile.jpg");
		byte[] buffer = new byte[4096];
		int filesize = 0;;
		int read = 0;
		int totalRead = 0;
		int remaining = 0;
		boolean writeFlag = false;
		dis.read(buffer);
		String token = new String(buffer,"UTF-8");
		System.out.println("this is " +token + "token");
		if(tokens.contains(token)){
			writeFlag = true;
		}
		if(writeFlag){
			dis.read(buffer);
			filesize = (int)Long.parseLong((new String(buffer, "UTF-8")));
			remaining = filesize;
			System.out.println(remaining);
		}
		if(writeFlag && filesize != 0)
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

	public void run() {
		int id = count;
		boolean exit = false;
		try{
			InputStream IS = new InputStream(this.csocket.getInputStream());
			InputStreamReader ISR = new InputStreamReader(this.csocket.getInputStream());
			if(ISR == null){
				System.out.println("THIS SHIT FUCKED YO");
			}
			BufferedReader brBufferedReader = new BufferedReader(ISR);
			PrintWriter pwPrintWriter =new PrintWriter(this.csocket.getOutputStream(),true);
			String messageString;
			while(!exit){
				System.out.println("inside");
				messageString = brBufferedReader.readLine();
				System.out.println("message is " + messageString);
				while(messageString != null){//assign message from client to messageString
					if(messageString.equals("KILL"))
					{
						System.out.println("Killed by client " + id);
						exit = true;//break to close socket if EXIT
						break;
					}
					System.out.println("Client " + map.get(this)/*count*/ + ": " + messageString);//print the message from client
					//System.out.println("SHA-1: "+new BigInteger(1,m.digest()).toString(16));

					pwPrintWriter.println("newserver echoes: '"+ messageString +"'");
					pwPrintWriter.flush();
				}
			}
			System.out.println("Bye bye..");
			this.csocket.close();

		}
		catch(IOException ex){
			System.out.println(ex.getMessage());
		}
	}

}
