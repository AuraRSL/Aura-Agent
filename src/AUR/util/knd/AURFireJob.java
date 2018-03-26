package AUR.util.knd;

import adf.agent.action.Action;
import adf.agent.action.fire.ActionExtinguish;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURFireJob {
	
	public final static int FIRE_JOB_GOTO_PERCEPT = 0;
	public final static int FIRE_JOB_EXTINGUISH = 1;
	public final static int FIRE_JOB_GOTO_REFILL = 2;
	public final static int FIRE_JOB_WAIT_FOR_REFILL = 3;
	public final static int FIRE_JOB_REST = 4;
	
	public AURAreaGraph target = null;
	public int job = -1;
	//public double cost = 0;
	public double score = 0;
	
	public AURWorldGraph wsg = null;

	public AURFireJob(AURWorldGraph wsg, int job, AURAreaGraph target) {
		this.wsg = wsg;
		this.job = job;
		this.target = target;
		this.score = getScore();
	}
	
	private double getScore() {
		switch(this.job) {
			case FIRE_JOB_GOTO_PERCEPT: {
				return getGotoPerceptScore();
			}
			case FIRE_JOB_EXTINGUISH: {
				return getExtinguishScore();
			}
			case FIRE_JOB_GOTO_REFILL: {
				return getGotoRefillScore();
			}
			case FIRE_JOB_WAIT_FOR_REFILL: {
				return getWaitForRefillScore();
			}
			case FIRE_JOB_REST: {
				return getRestScore();
			}
		}
		return 0;
	}
	
	
	private double getGotoPerceptScore() {
		return Math.random();
	}
	
	private double getExtinguishScore() {
		return Math.random();
	}
	
	private double getGotoRefillScore() {
		int wq = this.wsg.ai.getWater();
		
		if(wq >= 500) {
			return 0;
		}
		
		AURAreaGraph ag = this.wsg.getAreaGraph(this.wsg.ai.getPosition());
		if(ag == null) {
			return 1;
		}
		
		if(ag.isRefuge() == true) {
			return 0;
		}
		
		return 1;
	}
	
	private double getWaitForRefillScore() {
		
		int wq = this.wsg.ai.getWater();
		
		if(wq >= this.wsg.si.getFireTankMaximum() - 1) {
			return 0;
		}
		
		AURAreaGraph ag = this.wsg.getAreaGraph(this.wsg.ai.getPosition());
		if(ag == null) {
			return 0;
		}
		
		if(ag.isRefuge() == false) {
			return 0;
		}
		
		if(wq < 500) {
			return 1;
		}
		Action la = this.wsg.ai.getExecutedAction(this.wsg.ai.getTime() - 1);
		
		
		if(la != null && la instanceof ActionExtinguish) {
			return 1;
		}
		
		return 0;
	}
	
	private double getRestScore() {
		return 0;
	}
	
}
