import java.io.*;
public class Administrator extends user{
	public Administrator(int port, String host,String userName){
		super(port,host,userName);
	}

	public void addUser(String User, String Password,String isAdmin)throws IOException{
		PrintWriter p = new PrintWriter(socket.getOutputStream());
		p.println("AddUser " + User + " " + Password + " " + isAdmin);
		p.flush();
	}
}
