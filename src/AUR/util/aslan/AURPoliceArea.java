package AUR.util.aslan;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import java.util.ArrayList;
import java.util.Random;
import org.uncommons.maths.random.MersenneTwisterRNG;
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
        
        public Random random = new MersenneTwisterRNG();

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
                if(ag.isBuilding())
                        return score;
                for(Building building : getNeighbourBuildings()){
                        AURBuildingCollapseEstimator bce = new AURBuildingCollapseEstimator(building, random ,100);
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
