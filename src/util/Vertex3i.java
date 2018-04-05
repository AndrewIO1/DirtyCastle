package util;

import world.WorldMap;

public class Vertex3i {
	private int x;
	private int y;
	private int z;
	private float cost;
	
	public Vertex3i(int x, int y, int z, int cost){
		this.x = x;
		this.y = y;
		this.z = z;
		this.cost = cost;
	}
	
	public Vertex3i(int x, int y, int z){
		this(x,y,z,0);
	}
	
	public Vertex3i(Vertex3i v){
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.cost = v.cost;
	}
	
	public float getCost(){
		return cost;
	}
	
	public void setCost(float newCost){
		this.cost = newCost;
	}
	
	public int getX(){
		return x;
	}
	
	public int getTileX() {
		return x/WorldMap.tileSize;
	}
	
	public int getTileY() {
		return y/WorldMap.tileSize;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setlocation(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals(Vertex3i v){
		return x == v.x && y == v.y;
	}
	
	public Vertex3i copy(){
		return new Vertex3i(this);
	}

	public boolean totalEquals(Vertex3i from) {
		return x == from.x && y == from.y && cost == from.cost;
	}
}
