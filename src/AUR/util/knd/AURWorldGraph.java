 package AUR.util.knd;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import AUR.util.FibonacciHeap;
import adf.agent.action.common.ActionMove;
import adf.agent.communication.MessageManager;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.component.module.AbstractModule;
import java.awt.Polygon;
import viewer.K_Viewer;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityConstants.Fieryness;
import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author Alireza Kandeh - 2017 & 2018
 */

public class AURWorldGraph extends AbstractModule {

	public WorldInfo wi = null;
	public AgentInfo ai = null;
	public ScenarioInfo si = null;
	public HashMap<EntityID, AURAreaGraph> areas = new HashMap<EntityID, AURAreaGraph>();
	public AURAreaGrid instanceAreaGrid = new AURAreaGrid();
	public Collection<EntityID> changes = null;
	public ArrayList<AURWall> walls = new ArrayList<AURWall>();
	public AURNode startNullNode = new AURNode(0, 0, null, null);
	public EntityID lastDijkstraFrom = null;
	public EntityID lastNoBlockadeDijkstraFrom = null;
	private int updateTime = -1;
	public double gridDx = 0;
	public double gridDy = 0;
	public int gridCols = 0;
	public int gridRows = 0;
	public double worldGridSize = 500 * 2 * 22.08568;
	public boolean grid[][] = null;
	public AURFireSimulator fireSimulator = null;
	public LinkedList<AURAreaGraph> areaGraphsGrid[][] = null;

	public int agentOrder = -1;
	
	public final static double colorCoe[][] = {
		{1.0, 0.9, 0.8, 0.7},
		{0.7, 1.0, 0.9, 0.8},
		{0.8, 0.7, 1.0, 0.9},
		{0.9, 0.8, 0.7, 1.0}
	};
	
	public final static int dij_9[][] = {
		{-1, +1},
		{+0, +1},
		{+1, +1},
		{-1, +0},
		{+0, +0},
		{+1, +0},
		{-1, -1},
		{+0, -1},
		{+1, -1},
	};
	
	@SuppressWarnings("unchecked")
	private void initGrid() {
		if (grid != null && areaGraphsGrid != null) {
			return;
		}
		Rectangle2D worldBounds = worldInfo.getBounds();
		gridDx = 0 - worldBounds.getMinX();
		gridDy = 0 - worldBounds.getMinY();
		gridCols = (int) (worldBounds.getWidth() / worldGridSize) + 1;
		gridRows = (int) (worldBounds.getHeight() / worldGridSize) + 1;
		grid = new boolean[gridRows][gridCols];
		areaGraphsGrid = new LinkedList [gridRows][gridCols];
		int i, j;
		for (AURAreaGraph ag : areas.values()) {
			if (ag.isBuilding()) {
				i = (int) ((ag.getY() - gridDy) / worldGridSize);
				j = (int) ((ag.getX() - gridDx) / worldGridSize);
				if (areaGraphsGrid[i][j] == null) {
					areaGraphsGrid[i][j] = new LinkedList<AURAreaGraph>();
				}
				areaGraphsGrid[i][j].add(ag);
			}
		}
	}

	private void calcFireProbability() {
		initGrid();
		for (int i = 0; i < gridRows; i++) {
			for (int j = 0; j < gridCols; j++) {
				grid[i][j] = false;
			}
		}

		for (AURAreaGraph ag : areas.values()) {
			if (ag.isBuilding()) {
				Building b = (Building) (ag.area);
				if (b.isFierynessDefined()) {
					ag.onFireProbability = b.isOnFire();
				}

			}
		}

		for (int i = 0; i < gridRows; i++) {
			for (int j = 0; j < gridCols; j++) {
				if (grid[i][j] == false && areaGraphsGrid[i][j] != null) {
					for (AURAreaGraph ag : areaGraphsGrid[i][j]) {
						if (ag.isOnFire()) {
							grid[i][j] = true;
							break;
						} else {
							if (ag.isBuilding() && ag.noSeeTime() == 0) {
								Building b = (Building) (ag.area);
								if (ag.getBuilding().fireSimBuilding.getEstimatedTemperature() > 0) {
									grid[i][j] = true;
									break;
								}
							}
						}
					}
				}
			}
		}

		boolean f;
		for (int i = 0; i < gridRows; i++) {
			for (int j = 0; j < gridCols; j++) {
				if (areaGraphsGrid[i][j] != null) {
					f = grid[i][j];
					/*
					 * for(int d = 0; d < 9 && f == false; d++) { ip = i +
					 * dij_9[d][0]; jp = j + dij_9[d][1]; if(insideGrid(ip, jp))
					 * { f = f || grid[ip][jp]; } }
					 */
					if (f) {
						for (AURAreaGraph ag : areaGraphsGrid[i][j]) {
							if (ag.noSeeTime() > 0) {
								ag.onFireProbability = true;
							}
						}
					}
				}
			}
		}
		for (AURAreaGraph ag : areas.values()) {
			if (ag.noSeeTime() == 0) {
				if (ag.isBuilding() && ag.isOnFire() == false) {
					ag.onFireProbability = false;
				}
			}
		}
	}

