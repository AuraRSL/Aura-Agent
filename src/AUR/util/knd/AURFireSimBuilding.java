package AUR.util.knd;

import adf.agent.precompute.PrecomputeData;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
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
	
	private int estimatedTemperature = 0;
	private double estimatedEnergy = 0;
	private ArrayList<int[]> airCells = null;
	public AURWorldGraph wsg = null;
	public AURAreaGraph ag = null;
	public ArrayList<AURBuildingConnection> connections = null;
	public AURBuilding building = null;
	private short vis_ = 0;

	public AURFireSimBuilding(AURBuilding building) {
		this.building = building;
		this.wsg = building.wsg;
		this.ag = building.ag;
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
		double maxDist = AURConstants.MAX_RADIATION_DISTANCE;
		
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
				AURConstants.RADIATION_RAY_RATE
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

		int ij[] = building.wsg.fireSimulator.getCell_ij(buildingBounds.getMinX(), buildingBounds.getMinY());

		int i0 = ij[0];
		int j0 = ij[1];

		ij = building.wsg.fireSimulator.getCell_ij(buildingBounds.getMaxX(), buildingBounds.getMaxY());

		int i1 = ij[0];
		int j1 = ij[1];

		for(int i = i0; i <= i1; i++) {
			for(int j = j0; j <= j1; j++) {
				int xy[] = this.building.wsg.fireSimulator.getCell_xy(i, j);
				if(buildingPolygon.intersects(xy[0], xy[1], this.building.wsg.fireSimulator.getCellSize(), this.building.wsg.fireSimulator.getCellSize())) {
					int[] cell = new int[] {i, j, 0};
					AURGeoUtil.setAirCellPercent(building.wsg.fireSimulator, cell, building.wsg.fireSimulator.getCellSize(), buildingPolygon);
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
	
	public int getEstimatedFieryness() {
		Building b = ((Building) ag.area);
		if(b.isFierynessDefined() == false) {
			return 0;
		}
		return ((Building) ag.area).getFieryness();
	}
	
}
