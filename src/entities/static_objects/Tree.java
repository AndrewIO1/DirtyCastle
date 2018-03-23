package entities.static_objects;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import entities.EntityType;
import entities.StaticObject;
import graphics.TextureStorage;
import util.EntityFactory;
import util.tasks.Task;
import world.WorldMap;

public class Tree extends StaticObject{

	public Tree(String name, float x, float y, int z, int width, int height, float x_anchor, float y_anchor, WorldMap map, int variant) {
		super(name, x, y, z, width, height, x_anchor, y_anchor, map);
		hp = 3;
		sprites.add(new ArrayList<Image>(0));
		sprites.get(0).add(TextureStorage.getSpriteTile("trees", variant, 0));
		this.variant = variant;
	}
	
	public Tree(Builder builder) {
		super(builder);
		sprites.add(new ArrayList<Image>(0));
		sprites.get(0).add(TextureStorage.getSpriteTile("trees", builder.variant(), 0));
	}

	@Override
	protected void customRenderUnder(GameContainer gc, StateBasedGame game, Graphics g) {

	}

	@Override
	protected void customRenderAbove(GameContainer gc, StateBasedGame game, Graphics g) {
		
	}

	@Override
	public EntityType getType() {
		
		return EntityType.TREE;
	}

	@Override
	protected void killCustom() {
		
		EntityFactory.spawnLogBig(this);
	}

	@Override
	public void customUpdate(GameContainer gc, StateBasedGame game, int delta) {
		// TODO Рост

	}

	@Override
	public boolean isSelected() {
		if(getFirstTypeTask(Task.TREE_CUT) != null) {
			return true;
		}
		return false;
	}
	
	public static class Builder extends StaticObject.Builder implements util.Builder<Tree>{

		public Builder(String name) {
			super(name);
			hp(3);
		}

		@Override
		public Tree build() {
			return new Tree(this);
		}
		
	}

}
