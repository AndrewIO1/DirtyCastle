package util.tasks;

import ai.AI;
import util.alerts.AlertManager;
import util.path_finding.PathFinderManager;
import world.Tile;
import world.Tile.TILE_TYPE;
import world.WorldMap;

public class TaskTileMine extends Task {
	
	int progress = 0;
	int progressMax = 3;
	
	public TaskTileMine(Tile target) {
		super(target, Task.TILE_MINE);
		
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
			
			if(WorldMap.getMap().tilePassable(x-1, y, 0)) {
				x--;
			}else if(WorldMap.getMap().tilePassable(x+1, y, 0)) {
				x++;
			}else if(WorldMap.getMap().tilePassable(x, y-1, 0)) {
				y--;
			}else if(WorldMap.getMap().tilePassable(x, y+1, 0)) {
				y++;
			}
			
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
			taskTimer += delta;
			if(taskTimer >= taskTime) {
				progress++;;
				if(progress >= progressMax) {
					targetTile.setWall(TILE_TYPE.NONE);
					
					obstacleDestroyed(worker);
					
					completed = true;
					AlertManager.getInstance().log(worker.getHost().getName() + " mined tile!");
					return;
				}
				taskTimer = 0;
			}
		}
	}

}
