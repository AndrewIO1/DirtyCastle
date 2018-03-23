package gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import util.DataPack;

public abstract class Button extends GuiElement{

	public Button(Builder builder) {
		super(builder);
		
	}

	@Override
	public void mouseOverRender(GameContainer gc, StateBasedGame game, Graphics g) {
		g.setColor(Color.darkGray.brighter());
		g.fillRect(geometry.x, geometry.y, geometry.width, geometry.height);
		g.setColor(Color.white);
		g.drawRect(geometry.x, geometry.y, geometry.width, geometry.height);
	}

	@Override
	public void mousePressedRender(GameContainer gc, StateBasedGame game, Graphics g) {
		g.setColor(Color.darkGray.darker());
		g.fillRect(geometry.x, geometry.y, geometry.width, geometry.height);
		g.setColor(Color.white);
		g.drawRect(geometry.x, geometry.y, geometry.width, geometry.height);
	}

	@Override
	public void generalRender(GameContainer gc, StateBasedGame game, Graphics g) {
		g.setColor(Color.darkGray);
		g.fillRect(geometry.x, geometry.y, geometry.width, geometry.height);
		g.setColor(Color.white);
		g.drawRect(geometry.x, geometry.y, geometry.width, geometry.height);
	}

	@Override
	public void elementClickedSignal(GuiElement element, DataPack data) {
		
	}

	@Override
	public void elementClicked(GameContainer gc, StateBasedGame game, int delta) {
		
	}
	
	public static class Builder extends GuiElement.Builder{

		public Builder(int x, int y, int width, int height) {
			super(x, y, width, height);
			
		}
		
	}

}
