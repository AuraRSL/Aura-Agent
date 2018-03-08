package viewer;

import viewer.layers.knd.*;
import viewer.layers.AmboLayers.*;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class K_LayerAdder {
	
	public static void addTo(K_Viewer viewer) {
		
		viewer.addLayer(K_AreaPropery.class, true);
		viewer.addLayer(K_LayerRoads.class, true);
		viewer.addLayer(K_LayerBuildings.class, true);
		viewer.addLayer(K_LayerBuildingsClusterColor.class, false);
		viewer.addLayer(K_LayerAreaCenters.class, false);
		viewer.addLayer(k_LayerReachableAreas.class, false);
		viewer.addLayer(K_LayerTravelCost.class, false);
		viewer.addLayer(K_LayerAliveBlockades.class, true);
		viewer.addLayer(K_LayerAllBlockades.class, true);
		viewer.addLayer(K_LayerWalls.class, true);
		viewer.addLayer(K_LayerWorldGraph.class, false);
		viewer.addLayer(K_NoBlockadeWorldGraph.class, false);
		viewer.addLayer(K_ShortestPath.class, false);
		viewer.addLayer(K_NoBlockadeShortestPath.class, false);
		viewer.addLayer(K_AreaVertices.class, false);
		viewer.addLayer(K_AreaExtinguishableRange.class, false);
		viewer.addLayer(CivilianLayer.class, true);
		viewer.addLayer(K_AirCells.class, false);
		viewer.addLayer(K_BuildingAirCells.class, false);
		viewer.addLayer(K_AreaGrid.class, false);
		viewer.addLayer(K_AreaPassableSegments.class, false);
		viewer.addLayer(K_AreaGraph.class, false);
		viewer.addLayer(K_BuildingPerceptibleAreas.class, false);
		viewer.addLayer(K_PerceptibleAreaPolygon.class, false);
		viewer.addLayer(K_PerceptibleBuildings.class, false);
		viewer.addLayer(K_ShortestPathToCheckFire.class, false);
		viewer.addLayer(K_RoadScore.class, false);
		viewer.addLayer(K_SmallAreas.class, false);
		viewer.addLayer(K_MediumAreas.class, false);
		viewer.addLayer(K_BigAreas.class, false);
		viewer.addLayer(K_CommonWalls.class, false);
		viewer.addLayer(K_BuildingSightAreaPolygon.class, false);
		viewer.addLayer(K_BuildingCodes.class, false);
		viewer.addLayer(K_RealFieryBuildings.class, true);
		viewer.addLayer(K_EstimatedFieryness.class, true);
		viewer.addLayer(K_FireSimBuildingInfo.class, true);
		viewer.addLayer(K_InflammableBuildings.class, false);
		viewer.addLayer(K_AgentsLayer.class, true);
		viewer.addLayer(K_ConnectedBuildings.class, false);
		
	}
	
}
