package util.tasks;

import ai.AI;
import entities.MovableObject;
import util.alerts.AlertManager;
import util.path_finding.PathFinderManager;
import world.Tile;
import world.WorldMap;
import world.zones.StorageZone;

public class TaskDroppedItem extends Task{

	protected Tile dropTile = null;

	public TaskDroppedItem(MovableObject target) {
		super(target, Task.ITEM_DROPPED);
		
	}

	@Override
	public void restore() {
		setTargetTile(targets.get(0));
		step = 0;
	}

	public void setDropTile(Tile dropTile) {
		this.dropTile = dropTile;
		dropTile.addTask(this);
		StorageZone storage = (StorageZone)dropTile.getZone();
		if(storage != null) {
			storage.occupyOne();
		}
	}

	public void clearDropTile() {
		StorageZone storage = (StorageZone)dropTile.getZone();
		if(storage != null) {
			storage.freeOne();
		}
		dropTile.removeTask(this);
		dropTile = null;
	}

	@Override
	public void executeStep(AI worker, int delta) {
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
			worker.getHost().pickItem((MovableObject) targets.get(0));

			if(worker.getPath() != null) {
				worker.setPath(null);
			}

			int x = dropTile.getX();
			int y = dropTile.getY();
			int z = dropTile.getZ();

			worker.findPath(x, y, z, 1, 1);
			taskTimer = 0;
			taskTime = defaultTaskTime;
			step++;
		}else if(step == 3) {
			if(worker.isWaitingForPath()) {
				return;
			}
			step++;
			worker.setState(AI.WALKING_STATE);
		}else if(step == 4) {
			worker.getHost().dropItem();
			dropTile.removeTask(this);
			completed = true;
			AlertManager.getInstance().log(worker.getHost().getName() + " completed item carry task!");
			return;
		}
	}

}
