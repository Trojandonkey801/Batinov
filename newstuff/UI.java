import java.util.*;
import java.io.*;
public class UI{
	private static user instance;
	private static boolean Admin = false;
	private static Scanner s = new Scanner(System.in);
	public static void main(String[] args)throws IOException,InterruptedException {
		if(loginHandler()){
			String S;
			while(true){
				if(Admin)
					System.out.println("Convert File | Clear Notification | Send File | Receive File | Add User | Get File List | Delete File | Exit");
				else
					System.out.println("Convert File | Clear Notification | Send File | Receive File | Get File List | Exit");
				S = s.nextLine();
				if(S.equals("Exit"))
					break;
				actionHandler(S);
			}
		}
		s.close();
	}
	/**
	 * Action handler that parses the input function, and then calls the appropriate function depending on which function was called
	 */
	public static boolean actionHandler(String S)throws IOException,InterruptedException{
		if(instance.getSocket() == null){
			System.out.println(" in action sockets null");
		}
		if(S.equals("Clear Notification")){
			instance.clearNotif();
			return true;
		}
		else if(S.equals("Convert File")){
			System.out.println("File name");
			String fileName = s.nextLine();
			System.out.println("topic name");
			String topic = s.nextLine();
			System.out.println("format");
			String format = s.nextLine();
			instance.convertFile(fileName,topic,format);
			return true;
		}
		else if(S.equals("Send File")){
			System.out.println("Enter File name");
			String fileName = s.nextLine();
			System.out.println("Entire File topic");
			String fileTopic = s.nextLine();
			instance.sendFile(fileName,fileTopic);
			return true;
		}
		else if(S.equals("Delete File")){
			System.out.println("Enter File name");
			String fileName = s.nextLine();
			System.out.println("Entire File topic");
			String fileTopic = s.nextLine();
			((Administrator)instance).deleteFile(fileName,fileTopic);
			return true;
		}
		else if(S.equals("Add User")){
			System.out.println("Enter User name");
			String userName = s.nextLine();
			System.out.println("Enter User password");
			String userPassword = s.nextLine();
			System.out.println("Is this person an admin");
			String isAdmin = s.nextLine();
			((Administrator)instance).addUser(userName,userPassword,isAdmin);
			return true;
		}
		else if(S.equals("Receive File")){
			System.out.println("Enter File name");
			String fileName = s.nextLine();
			instance.getFile(fileName);
			return true;
		}
		else if(S.equals("Get File List")){
			instance.getFileList();
			return true;
		}
		return false;
	}

	/**
	 * login handler.
	 * When a user successfulyl logs in, the server is queried for the username/password.
	 * Depending on whether it is a student or admin, the instance object is downcasted.
	 * The server object has to be passed along, as downcasting creates a new object.
	 */
	public static boolean loginHandler(){
		System.out.println("attempt login");
		System.out.println("User userName");
		String userName = s.nextLine();
		System.out.println("Password");
		String password = s.nextLine();
		instance = new user(userName);
		instance.initCon(21839,"localhost");
		boolean toreturn = false;
		try{
			String loginsuccess = instance.login(userName,password);
			if(loginsuccess.length() > 0){
				String stuAd = loginsuccess.substring(0,loginsuccess.indexOf(" "));
				String notification = loginsuccess.substring(loginsuccess.indexOf(" "),loginsuccess.length());
				if(stuAd.equals("Student")){
					System.out.println("logged in as student");
					System.out.println("Your current notifications are \n" +  notification);
					instance = new Student(instance.getSocket(),userName);
					toreturn = true;
				}
				else if(stuAd.equals("Admin")){
					Admin = true;
					System.out.println("logged in as Admin");
					System.out.println("Your current notifications are \n" +  notification);
					instance = new Administrator(instance.getSocket(),userName);
					toreturn = true;
				}
			}
			else{
				System.out.println("log in failed. server will now close socket");
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		if(instance.getSocket() == null){
			System.out.println("in login sockets null");
		}
		System.out.println("toreturn bool" + toreturn);
		return toreturn;
	}
}