	public boolean insideGrid(int i, int j) {
		return (i >= 0 && j >= 0 && i < gridRows && j < gridCols);
	}

//	public ActionMove getMoveActionToSee(EntityID from, EntityID target) {
//		if (target == null) {
//			return null;
//		}
//		ActionMove result = null;
//		Collection<EntityID> targets = new ArrayList<>();
//		AURAreaGraph fromAg = getAreaGraph(from);
//		AURAreaGraph targetAg = getAreaGraph(target);
//		targets.add(target);
//		double destX = -1;
//		double destY = -1;
//		ArrayList<EntityID> path = null;
//		AURAreaInSightChecker checker = new AURAreaInSightChecker(this, targetAg);
//		path = getPathToClosest(from, targets);
//
//		if (path == null || path.size() <= 1) {
//			return result;
//		}
//		EntityID firstStep = path.get(1);
//		if (path.size() == 2 && (firstStep.equals(target) || firstStep.equals(from))) {
//			path.clear();
//			Point2D point = this.instanceAreaGrid.getPointHasSight(fromAg, checker, ai.getX(), ai.getY());
//			if (point != null) {
//				path.add(from);
//				destX = point.getX();
//				destY = point.getY();
//			}
//		} else if (path.get(path.size() - 1).getValue() == target.getValue()) {
//			path.remove(path.size() - 1);
//			if (path.size() >= 2) {
//				AURAreaGraph ag = getAreaGraph(path.get(path.size() - 1));
//				if (ag.isSmall()) {
//					path.remove(path.size() - 1);
//				}
//			}
//		}
//		if (path != null && path.size() > 1) {
//			if (destX >= 0) {
//				result = new ActionMove(path, (int) destX, (int) destY);
//			} else {
//				result = new ActionMove(path);
//			}
//		} else {
//			if (checker.hasChance(fromAg)) {
//				Point2D point = this.instanceAreaGrid.getPointHasSight(fromAg, checker, ai.getX(), ai.getY());
//				if (point != null) {
//					path = new ArrayList<>();
//					path.add(from);
//					destX = point.getX();
//					destY = point.getY();
//					result = new ActionMove(path, (int) destX, (int) destY);
//					return result;
//				}
//			}
//		}
//		return result;
//	}
	
	
	
	public ActionMove getMoveActionToPercept(EntityID from, EntityID target) {
		if (target == null) {
			return null;
		}
		Collection<EntityID> targets = new ArrayList<>();
		AURAreaGraph fromAg = getAreaGraph(from);
		AURAreaGraph targetAg = getAreaGraph(target);
		if(targetAg.isBuilding() == false) {
			return null;
		}
		targets.add(target);
		double destX = -1;
		double destY = -1;
		
		dijkstra(from);
		
		AUREdgeToStand etp = targetAg.getBuilding().edgeToPereceptAndExtinguish;
		if(etp == null) {
			return null;
		}
		ArrayList<EntityID> path = new ArrayList<>();
		
		
		path.add(etp.ownerAg.area.getID());
		AURNode node = etp.fromNode;
		while (node.pre != startNullNode) {
			path.add(node.getPreAreaGraph().area.getID());
			node = node.pre;
		}
		if(path.get(path.size() - 1).equals(from) == false) {
			path.add(from);
		}
		java.util.Collections.reverse(path);
		
		
		
		return new ActionMove(path, (int) etp.standX, (int) etp.standY);
		
	}
	
	public ActionMove getMoveActionToSeeInside(EntityID from, EntityID target) {
		if (target == null) {
			return null;
		}
		Collection<EntityID> targets = new ArrayList<>();
		AURAreaGraph fromAg = getAreaGraph(from);
		AURAreaGraph targetAg = getAreaGraph(target);
		if(targetAg.isBuilding() == false) {
			return null;
		}
		targets.add(target);
		double destX = -1;
		double destY = -1;
		
		dijkstra(from);
		
		AUREdgeToStand etp = targetAg.getBuilding().edgeToSeeInside;
		if(etp == null) {
			return null;
		}
		ArrayList<EntityID> path = new ArrayList<>();
		
		
		path.add(etp.ownerAg.area.getID());
		AURNode node = etp.fromNode;
		while (node.pre != startNullNode) {
			path.add(node.getPreAreaGraph().area.getID());
			node = node.pre;
		}
		if(path.get(path.size() - 1).equals(from) == false) {
			path.add(from);
		}
		java.util.Collections.reverse(path);
		
		return new ActionMove(path, (int) etp.standX, (int) etp.standY);
	}
	

