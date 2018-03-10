package AUR.util.knd;

import adf.agent.precompute.PrecomputeData;
import static firesimulator.simulator.Simulator.GAMMA;
import firesimulator.world.Building;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import rescuecore2.worldmodel.EntityID;

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
		for(AURAreaGraph ag : this.wsg.areas.values()) {
			if(ag.isBuilding()) {
				waterCooling(ag.getBuilding().fireSimBuilding);
			}
		}
	}
	
	private void waterCooling(AURFireSimBuilding b) {
		double lWATER_COEFFICIENT = (b.getEstimatedEnergy() > 0 && b.getEstimatedEnergy() < 4 ? AURConstants.FireSim.WATER_COEFFICIENT : AURConstants.FireSim.WATER_COEFFICIENT * AURConstants.FireSim.GAMMA);
		if (b.getWaterQuantity() > 0) {
			double dE = b.getEstimatedTemperature() * b.getCapacity();
			if (dE <= 0) {
				return;
			}
			double effect = b.getWaterQuantity() * lWATER_COEFFICIENT;
			double consumed = b.getWaterQuantity();
			if (effect > dE) {
				double pc = 1 - ((effect - dE) / effect);
				effect *= pc;
				consumed *= pc;
			}
			b.setWaterQuantity(b.getWaterQuantity() - consumed);
			b.setEstimatedEnergy(b.getEstimatedEnergy() - effect);
		}
	}
	
	private void updateGrid() {
		
	}
	
	private void exchangeBuilding() {
		for (AURAreaGraph ag : wsg.areas.values()) {
			if(ag.isBuilding() && ag.isOnFire()) {
				AURFireSimBuilding b = ag.getBuilding().fireSimBuilding;
				exchangeWithAir(b);
			}
		}
		
		for (AURAreaGraph ag : wsg.areas.values()) {
			if(ag.isBuilding()) {
				AURFireSimBuilding b = ag.getBuilding().fireSimBuilding;
				b.tempVar = b.getRadiationEnergy();
			}
		}
		for (AURAreaGraph ag : wsg.areas.values()) {
			if(ag.isBuilding()  && ag.isOnFire()) {
				AURFireSimBuilding b = ag.getBuilding().fireSimBuilding;
				double radEn = b.tempVar;
				
				
				if(b.connections != null) {
					for(AURBuildingConnection bc : b.connections) {

						AURFireSimBuilding cb = wsg.getAreaGraph(new EntityID(bc.toID)).getBuilding().fireSimBuilding;
						double oldEnergy = cb.getEstimatedEnergy();
						double connectionValue = bc.weight;
						double a = radEn * connectionValue;
						double sum = oldEnergy + a;
						cb.setEstimatedEnergy(sum);
					}
				}
				
				b.setEstimatedEnergy(b.getEstimatedEnergy() - radEn);
			}
		}
	}
	
	private void exchangeWithAir(AURFireSimBuilding building) {
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
