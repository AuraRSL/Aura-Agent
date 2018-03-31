package AUR.util.ambulance.ProbabilityDeterminant;

import AUR.util.ambulance.Information.RescueInfo;
import AUR.util.knd.AURWorldGraph;
import rescuecore2.standard.entities.*;

/**
 * Created by armanaxh on 3/17/18.
 */
public class AgentRateDeterminer {

    public static double calc(AURWorldGraph wsg, RescueInfo rescueInfo, Human human){
        double rate = 0;


        if( ignoreAgent(wsg, rescueInfo, human)){
            return rate;
        }


        rate += clusterEffect(wsg, rescueInfo, human, 1);
        rate += travelTimeEffect(wsg, rescueInfo, human, 1);
        rate += distanceFromFireEffect(wsg, rescueInfo, human, 0.2);
        rate += buriednessEffect(wsg, rescueInfo, human, 0.35);




        return rate;
    }

    public static boolean ignoreAgent(AURWorldGraph wsg, RescueInfo rescueInfo, Human human){

        //TODO
        if(human.isHPDefined() && human.getHP() < 1000){
            return true;
        }
        if(human.isBuriednessDefined() && human.getBuriedness() == 0){
            return true;
        }
        if(wsg.wi.getEntity(human.getPosition()) instanceof AmbulanceTeam){
            return true;
        }

        StandardEntity posEntity = wsg.wi.getEntity(human.getPosition());
        if(posEntity.getStandardURN().equals(StandardEntityURN.REFUGE)){
            return true;
        }

        if(posEntity instanceof Building) {
            Building position = (Building)posEntity;
            if (position.isOnFire()) {
                return true;
            }
        }

        return false;
    }
    public static double clusterEffect(AURWorldGraph wsg, RescueInfo rescueInfo, Human human , double coefficient){

        for(StandardEntity entity : rescueInfo.clusterEntity){
            if(human.isPositionDefined()) {
                if (entity.getID().equals(human.getPosition())) {
                    return 1 * coefficient;
                }
            }
        }
        return 0;
    }


    public static double travelTimeEffect(AURWorldGraph wsg, RescueInfo rescueInfo, Human human , double coefficient){
        if(!human.isPositionDefined()){
            return 0;
        }
        double temprate = RescueInfo.maxTravelTime - wsg.getAreaGraph(human.getPosition()).getTravelTime();
        return ((temprate*1D)/ RescueInfo.maxTravelTime)*coefficient;
    }

    public static double distanceFromFireEffect(AURWorldGraph wsg, RescueInfo rescueInfo, Human human , double coefficient){
        double tempRate = rescueInfo.maxTravelCost;
        return (tempRate/ rescueInfo.maxTravelCost)*coefficient;
    }
    public static double buriednessEffect(AURWorldGraph wsg, RescueInfo rescueInfo, Human human , double coefficient){
        if(!human.isBuriednessDefined()){
            return 0;
        }
        if(human.getBuriedness() == 0){
            return 0;
        }
        double tempRate = RescueInfo.maxBuriedness - human.getBuriedness();
        return (tempRate/ RescueInfo.maxBuriedness)*coefficient;

    }


}
