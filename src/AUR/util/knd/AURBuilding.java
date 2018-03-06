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
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.EntityID;
import viewer.K_ScreenTransform;


/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURBuilding {
	
	private Polygon perceptibleArea = null;
	private int estimatedTemperature = 0;
	private double estimatedEnergy = 0;

	public AURAreaGraph ag = null;
	public AURWorldGraph wsg = null;

	private ArrayList<int[]> airCells = null;
	
	public AUREdgeToSee ets = null;

	public boolean commonWall[] = null;
	
	public AURBuilding(AURWorldGraph wsg, AURAreaGraph ag) {
		this.wsg = wsg;
		this.ag = ag;

		commonWall = new boolean[ag.polygon.npoints];
		for(int i = 0; i < ag.polygon.npoints; i++) {
			commonWall[i] = false;
		}
	}
	
	public static class Connection {
		
		public int toID = 0;
		public float weight = 0;

		public Connection(int toID, float weight) {
			this.toID = toID;
			this.weight = weight;
		}
	}
	
	public void precomputeRadiation(PrecomputeData pd) {
		pd.setString("connectedBuildingsFrom_" + this.ag.area.getID(), connectionsToString(calcConnectionsAndPaint(null, null)));
	}
	
	public String connectionsToString(ArrayList<Connection> connections) {
		String result = "";
		for(Connection c : connections) {
			result += c.toID + " " + c.weight + " ";
		}
		return result;
	}
	
	private short vis_ = 0;
	
	public ArrayList<Connection> calcConnectionsAndPaint(Graphics2D g2, K_ScreenTransform kst) {
		
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
		
		ArrayList<AURBuilding> aroundBuildings = new ArrayList<>();
		
		for(StandardEntity sent : cands) {
			if(sent.getStandardURN().equals(StandardEntityURN.BUILDING) == false) {
				continue;
			}
			AURBuilding b = wsg.getAreaGraph(sent.getID()).getBuilding();
			b.vis_ = 0;
			aroundBuildings.add(b);
		}
		
		ArrayList<Connection> result = new ArrayList<>();
		
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
				
				AURBuilding last = null;
				
				for(AURBuilding building : aroundBuildings) {
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
		for(AURBuilding b : aroundBuildings) {
			if(b.vis_ > 0) {
				result.add(new Connection(b.ag.area.getID().getValue(), ((float) b.vis_ / rays)));
			}
			
		}
		return result;
	}
	
	public ArrayList<Connection> stringToConnections(String str) {
		ArrayList<Connection> result = new ArrayList<>();
		Scanner scn = new Scanner(str);
		while(scn.hasNextInt()) {
			int id = scn.nextInt();
			float weight = scn.nextFloat();
			result.add(new Connection(id, weight));
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

	public void setCommonWalls() {
		
		Polygon bp = ag.polygon;
		Rectangle bounds = bp.getBounds();
		
		bounds = new Rectangle(
				(int) (bounds.getMinX() - 2),
				(int) (bounds.getMinY() - 2),
				(int) (bounds.getWidth() + 2 * 2),
				(int) (bounds.getHeight() + 2 * 2)
		);
		int r = (int) Math.max(bounds.getWidth(), bounds.getHeight()) / 2;
		Collection<StandardEntity> cands = wsg.wi.getObjectsInRectangle(
			(int) bounds.getMinX(),
			(int) bounds.getMinY(),
			(int) bounds.getMaxX(),
			(int) bounds.getMaxY()
		);
		
		cands.remove(ag.area);

		ArrayList<AURAreaGraph> ags = new ArrayList<>();
		
		for(StandardEntity sent : cands) {
			AURAreaGraph ag_ = wsg.getAreaGraph(sent.getID());
			if(ag_ != null && ag_.isBuilding()) {
				ags.add(ag_);
			}
		}
		
		For:
		for(int i = 0; i < bp.npoints; i++) {
			if(commonWall[i] == true) {
				continue;
			}
			for(AURAreaGraph ag_ : ags) {
				Polygon po = ag_.polygon;
				for(int j = 0; j < po.npoints; j++) {
					if(ag_.getBuilding().commonWall[j]) {
						continue;
					}
					if(bp.xpoints[i] == po.xpoints[j] && bp.ypoints[i] == po.ypoints[j]) {
						if(true && bp.xpoints[(i + bp.npoints - 1) % bp.npoints] == po.xpoints[(j + 1) % po.npoints]
							&& bp.ypoints[(i + bp.npoints - 1) % bp.npoints] == po.ypoints[(j + 1) % po.npoints]) {
							commonWall[(i + bp.npoints - 1) % bp.npoints] = true;
							ag_.getBuilding().commonWall[j] = true;
							continue For;
						} else {
							if(true && bp.xpoints[(i + 1) % bp.npoints] == po.xpoints[(j + 1) % po.npoints]
								&& bp.ypoints[(i + 1) % bp.npoints] == po.ypoints[(j + 1) % po.npoints]) {
								commonWall[i] = true;
								ag_.getBuilding().commonWall[j] = true;
								continue For;
							}
						}
					}
				}
			}
		}
	}
	
	public ArrayList<AURAreaGraph> getPerceptibleAreas() {
		
		Polygon perceptibleAreaPolygon = getPerceptibleAreaPolygon();
		Rectangle2D bounds = perceptibleAreaPolygon.getBounds();
		
		Collection<StandardEntity> cands = wsg.wi.getObjectsInRectangle(
			(int) bounds.getMinX(),
			(int) bounds.getMinY(),
			(int) bounds.getMaxX(),
			(int) bounds.getMaxY()
		);
	
		ArrayList<AURAreaGraph> result = new ArrayList<>();
		
		for(StandardEntity sent : cands) {
			if(sent.getStandardURN().equals(StandardEntityURN.ROAD) == false && sent.getStandardURN().equals(StandardEntityURN.HYDRANT) == false) {
				continue;
			}
			if(AURGeoUtil.intersectsOrContains(perceptibleAreaPolygon, (Polygon) ((Area) sent).getShape())) {
				result.add(wsg.getAreaGraph(sent.getID()));
			}
		}
		return result;
	}
	
	public void init() {

	}

	public Polygon getPerceptibleAreaPolygon() {
		if(this.perceptibleArea == null) {
			this.perceptibleArea = AURPerceptibleArea.getPerceptibleArea(this);
		}
		return this.perceptibleArea;
	}
	
	public ArrayList<Connection> connections = null;
	
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
    
}
