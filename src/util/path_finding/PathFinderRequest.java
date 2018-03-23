package util.path_finding;

import ai.AI;
import util.Vertex;

public class PathFinderRequest {
	private int x;
	private int y;
	private int BaseCost;
	private int NoneCost;
	private Vertex from;
	private AI requester;
	
	public PathFinderRequest(int x, int y, int BaseCost, int NoneCost, Vertex from, AI requester) {
		this.x = x;
		this.y = y;
		this.BaseCost = BaseCost;
		this.NoneCost = NoneCost;
		this.from = from;
		this.requester = requester;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getBaseCost() {
		return BaseCost;
	}
	
	public int getNoneCost() {
		return NoneCost;
	}
	
	public Vertex getFrom() {
		return from;
	}
	
	public AI getRequester() {
		return requester;
	}
}
