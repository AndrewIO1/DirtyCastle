package entities;

import world.WorldMap;

/*
 * 
 * Ну это просто обычный лут, который можно стакать в одном тайле
 * отсюда будут наследоваться всякие брёвна, камни
 * 
 */

public abstract class StackableObject extends MovableObject{

	protected int amount;//кол-во
	protected int stackSize;//Максимальное кол-во в стаке
	
	public StackableObject(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor,
			int mass, int amount, int stackSize, WorldMap map) {
		super(name, x, y, z, width, height, x_anchor, y_anchor, mass, map);
		this.amount = amount;
		this.stackSize = stackSize;
	}
	
	public final int getAmount(){//Возвращает кол-во
		return amount;
	}
	
	public final int getStackSize(){//Возвращает размер стака
		return stackSize;
	}
	
	public final int getMass(){//Возвращает массу всех объектов
		return mass*amount;
	}

}
