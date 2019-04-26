import java.io.IOException;
import java.net.*;
/**
 * Defines STUDENT, which is a subclass of a user.
 *
 */
public class Student extends user{
	public Student(Socket S,String userName)throws IOException{
		super(userName);
		this.socket = S;
	}
}
