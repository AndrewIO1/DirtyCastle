package graphics;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public interface Renderable {
	public void render(GameContainer gc, StateBasedGame game, Graphics g);
	public int getPriority();
}