	public ActionMove getNoBlockadeMoveAction(EntityID from, EntityID target) {
		if (target == null) {
			return null;
		}
		ActionMove result = null;
		Collection<EntityID> targets = new ArrayList<>();
		targets.add(target);
		ArrayList<EntityID> path = null;
		path = getNoBlockadePathToClosest(from, targets);
		if (path == null || path.size() <= 1) {
			return result;
		}
		if (path != null && path.size() > 1) {
			result = new ActionMove(path);
		}
		return result;
	}

	public void setChangeSetSeen() {
		if (lastSetChangeSetSeenTime >= ai.getTime()) {
			return;
		}
		lastSetChangeSetSeenTime = ai.getTime();
		if (changes == null) {
			return;
		}
		ArrayList<AURAreaGraph> ags = getAreaGraph(changes);
		for (AURAreaGraph ag : ags) {
			ag.setSeen();
		}

	}

	public void setChangeSetIfBurnt() {
		if (lastSetChangeSetIfBurnt >= ai.getTime()) {
			return;
		}
		lastSetChangeSetIfBurnt = ai.getTime();
		if (changes == null) {
			return;
		}
		ArrayList<AURAreaGraph> ags = getAreaGraph(changes);
		for (AURAreaGraph ag : ags) {
			if (ag.isBuilding()) {
				Building b = (Building) (ag.area);
				if (b.isFierynessDefined() && b.getFierynessEnum().equals(Fieryness.BURNT_OUT)) {
					ag.burnt = true;
				}
			}
		}

	}

	public LinkedList<AURAreaGraph> getAllRefillers() {
		LinkedList<AURAreaGraph> result = new LinkedList<AURAreaGraph>();
		for (AURAreaGraph ag : this.areas.values()) {
			if(ag.lastDijkstraEntranceNode == null) {
				continue;
			}
			if (ag.isRefuge()) {
				result.add(ag);
			} else if (ag.isHydrant()) {
				//if (ag.ownerAgent == agentOrder) {
				result.add(ag);
				//}
			}
		}
		return result;
	}

	public List<EntityID> getAllRefuges() {
		ArrayList<EntityID> result = new ArrayList<EntityID>();
		for (AURAreaGraph ag : this.areas.values()) {
			if (ag.isRefuge()) {
				result.add(ag.area.getID());
			}
		}
		return result;
	}

	public AURWorldGraph(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData) {
		super(ai, wi, si, moduleManager, developData);
		this.wi = wi;
		this.ai = ai;
		this.si = si;

		build();
		
	}

	public ArrayList<AURAreaGraph> getUnseens(Collection<EntityID> list) {
		ArrayList<AURAreaGraph> result = new ArrayList<>();
		for (AURAreaGraph ag : areas.values()) {
			if (ag.seen() == false) {
				if (list.contains(ag.area.getID())) {
					result.add(ag);
				}
			}
		}
		return result;
	}

	public ArrayList<AURAreaGraph> getUnburnts(Collection<EntityID> list) {
		ArrayList<AURAreaGraph> result = new ArrayList<>();
		for (AURAreaGraph ag : areas.values()) {
			if (ag.burnt == false) {
				if (list.contains(ag.area.getID())) {
					result.add(ag);
				}
			}
		}
		return result;
	}

	private ArrayList<StandardEntity> sortedTeamAgents = new ArrayList<>();

	@Override
	public AbstractModule calc() {
		return this;
	}

	public int getAgentColor() {
		if(agentColor != -1) {
			return agentColor;
		}
		LinkedList<StandardEntity> sortedTeamAgents = new LinkedList<>();
		sortedTeamAgents.clear();
		sortedTeamAgents.addAll(this.wi.getEntitiesOfType(this.ai.me().getStandardURN()));

		Collections.sort(sortedTeamAgents, new Comparator<StandardEntity>() {
			@Override
			public int compare(StandardEntity o1, StandardEntity o2) {
				return o1.getID().getValue() - o2.getID().getValue();
			}
		});
		agentColor = sortedTeamAgents.indexOf(this.ai.me()) % 4;
		return agentColor;
	}


	private int agentColor = -1;

