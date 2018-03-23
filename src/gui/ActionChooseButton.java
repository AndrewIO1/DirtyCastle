package gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import core.GameState;

public class ActionChooseButton extends Button{

	public ActionChooseButton(Builder builder) {
		super(builder);
		
	}
	
	@Override
	public void elementClicked(GameContainer gc, StateBasedGame game, int delta) {
		GameState.setAction(elementData.getInt("Action"));
	}
	
	public static class Builder extends Button.Builder implements util.Builder<ActionChooseButton>{

		public Builder(int x, int y, int width, int height) {
			super(x, y, width, height);
			
		}

		@Override
		public ActionChooseButton build() {	
			return new ActionChooseButton(this);
		}
		
	}

}
