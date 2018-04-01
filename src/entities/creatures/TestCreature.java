package entities.creatures;


import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import core.DwarfsGame;
import entities.Creature;
import entities.EntityType;
import graphics.TextureStorage;

public class TestCreature extends Creature{
	List<Image> additionalSprites;

	public TestCreature(Builder builder) {
		super(builder);
		
		int variation = DwarfsGame.rnd.nextInt(5);
		int head_var_x = DwarfsGame.rnd.nextInt(14);
		int head_var_y = DwarfsGame.rnd.nextInt(6);
		sprites.add(new ArrayList<Image>());
		sprites.add(new ArrayList<Image>());
		sprites.add(new ArrayList<Image>());
		additionalSprites = new ArrayList<Image>();
		Image body = TextureStorage.getSpriteTile("citizen_body", 0, variation);
		Image head = TextureStorage.getSpriteTile("citizen_head_f", head_var_x, head_var_y);
		additionalSprites.add(head);
		sprites.get(0).add(body);
		for(int i = 1; i < 5; i++) {
			body = TextureStorage.getSpriteTile("citizen_body", i, variation);
			sprites.get(1).add(body);
		}
		for(int i = 5; i < 12; i++) {
			body = TextureStorage.getSpriteTile("citizen_body", i, variation);
			sprites.get(2).add(body);
		}
		
	}

	@Override
	protected void customRenderUnder(GameContainer gc, StateBasedGame game, Graphics g) {
		
	}

	@Override
	protected void customRenderAbove(GameContainer gc, StateBasedGame game, Graphics g) {
		Image sprite = additionalSprites.get(0);
		sprite = sprite.getFlippedCopy(flipX, flipY);
		g.drawImage(sprite, x-width*x_anchor, y-height*y_anchor);
		if(itemInHands != null) {
			g.drawImage(itemInHands.getCurrentSprite(), x-width*x_anchor, y-height*y_anchor);
		}
		if(ai.getTargetAI() != null) {
			g.setColor(Color.green);
			g.drawLine(x, y, ai.getTargetAI().getHostX(), ai.getTargetAI().getHostY());
		}
	}

	@Override
	public EntityType getType() {
		
		return EntityType.TEST_CREATURE;
	}

	@Override
	protected void killCustom() {
		
	}

	@Override
	public boolean isSelected() {
		
		return false;
	}
	
	public static class Builder extends Creature.Builder implements util.Builder<TestCreature>{

		public Builder(String name) { super(name); }
		@Override
		public TestCreature build() {
			
			return new TestCreature(this);
		}
		
	}
}
