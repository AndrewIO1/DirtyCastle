package world;

import java.util.ArrayList;

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

	private int x;
	private int y;
	private int z;

	public static enum TILE_TYPE{
		NONE(-1, "Nothing"),
		GRASS(0, "Grass"),
		ROCK(1, "Rock"),
		DIRT(2, "Dirt"),
		WATER(3, "Water");
		
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

	private int weight;
	private TileGroup group;
	private TILE_TYPE wallType;
	private TILE_TYPE floorType;
	private GameObject staticObject;
	private ArrayList<MovableObject> itemPile;
	private ArrayList<Task> tileTasks;
	private ArrayList<Path> partOfPaths;
	private ArrayList<Creature> creaturesInside;
	private Zone assignedZone;

	public Tile(TILE_TYPE wallType, TILE_TYPE floorType, int x, int y, int z){
		weight = 1;
		this.wallType = wallType;
		this.floorType = floorType;
		group = WorldMap.getMap().groupManager().getGroup(-1);
		itemPile = new ArrayList<MovableObject>();
		tileTasks = new ArrayList<Task>();
		partOfPaths = new ArrayList<Path>();
		creaturesInside = new ArrayList<Creature>();
		assignedZone = null;
		this.x = x;
		this.y = y;
		this.z = z;
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

	public int getWeight(){
		return weight;
	}

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

	public void setWeight(int weight){
		this.weight = weight;
	}

	public void setObject(GameObject object){
		if(object == null) {
			weight = 0;
		}else {
			weight = 999;
		}
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
		int x = this.x*WorldMap.tileSize;
		int y = this.y*WorldMap.tileSize;
		if(getWall() != TILE_TYPE.NONE) {
			Tile downTile = WorldMap.getMap().getTile(x/32, y/32+1, 0);
			if(downTile == null || downTile.getWall() == TILE_TYPE.NONE) {
				g.setColor(Color.darkGray);
				g.fillRect(x, y+WorldMap.tileSize/2, WorldMap.tileSize, WorldMap.tileSize/2);
			}
			y -= WorldMap.tileSize/2;
		}
		if(getFirstTypeTask(Task.TILE_MINE)!=null) {
			g.drawImage(toRender, x, y, Color.yellow);
		}else{
			if(wallType != TILE_TYPE.NONE) {
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
		
		//�����
		//g.setColor(Color.white);
		//g.drawString("P:" + partOfPaths.size(), x, y); //���-�� �����, ���������� ����� ����
		//g.drawString("C:" + creaturesInside.size(), x, y); //���-�� ������� � �����
		//g.drawString("G:" + group.getId(), x, y); //������ �����
		//g.drawRect(x, y, 32, 32); //����� ����� �� ������
	}

	@Override
	public int getPriority() {
		int priority = y*WorldMap.tileSize-256;
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
				getWall() == TILE_TYPE.NONE && 
				getFloor() != TILE_TYPE.NONE &&
				getFloor() != TILE_TYPE.WATER;
	}
	
	public String toString() {
		return "Wall: " + getWall() + " Floor: " + getFloor();
	}

}
