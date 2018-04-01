package world;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import entities.EntityType;
import util.tasks.Task;
import util.tasks.TaskManager;
import world.Tile.TILE_TYPE;

public class MiniMap {
	private int x;
	private int y;
	private volatile WorldMap map;
	private volatile ImageBuffer bufferMap;
	private volatile Image[] miniMap;

	public MiniMap(WorldMap map) {
		this.map = map;
		bufferMap = new ImageBuffer(map.getWidth(), map.getHeight());
		miniMap = new Image[map.getDepth()];
		updateMiniMap();
		x = Display.getWidth()-256;
		y = 0;
	}

	public void updateMiniMap() {
		updateMiniMap(0,0,0,map.getWidth(),map.getHeight(),map.getDepth());

	}

	public void updateMiniMap(int x, int y, int z, int width, int height, int depth) {
		int alpha = 255;
		for(int k = z; k < depth; k++) {
			for(int i = x; i < x+width; i++){
				for(int j = y; j < y+height; j++){
					boolean goodTile;
					int l = k;
					do {
						goodTile = true;
						Tile currentTile = map.getTile(i, j, l);
						if(currentTile.getWall() == TILE_TYPE.NONE){
							//Ставит цвет в зависимости от типа тайла
							if(currentTile.getFloor() == TILE_TYPE.NONE) {
								l++;
								goodTile = false;
								continue;
							}
							if(currentTile.getObject() != null && currentTile.getObject().getType() == EntityType.TREE) {
								bufferMap.setRGBA(i, j, 10, 130, 10, alpha);
								//g.setColor(Color.green.darker());
							}else if(currentTile.getFloor() == TILE_TYPE.WATER){
								bufferMap.setRGBA(i, j, 10, 10, 155, alpha);
								//g.setColor(Color.blue);
							}else {
								bufferMap.setRGBA(i, j, 10, 215, 10, alpha);
								//g.setColor(Color.green);
							}
						}else if(currentTile.getWall() == TILE_TYPE.DIRT){
							bufferMap.setRGBA(i, j, 225, 225, 160, alpha);
							//g.setColor(Color.orange.darker());
						}else{
							bufferMap.setRGBA(i, j, 150, 150, 150, alpha);
							//g.setColor(Color.gray);
						}
						//g.fillRect(i*3, j*3, 3, 3);
					}while(!goodTile && l < depth);
				}
			}
			miniMap[k] = bufferMap.getImage();
		}
	}

	public void render(GameContainer gc, StateBasedGame game, Graphics g){
		g.drawImage(miniMap[map.getZ()],x,y);
		Color selectedTreeColor = new Color(Color.pink);
		selectedTreeColor.a = 0.5f;
		g.setColor(selectedTreeColor);
		ArrayList<Task> treeTasks = TaskManager.getInstance().getTrees();
		for(int i = 0; i < treeTasks.size(); i++) {
			g.fillRect(x + treeTasks.get(i).getTargets().get(0).getX()/32,
					y + treeTasks.get(i).getTargets().get(0).getY()/32, 
					1, 1);
		}

		g.setColor(Color.red);
		for(int i = 0; i < map.getCreatures().size(); i++){
			g.fillRect(x+map.getCreatures().get(i).getX()/WorldMap.tileSize, 
					y+map.getCreatures().get(i).getY()/WorldMap.tileSize, 
					1, 1);
		}

		g.drawRect(map.getCameraX()/WorldMap.tileSize + x,
				map.getCameraY()/WorldMap.tileSize + y,
				Display.getWidth()/WorldMap.tileSize,
				Display.getHeight()/WorldMap.tileSize);
	}

	public boolean click(GameContainer gc) {
		if(!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) return false;
		int clickX = gc.getInput().getMouseX() - x;
		int clickY = gc.getInput().getMouseY() - y;
		if(clickX >= 0 && clickY >= 0 && clickX < map.getWidth() && clickY < map.getHeight()) {
			int leftLimit = Display.getWidth()/2;
			int rightLimit = map.getWidth()*WorldMap.tileSize - Display.getWidth()/2;
			int topLimit = Display.getHeight()/2;
			int bottomLimit = map.getHeight()*WorldMap.tileSize - Display.getHeight()/2;

			map.setCameraX(Math.min(rightLimit, Math.max(leftLimit, clickX*WorldMap.tileSize)) - Display.getWidth()/2);
			map.setCameraY(Math.min(bottomLimit, Math.max(topLimit, clickY*WorldMap.tileSize)) - Display.getHeight()/2);
			return true;
		}
		return false;
	}
}
