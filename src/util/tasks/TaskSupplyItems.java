package util.tasks;

import java.util.ArrayList;

import ai.AI;
import entities.GameObject;

public class TaskSupplyItems extends Task{

	private TaskConsumeItems taskToSupply;

	public TaskSupplyItems(TaskConsumeItems taskToSupply) {
		super(new ArrayList<GameObject>(0), Task.ITEM_SUPPLY);
		this.taskToSupply = taskToSupply;
	}

	public TaskDroppedItem getItemCarryTask() {
		TaskDroppedItem itemCarry = taskToSupply.getItemCarryTask();
		if(itemCarry != null) {
			itemCarry.setDropTile(taskToSupply.targetTile);
		}
		return itemCarry;
	}

	public boolean isSupplied() {
		return taskToSupply.isSupplied();
	}

	@Override
	public void restore() {
		
	}

	@Override
	public void executeStep(AI worker, int delta) {
		
	}

}
