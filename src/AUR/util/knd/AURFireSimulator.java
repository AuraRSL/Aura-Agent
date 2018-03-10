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
		for(AURAreaGraph ag : wsg.areas.values()) {
			if(ag.isBuilding() == true) {
				AURFireSimBuilding b = ag.getBuilding().fireSimBuilding;
				if(b.getEstimatedTemperature() >= b.getIgnitionPoint() && b.getEstimatedFuel() > 0 && b.inflammable() == true) {
					double consumed = b.getConsum();
					if(consumed > b.getEstimatedFuel()) {
					    consumed = b.getEstimatedFuel();
					}
					b.setEstimatedEnergy(b.getEstimatedEnergy() + consumed);
					b.setEstimatedFuel(b.getEstimatedFuel() - consumed);
				}
			}
		}
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
