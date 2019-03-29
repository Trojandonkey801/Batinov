//package server;
import java.util.Random;
public class Credential{
	private int token;
	private String userName;
	private boolean Admin;
	private int password;
	private Random rand = new Random();

	public Credential(boolean isAdmin,String userName,String password){
		token = rand.nextInt();
		Admin = isAdmin;
		this.userName = userName;
		this.password = password.hashCode();
	}
	public String getName(){
		return userName;
	}
	public boolean getAdmin(){
		return Admin;
	}

	public boolean equals(Credential C){
		return (this.userName.equals(C.getName()) && this.password == C.getPassword());
	}
	public int getPassword(){
		return password;
	}
	public int getToken(){
		return token;
	}
}
