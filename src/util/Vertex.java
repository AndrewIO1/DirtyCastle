package util;

import world.WorldMap;

public class Vertex {
	private int x;
	private int y;
	private float cost;
	
	public Vertex(int x, int y, int cost){
		this.x = x;
		this.y = y;
		this.cost = cost;
	}
	
	public Vertex(Vertex v){
		this.x = v.x;
		this.y = v.y;
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
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setlocation(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Vertex v){
		return x == v.x && y == v.y;
	}
	
	public Vertex copy(){
		return new Vertex(this);
	}

	public boolean totalEquals(Vertex from) {
		return x == from.x && y == from.y && cost == from.cost;
	}
}
