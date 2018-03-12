package AUR.util.aslan;

import AUR.util.knd.AURWorldGraph;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.component.module.AbstractModule;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author Amir Aslan Aslani - Mar 2018
 */
public class AURPoliceGraph extends AbstractModule {
        public final HashMap<EntityID, AURPoliceArea> list = new HashMap<>();
        public final ArrayList<EntityID> areas = new ArrayList<>();
        AgentInfo ai;
        WorldInfo wi;
        ScenarioInfo si;
        AURWorldGraph wsg;
        

        public AURPoliceGraph(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData) {
                super(ai, wi, si, moduleManager, developData);
                this.ai = ai;
                this.si = si;
                this.wi = wi;
                
                this.wsg = moduleManager.getModule("knd.AuraWorldGraph");
                
                fillLists();
                setScores();
        }

        private void fillLists(){
                for(StandardEntity entity : wi.getAllEntities()){
                        if(entity instanceof Area){
                                list.put(entity.getID(), new AURPoliceArea((Area) entity, wsg.getAreaGraph(entity.getID()),wsg));
                        }
                        if(entity instanceof Area){
                                areas.add(entity.getID());
                        }
                }
        }
        
        @Override
        public AbstractModule calc() {
                
                return this;
        }

        private void setScores() {
                addAreasConflictScore(0.02);
                addWSGRoadScores(0.5);
                addRefugeScore(0.5);
                blockadeExistancePossibilityScore(0.3);
        }

        private void addAreasConflictScore(double score) {
                int numberOfPathsToSelect = 200;
                for(int i = 0;i < numberOfPathsToSelect;i ++){
                        addScoreToCollection(
                                wsg.getNoBlockadePathToClosest(
                                        areas.get( (int) (Math.random() * areas.size()) ), 
                                        Lists.newArrayList(
                                                areas.get( (int) (Math.random() * areas.size()) )
                                        )
                                ),
                                score
                        );
                }
        }

        private void addScoreToCollection(ArrayList<EntityID> collection, double score) {
                for(EntityID eid : collection){
                        list.get(eid).baseScore += score;
                }
        }

        private void addWSGRoadScores(double score) {
                for(AURPoliceArea pa : list.values()){
                        pa.baseScore += pa.ag.getScore() * score;
                }
        }

        private void addRefugeScore(double score) {
                
        }

        private void blockadeExistancePossibilityScore(double score) {
                for(AURPoliceArea pa : list.values()){
                        pa.baseScore += pa.getBlockadeExistancePossibilityScore() * score;
                }
        }
}
