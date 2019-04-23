import java.io.IOException;
import java.net.*;
public class Student extends user{
	public Student(Socket S,String userName)throws IOException{
		super(userName);
		this.socket = S;
	}
}
