package world;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Vector2f;

public class Edge {
	public Corner p1;
	public Corner p2;
	public double water = 0;
	
	public Edge(Corner p1, Corner p2) {
		this.p1 = p1;
		this.p2 = p2;
		p1.connections.add(this);
		p2.connections.add(this);
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
		Line line = new Line(p1.loc.x, p1.loc.y, p2.loc.x, p2.loc.y);
		return line.distanceSquared(new Vector2f((float)x,(float)y));
	}
	
}
