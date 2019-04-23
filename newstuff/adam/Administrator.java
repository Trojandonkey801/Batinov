import java.io.*;
import java.util.*;
import java.net.*;
public class Administrator extends user{
	/**
	 *Constructor for Administorator.
	 *@param Socket S Socket that inherits from superclass user
	 *@param userName userName for Administrator
	 */
	public Administrator(Socket S,String userName){
		super(userName);
		this.socket = S;
	}

	/**
	 * Call to delete file, only for Administrator
	 *@param fileName Name of file to delete
	 *@param topic topic of file to delete
	 */
	public void deleteFile(String fileName,String topic)throws IOException{
		PrintWriter p = new PrintWriter(socket.getOutputStream(),true);
		p.println("delete " + fileName + " " + topic);
	}
	

	/**
	 * Call to add User, only for Administrator
	 *@param User Name of User to add
	 *@param Password Password of user
	 *@param isAdmin Is the user an admin
	 */
	public void addUser(String User, String Password,String isAdmin)throws IOException{
		PrintWriter p = new PrintWriter(socket.getOutputStream());
		p.println("AddUser " + User + " " + Password + " " + isAdmin);
		p.flush();
	}
}
