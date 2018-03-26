package AUR.util.knd;

import adf.agent.info.WorldInfo;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURUtil {
	
	public static boolean isBuilding(StandardEntity sent) {
		StandardEntityURN urn = sent.getStandardURN();
		return (false
			|| urn.equals(StandardEntityURN.BUILDING)
			|| urn.equals(StandardEntityURN.GAS_STATION)
			|| urn.equals(StandardEntityURN.REFUGE)
			|| urn.equals(StandardEntityURN.POLICE_OFFICE)
			|| urn.equals(StandardEntityURN.AMBULANCE_CENTRE)
			|| urn.equals(StandardEntityURN.FIRE_STATION));
	}
	
//	public int getAroundAliveLowerFireBrigades(WorldInfo wi) {
//		for (StandardEntity sent : wi.getEntitiesOfType(StandardEntityURN.FIRE_BRIGADE)) {
//			FireBrigade fb = (FireBrigade) sent;
//			if(fb.isHPDefined() && fb.getHP() > 0) {
//				
//			}
//			if (fb.isXDefined() && fb.isYDefined() && fb.isPositionDefined()) {
//				if (fb.getID().getValue() < ai.me().getID().getValue()) {
//
//					double dist = AURGeoUtil.dist(ai.getX(), ai.getY(), fb.getX(), fb.getY());
//
//					if (fb.getPosition().equals(ai.getPosition())) { // 
//						return this;
//					}
//				}
//			}
//		}
//	}
	
}