	public void build() {
		long t = System.currentTimeMillis();
		areas.clear();
		AURAreaGraph ag;
		Area area;
		for (StandardEntity ent : wi.getAllEntities()) {
			if (ent instanceof Area) {
				area = (Area) ent;
				ag = new AURAreaGraph(area, this, instanceAreaGrid);
				areas.put(ent.getID(), ag);
				if (ag.isGasStation()) {
					gasStations.add(ag);
				}
			}
		}
		LinkedList<AURAreaGraph> list = new LinkedList<>(areas.values());
		Collections.sort(list, new Comparator<AURAreaGraph>() {
			@Override
			public int compare(AURAreaGraph o1, AURAreaGraph o2) {
				return o1.area.getID().getValue() - o2.area.getID().getValue();
			}
		});
		@SuppressWarnings("unused")
		Random random = new Random(90);
		int c = 0;
		for (AURAreaGraph ag_ : list) {
			ag_.color = c;
			c = (c + 1) % 4;
		}

		sortedTeamAgents.clear();
		sortedTeamAgents.addAll(wi.getEntitiesOfType(ai.me().getStandardURN()));

		Collections.sort(sortedTeamAgents, new Comparator<StandardEntity>() {
			@Override
			public int compare(StandardEntity o1, StandardEntity o2) {
				return o1.getID().getValue() - o2.getID().getValue();
			}
		});

		agentOrder = sortedTeamAgents.indexOf(ai.me());
		//System.out.println(agentOrder);
		c = 0;
		int agents = sortedTeamAgents.size();
		maxAgentOrder = agents;
		for (AURAreaGraph ag_ : list) {
			if (ag_.isHydrant()) {
				ag_.ownerAgent = c;
				c = (c + 1) % agents;
			}

		}
		setNeighbours();
		addBorders();
		addWalls();
		setCommonWalls();
		addPerceptibleBuildings();
		addSightableBuildings();
		
//		for(AURAreaGraph ag_ : areas.values()) {
//			if(ag_.isBuilding()) {
//				ag_.getBuilding().init();
//			}
//		}


//		int count = 0;
//		for(AURAreaGraph ag_ : areas.values()) {
//			if(ag_.isBuilding()) {
//				count++;
//			}
//		}
		
		
//		System.out.println("buildings: " + count);
		
		this.fireSimulator = new AURFireSimulator(this);
                
		updateInfo(null);

//		System.out.println("walls: " + walls.size());
		System.out.println("Graph build time: " + (System.currentTimeMillis() - t));
	}
	
	public void setCommonWalls() {
//		long t = System.currentTimeMillis();
		for(AURAreaGraph ag : areas.values()) {
			if(ag.isBuilding()) {
				ag.getBuilding().setCommonWalls();
			}
		}
//		System.out.println("setCommonWalls(): " + (System.currentTimeMillis() - t) + " ms");
	}
	
	public void addPerceptibleBuildings() {
		for(AURAreaGraph ag : areas.values()) {
			if(ag.isBuilding()) {
				ArrayList<AURAreaGraph> arr = ag.getBuilding().getPerceptibleAndExtinguishableAreas();
				if(arr.size() == 0) {
					continue;
				}
				
				for(AURAreaGraph ag_ : arr) {
					if(ag_.perceptibleAndExtinguishableBuildings == null) {
						ag_.perceptibleAndExtinguishableBuildings = new ArrayList<>();
					}
					ag_.perceptibleAndExtinguishableBuildings.add(ag.getBuilding());
				}
			}
		}
	}
	
	public void addSightableBuildings() {
		for(AURAreaGraph ag : areas.values()) {
			if(ag.isBuilding()) {
				ArrayList<AURAreaGraph> arr = ag.getBuilding().getSightableAreas();
				if(arr.size() == 0) {
					continue;
				}
				
				for(AURAreaGraph ag_ : arr) {
					if(ag_.sightableBuildings == null) {
						ag_.sightableBuildings = new ArrayList<>();
					}
					ag_.sightableBuildings.add(ag.getBuilding());
				}
			}
		}
	}
	
	public int maxAgentOrder = 0;

	public void addWalls() {
		for (AURAreaGraph ag : areas.values()) {
			if (ag.isBuilding() == false) {
				continue;
			}
			for (Edge edge : ag.area.getEdges()) {
				if (edge.isPassable() == false) {
					walls.add(new AURWall(ag, edge.getStartX(), edge.getStartY(), edge.getEndX(), edge.getEndY()));
				}
			}
		}
		ArrayList<AURWall> dels = new ArrayList<>();
		AURWall iWall;
		AURWall jWall;
		for (int i = 0; i < walls.size(); i++) {
			iWall = walls.get(i);
			if (iWall.vis == true) {
				continue;
			}
			for (int j = i + 1; j < walls.size(); j++) {
				jWall = walls.get(j);
				if (iWall.equals(jWall)) {
					dels.add(jWall);
					jWall.vis = true;
				}
			}
		}
		walls.removeAll(dels);
		walls.addAll(dels);
	}

