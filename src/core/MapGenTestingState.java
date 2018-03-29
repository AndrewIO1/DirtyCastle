package core;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;
import util.SimplexNoise;
import world.Corner;
import world.DiagramMap;
import world.Edge;
import world.MapCell;

public class MapGenTestingState extends ExtendedState{
	private static final boolean edgeRender = false;
	
	int z = 16;
	int seed = 4;
	int rivers = 10;
	float scale = 3;
	int floydRelax = 15;
	int points = 450;
	int sX = 32;
	int sY = 32;

	Color[] bColor = {Color.blue.darker(), Color.green.darker(), Color.darkGray, Color.cyan.darker(), Color.yellow.darker(), Color.black};

	PowerDiagram diagram = new PowerDiagram();

	OpenList sites = new OpenList();
	ArrayList<MapCell> polys = new ArrayList<MapCell>();

	Random rand = new Random(seed);
	Random riverRand = new Random(seed);
	SimplexNoise simplex = new SimplexNoise(seed);

	PolygonSimple rootPolygon = new PolygonSimple();
	int width = 256;
	int height = 256;

	DiagramMap map = new DiagramMap();

	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {

	}

	@Override
	public void customLoad(GameContainer gc, StateBasedGame game) {
		rootPolygon.add(0, 0);
		rootPolygon.add(width, 0);
		rootPolygon.add(width, height);
		rootPolygon.add(0, height);

		for (int i = 0; i < points; i++) {
			Site site = new Site(rand.nextInt(width-16)+8, rand.nextInt(width-16)+8);
			sites.add(site);
		}

		diagram.setSites(sites);

		diagram.setClipPoly(rootPolygon);

		diagram.computeDiagram();

		for(int i = 0; i < floydRelax; i++) {
			OpenList relaxedSites = new OpenList();
			for(int j = 0; j < points; j++) {
				if(sites.array[j] == null) {
					continue;
				}
				PolygonSimple p = sites.array[j].getPolygon();
				if(p == null) {
					continue;
				}
				relaxedSites.add(new Site(p.getCentroid().getX(), p.getCentroid().getY()));
			}

			sites = relaxedSites;

			diagram.setSites(sites);
			diagram.computeDiagram();
		}

		for (int i = 0; i < points; i++) {
			MapCell mC = new MapCell(sites.array[i], map);
			if(sites.array[i] == null) {
				continue;
			}

			polys.add(mC);
		}

		for(MapCell mC : polys) {
			mC.genNoise(simplex);
		}

		for(Corner c : map.getCorners()) {
			c.findLowest();
			c.checkWater();
		}

		for(int i = 0 ; i < rivers; i++) {
			ArrayList<Edge> visitedr = new ArrayList<Edge>();

			Corner end = null;
			Corner river = null;
			while(river == null || river.coast || river.river > 0) {
				river = map.getCorners().get(riverRand.nextInt(map.getCorners().size()));
			}

			boolean fail = false;

			while(!river.coast) {
				if(river.lowest == null || visitedr.contains(river.lowest)) {
					fail = true;
					break;
				}
				river.lowest.water++;
				river.lowest.p1.river++;
				river.lowest.p2.river++;
				visitedr.add(river.lowest);
				river = river.lowest.otherEnd(river);

			}

			end = river;

			if(!end.coast) {
				fail = true;
			}

			if(fail) {
				for(Edge e : visitedr) {
					e.water--;
					e.p1.river--;
					e.p2.river--;
				}
			}

		}

		Site first = null;

		for(int i = 0; i < points; i++) {
			if(sites.array[i] == null) continue;
			if(sites.array[i].getPolygon().getBounds().x <= 1 ||
					sites.array[i].getPolygon().getBounds().y <= 1) {
				first = sites.array[i];
				break;
			}
		}

		if(first == null) return;

		ArrayList<Site> visited = new ArrayList<Site>();
		ArrayList<Site> frontier = new ArrayList<Site>();

		frontier.add(first);

		while(frontier.size() > 0) {
			Site current = frontier.get(0);
			visited.add(current);
			frontier.remove(0);
			for(MapCell mC : polys) {
				if(mC.site == current) {
					if(mC.biome != 3) break;
					mC.biome = 0;
					mC.color = Color.blue.darker();
					for(Site s : current.getNeighbours()) {
						if(visited.contains(s) || frontier.contains(s)) continue;
						frontier.add(s);
					}
					break;
				}
			}
		}

		map.computeTilesNoise(simplex);
		//map.computeTiles();
	}

