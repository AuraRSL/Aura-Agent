package AUR.util.knd;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AUREdgeToSee {
	
	public AURAreaGraph toSeeAreaGraph = null;
	public AURAreaGraph ownerAg = null;
	public double cost = 0;
	public AURNode fromNode = null;
	
	public double standX = 0;
	public double standY = 0;
	

	public AUREdgeToSee(AURAreaGraph ownerAg, AURAreaGraph toSeeAreaGraph, double cost, AURNode fromNode, double standX, double standY) {
		this.ownerAg = ownerAg;
		this.toSeeAreaGraph = toSeeAreaGraph;
		this.cost = cost;
		this.fromNode = fromNode;
		this.standX = standX;
		this.standY = standY;
	}

}