	public void setNeighbours() {
		for (AURAreaGraph area : areas.values()) {
			area.vis = false;
			area.neighbours.clear();
		}
		AURAreaGraph nei;
		for (AURAreaGraph ag : areas.values()) {
			for (EntityID neiEntID : ag.area.getNeighbours()) {
				nei = areas.get(neiEntID);
				if (nei.vis) {
					continue;
				}
				if (ag.neighbours.contains(nei)) {
					continue;
				}
				ag.neighbours.add(nei);
				nei.neighbours.add(ag);
			}
			ag.vis = true;
		}
	}

	public void addBorders() {
		for (AURAreaGraph area : areas.values()) {
			area.vis = false;
			area.borders.clear();
		}
		ArrayList<AURBorder> commons;
		for (AURAreaGraph area : areas.values()) {
			for (AURAreaGraph nei : area.neighbours) {
				if (nei.vis) {
					continue;
				}
				commons = getCommonBorders(area, nei);
				area.borders.addAll(commons);
				nei.borders.addAll(commons);
			}
			area.vis = true;
			area.addBorderCenterEdges();
		}
	}
	
	public ArrayList<AURAreaGraph> getPerceptibleUnburntBuildingIDs() {
		this.dijkstra(ai.getPosition());
		ArrayList<AURAreaGraph> result = new ArrayList<>();
		for (AURAreaGraph ag : areas.values()) {
			if (true && ag.isBuilding() && ag.noSeeTime() > 0 && ag.burnt == false
					&& ag.getBuilding().edgeToPereceptAndExtinguish != null) {
				result.add(ag);
			}
		}
		return result;
	}

//	public ArrayList<AURAreaGraph> getReachableUnburntBuildingIDs() {
//		this.dijkstra(ai.getPosition());
//		ArrayList<AURAreaGraph> result = new ArrayList<>();
//		for (AURAreaGraph ag : areas.values()) {
//			if (true && ag.isBuilding() && ag.noSeeTime() > 0 && ag.burnt == false
//					&& ag.lastDijkstraEntranceNode != null) {
//				result.add(ag);
//			}
//		}
//		return result;
//	}
//
//	public ArrayList<AURAreaGraph> getNoBlockadeReachableUnburntBuildingIDs() {
//		this.NoBlockadeDijkstra(ai.getPosition());
//		ArrayList<AURAreaGraph> result = new ArrayList<>();
//		for (AURAreaGraph ag : areas.values()) {
//			if (true && ag.isBuilding() && ag.noSeeTime() > 0 && ag.burnt == false
//					&& ag.lastNoBlockadeDijkstraEntranceNode != null) {
//				result.add(ag);
//			}
//		}
//		return result;
//	}

	@Override
	synchronized public AbstractModule updateInfo(MessageManager messageManager) {
		if (updateTime >= ai.getTime()) {
			return this;
		}

		if (ai.getChanged() == null) {
			changes = new ArrayList<>();
		} else {
			changes = ai.getChanged().getChangedEntities();
		}

		updateTime = ai.getTime();
		this.setChangeSetSeen();
		this.setChangeSetIfBurnt();

		lastDijkstraFrom = null;
		lastNoBlockadeDijkstraFrom = null;
		ArrayList<AURAreaGraph> forceUpdate = new ArrayList<>();
		for (AURAreaGraph ag : areas.values()) {
			ag.update(this);
			if (ag.needUpdate) {
				for (AURAreaGraph neiAg : ag.neighbours) {
					forceUpdate.add(neiAg);
				}
			}
		}
		for (AURAreaGraph ag : forceUpdate) {
			ag.initForReCalc();
		}
		for (AURAreaGraph ag : areas.values()) {
			if (ag.needUpdate) {
				instanceAreaGrid.init(ag);
				instanceAreaGrid.setEdgePointsAndCreateGraph();
			}
		}
		
		this.dijkstra(this.ai.getPosition());
		
		this.fireSimulator.step();
		
		for (EntityID entID : changes) {
			AURAreaGraph ag = getAreaGraph(entID);
			if (ag != null) {
				ag.fireChecked = true;
			}
		}
		calcFireProbability();
		
		if(AURConstants.Viewer.LAUNCH == true) {
			K_Viewer.getInstance().update(this);
		}
		
		return this;
	}

	public AURAreaGraph getAreaGraph(EntityID id) {
		return areas.get(id);
	}

	public void initForDijkstra() {
		for (AURAreaGraph ag : areas.values()) {
			if(ag.isBuilding()) {
				ag.getBuilding().edgeToPereceptAndExtinguish = null;
				ag.getBuilding().edgeToSeeInside = null;
			}
			ag.vis = false;
			ag.lastDijkstraEntranceNode = null;
			for (AURBorder border : ag.borders) {
				for (AURNode node : border.nodes) {
					node.cost = AURConstants.Math.INT_INF;
					node.pre = null;
					node.pQueEntry = null;
				}
			}
		}
	}

