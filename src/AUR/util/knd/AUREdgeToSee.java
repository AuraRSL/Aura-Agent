package AUR.util.knd;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AUREdgeToSee {
	
	public AURAreaGraph toSeeAreaGraph = null;
	public AURAreaGraph ownerAg = null;
	public int cost = 0;
	public AURNode fromNode = null;
	
	public int standX = 0;
	public int standY = 0;
	

	public AUREdgeToSee(AURAreaGraph ownerAg, AURAreaGraph toSeeAreaGraph, int cost, AURNode fromNode, int standX, int standY) {
		this.ownerAg = ownerAg;
		this.toSeeAreaGraph = toSeeAreaGraph;
		this.cost = cost;
		this.fromNode = fromNode;
		this.standX = standX;
		this.standY = standY;
	}

}
