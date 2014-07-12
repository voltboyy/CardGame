package cardGame;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main extends Applet implements Runnable, KeyListener{
	
	//Global variables
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	
	//unique id that server sends to client
	int playerid;
	
	//local coordinates
	int playerx;
	int playery;
	
	//server coordinates van andere ids
	int[] x = new int[10];
	int[] y = new int[10];
	
	//booleans
	boolean left, down, right, up;

	//Main method (changed to an initialize as it's an applet temporarily
	public void init() {
		setSize(100, 100); //screensize in px
		addKeyListener(this); //Zorgt ervoor da die naar u toetsaanslagen kan 'luisteren'
		try{
			System.out.println("Connecting...");
			socket = new Socket("localhost", 7777); //Connect to specific server using specified port
			System.out.println("Connection succesful!");
			in = new DataInputStream(socket.getInputStream());
			playerid = in.readInt(); //Receiving id from server
			out = new DataOutputStream(socket.getOutputStream());
			Input input = new Input(in, this);
			Thread thread = new Thread(input); //thread 1, listens to input
			thread.start();
			Thread thread2 = new Thread(this); //thread 2, runs this applet
			thread2.start();
		}catch(Exception e){
			System.out.println("Unable to start client");
		}
	}
	
	//Update van coordinaten van server van andere ids
	public void updateCoordinates(int pid, int x2, int y2){
		this.x[pid] = x2;
		this.y[pid] = y2;
	}
	
	//paints your stuff on screen
	public void paint(Graphics g){
		for(int i = 0; i < 10; i++){
			g.drawOval(x[i], y[i], 5, 5);
		}
	}

	//Makes this program run
	public void run() {
		while(true){
			if(right == true){
				playerx += 10;
			}
			if(left == true){
				playerx -= 10;
			}
			if(up == true){
				playery -= 10;
			}
			if(down == true){
				playery += 10;
			}
			if(right || left || up || down){ // "||" = of
				try{
					out.writeInt(playerid);
					out.writeInt(playerx);
					out.writeInt(playery);
				}catch(Exception e){
					System.out.println("Error sending Coordinates");
				}
			}
			repaint();
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void keyPressed(KeyEvent e) { //What to do if a key is pressed
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			left = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			right = true;	
		}
		if(e.getKeyCode() == KeyEvent.VK_UP){
			up = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			down = true;
		}
	}

	public void keyReleased(KeyEvent e) { //What to do if a key is released
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			left = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			right = false;	
		}
		if(e.getKeyCode() == KeyEvent.VK_UP){
			up = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			down = false;
		}
	}

	public void keyTyped(KeyEvent e) { //Not sure...
		
	}

}

//Separate class file, listens to all information received from server
//and send this to main class file.
class Input implements Runnable{

	DataInputStream in;
	Main client;
	
	public Input(DataInputStream in, Main c){
		this.in = in;
		this.client = c;
	}
	
	public void run() {
		while(true){
			try {
				int playerid = in.readInt();
				int x = in.readInt();
				int y = in.readInt();
				client.updateCoordinates(playerid, x, y);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}