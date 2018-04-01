package gui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public abstract class GuiContext {
	protected List<GuiElement> elements;
	
	public GuiContext() {
		elements = new ArrayList<GuiElement>();
	}
	
	public void update(GameContainer gc, StateBasedGame game, int delta) {
		
		for(int i = 0; i < elements.size(); i++) {
			elements.get(i).update(gc, game, delta);
		}
	}
	
	public void render(GameContainer gc, StateBasedGame game, Graphics g) {
		for(int i = 0; i < elements.size(); i++) {
			elements.get(i).render(gc, game, g);
		}
	}
	
}
