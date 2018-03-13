package AUR.util.aslan;

import java.util.Random;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.ContinuousUniformGenerator;
import rescuecore2.standard.entities.Building;

/**
 *
 * @author Amir Aslan Aslani - Mar 2018
 */
public class AURBuildingCollapseEstimator {
        
        public final double FLOOR_HEIGHT = 7;
        public final double MAX_COLLAPSE = 100;
        public final double WALL_COLLAPSE_EXTENT_MIN = 0.6;
        public final double WALL_COLLAPSE_EXTENT_MAX = 1;
        
        public final Building building;
        public double collapsedRatio = 0;
        public final double floorHeight = FLOOR_HEIGHT * 1000;
        
        public NumberGenerator<Double> extent;

        public AURBuildingCollapseEstimator(Building building, Random rnd) {
                this.extent = new ContinuousUniformGenerator(
                        WALL_COLLAPSE_EXTENT_MIN,
                        WALL_COLLAPSE_EXTENT_MAX,
//                        new MersenneTwisterRNG(new BigInteger("3", 16).toByteArray())
                        rnd
                );
                
                this.building = building;
        }
        
        public double d(){
                return getRemainingFloors() * (getDamage() /  MAX_COLLAPSE) * extent.nextValue();
        }

        public double getRemainingFloors() {
                return floorHeight * building.getFloors() * (1 - getCollapsedRatio());
        }

        public double getDamage() {
                return building.isBrokennessDefined() ? building.getBrokenness() : 0;
        }

        public double getCollapsedRatio() {
                return collapsedRatio;
        }
        
        public void setAftershockCollapsedRatio(){
                collapsedRatio += d() / getTotalCollapse(floorHeight);
        }
        
        public double getTotalCollapse(double floorHeight){
		return floorHeight * building.getFloors();
	}
}
