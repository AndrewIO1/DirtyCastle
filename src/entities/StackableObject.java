package entities;

import world.WorldMap;

/*
 * 
 * �� ��� ������ ������� ���, ������� ����� ������� � ����� �����
 * ������ ����� ������������� ������ �����, �����
 * 
 */

public abstract class StackableObject extends MovableObject{

	protected int amount;//���-��
	protected int stackSize;//������������ ���-�� � �����
	
	public StackableObject(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor,
			int mass, int amount, int stackSize, WorldMap map) {
		super(name, x, y, z, width, height, x_anchor, y_anchor, mass, map);
		this.amount = amount;
		this.stackSize = stackSize;
	}
	
	public final int getAmount(){//���������� ���-��
		return amount;
	}
	
	public final int getStackSize(){//���������� ������ �����
		return stackSize;
	}
	
	public final int getMass(){//���������� ����� ���� ��������
		return mass*amount;
	}

}
