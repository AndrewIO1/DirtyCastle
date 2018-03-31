package world;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;
import util.SimplexNoise;

public class DiagramMap {
	private double sX = 0;
	private double sY = 0;
	private double scale = 1;
	private int rivers = 10;
	private int floydRelax = 15;
	private int points = 450;
	private int width = 256;
	private int height = 256;
	private int[][][] tileMap = new int[256][256][64];
	private double[][] eMap = new double[256][256];
	private int[][] bMap = new int[256][256];
	private boolean[][] river = new boolean[256][256];
	private ArrayList<MapCell> cells = new ArrayList<MapCell>();
	private ArrayList<Corner> corners = new ArrayList<Corner>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
	private SimplexNoise noise;
	
	public DiagramMap(SimplexNoise noise) {
		this.noise = noise;
	}
	
	public void generate(double centerX, double centerY, double scale) {
		
		sX = centerX;
		sY = centerY;
		this.scale = scale;
		computeDiagram();
		computeTiles();
	}
	
	private void computeDiagram() {
		PowerDiagram diagram = new PowerDiagram();
		OpenList sites = new OpenList();
		
		Random rand = new Random(noise.seed());
		Random riverRand = new Random(noise.seed());
		
		PolygonSimple rootPolygon = new PolygonSimple();
		
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
			new MapCell(sites.array[i], this);
			if(sites.array[i] == null) {
				continue;
			}
		}

		for(MapCell mC : cells) {
			mC.genNoise(noise);
		}

		for(Corner c : getCorners()) {
			c.findLowest();
			c.checkWater();
		}

		for(int i = 0 ; i < rivers; i++) {
			ArrayList<Edge> visitedr = new ArrayList<Edge>();

			Corner end = null;
			Corner river = null;
			while(river == null || river.coast() || river.river() > 0) {
				river = getCorners().get(riverRand.nextInt(getCorners().size()));
			}

			boolean fail = false;

			while(!river.coast()) {
				if(river.lowest() == null || visitedr.contains(river.lowest())) {
					fail = true;
					break;
				}
				river.lowest().addWater(1);
				river.lowest().p1().addRiver();
				river.lowest().p2().addRiver();
				visitedr.add(river.lowest());
				river = river.lowest().otherEnd(river);

			}

			end = river;

			if(!end.coast()) {
				fail = true;
			}

			if(fail) {
				for(Edge e : visitedr) {
					e.addWater(-1);
					e.p1().removeRiver();
					e.p2().removeRiver();
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
			for(MapCell mC : cells) {
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
	}
	
	private void computeTiles() {
		for(int i = 0; i < tileMap.length; i++) {
			for(int j = 0; j < tileMap[0].length; j++) {
				double x = sX + i*scale;
				double y = sY + j*scale;
				
				double e = noise.generateSimplexNoise(x/100., y/100.)+1;
				e -= (noise.generateSimplexNoise(x/25., y/25.)+1)*0.3;
				
				
				
				MapCell parent = null;
				for(MapCell mC : cells) {
					if(mC.poly == null) continue;
					if(mC.poly.contains(x, y)) {
						parent = mC;
						break;
					}
				}
				if(parent != null) {
					Edge edge = getClosestEdge(x,y);
					if(edge != null && edge.water() > 0) {
						
						double eDist = edge.distance(x, y)/* + 2.3*noise.generateSimplexNoise(x/3., y/3.)*/;
						double base = 0.009/* + 0.0018*(noise.generateSimplexNoise(x/1.1, y/1.1))*/;
						double eDistNoise = -1.+edge.water()*0.6 + 2.9*(noise.generateSimplexNoise(x/3.1, y/3.1)+1);
						if(eDistNoise <= 0.75) eDistNoise = 0.75;
						double multi = eDist/eDistNoise;
						
						double rE = Math.pow(base, multi);
						if(rE >= 0.1) {
							river[i][j] = true;
						}
					}
				}
				
				eMap[i][j] = e;
				bMap[i][j] = getBiome(x,y);
			}
		}
		
		for(int i = 0; i < tileMap.length; i++) {
			for(int j = 0; j < tileMap[0].length; j++) {
				
				boolean first = true;
				for(int k = 0; k < tileMap[0][0].length; k++) {
					
					
					double z = (k-32);
					
					double e =  eMap[i][j];
					
					/*if(z <= 0) {
						e = (e + (0.31-z*0.05))/2.;
						//e += (e - 0.3+z*0.05)/2.;
					}*/
					
					if(bMap[i][j] == 1) {
						e = 0;
					}else if(bMap[i][j] == 2) {
						e = 0.3 + e/20.;
					}else if(bMap[i][j] == 3) {
						e = 0.3 + e/20.;
					}
					
					if(e <= 0.3-z*0.05) {
						if(z < 0) {
							tileMap[i][j][k] = 5;
						}else {
							tileMap[i][j][k] = 3;
						}
					}else if(e <= 0.8-z*0.05){
						if(first) {
							tileMap[i][j][k] = 1;
						}else {
							tileMap[i][j][k] = 4;
						}
					}else {
						tileMap[i][j][k] = 2;
					}
					
					if(first && river[i][j] && tileMap[i][j][k] != 5) {
						tileMap[i][j][k] = 3;
					}
					
					if(tileMap[i][j][k] != 5) first = false;
				}
			}
		}
		
		eMap = null;
		bMap = null;
		river = null;
		cells = null;
		corners = null;
		edges = null;
	}
	
	private Edge getClosestEdge(double x, double y) {
		double dist = 999999;
		Edge close = null;
		for(Edge e: getEdges()) {
			if(e == null || e.water() == 0) {
				continue;
			}
			double d = e.distance(x, y);
			if(d < dist) {
				close = e;
				dist = d;
			}
		}
		
		return close;
	}
	
	public ArrayList<MapCell> getCells(){
		return cells;
	}
	
	public ArrayList<Corner> getCorners(){
		return corners;
	}
	
	public ArrayList<Edge> getEdges(){
		return edges;
	}
	
	public int getTile(int x, int y, int z) {
		return tileMap[x][y][z];
	}
	
	public int width() {
		return tileMap.length;
	}
	
	public int height() {
		return tileMap[0].length;
	}
	
	public int depth() {
		return tileMap[0][0].length;
	}
	
	public double getElevation(double x, double y) {
		double e = noise.generateSimplexNoise(x/100., y/100.)+1;
		e -= (noise.generateSimplexNoise(x/25., y/25.)+1)*0.3;
		return e;
	}
	
	public double getMoisture(double x, double y) {
		double m = noise.generateSimplexNoise(x/200.-256, y/200.-256)+1;
		m += noise.generateSimplexNoise(x/25.-256, y/25.-256)+1;
		m/=2.;
		double cX = 128;
		double cY = 128;
		double dist = Math.sqrt((x-cX)*(x-cX) + (y-cY)*(y-cY))/128.;
		double t1 = Math.max(0, 0.95-1.1*Math.pow(dist, 1.9));
		m *= t1;
		
		return m;
	}
	
	public double getTemperature(double x, double y) {
		double t = noise.generateSimplexNoise(x/200. + 256, y/200. + 256)+1;
		return t;
	}
	
	public int getBiome(double x, double y) {
		double m = getMoisture(x,y);
		double t = getTemperature(x, y);
		if(m <= 0.3) {
			return 1; //Океан
		}else {
			if(t > 1.3) {
				return 3; //Горы
			}else {
				return 2; //Равнины
			}
		}
	}
	
}
