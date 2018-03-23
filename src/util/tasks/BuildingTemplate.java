package util.tasks;

import java.util.ArrayList;

import entities.EntityType;
import world.Tile.TILE_TYPE;

public class BuildingTemplate {
	public static int LOG_WALL = 0;
	
	private TILE_TYPE buildingType;
	private ArrayList<BuildingItemRequirement> items;
	
	public BuildingTemplate(TILE_TYPE buildingType, ArrayList<BuildingItemRequirement> items) {
		this.buildingType = buildingType;
		this.items = items;
	}
	
	public TILE_TYPE getBuildingType() {
		return buildingType;
	}
	
	public ArrayList<BuildingItemRequirement> getItemsNeeded(){
		return items;
	}
	
	public static BuildingTemplate getBuilding(int id) {
		BuildingTemplate building = null;
		ArrayList<BuildingItemRequirement> itemsNeeded = new ArrayList<BuildingItemRequirement>();
		if(id == LOG_WALL) {
			itemsNeeded.add(new BuildingItemRequirement(EntityType.LOG, -1, 3));
			building = new BuildingTemplate(TILE_TYPE.ROCK, itemsNeeded);
		}
		return building;
	}
}
