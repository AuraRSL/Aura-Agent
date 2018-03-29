package AUR.util.aslan;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURConstants;
import AUR.util.knd.AURWorldGraph;
import adf.agent.communication.MessageManager;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.component.module.AbstractModule;
import adf.component.module.algorithm.Clustering;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.PoliceForce;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author Amir Aslan Aslani - Mar 2018
 */
public class AURPoliceScoreGraph extends AbstractModule {
        public HashMap<EntityID, AURAreaGraph> areas = new HashMap<>();
        private final Clustering clustering;
        public AgentInfo ai;
        public WorldInfo wi;
        public ScenarioInfo si;
        public AURWorldGraph wsg;
        
        public ArrayList<AURAreaGraph> areasForScoring = new ArrayList<>();
        
        public Collection<EntityID> clusterEntityIDs;
        int cluseterIndex;
        double myClusterCenter[] = new double[2];
        StandardEntity myClusterCenterEntity = null;
        double maxDistToCluster = 0;
        
        double maxDisFromAgentStartPoint = 0;
        
        public AURPoliceScoreGraph(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData) {
                super(ai, wi, si, moduleManager, developData);
                this.ai = ai;
                this.si = si;
                this.wi = wi;
                
                this.wsg = moduleManager.getModule("knd.AuraWorldGraph");
                
                this.clustering = moduleManager.getModule("SampleRoadDetector.Clustering", "AUR.module.algorithm.AURMapClusterer");
                this.cluseterIndex = this.clustering.calc().getClusterIndex(ai.me());
                this.clusterEntityIDs = this.clustering.getClusterEntityIDs(cluseterIndex);
                
                fillLists();
                setScores();
        }

        private void fillLists(){
                this.areas = wsg.areas;
                
                // ---
                
                int counter = 0;
                for(StandardEntity se : this.clustering.getClusterEntities(cluseterIndex)){
                        if(se instanceof Area){
                                myClusterCenter[0] += ((Area) se).getX();
                                myClusterCenter[1] += ((Area) se).getY();
                                counter ++;
                        }
                }
                myClusterCenter[0] /= counter;
                myClusterCenter[1] /= counter;
                double dis = Double.MAX_VALUE;
                
                for(StandardEntity se : this.clustering.getClusterEntities(cluseterIndex)){
                        if(se instanceof Area && Math.hypot(myClusterCenter[0] - ((Area) se).getX(), myClusterCenter[1] - ((Area) se).getY()) < dis){
                                dis = Math.hypot(myClusterCenter[0] - ((Area) se).getX(), myClusterCenter[1] - ((Area) se).getY());
                                myClusterCenterEntity = se;
                        }
                }
                myClusterCenter[0] = ((Area) myClusterCenterEntity).getX();
                myClusterCenter[1] = ((Area) myClusterCenterEntity).getY();
                
                // ---
                
                for(AURAreaGraph entity : wsg.areas.values()){
                        if( Math.hypot(entity.getX() - myClusterCenter[0], entity.getY() - myClusterCenter[1]) > maxDistToCluster){
                                maxDistToCluster = Math.hypot(entity.getX() - myClusterCenter[0], entity.getY() - myClusterCenter[1]);
                        }
                }
                maxDistToCluster += 50;
                
                // --
                
                Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> worldBounds = wi.getWorldBounds();
                
                int[][] bound = new int[][]{
                        {worldBounds.first().first(),worldBounds.first().second()},
                        {worldBounds.second().first(),worldBounds.second().second()},
                        {worldBounds.first().first(),worldBounds.second().second()},
                        {worldBounds.second().first(),worldBounds.first().second()}
                };
                for(int[] point : bound){
                        if(Math.hypot(point[0] - agentInfo.getX(), point[1] - agentInfo.getY()) > maxDisFromAgentStartPoint)
                                maxDisFromAgentStartPoint = Math.hypot(point[0] - agentInfo.getX(), point[1] - agentInfo.getY());
                }
        }
        
        @Override
        public AbstractModule calc() {
                return this;
        }
        
