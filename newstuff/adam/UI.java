import java.util.*;
import java.io.*;
public class UI{
	private static user instance;
	private static boolean Admin = false;
	private static Scanner s = new Scanner(System.in);
	public static void main(String[] args)throws IOException,InterruptedException {
		if(loginHandler()){
			String S;
			do{
				if(Admin)
					System.out.println("Send File | Receive File | Add User | Get File List | Delete File | Access Forum | Lessons");
				else
					System.out.println("Send File | Receive File | Get File List | Access Forum | Lessons");
				S = s.nextLine();
			}while(actionHandler(S));
		}
		s.close();
	}
	public static boolean actionHandler(String S)throws IOException,InterruptedException{
		if(instance.getSocket() == null){
			System.out.println(" in action sockets null");
		}
			if(S.equals("Send File")){
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
			else if(S.equals("Lessons")){
				instance.showLessons();
				System.out.println("Show a lesson using the number keys");
				String lessonNumber = s.nextLine();
				instance.selectLesson(lessonNumber);
				return true;
			}
			else if (S.equals("Access Forum")){
				System.out.println("Enter action : Ask Question | List Questions");
				String action = s.nextLine();
				if (action.equals("Ask Question")){
					System.out.println("Enter question to post");
					String question = s.nextLine();
					instance.addQuestion(question);
					return true;
				}
				else if (action.equals("List Questions")){
					instance.listQuestions();
					return true;
				}
			}
			return false;
		}

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
			if(loginsuccess.equals("Student")){
				System.out.println("logged in as student");
				instance = new Student(instance.getSocket(),userName);
				toreturn = true;
			}
			else if(loginsuccess.equals("Admin")){
				Admin = true;
				System.out.println("logged in as Admin");
				instance = new Administrator(instance.getSocket(),userName);
				toreturn = true;
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
