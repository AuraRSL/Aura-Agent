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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.Area;
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
        
        public PriorityQueue<AURAreaGraph> pQueue = new PriorityQueue<>(new AURPoliceAreaScoreComparator());
        
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
                
                this.clustering = moduleManager.getModule("SampleRoadDetector.Clustering", "AUR.module.algorithm.AURBuildingClusterer");
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

        @Override
        public AbstractModule updateInfo(MessageManager messageManager) {
                pQueue.clear();
                wsg.NoBlockadeDijkstra(ai.getPosition());
                wsg.dijkstra(ai.getPosition());
                
                // Set dynamic scores
                decreasePoliceAreasScore(0.8);
                setDeadPoliceClusterScore(0.3 / this.clustering.getClusterNumber() * 2);
                setBlockedHumansScore(0.4);
                
                for(AURAreaGraph area : wsg.areas.values()){
                        setDistanceScore(area, 0.1);
                        setAliveBlockadesScore(area, 0.0);
                        setBroknessScore(area, 0.1);
                }
                pQueue.addAll(wsg.areas.values());
                return this;
        }

        private void setScores() {
                wsg.NoBlockadeDijkstra(ai.getPosition());
                
                addAreasConflictScore(0.02);
                
                for(AURAreaGraph area : wsg.areas.values()){
                        /* Distance Score 0.2 */
                        setDistanceScore(area, 0.2);
                        
                        /* Building Importance 1.5 */
                        addRefugeScore(area, 0.15);
                        addGasStationScore(area, 0.075);
                        addHydrandScore(area, 0.05);
                        
                        /* WSG Road Score 0.075 */
                        addWSGRoadScores(area, 0.075);
                        
                        /* Cluster Score 0.3 */
                        addClusterScore(area, 0.3);
                }
                pQueue.addAll(wsg.areas.values());
                
        }
        
        private void addAreasConflictScore(double score) {
                
        }

        private void addScoreToCollection(ArrayList<EntityID> collection, double score) {
                for(EntityID eid : collection){
                        areas.get(eid).baseScore += score;
                }
        }

        private void addWSGRoadScores(AURAreaGraph area, double score) {
                area.baseScore += area.getScore() * score;
        }

        private void addRefugeScore(AURAreaGraph area, double score) {
                for(AURAreaGraph ag : area.neighbours){
                        if(ag.isRoad()){
                                ag.baseScore += score;
                        }
                }
                if(area.isRefuge()){
                        area.baseScore += score;
                }
        }

        private void addClusterScore(AURAreaGraph area, double score) {
                if(clusterEntityIDs.contains(area.area.getID())){
                        
                }
                else{
                        
                        double distanceFromCluster = Math.hypot(area.getX() - myClusterCenter[0], area.getY() - myClusterCenter[1]) / this.maxDistToCluster;
                        score *= (1 - distanceFromCluster);
                }
                area.baseScore += score;
        }
        
        HashSet<EntityID> visitedAreas = new HashSet<>();
        private void decreasePoliceAreasScore(double score) {
                setTargetAsReached(ai.getPosition(), score);
        }

        private void setTargetAsReached(EntityID entity,double score){
                if(! visitedAreas.contains(entity)){
                        setTargetScore(entity, 0);
                        this.areas.get(entity).secondaryScore += 0.15 * score;
                        visitedAreas.add(entity);
                }
        }
        
        public void setTargetScore(EntityID entity,double score){
                this.areas.get(entity).targetScore = score;
        }
        
        private void setAliveBlockadesScore(AURAreaGraph area, double d) {
                
        }
        
        private void setBlockedHumansScore(double score) {
                for(StandardEntity agent : wi.getEntitiesOfType(StandardEntityURN.CIVILIAN, StandardEntityURN.AMBULANCE_TEAM, StandardEntityURN.FIRE_BRIGADE)){
                        AURAreaGraph pos = wsg.getAreaGraph(((Human) agent).getPosition());
                        if(pos.getTravelCost() == AURConstants.Math.INT_INF || pos.getNoBlockadeTravelCost() * 3 < pos.getTravelCost()){
                                pos.secondaryScore += score;
                        }
                }
        }

        private void addGasStationScore(AURAreaGraph area, double score) {
                if(! area.isGasStation()){
                        score = 0;
                }
                area.baseScore += score;
        }

        private void addHydrandScore(AURAreaGraph area, double score) {
                if(! area.isHydrant()){
                        score = 0;
                }
                area.baseScore += score;
        }

        private void setDistanceScore(AURAreaGraph area, double score) {
                area.distanceScore = (AURConstants.Agent.VELOCITY / (double) area.getNoBlockadeTravelCost()) * score;
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

        private void setBroknessScore(AURAreaGraph area, double d) {
                
        }
        

}
