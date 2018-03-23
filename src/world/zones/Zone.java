package world.zones;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import entities.MovableObject;
import graphics.Renderable;
import world.WorldMap;

public abstract class Zone implements Renderable{
	public static final int STORAGE = 0;
	
	protected int x;
	protected int y;
	
	protected int width;
	protected int height;
	
	public Zone(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				WorldMap.getMap().getTile(x+i, y+j, 0).setZone(this);
			}
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public abstract void itemDropped(MovableObject item);
	
	public abstract void itemPicked(MovableObject item);
	
	public abstract int getType();

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) {
		g.setColor(Color.yellow.darker());
		g.drawRect(x*WorldMap.tileSize, y*WorldMap.tileSize, width*WorldMap.tileSize, height*WorldMap.tileSize);
	}

	@Override
	public int getPriority() {
		
		return y*WorldMap.tileSize + height*WorldMap.tileSize;
	}
}
