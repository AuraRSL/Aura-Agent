package viewer;

import viewer.layers.knd.*;
import viewer.layers.AmboLayers.*;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class K_LayerAdder {
	
	public final static String TAB_MAP = "Map";
	public final static String TAB_PATH_PLANNING = "PathPlanning";
	public final static String TAB_SCENARIO = "Scenario";
	public final static String TAB_FIRE = "Fire";
	public final static String TAB_CLUSTERING = "Clustering";
	public final static String TAB_MISC = "Misc";
	
	public static void addTo(K_Viewer viewer) {
		
		viewer.addLayer(TAB_MAP, K_AreaPropery.class, true);
		viewer.addLayer(TAB_MAP, K_LayerRoads.class, true);
		viewer.addLayer(TAB_MAP, K_LayerBuildings.class, true);
		viewer.addLayer(TAB_MAP, K_LayerAliveBlockades.class, true);
		viewer.addLayer(TAB_MAP, K_LayerAllBlockades.class, true);
		viewer.addLayer(TAB_MAP, K_LayerWalls.class, true);
		viewer.addLayer(TAB_MAP, CivilianLayer.class, true);
		viewer.addLayer(TAB_MAP, K_AgentsLayer.class, true);
		
		viewer.addLayer(TAB_CLUSTERING, K_LayerBuildingsClusterColor.class, false);
		
		viewer.addLayer(TAB_MISC, K_RoadScore.class, false);
		viewer.addLayer(TAB_MISC, K_BuildingCodes.class, false);
		viewer.addLayer(TAB_MISC, K_LayerAreaCenters.class, false);
		viewer.addLayer(TAB_MISC, K_AreaVertices.class, false);
		viewer.addLayer(TAB_MISC, K_CommonWalls.class, false);
		viewer.addLayer(TAB_MISC, K_SmallAreas.class, false);
		viewer.addLayer(TAB_MISC, K_MediumAreas.class, false);
		viewer.addLayer(TAB_MISC, K_BigAreas.class, false);
		
		viewer.addLayer(TAB_FIRE, K_AirCells.class, false);
		viewer.addLayer(TAB_FIRE, K_BuildingAirCells.class, false);
		viewer.addLayer(TAB_FIRE, K_RealFieryBuildings.class, true);
		viewer.addLayer(TAB_FIRE, K_EstimatedFieryness.class, true);
		viewer.addLayer(TAB_FIRE, K_FireSimBuildingInfo.class, true);
		viewer.addLayer(TAB_FIRE, K_InflammableBuildings.class, false);
		viewer.addLayer(TAB_FIRE, K_ConnectedBuildings.class, false);
		viewer.addLayer(TAB_FIRE, K_AreaExtinguishableRange.class, false);
		
		viewer.addLayer(TAB_SCENARIO, K_AgentExtinguishRange.class, false);
		viewer.addLayer(TAB_SCENARIO, K_FireScenarioInfo.class, true);
		
		viewer.addLayer(TAB_PATH_PLANNING, K_LayerWorldGraph.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_NoBlockadeWorldGraph.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_AreaGraph.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_AreaGrid.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_AreaPassableSegments.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_ShortestPath.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_NoBlockadeShortestPath.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_BuildingPerceptibleAreas.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_PerceptibleAreaPolygon.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_PerceptibleBuildings.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_ShortestPathToCheckFire.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_BuildingSightAreaPolygon.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_LayerTravelCost.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, k_LayerReachableAreas.class, false);
		
		
	}
	
}
