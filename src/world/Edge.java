package world;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Vector2f;

public class Edge {
	private static Line line = new Line(0,0,0,0);
	private static Vector2f v = new Vector2f(0,0);
	
	private Corner p1;
	private Corner p2;
	private double water = 0;
	
	public Edge(Corner p1, Corner p2) {
		this.p1 = p1;
		this.p2 = p2;
		p1.connections().add(this);
		p2.connections().add(this);
	}
	
	public boolean equals(Edge e) {
		return (e.p1 == p1 && e.p2 == p2) || (e.p1 == p2 && e.p2 == p1);
	}
	
	public Corner otherEnd(Corner c) {
		if(c.equals(p1)) {
			return p2;
		}else if(c.equals(p2)) {
			return p1;
		}else {
			return null;
		}
	}
	
	public double distance(double x, double y) {
		line.set(p1.x(), p1.y(), p2.x(), p2.y());
		v.set((float)x,(float)y);
		return line.distanceSquared(v);
	}
	
	public Corner p1() {
		return p1;
	}
	
	public Corner p2() {
		return p2;
	}
	
	public double water() {
		return water;
	}
	
	public void addWater(double amount) {
		water += amount;
	}
	
}
