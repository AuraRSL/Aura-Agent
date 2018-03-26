package AUR.util.knd;

import java.util.ArrayList;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURFireJobValueSetter {
	
	public ArrayList<AURFireJob> jobs = null;
	public AURWorldGraph wsg = null;

	public AURFireJobValueSetter(AURWorldGraph wsg) {
		this.wsg = wsg;
		jobs = new ArrayList<>();
		
		addAllExtinguishJobs();
	}
	
	private void addAllExtinguishJobs() {
		for(AURAreaGraph ag : wsg.getExtinguishableBuildings()) {
			jobs.add(new AURFireJob(this.wsg, AURFireJob.FIRE_JOB_EXTINGUISH, ag));
		}
	}
	
	
	private void addAllSearchJobs() {
		for(AURAreaGraph ag : wsg.areas.values()) {
			if(ag.isBuilding() == false) {
				continue;
			}
			if(ag.noSeeTime() <= 0) {
				continue;
			}
			jobs.add(new AURFireJob(this.wsg, AURFireJob.FIRE_JOB_GOTO_PERCEPT, ag));
		}
	}
	
}