	@Override
	public void clear(GameContainer gc, StateBasedGame game) {

	}

	@Override
	public void customRender(GameContainer gc, StateBasedGame game, Graphics g) {
		g.translate(sX, sY);
		g.scale(scale, scale);

		/*for(MapCell p : polys) {
			if(p.site == null) continue;
			if(p.poly.getNumPoints() <= 0) continue;

			int x[] = p.poly.getXpointsClosed();
			int y[] = p.poly.getYpointsClosed();

			Polygon shape = new Polygon();
			for(int i = 0; i < x.length; i++) {
				shape.addPoint(x[i], y[i]);
			}

			g.setColor(p.color);
			g.fill(shape);

			//g.setColor(Color.gray);
			//for(int i = 0; i < x.length-1; i++) {
			//	g.drawLine(x[i], y[i], x[i+1], y[i+1]);
			//}
			//g.drawLine(x[x.length-1], y[y.length-1], x[0], y[0]);

		}*/



		for(int i = 0; i < 256; i++) {
			for(int j = 0; j < 256; j++) {
				g.setColor(bColor[map.getTile(i, j, z)]);
				g.fillRect(i, j, 1, 1);
			}
		}

		/*g.setColor(Color.red);
		for(Site s : sites) {
			g.fillRect((float)s.x, (float)s.y, 1, 1);
		}*/

		if(edgeRender) {
			for(Edge e : map.getEdges()) {
				if(e.water == 0) {
					g.setColor(Color.gray);
					g.setLineWidth(1);
				}else {
					g.setColor(Color.blue);
					g.setLineWidth((float) e.water);
				}
				g.drawLine(e.p1.loc.x, e.p1.loc.y, e.p2.loc.x, e.p2.loc.y);
			}
		}
		
		g.setColor(Color.white);
		g.drawString("z: " + z, 300, 20);

		/*for(Corner c : map.corners) {
			if(c.coast) {
				g.setColor(Color.cyan);
			}else {
				g.setColor(Color.yellow);
			}
			g.fillRect(c.loc.x, c.loc.y, 1, 1);
		}*/

	}

	@Override
	public void customUpdate(GameContainer gc, StateBasedGame game, int delta) {
		if(gc.getInput().isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {

			ArrayList<Edge> visited = new ArrayList<Edge>();

			Corner end = null;
			Corner river = null;
			while(river == null || river.coast) {
				river = map.getCorners().get(riverRand.nextInt(map.getCorners().size()));
			}

			boolean fail = false;

			while(!river.coast) {
				if(river.lowest == null || visited.contains(river.lowest)) {
					fail = true;
					break;
				}
				river.lowest.water++;
				visited.add(river.lowest);
				river = river.lowest.otherEnd(river);

			}

			end = river;

			if(!end.coast) {
				fail = true;
			}

			if(fail) {
				for(Edge e : visited) {
					e.water--;
				}
			}
		}
		
		if(gc.getInput().isKeyPressed(Input.KEY_Q)) {
			z--;
		}else if(gc.getInput().isKeyPressed(Input.KEY_E)) {
			z++;
		}
		
		if(z < 0) z = 0;
		else if(z >= map.depth()) z = map.depth()-1;
	}

	@Override
	public int getID() {
		return DwarfsGame.TEST;
	}

}
