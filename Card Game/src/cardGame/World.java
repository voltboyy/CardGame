package cardGame;

import java.awt.*;
import java.util.Random;

import javax.swing.ImageIcon;

public class World {

	public Rectangle[] tiles;
	private Image[] tileImg;
	public String[] tileName;
	public boolean[] isSolid;
	public boolean[] noLeft, noRight, noUp, noDown, isMapMovable;
	public boolean unsynced;
	//public boolean[] isHoverable;
	public static final int arrayNum = 792; //amount of tiles on the map
	
	private Camera p1;
	
	public boolean loaded;
	
	//Tile images
	public Image TILE_BLACK, TILE_GRASS, TILE_EMPTY;
	
	Rectangle RandomRectangle = new Rectangle(0, 0, 32, 32); //This is used to track the position of the map
	
	private int Imagex, Imagey, xDirection, yDirection, TreeChance, BlackChance, GrassChance, OreChance, WildLifeChance, 
	movedX = 0, movedY = 0;
	
	public static int 
	xOffset = 0,//33*-32, //-192
	yOffset = 0,//38*-32,
	widthAmount = 33,
	widthMap = widthAmount*32; //width of tiled map (#/32)
	
	//Map navigation
	static final int PAN_UP = 0, PAN_DOWN = 1, PAN_LEFT = 2, PAN_RIGHT = 3;
	
	public World(){
		TILE_BLACK = new ImageIcon(getClass().getResource("/TILE_BLACK.png")).getImage();
		TILE_GRASS = new ImageIcon(getClass().getResource("/TILE_GRASS.png")).getImage();
		TILE_EMPTY = new ImageIcon(getClass().getResource("/TILE_EMPTY.png")).getImage();
		tiles = new Rectangle[arrayNum];
		tileImg = new Image[arrayNum];
		tileName = new String[arrayNum];
		isSolid = new boolean[arrayNum];
		noLeft = new boolean[arrayNum];
		noRight = new boolean[arrayNum];
		noUp = new boolean[arrayNum];
		noDown = new boolean[arrayNum];
		isMapMovable = new boolean[arrayNum];
		
		p1 = new Camera(this);
		
		loadArrays();
		//loadArraysAgain();
		//decideMapMovable();
	}
	
	public void loadArrays(){
		widthMap = widthAmount*32+movedX;
		Imagex = movedX;
		Imagey = movedY;
		for(int i = 0; i < arrayNum; i++){
			if(Imagex >= widthMap){ //width of tiled map (#/32)
            	Imagex = movedX;
            	Imagey += 32; //height of tiles
            }
			if(i >= 0 && i < 10000){
				tileImg[i] = TILE_EMPTY;
				tiles[i] = new Rectangle(Imagex, Imagey, 32, 32);
				isSolid[i] = true; //Normaal ni, maar dan werkt hover over tile wel
				tileName[i] = "Empty";
			}
			if(i >= 0 && i < 10000){ //must be larger than arraynum
				Random r = new Random();
				
				TreeChance = r.nextInt(100) + 1;
				OreChance = r.nextInt(100) + 1;
				WildLifeChance = r.nextInt(100) + 1;
				
				/*BlackChance = r.nextInt(50) + 1;
				GrassChance = r.nextInt(80) + 1;
				if(i>0 && i<10 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>9 && i<20 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>10 && i<20 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>19 && i<30 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>20 && i<30 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>29 && i<40 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>30 && i<40 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>39 && i<50 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>40 && i<50 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>49 && i<60 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>50 && i<60 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>59 && i<70 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>60 && i<70 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>69 && i<80 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>70 && i<80 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>79 && i<90 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>80 && i<90 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>89 && i<100 && tileImg[i-widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>90 && i<100 && tileImg[i-1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(GrassChance == 1){
					tileImg[i] = TILE_GRASS;
				}*/
				/*if(TreeChance <= 3){
					tileImg[i] = TILE_WOOD;
					isSolid[i] = true;
					tileName[i] = "Wood";
				}
				else if(OreChance <= 2){
					tileImg[i] = TILE_ORE;
					isSolid[i] = true;
					tileName[i] = "Stone";
				}
				else if(WildLifeChance <= 2){
					tileImg[i] = TILE_WILDLIFE;
					isSolid[i] = true;
					tileName[i] = "Wildlife";
				}
				else*/
					tileImg[i] = TILE_EMPTY;
            	tiles[i] = new Rectangle(Imagex, Imagey, 32, 32);
            }
			Imagex += 32; //width of tiles
		}
	}
	
