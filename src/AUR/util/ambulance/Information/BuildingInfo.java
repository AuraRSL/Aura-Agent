package AUR.util.ambulance.Information;

import AUR.util.knd.AURWorldGraph;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.StandardEntity;

/**
 *
 * @author armanaxh - 2018
 */

public class BuildingInfo {

    public double rate = 0;
    public boolean visit = false;
    public AURWorldGraph wsg;
    public RescueInfo rescueInfo;
    public final Building me;
    public int travelTimeTobulding = RescueInfo.maxTravelTime;
    public int distanceFromFire = RescueInfo.maxDistanceFromFire;

    public BuildingInfo(AURWorldGraph wsg, RescueInfo rescueInfo, Building me) {
        this.wsg = wsg;
        this.rescueInfo = rescueInfo;
        this.me = me;

        init();
    }

    // init *****************************************************************************

    private void init(){
        this.travelTimeTobulding = calcTravelTimeToBuilding();
        this.distanceFromFire = calcDistanceFromFire();
        this.rate = BuildingRateDeterminer.calc(wsg, rescueInfo, this);
    }



    // update *****************************************************************************

    public void updateInformation(){
        this.travelTimeTobulding = calcTravelTimeToBuilding();
        this.distanceFromFire = calcDistanceFromFire();
        this.rate = BuildingRateDeterminer.calc(wsg, rescueInfo, this);

    }

    // Calc *****************************************************************************

    public int calcDistanceFromFire(){

        return RescueInfo.maxTravelTime;
    }

    public int calcTravelTimeToBuilding(){
        if(me != null) {
            wsg.dijkstra(wsg.ai.getPosition());

            int tempT = RescueInfo.maxTravelTime;
            StandardEntity pos = wsg.wi.getEntity(me.getID());
            if(pos instanceof Area) {
                if(wsg.getAreaGraph(me.getID()) != null) {
                    tempT = wsg.getAreaGraph(me.getID()).getTravelTime();
                }
            }else if(pos instanceof AmbulanceTeam){
                AmbulanceTeam amtPos = (AmbulanceTeam)pos;
                if(wsg.getAreaGraph(amtPos.getPosition()) != null) {
                    tempT = wsg.getAreaGraph(amtPos.getPosition()).getTravelTime();
                }
            }

            return tempT;
        }
        return RescueInfo.maxTravelTime;
    }

}
