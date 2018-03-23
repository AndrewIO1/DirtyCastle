package ai;

import java.util.ArrayList;

import util.Vertex;
import world.Tile;
import world.WorldMap;

public class Path {
	private ArrayList<Vertex> points;
	private static WorldMap map;
	
	public Path(){
		if(map == null) {
			map = WorldMap.getMap();
		}
		points = new ArrayList<Vertex>(0);
	}
	
	public Vertex nextPoint(){
		if(points == null || points.size() == 0) return null;
		Vertex next = points.get(0);
		Tile vertexTile = map.getTile(next.getTileX(), next.getTileY(), 0);
		vertexTile.removePath(this);
		points.remove(0);
		return next;
	}
	
	public Vertex peek(){
		if(points == null || points.size() == 0) return null;
		return points.get(0);
	}
	
	public void addPoint(Vertex point){
		map.getTile(point.getTileX(), point.getTileY(), 0).addPath(this);
		points.add(point);
	}
	
	public int size(){
		return points.size();
	}
	
	public void optimize(WorldMap map){
		for(int i = 1; i < points.size()-1; i++){
			if(points.get(i-1).getX() == points.get(i+1).getX() || 
			   points.get(i-1).getY() == points.get(i+1).getY()){
				points.remove(i);
				i--;
			}		
		}
		for(int i = 1; i < points.size() - 1; i++){
			if(!checkObstacles(map, points.get(i-1), points.get(i+1))){
				points.remove(i);
				i--;
			}
		}
	}
	
	private boolean checkObstacles(WorldMap map, Vertex p1, Vertex p2){
		if(Math.abs(p1.getX()/32 - p2.getX()/32) >= Math.abs(p1.getY()/32 - p2.getY()/32)){
			int x = p1.getX()/32;
			int y = p1.getY()/32;
			while(x != p2.getX()/32){
				x += (p1.getX()/32 - p2.getX()/32)/Math.abs(p1.getX()/32 - p2.getX()/32);
				y += (p2.getY()/32 - p1.getY()/32)/(p2.getX()/32 - p1.getX()/32);
				if(!map.tilePassable(x, y, 0)){
					return true;
				}
			}
		}else{
			int x = p1.getY()/32;
			int y = p1.getX()/32;
			while(x != p2.getY()/32){
				x += (p1.getY()/32 - p2.getY()/32)/Math.abs(p1.getY()/32 - p2.getY()/32);
				y += (p2.getX()/32 - p1.getX()/32)/(p2.getY()/32 - p1.getY()/32);
				if(!map.tilePassable(y, x, 0)){
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<Vertex> getPoints() {
		return points;
	}
}
