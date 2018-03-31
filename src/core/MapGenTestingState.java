package core;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import util.SimplexNoise;
import world.DiagramMap;
import world.Edge;

public class MapGenTestingState extends ExtendedState{
	private static final boolean edgeRender = false;
	
	int z = 16;
	int seed = 4;
	float scale = 3;
	int sX = 32;
	int sY = 32;

	Color[] bColor = {Color.blue.darker(), Color.green.darker(), Color.darkGray, Color.cyan.darker(), Color.yellow.darker(), Color.black};

	SimplexNoise simplex = new SimplexNoise(seed);
	DiagramMap map = new DiagramMap(simplex);

	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void customLoad(GameContainer gc, StateBasedGame game) {
		map.generate(0,0, 1);
	}

	@Override
	public void clear(GameContainer gc, StateBasedGame game) {

	}

	@Override
	public void customRender(GameContainer gc, StateBasedGame game, Graphics g) {
		g.translate(sX, sY);
		g.scale(scale, scale);

		for(int i = 0; i < 256; i++) {
			for(int j = 0; j < 256; j++) {
				g.setColor(bColor[map.getTile(i, j, z)]);
				g.fillRect(i, j, 1, 1);
			}
		}

		if(edgeRender) {
			for(Edge e : map.getEdges()) {
				if(e.water() == 0) {
					g.setColor(Color.gray);
					g.setLineWidth(1);
				}else {
					g.setColor(Color.blue);
					g.setLineWidth((float) e.water());
				}
				g.drawLine(e.p1().x(), e.p1().y(), e.p2().x(), e.p2().y());
			}
		}
		
		g.setColor(Color.white);
		g.drawString("z: " + z, 300, 20);

	}

	@Override
	public void customUpdate(GameContainer gc, StateBasedGame game, int delta) {
		
		if(gc.getInput().isKeyPressed(Input.KEY_Q)) {
			z--;
		}else if(gc.getInput().isKeyPressed(Input.KEY_E)) {
			z++;
		}
		
		if(z < 0) z = 0;
		else if(z >= map.depth()) z = map.depth()-1;
	}

	@Override
	public int getID() {
		return DwarfsGame.TEST;
	}

}
