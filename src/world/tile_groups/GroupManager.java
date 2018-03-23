package world.tile_groups;

import java.util.ArrayList;

import util.PriorityQueue;
import util.Vertex;
import world.WorldMap;
import world.Tile.TILE_TYPE;

public class GroupManager {

	private TileGroup reachableWalls;
	private TileGroup unreachableWalls;
	private ArrayList<TileGroup> groups;

	private WorldMap map;
	
	public static void createInstance(WorldMap map) {
		if(map == null) {
			System.out.println("No map to attach to");
			return;
		}
		map.setGroupManager(new GroupManager(map));
	}

	private GroupManager(WorldMap map) {
		this.map = map;
		unreachableWalls = new TileGroup();
		reachableWalls = new TileGroup();
		groups = new ArrayList<TileGroup>();
	}
	
	public TileGroup getGroup(int id) {
		if(id == -1) {
			return reachableWalls;
		}
		
		if(id == -2) {
			return unreachableWalls;
		}
		
		for(int i = 0; i < groups.size(); i++) {
			if(groups.get(i).getId() == id) {
				return groups.get(i);
			}
		}
		
		TileGroup group = new TileGroup(id);
		groups.add(group);
		
		return group;
	}
	
	public TileGroup getExistingGroup(int id) {
		for(int i = 0; i < groups.size(); i++) {
			if(groups.get(i).getId() == id) {
				return groups.get(i);
			}
		}
		
		return null;
	}
	
	public void combine(ArrayList<TileGroup> groups) {
		int choosen = -1;
		int maxSize = -1;
		for(int i = 0; i < groups.size(); i++) {
			if(groups.get(i).getSize() > maxSize) {
				choosen = i;
				maxSize = groups.get(i).getSize();
			}
		}
		
		for(int i = 0; i < groups.size(); i++) {
			if(i == choosen) continue;
			groups.get(choosen).consume(groups.get(i));
			this.groups.remove(groups.get(i));
		}
	}

	public void setMapGroups() {
		int groupCount = 0;
		boolean[][][] visited = new boolean[256][256][1];

		if(map.getWall(0, 0, 0) == TILE_TYPE.NONE) map.getTile(0, 0, 0).setGroup(groupCount++);

		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
		Vertex v = new Vertex(0,0, map.getWeight(0, 0, 0));
		visited[v.getX()][v.getY()][0]=true;

		queue.add(v, v.getCost());
		while(queue.size() > 0){
			Vertex current = queue.poll();
			boolean nextGroup = false;
			int group = map.getGroup(current.getX(), current.getY(), 0);
			
			int x = current.getX();
			int y = current.getY();
			int z = 0;
			
			for(int i = x-1 ; i <= x+1; i++) {
				if(i < 0 || i >= map.getWidth()) continue;
				for(int j = y-1; j <= y+1; j++) {
					if(j < 0 || j >= map.getHeight() || visited[i][j][z] || (i == x && j == y)) continue;
					
					queue.add(new Vertex(i, j, map.getWeight(i, j, z)), 
							map.getWeight(i, j, z));
					visited[i][j][z] = true;
					if((group == -1 || group == -2) && map.tilePassable(i, j, z)){
						map.getTile(i, j, z).setGroup(groupCount);
						nextGroup = true;
						
					}else if(map.tilePassable(i, j, z)){
						map.getTile(i, j, z).setGroup(group);
					}else {
						boolean unreachable = true;
						outer: for(int k = i-1; k <= i+1; k++) {
							for(int h = j-1; h <= j+1; h++) {
								if(k == i-1 && h == j-1) continue;
								if(k == i+1 && h == j-1) continue;
								if(k == i-1 && h == j+1) continue;
								if(k == i+1 && h == j+1) continue;
								if(map.tilePassable(k,h,z)) {
									unreachable = false;
									break outer;
								}
							}
						}
						if(unreachable) {
							map.getTile(i, j, z).setGroup(-2);
						}
					}
					//Новые тайлы раскидываются по группам только если они проходимы
				}
			}

			if(nextGroup){
				groupCount++;
			}
		}
	}
}
