package graphics;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class TextureStorage {
	private static Map<String, Image> images;
	private static Map<String, SpriteSheet> tileSets;
	
	public static void init() {
		images = new HashMap<String, Image>();
		tileSets = new HashMap<String, SpriteSheet>();
		
		try {
			tileSets.put("tiles", loadSpriteSheet("assets/environment/tiles.png", 32, 32));
			tileSets.put("trees", loadSpriteSheet("assets/environment/trees.png", 64, 96));
			tileSets.put("citizen_body", loadSpriteSheet("assets/citizen/animations.png", 32, 32));
			tileSets.put("citizen_head_m", loadSpriteSheet("assets/citizen/male_hairs.png", 32, 32));
			tileSets.put("citizen_head_f", loadSpriteSheet("assets/citizen/female_hairs.png", 32, 32));
			tileSets.put("logs", loadSpriteSheet("assets/items/logSet.png", 32, 32));
			tileSets.put("dialogs", loadSpriteSheet("assets/gui/dialogAnim.png", 32, 32));
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public static SpriteSheet loadSpriteSheet(String path, int tw, int th) throws SlickException{
		return new SpriteSheet(loadImage(path), tw, th);
	}
	
	public static Image loadImage(String path) throws SlickException{
		return new Image(path, false, Image.FILTER_NEAREST);
	}
	
	public final static Image getSpriteTile(String name, int x, int y){
		return tileSets.get(name).getSubImage(x, y);
	}
	
	public final static Image getSpriteSheet(String name){
		return tileSets.get(name);
	}
	
	public final static Image getImage(String name){
		return images.get(name);
	}
	
	public final static Image combineImages(Image one, Image two) {
		
			ImageBuffer complexSprite = new ImageBuffer(Math.max(one.getWidth(), two.getWidth()), Math.max(one.getHeight(), two.getHeight()));
			for(int i = 0; i < one.getWidth(); i++) {
				for(int j = 0; j < one.getHeight(); j++) {
					Color c = one.getColor(i, j);
					int a = c.getAlpha();
					if(a == 0) {
						continue;
					}
					int r = c.getRed();
					int g = c.getGreen();
					int b = c.getBlue();
					complexSprite.setRGBA(i, j, r, g, b, a);
				}
			}
			
			for(int i = 0; i < two.getWidth(); i++) {
				for(int j = 0; j < two.getHeight(); j++) {
					Color c = two.getColor(i, j);
					int a = c.getAlpha();
					if(a == 0) {
						continue;
					}
					int r = c.getRed();
					int g = c.getGreen();
					int b = c.getBlue();
					complexSprite.setRGBA(i, j, r, g, b, a);
				}
			}
			
			return complexSprite.getImage();
		
	}
}
