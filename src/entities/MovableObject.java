package entities;

import world.WorldMap;

/*
 * 
 * Это объект, который может передвигаться по карте, либо его могут таскать
 * напрямую отсюда ничего не наследуется, это просто общий класс для переносимых объектов разного типа
 * В будущем возможно отсюда унаследуются всякие ящики и бочки
 * 
 */

public abstract class MovableObject extends GameObject{

	protected int mass;//Масса объекта
	
	public MovableObject(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor, int mass, WorldMap map) {
		super(name, x, y, z, width, height, x_anchor, y_anchor, map);
		this.mass = mass;
	}
	
	public MovableObject(Builder builder) {
		super(builder);
		this.mass = builder.mass;
	}
	
	public int getMass(){//Возвращает массу объекта
		return mass;
	}
	
	public static abstract class Builder extends GameObject.Builder {
		private int mass;//Масса объекта
		
		public Builder(String name) { super(name); }
		public Builder mass(int mass) { this.mass = mass; return this; }
	}

}