        public EntityID getAreaWithMaximumScore(){
                return this.areasForScoring.get(0).area.getID();
        }

        @Override
        public AbstractModule updateInfo(MessageManager messageManager) {
                long sTime = System.currentTimeMillis();
                System.out.println("Updating RoadDetector Scores...");
                
                wsg.updateInfo(messageManager);
                wsg.NoBlockadeDijkstra(ai.getPosition());
                wsg.dijkstra(ai.getPosition());
                
                // Set dynamic scores
                decreasePoliceAreasScore(AURConstants.RoadDetector.DECREASE_POLICE_AREA_SCORE);
                setDeadPoliceClusterScore(AURConstants.RoadDetector.SecondaryScore.DEAD_POLICE_CLUSTER / this.clustering.getClusterNumber() * 2);
                setReleasedAgentStartEntityScore(AURConstants.RoadDetector.SecondaryScore.RELEASED_AGENTS_START_POSITION_SCORE);
                
                if(ai.getChanged().getChangedEntities() != null){
                        for(EntityID eid : ai.getChanged().getChangedEntities()){
                                setFiredBuildingsScore(eid, AURConstants.RoadDetector.SecondaryScore.FIRED_BUILDING);
                                setBlockedHumansScore(eid, AURConstants.RoadDetector.SecondaryScore.BLOCKED_HUMAN);
                                setRoadsWithoutBlockadesScore(eid, AURConstants.RoadDetector.SecondaryScore.ROADS_WITHOUT_BLOCKADES);
                                setOpenBuildingsScore(eid); // Score (Range) is (0 - ) 1 (Because of default value of targetScore)
                        }
                }
                
                for(AURAreaGraph area : wsg.areas.values()){
                        setDistanceScore(area, AURConstants.RoadDetector.SecondaryScore.DISTANCE);
                }
                
                areasForScoring.sort(new AURPoliceAreaScoreComparator());
                
                System.out.println("PSG UpdateInfo Time (Contains WSG UpdateInfo): " + (System.currentTimeMillis() - sTime));
                return this;
        }

        private void setScores() {
                long sTime = System.currentTimeMillis();
                
                wsg.NoBlockadeDijkstra(ai.getPosition());
                
                decreasePoliceAreasScore(AURConstants.RoadDetector.DECREASE_POLICE_AREA_SCORE);
                
                setPoliceForceScore(AURConstants.RoadDetector.BaseScore.POLICE_FORCE);
                setFireBrigadeScore(AURConstants.RoadDetector.BaseScore.FIRE_BRIGADE);
                setAmbulanceTeamScore(AURConstants.RoadDetector.BaseScore.AMBULANCE_TEAM);
                
                for(AURAreaGraph area : wsg.areas.values()){
                        /* Distance Score */
                        setDistanceScore(area, AURConstants.RoadDetector.BaseScore.DISTANCE);
                        
                        /* Building Importance */
                        addRefugeScore(area, AURConstants.RoadDetector.BaseScore.REFUGE);
                        addGasStationScore(area, AURConstants.RoadDetector.BaseScore.GAS_STATION);
                        addHydrandScore(area, AURConstants.RoadDetector.BaseScore.HYDRANT);
                        
                        /* WSG Road Score */
                        addWSGRoadScores(area, AURConstants.RoadDetector.BaseScore.WSG_ROAD);
                        
                        /* Cluster Score */
                        addClusterScore(area, AURConstants.RoadDetector.BaseScore.CLUSTER);
                        
                        /* Entrance Number Score */
                        addEntrancesNumberScore(area, AURConstants.RoadDetector.BaseScore.ENTRANCES_NUMBER);
                }
                areasForScoring.addAll(wsg.areas.values());
                areasForScoring.sort(new AURPoliceAreaScoreComparator());
                
                System.out.println("PSG Setting Base Scores Time: " + (System.currentTimeMillis() - sTime));
        }

        private void addScoreToCollection(ArrayList<EntityID> collection, double score) {
                for(EntityID eid : collection){
                        areas.get(eid).baseScore += score;
                }
        }

