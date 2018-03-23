package util.tasks;

import java.util.ArrayList;
import java.util.HashMap;

import ai.AI;
import entities.EntityType;
import entities.MovableObject;
import world.Tile;
import world.WorldMap;
import world.zones.StorageZone;

public class TaskManager {
	private static volatile TaskManager managerSingleton;

	public static TaskManager getInstance(){
		if(managerSingleton == null) {
			synchronized(TaskManager.class) {
				if(managerSingleton == null) {
					managerSingleton = new TaskManager();
				}
			}
		}
		return managerSingleton;
	}

	private HashMap<String, ArrayList<Task>> tasks;
	private WorldMap map;

	private TaskManager() {
		map = WorldMap.getMap();
		tasks = new HashMap<String, ArrayList<Task>>();
		tasks.put("treeCutting", new ArrayList<Task>());
		tasks.put("tileMining", new ArrayList<Task>());
		tasks.put("itemMoving", new ArrayList<Task>());
		tasks.put("tileBuilding", new ArrayList<Task>());
		tasks.put("itemSupply", new ArrayList<Task>());
	}

	public void addTree(Tile tile) {
		Task treeTask = new TaskTreeCut(tile.getObject());
		tasks.get("treeCutting").add(treeTask);
	}

	public ArrayList<Task> getTrees(){
		return tasks.get("treeCutting");
	}

	public void pickTreeCutTask(AI citizen) {
		if(tasks.get("treeCutting").size() == 0) return;

		int x = citizen.getHostTileX();
		int y = citizen.getHostTileY();
		int i = 0;
		int j = 0;
		int step = 0;

		while(step < map.getWidth()) {
			step++;

			if(map.getTile(x+i, y+j, 0) == null) {
				i-= step;
			}else {
				for(int k = 0; k < step; k++) {
					i--;
					if(checkTreeTask(x+i, y+j, 0, citizen)) return;
				}
			}

			if(map.getTile(x+i, y+j, 0) == null) {
				j-= step;
			}else {
				for(int k = 0; k < step; k++) {
					j--;
					if(checkTreeTask(x+i, y+j, 0, citizen)) return;
				}
			}

			step++;

			if(map.getTile(x+i, y+j, 0) == null) {
				i+= step;
			}else {
				for(int k = 0; k < step; k++) {
					i++;
					if(checkTreeTask(x+i, y+j, 0, citizen)) return;
				}
			}

			if(map.getTile(x+i, y+j, 0) == null) {
				j+= step;
			}else {
				for(int k = 0; k < step; k++) {
					j++;
					if(checkTreeTask(x+i, y+j, 0, citizen)) return;
				}
			}
		}
	}

	protected boolean checkTreeTask(int x, int y, int z, AI citizen) {
		Tile taskTile = map.getTile(x, y, z);
		if(taskTile == null) return false;

		Task treeTask;
		treeTask = taskTile.getFirstTypeTask(Task.TREE_CUT);
		if(treeTask != null && sameGroup(treeTask, citizen) && !treeTask.isPicked()) {
			citizen.setTask(treeTask);
			tasks.get("treeCutting").remove(treeTask);
			taskTile.removeTask(treeTask);
			return true;
		}
		return false;
	}

	public void addTile(Tile tile) {
		Task mineTask = new TaskTileMine(tile);
		mineTask.disable();
		if(map.tilePassable(tile.getX()-1, tile.getY(), 0)) {
			mineTask.enable();
		}else if(map.tilePassable(tile.getX()+1, tile.getY(), 0)) {
			mineTask.enable();
		}else if(map.tilePassable(tile.getX(), tile.getY()-1, 0)) {
			mineTask.enable();
		}else if(map.tilePassable(tile.getX(), tile.getY()+1, 0)) {
			mineTask.enable();
		}
		tasks.get("tileMining").add(mineTask);
	}

	public void pickTileMineTask(AI citizen) {
		for(int i = 0; i < tasks.get("tileMining").size(); i++) {
			Task pickedTask = tasks.get("tileMining").get(i);
			if(pickedTask.isAvailable()) {
				if(!sameGroup(pickedTask, citizen)) continue;
				citizen.setTask(pickedTask);
				tasks.get("tileMining").remove(i);
				return;
			}
		}
	}
	
