package util.tasks;

import java.util.List;

import entities.EntityType;
import entities.MovableObject;
import world.Tile;
import world.WorldMap;
import world.zones.StorageZone;

public abstract class TaskConsumeItems extends Task{
	protected List<BuildingItemRequirement> itemsNeeded;
	
	public TaskConsumeItems(Tile target, List<BuildingItemRequirement> items, int type) {
		super(target, type);
		
		itemsNeeded = items;
		
		TaskManager.getInstance().addSupplyTask(this);
	}
	
	public abstract boolean areItemsReady();
	
	public boolean isSupplied() {
		for(int i = 0; i < itemsNeeded.size(); i++) {
			if(itemsNeeded.get(i).getAmountReady() < itemsNeeded.get(i).getAmount()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void carryingItem(MovableObject item) {
		for(int i = 0; i < itemsNeeded.size(); i++) {
			if(itemsNeeded.get(i).itemMatches(item.getType(), item.getVariant())) {
				itemsNeeded.get(i).itemCarrying();
				return;
			}
		}
	}
	
	public boolean itemNeeded(MovableObject item) {
		for(int i = 0; i < itemsNeeded.size(); i++) {
			if(itemsNeeded.get(i).itemMatches(item.getType(), item.getVariant())) {
				return true;
			}
		}
		return false;
	}
	
	public TaskDroppedItem getItemCarryTask() {
		for(int i = 0; i < itemsNeeded.size(); i++) {
			if(itemsNeeded.get(i).getAmountReady() >= itemsNeeded.get(i).getAmount()) continue;
			EntityType type = itemsNeeded.get(i).getType();
			int var = itemsNeeded.get(i).getVariant();
			TaskDroppedItem itemCarry = (TaskDroppedItem) TaskManager.getInstance().findFreeItem(type, 
																	var, 
																	this);
			
			if(itemCarry != null) return itemCarry;
			
			List<StorageZone> storages = WorldMap.getZoneManager().getStorages();
			for(int j = 0; j < storages.size(); j++) {
				MovableObject freeItem = storages.get(i).findItem(type, var);
				
				if(freeItem != null) {
					itemCarry = new TaskDroppedItem(freeItem);
					carryingItem(freeItem);
					return itemCarry;
				}
			}
		}
		
		return null;
	}
}
