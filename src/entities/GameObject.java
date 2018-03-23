package entities;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import core.DwarfsGame;
import graphics.Renderable;
import util.tasks.Task;
import world.Tile;
import world.WorldMap;

/*
 * 
 * ��� �������� ����� � �������� ����������� ��� ���� ��������, ���� ���������, �������, ������� � �������.
 * �������� �� ���� ����������� ������ ��������� �������, ������� ���� �� ���� ������ �� ������
 * � �� ��� ����� �������� �� ������������ �� �����, ���� ��������
 * 
 */

public abstract class GameObject implements Renderable {
	protected String name;//��� �������, ��� ������
	protected float x;//���������� � ����
	protected float y;//���������� � ����
	protected int z; //��� �������, �� ������� ��������� ������
	protected int width;//������ �������
	protected int height;//������ �������
	protected float x_anchor;//����� �������
	protected float y_anchor;//����� �������
	protected int animationId;//����� ��������
	protected int animationFrame;//���� ��������
	protected int animationTime;
	protected int animationTimer = 0;
	protected boolean flipX;//�������������� ������� ��������
	protected boolean flipY;//������������ ������� ��������
	protected float rotation;//������� �������
	protected int hp;
	protected int maxHp;
	protected boolean dead = false;
	protected int variant;
	
	protected ArrayList<Task> objectTasks;
	protected ArrayList<ArrayList<Image>> sprites;//��� ��������
	
	protected WorldMap map;
	
	public GameObject(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor, WorldMap map){
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.x_anchor = x_anchor;
		this.y_anchor = y_anchor;
		animationId = 0;
		animationFrame = 0;
		flipX = false;
		flipY = false;
		rotation = 0;
		sprites = new ArrayList<ArrayList<Image>>(0);
		objectTasks = new ArrayList<Task>(0);
		this.map = map;
	}
	
	public GameObject(Builder builder){
		name = builder.name;
		x = builder.x;
		y = builder.y;
		z = builder.z;
		width = builder.width;
		height = builder.height;
		x_anchor = builder.x_anchor;
		y_anchor = builder.y_anchor;
		animationId = builder.animationId;
		animationFrame = builder.animationFrame;
		animationTime = builder.animationTime;
		flipX = builder.flipX;
		flipY = builder.flipY;
		rotation = builder.rotation;
		hp = builder.hp;
		maxHp = builder.maxHp;
		variant = builder.variant;
		map = builder.map;
		
		sprites = new ArrayList<ArrayList<Image>>(0);
		objectTasks = new ArrayList<Task>(0);
	}
	
	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Tile getTile() {
		return WorldMap.getMap().getTile((int)x/WorldMap.tileSize, (int)y/WorldMap.tileSize, z);
	}
	
	public void addTask(Task task) {
		objectTasks.add(task);
	}
	
	public void removeTask(Task task) {
		objectTasks.remove(task);
	}
	
	public Task getFirstTypeTask(int type) {
		if(objectTasks.size() == 0) return null;
		for(int i = 0; i < objectTasks.size(); i++) {
			if(objectTasks.get(i).getType() == type) {
				return objectTasks.get(i);
			}
		}
		return null;
	}
	
	public void update(GameContainer gc, StateBasedGame game, int delta) {
		updateAnimation(delta);
		customUpdate(gc, game, delta);
	}
	
	public final void render(GameContainer gc, StateBasedGame game, Graphics g) {//���������
		
		customRenderUnder(gc, game, g);
		
		Image sprite = getCurrentSprite();
		
		if(sprite != null){//���� ����� ���, �� �����
			//���� �� ������, �� �������� ������ � ������ ��������� � ����������� �������
			sprite = sprite.getScaledCopy(width, height).getFlippedCopy(flipX, flipY);
			sprite.setCenterOfRotation(width*x_anchor, height*y_anchor);
			sprite.rotate(rotation);
			
			if(!isSelected()) {
				g.drawImage(sprite, x-width*x_anchor, y-height*y_anchor);
			}else {
				sprite.drawFlash(x-width*x_anchor, y-height*y_anchor, width, height, Color.pink.darker());
				//g.drawImage(sprite, x-width*x_anchor, y-height*y_anchor, Color.pink);
			}
			
		}
		
		customRenderAbove(gc, game, g);
	}
	
	public final String getName() {
		return name;
	}

	public final float getX(){//�������������� ����������
		return x;
	}

	public final float getY(){//������������ ����������
		return y;
	}
	
	public final int getZ(){
		return z;
	}
	
	public final int getWidth(){//������ �������
		return width;
	}
	
	public final int getHeight(){//������ �������
		return height;
	}
	
	public final boolean flippedHorizontally(){//��������� �� ������ �� �����������?
		return flipX;
	}
	
	public final boolean flippedVertically(){//��������� �� ������ �� ���������?
		return flipY;
	}

	public final float getXAnchor(){//���������� ����� ������� �� �����������
		return x_anchor;
	}

	public final float getYAnchor(){//���������� ����� ������� �� ���������
		return y_anchor;
	}

