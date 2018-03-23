package world;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import ai.TestAI;
import core.DwarfsGame;
import core.GameState;
import entities.Creature;
import entities.EntityType;
import entities.GameObject;
import entities.creatures.TestCreature;
import entities.static_objects.Tree;
import graphics.Renderable;
import util.EntityFactory;
import util.PriorityQueue;
import util.Vertex;
import util.alerts.AlertManager;
import util.path_finding.PathFinderManager;
import util.tasks.BuildingTemplate;
import util.tasks.Task;
import util.tasks.TaskManager;
import world.Tile.TILE_TYPE;
import world.tile_groups.GroupManager;
import world.tile_groups.TileGroup;
import world.zones.Zone;
import world.zones.ZoneManager;

public class WorldMap {
	private volatile Tile[][][] tiles;
	//Тайлы
	private volatile ArrayList<GameObject> allObjects;
	private volatile ArrayList<Creature> creatures;
	//Тут вообще все объекты
	private volatile ArrayList<GameObject> needUpdate;
	//Тут объекты, которые нужно обновлять (растущие деревья, например)
	private volatile GroupManager groupManager;
	private volatile ZoneManager zoneManager;
	private MiniMap miniMap;
	private int cameraX = 0;
	private int cameraY = 0;


	private int[] deltas;

	private int selectionX;
	private int selectionY;
	private boolean selecting;

	private WorldMap(Tile[][][] tiles){
		//Конструктор карты, закидываешь внутрь тайлы (не обязательно заполненные)
		this.tiles = tiles;
		allObjects = new ArrayList<GameObject>(0);
		creatures = new ArrayList<Creature>(0);
		needUpdate = new ArrayList<GameObject>(0);
		selecting = false;
		deltas = new int[50];
		GroupManager.createInstance(this);
		ZoneManager.createInstance(this);
	}

	public GroupManager groupManager() {
		return groupManager;
	}

	public void setGroupManager(GroupManager groupManager) {
		this.groupManager = groupManager;
	}

	public ZoneManager zoneManager() {
		return zoneManager;
	}

	public void setZoneManager(ZoneManager zoneManager) {
		this.zoneManager = zoneManager;
	}

	public MiniMap getMiniMap() {
		return miniMap;
	}

	public void addObject(GameObject object, boolean needsUpdate) {
		allObjects.add(object);
		if(needsUpdate) {
			needUpdate.add(object);
		}
	}

	public void addObject(GameObject object) {
		addObject(object, false);
	}

	public ArrayList<Creature> getCreatures() {
		return creatures;
	}

	public ArrayList<GameObject> getAllObjects(){
		return allObjects;
	}

	public void createMinimap() {
		if(miniMap != null) return;
		miniMap = new MiniMap(this);
	}

	public int getWidth(){
		return tiles.length;
	}

	public int getHeight(){
		return tiles[0].length;
	}

	public int getCameraX() {
		return cameraX;
	}

	public int getCameraY() {
		return cameraY;
	}

	public void setCameraX(int x) {
		cameraX = x;
	}

	public void setCameraY(int y) {
		cameraY = y;
	}

	public Zone getZone(int x, int y, int z) {
		return tiles[x][y][z].getZone();
	}

	public int getGroup(int x, int y, int z){
		//Возвращает группу из тайла по координатам
		if(x < 0 || y < 0 || z < 0 || 
				x >= tiles.length || y >= tiles[0].length || z >= tiles[0][0].length){
			return -3;
		}

		return tiles[x][y][z].getGroup();
	}

	public TileGroup getGroupObject(int x, int y, int z){
		//Возвращает группу из тайла по координатам
		if(x < 0 || y < 0 || z < 0 || 
				x >= tiles.length || y >= tiles[0].length || z >= tiles[0][0].length){
			return null;
		}

		return tiles[x][y][z].getGroupObject();
	}

	public void placeTile(int x, int y, int z, TILE_TYPE wall, TILE_TYPE floor, double moisture){
		//Ставит тайл в координаты, меняет вес тайла, если он непроходим
		tiles[x][y][z] = new Tile(wall, floor, x, y, z);
		if(!tilePassable(x,y,z)) {
			tiles[x][y][z].setWeight(999);
		}else {
			if(moisture >= 0.3) {
				Tree newTree = EntityFactory.generateTree(x*tileSize, y*tileSize, z);
				allObjects.add(newTree);
				//tiles[x][y][z].setObject(newTree);
			}
		}
	}

