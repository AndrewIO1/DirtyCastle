package util;

import core.DwarfsGame;
import entities.creatures.TestCreature;
import entities.loot.Log;
import entities.static_objects.LogBig;
import entities.static_objects.Tree;
import util.tasks.TaskManager;
import world.WorldMap;

public class EntityFactory {
	private static int testCreatureCounter = 0;
	private static int treeCounter = 0;
	private static WorldMap map;
	
	private EntityFactory() {
		throw new AssertionError();
		//��������, ����� ������ ���� ���������� �������
	}
	
	public static void init() {
		map = WorldMap.getMap();
	}
	
	private static boolean check(int x, int y, int z, String objectName) {
		if(WorldMap.getMap() == null) {
			System.out.println("Map is not generated, failed to spawn  + objectName");
			return false;
		}
		if(map == null) {
			System.out.println("Factory is not initialized, failed to spawn " + objectName);
			return false;
		}
		if(x < 0 || y < 0 || x >= map.getWidth()*WorldMap.tileSize || y >= map.getHeight()*WorldMap.tileSize) {
			System.out.println("Spawning outside the map, failed to spawn " + objectName);
			return false;
		}
		return true;
	}
	
	public static TestCreature generateTestCreature(int x, int y, int z) {
		testCreatureCounter++;
		if(!check(x,y,z,"TEST " + testCreatureCounter)) {
			return null;
		}
		TestCreature.Builder creature = new TestCreature.Builder("TEST " + testCreatureCounter);
		creature.x(x).y(y).z(z);
		creature.width(32).height(32);
		creature.x_anchor(0.5f).y_anchor(0.5f);
		creature.map(map);
		creature.mass(1);
		return creature.build();
	}
	
	public static Tree generateTree(int x, int y, int z) {
		treeCounter++;
		if(!check(x,y,z,"Tree " + treeCounter)) {
			return null;
		}
		Tree.Builder tree = new Tree.Builder("Tree " + treeCounter);
		tree.x(x+16).y(y+16).z(z);
		tree.width(64).height(96);
		tree.x_anchor(0.5f).y_anchor(0.85f);
		tree.map(map);
		tree.variant(DwarfsGame.rnd.nextInt(6));
		return tree.build();
	}
	
	public static void spawnLogBig(Tree tree) {
		if(tree == null) return;
		map.addObject(new LogBig.Builder(tree).build());
	}
	
	public static void spawnLog(LogBig sawn) {
		Log loot = new Log.Builder(sawn).build();
		TaskManager.getInstance().addDroppedItem(loot);
		sawn.getHost().addLoot(loot);
	}
}
