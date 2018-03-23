package util.tasks;

import java.util.ArrayList;

import ai.AI;
import util.alerts.AlertManager;
import util.path_finding.PathFinderManager;
import world.Tile;
import world.Tile.TILE_TYPE;
import world.WorldMap;

public class TaskWallBuild extends TaskConsumeItems {

	private int progress = 0;
	private int progressMax = 3;

	private TILE_TYPE wallType;

	public TaskWallBuild(Tile target, ArrayList<BuildingItemRequirement> items, TILE_TYPE toBuild) {
		super(target, items, Task.WALL_BUILD);
		wallType = toBuild;
	}

	@Override
	public void restore() {
		step = 0;
		picked = false;
	}

	@Override
	public void executeStep(AI worker, int delta) {
		//System.out.println("Executing task: " + task_id);
		if(completed) return;

		if(step == 0) {
			if(worker.isWaitingForPath()){
				PathFinderManager.getInstance().cancelRequest(worker);
			}
			if(worker.getPath() != null) {
				worker.setPath(null);
			}

			int x = targetTile.getX();
			int y = targetTile.getY();

			WorldMap map = WorldMap.getMap();
			
			Tile[] tilesToWalk = new Tile[4];
			tilesToWalk[0] = map.getTile(x-1, y, 0);
			tilesToWalk[1] = map.getTile(x+1, y, 0);
			tilesToWalk[2] = map.getTile(x, y-1, 0);
			tilesToWalk[3] = map.getTile(x, y+1, 0);
			
			int maxPriority = 0;
			int c = -1;

			for(int i = 0; i < tilesToWalk.length; i++) {
				int priority = 10;
				if(!tilesToWalk[i].tilePassable()) priority -= 500;
				if(tilesToWalk[i].getFirstTypeTask(Task.WALL_BUILD) != null) {
					priority -= 2;
					if(tilesToWalk[i].getFirstTypeTask(Task.WALL_BUILD).isPicked()) {
						priority -= 500;
					}
				}
				if(priority > maxPriority) {
					maxPriority = priority;
					c = i;
				}
			}
			if(c == -1) return;
			
			x = tilesToWalk[c].getX();
			y = tilesToWalk[c].getY();

			worker.findPath(x, y, 1, 1);
			taskTimer = 0;
			taskTime = defaultTaskTime;
			step++;
		}else if(step == 1) {
			if(worker.isWaitingForPath()) {
				return;
			}
			step++;
			worker.setState(AI.WALKING_STATE);
		}else if(step == 2) {
			if(targetTile.containsCreature() || targetTile.containsPath()) return;
			taskTimer += delta;
			if(taskTimer >= taskTime) {
				progress++;
				if(progress >= progressMax) {
					targetTile.setWall(wallType);
					targetTile.consumeItems();
					WorldMap.getMap().getMiniMap().updateMiniMap(targetTile.getX(), targetTile.getY(), 1, 1);
					obstacleConstructed(worker);

					completed = true;
					AlertManager.getInstance().log(worker.getHost().getName() + " built a wall!");
					return;
				}
				taskTimer = 0;
			}
		}
	}

	@Override
	public boolean areItemsReady() {
		int itemAmount = 0;
		for(int i = 0; i < itemsNeeded.size(); i++) {
			itemAmount += itemsNeeded.get(i).getAmount();
		}
		return itemAmount == targetTile.getItemAmount();
	}

}
