package world;

import util.EntityFactory;
import util.SimplexNoise;
import world.Tile.TILE_TYPE;

public class MapGenerator {
	
	private static int seed = 4;

	public static void generate(int centerX, int centerY){
		
		SimplexNoise simplex = new SimplexNoise(seed);
		
		DiagramMap diagram = new DiagramMap(simplex);
		diagram.generate(64, 64, 0.25);
		
		Tile[][][] tiles = new Tile[256][256][1];
		WorldMap mapSingleton = WorldMap.createMap(tiles);

		EntityFactory.init();

		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[0].length; j++){
				int b = diagram.getTile(i, j, 32);
				mapSingleton.placeTile(i, j, 0, getWall(b), getFloor(b));
			}
		}
		//генерация карты

		mapSingleton.groupManager().setMapGroups();
		
		mapSingleton.createMinimap();
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
