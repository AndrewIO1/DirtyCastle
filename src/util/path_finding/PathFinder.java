package util.path_finding;

import java.util.ArrayList;
import java.util.List;

import ai.AI;
import ai.Path;
import util.PriorityQueue;
import util.Vertex3i;
import util.tasks.Task;
import world.Tile.TILE_TYPE;
import world.WorldMap;

public class PathFinder extends Thread {
	protected static volatile int calculationIterLimit = 10000;

	private int x;
	private int y;
	private int z;

	@SuppressWarnings("unused")
	private int BaseCost;

	private int NoneCost;
	private Vertex3i from;
	private PriorityQueue<Vertex3i> now;
	private List<Vertex3i> come_from;
	private float[][][] costs;
	private boolean[] reset;
	private boolean alive;
	private volatile boolean calculating;
	private AI requester;

	protected PathFinder() {
		this.setDaemon(true);
		alive = true;
		reset = new boolean[WorldMap.getMap().getDepth()];
		costs = new float[WorldMap.getMap().getDepth()][WorldMap.getMap().getWidth()][WorldMap.getMap().getHeight()];
		for(int k = 0; k < costs.length; k++) {
			reset[k] = false;
			for(int i = 0; i < costs[0].length ; i++){
				for(int j = 0; j < costs[0][0].length ; j++){
					costs[k][i][j] = 9999;
				}
			}
		}
	}

