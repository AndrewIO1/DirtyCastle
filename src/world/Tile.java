package world;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import ai.Path;
import entities.Creature;
import entities.GameObject;
import entities.MovableObject;
import graphics.Renderable;
import graphics.TextureStorage;
import util.tasks.Task;
import world.tile_groups.TileGroup;
import world.zones.Zone;

public class Tile implements Renderable{

	private short x;
	private short y;
	private short z;

	public static enum TILE_TYPE{
		NONE(-1, "Nothing"),
		GRASS(0, "Grass"),
		ROCK(1, "Rock"),
		DIRT(2, "Dirt"),
		WATER(3, "Water"),
		STAIRS_D_GRASS(4, "Stairs down"),
		STAIRS_U_GRASS(5, "Stairs down");
		
		private int type;
		private String name;
		
		TILE_TYPE(int type, String name){
			this.type = type;
			this.name = name;
		}
		
		public int type() { return type; }
		
		public String toString() {
			return name;
		}
	}

	//private int weight;
	private TileGroup group;
	private TILE_TYPE wallType;
	private TILE_TYPE floorType;
	private GameObject staticObject;
	private List<MovableObject> itemPile;
	private List<Task> tileTasks;
	private List<Path> partOfPaths;
	private List<Creature> creaturesInside;
	private Zone assignedZone;
	private short renderX;
	private short renderY;

	public Tile(TILE_TYPE wallType, TILE_TYPE floorType, short x, short y, short z){
		//weight = 1;
		WorldMap map = WorldMap.getMap();
		this.wallType = wallType;
		this.floorType = floorType;
		group = map.groupManager().getGroup(-1);
		itemPile = new ArrayList<MovableObject>(1);
		tileTasks = new ArrayList<Task>(1);
		partOfPaths = new ArrayList<Path>(1);
		creaturesInside = new ArrayList<Creature>(1);
		assignedZone = null;
		this.x = x;
		this.y = y;
		this.z = z;
		renderX = (short) (x*WorldMap.tileSize);
		renderY = (short) (y*WorldMap.tileSize);
	}
	
	public boolean containsCreature() {
		return creaturesInside.size() > 0;
	}
	
	public boolean containsPath() {
		return partOfPaths.size() > 0;
	}
	
	public void addCreature(Creature toAdd) {
		creaturesInside.add(toAdd);
	}
	
	public void removeCreature(Creature toRem) {
		creaturesInside.remove(toRem);
	}
	
	public void addPath(Path path) {
		partOfPaths.add(path);
	}
	
	public void removePath(Path path) {
		partOfPaths.remove(path);
	}
	
	public void itemPicked(MovableObject item, Creature picker) {
		if(!itemPile.contains(item)) return;
		picker.giveItem(item);
		itemPile.remove(item);
		if(assignedZone == null) return;
		assignedZone.itemPicked(item);
	}
	
	public Zone getZone() {
		return assignedZone;
	}
	
	public void setZone(Zone zone) {
		assignedZone = zone;
	}

	/*public int getWeight(){
		return weight;
	}*/

	public TILE_TYPE getWall(){
		return wallType;
	}

	public TILE_TYPE getFloor(){
		return floorType;
	}

	public int getRenderType() {
		if(wallType != TILE_TYPE.NONE) {
			return wallType.type();
		}
		return floorType.type();
	}

	public int getGroup(){
		return group.getId();
	}
	
	public TileGroup getGroupObject(){
		return group;
	}

	public GameObject getObject(){
		return staticObject;
	}

	public void setGroup(TileGroup group){
		this.group.removeTile(this);
		group.addTile(this);
		this.group = group;
	}
	
	public void setGroup(int group) {
		setGroup(WorldMap.getMap().groupManager().getGroup(group));
	}

	/*public void setWeight(int weight){
		this.weight = weight;
	}*/

