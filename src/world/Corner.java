package world;

import java.awt.Point;
import java.util.ArrayList;

public class Corner {
	ArrayList<MapCell> parents = new ArrayList<MapCell>();
	public ArrayList<Edge> connections = new ArrayList<Edge>();
	public Edge lowest;
	public Point loc;
	public boolean coast = false;
	public int river = 0;
	public double noise = -99;
	
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
		return c.loc.x == loc.x && c.loc.y == loc.y;
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
		/*for(MapCell mC : parents) {
			if(mC.biome == 0 || mC.biome == 3) {
				coast = true;
				return;
			}
		}*/
	}
	
}
