package cardGame;

import java.awt.*;
import java.util.Random;

import javax.swing.ImageIcon;

public class World {

	public Rectangle[][] tiles;
	private Image[][] tileImg;
	public String[][] tileName;
	public boolean[][] isSolid;
	public boolean unsynced;
	//public boolean[] isHoverable;
	public static final int arrayNum = 32; //amount of tiles on the map
	
	private Camera p1;
	
	public boolean loaded, mousePressed;
	
	//Tile images
	public Image TILE_BLACK, TILE_GRASS, TILE_EMPTY, ISOTILE_DIRT, ISOTILE_GRASS;
	
	Rectangle RandomRectangle = new Rectangle(0, 0, 32, 32); //This is used to track the position of the map
	
	private int Imagex, Imagey, xDirection, yDirection, TreeChance, BlackChance, GrassChance, OreChance, WildLifeChance, 
	movedX = 0, movedY = 0;
	
	public static int 
	xOffset = 400,//33*-32, //-192
	yOffset = 100,//38*-32,
	imageOffset = 26,
	widthAmount = 33,
	widthMap = widthAmount*32; //width of tiled map (#/32)
	
	//Map navigation
	static final int PAN_UP = 0, PAN_DOWN = 1, PAN_LEFT = 2, PAN_RIGHT = 3;
	
	public World(){
		TILE_BLACK = new ImageIcon(getClass().getResource("/TILE_BLACK.png")).getImage();
		TILE_GRASS = new ImageIcon(getClass().getResource("/TILE_GRASS.png")).getImage();
		TILE_EMPTY = new ImageIcon(getClass().getResource("/TILE_EMPTY.png")).getImage();
		ISOTILE_DIRT = new ImageIcon(getClass().getResource("/ISOTILE_DIRT.png")).getImage();
		ISOTILE_GRASS = new ImageIcon(getClass().getResource("/ISOTILE_GRASS.png")).getImage();
		tiles = new Rectangle[arrayNum][arrayNum];
		tileImg = new Image[arrayNum][arrayNum];
		tileName = new String[arrayNum][arrayNum];
		isSolid = new boolean[arrayNum][arrayNum];
		
		p1 = new Camera(this);
		
		loadArrays();
		//loadArraysAgain();
		//decideMapMovable();
	}
	
	//First load of the map
	public void loadArrays(){
		for(int i = 0; i < arrayNum; i++){
			for(int j = 0; j < arrayNum; j++){
				tileImg[i][j] = TILE_EMPTY;
				tiles[i][j] = new Rectangle((i*16)-(j*16), (i*8)-(j*8), 32, 16);
				isSolid[i][j] = true;
				tileName[i][j] = "Empty";
			}
		}
	}
	
	public void drawWorld(Graphics g){
		for(int i = 0; i < arrayNum; i++){
			for(int j = 0; j < arrayNum; j++){
				tileImg[i][j] = ISOTILE_GRASS;
				tiles[i][j] = new Rectangle(j*30-i*30 + movedX, i*15+j*15 + movedY, 64, 32);
				isSolid[i][j] = true;
				tileName[i][j] = "Grass";
				g.drawImage(tileImg[i][j], tiles[i][j].x + xOffset, tiles[i][j].y + yOffset, null);
			}
		}
	}
	
	public void moveMap(){
		/*for(Rectangle r : tiles){
			/*r.x += 32*xDirection;
			r.y += 32*yDirection;
			//collision();
			r.x += xDirection;
			r.y += yDirection;
		}*/
		RandomRectangle.x += xDirection;
		RandomRectangle.y += yDirection;
		//System.out.println(RandomRectangle.x + " " + RandomRectangle.y);
	}
	
	public int getSavedX(){
		return RandomRectangle.x;
	}
	public int getSavedY(){
		return RandomRectangle.y;
	}
	public void setMovedX(int d){
		movedX += d;
	}
	public void setMovedY(int d){
		movedY += d;
	}
	public int getMovedX(){
		return movedX;
	}
	public int getMovedY(){
		return movedY;
	}
	public void setMousePressed(boolean d){
		mousePressed = d;
	}
	/*public void setTiledX(int d){
		for(Rectangle r : tiles){
			r.x += d;
		}
	}
	public void setTiledY(int d){
		for(Rectangle r : tiles){
			r.y += d;
		}
	}*/
	public void setSavedX(int d){
		RandomRectangle.x = d;
	}
	public void setSavedY(int d){
		RandomRectangle.y = d;
	}
	
	public void stopMoveMapHorizontal(){
		setXDirection(0);
	}
	
	public void stopMoveMapVertical(){
		setYDirection(0);
	}
	
	public void stopMoveMap(){
		setXDirection(0);
		setYDirection(0);
	}
	
	public void setXDirection(int dir){
		xDirection = dir;
	}
	
	public void setYDirection(int dir){
		yDirection = dir;
	}
	
	public void collision(){
		/*for(int i = 0; i < arrayNum; i++){
			if(isSolid[i] && tiles[i].intersects(p1.upRect)){
	        	setYDirection(0);
			}
		}*/
	}
	
	public void navigateMap(int nav){
		switch(nav){
			default:
				System.out.println("Dude, not gonna do anything now ");
			case PAN_UP:
				setYDirection(1);
				break;
			case PAN_DOWN:
				setYDirection(-1);
				break;
			case PAN_LEFT:
				setXDirection(1);
				break;
			case PAN_RIGHT:
				setXDirection(-1);
				break;
		}
	}
	
	/*public void destroyTile(int tileNum){
		tiles[tileNum] = new Rectangle(-10000, -10000, 0, 0);//place where tile gets stored
		isSolid[tileNum] = false;//Still have to adjust to leave solid stuff behind
	}*/
	
}

