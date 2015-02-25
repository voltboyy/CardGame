package cardGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

public class Camera {
	
	private World world;
	
	public Rectangle playerRect, upRect, downRect, leftRect, rightRect, upRightCornerRect, downRightCornerRect, downLeftCornerRect, upLeftCornerRect;
	private Image playerImg0, playerImg1, playerImg2, playerImg3;
	
	protected int xDirection, yDirection;
	
	private int xCharacter = 32*16-4, yCharacter = 32*11; //Do not change! this is location of character in the middle of the screen
	
	public int up = 0, down = 0, left = 0, right = 0, leftMovable = 0, rightMovable = 0, upMovable = 0, downMovable = 0, movable, playerTexture = 2,
			upRight = 0, downRight = 0, downLeft = 0, upLeft = 0;
	
	private Weapon weapon;
	
	//Tile variables
	private int hoverX, hoverY;
	private boolean hovering = false;
	
	//Character  movement
	static final int MOVE_UP = 0, MOVE_DOWN = 1, MOVE_LEFT = 2, MOVE_RIGHT = 3;
	
	public Camera(World world){
		this.world = world;
		weapon = new Weapon(Weapon.HAMMER); //unused atm
		playerImg0 = new ImageIcon(getClass().getResource("/PLAYER_MAN0.png")).getImage();
		playerImg1 = new ImageIcon(getClass().getResource("/PLAYER_MAN1.png")).getImage();
		playerImg2 = new ImageIcon(getClass().getResource("/PLAYER_MAN2.png")).getImage();
		playerImg3 = new ImageIcon(getClass().getResource("/PLAYER_MAN3.png")).getImage();
		playerRect = new Rectangle(xCharacter, yCharacter, 32, 32);
		upRect = new Rectangle(xCharacter, yCharacter - 2, 32, 1);
		downRect = new Rectangle(xCharacter, yCharacter + 32, 32, 1);
		leftRect = new Rectangle(xCharacter -1 , yCharacter, 1, 32);
		rightRect = new Rectangle(xCharacter + 32, yCharacter, 1, 32);
		upRightCornerRect = new Rectangle(xCharacter + 32, yCharacter - 1, 1, 1);
		downRightCornerRect = new Rectangle(xCharacter + 32, yCharacter + 32, 1, 1);
		downLeftCornerRect = new Rectangle(xCharacter - 1, yCharacter + 32, 1, 1);
		upLeftCornerRect = new Rectangle(xCharacter - 1, yCharacter - 1, 1, 1);
	}
	
	private void setXDirection(int d){
		xDirection = d;
	}
	
	private void setYDirection(int d){
		yDirection = d;
	}
	
	public int getUp(){
		return this.up;
	}
	public void setUp(int d){
		up = d;
	}
	public int getDown(){
		return this.down;
	}
	public void setDown(int d){
		down = d;
	}
	public int getLeft(){
		return this.left;
	}
	public void setLeft(int d){
		left = d;
	}
	public int getRight(){
		return this.right;
	}
	public void setRight(int d){
		right = d;
	}
	public int getUpMovable(){
		return this.upMovable;
	}
	public void setUpMovable(int d){
		upMovable = d;
	}
	public int getDownMovable(){
		return this.downMovable;
	}
	public void setDownMovable(int d){
		downMovable = d;
	}
	public int getLeftMovable(){
		return this.leftMovable;
	}
	public void setLeftMovable(int d){
		leftMovable = d;
	}
	public int getRightMovable(){
		return this.rightMovable;
	}
	public void setRightMovable(int d){
		rightMovable = d;
	}
	public int getMovable(){
		return this.movable;
	}
	public void setMovable(int d){
		movable = d;
	}
	public int getUpRight(){
		return this.upRight;
	}
	public void setUpRight(int d){
		upRight = d;
	}
	public int getDownRight(){
		return this.downRight;
	}
	public void setDownRight(int d){
		downRight = d;
	}
	public int getDownLeft(){
		return this.downLeft;
	}
	public void setDownLeft(int d){
		downLeft = d;
	}
	public int getUpLeft(){
		return this.upLeft;
	}
	public void setUpLeft(int d){
		upLeft = d;
	}
	public void setCamX(int d){
		playerRect.x += d;
	}
	public void setCamY(int d){
		playerRect.y += d;
	}
	public int getCamX(){
		return playerRect.x;
	}
	public int getCamY(){
		return playerRect.y;
	}
	
	public void update(){
		move();
		//collision();
		//collisionMovableMap();
	}
	public int getPlayerTexture(){
		return this.playerTexture;
	}
	public void setPlayerTexture(int d){
		playerTexture = d;
	}
	
