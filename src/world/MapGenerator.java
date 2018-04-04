package world;

import util.EntityFactory;
import util.SimplexNoise;
import world.Tile.TILE_TYPE;

public class MapGenerator {

	private static int seed = 4;
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
							//TODO где-то юзается очень много памяти
							int b = diagram.getTile(i, j, k);
							mapSingleton.placeTile(i, j, k, getWall(b), getFloor(b));
							progress++;
						}
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