	public void requestPath(int x, int y, int z, int BaseCost, int NoneCost, Vertex3i from, AI requester) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.BaseCost = BaseCost;
		this.NoneCost = NoneCost;
		this.from = from;
		this.requester = requester;
		now = null;
		come_from = null;
		calculating = true;
		this.interrupt();
	}

	public void requestPath(PathFinderRequest request) {
		this.requestPath(request.getX(), request.getY(), request.getZ(), request.getBaseCost(), 
				request.getNoneCost(), request.getFrom(), request.getRequester());
	}

	public boolean isCalculating() {
		return calculating;
	}

	private Vertex3i getLastVertex() {
		return come_from.get(0);
	}

	protected final boolean unusedVertex(Vertex3i v){
		for(int i = 0; i < come_from.size(); i++){
			if(come_from.get(i).equals(v)){
				return false;
			}
		}
		return true;
	}

	public AI getRequester() {
		return requester;
	}

	public void cancelTask() {
		calculating = false;
	}

	public void run() {
		main: while(alive) {
			if(!calculating) {
				try {
					Thread.sleep(240000);
				} catch (InterruptedException e) {
					//System.out.println(this.getName() + " awoken");
				}
				continue;
			}

			if(x == from.getX() && y == from.getY()) {
				requester.setPath(new Path());
				calculating = false;
				continue;
			}

			//int calculationIter = 0;

			if(now == null || now.size() == 0){

				if(now == null) {
					now = new PriorityQueue<Vertex3i>();
				}
				if(come_from == null) {
					come_from = new ArrayList<Vertex3i>();
				}

				float priority = (Math.abs(requester.getHostTileX() - x) + 
						Math.abs(requester.getHostTileY() - y) + 
						Math.abs(requester.getHostZ() - z))*2;

				now.add(new Vertex3i(x, y, z, 0), priority);

				for(int i = 0; i < reset.length; i++) {
					reset[i] = false;
					if(i == z) {
						reset[i] = true;
					}
				}
				
				for(int i = 0; i < costs[0].length ; i++){
					for(int j = 0; j < costs[0][0].length ; j++){
						costs[z][i][j] = 9999;
					}
				}

				costs[z][x][y] = 0;
			}

			while(now.size() > 0){

				//visited[now.peek(0).getX()][now.peek(0).getY()] = true;
				come_from.add(0, now.poll());
				Vertex3i tmp;
				tmp = getLastVertex().copy();
				int lastVertexX = getLastVertex().getX();
				int lastVertexY = getLastVertex().getY();
				int lastVertexZ = getLastVertex().getZ();
				float lastVertexCost = getLastVertex().getCost();
				for(int m = 0; m < 3; m++) {
					if(WorldMap.getMap().getTile(lastVertexX, lastVertexY, z).getFloor() != TILE_TYPE.STAIRS_D_GRASS && m == 2) {
						continue;
					}
					if(WorldMap.getMap().getTile(lastVertexX, lastVertexY, z).getWall() != TILE_TYPE.STAIRS_U_GRASS && m == 0) {
						continue;
					}
					if(m != 1 && z+m > 0 && z+m < costs.length && !reset[z+m]) {
						for(int i = 0; i < costs[0].length ; i++){
							for(int j = 0; j < costs[0][0].length ; j++){
								costs[z+m][i][j] = 9999;
							}
						}
					}
					for(int i = 0; i < 3; i++){
						for(int j = 0; j < 3; j++){

							if(i == 1 && j == 1){
								if(m == 1) {
									continue;
								}
							}else if(m != 1) {
								continue;
							}

							//tmp = getLastVertex().copy();
							tmp.setlocation(lastVertexX - 1 + i,
									lastVertexY - 1 + j, lastVertexZ - 1 + m);

							if(tmp.getX() < 0 || tmp.getX() >= costs[0].length ||
									tmp.getY() < 0 || tmp.getY() >= costs[0][0].length) {
								continue;
							}

							if(come_from.contains(tmp)) {
								continue;
							}

							if(costs[tmp.getZ()][tmp.getX()][tmp.getY()] != 9999) {
								continue;
							}

							float newCost;
							if(WorldMap.getMap().tilePassable(tmp.getX(), tmp.getY(), tmp.getZ())){
								newCost = (float) (lastVertexCost+NoneCost*(Math.abs(i-1)+Math.abs(j-1)+Math.abs(m-1)));
								if(WorldMap.getMap().getFirstTypeTask(tmp.getX(), tmp.getY(), tmp.getZ(), Task.WALL_BUILD) != null) {
									newCost += 40;
								}
								//newCost = (float) (lastVertexCost+NoneCost*(Math.abs(i-1)+Math.abs(j-1)));
							}else{
								newCost = lastVertexCost+9999;
							}

							tmp.setCost(newCost);
							if(tmp.equals(from)){
								Path foundPath = new Path();
								int curX = tmp.getX();
								int curY = tmp.getY();
								int curZ = tmp.getZ();
								while(curX != x || curY != y || curZ != z){
									float curCost = costs[curZ][curX][curY];
									int tmpK = 0;
									int tmpN = 0;
									int tmpC = 0;
									for(int c = 0; c < 3; c++) {
										for(int k = 0; k < 3; k++){
											for(int n = 0; n < 3; n++){
												if(c != 1) {
													if(k != 1 || n != 1) continue;
												}
												if(curX-1+k < 0 || curX-1+k >= costs[0].length) continue;
												if(curY-1+n < 0 || curY-1+n >= costs[0][0].length) continue;
												if(curZ-1+c < 0 || curZ-1+c >= costs.length) continue;
												if(costs[curZ-1+c][curX-1+k][curY-1+n] < curCost){
													tmpK = k;
													tmpN = n;
													tmpC = c;
													curCost = costs[curZ][curX-1+k][curY-1+n];
												}
											}
										}
									}
									curX += -1 + tmpK;
									curY += -1 + tmpN;
									curZ += -1 + tmpC;
									foundPath.addPoint(new Vertex3i(curX*WorldMap.tileSize+16, curY*WorldMap.tileSize+16, curZ, 0));
								}
								now = null;
								come_from = null;
								requester.setPath(foundPath);
								requester = null;
								calculating = false;
								//System.out.println("Iterations: " + calculationIter);
								continue main;
							}
							if(!calculating) {
								now = null;
								come_from = null;
								requester = null;
								continue main;
							}

							if(tmp.getCost() < costs[tmp.getZ()][tmp.getX()][tmp.getY()]){

								double distance = Math.sqrt(Math.pow(from.getX() - tmp.getX(),2) + Math.pow(from.getY() - tmp.getY(),2));
								distance += Math.abs(from.getZ() - tmp.getZ());
								float priority = (float) (tmp.getCost() + distance*3);

								//float priority = (float) (tmp.getCost() + (Math.abs(from.getX() - tmp.getX()) + Math.abs(from.getY() - tmp.getY()))*2);
								now.remove(tmp); //оптимизация закомментированного кода выше
								now.add(tmp.copy(), priority);
								costs[tmp.getZ()][tmp.getX()][tmp.getY()] = tmp.getCost();
							}
							/*calculationIter++;
						if(calculationIter >= calculationIterLimit) {
							System.out.println(getName() + " reached limit, taking rest");
							System.out.println("Now size: " + now.size());
							System.out.println("Come_from size: " + come_from.size());
							System.out.println("from: " + from.getX() + " " + from.getY());
							System.out.println("to: " + x + " " + y);

							try {
								sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							calculationIter = 0;
						}*/
						}
					}
				}
			}


			now = null;
			come_from = null;
			//TODO нахождение пути фейлится
			requester.pathFinderFailed();
			requester = null;
			calculating = false;
			continue main;
		}
	}
}