	private void move(){
		playerRect.x += 32*xDirection;
		playerRect.y += 32*yDirection;
		upRect.x += 32*xDirection;
		upRect.y += 32*yDirection;
		downRect.x += 32*xDirection;
		downRect.y += 32*yDirection;
		leftRect.x += 32*xDirection;
		leftRect.y += 32*yDirection;
		rightRect.x += 32*xDirection;
		rightRect.y += 32*yDirection;
	}
	
	/*private void collision(){
		for(int i = 0; i < world.arrayNum; i++){
            if(world.isSolid[i] && upRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	world.setYDirection(0);
            	setUp(1);
            }
            if(!world.isSolid[i] && upRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setUp(0);
            }
            if(world.isSolid[i] && downRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	world.setYDirection(0);
            	setDown(1);
            }
            if(!world.isSolid[i] && downRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setDown(0);
            }
            if(world.isSolid[i] && leftRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	world.setXDirection(0);
            	setLeft(1);
            }
            if(!world.isSolid[i] && leftRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setLeft(0);
            }
            if(world.isSolid[i] && rightRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	world.setXDirection(0);
            	setRight(1);
            }
            if(!world.isSolid[i] && rightRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setRight(0);
            }
            
            //Here are the collisions for the corners
            
            if(world.isSolid[i] && upRightCornerRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setUpRight(1);
            }
            if(!world.isSolid[i] && upRightCornerRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setUpRight(0);
            }
            if(world.isSolid[i] && downRightCornerRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setDownRight(1);
            }
            if(!world.isSolid[i] && downRightCornerRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setDownRight(0);
            }
            if(world.isSolid[i] && downLeftCornerRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setDownLeft(1);
            }
            if(!world.isSolid[i] && downLeftCornerRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setDownLeft(0);
            }
            if(world.isSolid[i] && upLeftCornerRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setUpLeft(1);
            }
            if(!world.isSolid[i] && upLeftCornerRect.intersects(world.tiles[i].x + World.xOffset, world.tiles[i].y + World.yOffset, world.tiles[i].width, world.tiles[i].height)){
            	setUpLeft(0);
            }
            
            //if(up == 1)
            //System.out.println(getUp());
            /*else if(collision == true){
            	System.out.println("true");
            }
            /*if(collision == false){
            	System.out.println("false");
            }
            
        }
    }*/
	
	private void checkForCollision(){
		
	}
	
	public void draw(Graphics g){
		if(getPlayerTexture() == 0){
			g.drawImage(playerImg0, playerRect.x, playerRect.y, null);
		}
		if(getPlayerTexture() == 1){
			g.drawImage(playerImg1, playerRect.x, playerRect.y, null);
		}
		if(getPlayerTexture() == 2){
			g.drawImage(playerImg2, playerRect.x, playerRect.y, null);
		}
		if(getPlayerTexture() == 3){
			g.drawImage(playerImg3, playerRect.x, playerRect.y, null);
		}
		/*if(hovering)
			drawBlockOutline(g);
		g.setColor(Color.RED);
		g.fillRect(upRightCornerRect.x, upRightCornerRect.y, upRightCornerRect.width, upRightCornerRect.height);
		g.fillRect(downRightCornerRect.x, downRightCornerRect.y, downRightCornerRect.width, downRightCornerRect.height);
		g.fillRect(downLeftCornerRect.x, downLeftCornerRect.y, downLeftCornerRect.width, downLeftCornerRect.height);
		g.fillRect(upLeftCornerRect.x, upLeftCornerRect.y, upLeftCornerRect.width, upLeftCornerRect.height);*/
	}
	private void drawBlockOutline(Graphics g){
		g.setColor(Color.BLACK);
		g.drawRect(hoverX, hoverY, world.tiles[0][0].width, world.tiles[0][0].height);
	}
	