	public void setObject(GameObject object){
		/*if(object == null) {
			weight = 0;
		}else {
			weight = 999;
		}*/
		this.staticObject = object;
	}

	public void addTask(Task task) {
		if(tileTasks.contains(task)) {
			System.out.println("task already added");
			return;
		}
		tileTasks.add(task);
	}

	public Task getFirstTypeTask(int type) {
		if(tileTasks.size() == 0) return null;
		for(int i = 0; i < tileTasks.size(); i++) {
			if(tileTasks.get(i).getType() == type) {
				return tileTasks.get(i);
			}
		}
		return null;
	}

	public void removeTask(Task task) {
		tileTasks.remove(task);
	}
	
	public boolean canAcceptItem(MovableObject item) {
		return itemPile.size() == 0;
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) {
		Image toRender;
		int renderType = getRenderType();
		if(renderType == -1) return;
		
		toRender = TextureStorage.getSpriteTile("tiles", renderType, 0);
		
		if(toRender == null) {
			return;
		}
		WorldMap map = WorldMap.getMap();
		int x = renderX;
		int renderZ = z - map.getZ();
		int y = renderY + (renderZ>0?(renderZ*16):0);
		if(getWall() != TILE_TYPE.NONE) {
			Tile downTile = map.getTile(this.x, this.y+1, 0);
			if(downTile == null || downTile.getWall() == TILE_TYPE.NONE) {
				g.setColor(Color.darkGray);
				g.fillRect(x, y+WorldMap.tileSize/2, WorldMap.tileSize, WorldMap.tileSize/2);
			}
			y -= WorldMap.tileSize/2;
		}
		if(getFirstTypeTask(Task.TILE_MINE)!=null) {
			g.drawImage(toRender, x, y, Color.yellow);
		}else{
			if(wallType != TILE_TYPE.NONE || z != map.getZ()) {
				g.drawImage(toRender, x, y, Color.darkGray);
			}else{
				g.drawImage(toRender, x, y);
			}
		}
		
		if(getFirstTypeTask(Task.WALL_BUILD) != null) {
			Color taskColor = new Color(Color.magenta);
			taskColor.a = 0.4f;
			g.setColor(taskColor);
			g.fillRect(x, y, 32, 32);
		}
		if(itemPile.size() > 0) {
			itemPile.get(0).render(gc, game, g);
		}
		
		//ДЕБАГ
		//g.setColor(Color.white);
		//g.drawString("P:" + partOfPaths.size(), x, y); //Кол-во путей, проходящих через тайл
		//g.drawString("C:" + creaturesInside.size(), x, y); //Кол-во существ в тайле
		//g.drawString("G:" + group.getId(), x, y); //Группа тайла
		//g.drawRect(x, y, 32, 32); //Дебаг сетка на тайлах
	}

	@Override
	public int getPriority() {
		int priority = y*WorldMap.tileSize-256 - z*16;
		if(getWall() != TILE_TYPE.NONE) {
			priority += 256;
		}
		return priority;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}
	
	public final int getZ() {
		return z;
	}

	public void setWall(TILE_TYPE wall) {
		this.wallType = wall;
	}
	
	public void setFloor(TILE_TYPE floor) {
		this.floorType = floor;
	}

	public void addLoot(MovableObject item) {
		itemPile.add(item);
		if(assignedZone == null) return;
		assignedZone.itemDropped(item);
	}
	
	public int getItemAmount() {
		return itemPile.size();
	}
	
	public void consumeItems() {
		itemPile.clear();
	}

	public boolean tilePassable() {
		
		return getObject() == null &&
				(getWall() == TILE_TYPE.NONE ||
				getWall() == TILE_TYPE.STAIRS_U_GRASS) && 
				getFloor() != TILE_TYPE.NONE &&
				getFloor() != TILE_TYPE.WATER;
	}
	
	public String toString() {
		return "Wall: " + getWall() + " Floor: " + getFloor();
	}

}
