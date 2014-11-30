package cardGame;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main extends JFrame{
	
	static int //deze twee heb ik gewoon overgenomen van ander programma, kunnen we nog volledig adjusten, don't worry :p
    GWIDTH = 32*33, //adjust second number 1056
	GHEIGHT = 32*24; //adjust second number 768
	
	//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	//double width = screenSize.getWidth();
	//double height = screenSize.getHeight();

	Dimension screenSize = new Dimension(GWIDTH, GHEIGHT);
	
	
	GamePanel gp;
	
	//This indicates that the entire program should start here
	public static void main(String[] args){
		
		new Main();
		
	}

	//Main method with some basic settings, not too hard
	public Main() {
		
		//Sets icon
		BufferedImage image = null;
	    try {
	        image = ImageIO.read(getClass().getClassLoader().getResource("dirt.png"));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    super.setIconImage(image);
	    
		gp = new GamePanel();
		setTitle("Client");
		setSize(screenSize);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		add(gp);
	}

}

//Separate class file, listens to all information received from server
//and send this to main class file.
