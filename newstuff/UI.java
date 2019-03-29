import java.util.*;
import java.io.*;
public class UI{
	private static user instance;
	private static boolean Admin = false;;
	private static Scanner s = new Scanner(System.in);
	public static void main(String[] args)throws IOException,InterruptedException {
		if(loginHandler()){
			String S;
			do{
				if(Admin)
					System.out.println("Send File | Receive File | Add User");
				else
					System.out.println("Send File | Receive File");
				S = s.nextLine();
			}while(actionHandler(S));
		}
		s.close();
	}
	public static boolean actionHandler(String S)throws IOException,InterruptedException{
			if(S.equals("Send File")){
				System.out.println("Enter File name");
				String fileName = s.nextLine();
				instance.sendFile(fileName);
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
			return false;
		}
	public static boolean loginHandler(){
		System.out.println("attempt login");
		System.out.println("User userName");
		String userName = s.nextLine();
		System.out.println("Password");
		String password = s.nextLine();
		instance = new user(21839,"localhost",userName);
		try{
			String loginsuccess = instance.login(userName,password);
			if(loginsuccess.equals("Student")){
				System.out.println("logged in as student");
				instance = new Student(21839,"localhost",userName);
				return true;
			}
			if(loginsuccess.equals("Admin")){
				Admin = true;
				System.out.println("logged in as Admin");
				instance = new Administrator(21839,"localhost",userName);
				return true;
			}
			else{
				System.out.println("log in failed. server will now close socket");
				return false;
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		return false;
	}
}