	/*private void loadArraysAgain(){
		for(int i = 0; i < arrayNum; i++){
			if(Imagex >= widthMap){ //width of tiled map (#/32)
            	Imagex = 0;
            	Imagey += 32; //height of tiles
            }
			if(i >= 0 && i < 100){ //must be larger than arraynum
				Random r = new Random();
				
				GrassChance = r.nextInt(80) + 1;
				if(i>0 && i<10 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>9 && i<20 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>10 && i<20 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>19 && i<30 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>20 && i<30 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>29 && i<40 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>30 && i<40 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>39 && i<50 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>40 && i<50 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>49 && i<60 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>50 && i<60 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>59 && i<70 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>60 && i<70 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>69 && i<80 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>70 && i<80 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>79 && i<90 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				else if(i>80 && i<90 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				/*else if(i>89 && i<99 && tileImg[i+widthAmount] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
				/*else if(i>90 && i<100 && tileImg[i+1] == TILE_GRASS && GrassChance >= 20){
					tileImg[i] = TILE_GRASS;
				}
            	tiles[i] = new Rectangle(Imagex, Imagey, 32, 32);
            }
			/*if(i == 1){
				tileImg[i] = TILE_GRASS;
				tiles[i] = new Rectangle(Imagex, Imagey, 32, 32);
			}
			Imagex += 32; //width of tiles
		}
	}*/
	
	public void drawWorld(Graphics g){
		//33*24
		
		/*for(int i = 0; i < 33; i++){
			g.drawImage(tileImg[i], tiles[i].x + xOffset, tiles[i].y + yOffset, null);
		}*/
		widthMap = widthAmount*32+movedX;
		Imagex = movedX;
		Imagey = movedY;
		for(int i = 0; i < arrayNum; i++){
			if(Imagex >= widthMap){ //width of tiled map (#/32)
            	Imagex = movedX;
            	Imagey += 32; //height of tiles
            }
			if(i >= 0 && i < 10000){
				tileImg[i] = TILE_EMPTY;
				tiles[i] = new Rectangle(Imagex, Imagey, 32, 32);
				isSolid[i] = true; //Normaal ni, maar dan werkt hover over tile wel
				tileName[i] = "Empty";
			}
			Imagex += 32; //width of tiles
			
			g.drawImage(tileImg[i], tiles[i].x + xOffset, tiles[i].y + yOffset, null);
			
		}
	}
	
	public void moveMap(){
		for(Rectangle r : tiles){
			/*r.x += 32*xDirection;
			r.y += 32*yDirection;*/
			//collision();
			r.x += xDirection;
			r.y += yDirection;
		}
		RandomRectangle.x += xDirection;
		RandomRectangle.y += yDirection;
		//System.out.println(RandomRectangle.x + " " + RandomRectangle.y);
	}
	public boolean testgrid(){
		unsynced = false;
		int combined;
		boolean sync;
		for(int i = 0; i < arrayNum; i++){
			if(tiles[i].x != 0 && tiles[i].y != 0 && i != 0){
				combined = tiles[i-1].x + tiles[i].y;
				sync = combined % 32 == 0;
				if(!sync){
					unsynced = true;
					//System.out.println(tiles[i-1].x%tiles[i].y);
				}
			}
			
		}
		return unsynced;
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
	public void setTiledX(int d){
		for(Rectangle r : tiles){
			r.x += d;
		}
	}
	public void setTiledY(int d){
		for(Rectangle r : tiles){
			r.y += d;
		}
	}
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
	
	public void destroyTile(int tileNum){
		tiles[tileNum] = new Rectangle(-10000, -10000, 0, 0);//place where tile gets stored
		isSolid[tileNum] = false;//Still have to adjust to leave solid stuff behind
	}
	
}

