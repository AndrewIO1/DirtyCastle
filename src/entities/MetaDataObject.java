package entities;

import java.util.HashMap;
import java.util.Map;

import world.WorldMap;

/*
 * 
 * Это лут, который не стакается, всякие инструменты и прочее, их отсюда и наследуй
 * Метаданные будут использоваться для обозначения хп и прочих характеристик у шмоток
 * Надо будет сделать табличку с этими статами, заполняться метаданные будут
 * в констуркторе объектов, что-то прилепить потом нельзя, возможно потом только можно будет зачаровать предмет 
 * и добавить метаданные об этом, типа увеличенный урон или что-нибудь такое
 * Лучше этот класс не трогать, пока не дойдём до создания инструментов
 * 
 */

public abstract class MetaDataObject extends MovableObject{

	protected Map<String, Integer> metadata;//Метаданные, хп и всякая дичь у инструментов
	
	public MetaDataObject(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor,
			int mass, HashMap<String, Integer> metadata, WorldMap map) {
		super(name, x, y, z, width, height, x_anchor, y_anchor, mass, map);
		this.metadata = metadata;
	}
	
	public int getData(String name){//Возвращает данные по имени
		return metadata.get(name);
	}
	
}
