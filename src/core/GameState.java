package core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import gui.Gui;
import world.MapGenerator;
import world.WorldMap;

public class GameState extends ExtendedState{

	public static final int NONE = 0;
	public static final int SPAWN_CITIZEN = 1;
	public static final int TREE_CUT_TASK = 2;
	public static final int MINE_TILE_TASK = 3;
	public static final int ADD_STORAGE_ZONE_TASK = 4;
	public static final int ADD_BUILDING_WALL = 5;
	//сюда новые задания
	public static final int MAX_TASK = 6;

	public static String[] actionName = {"nihuya", "Spawn citizen", "Cut trees", "Digger online", "Place storage", "Building"};

	private static WorldMap map;
	private static Gui gui;
	private static int playerAction;
	
	private static boolean mouseInGui = false;
	private static boolean lbmPressed = false;
	private static boolean miniMapGenerated = false;

	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		//При запуске игры
	}

	public void customLoad(GameContainer gc, StateBasedGame game){
		//При переходе в State
		MapGenerator.generate(512, 464);//512 512

		playerAction = NONE;
		//TestCreature test = new TestCreature("TEST", 64, 64, 0, 3, 3, 0.5f, 0.5f, 1);
		//TestAI testai= new TestAI();
		//testai.setMap(map);
		//test.setAI(testai);
		//map.addCreature(test);
		gc.setClearEachFrame(false);

	}

	public static int getAction() {
		return playerAction;
	}

	public static String getActionName() {
		return actionName[playerAction];
	}

	public static void setAction(int action) {
		if(action < 0) action = MAX_TASK-1;
		if(action >= MAX_TASK) action = 0;
		playerAction = action;
	}

	public void clear(GameContainer gc, StateBasedGame game){
		//Очищение переменных
		map = null;
	}

	@Override
	public void customRender(GameContainer gc, StateBasedGame game, Graphics g) {
		//long time = System.currentTimeMillis();

		try {
			if(MapGenerator.generating() || !miniMapGenerated) {
				g.setColor(Color.black);
				g.fillRect(300, 390, 400, 40);
				float p = MapGenerator.getProgress();
				float mP = MapGenerator.getMaxProgress();
				g.setColor(Color.green.darker());
				g.fillRect(300, 390, 400f*p/mP, 40);
				g.setColor(Color.white);
				g.drawString(MapGenerator.getStatus() + MapGenerator.getProgress() + "/" + MapGenerator.getMaxProgress(), 310, 400);
				return;
			}
			map.render(gc, game, g);
			gui.render(gc, game, g);
		}catch(Exception e) {
			try {
				PrintWriter error = new PrintWriter("error-log.txt", "UTF-8");
				e.printStackTrace(error);
				error.close();
				
			} catch (FileNotFoundException e1) {
				
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				
				e1.printStackTrace();
			}
			gc.exit();
		}


		//System.out.println("RenderTime: " + (System.currentTimeMillis()-time));
	}

	@Override
	public void customUpdate(GameContainer gc, StateBasedGame game, int delta) {

		try {
			if(MapGenerator.generating()) {
				return;
			}else if(!miniMapGenerated){
				map = WorldMap.getMap();
				gui = new Gui();
				map.createMinimap();
				miniMapGenerated = true;
			}
			mouseInGui = false;
			lbmPressed = gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON);
			gui.update(gc, game, delta);
			map.update(gc, (DwarfsGame)game, delta);
		}catch(Exception e) {
			try {
				PrintWriter error = new PrintWriter("error-log.txt", "UTF-8");
				e.printStackTrace(error);
				error.close();
				
			} catch (FileNotFoundException e1) {
				
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				
				e1.printStackTrace();
			}
			gc.exit();
		}

	}

	@Override
	public int getID() {
		return DwarfsGame.GAME;
	}
	
	public static boolean lbmClicked(boolean consumeEvent) {
		boolean result = lbmPressed;
		if(consumeEvent) {
			lbmPressed = false;
		}
		return result;
	}
	
	public static void mouseEnteredGui() {
		mouseInGui = true;
	}
	
	public static boolean mouseInGui() {
		return mouseInGui;
	}

}
