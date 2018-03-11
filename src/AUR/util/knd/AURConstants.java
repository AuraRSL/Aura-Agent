package AUR.util.knd;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURConstants {
	
	public final static int DEFAULT_FORGET_TIME = 30;
	public final static int POLICE_FORGET_TIME = 30;
	public final static int AMBULANCE_FORGET_TIME = 30;
	public final static int FIREBRIGADE_FORGET_TIME = 30;
	
	public static class Agent {
		public final static int RADIUS = 500;
		public final static int VELOCITY = 40000;
		
	}
	
	public static class Math {
		public final static double DOUBLE_INF = Double.MIN_VALUE;
		public final static int INT_INF = Integer.MAX_VALUE;
		public final static double sqr2 = 1.41421;
	}
	
	static class Viewer {
		public final static boolean LAUNCH = true;
	}
	
	static class FireSim {
		
		public final static double RADIATION_COEFFICENT = 0.011;
		public final static double STEFAN_BOLTZMANN_CONSTANT = 0.000000056704;
		public final static double GAMMA = 0.2;
		public final static double WATER_COEFFICIENT = 0.5;
		
		public final static int WORLD_AIR_CELL_SIZE = 10000;
		public final static int MAX_RADIATION_DISTANCE = 200000;
		public final static double RADIATION_RAY_RATE = 0.0025;
	
		public final static int FLOOR_HEIGHT = 3;
		
		public final static double STEEL_CAPACITY = 1.0;
		public final static double WOODEN_CAPACITY = 1.1;
		public final static double CONCRETE_CAPACITY = 1.5;
		
		public final static double STEEL_ENERGY = 800.0;
		public final static double WOODEN_ENERGY = 2400.0;
		public final static double CONCRETE_ENERGY = 350.0;
		
		public final static double STEEL_IGNITION = 47.0;
		public final static double WOODEN_IGNITION = 47.0;
		public final static double CONCRETE_IGNITION = 47.0;
		
		public final static boolean POLICE_OFFICE_INFLAMMABLE = false;
		public final static boolean AMBULANCE_CENTRE_INFLAMMABLE = false;
		public final static boolean FIRE_STATION_INFLAMMABLE = false;
		public final static boolean REFUGE_INFLAMMABLE = false;
		
	}
        
        /**
         * 
         * @author Amir Aslan Aslani - Mar 2018
         */
        public static class PoliceExtClear {
                public final static int CLEAR_POLYGON_HEIGHT = AURConstants.Agent.RADIUS * 3;
                public final static int MOVE_LENGTH_CALCULATE_ERROR = 500;
                
                public final static boolean USE_BUILDINGS_ENTRANCE_PERPENDICULAR_LINE = false;
        }
	
}
