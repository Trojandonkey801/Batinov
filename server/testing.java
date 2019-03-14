import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.util.Random;
public class testing{
	public static void main(String[] args)throws UnsupportedEncodingException{
		String s = "Hello World";
		System.out.println(s.getBytes());
		System.out.println(new String(s.getBytes(), "UTF-8"));
		
	}
}
