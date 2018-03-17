package viewer.layers.AmboLayers;


import AUR.util.ambulance.Information.BuildingInfo;
import AUR.util.ambulance.Information.BuildingRateDeterminer;
import AUR.util.ambulance.Information.RescueInfo;
import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import rescuecore2.standard.entities.Area;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

import java.awt.*;

/**
 * Created by armanaxh on 2018.
 */

public class BuildingInfoLayer extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {


    }


    public String getString(AURWorldGraph wsg, AURAreaGraph selected_ag){
        String s = "";
        if(selected_ag != null) {
            Area area = selected_ag.area;
            if(wsg.rescueInfo != null) {
                if (wsg.rescueInfo.buildingInfo != null) {
                    for (BuildingInfo b : wsg.rescueInfo.buildingInfo.values()) {
                        if (b.me.getID().equals(area.getID())) {
                            s += calc(wsg, b);

                            s += "\n==============================\n";
                        }
                    }
                }
            }
        }
        return s;
    }

    public String calc(AURWorldGraph wsg, BuildingInfo building){
        String rate = "";

        RescueInfo rescueInfo = wsg.rescueInfo;
        double clusterEffect = BuildingRateDeterminer.clusterEffect(wsg, rescueInfo, building, 0.8);
        double TravelTime =  BuildingRateDeterminer.TravelTimeToBuildingEffect(wsg, rescueInfo, building, 0.6);
        double distanceFromFire =  BuildingRateDeterminer.distanceFromFireEffect(wsg, rescueInfo, building, 0.2);
        double brokness =   BuildingRateDeterminer.broknessEffect(wsg, rescueInfo, building, 0.2);
        double teperature =  BuildingRateDeterminer.buildingTemperatureEffect(wsg, rescueInfo, building, 0.2);



        rate += "\nclusterEffect :"+clusterEffect;
        rate += "\nTravelTime :"+TravelTime+"  > " +building.travelTimeTobulding ;
        rate += "\ndistanceFromFire :"+distanceFromFire + "  > "  ;
        rate += "\nbrokness :"+brokness + " > " + (building.me.isBrokennessDefined() ? building.me.getBrokenness() : 0);
        rate += "\nteperature :"+teperature + " > "+ (building.me.isTemperatureDefined() ? building.me.getTemperature() : 0 );



        rate += " \n Rate :: " + building.rate;

        return rate;
    }

}