        private void addWSGRoadScores(AURAreaGraph area, double score) {
                if(area.isRoad()){
                        area.baseScore += area.getScore() * score;
                }
                else{
                        area.baseScore += score / 2;
                }
        }

        private void addRefugeScore(AURAreaGraph area, double score) {
                if(area.isRefuge()){
                        for(AURAreaGraph ag : area.neighbours){
                                if(ag.isRoad()){
                                        ag.baseScore += score * 2 / 3;
                                }
                        }
                        area.baseScore += score;
                }
        }

        private void addClusterScore(AURAreaGraph area, double score) {
                if(clusterEntityIDs.contains(area.area.getID())){
                        
                }
                else{
                        double distanceFromCluster = Math.hypot(area.getX() - myClusterCenter[0], area.getY() - myClusterCenter[1]) / this.maxDistToCluster;
                        score *= (1 - distanceFromCluster) * 2 / 3;
                }
                
                if(area.isRoad()){
                        score /= 2;
                }
                
                area.baseScore += score;
        }
        
        HashSet<EntityID> visitedAreas = new HashSet<>();
        private void decreasePoliceAreasScore(double score) {
                setTargetAsReached(ai.getPosition(), score);
        }

        private void setTargetAsReached(EntityID entity,double score){
                this.areas.get(entity).targetScore = score;
                visitedAreas.add(entity);
        }
        
        public void setTargetScore(EntityID entity,double score){
                this.areas.get(entity).secondaryScore += score;
        }
        
        private void setBlockedHumansScore(EntityID eid, double score) {
                StandardEntity entity = wi.getEntity(eid);
                if(entity instanceof Civilian || entity instanceof AmbulanceTeam || entity instanceof FireBrigade){
                        AURAreaGraph pos = wsg.getAreaGraph(((Human) entity).getPosition());
                        if(pos.getTravelCost() == AURConstants.Math.INT_INF || pos.getNoBlockadeTravelCost() * 4 < pos.getTravelCost()){
                                pos.secondaryScore += score;
                        }
                }
        }

        private void addGasStationScore(AURAreaGraph area, double score) {
                if(area.isGasStation()){
                        area.baseScore += score;
                        for(AURAreaGraph neigs : area.neighbours){
                                neigs.baseScore += score;
                        }
                }
        }

        private void addHydrandScore(AURAreaGraph area, double score) {
                if(! area.isHydrant()){
                        score = 0;
                }
                area.baseScore += score;
        }

        private void setDistanceScore(AURAreaGraph area, double score) {
                area.distanceScore = (1 - score) + Math.min((AURConstants.RoadDetector.DIST_SCORE_COEFFICIENT / (double) area.getNoBlockadeTravelCost()) * score, score);
        }

        HashSet<EntityID> visitedDeadPoliceForces = new HashSet<>();
        private void setDeadPoliceClusterScore(double score) {
                for(StandardEntity se : wi.getEntitiesOfType(StandardEntityURN.POLICE_FORCE)){
                        if(! visitedDeadPoliceForces.contains(se.getID()) && ((PoliceForce) se).isHPDefined() && ((PoliceForce) se).getHP() < 100 && ! se.getID().equals(ai.getID())){
                                visitedDeadPoliceForces.add(se.getID());
                                int clusterIndex = this.clustering.getClusterIndex(se);
                                for(AURAreaGraph area : wsg.getAreaGraph(clustering.getClusterEntityIDs(clusterIndex))){
                                        area.baseScore += score;
                                }
                        }         
                }
        }

        HashMap<EntityID,AURAreaGraph> agentsInBuildings = new HashMap<>();
        
        private void setReleasedAgentStartEntityScore(double score){
                for(EntityID eid : ai.getChanged().getChangedEntities()){
                        if(agentsInBuildings.containsKey(eid) && wsg.getAreaGraph(((Human) wi.getEntity(eid)).getPosition()).isRoad()){
                                wsg.getAreaGraph(((Human) wi.getEntity(eid)).getPosition()).targetScore = score;
                                agentsInBuildings.remove(eid);
                        }
                }
        }
        