	public int getWeight(int x, int y, int z){
		//Возвращает вес тайла по координатам (для нахождения пути)
		return tiles[x][y][z].getWeight();
	}

	public Task getFirstTypeTask(int x, int y, int z, int taskType){
		if(x < 0 || y < 0 || z < 0 || 
				x >= tiles.length || y >= tiles[0].length || z >= tiles[0][0].length){
			return null;
		}

		return tiles[x][y][z].getFirstTypeTask(taskType);
	}

	public Tile getTile(int x, int y, int z){
		//Возвращает целый тайл по координатам
		if(x < 0 || y < 0 || z < 0 || 
				x >= tiles.length || y >= tiles[0].length || z >= tiles[0][0].length){
			return null;
		}

		return tiles[x][y][z];
	}

	public TILE_TYPE getWall(int x, int y, int z){
		//Возвращает тип стены
		return tiles[x][y][z].getWall();
	}

	public TILE_TYPE getFloor(int x, int y, int z){
		//Возвращает тип пола
		return tiles[x][y][z].getFloor();
	}

	public synchronized boolean tilePassable(int x, int y, int z){
		//Смотрит, проходим ли тайл (стена пустая и пол не пустой)
		return  x >=0 && y >= 0 && z >= 0 &&
				x < tiles.length && y < tiles[0].length && z < tiles[0][0].length &&
				tiles[x][y][z].tilePassable();
	}

	public void addCreature(Creature c){
		this.allObjects.add(c);
		this.creatures.add(c);
	}

	public void requestUpdate(GameObject object) {
		if(needUpdate.contains(object)) return;
		needUpdate.add(object);
	}

