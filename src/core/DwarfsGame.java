package core;
import java.util.Random;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.state.StateBasedGame;

import graphics.TextureStorage;
import sound.SoundSystem;

public class DwarfsGame extends StateBasedGame{
	public static Random rnd = new Random(System.currentTimeMillis());

	static final String[] namePool1 = {"Sexy", "Naughty", "Crazy", "Shitty", "Dirty"};
	static final String[] namePool2 = {"castle", "fortress", "village", "tribemania"};
	public static String gamename = namePool1[rnd.nextInt(namePool1.length)] + " " + namePool2[rnd.nextInt(namePool2.length)];
	public static boolean debug = true;
	
	public static int xSize;
	public static int ySize;
	
	public static boolean mouseOutside = false;
	
	public DwarfsGame(String name) {
		super(name);
		
	}

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		TextureStorage.init();
		SoundSystem.getInstance().init();
		this.addState(new GameState());
		this.addState(new MapGenTestingState());
		goToState(gc, GAME);
	}
	
	public static void main(String[] args) {
		AppGameContainer appgc;
		try{
			//ѕопробовал рендерер помен€ть, вроде производительность улучшилась
			Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
			xSize = 1300;//Display.getDesktopDisplayMode().getWidth();
			ySize = 900;//Display.getDesktopDisplayMode().getHeight();
			appgc = new AppGameContainer(new DwarfsGame(gamename));
			appgc.setDisplayMode(xSize, ySize, false);
			appgc.setTargetFrameRate(60);
			appgc.setVSync(true);
			appgc.start();
		}catch(SlickException e){
			e.printStackTrace();
		}
	}
	
	public void goToState(GameContainer gc, int id){
		((ExtendedState)this.getState(id)).clear(gc, this);
		((ExtendedState)this.getState(id)).load(gc, this);
		this.enterState(id);
	}

	
	public final static int GAME = 0;
	public final static int TEST = 1;
}
