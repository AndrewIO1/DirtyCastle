package gui;

import org.lwjgl.opengl.Display;

import core.GameState;

public class Gui extends GuiContext{

	public Gui() {
		super();
		PageElement.Builder builderPage = new PageElement.Builder(Display.getWidth()-256, 
				Display.getHeight()-256, 
				256, 256);
		builderPage.context(this);
		PageElement actionPanel = builderPage.build();

		for(int i = 0; i < GameState.MAX_TASK; i++) {
			ActionChooseButton.Builder builder = new ActionChooseButton.Builder(Display.getWidth()-250 + i*38, 
																				Display.getHeight()-250, 
																				32, 32);
			builder.parent(actionPanel);
			builder.context(this);
			ActionChooseButton actionButton = builder.build();
			actionButton.addData("Action", i);
			actionPanel.addElement(actionButton, 0);
		}

		elements.add(actionPanel);
	}
}
