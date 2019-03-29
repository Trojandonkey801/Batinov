//package batinov;
import java.io.IOException;
public abstract class user extends client{
	private String token;

	public user(int port, String host, String userName)throws IOException{
		super(port,host,userName);
		token = getToken();
	}
}