	public void initForNoBlockadeDijkstra() {
		for (AURAreaGraph ag : areas.values()) {
			ag.vis = false;
			ag.lastNoBlockadeDijkstraEntranceNode = null;
			ag.pQueEntry = null;
			for (AURBorder border : ag.borders) {
				border.CenterNode.cost = AURConstants.Math.INT_INF;
				border.CenterNode.pre = null;
				border.CenterNode.pQueEntry = null;
			}
		}
	}

	public ArrayList<AURAreaGraph> getAreaGraph(Collection<EntityID> IDs) {
		ArrayList<AURAreaGraph> result = new ArrayList<AURAreaGraph>();
		for (EntityID ID : IDs) {
			AURAreaGraph ag = getAreaGraph(ID);
			if (ag != null) {
				result.add(ag);
			}
		}
		return result;
	}
	
	public ArrayList<EntityID> getPathToClosest(EntityID fromID, Collection<EntityID> targets) {
		ArrayList<EntityID> result = new ArrayList<>();
		dijkstra(fromID);
		if (targets.contains(fromID)) {
			result.add(fromID);
			return result;
		}
		ArrayList<AURAreaGraph> targetAgs = getAreaGraph(targets);
		AURAreaGraph closest = null;
		for (AURAreaGraph ag : targetAgs) {
			if (ag.lastDijkstraEntranceNode != null) {
				if (closest == null || closest.getTravelCost() > ag.getTravelCost()) {
					closest = ag;
				}
			}
		}
		if (closest == null) {
			return result;
		}
		AURNode node = closest.lastDijkstraEntranceNode;
		result.add(closest.area.getID());
		while (node.pre != startNullNode) {
			result.add(node.getPreAreaGraph().area.getID());
			node = node.pre;
		}
		result.add(fromID);
		java.util.Collections.reverse(result);
		return result;
	}

	public void dijkstra(EntityID fromID) {
		if (lastDijkstraFrom != null && lastDijkstraFrom.equals(fromID)) {
			return;
		}
		lastDijkstraFrom = fromID;
		initForDijkstra();
		AURAreaGraph fromAg = getAreaGraph(fromID);
		if (fromAg == null) {
			return;
		}
		fromAg.lastDijkstraEntranceNode = startNullNode;
		
		if(fromID.equals(this.ai.getPosition())) {
			ArrayList<AUREdgeToStand> etps = fromAg.getEdgesToPerceptiblePolygons((int) ai.getX(), (int) ai.getY());
			for(AUREdgeToStand etp : etps) {
				etp.fromNode.pre = startNullNode;
				etp.toSeeAreaGraph.getBuilding().edgeToPereceptAndExtinguish = etp;
			}
		}
		
		if(fromID.equals(this.ai.getPosition())) {
			ArrayList<AUREdgeToStand> etss = fromAg.getEdgesToSightPolygons((int) ai.getX(), (int) ai.getY());
			for(AUREdgeToStand ets : etss) {
				ets.fromNode.pre = startNullNode;
				ets.toSeeAreaGraph.getBuilding().edgeToSeeInside = ets;
			}
		}
		
		
		ArrayList<AURNode> startNodes = fromAg.getReachabeEdgeNodes(ai.getX(), ai.getY());
		if (startNodes.isEmpty()) {
			return;
		}
		FibonacciHeap<AURNode> que = new FibonacciHeap<>();
		for (AURNode node : startNodes) {
			node.pre = startNullNode;
			node.pQueEntry = que.enqueue(node, node.cost);
		}
		AURNode qNode = null;
		AURAreaGraph ag;
		AURNode toNode = null;
		while (que.isEmpty() == false) {
			
			qNode = que.dequeueMin().getValue();
			qNode.pQueEntry = null;
			if(qNode.edgesToPerceptAndExtinguish != null) {
				for(AUREdgeToStand etp : qNode.edgesToPerceptAndExtinguish) {
					if(etp.toSeeAreaGraph.getBuilding().edgeToPereceptAndExtinguish == null) {
						etp.standCost = etp.weight + qNode.cost;
						etp.toSeeAreaGraph.getBuilding().edgeToPereceptAndExtinguish = etp;
					} else {
						int oldCost = etp.toSeeAreaGraph.getBuilding().edgeToPereceptAndExtinguish.standCost;
						etp.standCost = etp.weight + qNode.cost;
						if(etp.standCost < oldCost) {
							etp.toSeeAreaGraph.getBuilding().edgeToPereceptAndExtinguish = etp;
						}
					}
				}
			}
			
			if(qNode.edgesToSeeInside != null) {
				for(AUREdgeToStand ets : qNode.edgesToSeeInside) {
					if(ets.toSeeAreaGraph.getBuilding().edgeToSeeInside == null) {
						ets.standCost = ets.weight + qNode.cost;
						ets.toSeeAreaGraph.getBuilding().edgeToSeeInside = ets;
					} else {
						int oldCost = ets.toSeeAreaGraph.getBuilding().edgeToSeeInside.standCost;
						ets.standCost = ets.weight + qNode.cost;
						if(ets.standCost < oldCost) {
							ets.toSeeAreaGraph.getBuilding().edgeToSeeInside = ets;
						}
					}
				}
			}

			
			ag = qNode.ownerArea1;
			if (ag.getTravelCost()> qNode.cost) {
				ag.lastDijkstraEntranceNode = qNode;
			}
			ag = qNode.ownerArea2;
			if (ag.getTravelCost() > qNode.cost) {
				ag.lastDijkstraEntranceNode = qNode;
			}
			for (AUREdge edge : qNode.edges) {

				int cost = (qNode.cost + edge.weight) //  * edge.areaGraph.areaCostFactor
						+ (1000 - edge.areaGraph.noSeeTime()) * 2;
				toNode = edge.nextNode(qNode);
				if (toNode.cost > cost) {
					toNode.cost = cost;

					if (toNode.pQueEntry == null) {
						toNode.pQueEntry = que.enqueue(toNode, toNode.cost);
					} else {
						que.decreaseKey(toNode.pQueEntry, toNode.cost);
					}
					toNode.pre = qNode;
				}
			}

		}
		
		for(AURAreaGraph ag_ : this.areas.values()) {
			if(ag_.isBuilding() == true) {
				if(ag_.lastDijkstraEntranceNode == null) {
					continue;
				}
				AURBuilding b = ag_.getBuilding();
				AURNode eNode = ag_.lastDijkstraEntranceNode;
				if(b.edgeToPereceptAndExtinguish == null) {
					double cost = AURGeoUtil.dist(eNode.x, eNode.y, ag_.getX(), ag_.getY());
					AUREdgeToStand e = new AUREdgeToStand(ag_, ag_, (int) cost, eNode, ag_.getX(), ag_.getY());
					b.edgeToPereceptAndExtinguish = e;
				}
				if(b.edgeToSeeInside == null) {
					double cost = AURGeoUtil.dist(eNode.x, eNode.y, ag_.getX(), ag_.getY());
					AUREdgeToStand e = new AUREdgeToStand(ag_, ag_, (int) cost, eNode, ag_.getX(), ag_.getY());
					b.edgeToSeeInside = e;
				}
			}
		}
		
	}

