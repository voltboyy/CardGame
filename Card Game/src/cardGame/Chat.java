package cardGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Chat implements KeyListener{

	static final int GWIDTH = Main.GWIDTH, GHEIGHT = Main.GHEIGHT;
	
	boolean canChat = false, chatActive, capsOn;
	
	int maxchar = 1000, numchars = 0, currentKeyCode;
	
	Color chatBackground = new Color(73,73,73, 64);
	
	String message = "", rawmessage = "";
	String[] messagechars = new String[maxchar];
	String[] chatHistory = new String[100], chatUsers = new String[100];
	
	public Chat(){
		
	}
	
	public void setCanChat(boolean bool){
		canChat = bool;
	}
	
	public boolean getChatActive(){
		return chatActive;
	}
	
	public void AddMessage(){
		
	}
	
	public void Draw(Graphics g){
		if(chatActive){
			g.setColor(chatBackground);
			g.fillRect(0, GHEIGHT/2, GWIDTH/3, GHEIGHT/2);
		}
	}
	
	protected void Username(KeyEvent e){
		int keyCode = e.getKeyCode();
		int modifiers = e.getModifiersEx();
		if(keyCode >= 48 && keyCode <= 90 && keyCode != 59 && keyCode !=61 && numchars < maxchar){ //All letters
			if(modifiers == 64 | capsOn){ //UpperCase
				message += KeyEvent.getKeyText(keyCode);
				messagechars[numchars] = KeyEvent.getKeyText(keyCode);
				numchars++;
			}else{ //LowerCase
				if(keyCode >= 48 && keyCode <= 57){
					
				}else{
					rawmessage = KeyEvent.getKeyText(keyCode);
					rawmessage = rawmessage.toLowerCase();
					message += rawmessage;
					messagechars[numchars] = rawmessage;
					numchars++;
				}
			}
		}else if(keyCode >= 96 && keyCode <= 105 && numchars < maxchar){ //Numpad
			currentKeyCode = keyCode - 96;
			message += currentKeyCode;
			messagechars[numchars] = ""+currentKeyCode;
			numchars++;
		}else if(keyCode == 8 && numchars > 0){ //Backspace
			numchars--;
			message = "";
			for(int i = 0; i < numchars; i++){
				message += messagechars[i];
			}
		}else if(modifiers == 64 || capsOn){
			if(modifiers == 64 && capsOn){
				
			}else if(keyCode == 513){
				message += "/";
				messagechars[numchars] = "/";
				numchars++;
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		capsOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		if(canChat){
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				chatActive = true;
			}
			if(chatActive){
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					chatActive = false;
				}
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
}
