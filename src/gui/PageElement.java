package gui;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import util.DataPack;

public class PageElement extends GuiElement{
	ArrayList<ArrayList<GuiElement>> pages;
	int page = 0;
	
	public PageElement(Builder builder) {
		super(builder);
		
		pages = new ArrayList<ArrayList<GuiElement>>();
		pages.add(new ArrayList<GuiElement>());
	}

	@Override
	public void mouseOverRender(GameContainer gc, StateBasedGame game, Graphics g) {
		generalRender(gc, game, g);
	}

	@Override
	public void mousePressedRender(GameContainer gc, StateBasedGame game, Graphics g) {
		generalRender(gc, game, g);
	}

	@Override
	public void generalRender(GameContainer gc, StateBasedGame game, Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(geometry.x, geometry.y, geometry.width, geometry.height);
		g.setColor(Color.black);
		g.drawRect(geometry.x, geometry.y, geometry.width, geometry.height);
		for(GuiElement e : pages.get(page)) {
			e.render(gc, game, g);
		}
	}

	@Override
	public void elementClickedSignal(GuiElement element, DataPack data) {
		
	}
	
	public void addElement(GuiElement element, int page) {
		super.addElement(element);
		pages.get(page).add(element);
	}
	
	public static class Builder extends GuiElement.Builder implements util.Builder<PageElement>{

		public Builder(int x, int y, int width, int height) {
			super(x, y, width, height);
			
		}

		@Override
		public PageElement build() {	
			return new PageElement(this);
		}
		
	}

	@Override
	public void elementClicked(GameContainer gc, StateBasedGame game, int delta) {
		// TODO Auto-generated method stub
		
	}
	
}