	private int lastSetChangeSetSeenTime = -1;
	private int lastSetChangeSetIfBurnt = -1;
	public LinkedList<AURAreaGraph> gasStations = new LinkedList<>();

	public int maxPerceptibleBuildings = -1;
	
	public int getMaxPerceptibleBuildings() {
		if(maxPerceptibleBuildings < 0) {
			maxPerceptibleBuildings = 0;
			for(AURAreaGraph ag : areas.values()) {
				if(ag.isBuilding() == false) {
					if(ag.perceptibleAndExtinguishableBuildings == null) {
						continue;
					}
					maxPerceptibleBuildings = Math.max(maxPerceptibleBuildings, ag.perceptibleAndExtinguishableBuildings.size());
				}
			}
		}
		return maxPerceptibleBuildings;
	}
	
	public double maxRoadArea = -1;
	
	public double getMaxRoadArea() {
		if(maxRoadArea < 0) {
			maxRoadArea = 0;
			for(AURAreaGraph ag : areas.values()) {
				if(ag.isBuilding() == false) {
					maxRoadArea = Math.max(maxRoadArea, AURGeoUtil.getArea((Polygon) ag.area.getShape()));
				}
			}
		}
		return maxRoadArea;
	}
	
	public double maxRoadPerimeter = -1;
	
	public double getMaxRoadPerimeter() {
		if(maxRoadPerimeter < 0) {
			maxRoadPerimeter = 0;
			for(AURAreaGraph ag : areas.values()) {
				if(ag.isBuilding() == false) {
					maxRoadPerimeter = Math.max(maxRoadPerimeter, AURGeoUtil.getPerimeter((Polygon) ag.area.getShape()));
				}
			}
		}
		return maxRoadArea;
	}
	
	
	public ArrayList<EntityID> getNoBlockadePathToClosest(EntityID fromID, Collection<EntityID> targets) {
		ArrayList<EntityID> result = new ArrayList<>();
		if (targets.contains(fromID)) {
			result.add(fromID);
			return result;
		}
		NoBlockadeDijkstra(fromID);
		ArrayList<AURAreaGraph> targetAgs = getAreaGraph(targets);
		AURAreaGraph closest = null;
		for (AURAreaGraph ag : targetAgs) {
			if (ag.lastNoBlockadeDijkstraEntranceNode != null) {
				if (closest == null || closest.getNoBlockadeTravelCost() > ag.getNoBlockadeTravelCost()) {
					closest = ag;
				}
			}
		}
		if (closest == null) {
			return result;
		}
		AURNode node = closest.lastNoBlockadeDijkstraEntranceNode;
		result.add(closest.area.getID());
		while (node.pre != startNullNode) {
			AURAreaGraph pag = node.getPreAreaGraph();
			result.add(pag.area.getID());
			node = node.pre;
		}
		result.add(fromID);
		java.util.Collections.reverse(result);
		return result;
	}

