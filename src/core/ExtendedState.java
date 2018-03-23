package core;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public abstract class ExtendedState extends BasicGameState{
	
	protected boolean currentState = false;

	@Override
	public abstract void init(GameContainer gc, StateBasedGame game) throws SlickException;//При запуске игры
	
	public final void load(GameContainer gc, StateBasedGame game) {
		
		customLoad(gc, game);
		currentState = true;
		
	}
	
	public abstract void customLoad(GameContainer gc, StateBasedGame game);//При переходе в State
	
	public abstract void clear(GameContainer gc, StateBasedGame game);//Очищение переменных

	@Override
	public final void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		if(!currentState) return;
		customRender(gc,game,g);
	}
	
	public abstract void customRender(GameContainer gc, StateBasedGame game, Graphics g);

	@Override
	public final void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(!currentState) return;
		customUpdate(gc, game, delta);
	}
	
	public abstract void customUpdate(GameContainer gc, StateBasedGame game, int delta);

	@Override
	public abstract int getID();

}
