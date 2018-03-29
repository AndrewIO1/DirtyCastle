package world;

import java.util.ArrayList;

import util.SimplexNoise;

public class DiagramMap {
	private double sX = 96;
	private double sY = 96;
	private double scale = 0.25;
	//private double eScale = 0.02;
	private int[][][] tileMap = new int[256][256][64];
	private double[][] eMap = new double[256][256];
	private boolean[][] river = new boolean[256][256];
	private ArrayList<MapCell> cells = new ArrayList<MapCell>();
	private ArrayList<Corner> corners = new ArrayList<Corner>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
	public DiagramMap() {
		
	}
	
	public void computeTilesNoise(SimplexNoise noise) {
		for(int i = 0; i < tileMap.length; i++) {
			for(int j = 0; j < tileMap[0].length; j++) {
				double x = sX + i*scale;
				double y = sY + j*scale;
				
				double e = noise.generateSimplexNoise(x/100., y/100.)+1;
				e -= (noise.generateSimplexNoise(x/25., y/25.)+1)*0.3;

				double cX = 128;
				double cY = 128;
				double dist = Math.sqrt((x-cX)*(x-cX) + (y-cY)*(y-cY))/128.;
				double t1 = Math.max(0, 0.95-1.1*Math.pow(dist, 1.9));
				e *= t1;
				
				MapCell parent = null;
				for(MapCell mC : cells) {
					if(mC.poly == null) continue;
					if(mC.poly.contains(x, y)) {
						parent = mC;
						break;
					}
				}
				if(parent != null) {
					Edge edge = parent.getClosestEdge(x,y);
					if(edge != null && edge.water > 0) {
						
						double eDist = edge.distance(x, y)/* + 2.3*noise.generateSimplexNoise(x/3., y/3.)*/;
						double base = 0.009/* + 0.0018*(noise.generateSimplexNoise(x/1.1, y/1.1))*/;
						double eDistNoise = -1.+edge.water*0.6 + 2.9*(noise.generateSimplexNoise(x/3.1, y/3.1)+1);
						if(eDistNoise <= 0.75) eDistNoise = 0.75;
						double multi = eDist/eDistNoise;
						
						double rE = Math.pow(base, multi);
						if(rE >= 0.1) {
							river[i][j] = true;
						}
					}
				}
				
				eMap[i][j] = e;
			}
		}
		
		for(int i = 0; i < tileMap.length; i++) {
			for(int j = 0; j < tileMap[0].length; j++) {
				
				boolean first = true;
				for(int k = 0; k < tileMap[0][0].length; k++) {
					
					
					double z = (k-32);
					
					double e =  eMap[i][j];
					
					if(z <= 0) {
						e = (e + (0.31-z*0.05))/2.;
						//e += (e - 0.3+z*0.05)/2.;
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
	
}
