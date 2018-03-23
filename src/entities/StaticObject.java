package entities;

import world.Tile;
import world.WorldMap;

public abstract class StaticObject extends GameObject{
	protected Tile host;

	public StaticObject(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor,
			WorldMap map) {
		super(name, x, y, z, width, height, x_anchor, y_anchor, map);
		host = getTile();
		host.setObject(this);
	}
	
	public StaticObject(Builder builder) {
		super(builder);
		
		host = getTile();
		host.setObject(this);
	}
	
	public final Tile getHost() {
		return host;
	}
	
	public static class Builder extends GameObject.Builder{
		
		public Builder(String name) { super(name); }
		
	}

}
