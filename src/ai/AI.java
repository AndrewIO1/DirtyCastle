package ai;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import core.DwarfsGame;
import entities.Creature;
import util.Vertex3i;
import util.path_finding.PathFinderManager;
import util.tasks.Task;
import util.tasks.TaskManager;
import world.WorldMap;

public abstract class AI {

	protected static final Random rand = new Random(System.currentTimeMillis());

	public static final int IDLE_STATE = 0;
	public static final int WALKING_STATE = 1;
	public static final int WORKING_STATE = 2;
	public static final int TALKING_STATE = 3;
	public static final int BUSY_STATE = 4;

	protected static final int IDLE_WAIT = 2000;

	//---------------------------------------------------------


	protected static volatile int calculatingPath = 0;

	protected int targetX;
	protected int targetY;

	protected Creature host;
	protected volatile WorldMap map;
	protected volatile Path path;
	protected volatile boolean waitingForPath;
	protected boolean taskFindCooldown = false;

	protected Task currentTask;

	protected int state = IDLE_STATE;
	protected int progress_timer = 0;

	protected AI targetAI;

	protected int testProfa;

	public AI(){
		testProfa = DwarfsGame.rnd.nextInt(3);
	}

	public final int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public final Path getPath() {
		return path;
	}

	public void setHost(Creature host){
		this.host = host;
		map = host.getMap();
	}

	public final Creature getHost() {
		return host;
	}

	public void setTask(Task task) {
		currentTask = task;
		task.pick();
	}

	public void setPath(Path path) {
		this.path = path;
		waitingForPath = false;
	}

	public abstract void update(GameContainer gc, StateBasedGame game, int delta);

	public void checkTasks() {
		if(!taskFindCooldown) {
			taskFindCooldown = true;
			if(currentTask == null) {
				if(testProfa == 0) {
					TaskManager.getInstance().pickItemSupplyTask(this);
					if(currentTask != null) {
						state = WORKING_STATE;
						return;
					}
					TaskManager.getInstance().pickDroppedItemTask(this);
					if(currentTask != null) {
						state = WORKING_STATE;
						return;
					}
					return;
				}
				if(testProfa == 1) {
					TaskManager.getInstance().pickTreeCutTask(this);
					if(currentTask != null) {
						state = WORKING_STATE;
						return;
					}
					TaskManager.getInstance().pickBuildTask(this);
					if(currentTask != null) {
						state = WORKING_STATE;
						return;
					}
					TaskManager.getInstance().pickTileMineTask(this);
					if(currentTask != null) {
						state = WORKING_STATE;
						return;
					}
					return;
				}
				if(testProfa == 2) {
					TaskManager.getInstance().pickTileMineTask(this);
					if(currentTask != null) {
						state = WORKING_STATE;
						return;
					}
					TaskManager.getInstance().pickTreeCutTask(this);
					if(currentTask != null) {
						state = WORKING_STATE;
						return;
					}
					return;
				}
			}
		}
	}

	public void idle(int delta) {
		host.changeAnimation(0);
		checkTasks();
		if(currentTask != null) return;

		if(progress_timer == 0) {
			//TODO сделать нормально
			//if(lookForTalkPartner()) return;
		}

		if(progress_timer >= IDLE_WAIT + 200*PathFinderManager.getInstance().requestNumber()) {
			int x = (int) (host.getX()/32);
			int y = (int) (host.getY()/32);
			int z = host.getZ();
			if(!waitingForPath) {
				do {
					x = (int) (host.getX()/32 + 6 - rand.nextInt(13));
					y = (int) (host.getY()/32 + 6 - rand.nextInt(13));
				}while(!map.tilePassable(x, y, z) || map.getTile(x, y, z).getFirstTypeTask(Task.WALL_BUILD) != null);
			}
			findPath(x,y,z,1,1);
			state = WALKING_STATE;
			taskFindCooldown = false;
			progress_timer = 0;
			return;
		}
		progress_timer += delta;
	}

