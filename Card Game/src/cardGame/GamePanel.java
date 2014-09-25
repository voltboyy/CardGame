package cardGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
	
	//This is needed for ipAdress configuration
	private InetAddress ipAdress;
	
	//This is needed for screen repainting, also known as double buffering
	private Image dbImage;
	private Graphics dbg;
	
	//getting proces speed from pc
	static long getTickCount = java.lang.System.currentTimeMillis();
	
	static final int GWIDTH = Main.GWIDTH, GHEIGHT = Main.GHEIGHT;
	static final Dimension panelSize = new Dimension(GWIDTH, GHEIGHT);
	
	//All variables that makes the game run good and smoothly, just ignore
	private Thread game;
	public Thread threadinput;
	private volatile boolean running = false;
	private long period = (long) (1*1000000); //ms -> nanoseconds
	private static final int DELAYS_BEFORE_YIELD = 10;
	
	//Rectangles (x,y,width,height)
	Rectangle connectButton = new Rectangle(GWIDTH - 200, GHEIGHT - 100, 150, 50);
	Rectangle messageBox = new Rectangle(GWIDTH/2 - 100, GHEIGHT/2 - 75, 200, 75);
	Rectangle closeBox = new Rectangle(GWIDTH - 25, 0, 20, 20);
	Rectangle loginBox = new Rectangle(GWIDTH/2 - 150, GHEIGHT/2 - 200, 300, 400);
	Rectangle usernameInput = new Rectangle(loginBox.x+15, loginBox.y+80, loginBox.width-30, 30);
	Rectangle passwordInput = new Rectangle(loginBox.x+15, loginBox.y+230, loginBox.width-30, 30);
	
	//booleans
	boolean debug = false, connect = false, connected, connectionFailed, local = false, usernameActive, passwordActive;
	boolean pendingData;
	boolean ConnectHover = false;
	boolean left, down, right, up;
	
	//Strings
	String username = "", password = "", passwordshown = "", rawusername = "", rawpassword = "";
	String usernamechars[], passwordchars[];
	
	//Global variables
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	
	//Integers
	int level = 1, count = 0, unumchars = 0, pnumchars = 0, maxchar = 25, currentKeyCode;
	int dataTick = 10;
	
	//Number of clicks
	int clicks = 0;
	
	//unique id that server sends to client
	int playerid;
	
	//local coordinates
	int playerx;
	int playery;
	
	//server coordinates van andere ids
	int[] x = new int[10];
	int[] y = new int[10];
	
	//Main method of this class file
	public GamePanel(){
		
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());
		
		usernamechars = new String[maxchar];
		passwordchars = new String[maxchar];
		
		//Load Images
		//arrowNextIdle = new ImageIcon(getClass().getResource("/ArrowNextIdle.png")).getImage();
		
		//load sounds
		//hoverSound = java.applet.Applet.newAudioClip(getClass().getResource("/hover.wav"));
		
		
		//basic settings
		setPreferredSize(panelSize);
		setBackground(Color.DARK_GRAY);
		setFocusable(true);
		requestFocus(false);
		setFocusTraversalKeysEnabled(false); //This makes sure that the tab key can be detected
		
		//Key presses and stuf are being tracked here
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				displayInfo(e, "KEY PRESSED: ");
				
				if(usernameActive){
					Username(e);
				}
				if(passwordActive){
					Password(e);
				}
				
				if(e.getKeyCode() == KeyEvent.VK_TAB){
					if(usernameActive){
						usernameActive = false;
						passwordActive = true;
					}
				}
				
				if(e.getKeyCode() == KeyEvent.VK_D && !usernameActive && !passwordActive){
					if(debug)
						debug = false;
					else
						debug = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_C && !usernameActive && !passwordActive){
					if(!connect){
						connect = true;
						Connect();
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_L && !usernameActive && !passwordActive){
					if(local){
						local = false;
						try {
							ipAdress = InetAddress.getByName("94.226.250.203");
						} catch (UnknownHostException e1) {
							System.out.println("Local key error:" + e1);
						}
					}
					else{
						local = true;
						try {
							ipAdress = InetAddress.getByName("localhost");
						} catch (UnknownHostException e2) {
							System.out.println("Local key error:" + e2);
						}
					}
						
				}
				
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
			
			public void keyReleased(KeyEvent e){
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
			
			public void KeyTyped(KeyEvent e){ //Doesn't work for some unknown reason, some focus shit problems
				displayInfo(e, "KEY TYPED: ");
			}
			
			protected void Username(KeyEvent e){
				int keyCode = e.getKeyCode();
				int modifiers = e.getModifiersEx();
				if(keyCode >= 48 && keyCode <= 90 && unumchars < maxchar){ //All letters
					if(modifiers == 64){ //UpperCase
						username += KeyEvent.getKeyText(keyCode);
						usernamechars[unumchars] = KeyEvent.getKeyText(keyCode);
						unumchars++;
					}else{ //LowerCase
						rawusername = KeyEvent.getKeyText(keyCode);
						rawusername = rawusername.toLowerCase();
						username += rawusername;
						usernamechars[unumchars] = rawusername;
						unumchars++;
					}
				}else if(keyCode >= 96 && keyCode <= 105 && unumchars < maxchar){ //Numpad
					currentKeyCode = keyCode - 96;
					username += currentKeyCode;
					usernamechars[unumchars] = ""+currentKeyCode;
					unumchars++;
				}else if(keyCode == 8 && unumchars > 0){ //Backspace
					unumchars--;
					username = "";
					for(int i = 0; i < unumchars; i++){
						username += usernamechars[i];
					}
				}
			}
			protected void Password(KeyEvent e){
				int keyCode = e.getKeyCode();
				int modifiers = e.getModifiersEx();
				if(keyCode >= 48 && keyCode <= 90 && pnumchars < maxchar){ //All letters
					if(modifiers == 64){ //UpperCase
						password += KeyEvent.getKeyText(keyCode);
						passwordchars[pnumchars] = KeyEvent.getKeyText(keyCode);
						pnumchars++;
						PasswordAsterisk();
					}else{ //LowerCase
						rawpassword = KeyEvent.getKeyText(keyCode);
						rawpassword = rawpassword.toLowerCase();
						password += rawpassword;
						passwordchars[pnumchars] = rawpassword;
						pnumchars++;
						PasswordAsterisk();
					}
				}else if(keyCode >= 96 && keyCode <= 105 && pnumchars < maxchar){ //Numpad
					currentKeyCode = keyCode - 96;
					password += currentKeyCode;
					passwordchars[pnumchars] = ""+currentKeyCode;
					pnumchars++;
					PasswordAsterisk();
				}else if(keyCode == 8 && pnumchars > 0){ //Backspace
					pnumchars--;
					password = "";
					for(int i = 0; i < pnumchars; i++){
						password += passwordchars[i];
					}
					PasswordRemoveAsterisk();
				}
			}
			protected void PasswordAsterisk(){
				passwordshown += "* ";
			}
			protected void PasswordRemoveAsterisk(){
				passwordshown = "";
				for(int i = 0; i < pnumchars; i++){
					passwordshown += "* ";
				}
			}
			
			protected void displayInfo(KeyEvent e, String s) {
		        String keyString, modString, tmpString, actionString, locationString;
		        int id = e.getID();
		        if (id == KeyEvent.KEY_TYPED) {
		        	char c = e.getKeyChar();
		        	keyString = "key character = '" + c + "'";
		        } else {
		        	int keyCode = e.getKeyCode();
		        	keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";
		        }

		        int modifiers = e.getModifiersEx();
		        modString = "modifiers = " + modifiers;
		        tmpString = KeyEvent.getModifiersExText(modifiers);
		        if (tmpString.length() > 0) {
		        	modString += " (" + tmpString + ")";
		        } else {
		        	modString += " (no modifiers)";
		        }

		        actionString = "action key? ";
		        if (e.isActionKey()) {
		        	actionString += "YES";
		        } else {
		        	actionString += "NO";
		        }

		        locationString = "key location: ";
		        int location = e.getKeyLocation();
		        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
		        	locationString += "standard";
		        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
		        	locationString += "left";
		        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
		        	locationString += "right";
		        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
		        	locationString += "numpad";
		        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
		        	locationString += "unknown";
		        }

		        System.out.println(keyString);
		        System.out.println(modString);
		        System.out.println(actionString);
		        System.out.println(locationString);
			}
			
		});		
	}
	
	//This is the main method that controls how the game runs.
	public void run(){
		long beforeTime, afterTime, diff, sleepTime, overSleepTime = 0;
		int delays = 30;
		
		/*final int FRAMES_PER_SECOND = 25;
	    final int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;
	    
	    int next_game_tick = (int) getTickCount;*/
		
		while(true){ //"true" was "running", it works now but something might be broken now
			
			if(connected){
				if(right == true){
					playerx += 1;
				}
				if(left == true){
					playerx -= 1;
				}
				if(up == true){
					playery -= 1;
				}
				if(down == true){
					playery += 1;
				}
				if(right || left || up || down || pendingData){ // "||" = of
					pendingData = true;
					if(count>dataTick){
						count = 0;
						pendingData = false;
						try{
							out.writeInt(playerid);
							out.writeInt(playerx);
							out.writeInt(playery);
						}catch(Exception e){
							System.out.println("Error sending Coordinates");
						}
					}
					
				}
			}
			
			
			
			//A lot of bullshit that keeps game running on same speed but possibly with slower frame rate if pc is lagging
			beforeTime = System.nanoTime();
			gameUpdate();
			gameRender();
			paintScreen();
			
			afterTime = System.nanoTime();
			diff = afterTime - beforeTime;
			sleepTime = period - diff - overSleepTime;
			//If the sleep time is between 0 and period, we can happily sleep ^^
			if(sleepTime < period && sleepTime > 0){
				try{	
					game.sleep(sleepTime / 100000L);//default: 100000
					threadinput.sleep(sleepTime / 100000L);
					overSleepTime = 0;
				}catch (InterruptedException ex) {
					Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			//The diff was greater than the period
			else if(diff > period){
				overSleepTime = diff - period;
			}
			//Accumulate the amount of delays and eventually yield
			else if(++delays >= DELAYS_BEFORE_YIELD){
				game.yield();
				threadinput.yield();
				delays = 0;
				overSleepTime = 0;
			}
			//the loop took less time than expected, but we need to make up
			//for the oversleeptime!
			else{
				overSleepTime = 0;
			}
			//Print out game stats
			/*log(
				"beforeTime:	" + beforeTime +"\n" +
				"afterTime:	" + afterTime + "\n" +
				"diff:		" + diff + "\n" +
				"sleepTime:	" + sleepTime / 1000000L + "\n" +
				"overSleepTime:	" + overSleepTime / 1000000L + "\n" +
				"delays:		" + delays + "\n"
			);*/
		}
	}
	
	//This method keeps the game running.
	private void gameUpdate(){
		if(game != null){ //! removed a part here, always check here for a possible fix!
			Counter();
			//update game state
			//if(level == 30){
			//world1.moveMap();
			//p1.update();
			//}
			//AchievementListener();
			//if (world1.getSavedX() > 0){
				//for (long stop=System.nanoTime()+TimeUnit.SECONDS.toNanos(2);stop>System.nanoTime();) {
					//log(world1.getSavedX() + " " + world1.getSavedY());
					//System.out.print(count + " ");
				//}
			//}
			
		}
	}
	
	//This method makes sure that all textures are properly visualized
	private void gameRender(){
		if(dbImage == null){
			dbImage = createImage(GWIDTH, GHEIGHT);
			if(dbImage == null){
				System.err.println("Why the fuck is that so called dbImage still null??");
				return;
			}else{
				dbg = dbImage.getGraphics();
			}
		}
		//Clear the screen
		dbg.setColor(Color.DARK_GRAY);
		dbg.fillRect(0, 0, GWIDTH, GHEIGHT);
		//Draw Game elements
		draw(dbg);
		if(debug)
			drawDebug(dbg);
	}
	
	//This makes sure that the screen repaints itself with every tick
	private void paintScreen(){
		Graphics g;
		try{
			g = this.getGraphics();
			if(dbImage != null && g != null){
				g.drawImage(dbImage, 0, 0, null);
			}
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}catch(Exception e){
			System.err.println(e);
		}
	}
	
	//I have no clue, is necessary though
	public void addNotify(){
		super.addNotify();
		startGame();
	}
	
	private void startGame(){
        if(game == null){ //removed something! Might cause bugs later! Remember!
            game = new Thread(this);
            game.start();
        	//probably the most essential part of this entire class file
            
            running = true;
        }
    }
	
	//Probably won't work as I deleted this "running" boolean on multiple places XD
	public void stopGame(){
		if(running){
			running = false;
		}
	}
	
	private void Counter(){
		if(count >200)
			count = 100;
		else
			count++;
	}
	
	private void Connect(){
		if(connect){
        	try{
            	//InetAddress.getLocalHost().getHostAddress()
            	this.ipAdress = InetAddress.getByName("94.226.250.203"); //You can remove this entirely and just type the string as far as I know, just tried this for debugging
    			System.out.println("Connecting to " + ipAdress + " ...");
    			socket = new Socket(ipAdress,49500); //Connect to specific server using specified port
    			System.out.println("Connection succesful!");
    			in = new DataInputStream(socket.getInputStream());
    			playerid = in.readInt(); //Receiving id from server
    			out = new DataOutputStream(socket.getOutputStream());
    			Input input = new Input(in, this);
    			threadinput = new Thread(input);
    			threadinput.start();
    			connected = true;
    		}catch(Exception e){
    			System.out.println("Unable to start client");
    			System.out.println(e);
    			connect = false;
    			connectionFailed = true;
    		}
        }
	}
	
	//Easier sending stuff to console
	private void log(String s){
		System.out.println(s);
	}
	
	//Easier filling of rectangles
	public void Fill(Rectangle box, Graphics g){//This fills the rectangle with your desired color
		g.fillRect(box.x, box.y, box.width, box.height);
	}
	
	//Draws content on screen!
	public void draw(Graphics g){
		switch(level){
		case 1:
			g.setFont(new Font("Arial", Font.BOLD, 14));
			g.setColor(Color.WHITE);
	        if(!connect && !connectionFailed){
	        	g.setColor(Color.GRAY);
	        	Fill(loginBox, g);
	        	g.setColor(Color.LIGHT_GRAY);
	        	Fill(usernameInput, g);
	        	g.setColor(Color.LIGHT_GRAY);
	        	Fill(passwordInput, g);
	        	g.setColor(Color.WHITE);
	        	g.drawString(username, usernameInput.x + 5, usernameInput.y + 19);
	        	g.drawString(passwordshown, passwordInput.x + 5, passwordInput.y + 22);
	        	g.setFont(new Font("Arial", Font.BOLD, 14));
				g.setColor(Color.BLACK);
				g.drawString("Username:", usernameInput.x, usernameInput.y - 5);
				g.drawString("Password:", passwordInput.x, passwordInput.y - 5);
	    		if(ConnectHover)
	    			g.setColor(Color.LIGHT_GRAY);
	    		else{
	    			g.setColor(Color.GRAY);
	    		}
	    		Fill(connectButton, g);
	    		g.setFont(new Font("Arial", Font.BOLD, 14));
	    		g.setColor(Color.WHITE);
	    		g.drawString("Connect", connectButton.x + 45, connectButton.y + 30);
	        }
	        if(connect && !connected){
	        	g.setColor(Color.GRAY);
	        	Fill(messageBox, g);
	    		g.setFont(new Font("Arial", Font.BOLD, 14));
	    		g.setColor(Color.WHITE);
	    		g.drawString("Attempting to connect...", messageBox.x + 15, messageBox.y + 42);
	        }
	        if(connectionFailed){
	        	g.setColor(Color.GRAY);
	        	Fill(messageBox, g);
	    		g.setFont(new Font("Arial", Font.BOLD, 14));
	    		g.setColor(Color.RED);
	    		g.drawString("Connection Failed", messageBox.x + 28, messageBox.y + 42);
	    		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 32));
	    		g.drawString("X", closeBox.x, closeBox.y + 20);
	        }
	        if(connected){
	        	g.setColor(Color.RED);
				for(int i = 0; i < 10; i++){
					g.drawOval(x[i], y[i], 9, 9);
				}
				g.setColor(Color.GREEN);
				g.drawOval(playerx + 2, playery + 2, 5, 5);
	        }
			break;
		case 2:
			break;
		}
        
        
	}
	
	//Toggled by pressing the "D" key, shows some usefull values to help debugging, add as much as you want
	public void drawDebug(Graphics g){
		g.setFont(new Font("Arial", Font.PLAIN, 12));
		g.setColor(Color.MAGENTA);
		g.drawString("y: " + playery, 0, GHEIGHT - 30);
		g.drawString("x: " + playerx, 0, GHEIGHT - 42);
		g.drawString("Clicks: " + clicks, 0, GHEIGHT - 54);
		g.drawString("Connect: " + connect, 0, GHEIGHT - 66);
		g.drawString("CHover: " + ConnectHover, 100, GHEIGHT - 30);
		g.drawString("Connected: " + connected, 100, GHEIGHT - 42);
		g.drawString("Local: " + local, 100, GHEIGHT - 54);
		g.drawString("#username: " + unumchars, 100, GHEIGHT - 66);
		g.drawString("usernA: " + usernameActive, 200, GHEIGHT - 30);
		g.drawString("passwA: " + passwordActive, 200, GHEIGHT - 42);
		g.drawString("Count: " + count, 200, GHEIGHT - 54);
		g.drawString("Username: " + username, 300, GHEIGHT - 30);
		g.drawString("Password: " + password, 300, GHEIGHT - 42);
		g.drawString("PassShown: " + passwordshown, 300, GHEIGHT - 54);
		//g.drawString("Saved Coord: "+world1.getSavedX()+" "+world1.getSavedY(), 0, GHEIGHT - 30);
		//g.drawString("Count: "+count, 0, GHEIGHT - 42);
	}
	
	//Updates the coordinates of other clients
	public void updateCoordinates(int pid, int x2, int y2){
		this.x[pid] = x2;
		this.y[pid] = y2;
	}
	
	//Easier declaring hitboxes, really useful for creating buttons
	public boolean hitBox(Rectangle box, int x, int y){//This handles the detection of your cursor in rectangles, mostly buttons
		if (x > box.x && x < box.x+box.width &&
				y > box.y && y < box.y+box.height){
			return true;
		}
		else
			return false;
	}
	
	//Ever wanted to do stuff with your mouse? Well you came to the rigth place! :p
	public class MouseHandler extends MouseAdapter{
		public void mouseMoved(MouseEvent e){
			int mx = e.getX();
			int my = e.getY();
			if(!connect){
				if(hitBox(connectButton, mx, my))
					ConnectHover = true;
				else
					ConnectHover = false;
			}
			/*if(hitBox(achievementGet, mx, my))
				AchievementGetHover = true;
			else
				AchievementGetHover = false;*/
		}
		public void mouseDragged(MouseEvent e){
			
		}
		public void mouseEntered(MouseEvent e){
			
		}
		public void mouseExited(MouseEvent e){
			
		}
		public void mousePressed(MouseEvent e){
			int mx = e.getX();
			int my = e.getY();
			if(mx > 0 && mx < GWIDTH &&
					my > 0 && my < GWIDTH){
					clicks++;
					requestFocusInWindow();
			}
			if(!connect && !connectionFailed){
				if(hitBox(connectButton, mx, my)){
					connect = true;
					ConnectHover = false;
					Connect();
				}
			}
			if(connectionFailed){
				if(hitBox(closeBox, mx, my)){
					connectionFailed = false;
				}
			}
			if(hitBox(usernameInput, mx, my)){
				usernameActive = true; passwordActive = false;
			}
			else if(hitBox(passwordInput, mx, my)){
				passwordActive = true; usernameActive= false;
			}
			else{
				usernameActive= false; passwordActive = false;
			}
		}
		public void mouseReleased(MouseEvent e){
			
		}
		public void mouseClicked(MouseEvent e){
			
		}
	}

}

//Seperate class file, could split it but... meh
class Input implements Runnable{

	DataInputStream in;
	GamePanel client;
	
	public Input(DataInputStream in, GamePanel c){
		this.in = in;
		this.client = c;
	}
	
	public void run() {
		while(true){
			try {
				int playerid = in.readInt();
				int x = in.readInt();
				int y = in.readInt();
				client.updateCoordinates(playerid, x, y); //This updates coordinates from other clients in gamepanel class file
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

