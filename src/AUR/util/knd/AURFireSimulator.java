package AUR.util.knd;

import adf.agent.precompute.PrecomputeData;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import rescuecore2.standard.entities.Building;
import viewer.K_ScreenTransform;


/**
 *
 * @author Alireza Kandeh - 2017 & 2018
 */

public class AURFireSimulator {

    private AURWorldGraph wsg = null;
    private float cells[][][] = null;
    private Rectangle2D worldBounds = null;
    
    public float[][][] getCells() {
        return cells;
    }
    
    public boolean isPrecomputedConnections = false;
    
    public int getCellSize() {
        return AURConstants.WORLD_AIR_CELL_SIZE;
    }
    
    public void precompute(PrecomputeData pd) {
        for(AURAreaGraph ag : wsg.areas.values()) {
            if(ag.isBuilding()) {
                ag.getBuilding().fireSimBuilding.precomputeRadiation(pd);
            }
        }
        pd.setBoolean("radiation", true);
    }
    
    public void resume(PrecomputeData pd) {
        Boolean b = pd.getBoolean("radiation");
        if(b == null || b == false) {
		    
            return;
        }
        this.isPrecomputedConnections = true;
        for(AURAreaGraph ag : wsg.areas.values()) {
            if(ag.isBuilding()) {
                ag.getBuilding().fireSimBuilding.resumeRadiation(pd);
            }
        }    
    }

    public AURFireSimulator(AURWorldGraph wsg) {
        this.wsg = wsg;
        this.worldBounds = wsg.wi.getBounds();
        int rows = (int) Math.ceil(worldBounds.getHeight() / AURConstants.WORLD_AIR_CELL_SIZE);
        int cols = (int) Math.ceil(worldBounds.getWidth() / AURConstants.WORLD_AIR_CELL_SIZE);
        this.worldBounds.setRect(
            worldBounds.getMinX(),
            worldBounds.getMinY(),
            cols * AURConstants.WORLD_AIR_CELL_SIZE,
            rows * AURConstants.WORLD_AIR_CELL_SIZE
        );
        
        cells = new float[rows][cols][2];
    }
    
    private int[] res = new int[2];
    public int[] getCell_ij(double x, double y) {
        if(worldBounds.contains(x, y) == false) {
            return null;
        }
        x -= worldBounds.getMinX();
        y -= worldBounds.getMinY();
        
        res[0] = (int) Math.floor(y / AURConstants.WORLD_AIR_CELL_SIZE);
        res[1] = (int) Math.floor(x / AURConstants.WORLD_AIR_CELL_SIZE);
        
        return res;
    }
    
    private int[] res2 = new int[2];
    public int[] getCell_xy(int i, int j) {
        res2[0] = (int) (j * getCellSize() + worldBounds.getMinX());
        res2[1] = (int) (i * getCellSize() + worldBounds.getMinY());
        return res2;
    }
    
    public void paintJustCells(Graphics2D g2, K_ScreenTransform kst) {
        int a = AURConstants.WORLD_AIR_CELL_SIZE;
        int mx = (int) (worldBounds.getMinX());
        int my = (int) (worldBounds.getMinY());
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[i].length; j++) {
                Rectangle2D cell = kst.getTransformedRectangle(j * a + mx, i * a + my, a, a);
                g2.draw(cell);
            }
        }
    }
    
    public static double FLOOL_HEIGHT = 4.08569;

    public static double unitCapacity(Building b) {
        switch (b.getBuildingCodeEnum()) {
        case STEEL:
            return 1;
        case WOOD:
            return 1.1;
        case CONCRETE:
            return 1.5;
        }
        return 1.5;
    }

    public static double getBuildingCapacity(AURAreaGraph ag) {
        Building b = (Building) (ag.area);
        double result = b.getFloors() * b.getGroundArea() * FLOOL_HEIGHT;
        return result * unitCapacity(b);
    }

    // copied from MRL
    public static double getBuildingEnergy(AURAreaGraph ag, double t) {
        return getBuildingCapacity(ag) * t;
    }

    // copied from MRL
    public static double waterCooling(AURAreaGraph ag, double temperature, int water) {
        double effect = water * 20;
        return (getBuildingEnergy(ag, temperature) - effect) / getBuildingCapacity(ag);
    }

    // copied from MRL
    public static int getWaterNeeded(AURAreaGraph ag, double temperature, double finalTemperature) {
        int waterNeeded = 0;
        double currentTemperature = temperature;
        int step = 500;
        while (true) {
            currentTemperature = waterCooling(ag, currentTemperature, step);
            waterNeeded += step;
            if (currentTemperature <= finalTemperature) {
                break;
            }
        }
        return waterNeeded;
    }

}
