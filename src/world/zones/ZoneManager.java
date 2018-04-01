package world.zones;

import java.util.ArrayList;
import java.util.List;

import entities.MovableObject;
import world.WorldMap;

public class ZoneManager {
	private List<StorageZone> storages;
	private WorldMap map;
	
	public static void createInstance(WorldMap map) {
		if(map == null) {
			System.out.println("No map to attach to");
			return;
		}
		map.setZoneManager(new ZoneManager(map));
	}
	
	private ZoneManager(WorldMap map) {
		storages = new ArrayList<StorageZone>();
		this.map = map;
	}
	
	public void addStorage(int x, int y, int width, int height) {
		storages.add(new StorageZone(x,y,width,height));
	}
	
	public StorageZone findStorage(MovableObject item) {
		if(storages == null || storages.size() == 0){
			return null;
		}
		for(int i = 0; i < storages.size(); i++) {
			if(map.getGroup((int)item.getX()/WorldMap.tileSize, (int)item.getY()/WorldMap.tileSize, 0)
					   != map.getGroup(storages.get(i).getX(),storages.get(i).getY(),0)) continue;
			if(storages.get(i).isItemAllowed(item) && storages.get(i).getSpace() > 0) {
				
				return storages.get(i);
			}
		}
		return null;
	}
	
	public List<StorageZone> getStorages(){
		return storages;
	}
}
