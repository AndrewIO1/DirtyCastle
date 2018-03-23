package entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import ai.AI;
import world.Tile;

/*
 * 
 * ����� ��� �������, � �� ���� ����� ������ ���������� ��� ������ �������
 * ��� ������� ����� ����������� � ��, �� ��� ����� ��� ���� ����� �������� ����� �
 * ����, ������� � ������������ + ������� ���, ����� ����� �������� ���������
 * ���� ��, ����� �� ��� ���-�� ����������, �������� ������ �������
 * �� ��� � ������ ���, ����� ��� �������� � ������ �����������������, ��� ������ ����� ���� ����� �����
 * 
 */

public abstract class Creature extends MovableObject{

	protected AI ai;
	protected float speed;
	protected int str; //����
	protected int intel; //��
	protected int dex; //��������
	protected int sex; //���
	protected int bodyType;

	protected Tile tilePos;

	protected MovableObject itemInHands = null;
	
	public Creature(Builder builder) {
		super(builder);
		speed = builder.speed;
		str = builder.str;
		intel = builder.intel;
		dex = builder.dex;
		sex = builder.sex;
		
		changeTile(getTile());
	}

	public final void changeTile(Tile tile) {
		if(tilePos != null) {
			tilePos.removeCreature(this);
		}
		tilePos = tile;
		tilePos.addCreature(this);
	}

	public void setAI(AI ai){//���������� ��
		ai.setHost(this);
		this.ai = ai;
	}

	public final AI getAI() {
		return ai;
	}

	public final int getHP(){
		return hp;
	}

	public final int getMaxHP(){
		return maxHp;
	}

	public final float getSpeed() {
		return speed;
	}

	public void move(float x, float y){
		if(x < 0) {
			flipX = true;
		}else {
			flipX = false;
		}
		this.x += x;
		this.y += y;
		Tile current = getTile();
		if(current != tilePos) {
			changeTile(current);
		}
	}

	public void moveTo(float x, float y) {
		this.x = x;
		this.y = y;
		Tile current = getTile();
		if(current != tilePos) {
			changeTile(current);
		}
	}

	public void customUpdate(GameContainer gc, StateBasedGame game, int delta){
		ai.update(gc, game, delta);
	}

	public void pickItem(MovableObject item) {
		getTile().itemPicked(item, this);
	}

	public void giveItem(MovableObject item) {
		itemInHands = item;
	}

	public void dropItem() {
		itemInHands.setLocation(x, y);
		getTile().addLoot(itemInHands);
		itemInHands = null;
	}

	public static abstract class Builder extends MovableObject.Builder{
		protected float speed = 0.1f;
		protected int str = 0; //����
		protected int intel = 0; //��
		protected int dex = 0; //��������
		protected int sex = 0; //yes
		protected int bodyType = 0;
		
		public Builder(String name) { super(name); }
		public Builder speed(float speed) { this.speed = speed; return this; }
		public Builder str(int str) { this.str = str; return this; }
		public Builder intel(int intel) { this.intel = intel; return this; }
		public Builder dex(int dex) { this.dex = dex; return this; }
		public Builder sex(int sex) { this.sex = sex; return this; }
		public Builder bodyType(int bodyType) { this.bodyType = bodyType; return this; }
		
		public Builder() { super(""); }
		public float speed() { return speed; }
		public int str() { return str; }
		public int intel() { return intel; }
		public int dex() { return dex; }
		public int sex() { return sex; }
		public int bodyType() { return bodyType; }
	}

}
