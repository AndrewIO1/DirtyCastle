package world.zones;

import entities.EntityType;
import entities.MovableObject;

public class ItemAllowance {
	private EntityType type;
	private int[] variant;
	
	public ItemAllowance(EntityType log, int[] variant) {
		this.type = log;
		this.variant = variant;
	}
	
	public void modifyRestriction(int[] variant) {
		this.variant = variant;
	}
	
	public boolean isItemAllowed(MovableObject item) {
		if(item.getType() != type) {
			return false;
		}
		
		if(variant == null) {
			return false;
		}
		
		if(variant.length == 0) {
			return true;
		}
		
		for(int i = 0; i < variant.length; i++) {
			if(item.getVariant() == variant[i]) {
				return true;
			}
		}
		
		return false;
	}
}