	public void NoBlockadeDijkstra(EntityID fromID) {
		if (lastNoBlockadeDijkstraFrom != null && lastNoBlockadeDijkstraFrom.equals(fromID)) {
			return;
		}
		lastNoBlockadeDijkstraFrom = fromID;
		initForNoBlockadeDijkstra();
		AURAreaGraph fromAg = getAreaGraph(fromID);
		if (fromAg == null) {
			return;
		}
		fromAg.lastNoBlockadeDijkstraEntranceNode = startNullNode;
		ArrayList<AURNode> startNodes = fromAg.getEdgeToAllBorderCenters(ai.getX(), ai.getY());
		if (startNodes.isEmpty()) {
			return;
		}
		FibonacciHeap<AURNode> que = new FibonacciHeap<>();
		for (AURNode node : startNodes) {
			node.pre = startNullNode;
			node.pQueEntry = que.enqueue(node, node.cost);
		}
		AURNode qNode = null;
		AURAreaGraph ag;
		AURNode toNode = null;
		while (que.isEmpty() == false) {
			qNode = que.dequeueMin().getValue();
			qNode.pQueEntry = null;
			ag = qNode.ownerArea1;
			if (ag.getNoBlockadeTravelCost() > qNode.cost) {
				ag.lastNoBlockadeDijkstraEntranceNode = qNode;
			}
			ag = qNode.ownerArea2;
			if (ag.getNoBlockadeTravelCost() > qNode.cost) {
				ag.lastNoBlockadeDijkstraEntranceNode = qNode;
			}
			for (AUREdge edge : qNode.edges) {
				toNode = edge.nextNode(qNode);
				int cost = (qNode.cost + edge.weight) + 0;
				if (toNode.cost > cost) {
					toNode.cost = cost;

					if (toNode.pQueEntry == null) {
						toNode.pQueEntry = que.enqueue(toNode, toNode.cost);
					} else {
						que.decreaseKey(toNode.pQueEntry, toNode.cost);
					}
					toNode.pre = qNode;
				}
			}
		}
	}

	public boolean isPassable(EntityID fromID, double fromX, double fromY, EntityID viaID, EntityID toID) {
		AURAreaGraph fromAg = this.getAreaGraph(fromID);
		ArrayList<AURNode> exitNodes = fromAg.getReachabeEdgeNodes(fromX, fromY);
		if (exitNodes == null || exitNodes.size() == 0) {
			return false;
		}
		for (AURNode node : exitNodes) {
			for (AUREdge nextEdge : node.edges) {
				if (nextEdge.areaGraph.area.getID().equals(viaID)) {
					if (nextEdge.getNextAreaGraph(node).area.getID().equals(toID)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean noBlockadeIsPassable(EntityID fromID, double fromX, double fromY, EntityID viaID, EntityID toID) {
		AURAreaGraph fromAg = this.getAreaGraph(fromID);
		ArrayList<AURNode> exitNodes = fromAg.getEdgeToAllBorderCenters(fromX, fromY);
		if (exitNodes == null || exitNodes.size() == 0) {
			return false;
		}
		for (AURNode node : exitNodes) {
			for (AUREdge nextEdge : node.edges) {
				if (nextEdge.areaGraph.area.getID().equals(viaID)) {
					if (nextEdge.getNextAreaGraph(node).area.getID().equals(toID)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public double getEdgeLength(Edge edge) {
		return AURGeoUtil.dist(edge.getStartX(), edge.getStartY(), edge.getEndX(), edge.getEndY());
	}

	public ArrayList<AURBorder> getCommonBorders(AURAreaGraph a1, AURAreaGraph a2) {
		ArrayList<AURBorder> result = new ArrayList<AURBorder>();
		for (Edge e1 : a1.area.getEdges()) {
			if (e1.isPassable() && getEdgeLength(e1) > 1) {
				for (Edge e2 : a2.area.getEdges()) {
					if (e2.isPassable() && AURGeoUtil.equals(e1, e2)) {
						result.add(new AURBorder(a1, a2, e1.getStartX(), e1.getStartY(), e1.getEndX(), e1.getEndY()));
					}
				}
			}
		}

		return result;
	}
}
