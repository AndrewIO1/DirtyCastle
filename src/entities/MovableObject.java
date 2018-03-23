package entities;

import world.WorldMap;

/*
 * 
 * ��� ������, ������� ����� ������������� �� �����, ���� ��� ����� �������
 * �������� ������ ������ �� �����������, ��� ������ ����� ����� ��� ����������� �������� ������� ����
 * � ������� �������� ������ ������������ ������ ����� � �����
 * 
 */

public abstract class MovableObject extends GameObject{

	protected int mass;//����� �������
	
	public MovableObject(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor, int mass, WorldMap map) {
		super(name, x, y, z, width, height, x_anchor, y_anchor, map);
		this.mass = mass;
	}
	
	public MovableObject(Builder builder) {
		super(builder);
		this.mass = builder.mass;
	}
	
	public int getMass(){//���������� ����� �������
		return mass;
	}
	
	public static abstract class Builder extends GameObject.Builder {
		private int mass;//����� �������
		
		public Builder(String name) { super(name); }
		public Builder mass(int mass) { this.mass = mass; return this; }
	}

}
