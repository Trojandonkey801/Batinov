//package server;
import java.util.Random;
public class Credential{
	/*
	 * Credential Class that is used serverside to store credentials for users
	 */
	private int token;
	private String userName;
	private boolean Admin;
	private int password;
	private Random rand = new Random();

	/**
	 *Generic constructor
	 *@param isAdmin is user admin
	 *@param userName user Name
	 *@param password the password is stored as a hash
	 */
	public Credential(boolean isAdmin,String userName,String password){
		token = rand.nextInt();
		Admin = isAdmin;
		this.userName = userName;
		this.password = password.hashCode();
	}
	/**
	 *Gets Name
	 */
	public String getName(){
		return userName;
	}
	/**
	 * Gets if Admin
	 */
	public boolean getAdmin(){
		return Admin;
	}

	/**
	 * Compares 2 users, by seeing if the username and hash of passwords match
	 */
	public boolean equals(Credential C){
		return (this.userName.equals(C.getName()) && this.password == C.getPassword());
	}
	/**
	 * Returns hash of password
	 */
	public int getPassword(){
		return password;
	}
	/**
	 * Returns token assigned to user 
	 */
	public int getToken(){
		return token;
	}
}
