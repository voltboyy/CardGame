package cardGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

public class Stats {
	
	static final int GWIDTH = Main.GWIDTH, GHEIGHT = Main.GHEIGHT;
	
	GamePanel gp;
	
	//All stats
	int[] health = new int[2];
	int[] money = new int[3];
	int healthsize;
	int att, def, speed, level, karma, mana, shield, farming;
	
	String rawstats;
	String[] playerstats = new String[50];
	
	//Images
	Image HUD_BARS, COIN_GOLD, COIN_SILVER, COIN_COPPER;
	
	//Colors
	Color Health_1 = new Color(210,170,153);
	Color Health_2 = new Color(208,70,72);
	
	public Stats(){ //Loads on launch
		HUD_BARS = new ImageIcon(getClass().getResource("/HUD_BARS.png")).getImage();
		COIN_GOLD = new ImageIcon(getClass().getResource("/COIN_GOLD.png")).getImage();
		COIN_SILVER = new ImageIcon(getClass().getResource("/COIN_SILVER.png")).getImage();
		COIN_COPPER = new ImageIcon(getClass().getResource("/COIN_COPPER.png")).getImage();
	}
	
	public void SetRawStats(String rs){
		rawstats = rs;
	}
	
	public void ProcessStats(){ //Stats received from server are correctly parsed, etc
		playerstats = rawstats.split("_");
		health[0] = Integer.parseInt(playerstats[6]);
		health[1] = Integer.parseInt(playerstats[7]);
		money[0] = Integer.parseInt(playerstats[1]);
		money[1] = Integer.parseInt(playerstats[2]);
		money[2] = Integer.parseInt(playerstats[3]);
	}
	
	public void Draw(Graphics g){
		g.drawImage(HUD_BARS, 0, 20, gp);
		g.setColor(Health_1);
		g.fillRect(16, 26, healthsize, 4);
		g.setColor(Health_2);
		g.fillRect(16, 30, healthsize, 4);
		g.setColor(Health_2);
		g.fillRect(14, 26, 2, 8);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.setColor(Health_2);
		g.drawString(health[0] + "/" + health[1], 144, 35);
		g.setColor(Color.ORANGE);
		g.drawString(money[0]+"", 30, 96);
		g.drawImage(COIN_GOLD, 5, 82, gp);
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(money[1]+"", 30, 118);
		g.drawImage(COIN_SILVER, 5, 104, gp);
		g.setColor(Color.RED);
		g.drawString(money[2]+"", 30, 140);
		g.drawImage(COIN_COPPER, 5, 126, gp);
	}
	
	public void UpdateStats(){
		//Determines healthbar size
		if(health[1] != 0)
			healthsize = ((health[0]*100)/(health[1]));
	}
	
}
