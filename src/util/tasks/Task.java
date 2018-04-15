package util.tasks;

import java.util.ArrayList;
import java.util.List;

import ai.AI;
import entities.GameObject;
import util.Vertex3i;
import world.Tile;
import world.WorldMap;
import world.tile_groups.TileGroup;

public abstract class Task {
	public static final int TREE_CUT = 0;
	public static final int TILE_MINE = 1;
	public static final int ITEM_DROPPED = 2;
	public static final int WALL_BUILD = 3;
	public static final int ITEM_SUPPLY = 4;
	public static int id = 0;

	protected int step = 0;
	protected List<GameObject> targets;
	protected List<Vertex3i> vertexes;
	protected int type = -1;
	protected Tile targetTile;
	protected int taskTimer;
	protected int taskTime;
	protected boolean completed = false;
	protected boolean picked = false;
	protected boolean available = true;
	protected int task_id;

	protected final int defaultTaskTime = 1000;

	public Task(ArrayList<GameObject> targets, ArrayList<Vertex3i> targets2, int type) {
		init(targets, targets2, type);

	}

	public Task(ArrayList<GameObject> targets, int type) {
		this(targets, new ArrayList<Vertex3i>(0), type);
	}

	public Task(GameObject target, int type) {
		List<GameObject> newTargets = new ArrayList<GameObject>();
		newTargets.add(target);
		init(newTargets, new ArrayList<Vertex3i>(0), type);
	}

	public Task(Tile target, int type) {
		init(new ArrayList<GameObject>(0), new ArrayList<Vertex3i>(0), type);
		setTargetTile(target);
	}

	protected void init(List<GameObject> targets, ArrayList<Vertex3i> targets2, int type) {
		this.targets = targets;
		this.type = type;
		if(targets.size() > 0) {
			setTargetTile(targets.get(targets.size()-1));
		}
		for(int i = 0; i < targets.size(); i++) {
			targets.get(i).addTask(this);
		}
		vertexes = targets2;
		task_id = id;
		id++;
	}
	
	protected void obstacleDestroyed(AI worker) {
		int x = targetTile.getX();
		int y = targetTile.getY();
		int z = targetTile.getZ();
		
		Task enableTile = WorldMap.getMap().getFirstTypeTask(x-1, y, z, TILE_MINE);
		if(WorldMap.getMap().getGroup(x-1, y, z) == -2) {
			WorldMap.getMap().getTile(x-1, y, z).setGroup(-1);
		}
		if(enableTile != null) {
			enableTile.enable();
		}
		
		enableTile = WorldMap.getMap().getFirstTypeTask(x+1, y, z, TILE_MINE);
		if(WorldMap.getMap().getGroup(x+1, y, z) == -2) {
			WorldMap.getMap().getTile(x+1, y, z).setGroup(-1);
		}
		if(enableTile != null) {
			enableTile.enable();
		}
		
		enableTile = WorldMap.getMap().getFirstTypeTask(x, y-1, z, TILE_MINE);
		if(WorldMap.getMap().getGroup(x, y-1, z) == -2) {
			WorldMap.getMap().getTile(x, y-1, z).setGroup(-1);
		}
		if(enableTile != null) {
			enableTile.enable();
		}
		
		enableTile = WorldMap.getMap().getFirstTypeTask(x, y+1, z, TILE_MINE);
		if(WorldMap.getMap().getGroup(x, y+1, z) == -2) {
			WorldMap.getMap().getTile(x, y+1, z).setGroup(-1);
		}
		if(enableTile != null) {
			enableTile.enable();
		}
		
		int workerX = worker.getHostTileX();
		int workerY = worker.getHostTileY();
		int workerZ = worker.getHostZ();
		
		targetTile.setGroup(WorldMap.getMap().getGroupObject(workerX, workerY, workerZ));
		
		ArrayList<TileGroup> connectedGroups = new ArrayList<TileGroup>();
		
		connectedGroups.add(WorldMap.getMap().getGroupObject(workerX, workerY, workerZ));
		
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				if(Math.abs(workerX-(x+i)) + Math.abs(workerY-(y+j)) <= 1) continue;
				if(i == 0 && j == 0) continue;
				if(WorldMap.getMap().getGroup(x+i, y+j, z) < 0) continue;
				if(connectedGroups.contains(WorldMap.getMap().getGroupObject(x+i, y+j, z))) continue;
				connectedGroups.add(WorldMap.getMap().getGroupObject(x+i, y+j, z));
			}
		}
		
		if(connectedGroups.size() > 1) {
			WorldMap.getGroupManager().combine(connectedGroups);
		}
	}
	
	protected void obstacleConstructed(AI worker) {
		//TODO ��������� ����
	}
	
	public boolean isAvailable() {
		return available;
	}

	public void enable() {
		available = true;
	}

	public void disable() {
		available = false;
	}
	
	protected void setTargetTile(Tile target) {
		targetTile = target;
		targetTile.addTask(this);
	}
	
	protected void setTargetTile(GameObject target) {
		Tile newTarget = WorldMap.getMap().getTile((int)target.getX()/WorldMap.tileSize, 
												   (int)target.getY()/WorldMap.tileSize, 
												   target.getZ());
		setTargetTile(newTarget);
	}

	public final boolean isCompleted() {
		return completed;
	}

	public final boolean isPicked() {
		return picked;
	}

	public final void pick() {
		if(picked) {
			System.out.println("Task " + task_id + " type: " + type + " picked by 2 people");
		}
		picked = true;
	}

	public final void nextStep() {
		step++;
	}

	public abstract void restore();
	public abstract void executeStep(AI worker, int delta);

	public final int getType() {
		return type;
	}

	public final List<GameObject> getTargets(){
		return targets;
	}

	public void kill() {
		if(targetTile != null) {
			targetTile.removeTask(this);
		}
		for(int i = 0; i < targets.size(); i++) {
			targets.get(i).removeTask(this);
		}
	}
}
