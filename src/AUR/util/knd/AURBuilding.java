package AUR.util.knd;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURBuilding {
    
    private int estimatedTemperature = 0;
    private double estimatedEnergy = 0;
    
    private AURAreaGraph ag = null;
    private AURWorldGraph wsg = null;
    
    private ArrayList<AURBuilding> connectedBuildings = null;
    
    private ArrayList<int[]> airCells = null;

    public AURBuilding(AURWorldGraph wsg, AURAreaGraph ag) {
        this.wsg = wsg;
        this.ag = ag;
    }
    
    public void init() {
        // ..
    }
    
    private void findAirCells() {
        airCells = new ArrayList<>();
        Polygon buildingPolygon = (Polygon) this.ag.area.getShape();
        Rectangle2D buildingBounds = buildingPolygon.getBounds();
        
        int ij[] = wsg.fireSimulator.getCell_ij(buildingBounds.getMinX(), buildingBounds.getMinY());
        
        int i0 = ij[0];
        int j0 = ij[1];
        
        ij = wsg.fireSimulator.getCell_ij(buildingBounds.getMaxX(), buildingBounds.getMaxY());
        
        int i1 = ij[0];
        int j1 = ij[1];
        
        for(int i = i0; i <= i1; i++) {
            for(int j = j0; j <= j1; j++) {
                int xy[] = this.wsg.fireSimulator.getCell_xy(i, j);
                if(buildingPolygon.intersects(xy[0], xy[1], this.wsg.fireSimulator.getCellSize(), this.wsg.fireSimulator.getCellSize())) {
                    int[] cell = new int[] {i, j, 0};
                    AURGeoUtil.setAirCellPercent(wsg.fireSimulator, cell, wsg.fireSimulator.getCellSize(), buildingPolygon);
                    airCells.add(cell);
                }
                
            }
        }
        
    }
    
    public ArrayList<int[]> getAirCells() {
        if(airCells == null) {
            findAirCells();
        }
        return airCells;
    }
    
    public double getEstimatedEnergy() {
        return this.estimatedEnergy;
    }

    public void setEstimatedEnergy(double estimatedEnergy) {
        this.estimatedEnergy = estimatedEnergy;
    }

    public int getEstimatedTemperature() {
        return estimatedTemperature;
    }

    public void setEstimatedTemperature(int estimatedTemperature) {
        this.estimatedTemperature = estimatedTemperature;
    }
    
    private void calcConnectedBuildings() {
        connectedBuildings = new ArrayList<>();
        // ..
    }
    
    public ArrayList<AURBuilding> getConnectedBuildings() {
        if(connectedBuildings == null) {
            calcConnectedBuildings();
        }
        return connectedBuildings;
    }
    
}
