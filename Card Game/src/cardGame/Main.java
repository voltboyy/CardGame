package cardGame;

import java.io.*;
import java.net.*;

public class Main {
	
	static Socket socket;
	static DataInputStream in;

	public static void main(String[] args) throws UnknownHostException, Exception {
		System.out.println("Connecting...");
		socket = new Socket("localhost", 7777);
		System.out.println("Connection succesful!");
		in = new DataInputStream(socket.getInputStream());
		System.out.println("Receiving information...");
		String test = in.readUTF();
		System.out.println("Message from server: " + test);
	}

}
