package util.tasks;

import entities.EntityType;

public class BuildingItemRequirement {
	private EntityType type;
	private int variant;
	private int amount;
	private int amountReady;
	
	public BuildingItemRequirement(EntityType log, int variant, int amount) {
		this.type = log;
		this.variant = variant;
		this.amount = amount;
		amountReady = 0;
	}
	
	public EntityType getType() {
		return type;
	}
	
	public int getVariant() {
		return variant;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int getAmountReady() {
		return amountReady;
	}
	
	public boolean itemMatches(EntityType type, int variant) {
		if(this.type != type) return false;
		if(this.variant == -1) return true;
		if(amountReady >= amount) return false;
		return this.variant == variant;
	}
	
	public void itemCarrying() {
		amountReady++;
	}
}
