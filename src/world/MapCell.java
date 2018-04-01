package world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;
import util.SimplexNoise;

public class MapCell {

	DiagramMap map;
	public PolygonSimple poly;
	public Site site;
	List<Corner> corners = new ArrayList<Corner>();
	List<Edge> edges = new ArrayList<Edge>();

	public int biome = 3;

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
				next.parents().add(this);
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
			if(c.noise() <= -98) {
				double cE = map.getElevation(c.x(), c.y());
				double b = map.getBiome(c.x(),c.y());
				
				if(b == 1) {
					e = 0;
				}else if(b == 2) {
					e = 0.3 + e/20.;
				}else if(b == 3) {
					e = 0.3 + e/20.;
				}

				c.setNoise(cE);
			}
			e += c.noise();
		}

		e /= (double)corners.size();

		if(e <= 0.3) {
			biome = 3;
		}else if(e <= 0.8){
			biome = 1;
		}else {
			biome = 2;
		}
	}

}
