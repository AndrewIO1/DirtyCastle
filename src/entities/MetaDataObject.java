package entities;

import java.util.HashMap;
import java.util.Map;

import world.WorldMap;

/*
 * 
 * ��� ���, ������� �� ���������, ������ ����������� � ������, �� ������ � ��������
 * ���������� ����� �������������� ��� ����������� �� � ������ ������������� � ������
 * ���� ����� ������� �������� � ����� �������, ����������� ���������� �����
 * � ������������ ��������, ���-�� ��������� ����� ������, �������� ����� ������ ����� ����� ���������� ������� 
 * � �������� ���������� �� ����, ���� ����������� ���� ��� ���-������ �����
 * ����� ���� ����� �� �������, ���� �� ����� �� �������� ������������
 * 
 */

public abstract class MetaDataObject extends MovableObject{

	protected Map<String, Integer> metadata;//����������, �� � ������ ���� � ������������
	
	public MetaDataObject(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor,
			int mass, HashMap<String, Integer> metadata, WorldMap map) {
		super(name, x, y, z, width, height, x_anchor, y_anchor, mass, map);
		this.metadata = metadata;
	}
	
	public int getData(String name){//���������� ������ �� �����
		return metadata.get(name);
	}
	
}
