package util.path_finding;

import ai.AI;
import util.Vertex3i;

public class PathFinderRequest {
	private int x;
	private int y;
	private int z;
	private int BaseCost;
	private int NoneCost;
	private Vertex3i from;
	private AI requester;
	
	public PathFinderRequest(int x, int y, int z, int BaseCost, int NoneCost, Vertex3i from, AI requester) {
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	public int getZ() {
		return z;
	}
	
	public int getBaseCost() {
		return BaseCost;
	}
	
	public int getNoneCost() {
		return NoneCost;
	}
	
	public Vertex3i getFrom() {
		return from;
	}
	
	public AI getRequester() {
		return requester;
	}
}
