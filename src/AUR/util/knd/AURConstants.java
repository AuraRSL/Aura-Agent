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
	
	public final static int AGENT_RADIUS = 500;
	
	static class FireSim {
		
		public final static int WORLD_AIR_CELL_SIZE = 10000;
		public final static int MAX_RADIATION_DISTANCE = 200000;
		public final static double RADIATION_RAY_RATE = 0.0035;
	
		public final static int FLOOR_HEIGHT = 3;
		
		public final static double STEEL_CAPACITY = 1;
		public final static double WOODEN_CAPACITY = 1.1;
		public final static double CONCRETE_CAPACITY = 1.5;
		
		public final static double STEEL_ENERGY = 800.0;
		public final static double WOODEN_ENERGY = 2400.0;
		public final static double CONCRETE_ENERGY = 350.0;
		
	}
	
}
