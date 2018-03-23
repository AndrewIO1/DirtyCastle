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
 * Это основной класс с базовыми переменными для всех объектов, типа координат, размера, текстур и прочего.
 * Напрямую от него наследуются только статичные объекты, которые сами по себе ничего не делают
 * и ни при каких условиях не перемещаются по карте, типа деревьев
 * 
 */

public abstract class GameObject implements Renderable {
	protected String name;//имя объекта, для дебага
	protected float x;//координаты в игре
	protected float y;//координаты в игре
	protected int z; //это уровень, на котором находится объект
	protected int width;//ширина объекта
	protected int height;//высота объекта
	protected float x_anchor;//центр объекта
	protected float y_anchor;//центр объекта
	protected int animationId;//номер анимации
	protected int animationFrame;//кадр анимации
	protected int animationTime;
	protected int animationTimer = 0;
	protected boolean flipX;//Горизонтальный поворот текстуры
	protected boolean flipY;//Вертикальный поворот текстуры
	protected float rotation;//Поворот спрайта
	protected int hp;
	protected int maxHp;
	protected boolean dead = false;
	protected int variant;
	
	protected ArrayList<Task> objectTasks;
	protected ArrayList<ArrayList<Image>> sprites;//все анимации
	
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
	
	public final void render(GameContainer gc, StateBasedGame game, Graphics g) {//Отрисовка
		
		customRenderUnder(gc, game, g);
		
		Image sprite = getCurrentSprite();
		
		if(sprite != null){//Если кадра нет, то выход
			//Если всё нормас, то рисуется спрайт с нужным поворотом в координатах объекта
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

	public final float getX(){//Горизонтальные координаты
		return x;
	}

	public final float getY(){//Вертикальные координаты
		return y;
	}
	
	public final int getZ(){
		return z;
	}
	
	public final int getWidth(){//Ширина объекта
		return width;
	}
	
	public final int getHeight(){//Высота объекта
		return height;
	}
	
	public final boolean flippedHorizontally(){//Перевёрнут ли спрайт по горизонтали?
		return flipX;
	}
	
	public final boolean flippedVertically(){//Перевёрнут ли спрайт по вертикали?
		return flipY;
	}

	public final float getXAnchor(){//Возвращает центр спрайта по горизонтали
		return x_anchor;
	}

	public final float getYAnchor(){//Возвращает центр спрайта по вертикали
		return y_anchor;
	}

	public final Image getCurrentSprite(){
		if(sprites == null || sprites.size() == 0){//Если спрайтов нет вообще
			if(DwarfsGame.debug){
				System.out.println("Error: object " + name + " has no sprites inside");
			}
			return null;
		}
		if(sprites.size() <= animationId){//Если нет такой анимации
			if(DwarfsGame.debug){
				System.out.println("Error: object " + name + " has no animation with id " + animationId);
			}
			return null;
		}
		if(sprites.get(animationId).size() <= animationFrame){//Если нет такого кадра
			if(DwarfsGame.debug){
				System.out.println("Error: object " + name + " has animationFrame out of bounds in animation " + animationId);
			}
			return null;
		}
		return sprites.get(animationId).get(animationFrame);//Возвращает текущий кадр текущей анимации
	}
	
	public final int getAnimation(){//Возвращает id текущей анимации
		return animationId;
	}
	
	public final int getAnimationFrame(){//Возвращает кадр текущей анимации
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
	
	public abstract boolean isSelected();//Выделен ли объект
	public abstract EntityType getType();//Тип объекта
	protected abstract void customUpdate(GameContainer gc, StateBasedGame game, int delta);
	protected abstract void customRenderUnder(GameContainer gc, StateBasedGame game, Graphics g);//Рисует под спрайтом
	protected abstract void customRenderAbove(GameContainer gc, StateBasedGame game, Graphics g);//Рисует над спрайтом
	protected abstract void killCustom();//Игровое уничтожение объекта с выпадением лута или ещё какими-то событиями
	
	public static abstract class Builder{
		private String name;//имя объекта, для дебага
		private float x = 0;//координаты в игре
		private float y = 0;//координаты в игре
		private int z = 0; //это уровень, на котором находится объект
		private int width = WorldMap.tileSize;//ширина объекта
		private int height = WorldMap.tileSize;//высота объекта
		private float x_anchor = 0;//центр объекта
		private float y_anchor = 0;//центр объекта
		private int animationId = 0;//номер анимации
		private int animationFrame = 0;//кадр анимации
		private int animationTime = 200;
		private boolean flipX = false;//Горизонтальный поворот текстуры
		private boolean flipY = false;//Вертикальный поворот текстуры
		private float rotation = 0;//Поворот спрайта
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