	//Mouse events
	public void mousePressed(MouseEvent e){
		int x = e.getX();
		int y = e.getY();
		int px = playerRect.x;
		int py = playerRect.y;
		int leftClick = MouseEvent.BUTTON1;
		int rightClick = MouseEvent.BUTTON3;
		/*for(int i = 0; i < world.arrayNum; i++){
			if(e.getButton() == leftClick){
				if(weapon.isEquipped(weapon.HAMMER) &&
						x > (world.tiles[i].x + World.xOffset) && x < (world.tiles[i].x + World.xOffset) + world.tiles[i].width &&
						y > (world.tiles[i].y + World.yOffset) && y < (world.tiles[i].y + World.yOffset) + world.tiles[i].height &&
						world.isSolid[i] &&
						((world.tiles[i].x + World.xOffset) + (world.tiles[i].width/2)) <= (px + (playerRect.width/2)) + weapon.WEAPON_RADIUS && 
						((world.tiles[i].x + World.xOffset) + (world.tiles[i].width/2)) >= (px + (playerRect.width/2)) - weapon.WEAPON_RADIUS && 
						((world.tiles[i].y + World.yOffset) + (world.tiles[i].height/2)) <= (py + (playerRect.height/2)) + weapon.WEAPON_RADIUS && 
						((world.tiles[i].y + World.yOffset) + (world.tiles[i].height/2)) >= (py + (playerRect.height/2)) - weapon.WEAPON_RADIUS){
					world.destroyTile(i);
				}
			}
			else if(e.getButton() == rightClick){
				
			}
		}*/
	}
	public void mouseReleased(MouseEvent e){
		
	}
	public void mouseMoved(MouseEvent e){
		int x = e.getX();
		int y = e.getY();
		int px = playerRect.x;
		int py = playerRect.y;
		for(int i = 0; i < world.arrayNum; i++){
			for(int j = 0; j < world.arrayNum; j++){
				if(weapon.isEquipped(weapon.HAMMER) &&
						x > (world.tiles[i][j].x + World.xOffset) && x < (world.tiles[i][j].x + World.xOffset) + world.tiles[i][j].width &&
						y > (world.tiles[i][j].y + World.yOffset) && y < (world.tiles[i][j].y + World.yOffset) + world.tiles[i][j].height &&
						world.isSolid[i][j] &&
						((world.tiles[i][j].x + World.xOffset) + (world.tiles[i][j].width/2)) <= (px + (playerRect.width/2)) + weapon.WEAPON_RADIUS && 
						((world.tiles[i][j].x + World.xOffset) + (world.tiles[i][j].width/2)) >= (px + (playerRect.width/2)) - weapon.WEAPON_RADIUS && 
						((world.tiles[i][j].y + World.yOffset) + (world.tiles[i][j].height/2)) <= (py + (playerRect.height/2)) + weapon.WEAPON_RADIUS && 
						((world.tiles[i][j].y + World.yOffset) + (world.tiles[i][j].height/2)) >= (py + (playerRect.height/2)) - weapon.WEAPON_RADIUS){
						hovering = true;
						hoverX = (world.tiles[i][j].x + World.xOffset);
						hoverY = (world.tiles[i][j].y + World.yOffset);
						break;
					}
					else{
						hovering = false;
					}
			}
		}
	}
	public void mouseDragged(MouseEvent e){
		
	}
	
	public void stopMoveCharacter(){
		setXDirection(0);
		setYDirection(0);
	}
	
	public void moveCharacter(int m){
		switch(m){
		default:
			System.out.println("Dude, not gonna move now ");
		case MOVE_UP:
			setYDirection(-1);
			break;
		case MOVE_DOWN:
			setYDirection(1);
			break;
		case MOVE_LEFT:
			setXDirection(-1);
			break;
		case MOVE_RIGHT:
			setXDirection(1);
			break;
		}
	}
	
	private class Weapon{
		public static final int UNARMED = 0;
		public static final int SWORD = 1;
		public static final int GUN = 2;
		public static final int HAMMER = 3;
		
		public int CURRENT_WEAPON;
		
		public int WEAPON_RADIUS;
		
		public Weapon(int w){
			switch(w){
				default:
					System.out.println("No weapon selected!");
					break;
				case UNARMED:
					CURRENT_WEAPON = UNARMED;
					WEAPON_RADIUS = 32;
					break;
				case SWORD:
					CURRENT_WEAPON = SWORD;
					WEAPON_RADIUS = 64;
					break;
				case GUN:
					CURRENT_WEAPON = GUN;
					WEAPON_RADIUS = 128;
					break;
				case HAMMER:
					CURRENT_WEAPON = HAMMER;
					WEAPON_RADIUS = 64;
					break;
			}
		}
		
		public void selectWeapon(int w){
			switch(w){
				default:
					System.out.println("No weapon selected!");
					break;
				case UNARMED:
					CURRENT_WEAPON = UNARMED;
					WEAPON_RADIUS = 32;
					break;
				case SWORD:
					CURRENT_WEAPON = SWORD;
					WEAPON_RADIUS = 64;
					break;
				case GUN:
					CURRENT_WEAPON = GUN;
					WEAPON_RADIUS = 128;
					break;
				case HAMMER:
					CURRENT_WEAPON = HAMMER;
					WEAPON_RADIUS = 64;
					break;
			}
		}
		
		public boolean isEquipped(int w){
			if(w == CURRENT_WEAPON){
				return true;
			}
			else
				return false;
		}
	}


}

