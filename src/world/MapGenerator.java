package world;

import util.EntityFactory;
import util.SimplexNoise;
import world.Tile.TILE_TYPE;

public class MapGenerator {

	private static int seed = 4;
	private static String status = "Generating tiles";
	private static volatile int progress;
	private static int maxProgress;
	private static volatile boolean generating = true;

	public static void generate(int centerX, int centerY){
		progress = 0;
		maxProgress = 256*256*32;

		Thread generatorThread = new Thread() {
			public void run() {
				SimplexNoise simplex = new SimplexNoise(seed);

				DiagramMap diagram = new DiagramMap(simplex);
				diagram.generate(64, 64, 0.25);

				final WorldMap mapSingleton = WorldMap.createMap();

				EntityFactory.init();

				for(short i = 0; i < mapSingleton.getWidth(); i++){
					for(short j = 0; j < mapSingleton.getHeight(); j++){
						for(short k = 0; k < mapSingleton.getDepth(); k++){
							int b = diagram.getTile(i, j, k);
							mapSingleton.placeTile(i, j, k, getWall(b), getFloor(b));
							progress++;
						}
					}
				}
				progress = 0;
				status = "Placing slopes: ";
				Tile check;
				for(short k = 0; k < mapSingleton.getDepth(); k++){
					for(short i = 0; i < mapSingleton.getWidth(); i++){
						nextTile: for(short j = 0; j < mapSingleton.getHeight(); j++){
							progress++;
							if(mapSingleton.getTile(i, j, k).getFloor() != TILE_TYPE.GRASS) continue;

							for(int x = i-1; x <= i+1; x++) {
								for(int y = j-1; y <= j+1; y++) {
									if(Math.abs(i-x) + Math.abs(j-y) >= 2) continue;
									if(x == i && j == y) continue;

									check = mapSingleton.getTile(x, y, k);
									if(check == null) continue;
									if(check.getFloor() != TILE_TYPE.NONE) continue;

									check = mapSingleton.getTile(x, y, k+1);
									if(check == null) continue;
									if(check.getFloor() == TILE_TYPE.NONE) continue;
									if(check.getWall() != TILE_TYPE.NONE) continue;

									if(mapSingleton.getTile(i, j, k+1).getFloor() == TILE_TYPE.NONE) continue;
									mapSingleton.getTile(i, j, k+1).setWall(TILE_TYPE.STAIRS_U_GRASS);
									mapSingleton.getTile(i, j, k).setFloor(TILE_TYPE.STAIRS_D_GRASS);
									continue nextTile;
								}
							}
						}
					}
				}
				progress = 0;
				maxProgress = mapSingleton.getWidth()*mapSingleton.getHeight();
				status = "Growing trees: ";
				for(short i = 0; i < mapSingleton.getWidth(); i++){
					for(short j = 0; j < mapSingleton.getHeight(); j++){
						progress++;
						if(diagram.getTreeValue(i, j) < 0.6) continue;
						
						short z = -1;
						Tile selected;
						do {
							z++;
							selected = mapSingleton.getTile(i, j, z);
							if(selected.getFloor() != TILE_TYPE.NONE && selected.getFloor() != TILE_TYPE.GRASS) {
								z = -1;
								break;
							}
						}while(selected.getWall() != TILE_TYPE.NONE || selected.getFloor() != TILE_TYPE.GRASS);
						if(z == -1) continue;
						EntityFactory.spawnTree(i, j, z);
					}
				}
				//генерация карты

				mapSingleton.groupManager().setMapGroups();

				generating = false;
			}
		};

		generatorThread.setName("MapGen");
		generatorThread.setDaemon(true);
		generatorThread.start();

	}

	public static synchronized String getStatus() {
		return status;
	}

	public static synchronized int getProgress() {
		return progress;
	}

	public static int getMaxProgress() {
		return maxProgress;
	}

	public static synchronized boolean generating() {
		return generating;
	}

	private static final TILE_TYPE getFloor(int biome){
		if(biome == 1) {
			return TILE_TYPE.GRASS;
		}else if(biome == 2) {
			return TILE_TYPE.ROCK;
		}else if(biome == 3) {
			return TILE_TYPE.WATER;
		}else if(biome == 4) {
			return TILE_TYPE.DIRT;
		}

		return TILE_TYPE.NONE;
	}

	private static final TILE_TYPE getWall(int biome){

		if(biome == 2) {
			return TILE_TYPE.ROCK;
		}else if(biome == 4) {
			return TILE_TYPE.DIRT;
		}

		return TILE_TYPE.NONE;
		//Достаёт стенку в зависимости от высоты (e)
	}
}
