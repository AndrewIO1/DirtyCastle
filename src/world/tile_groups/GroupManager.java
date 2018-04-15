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
		boolean[][][] visited = new boolean[32][256][256];
		PriorityQueue<Vertex3i> queue = new PriorityQueue<Vertex3i>();

		for(int iX = 0; iX < map.getWidth(); iX++) {
			for(int jY = 0; jY < map.getHeight(); jY++) {
				for(int k = 0; k < map.getDepth(); k++) {


					if(visited[k][iX][jY] || !map.tilePassable(iX, jY, k)) continue;
					map.getTile(iX, jY, k).setGroup(groupCount++);

					Vertex3i v = new Vertex3i(iX,jY,k, map.getWeight(iX, jY, k));
					visited[v.getZ()][v.getX()][v.getY()]=true;

					queue.add(v, v.getCost());
					while(queue.size() > 0){
						Vertex3i current = queue.poll();

						int x = current.getX();
						int y = current.getY();
						int z = current.getZ();

						int group = map.getGroup(x, y, z);

						for(int m = z-1; m <= z+1; m++) {
							if(m < 0 || m >= map.getDepth()) continue;
							for(int i = x-1 ; i <= x+1; i++) {
								if(i < 0 || i >= map.getWidth()) continue;
								for(int j = y-1; j <= y+1; j++) {
									if(j < 0 || j >= map.getHeight() || visited[m][i][j]) continue;
									if(m != z) {
										if (i != x || j != y) continue;
									}else {
										if (i == x && j == y) continue;
									}

									if(visited[m][i][j]) continue;
									if(map.tilePassable(i, j, m)){
										map.getTile(i, j, m).setGroup(group);
										queue.add(new Vertex3i(i, j, m, map.getWeight(i, j, m)), 
												map.getWeight(i, j, m));
									}else{
										map.getTile(i, j, m).setGroup(-1);
									}

									visited[m][i][j] = true;
									//Новые тайлы раскидываются по группам только если они проходимы
								}
							}
						}
					}
				}
			}
		}
	}
}
