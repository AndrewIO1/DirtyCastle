package util.tasks;

import ai.AI;
import entities.EntityType;
import entities.GameObject;
import entities.static_objects.Tree;
import sound.SoundSystem;
import util.alerts.AlertManager;
import util.path_finding.PathFinderManager;
import world.WorldMap;

public class TaskTreeCut extends Task {
	
	public TaskTreeCut(GameObject target) {
		super(target, Task.TREE_CUT);
		
	}

	@Override
	public void restore() {
		picked = false;
		step = 0;
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
			
			int x = (int)targets.get(0).getX()/WorldMap.tileSize;
			int y = (int)targets.get(0).getY()/WorldMap.tileSize;
			int z = targets.get(0).getZ();
			
			if(WorldMap.getMap().tilePassable(x-1, y, z)) {
				x--;
			}else if(WorldMap.getMap().tilePassable(x+1, y, z)) {
				x++;
			}else if(WorldMap.getMap().tilePassable(x, y-1, z)) {
				y--;
			}else if(WorldMap.getMap().tilePassable(x, y+1, z)) {
				y++;
			}
			
			worker.findPath(x, y, z, 1, 1);
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
			worker.getHost().changeAnimation(2,142);
			if(taskTimer >= taskTime) {
				targets.get(0).damage(1);
				
				SoundSystem.getInstance().playSound("treeCut", targetTile.getX()*WorldMap.tileSize, targetTile.getY()*WorldMap.tileSize, 0);
				
				if(targets.get(0).isDead()) {
					if(targets.get(0).getType() == EntityType.TREE) {
						targets.add(0, ((Tree)targets.get(0)).getHost().getObject());
						taskTimer = 0;
						AlertManager.getInstance().log(worker.getHost().getName() + " cutted tree down!");
						return;
					}
					
					obstacleDestroyed(worker);
					
					AlertManager.getInstance().log(worker.getHost().getName() + " completed tree cutting task!");
					completed = true;
					return;
				}
				taskTimer = 0;
			}
		}
	}

}
