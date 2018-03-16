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
		public final static double WATER_COEFFICIENT = 20;
		public final static float TIME_STEP_LENGTH = 1;
		public final static float ENERGY_LOSS = 0.86f;
		public final static float AIR_TO_AIR_COEFFICIENT = 1f;
		public final static float AIR_TO_BUILDING_COEFFICIENT = 0.0015f;
		public final static float WEIGHT_GRID = 0.2f;
		public final static float AIR_CELL_HEAT_CAPACITY = 0.004f;
		
		
		public final static int WORLD_AIR_CELL_SIZE = 5000;
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
	
}
