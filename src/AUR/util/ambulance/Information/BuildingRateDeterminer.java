package AUR.util.ambulance.Information;

import AUR.util.knd.AURWorldGraph;
import rescuecore2.standard.entities.StandardEntityURN;

/**
 * Created by armanaxh on 3/17/18.
 */
public class BuildingRateDeterminer {

    public static double calc(AURWorldGraph wsg, RescueInfo rescueInfo, BuildingInfo building){
        double rate = 0;


        if( ignoreBulding(wsg, rescueInfo, building)){
            return rate;
        }



        rate += clusterEffect(wsg, rescueInfo, building, 0.8);
        rate += TravelTimeToBuildingEffect(wsg, rescueInfo, building, 0.6);
        rate += distanceFromFireEffect(wsg, rescueInfo, building, 0.2);
        rate += broknessEffect(wsg, rescueInfo, building, 0.2);
        rate += buildingTemperatureEffect(wsg, rescueInfo, building, 0.2);

        if(rate > 1){

        }


        return rate;
    }

    public static boolean ignoreBulding(AURWorldGraph wsg, RescueInfo rescueInfo, BuildingInfo building){

        if(rescueInfo == null || wsg == null || building == null){
            return true;
        }
        if(building.me.getURN().equals(StandardEntityURN.REFUGE)
                || building.me.isOnFire()
                || (building.me.isFierynessDefined() && building.me.getFieryness() == 8)
                ||  (building.me.isBrokennessDefined() && building.me.getBrokenness() == 0)){
            return true;
        }

        return false;
    }
    public static double clusterEffect(AURWorldGraph wsg, RescueInfo rescueInfo, BuildingInfo building , double coefficient){

        if(rescueInfo.clusterEntity.contains(building.me)){
            return 1 * coefficient;
        }
        return 0;
    }

    public static double TravelTimeToBuildingEffect(AURWorldGraph wsg, RescueInfo rescueInfo, BuildingInfo building , double coefficient){
        double tempRate = RescueInfo.maxTravelTime - building.travelTimeTobulding;
        return (tempRate/ RescueInfo.maxTravelTime)*coefficient;
    }

    public static double distanceFromFireEffect(AURWorldGraph wsg, RescueInfo rescueInfo, BuildingInfo building , double coefficient){
        double tempRate = RescueInfo.maxDistanceFromFire;
        return (tempRate/ RescueInfo.maxDistanceFromFire)*coefficient;
    }

    public static double broknessEffect(AURWorldGraph wsg, RescueInfo rescueInfo, BuildingInfo building , double coefficient){
        if(building.me.isBrokennessDefined()) {
            double tempRate = RescueInfo.maxBrokness - building.me.getBrokenness();
            return (tempRate / RescueInfo.maxBrokness) * coefficient;
        }
        return 0;
    }

    public static double buildingTemperatureEffect(AURWorldGraph wsg, RescueInfo rescueInfo, BuildingInfo building , double coefficient){
        if(building.me.isTemperatureDefined()) {
            double tempRate = RescueInfo.maxTemperature - building.me.getTemperature();
            return (tempRate / RescueInfo.maxTemperature) * coefficient;
        }
        return 0;
    }

}
