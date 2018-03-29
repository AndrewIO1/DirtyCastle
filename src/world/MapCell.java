package world;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Color;

import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;
import util.SimplexNoise;

public class MapCell {

	DiagramMap map;
	public PolygonSimple poly;
	public Site site;
	ArrayList<Corner> corners = new ArrayList<Corner>();
	ArrayList<Edge> edges = new ArrayList<Edge>();

	public int biome = 3;
	public Color color = Color.cyan.darker();

	public MapCell(Site s, DiagramMap map) {
		this.map = map;
		map.getCells().add(this);
		site = s;
		if(s != null) {
			poly = s.getPolygon();
		}
		Corner last = null;
		if(poly == null) {
			return;
		}
		for(int i = 0; i <= poly.getNumPoints(); i++) {
			Corner next;
			if(i == poly.getNumPoints()) {
				next = corners.get(0);
			}else {
				next = new Corner(new Point(poly.getXpointsClosed()[i], poly.getYpointsClosed()[i]));
			}
			
			Edge e = null;

			boolean found = false;
			for(int j = 0; j < map.getCorners().size(); j++) {
				if(map.getCorners().get(j).equals(next)) {
					next = map.getCorners().get(j);
					found = true;
					break;
				}
			}
			if(!found) {
				map.getCorners().add(next);
			}

			if(last != null) {
				e = new Edge(last, next);

				found = false;
				for(int j = 0; j < map.getEdges().size(); j++) {
					if(map.getEdges().get(j).equals(e)) {
						e = map.getEdges().get(j);
						found = true;
						break;
					}
				}
				if(!found) {
					map.getEdges().add(e);
				}
			}

			if(!corners.contains(next)) {
				corners.add(next);
				next.parents.add(this);
			}
			if(!edges.contains(e)) {
				edges.add(e);
			}
			
			last = next;
		}
		
	}

	public void genNoise(SimplexNoise simplex) {
		double e = 0;
		for(Corner c : corners) {
			if(c.noise <= -98) {
				double cE = simplex.generateSimplexNoise(c.loc.x/100., c.loc.y/100.)+1;
				cE -= (simplex.generateSimplexNoise(c.loc.x/25., c.loc.y/25.)+1)*0.3;

				double cX = 128;
				double cY = 128;
				double dist = Math.sqrt((c.loc.x-cX)*(c.loc.x-cX) + (c.loc.y-cY)*(c.loc.y-cY))/128.;
				double t1 = Math.max(0, 0.95-1.1*Math.pow(dist, 1.9));
				cE *= t1;

				c.noise = cE;
			}
			e += c.noise;
		}

		e /= (double)corners.size();

		if(e <= 0.3) {
			biome = 3;
			color = Color.cyan.darker();
		}else if(e <= 0.8){
			biome = 1;
			color = Color.green.darker();
		}else {
			biome = 2;
			color = Color.gray.darker();
		}
	}

	public Edge getClosestEdge(double x, double y) {
		double dist = 999999;
		Edge close = null;
		for(Edge e: map.getEdges()) {
			if(e == null || e.water == 0) {
				continue;
			}
			double d = e.distance(x, y);
			d /= 3.;
			if(d < dist) {
				close = e;
				dist = d;
			}
		}
		
		return close;
	}

}
