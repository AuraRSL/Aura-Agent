package AUR.util.aslan;

import AUR.util.knd.AURAreaGraph;
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
import java.util.PriorityQueue;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author Amir Aslan Aslani - Mar 2018
 */
public class AURPoliceScoreGraph extends AbstractModule {
        public HashMap<EntityID, AURAreaGraph> areas = new HashMap<>();
        public ArrayList<EntityID> list;
        private Clustering clustering;
        public AgentInfo ai;
        public WorldInfo wi;
        public ScenarioInfo si;
        public AURWorldGraph wsg;
        
        public PriorityQueue<AURPoliceArea> pQueue = new PriorityQueue<>(new AURPoliceAreaScoreComparator());
        
        public Collection<EntityID> clusterEntityIDs;
        int cluseterIndex;
        
        public AURPoliceScoreGraph(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData) {
                super(ai, wi, si, moduleManager, developData);
                this.ai = ai;
                this.si = si;
                this.wi = wi;
                
                this.wsg = moduleManager.getModule("knd.AuraWorldGraph");
                
                this.clustering = moduleManager.getModule("SampleRoadDetector.Clustering", "AUR.module.algorithm.AURKMeans");
                this.cluseterIndex = this.clustering.getClusterIndex(ai.getID());
                this.clusterEntityIDs = this.clustering.getClusterEntityIDs(cluseterIndex);
                
                setScores();
                fillLists();
        }

        private void fillLists(){
                this.areas = wsg.areas;
                
                Collection<EntityID> entitiesOfType = wi.getEntityIDsOfType(StandardEntityURN.ROAD, StandardEntityURN.BUILDING, StandardEntityURN.HYDRANT, StandardEntityURN.GAS_STATION, StandardEntityURN.REFUGE);
                this.list = new ArrayList<>(entitiesOfType.size());
                for(EntityID entity : entitiesOfType){
                        if(worldInfo.getEntity(entity) instanceof Area){
                                list.add(entity);
                        }
                }
                
        }
        
        @Override
        public AbstractModule calc() {
                return this;
        }

        @Override
        public AbstractModule updateInfo(MessageManager messageManager) {
                // Set dynamic scores
                HashSet<AURPoliceArea> changedAreas = new HashSet<>();
                changedAreas.addAll(decreasePoliceAreasScore(0.8));
                
                for(EntityID changed : wi.getChanged().getChangedEntities()){
                        if(wi.getEntity(changed) instanceof  Area){
                                AURAreaGraph areaGraph = wsg.getAreaGraph(changed);

                                changedAreas.addAll(setAliveBlockadesScore(areaGraph, 0.0));
                                changedAreas.addAll(blockedHumansScore(areaGraph, 1.2));
                        }
                }
                pQueue.removeAll(changedAreas);
                pQueue.addAll(changedAreas);
                return this;
        }

        private void setScores() {
                addAreasConflictScore(0.02);
                
                for(AURAreaGraph area : wsg.areas.values()){
                        addRefugeScore(area, 0.15);
                        addGasStationScore(area, 0.075);
                        addHydrandScore(area, 0.05);
                        addWSGRoadScores(area, 0.075);
                        addClusterScore(area, 0.4);
                        
                        blockadeExistancePossibilityScore(area, 0.05);
                        humansBlockedPossibilityScore(area, 0.05);
                        
                        pQueue.add(area.pa);
                }
                
        }
        
        private void addAreasConflictScore(double score) {
                
        }

        private void addScoreToCollection(ArrayList<EntityID> collection, double score) {
                for(EntityID eid : collection){
                        areas.get(eid).pa.baseScore += score;
                }
        }

        private void addWSGRoadScores(AURAreaGraph area, double score) {
                area.pa.baseScore += area.getScore() * score;
        }

        private void addRefugeScore(AURAreaGraph area, double score) {
                if(! area.isRefuge()){
                        score = 0;
                }
                area.pa.baseScore += score;
        }

        private void blockadeExistancePossibilityScore(AURAreaGraph area, double score) {
                
        }

        private void humansBlockedPossibilityScore(AURAreaGraph area, double d) {
                
        }

        private void addClusterScore(AURAreaGraph area, double score) {
                if(! clusterEntityIDs.contains(area.area.getID())){
                        double distanceFromCluster = 0; // TODO Fill
                        score *= (1 - distanceFromCluster);
                }
                area.pa.baseScore += score;
        }
        
        HashSet<EntityID> visitedAreas = new HashSet<>();
        private Collection<AURPoliceArea> decreasePoliceAreasScore(double score) {
                if(! visitedAreas.contains(ai.getPosition())){
                        this.areas.get(ai.getPosition()).pa.secondaryScore += 0.2 * score;
                        visitedAreas.add(ai.getPosition());
                        return Lists.newArrayList(this.areas.get(ai.getPosition()).pa);
                }
                return Lists.newArrayList();
        }

        private Collection<AURPoliceArea> setAliveBlockadesScore(AURAreaGraph area, double d) {
                return Lists.newArrayList();
        }
        
        private Collection<AURPoliceArea> blockedHumansScore(AURAreaGraph area, double d) {
                return Lists.newArrayList();
        }

        private void addGasStationScore(AURAreaGraph area, double score) {
                if(! area.isGasStation()){
                        score = 0;
                }
                area.pa.baseScore += score;
        }

        private void addHydrandScore(AURAreaGraph area, double score) {
                if(! area.isHydrant()){
                        score = 0;
                }
                area.pa.baseScore += score;
        }

}
