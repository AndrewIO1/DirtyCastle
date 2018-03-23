package world.tile_groups;

import java.util.ArrayList;

import world.Tile;

public class TileGroup {
	private static int lastID = -2;

	private int id;
	private ArrayList<Tile> tiles;

	public TileGroup() {
		tiles = new ArrayList<Tile>();
		id = lastID;
		lastID++;
	}

	public TileGroup(int id) {
		tiles = new ArrayList<Tile>();
		
		if(id < lastID) {
			this.id = lastID;
			lastID++;
			return;
		}
		
		this.id = id;
		lastID = id+1;
	}
	
	public int getSize() {
		return tiles.size();
	}

	public int getId() {
		return id;
	}

	public void addTile(Tile tile) {
		tiles.add(tile);
	}

	public void removeTile(Tile tile) {
		tiles.remove(tile);
	}

	public ArrayList<Tile> getTiles(){
		return tiles;
	}

	public void consume(TileGroup another) {
		ArrayList<Tile> newTiles = another.getTiles();
		for(int i = 0; i < newTiles.size();i++) {
			newTiles.get(i).setGroup(this);
			i--;
		}
		tiles.addAll(newTiles);
		newTiles.clear();
	}
}
