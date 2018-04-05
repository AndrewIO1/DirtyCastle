package util.path_finding;

import java.util.ArrayList;
import java.util.List;

import ai.AI;
import util.Vertex3i;

public class PathFinderManager {
	private static PathFinderManager instance;
	
	private static final int pathFinderAmount = 3;
	
	public static PathFinderManager getInstance() {
		if(instance == null) {
			instance = new PathFinderManager();
		}
		
		return instance;
	}
	
	public static int getAllPathFinders() {
		return pathFinderAmount;
	}
	
	private PathFinder[] finders;
	private List<PathFinderRequest> queue;
	
	private PathFinderManager() {
		queue = new ArrayList<PathFinderRequest>();
		finders = new PathFinder[pathFinderAmount];
		for(int i = 0; i < pathFinderAmount; i++) {
			finders[i] = new PathFinder();
			finders[i].start();
		}
	}
	
	public void requestPath(int x, int y, int z, int BaseCost, int NoneCost, Vertex3i from, AI requester) {
		queue.add(new PathFinderRequest(x,y,z,BaseCost,NoneCost,from,requester));
	}
	
	public void update() {
		for(int i = 0; i < pathFinderAmount; i++) {
			if(queue.size() == 0) return;
			if(!finders[i].isCalculating()) {
				finders[i].requestPath(queue.get(0));
				queue.remove(0);
			}
		}
	}
	
	public int getCalculatingPathFinders() {
		int calculating = 0;
		for(int i = 0; i < pathFinderAmount; i++) {
			if(finders[i].isCalculating()) {
				calculating++;
			}
		}
		return calculating;
	}
	
	public int requestNumber() {
		return queue.size();
	}
	
	public void cancelRequest(AI requester) {
		for(int i = 0; i < finders.length; i++) {
			if(finders[i].getRequester() == requester) {
				finders[i].cancelTask();
				return;
			}
		}
		for(int i = 0; i < queue.size(); i++) {
			if(queue.get(i).getRequester() == requester) {
				queue.remove(i);
				requester.setPath(null);
				return;
			}
		}
	}
}
