package AUR.util.knd;

import adf.agent.precompute.PrecomputeData;
import firesimulator.simulator.Simulator;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Scanner;
import org.uncommons.maths.random.GaussianGenerator;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import viewer.K_ScreenTransform;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURFireSimBuilding {

	private double estimatedEnergy = 0;
	private ArrayList<int[]> airCells = null;
	public AURWorldGraph wsg = null;
	public AURAreaGraph ag = null;
	public ArrayList<AURBuildingConnection> connections = null;
	public AURBuilding building = null;
	private short vis_ = 0;
	public int floors = 1;
	private double estimatedFuel = 0;
	private boolean wasEverWatered = false;
	private double waterQuantity = 0;
//	private boolean ignite = false;
	private int lastRealFieryness = 0;
	private double lastRealTemperature = 0;
	
	private GaussianGenerator burnRate = new GaussianGenerator(0.15, 0.02, new Random(0));
	
	public AURFireSimBuilding(AURBuilding building) {
		this.building = building;
		this.wsg = building.wsg;
		this.ag = building.ag;
		
		Building b = building.building;
		this.floors = 1;
		if(b.isFloorsDefined()) {
			this.floors = b.getFloors();
		}
		this.estimatedEnergy = 0;
		this.estimatedFuel = getInitialFuel();
//		this.ignite = false;

//		if(Math.random() < 0.3) {
//			this.ignite();
//		}
	}
	
	
	public void update() {
		if(this.building.building.isFierynessDefined()) {
			int f = this.building.building.getFieryness();
			if(f == 4) {
				this.setWasEverWatered(true);
			}
			if(f != this.lastRealFieryness) {
				onRealFierynessChange(f);
				this.lastRealFieryness = f;
			}
		}
		if(this.building.building.isTemperatureDefined()) {
			double t = this.building.building.getTemperature();
			if(Math.abs(this.lastRealTemperature - t) < 1e-8) {
				this.lastRealTemperature = t;
				this.onRealTemperatureChange(t);
			}
		}
		
	}
	
	public void onRealFierynessChange(int newFieryness) {
		switch(newFieryness) {
			case 0:
			case 1:
			case 4:
			case 5: {
				setEstimatedFuel(getInitialFuel());
				break;
			}
			case 2:
			case 6: {
				setEstimatedFuel(getInitialFuel() * 0.66);
				break;
			}
			case 3:
			case 7: {
				setEstimatedFuel(getInitialFuel() * 0.33);
				break;
			}
			
			default: {
				setEstimatedFuel(0);
				break;
			}
		}
	}
	
	public void onRealTemperatureChange(double newTemperature) {
		if(newTemperature >= getIgnitionPoint()) {
			setWaterQuantity(0);
		}
		this.setEstimatedEnergy(newTemperature * getCapacity());
	}
	
	public void precomputeRadiation(PrecomputeData pd) {
		pd.setString("connectedBuildingsFrom_" + this.ag.area.getID(), connectionsToString(calcConnectionsAndPaint(null, null)));
	}
	
	public String connectionsToString(ArrayList<AURBuildingConnection> connections) {
		String result = "";
		for(AURBuildingConnection c : connections) {
			result += c.toID + " " + c.weight + " ";
		}
		return result;
	}
	
	public ArrayList<AURBuildingConnection> calcConnectionsAndPaint(Graphics2D g2, K_ScreenTransform kst) {
		
		boolean paint = g2 != null && kst != null;
		double maxDist = AURConstants.FireSim.MAX_RADIATION_DISTANCE;
		
		Polygon bp = this.ag.polygon;
		Rectangle bounds = bp.getBounds();
		
		bounds = new Rectangle(
				(int) (bounds.getMinX() - maxDist),
				(int) (bounds.getMinY() - maxDist),
				(int) (bounds.getWidth() + 2 * maxDist),
				(int) (bounds.getHeight() + 2 * maxDist)
		);
		
		Collection<StandardEntity> cands = this.wsg.wi.getObjectsInRectangle(
			(int) bounds.getMinX(),
			(int) bounds.getMinY(),
			(int) bounds.getMaxX(),
			(int) bounds.getMaxY()
		);
		cands.remove(this.ag.area);
		
		ArrayList<AURFireSimBuilding> aroundBuildings = new ArrayList<>();
		
		for(StandardEntity sent : cands) {
			if(sent.getStandardURN().equals(StandardEntityURN.BUILDING) == false) {
				continue;
			}
			AURBuilding b = wsg.getAreaGraph(sent.getID()).getBuilding();
			b.fireSimBuilding.vis_ = 0;
			aroundBuildings.add(b.fireSimBuilding);
		}
		
		ArrayList<AURBuildingConnection> result = new ArrayList<>();
		
		int rays = 0;
		
		for(Edge edge : this.ag.area.getEdges()) {
			ArrayList<double[]> randomOrigins = AURGeoUtil.getRandomPointsOnSegmentLine(
				edge.getStartX(),
				edge.getStartY(),
				edge.getEndX(),
				edge.getEndY(),
				AURConstants.FireSim.RADIATION_RAY_RATE
			);
			
			rays += randomOrigins.size();
			
			double rv[] = new double[2];
			
			double ray[] = new double[4];
			
			if(paint) {
				g2.setStroke(new BasicStroke(1));
			}
			
			for(double[] o : randomOrigins) {
				if(paint) {
					g2.setColor(Color.white);
					kst.fillTransformedOvalFixedRadius(g2, o[0], o[1], 2);
					g2.setColor(Color.red);
				}

				AURGeoUtil.getRandomUnitVector(rv);
				
				ray[0] = o[0];
				ray[1] = o[1];
				ray[2] = o[0] + rv[0] * maxDist;
				ray[3] = o[1] + rv[1] * maxDist;
				
				AURFireSimBuilding last = null;
				
				for(AURFireSimBuilding building : aroundBuildings) {
					boolean b = AURGeoUtil.hitRayAllEdges(building.ag.polygon , ray);
					if(b) {
						last = building;
					}
				}
				
				if(paint) {
					kst.drawTransformedLine(g2, ray[0], ray[1], ray[2], ray[3]);
				}
				
				if(last != null) {
					last.vis_++;
				}
			}
		}
		for(AURFireSimBuilding b : aroundBuildings) {
			if(b.vis_ > 0) {
				result.add(new AURBuildingConnection(b.ag.area.getID().getValue(), ((float) b.vis_ / rays)));
			}
			
		}
		return result;
	}
	
	public ArrayList<AURBuildingConnection> stringToConnections(String str) {
		ArrayList<AURBuildingConnection> result = new ArrayList<>();
		Scanner scn = new Scanner(str);
		while(scn.hasNextInt()) {
			int id = scn.nextInt();
			float weight = scn.nextFloat();
			result.add(new AURBuildingConnection(id, weight));
		}
		
		return result;
	}
	
	public void resumeRadiation(PrecomputeData pd) {
		String str = pd.getString("connectedBuildingsFrom_" + this.ag.area.getID());
		if(str == null) {
			return;
		}
		this.connections = stringToConnections(str);
	}
	
	private void findAirCells() {
		airCells = new ArrayList<>();
		Polygon buildingPolygon = (Polygon) this.building.ag.area.getShape();
		Rectangle2D buildingBounds = buildingPolygon.getBounds();
		int ij[] = building.wsg.fireSimulator.airCells.getCell_ij(buildingBounds.getMinX(), buildingBounds.getMinY());
		int i0 = ij[0];
		int j0 = ij[1];
		ij = building.wsg.fireSimulator.airCells.getCell_ij(buildingBounds.getMaxX(), buildingBounds.getMaxY());
		int i1 = ij[0];
		int j1 = ij[1];
		for(int i = i0; i <= i1; i++) {
			for(int j = j0; j <= j1; j++) {
				int xy[] = this.building.wsg.fireSimulator.airCells.getCell_xy(i, j);
				if(buildingPolygon.intersects(xy[0], xy[1], this.building.wsg.fireSimulator.airCells.getCellSize(), this.building.wsg.fireSimulator.airCells.getCellSize())) {
					int[] cell = new int[] {i, j, 0};
					setAirCellPercent(building.wsg.fireSimulator, cell, building.wsg.fireSimulator.airCells.getCellSize(), buildingPolygon);
					airCells.add(cell);
				}
			}
		}
	}
	
	private void setAirCellPercent(AURFireSimulator fs, int airCell[], int airCellSize, Polygon buildingPolygon) {
		double dw = (double) airCellSize / 10;
		double dh = (double) airCellSize / 10;
		int xy[] = fs.airCells.getCell_xy(airCell[0], airCell[1]);
		int count = 0;
		double x = 0;
		double y = 0;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				x = xy[0] + j * dw;
				y = xy[1] + i * dh;
				if(buildingPolygon.contains(x, y)) {
					count++;
				}
			}
		}
		airCell[2] = count;
	}
	
	public double getRadiationEnergy() {
		double t = getEstimatedTemperature() + 293; // Assume ambient temperature is 293 Kelvin.
		double radEn = (t * t * t * t) * getTotalWallArea() * AURConstants.FireSim.RADIATION_COEFFICENT * AURConstants.FireSim.STEFAN_BOLTZMANN_CONSTANT;
		if (radEn == Double.NaN || radEn == Double.POSITIVE_INFINITY || radEn == Double.NEGATIVE_INFINITY) {
			radEn = Double.MAX_VALUE * 0.75;
		}
		if (radEn > getEstimatedEnergy()) {
			radEn = getEstimatedEnergy();
		}
		return radEn;
	}
	
	public double getConsum() {
		if (this.estimatedFuel <= 1e-8) {
			return 0;
		}
		double tf = (double) (getEstimatedTemperature() / 1000f);
		double lf = (double) getEstimatedFuel() / getInitialFuel();
		double f = (double) (tf * lf * burnRate.nextValue());
		if (f < 0.005d) {
			f = 0.005d;
		}
		return getInitialFuel() * f;
	}
		
	public boolean inflammable() {
		StandardEntityURN urn = this.building.building.getStandardURN();
		switch(urn) {
			case REFUGE: {
				return AURConstants.FireSim.REFUGE_INFLAMMABLE;
			}
			case AMBULANCE_CENTRE: {
				return AURConstants.FireSim.AMBULANCE_CENTRE_INFLAMMABLE;
			}
			case FIRE_STATION: {
				return AURConstants.FireSim.FIRE_STATION_INFLAMMABLE;
			}
			case POLICE_OFFICE: {
				return AURConstants.FireSim.POLICE_OFFICE_INFLAMMABLE;
			}
			default: {
				return true;
			}
		}
	}
	
	public void setWaterQuantity(double waterQuantity) {
		if(waterQuantity > 0) {
			setWasEverWatered(true);
		}
		this.waterQuantity = waterQuantity;
	}
	
	public double getWaterQuantity() {
		return this.waterQuantity;
	}
	
	public void setWasEverWatered(boolean b) {
		this.wasEverWatered = b;
	}
	
	public boolean wasEverWatered() {
		return this.wasEverWatered;
	}
	
	public void ignite() {
		setEstimatedEnergy(getCapacity() * getIgnitionPoint() * 1.5);
//		setIgnite(true);
	}
	