	public final Image getCurrentSprite(){
		if(sprites == null || sprites.size() == 0){//���� �������� ��� ������
			if(DwarfsGame.debug){
				System.out.println("Error: object " + name + " has no sprites inside");
			}
			return null;
		}
		if(sprites.size() <= animationId){//���� ��� ����� ��������
			if(DwarfsGame.debug){
				System.out.println("Error: object " + name + " has no animation with id " + animationId);
			}
			return null;
		}
		if(sprites.get(animationId).size() <= animationFrame){//���� ��� ������ �����
			if(DwarfsGame.debug){
				System.out.println("Error: object " + name + " has animationFrame out of bounds in animation " + animationId);
			}
			return null;
		}
		return sprites.get(animationId).get(animationFrame);//���������� ������� ���� ������� ��������
	}
	
	public final int getAnimation(){//���������� id ������� ��������
		return animationId;
	}
	
	public final int getAnimationFrame(){//���������� ���� ������� ��������
		return animationFrame;
	}
	
	public final void changeAnimation(int id, int frame, int frameTime) {
		if(animationId == id) return;
		
		animationId = id;
		if(animationId >= sprites.size() || animationId < 0) {
			animationId = 0;
		}
		animationFrame = frame;
		if(animationFrame >= sprites.get(animationId).size() || animationFrame < 0) {
			animationFrame = 0;
		}
		animationTime = frameTime;
	}
	
	public final void changeAnimation(int id, int frameTime) {
		changeAnimation(id, 0, frameTime);
	}
	
	public final void changeAnimation(int id) {
		changeAnimation(id, 0, 100);
	}
	
	public final void updateAnimation(int delta) {
		animationTimer+=delta;
		if(animationTimer >= animationTime) {
			animationTimer = 0;
			animationFrame++;
			if(animationFrame >= sprites.get(animationId).size()) {
				animationFrame = 0;
			}
		}
	}
	
	public final WorldMap getMap() {
		return map;
	}
	
	public final void damage(int damage) {
		hp -= damage;
		if(hp <= 0 && !dead) {
			kill();
		}
	}
	
	public final boolean isDead() {
		return dead;
	}
	
	public final void kill() {
		dead = true;
		WorldMap.getMap().requestUpdate(this);
		killCustom();
	}
	
	public int getVariant() {
		return variant;
	}
	
	public int getPriority() {
		return (int) getY();
	}
	
	public abstract boolean isSelected();//������� �� ������
	public abstract EntityType getType();//��� �������
	protected abstract void customUpdate(GameContainer gc, StateBasedGame game, int delta);
	protected abstract void customRenderUnder(GameContainer gc, StateBasedGame game, Graphics g);//������ ��� ��������
	protected abstract void customRenderAbove(GameContainer gc, StateBasedGame game, Graphics g);//������ ��� ��������
	protected abstract void killCustom();//������� ����������� ������� � ���������� ���� ��� ��� ������-�� ���������
	
	public static abstract class Builder{
		private String name;//��� �������, ��� ������
		private float x = 0;//���������� � ����
		private float y = 0;//���������� � ����
		private int z = 0; //��� �������, �� ������� ��������� ������
		private int width = WorldMap.tileSize;//������ �������
		private int height = WorldMap.tileSize;//������ �������
		private float x_anchor = 0;//����� �������
		private float y_anchor = 0;//����� �������
		private int animationId = 0;//����� ��������
		private int animationFrame = 0;//���� ��������
		private int animationTime = 200;
		private boolean flipX = false;//�������������� ������� ��������
		private boolean flipY = false;//������������ ������� ��������
		private float rotation = 0;//������� �������
		private int hp = 0;
		private int maxHp = 0;
		private int variant = 0;
		private WorldMap map = WorldMap.getMap();
		
		public Builder(String name) {this.name = name; }
		public Builder name(String name) { this.name = name; return this; }
		public Builder x(float x) { this.x = x; return this; }
		public Builder y(float y) { this.y = y; return this; }
		public Builder z(int z) { this.z = z; return this; }
		public Builder width(int width) { this.width = width; return this; }
		public Builder height(int height) { this.height = height; return this; }
		public Builder x_anchor(float x_anchor) { this.x_anchor = x_anchor; return this; }
		public Builder y_anchor(float y_anchor) { this.y_anchor = y_anchor; return this; }
		public Builder animationId(int animationId) { this.animationId = animationId; return this; }
		public Builder animationFrame(int animationFrame) { this.animationFrame = animationFrame; return this; }
		public Builder animationTime(int animationTime) { this.animationTime = animationTime; return this; }
		public Builder flipX(boolean flipX) { this.flipX = flipX; return this; }
		public Builder flipY(boolean flipY) { this.flipY = flipY; return this; }
		public Builder rotation(float rotation) { this.rotation = rotation; return this; }
		public Builder hp(int hp) { this.hp = hp; return this; }
		public Builder maxHp(int maxHp) { this.maxHp = maxHp; return this; }
		public Builder variant(int variant) { this.variant = variant; return this; }
		public Builder map(WorldMap map) { this.map = map; return this; }
		
		public String name() { return name; }
		public int variant() { return variant; }
		public int maxHp() { return maxHp; }
	}
	
}