	public void talk(int delta) {
		if(waitingForPath) return;
		
		if(progress_timer >= IDLE_WAIT + 200*PathFinderManager.getInstance().requestNumber()) {
			targetAI.setState(IDLE_STATE);
			state = IDLE_STATE;
			targetAI = null;
		}
		progress_timer += delta;
	}

	public AI getTargetAI() {
		return targetAI;
	}

	public boolean lookForTalkPartner() {
		for(int i = 0; i < map.getCreatures().size(); i++) {
			if(map.getCreatures().get(i).getAI() == this) continue;
			if(host.getZ() != map.getCreatures().get(i).getZ()) continue;
			
			if(map.getCreatures().get(i).getAI().getState() == IDLE_STATE) {
				if(Math.abs(map.getCreatures().get(i).getX() - getHostX()) > 256 ||
						Math.abs(map.getCreatures().get(i).getY() - getHostY()) > 256) continue;
				if(DwarfsGame.rnd.nextInt(100) > 98) {
					progress_timer = 0;
					state = WALKING_STATE;
					targetAI = map.getCreatures().get(i).getAI();
					targetAI.setState(BUSY_STATE);
					findPath(targetAI.getHostTileX()+1, targetAI.getHostTileY(), host.getZ(), 1, 1);
					return true;
				}
			}
		}
		return false;
	}

	public void animationCompleted() {

	}

	public void walk(int delta) {
		if(waitingForPath) return;
		host.changeAnimation(1,200);
		followThePath(delta);
		if(path == null || path.size() == 0) {
			progress_timer = 0;
			path = null;
			if(currentTask != null) {
				state = WORKING_STATE;
				return;
			}
			if(targetAI != null) {
				state = TALKING_STATE;
				return;
			}
			state = IDLE_STATE;
		}
	}

	protected void followThePath(int delta){
		if(path == null) return;
		if(path.size() == 0) return;
		Vertex3i target = path.peek();
		float xSpeed = 0;
		float ySpeed = 0;

		if(Math.abs(host.getX() - target.getX()) < host.getSpeed()*delta &&
				Math.abs(host.getY() - target.getY()) < host.getSpeed()*delta){
			if(path.size() == 1) {
				host.moveTo(target.getX(), target.getY());
			}
			target = path.nextPoint();
			return;
		}

		if(Math.abs(host.getX() - target.getX()) >= host.getSpeed()*delta){
			xSpeed = (target.getX() - host.getX())/Math.abs(host.getX() - target.getX())*host.getSpeed();
		}
		if(Math.abs(host.getY() - target.getY()) >= host.getSpeed()*delta){
			ySpeed = (target.getY() - host.getY())/Math.abs(host.getY() - target.getY())*host.getSpeed();
		}

		host.move(xSpeed*delta, ySpeed*delta);
	}

	public final void findPath(int x, int y, int z, int BaseCost, int NoneCost){
		if(waitingForPath || path != null) {
			return;
		}

		Vertex3i from = new Vertex3i((int)host.getX()/32,(int)host.getY()/32, host.getZ(), 0);

		waitingForPath = true;
		PathFinderManager.getInstance().requestPath(x,y,z,BaseCost,NoneCost,from, this);

	}

	public boolean isWaitingForPath() {
		return waitingForPath;
	}

	public void pathFinderFailed() {
		if(currentTask != null) {
			taskFindCooldown = true;
			state = IDLE_STATE;
			progress_timer = 0;
			TaskManager.getInstance().returnTask(currentTask);
			currentTask = null;
		}
		waitingForPath = false;
	}

	public float getHostX() {
		return host.getX();
	}

	public float getHostY() {
		return host.getY();
	}

	public int getHostTileX() {
		return (int) (host.getX()/WorldMap.tileSize);
	}

	public int getHostTileY() {
		return (int) (host.getY()/WorldMap.tileSize);
	}
	
	public int getHostZ() {
		return host.getZ();
	}

}