	private boolean sameGroup(Task task, AI creature) {
		if(task == null) return false;
		if(task.targetTile == null) return false;
		
		int x = task.targetTile.getX();
		int y = task.targetTile.getY();
		
		Tile creatureGroup = creature.getHost().getTile();
		
		if(map.getGroup(x, y, 0) == creatureGroup.getGroup()) {
			return true;
		}
		
		for(int i = x-1; i <= x+1; i++) {
			for(int j = y-1; j <= y+1; j++) {
				if(i == x && j == y) continue;
				if(map.getGroup(i, j, 0) == creatureGroup.getGroup()) {
					return true;
				}
			}
		}
		
		return false;
	}

	public void addDroppedItem(MovableObject item) {
		Task droppedItemTask = new TaskDroppedItem(item);
		tasks.get("itemMoving").add(droppedItemTask);
	}

	public void pickDroppedItemTask(AI citizen) {
		for(int i = 0; i < tasks.get("itemMoving").size(); i++) {
			if(!sameGroup(tasks.get("itemMoving").get(i), citizen)) continue;
			MovableObject item = (MovableObject) tasks.get("itemMoving").get(i).getTargets().get(0);
			StorageZone storage = WorldMap.getZoneManager().findStorage(item);
			if(storage != null) {
				Tile target = storage.pickFreeTile(item);
				if(target != null) {
					((TaskDroppedItem)tasks.get("itemMoving").get(i)).setDropTile(target);
					citizen.setTask(tasks.get("itemMoving").get(i));
					tasks.get("itemMoving").remove(i);
					return;
				}
			}
		}
	}

	public void addWallBuildingSpot(Tile tile, int wallId) {
		BuildingTemplate template = BuildingTemplate.getBuilding(wallId);
		Task wallBuilding = new TaskWallBuild(tile, template.getItemsNeeded(), template.getBuildingType());
		tasks.get("tileBuilding").add(wallBuilding);
	}

	public void addSupplyTask(TaskConsumeItems target) {
		tasks.get("itemSupply").add(new TaskSupplyItems(target));
	}

	public void pickBuildTask(AI citizen) {
		for(int i = 0; i < tasks.get("tileBuilding").size(); i++) {
			TaskConsumeItems task = (TaskConsumeItems) tasks.get("tileBuilding").get(i);
			if(!sameGroup(task, citizen)) continue;
			if(task.areItemsReady()) {
				citizen.setTask(task);
				tasks.get("tileBuilding").remove(i);
				return;
			}
		}
	}

	public void pickItemSupplyTask(AI citizen) {
		for(int i = 0; i < tasks.get("itemSupply").size(); i++) {
			Task supply = ((TaskSupplyItems) tasks.get("itemSupply").get(i)).getItemCarryTask();
			if(!sameGroup(supply, citizen)) continue;
			if(supply != null) {
				citizen.setTask(supply);
				return;
			}
		}
	}

	public Task findFreeItem(EntityType type, int variant, TaskConsumeItems supply) {
		for(int i = 0; i < tasks.get("itemMoving").size(); i++) {
			MovableObject item = (MovableObject) tasks.get("itemMoving").get(i).getTargets().get(0);

			if(item.getType() != type) continue;

			if(variant == -1 || variant == item.getVariant()) {
				Task itemTask = tasks.get("itemMoving").get(i);
				supply.carryingItem((MovableObject) itemTask.getTargets().get(0));
				tasks.get("itemMoving").remove(i);
				return itemTask;
			}
		}
		
		return null;
	}

	public synchronized void returnTask(Task task) {
		if(task.getType() == Task.TREE_CUT) {
			task.restore();
			tasks.get("treeCutting").add(task);
			task.targetTile.addTask(task);
		}else if(task.getType() == Task.TILE_MINE){
			task.restore();
			tasks.get("tileMining").add(task);
		}
	}

}
