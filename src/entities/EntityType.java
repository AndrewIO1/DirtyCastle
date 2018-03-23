package entities;

public enum EntityType {
	TREE(0),
	LOG_BIG(1),
	LOG(2),
	TEST_CREATURE(3);
	
	private int type;
	
	EntityType(int type){
		this.type = type;
	}
	
	public int type() { return type; };
	
}
