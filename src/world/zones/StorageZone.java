package world.zones;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import entities.EntityType;
import entities.MovableObject;
import util.tasks.Task;
import world.Tile;
import world.WorldMap;

public class StorageZone extends Zone{
	
	protected int availableSpace;
	protected int maxSpace;
	
	protected List<ItemAllowance> allowances;
	
	protected List<MovableObject> storage;

	public StorageZone(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		allowances = new ArrayList<ItemAllowance>();
		allowances.add(new ItemAllowance(EntityType.LOG, new int[0]));
		
		availableSpace = maxSpace = width*height;
		
		storage = new ArrayList<MovableObject>();
	}
	
	public MovableObject findItem(EntityType type, int variant) {
		for(int i = 0; i < storage.size(); i++) {
			Tile storageTile = WorldMap.getMap().getTile((int)storage.get(i).getX()/WorldMap.tileSize, 
					  									 (int)storage.get(i).getY()/WorldMap.tileSize, 
					  									 0);
			if(storageTile.getFirstTypeTask(Task.ITEM_DROPPED) != null) continue;
			if(type != storage.get(i).getType()) continue;
			
			if(variant == -1 || variant == storage.get(i).getVariant()) {
				return storage.get(i);
			}
		}
		return null;
	}
	
	public boolean isItemAllowed(MovableObject item) {
		for(int i = 0; i < allowances.size(); i++) {
			if(allowances.get(i).isItemAllowed(item)) {
				return true;
			}
		}
		return false;
	}
	
	public void occupyOne() {
		availableSpace--;
	}
	
	public void freeOne() {
		availableSpace++;
	}
	
	public int getSpace() {
		return availableSpace;
	}
	
	public int getCapacity() {
		return maxSpace;
	}

	@Override
	public int getType() {
		return Zone.STORAGE;
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) {
		g.setColor(Color.yellow.darker());
		g.drawRect(x*WorldMap.tileSize, y*WorldMap.tileSize, width*WorldMap.tileSize, height*WorldMap.tileSize);
		g.drawString("Space: " + availableSpace, x*WorldMap.tileSize+5, y*WorldMap.tileSize+5);
		g.drawString("MaxSpace: " + maxSpace, x*WorldMap.tileSize+5, y*WorldMap.tileSize+35);
	}
	
	public Tile pickFreeTile(MovableObject item) {
		
		for(int i = x; i < x+width; i++) {
			for(int j = y; j < y+height; j++) {
				Tile pickedTile = WorldMap.getMap().getTile(i,j,0);
				if(!pickedTile.canAcceptItem(item)) {
					continue;
				}
				if(pickedTile.getFirstTypeTask(Task.ITEM_DROPPED) == null) {
					return pickedTile;
				}
			}
		}
		return null;
	}

	@Override
	public void itemDropped(MovableObject item) {
		storage.add(item);
	}

	@Override
	public void itemPicked(MovableObject item) {
		storage.remove(item);
		freeOne();
	}
	
}