        private void setPoliceForceScore(double score) {
                for(StandardEntity se : wi.getEntitiesOfType(StandardEntityURN.POLICE_FORCE)){
                        if(se.getID().equals(ai.getID())){
                                continue;
                        }
                        
                        AURAreaGraph areaGraph = wsg.getAreaGraph(((PoliceForce) se).getPosition());
                        
                        if(areaGraph.isBuilding()){
                                agentsInBuildings.put(se.getID(),areaGraph);
                                areaGraph.baseScore += score;
                        }
                }
        }
        
        private void setFireBrigadeScore(double score) {
                for(StandardEntity se : wi.getEntitiesOfType(StandardEntityURN.POLICE_FORCE)){
                        AURAreaGraph areaGraph = wsg.getAreaGraph(((PoliceForce) se).getPosition());
                        
                        if(areaGraph.isBuilding()){
                                agentsInBuildings.put(se.getID(),areaGraph);
                                score *= 2.0 / 3.0;
                        }
                        
                        areaGraph.baseScore += score;
                }
        }

        private void setAmbulanceTeamScore(double score) {
                for(StandardEntity se : wi.getEntitiesOfType(StandardEntityURN.POLICE_FORCE)){
                        AURAreaGraph areaGraph = wsg.getAreaGraph(((PoliceForce) se).getPosition());
                        
                        if(areaGraph.isBuilding()){
                                agentsInBuildings.put(se.getID(),areaGraph);
                                score *= 2.0 / 3.0;
                        }
                        
                        areaGraph.baseScore += score;
                }
        }

        private void setRoadsWithoutBlockadesScore(EntityID eid, double score) {
                AURAreaGraph areaGraph = wsg.getAreaGraph(eid);

                if(areaGraph != null && areaGraph.isRoad() &&
                   areaGraph.area.isBlockadesDefined() &&
                   areaGraph.area.getBlockades().isEmpty()){

                        areaGraph.targetScore = score;
                }
        }

        private void setOpenBuildingsScore(EntityID eid) {
                AURAreaGraph areaGraph = wsg.getAreaGraph(eid);
                if(areaGraph != null && areaGraph.isBuilding()){
                        int all = 0, open = 0;

                        for(AURAreaGraph ag : areaGraph.neighbours){
                                Edge edgeTo = ag.area.getEdgeTo(areaGraph.area.getID());
                                if(edgeTo.isPassable()){
                                        all ++;

                                        if(!ag.area.isBlockadesDefined()){
                                                continue;
                                        }

                                        int size = AURPoliceUtil.filterAlirezaPathBug(wsg.getPathToClosest(ai.getPosition(), Lists.newArrayList(ag.area.getID()))).size();
                                        int size1 = AURPoliceUtil.filterAlirezaPathBug(wsg.getPathToClosest(ai.getPosition(), Lists.newArrayList(areaGraph.area.getID()))).size();
                                        if(size1 != 0 && Math.abs(size - size1) == 1){
                                                open ++;
                                        }
                                }
                        }

                        areaGraph.targetScore = Math.min((all - open) / all, areaGraph.targetScore);
                }
        }

        private void setFiredBuildingsScore(EntityID eid, double score) {
                AURAreaGraph areaGraph = wsg.getAreaGraph(eid);
                if(areaGraph != null && areaGraph.isBuilding()){
                        Building b = (Building) areaGraph.area;
                        if(b.isFierynessDefined()){
                                if(b.getFieryness() >= 4){
                                        areaGraph.targetScore = 0;
                                }
                                else if (b.getFieryness() > 0){
                                        areaGraph.secondaryScore += score;
                                }
                        }
                }
        }

        private void addEntrancesNumberScore(AURAreaGraph area, double score) {
                if(area.borders.size() == 0)
                        return;
                
                double coo = 1 - (1 / area.borders.size());
                score *= coo;
                
                area.baseScore += score;
        }

}