	public void update(GameContainer gc, DwarfsGame game, int delta){

		if(gc.getInput().isKeyPressed(Input.KEY_E)) {
			GameState.setAction(GameState.getAction() + 1);
		}else if(gc.getInput().isKeyPressed(Input.KEY_Q)) {
			GameState.setAction(GameState.getAction() - 1);
		}else if(gc.getInput().isKeyPressed(Input.KEY_TAB)) {
			AlertManager.getInstance().toggleShow();
		}

		cameraControls(gc, game, delta);

		PathFinderManager.getInstance().update();

		for(int i = 0; i < creatures.size(); i++){
			if(creatures.get(i).isDead()) {
				allObjects.remove(creatures.get(i));
				creatures.remove(i);
				i--;
				continue;
			}
			creatures.get(i).update(gc, game, delta);
		}
		for(int i = 0; i < needUpdate.size(); i++){
			if(needUpdate.get(i).isDead()) {
				int x = (int) (needUpdate.get(i).getX()/32);
				int y = (int) (needUpdate.get(i).getY()/32);
				allObjects.remove(needUpdate.get(i));
				needUpdate.remove(i);
				miniMap.updateMiniMap(x,y,1,1);
				i--;
				continue;
			}
			needUpdate.get(i).update(gc, game, delta);
		}

		if(GameState.getAction() == GameState.SPAWN_CITIZEN) {
			if(gc.getInput().isMousePressed(Input.MOUSE_RIGHT_BUTTON)){
				if(gc.getInput().getMouseX() < (tiles.length-1)*3 && gc.getInput().getMouseY() < (tiles[0].length-1)*3
						&& gc.getInput().getMouseY() > 0){

					if(!tilePassable((gc.getInput().getMouseX()+cameraX)/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize,0)){
						return;
					}
					TestCreature test = EntityFactory.generateTestCreature(gc.getInput().getMouseX()+cameraX, 
							gc.getInput().getMouseY()+cameraY, 0);
					TestAI testai= new TestAI();
					test.setAI(testai);
					addCreature(test);
				}
			}
		}else if(GameState.getAction() == GameState.TREE_CUT_TASK) {
			if(gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)){
				if(!selecting) {
					selecting = true;
					selectionX = gc.getInput().getMouseX()+cameraX;
					selectionY = gc.getInput().getMouseY()+cameraY;
				}
			}else {
				if(selecting) {
					//Выбор деревьев
					int selectionStartX = Math.max(0,Math.min(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
					int selectionStartY = Math.max(0,Math.min(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));
					int selectionEndX = Math.min(tiles.length-1,Math.max(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
					int selectionEndY = Math.min(tiles[0].length-1,Math.max(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));

					for(int i = selectionStartX; i <= selectionEndX; i++) {
						for(int j = selectionStartY; j <= selectionEndY; j++) {
							if(tiles[i][j][0].getObject() != null && tiles[i][j][0].getObject().getType() == EntityType.TREE) {
								if(tiles[i][j][0].getFirstTypeTask(Task.TREE_CUT) != null) {
									continue;
								}
								TaskManager.getInstance().addTree(tiles[i][j][0]);
							}
						}
					}

					selecting = false;
					selectionX = -1;
					selectionY = -1;
				}
			}
		}else if(GameState.getAction() == GameState.MINE_TILE_TASK) {
			if(gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)){
				if(!selecting) {
					selecting = true;
					selectionX = gc.getInput().getMouseX()+cameraX;
					selectionY = gc.getInput().getMouseY()+cameraY;
				}
			}else {
				if(selecting) {
					//Выбор стен
					int selectionStartX = Math.max(0,Math.min(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
					int selectionStartY = Math.max(0,Math.min(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));
					int selectionEndX = Math.min(tiles.length-1,Math.max(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
					int selectionEndY = Math.min(tiles[0].length-1,Math.max(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));

					for(int i = selectionStartX; i <= selectionEndX; i++) {
						for(int j = selectionStartY; j <= selectionEndY; j++) {
							if(tiles[i][j][0].getWall() != TILE_TYPE.NONE) {
								if(tiles[i][j][0].getFirstTypeTask(Task.TILE_MINE) != null) {
									continue;
								}
								TaskManager.getInstance().addTile(tiles[i][j][0]);
							}
						}
					}

					selecting = false;
					selectionX = -1;
					selectionY = -1;
				}
			}
		}else if(GameState.getAction() == GameState.ADD_STORAGE_ZONE_TASK) {
			if(gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)){
				if(!selecting) {
					selecting = true;
					selectionX = gc.getInput().getMouseX()+cameraX;
					selectionY = gc.getInput().getMouseY()+cameraY;
				}
			}else {
				if(selecting) {
					//Выбор зоны хранилища
					int selectionStartX = Math.max(0,Math.min(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
					int selectionStartY = Math.max(0,Math.min(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));
					int selectionEndX = Math.min(tiles.length-1,Math.max(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
					int selectionEndY = Math.min(tiles[0].length-1,Math.max(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));

					boolean success = true;
					outer: for(int i = selectionStartX; i <= selectionEndX; i++) {
						for(int j = selectionStartY; j <= selectionEndY; j++) {
							if(!tilePassable(i,j,0) || tiles[i][j][0].getZone() != null) {
								success = false;
								break outer;
							}
						}
					}

					if(success) {
						zoneManager.addStorage(selectionStartX, selectionStartY, 
								selectionEndX - selectionStartX+1, 
								selectionEndY - selectionStartY+1);
					}

					selecting = false;
					selectionX = -1;
					selectionY = -1;
				}
			}
		}else if(GameState.getAction() == GameState.ADD_BUILDING_WALL) {
			if(gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)){
				if(!selecting) {
					selecting = true;
					selectionX = gc.getInput().getMouseX()+cameraX;
					selectionY = gc.getInput().getMouseY()+cameraY;
				}
			}else {
				if(selecting) {
					//Выбор зоны постройки
					int selectionStartX = Math.max(0,Math.min(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
					int selectionStartY = Math.max(0,Math.min(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));
					int selectionEndX = Math.min(tiles.length-1,Math.max(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
					int selectionEndY = Math.min(tiles[0].length-1,Math.max(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));

					for(int i = selectionStartX; i <= selectionEndX; i++) {
						for(int j = selectionStartY; j <= selectionEndY; j++) {
							if(!tilePassable(i,j,0) || tiles[i][j][0].getZone() != null || tiles[i][j][0].getFirstTypeTask(Task.WALL_BUILD) != null) {
								continue;
							}
							TaskManager.getInstance().addWallBuildingSpot(tiles[i][j][0], BuildingTemplate.LOG_WALL);
						}
					}

					selecting = false;
					selectionX = -1;
					selectionY = -1;
				}
			}
		}

		for(int i = 1; i < deltas.length; i++) {
			deltas[i-1] = deltas[i];
		}
		deltas[deltas.length-1] = delta;
	}

	private float cameraMovementBorder = 75;
	private int oldX = -1;
	private int oldY = -1;

	private void cameraControls(GameContainer gc, DwarfsGame game, int delta) {
		if(!gc.hasFocus()) return;

		if(miniMap.click(gc)) return;

		cameraMouseControls(gc,game,delta);


		if(gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
			cameraX += delta;
		}
		if(gc.getInput().isKeyDown(Input.KEY_LEFT)) {
			cameraX -= delta;
		}

		if(gc.getInput().isKeyDown(Input.KEY_DOWN)) {
			cameraY += delta;
		}
		if(gc.getInput().isKeyDown(Input.KEY_UP)) {
			cameraY -= delta;
		}

		if(cameraX + Display.getWidth() > getWidth()*tileSize) {
			cameraX = getWidth()*tileSize - Display.getWidth();
		}
		if(cameraX < 0) {
			cameraX = 0;
		}

		if(cameraY + Display.getHeight() > getHeight()*tileSize) {
			cameraY = getHeight()*tileSize - Display.getHeight();
		}
		if(cameraY < 0) {
			cameraY = 0;
		}

	}

	private void cameraMouseControls(GameContainer gc, DwarfsGame game, int delta) {
		float cameraMovementBorder = this.cameraMovementBorder;
		if(GameState.mouseInGui()) {
			cameraMovementBorder -= 60;
		}

		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();

		if(!gc.getInput().isMouseButtonDown(Input.MOUSE_MIDDLE_BUTTON)) {
			oldX = -1;
			oldY = -1;
			if(mouseX <= cameraMovementBorder) {
				cameraX -= delta*(1-mouseX/cameraMovementBorder);
			}

			if(mouseY <= cameraMovementBorder) {
				cameraY -= delta*(1-mouseY/cameraMovementBorder);
			}

			if(mouseX >= Display.getWidth()-cameraMovementBorder) {
				int mouseXRight = (int) (Display.getWidth()-cameraMovementBorder - mouseX);
				cameraX -= delta*(mouseXRight/cameraMovementBorder);
			}

			if(mouseY >= Display.getHeight()-cameraMovementBorder) {
				int mouseYRight = (int) (Display.getHeight()-cameraMovementBorder - mouseY);
				cameraY -= delta*(mouseYRight/cameraMovementBorder);
			}
		}else {
			if(oldX == -1 || oldY == -1) {
				oldX = mouseX;
				oldY = mouseY;
			}else {

				int xDist = Math.max(-200, Math.min(mouseX - oldX, 200));
				int yDist = Math.max(-200, Math.min(mouseY - oldY, 200));

				cameraX -= xDist;
				cameraY -= yDist;


				oldX = mouseX;
				oldY = mouseY;
			}

		}
	}

	public void render(GameContainer gc, StateBasedGame game, Graphics g){

		g.translate(-cameraX, -cameraY);
		//Дальше карта

		int renderXStart = Math.max(0,cameraX - 32)/32;
		int renderXEnd = Math.min(getWidth()*32,cameraX + Display.getWidth()+32)/32;

		int renderYStart = Math.max(0,cameraY - 32)/32;
		int renderYEnd = Math.min(getHeight()*32,cameraY + Display.getHeight()+32)/32;



		PriorityQueue<Renderable> renderQueue = new PriorityQueue<Renderable>();

		for(int i = renderXStart; i < renderXEnd; i++) {
			for(int j = renderYStart; j < renderYEnd; j++) {
				renderQueue.add(tiles[i][j][0], tiles[i][j][0].getPriority());
			}
		}

		for(int i = 0; i < allObjects.size(); i++) {
			if(allObjects.get(i).getX() > cameraX-allObjects.get(i).getWidth() &&
					allObjects.get(i).getX() < cameraX+Display.getWidth()+allObjects.get(i).getWidth() &&
					allObjects.get(i).getY() > cameraY-allObjects.get(i).getHeight() &&
					allObjects.get(i).getY() < cameraY+Display.getHeight()+allObjects.get(i).getHeight()) {
				renderQueue.add(allObjects.get(i), allObjects.get(i).getPriority());
			}
		}

		for(int i = 0; i < zoneManager.getStorages().size(); i++) {
			renderQueue.add(zoneManager.getStorages().get(i), zoneManager.getStorages().get(i).getPriority());
		}

		while(renderQueue.size() > 0) {
			renderQueue.poll().render(gc, game, g);
		}

		for(int i = 0; i < creatures.size(); i++) {
			/*
			if(creatures.get(i).getX() > cameraX-16 &&
					creatures.get(i).getX() < cameraX+Display.getWidth()+16 &&
					creatures.get(i).getY() > cameraY-16 &&
					creatures.get(i).getY() < cameraY+Display.getHeight()+16) {
				creatures.get(i).render(gc, game, g);
			}
			 */

			if(creatures.get(i).getAI().getPath() != null) {
				g.setColor(Color.cyan);
				ArrayList<Vertex> path = creatures.get(i).getAI().getPath().getPoints();
				for(int j = 0; j < path.size()-1; j++) {
					g.drawLine(path.get(j).getX(), path.get(j).getY(), path.get(j+1).getX(), path.get(j+1).getY());
					g.drawRect(path.get(j+1).getX()-4, path.get(j+1).getY()-4, 9, 9);
				}
			}
		}


		g.translate(cameraX, cameraY);
		//Дальше GUI

		Color selectionColor = new Color(Color.yellow);
		selectionColor.a = 0.6f;
		g.setColor(selectionColor);

		if(selecting) {
			int selectionStartX = Math.max(0,Math.min(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
			int selectionStartY = Math.max(0,Math.min(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));
			int selectionEndX = Math.min(tiles.length-1,Math.max(selectionX/tileSize, (gc.getInput().getMouseX()+cameraX)/tileSize));
			int selectionEndY = Math.min(tiles[0].length-1,Math.max(selectionY/tileSize, (gc.getInput().getMouseY()+cameraY)/tileSize));

			for(int i = selectionStartX; i <= selectionEndX; i++) {
				for(int j = selectionStartY; j <= selectionEndY; j++) {
					int wallCorrection = 0;
					if(tiles[i][j][0].getWall() != TILE_TYPE.NONE) {
						wallCorrection = -16;
					}
					g.fillRect(i*tileSize-cameraX, j*tileSize-cameraY + wallCorrection, tileSize, tileSize);
				}
			}

		}else {
			int i = (gc.getInput().getMouseX()+cameraX)/tileSize;
			int j = (gc.getInput().getMouseY()+cameraY)/tileSize;
			int wallCorrection = 0;
			if(j < 256 && i < 256 && tiles[i][j][0].getWall() != TILE_TYPE.NONE) {
				wallCorrection = -16;
			}
			g.fillRect(i*tileSize-cameraX, j*tileSize-cameraY + wallCorrection, tileSize, tileSize);
		}

		g.setColor(Color.white);
		g.drawString(GameState.getActionName(), 1000, 370);

		g.drawString("PathFinders working: " + PathFinderManager.getInstance().getCalculatingPathFinders() + 
				"/" + PathFinderManager.getAllPathFinders(), 1000, 400);
		g.drawString("Requests waiting: " + PathFinderManager.getInstance().requestNumber(), 1000, 430);
		g.drawString("Villagers: " + creatures.size(), 1000, 460);
		g.drawString("Selected tile: " + gc.getInput().getMouseX()/3 + " " + gc.getInput().getMouseY()/3, 1000, 490);
		g.drawString("Processors: " + Runtime.getRuntime().availableProcessors(), 1000, 520);

		AlertManager aM = AlertManager.getInstance();

		if(aM.showingLog()) {
			g.drawString("Log:", 50, 120);
			for(int i = 0; i < aM.getLog().size(); i++) {
				g.drawString(aM.getLog().get(i), 50, 140+i*20);
			}
		}

		g.setColor(Color.gray);
		for(int i = 0; i < deltas.length-1; i++) {
			g.drawLine(1000+i*5, 600-deltas[i], 1000+(i+1)*5, 600-deltas[i+1]);
		}

		miniMap.render(gc, game, g);
	}

	private static volatile WorldMap mapSingleton;
	public static final int tileSize = 32;
	
	public static WorldMap createMap(Tile[][][] tileMap) {
		mapSingleton = new WorldMap(tileMap);
		return mapSingleton;
	}

	public static synchronized WorldMap getMap() {
		return mapSingleton;
	}

	public static GroupManager getGroupManager() {
		return mapSingleton.groupManager();
	}

	public static ZoneManager getZoneManager() {
		return mapSingleton.zoneManager();
	}
}
