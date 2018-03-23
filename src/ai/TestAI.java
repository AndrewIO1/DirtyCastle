package ai;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

public class TestAI extends AI{

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) {
		if(state == BUSY_STATE) return;
		
		if(state == WORKING_STATE) {
			currentTask.executeStep(this, delta);
			if(currentTask.isCompleted()) {
				currentTask.kill();
				currentTask = null;
				taskFindCooldown = false;
				checkTasks();
				if(currentTask == null) {
					state = IDLE_STATE;
				}
				return;
			}
		}else if(state == IDLE_STATE){
			idle(delta);
		}else if(state == WALKING_STATE) {
			walk(delta);
		}else if(state == TALKING_STATE) {
			talk(delta);
		}
		
		
	}

}
