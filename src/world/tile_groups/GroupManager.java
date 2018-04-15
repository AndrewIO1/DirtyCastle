package world.tile_groups;

import java.util.ArrayList;
import java.util.List;

import util.PriorityQueue;
import util.Vertex3i;
import world.WorldMap;

public class GroupManager {

	private TileGroup reachableWalls;
	private TileGroup unreachableWalls;
	private List<TileGroup> groups;

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
		int startZ = 16;
		boolean[][][] visited = new boolean[32][256][256];

		if(map.tilePassable(0, 0, startZ)) map.getTile(0, 0, startZ).setGroup(groupCount++);

		PriorityQueue<Vertex3i> queue = new PriorityQueue<Vertex3i>();
		Vertex3i v = new Vertex3i(0,0,startZ, map.getWeight(0, 0, startZ));
		visited[startZ][v.getX()][v.getY()]=true;

		queue.add(v, v.getCost());
		while(queue.size() > 0){
			Vertex3i current = queue.poll();
			boolean nextGroup = false;

			int x = current.getX();
			int y = current.getY();
			int z = current.getZ();

			int group = map.getGroup(x, y, z);

			for(int m = z-1; m <= z+1; m++) {
				if(m < 0 || m >= map.getDepth()) continue;
				/*if(m != z) {
					if(map.getWall(x, y, z) != TILE_TYPE.STAIRS_U_GRASS &&
					   map.getFloor(x, y, z) != TILE_TYPE.STAIRS_D_GRASS) {
						continue;
					}
				}*/
				for(int i = x-1 ; i <= x+1; i++) {
					if(i < 0 || i >= map.getWidth()) continue;
					for(int j = y-1; j <= y+1; j++) {
						if(j < 0 || j >= map.getHeight() || visited[m][i][j]) continue;
						if(m != z) {
							if (i != x || j != y) continue;
						}else {
							if (i == x && j == y) continue;
						}

						queue.add(new Vertex3i(i, j, m, map.getWeight(i, j, m)), 
								map.getWeight(i, j, m));
						visited[m][i][j] = true;
						if((group == -1 || group == -2) && map.tilePassable(i, j, m)){
							map.getTile(i, j, m).setGroup(groupCount);
							nextGroup = true;

						}else if(map.tilePassable(i, j, m)){
							map.getTile(i, j, m).setGroup(group);
						}else {
							boolean unreachable = true;
							outer: for(int k = i-1; k <= i+1; k++) {
								for(int h = j-1; h <= j+1; h++) {
									if(k == i-1 && h == j-1) continue;
									if(k == i+1 && h == j-1) continue;
									if(k == i-1 && h == j+1) continue;
									if(k == i+1 && h == j+1) continue;
									if(map.tilePassable(k,h,m)) {
										unreachable = false;
										break outer;
									}
								}
							}
							if(unreachable) {
								map.getTile(i, j, m).setGroup(-2);
							}
						}
						//Новые тайлы раскидываются по группам только если они проходимы
					}
				}
			}

			if(nextGroup){
				groupCount++;
			}
		}
	}
}
