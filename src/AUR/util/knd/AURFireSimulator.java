package AUR.util.knd;

import adf.agent.precompute.PrecomputeData;

/**
 *
 * @author Alireza Kandeh - 2017 & 2018
 */

public class AURFireSimulator {

	private AURWorldGraph wsg = null;
	public AURWorldAirCells airCells = null;
	public boolean isPrecomputedConnections = false;
	
	public AURFireSimulator(AURWorldGraph wsg) {
		this.wsg = wsg;
		this.airCells = new AURWorldAirCells(wsg);
	}
	
	public void step() {
		burn();
		cool();
		updateGrid();
		exchangeBuilding();
		cool();
	}
	
	private void burn() {
		
	}
	
	private void cool() {
		
	}
	
	private void updateGrid() {
		
	}
	
	private void exchangeBuilding() {
		
	}
	
	public void precompute(PrecomputeData pd) {
		for (AURAreaGraph ag : wsg.areas.values()) {
			if (ag.isBuilding()) {
				ag.getBuilding().fireSimBuilding.precomputeRadiation(pd);
			}
		}
		pd.setBoolean("radiation", true);
	}

	public void resume(PrecomputeData pd) {
		Boolean b = pd.getBoolean("radiation");
		if (b == null || b == false) {

			return;
		}
		this.isPrecomputedConnections = true;
		for (AURAreaGraph ag : wsg.areas.values()) {
			if (ag.isBuilding()) {
				ag.getBuilding().fireSimBuilding.resumeRadiation(pd);
			}
		}
	}

}
