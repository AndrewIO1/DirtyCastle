package entities.loot;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import entities.EntityType;
import entities.MovableObject;
import entities.static_objects.LogBig;
import graphics.TextureStorage;

public class Log extends MovableObject{
	
	public Log(Builder builder) {
		super(builder);
		
		sprites.add(new ArrayList<Image>(0));
		sprites.get(0).add(TextureStorage.getSpriteTile("logs", variant, 0));
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
		
	}

	@Override
	public EntityType getType() {
		
		return EntityType.LOG;
	}

	@Override
	protected void killCustom() {
		
	}
	
	public static class Builder extends MovableObject.Builder implements util.Builder<Log>{
		
		public Builder(String name) { super(name); }
		
		public Builder(LogBig log) {
			super(log.getName() + "_loot");
			x(log.getX());
			y(log.getY());
			z(log.getZ());
			width(log.getWidth());
			height(log.getHeight());
			x_anchor(0.5f);
			y_anchor(0.5f);
			mass(20);
			map(log.getMap());
			variant(log.getVariant()/3);
		}
		@Override
		public Log build() {
			
			return new Log(this);
		}
		
	}
	
}
