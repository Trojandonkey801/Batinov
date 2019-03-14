package server;
import java.util.Random;
public class Credential{
	private int token;
	private String userName;
	private boolean Admin;
	private Random rand = new Random();

	public Credential(boolean isAdmin,String userName){
		token = rand.nextInt();
		Admin = isAdmin;
		this.userName = userName;
	}
	public String getName(){
		return userName;
	}
	public boolean getAdmin(){
		return Admin;
	}
	public int getToken(){
		return token;
	}
}
