package cardGame;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {
	
	//Global variables
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;

	//Main method
	public static void main(String[] args) throws UnknownHostException, Exception {
		System.out.println("Connecting...");
		socket = new Socket("localhost", 7777); //Connect to specific server using specified port
		System.out.println("Connection succesful!");
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		Input input = new Input(in);
		Thread thread = new Thread(input);
		thread.start();
		Scanner sc = new Scanner(System.in); //Reads input in console
		System.out.println("Enter your name: ");
		String name = sc.nextLine();
		out.writeUTF(name);
		while(true){
			String sendMessage = sc.nextLine();
			out.writeUTF(sendMessage); //Sends message to server
		}
	}

}

//Separate class file
class Input implements Runnable{

	DataInputStream in;
	
	public Input(DataInputStream in){
		this.in = in;
	}
	
	public void run() {
		while(true){
			String message;
			try {
				message = in.readUTF(); //Receive message from server
				System.out.println(message); //Write message in console
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}