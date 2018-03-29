package viewer;

import viewer.layers.knd.K_AreaNoSeeTime;
import viewer.layers.knd.*;
import viewer.layers.AmboLayers.*;
import viewer.layers.aslan.*;

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

	public final static String TAB_POLICE = "Police";

	public final static String TAB_AMBULANCE ="Ambulance";

	
	public static void addTo(K_Viewer viewer) {
		viewer.addLayer(TAB_MAP, K_AreaPropery.class, true);
		viewer.addLayer(TAB_MAP, K_LayerRoads.class, true);
		viewer.addLayer(TAB_MAP, K_LayerBuildings.class, true);
		viewer.addLayer(TAB_MAP, K_LayerAliveBlockades.class, true);
		viewer.addLayer(TAB_MAP, K_LayerAllBlockades.class, true);
		viewer.addLayer(TAB_MAP, K_LayerWalls.class, true);
		viewer.addLayer(TAB_MAP, CivilianLayer.class, true);
		viewer.addLayer(TAB_MAP, K_AgentsLayer.class, true);

                
		viewer.addLayer(TAB_MAP, A_SelectedArea.class, true);

		viewer.addLayer(TAB_MAP, RefugeLayer.class, true);
		viewer.addLayer(TAB_MAP, SelectedArea.class, true);

		
		viewer.addLayer(TAB_CLUSTERING, K_LayerBuildingsClusterColor.class, false);
		viewer.addLayer(TAB_CLUSTERING, AgentClusterLayer.class, false);
		viewer.addLayer(TAB_CLUSTERING, A_LayerMapClusterColor.class, false);
		
		viewer.addLayer(TAB_MISC, K_RoadScore.class, false);
		viewer.addLayer(TAB_MISC, K_BuildingCodes.class, false);
		viewer.addLayer(TAB_MISC, K_LayerAreaCenters.class, false);
		viewer.addLayer(TAB_MISC, K_AreaVertices.class, false);
		viewer.addLayer(TAB_MISC, K_CommonWalls.class, false);
		viewer.addLayer(TAB_MISC, K_SmallAreas.class, false);
		viewer.addLayer(TAB_MISC, K_MediumAreas.class, false);
		viewer.addLayer(TAB_MISC, K_BigAreas.class, false);
		viewer.addLayer(TAB_MISC, K_AreaNoSeeTime.class, false);
		
		viewer.addLayer(TAB_FIRE, K_AirCells.class, false);
		viewer.addLayer(TAB_FIRE, K_BuildingAirCells.class, false);
		viewer.addLayer(TAB_FIRE, K_RealFieryBuildings.class, false);
		viewer.addLayer(TAB_FIRE, K_EstimatedFieryness.class, false);
		viewer.addLayer(TAB_FIRE, K_FireSimBuildingInfo.class, false);
		viewer.addLayer(TAB_FIRE, K_InflammableBuildings.class, false);
		viewer.addLayer(TAB_FIRE, K_ConnectedBuildings.class, false);
		viewer.addLayer(TAB_FIRE, K_AreaExtinguishableRange.class, false);
		
		viewer.addLayer(TAB_SCENARIO, K_AgentExtinguishRange.class, false);
		viewer.addLayer(TAB_SCENARIO, K_AgentPerceptionRange.class, false);
		viewer.addLayer(TAB_SCENARIO, K_FireScenarioInfo.class, false);
		
		viewer.addLayer(TAB_PATH_PLANNING, K_LayerWorldGraph.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_NoBlockadeWorldGraph.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_AreaGraph.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_AreaGrid.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_AreaBorders.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_ShortestPath.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_NoBlockadeShortestPath.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_BuildingPerceptibleAreas.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_PerceptibleAndExtinguishableBuildings.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_ShortestPathToCheckFire.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_BuildingSightAreaPolygon.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_PerceptibleAreaPolygon.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_PerceptibleAndExtinguishablePolygon.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_LayerTravelCost.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, k_LayerReachableAreas.class, false);

		viewer.addLayer(TAB_PATH_PLANNING, K_TravelTime.class, false);
		
		viewer.addLayer(TAB_MAP, A_AreasEntityID.class, false);
		viewer.addLayer(TAB_MAP, A_BlockadesEntityID.class, false);
		viewer.addLayer(TAB_POLICE, A_PoliceClearAreaAndAgentsInThat.class, false);
		viewer.addLayer(TAB_POLICE, A_BuildingsEntrancePerpendicularLine.class, false);
		viewer.addLayer(TAB_POLICE, A_BuildingBlockadeEstimator.class, false);
		viewer.addLayer(TAB_POLICE, A_WorldBlockadeEstimator.class, false);
		viewer.addLayer(TAB_POLICE, A_AreasRoadDetectorScore.class, false);
		viewer.addLayer(TAB_POLICE, A_AreasThatBlockadesNotDefined.class, false);
		viewer.addLayer(TAB_POLICE, A_AroundEntrances.class, false);

		viewer.addLayer(TAB_PATH_PLANNING, K_TravelTime.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_BuildingSightableAreas.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_SightableBuildings.class, false);
		viewer.addLayer(TAB_PATH_PLANNING, K_ShortestPathSeeInside.class, false);


		viewer.addLayer(TAB_AMBULANCE, AgentBig.class, false);
		viewer.addLayer(TAB_AMBULANCE, CivilianID.class, false);
		viewer.addLayer(TAB_AMBULANCE, WorkOnIt.class, false);
		viewer.addLayer(TAB_AMBULANCE, Transport.class, true);
		viewer.addLayer(TAB_AMBULANCE, BestRefugeForCivilian.class, false);
		viewer.addLayer(TAB_AMBULANCE, CivilianInSideBuldingInfo.class, false);
		viewer.addLayer(TAB_AMBULANCE, CivilianRate.class, false);
		viewer.addLayer(TAB_AMBULANCE, CivilianDeathTimeLayer.class, false);
		viewer.addLayer(TAB_AMBULANCE, WorstCaseDeathTime.class, false);
		viewer.addLayer(TAB_AMBULANCE, CivilianSaveTimeLayer.class, false);
		viewer.addLayer(TAB_AMBULANCE, RepresentCanNotRescueCivilian.class, false);
		viewer.addLayer(TAB_AMBULANCE, SightPolygonLayer.class, false);
		

	}
	
}
