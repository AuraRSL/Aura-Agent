package AUR.module.complex.self;

import AUR.util.aslan.AURPoliceScoreGraph;
import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import static rescuecore2.standard.entities.StandardEntityURN.AMBULANCE_TEAM;
import static rescuecore2.standard.entities.StandardEntityURN.REFUGE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adf.agent.communication.MessageManager;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.component.module.algorithm.Clustering;
import adf.component.module.algorithm.PathPlanning;
import adf.component.module.complex.RoadDetector;
import com.google.common.collect.Lists;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.EntityID;

public class AURRoadDetector extends RoadDetector {

        private Set<Area> openedAreas = new HashSet<>();
                
        private Clustering clustering;
        private PathPlanning pathPlanning;

        private EntityID result;
        private final AURWorldGraph wsg;
        private final AURPoliceScoreGraph psg;

        public AURRoadDetector(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData) {
                super(ai, wi, si, moduleManager, developData);
                this.pathPlanning = moduleManager.getModule("ActionTransport.PathPlanning", "AUR.module.algorithm.AuraPathPlanning");
                this.clustering = moduleManager.getModule("SampleRoadDetector.Clustering", "AUR.module.algorithm.AURBuildingClusterer");
                
                this.wsg = moduleManager.getModule("knd.AuraWorldGraph");
                this.psg = moduleManager.getModule("aslan.PoliceScoreGraph","AUR.util.aslan.AURPoliceScoreGraph");
                registerModule(this.psg);
                registerModule(this.clustering);
                
                this.result = null;
        }

        @Override
        public RoadDetector updateInfo(MessageManager messageManager) {
                super.updateInfo(messageManager);
                return this;
        }

        @Override
        public RoadDetector calc() {
                EntityID positionID = this.agentInfo.getPosition();
                StandardEntity currentPosition = worldInfo.getEntity(positionID);
                openedAreas.add((Area) currentPosition);
                if (positionID.equals(result)) {
                        this.result = null;
                }

                if (this.result == null) {
                        EntityID targetID =  psg.pQueue.poll().area.getID();
                        psg.setTargetScore(targetID, 0.1);
                        if (targetID == null) {
                                return this;
                        }
                        List<EntityID> path = this.wsg.getNoBlockadePathToClosest(positionID, Lists.newArrayList(targetID));
                        if (path != null && path.size() > 0) {
                                this.result = path.get(path.size() - 1);
                        }
                }
                return this;
        }
        
        private double getScoreOfPath(Collection<EntityID> path){
                double score = 0;
                ArrayList<AURAreaGraph> areaGraph = wsg.getAreaGraph(path);
                for(AURAreaGraph area : areaGraph)
                        score += area.getFinalScore();
                return score;
        }
        
        private Collection<EntityID> toEntityIds(Collection<? extends StandardEntity> entities) {
                ArrayList<EntityID> eids = new ArrayList<>();
                for (StandardEntity standardEntity : entities) {
                        eids.add(standardEntity.getID());
                }
                return eids;
        }
        
        @Override
        public EntityID getTarget() {
                return this.result;
        }

        private boolean isValidHuman(StandardEntity entity) {
                if (entity == null) {
                        return false;
                }
                if (!(entity instanceof Human)) {
                        return false;
                }

                Human target = (Human) entity;
                if (!target.isHPDefined() || target.getHP() == 0) {
                        return false;
                }
                if (!target.isPositionDefined()) {
                        return false;
                }
                if (!target.isDamageDefined() || target.getDamage() == 0) {
                        return false;
                }
                if (!target.isBuriednessDefined()) {
                        return false;
                }

                StandardEntity position = worldInfo.getPosition(target);
                if (position == null) {
                        return false;
                }

                StandardEntityURN positionURN = position.getStandardURN();
                if (positionURN == REFUGE || positionURN == AMBULANCE_TEAM) {
                        return false;
                }

                return true;
        }
}