//	public void setIgnite(boolean i) {
//		this.ignite = i;
//	}
//	
//	public boolean getIgnite() {
//		return this.ignite;
//	}
	
	public double getIgnitionPoint() {
		switch (building.building.getBuildingCodeEnum()) {
			case STEEL: {
				return AURConstants.FireSim.STEEL_IGNITION;
			}
			case WOOD: {
				return AURConstants.FireSim.WOODEN_IGNITION;
			}
			case CONCRETE: {
				return AURConstants.FireSim.CONCRETE_IGNITION;
			}
			default: {
				return AURConstants.FireSim.CONCRETE_IGNITION;
			}
		}
	}
	
	public void setEstimatedFuel(double f) {
		this.estimatedFuel = f;
	}
	
	public double getEstimatedFuel() {
		return (double) this.estimatedFuel;
	}

	public double getInitialFuel() {
		return (double) getThermoCapacity() * getVolume();
	}
	
	public double getFuelDensity() {
		switch (building.building.getBuildingCodeEnum()) {
			case STEEL: {
				return AURConstants.FireSim.STEEL_ENERGY;
			}
			case WOOD: {
				return AURConstants.FireSim.WOODEN_ENERGY;
			}
			case CONCRETE: {
				return AURConstants.FireSim.CONCRETE_ENERGY;
			}
			default: {
				return AURConstants.FireSim.CONCRETE_ENERGY;
			}
		}
	}
	
	public double getThermoCapacity() {
		switch (building.building.getBuildingCodeEnum()) {
			case STEEL: {
				return AURConstants.FireSim.STEEL_CAPACITY;
			}
			case WOOD: {
				return AURConstants.FireSim.WOODEN_CAPACITY;
			}
			case CONCRETE: {
				return AURConstants.FireSim.CONCRETE_CAPACITY;
			}
			default: {
				return AURConstants.FireSim.CONCRETE_CAPACITY;
			}
		}
	}
	
	public double getCapacity() {
		return getThermoCapacity() * getVolume();
	}
	
	public double getPerimeter() {
		return ((double) this.ag.perimeter / 1000d);
	}
	
	public double getGroundArea() {
		return ((double) this.ag.goundArea / 1000000d);
	}
	
	public double getTotalWallArea() {
		// according to the old fire simulator
		return ((double) this.ag.perimeter * AURConstants.FireSim.FLOOR_HEIGHT) / 1000d;
	}
	
	public double getVolume() {
		return ((double) this.ag.goundArea / 1000000d) * this.floors * AURConstants.FireSim.FLOOR_HEIGHT;
	}

	public ArrayList<int[]> getAirCells() {
		if(airCells == null) {
			findAirCells();
		}
		return airCells;
	}

	public double getEstimatedEnergy() {
		if(this.estimatedEnergy == Double.NaN || this.estimatedEnergy == Double.POSITIVE_INFINITY || this.estimatedEnergy == Double.NEGATIVE_INFINITY) {
			this.estimatedEnergy = Double.MAX_VALUE  * 0.75d;
		}
		return this.estimatedEnergy;
	}

	public void setEstimatedEnergy(double energy) {
		if(energy == Double.NaN || energy == Double.POSITIVE_INFINITY || energy == Double.NEGATIVE_INFINITY) {
			energy = Double.MAX_VALUE  * 0.75d;
		}
		this.estimatedEnergy = energy;
	}

	public double getEstimatedTemperature() {
		return (double) this.getEstimatedEnergy() / this.getCapacity();
	}
	
	public int getEstimatedFieryness() {
		if (inflammable() == false) {
			return 0;
		}
		if (getEstimatedTemperature() >= getIgnitionPoint()) {
			if (estimatedFuel >= getInitialFuel() * 0.66) {
				return 1;   // burning, slightly damaged
			}
			if (estimatedFuel >= getInitialFuel() * 0.33) {
				return 2;   // burning, more damaged
			}
			if (estimatedFuel > 0) {
				return 3;    // burning, severly damaged
			}
		}
		if (Math.abs(estimatedFuel - getInitialFuel()) < 1e-8) {
			if (wasEverWatered == true) {
				return 4;   // not burnt, but watered-damaged
			} else {
				return 0;   // not burnt, no water damage
			}
		}
		if (estimatedFuel >= getInitialFuel() * 0.66) {
			return 5;        // extinguished, slightly damaged
		}
		if (estimatedFuel >= getInitialFuel() * 0.33) {
			return 6;        // extinguished, more damaged
		}
		if (estimatedFuel > 0) {
			return 7;        // extinguished, severely damaged
		}
		return 8;           // completely burnt down
	}
	
	public boolean isOnFire() {
		int f = getEstimatedFieryness();
		return f > 0 && f < 4;
	}
	
	public int getWaterNeeded() {
		if(isOnFire() == true) {
			return ag.wsg.si.getFireExtinguishMaxSum();
		} else {
			return 0;
		}
	}
	
}
