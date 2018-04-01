package gui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import core.GameState;
import util.DataPack;

public abstract class GuiElement {
	protected GuiContext context;
	protected GuiElement parent;
	protected List<GuiElement> child;
	protected Geometry geometry;
	protected List<List<Image>> sprites;
	protected DataPack elementData;
	
	public GuiElement(Builder builder) {
		parent = builder.parent;
		context = builder.context;
		
		geometry = new Geometry(builder.x, builder.y, 
				             builder.width, builder.height);
		
		child = new ArrayList<GuiElement>();
		elementData = new DataPack();
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public void addData(String name, int data) {
		elementData.putInt(name, data);
	}
	
	public void addData(String name, String data) {
		elementData.putString(name, data);
	}
	
	public void addElement(GuiElement element) {
		child.add(element);
	}
	
	public void update(GameContainer gc, StateBasedGame game, int delta) {
		if(clicked(gc)) {
			elementClicked(gc, game, delta);
			if(parent!=null) {
				parent.elementClickedSignal(this, elementData);
			}
		}
		for(GuiElement e : child) {
			e.update(gc, game, delta);
		}
	}
	
	public void render(GameContainer gc, StateBasedGame game, Graphics g) {
		if(pressed(gc)) {
			mousePressedRender(gc, game, g);
		}else if(mouseOver(gc)) {
			mouseOverRender(gc, game, g);
		}else {
			generalRender(gc, game, g);
		}
		for(GuiElement e : child) {
			e.render(gc, game, g);
		}
	}
	
	public abstract void mouseOverRender(GameContainer gc, StateBasedGame game, Graphics g);
	public abstract void mousePressedRender(GameContainer gc, StateBasedGame game, Graphics g);
	public abstract void generalRender(GameContainer gc, StateBasedGame game, Graphics g);
	
	public boolean mouseOver(GameContainer gc) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		boolean result = mouseX >= geometry.x &&
				   		 mouseX <= geometry.x + geometry.width &&
				   		 mouseY >= geometry.y &&
				   		 mouseY <= geometry.y + geometry.height;
		if(result) GameState.mouseEnteredGui();
		return result;
	}
	
	public boolean clicked(GameContainer gc) {
		return mouseOver(gc) && GameState.lbmClicked(false);
	}
	
	public boolean pressed(GameContainer gc) {
		return mouseOver(gc) && gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
	}
	
	public abstract void elementClickedSignal(GuiElement element, DataPack data);
	public abstract void elementClicked(GameContainer gc, StateBasedGame game, int delta);
	
	protected class Geometry{
		protected int x, y, width, height;
		
		private Geometry(int x, int y, int width, int height) {
			setGeometry(x,y,width,height);
		}
		
		public void setGeometry(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public void setX(int x) {
			this.x = x;
		}
		
		public void setY(int y) {
			this.y = y;
		}
		
		public void setWidth(int width) {
			this.width = width;
		}
		
		public void setHeight(int height) {
			this.height = height;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
	}
	
	public static class Builder{
		protected int x, y, width, height;
		protected GuiContext context = null;
		protected GuiElement parent = null;
		
		public Builder(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public Builder context(GuiContext context) { this.context = context; return this; }
		public Builder parent(GuiElement parent) { this.parent = parent; return this; }
	}
}
