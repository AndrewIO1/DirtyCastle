package sound;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import core.DwarfsGame;
import world.WorldMap;

public class SoundSystem {
	private static SoundSystem system;

	public static SoundSystem getInstance() {
		if(system == null) {
			system = new SoundSystem();
		}
		return system;
	}

	private Map<String, Sound[]> sounds;

	public SoundSystem() {
		sounds = new HashMap<String, Sound[]>();
	}

	public void init() {
		Sound[] soundPack = new Sound[6];
		try {
			for(int i = 0; i < soundPack.length; i++) {

				soundPack[i] = new Sound("assets/sounds/cutTree" + (i+1) + ".ogg");

			}
			//soundPack[5] = new Sound("assets/sounds/Take_it_boy.wav");
		} catch (SlickException e) {
			
			e.printStackTrace();
		}

		sounds.put("treeCut", soundPack);

	}
	
	public void playSound(String name, float x, float y, float z) {
		Sound toPlay = sounds.get(name)[DwarfsGame.rnd.nextInt(sounds.get(name).length)];
		float distX = (x-WorldMap.getMap().getCameraX()-Display.getWidth()/2);
		float distY = (y-WorldMap.getMap().getCameraY()-Display.getHeight()/2);
		float dist = (distX*distX + distY*distY)/2000000f;
		
		if(dist >= 1) return;
		
		toPlay.playAt(0.6f, 0.2f*(1f-dist), distX/1000f, distY/1000f, z);
	}
}
