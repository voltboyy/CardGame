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
	Rectangle connectButton = new Rectangle(GWIDTH/2 - 75, GHEIGHT/2 - 50, 150, 50);
	Rectangle messageBox = new Rectangle(GWIDTH/2 - 100, GHEIGHT/2 - 75, 200, 75);
	Rectangle closeBox = new Rectangle(GWIDTH - 25, 0, 20, 20);
	
	//booleans
	boolean debug = false, connect = false, connected, connectionFailed;
	boolean ConnectHover = false;
	boolean left, down, right, up;
	
	//Global variables
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	
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
		
		//Load Images
		//arrowNextIdle = new ImageIcon(getClass().getResource("/ArrowNextIdle.png")).getImage();
		
		//load sounds
		//hoverSound = java.applet.Applet.newAudioClip(getClass().getResource("/hover.wav"));
		
		
		//basic settings
		setPreferredSize(panelSize);
		setBackground(Color.DARK_GRAY);
		setFocusable(true);
		requestFocus(false);
		
		//Key presses and stuf are being tracked here
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_D){
					if(debug)
						debug = false;
					else
						debug = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_C){
					if(!connect){
						connect = true;
						Connect();
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
			
			public void KeyTyped(KeyEvent e){
				
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
				if(right || left || up || down){ // "||" = of
					try{
						out.writeInt(playerid);
						out.writeInt(playerx);
						out.writeInt(playery);
					}catch(Exception e){
						System.out.println("Error sending Coordinates");
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
        g.setFont(new Font("Arial", Font.BOLD, 14));
		g.setColor(Color.WHITE);
        if(!connect && !connectionFailed){
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

