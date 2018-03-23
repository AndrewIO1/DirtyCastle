package entities.static_objects;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import entities.EntityType;
import entities.StaticObject;
import util.EntityFactory;
import world.WorldMap;

public class LogBig extends StaticObject{

	public LogBig(Builder builder) {
		super(builder);
		
	}

	@Override
	public void customUpdate(GameContainer gc, StateBasedGame game, int delta) {
		
	}

	@Override
	public boolean isSelected() {
		
		return false;
	}

	@Override
	protected void customRenderUnder(GameContainer gc, StateBasedGame game, Graphics g) {
		
	}

	@Override
	protected void customRenderAbove(GameContainer gc, StateBasedGame game, Graphics g) {
		g.setColor(Color.orange.darker());
		g.fillRect(x-16, y-16, width, height/2);
	}

	@Override
	public EntityType getType() {
		return EntityType.LOG_BIG;
	}

	@Override
	protected void killCustom() {
		EntityFactory.spawnLog(this);
		host.setObject(null);
	}
	
	public static class Builder extends StaticObject.Builder implements util.Builder<LogBig>{
		
		public Builder(String name) { super(name); }
		
		public Builder(Tree parent) { 
			super(parent.getName() + "_log");
			
			x(parent.getX());
			y(parent.getY());
			z(parent.getZ());
			width(WorldMap.tileSize);
			height(WorldMap.tileSize);
			x_anchor(0.5f);
			y_anchor(0.5f);
			map(parent.getMap());
			variant(parent.getVariant());
			hp(3);
			
		}
		
		@Override
		public LogBig build() {
			
			return new LogBig(this);
		}
		
	}

}
