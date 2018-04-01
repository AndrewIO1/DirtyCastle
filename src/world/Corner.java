package world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Corner {
	private List<MapCell> parents = new ArrayList<MapCell>();
	private List<Edge> connections = new ArrayList<Edge>();
	private Edge lowest;
	private Point loc;
	private boolean coast = false;
	private int river = 0;
	private double noise = -99;
	
	public Corner(Point loc) {
		this.loc = loc;
	}
	
	public void addSite(MapCell site) {
		if(parents.contains(site)) return;
		parents.add(site);
	}
	
	public void addEdge(Edge edge) {
		if(connections.contains(edge)) return;
		connections.add(edge);
	}
	
	public boolean equals(Corner c) {
		if(c == this) return true;
		return c.x() == x() && c.y() == y();
	}
	
	public void findLowest() {
		double noise = 99;
		for(Edge e : connections) {
			if(e.otherEnd(this).noise < noise) {
				lowest = e;
				noise = e.otherEnd(this).noise;
			}
		}
	}
	
	public void checkWater() {
		if(noise <= 0.3) {
			coast = true; 
			return;
		}
	}
	
	public int river() {
		return river;
	}
	
	public void addRiver() {
		river++;
	}
	
	public void removeRiver() {
		river--;
	}
	
	public List<MapCell> parents(){
		return parents;
	}
	
	public List<Edge> connections(){
		return connections;
	}
	
	public Edge lowest() {
		return lowest;
	}
	
	public int x() {
		return loc.x;
	}
	
	public int y() {
		return loc.y;
	}
	
	public boolean coast() {
		return coast;
	}
	
	public double noise() {
		return noise;
	}
	
	public void setNoise(double noise) {
		this.noise = noise;
	}
	
}
