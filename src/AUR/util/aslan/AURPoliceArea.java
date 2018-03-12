package AUR.util.aslan;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import java.util.ArrayList;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Building;
import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author Amir Aslan Aslani - Mar 2018
 */
public class AURPoliceArea {
        public AURAreaGraph ag;
        public Area area;
        public AURWorldGraph wsg;
        
        public double baseScore = 1;
        public double secondaryScore = 1;

        public AURPoliceArea(Area area,AURAreaGraph ag,AURWorldGraph wsg) {
                this.area = area;
                this.ag = ag;
                this.wsg = wsg;
        }
        
        public double getFinalScore(){
                return baseScore * secondaryScore;
        }
        
        public double getBlockadeExistancePossibilityScore(){
                double score = 1;
                double errorScore = 0.8;
                for(Building building : getNeighbourBuildings()){
                        score *= building.isFloorsDefined() ? building.getFloors() : errorScore;
                        score *= building.isBrokennessDefined() ? building.getBrokenness() : errorScore;
                        score *= building.isGroundAreaDefined() ? building.getGroundArea() : errorScore;
                        score *= building.isFloorsDefined() ? building.getFloors() : errorScore;
                        
                }
                return score;
        }
        
        public ArrayList<Building> getNeighbourBuildings(){
                ArrayList<Building> result = new ArrayList<>();
                for(EntityID eid : area.getNeighbours()){
                        if(wsg.wi.getEntity(eid) instanceof Building)
                                result.add((Building) wsg.wi.getEntity(eid));
                }
                return result;
        }
}
