package AUR.util.knd;

public class AUREdge {

	public AURNode A;
	public AURNode B;
	public int weight = 0;
	public AURAreaGraph areaGraph;

	public AUREdge(AURNode A, AURNode B, int weight, AURAreaGraph areaGraph) {
		this.A = A;
		this.B = B;
		this.weight = weight;
		this.areaGraph = areaGraph;
	}

	public AURNode nextNode(AURNode from) {
		if (from == A) {
			return B;
		} else if (from == B) {
			return A;
		}
		return null;
	}

	public AURAreaGraph getNextAreaGraph(AURNode fromNode) {
		AURNode toNode = nextNode(fromNode);
		if (toNode.ownerArea1 == areaGraph) {
			return toNode.ownerArea2;
		}
		return toNode.ownerArea1;
	}

	public double getPriority() {
		double result = 0;
		double w = weight / 100;
		result += w;
		AURAreaGraph ag = areaGraph;
		boolean isSmallOrExtraSmall = (ag.isExtraSmall() || ag.isSmall());
		
		if(isSmallOrExtraSmall == true && ag.isAlmostConvex() == false) {
			result += w * 2;
		}
		
		if(ag.isExtraSmall()) {
			result += w * 10;
		}
		
		if(ag.isBuilding()) {
			result += w * 100;
			if(ag.getBuilding().fireSimBuilding.isOnFire()) {
				result += w * 200;
			}
		}
		
		if(isSmallOrExtraSmall && ag.isBuildingNeighbour() == true) {
			result += w * 40;
		}
		
		result += 3 * (1 - ((double) Math.min(500, ag.noSeeTime()) / 500)) * w;
		
		if(ag.isPassed()) {
			result *= 1.1;
		}
		
		return result;
	}
	
	public double getNoBlockadePriority() {

		switch(this.areaGraph.wsg.ai.me().getStandardURN()) {
			case FIRE_BRIGADE:
			case AMBULANCE_TEAM: {
				return getPriority();
			}
			case POLICE_FORCE: {
				double p = getPriority();;
				if(this.areaGraph.isPassed()) {
					return p * 1.5;
				}
			}
		}
		
		return getPriority();
	}
	
}
