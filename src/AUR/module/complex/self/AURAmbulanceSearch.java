package AUR.module.complex.self;

import AUR.util.ambulance.AmbulanceUtil;
import AUR.util.ambulance.Information.BuildingInfo;
import AUR.util.ambulance.Information.CivilianInfo;
import AUR.util.ambulance.Information.RescueInfo;
import AUR.util.knd.AURGeoUtil;
import AUR.util.knd.AURWorldGraph;
import adf.agent.communication.MessageManager;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.agent.precompute.PrecomputeData;
import adf.component.module.algorithm.Clustering;
import adf.component.module.algorithm.PathPlanning;
import adf.component.module.complex.Search;
import rescuecore2.misc.geometry.Line2D;
import rescuecore2.misc.geometry.Point2D;
import rescuecore2.standard.entities.*;
import rescuecore2.standard.view.BuildingLayer;
import rescuecore2.worldmodel.EntityID;

import java.util.*;

import static rescuecore2.standard.entities.StandardEntityURN.*;

public class AURAmbulanceSearch extends Search
{
    private PathPlanning pathPlanning;
    private Clustering clustering;

    private EntityID result;
    private RescueInfo rescueInfo;
    private AURWorldGraph wsg;

    public AURAmbulanceSearch(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData)
    {
        super(ai, wi, si, moduleManager, developData);

        StandardEntityURN agentURN = ai.me().getStandardURN();
        switch (si.getMode())
        {
            case PRECOMPUTATION_PHASE:
                if (agentURN == AMBULANCE_TEAM)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Ambulance", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Ambulance", "adf.sample.module.algorithm.SampleKMeans");
                }
                else if (agentURN == FIRE_BRIGADE)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Fire", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Fire", "adf.sample.module.algorithm.SampleKMeans");
                }
                else if (agentURN == POLICE_FORCE)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Police", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Police", "adf.sample.module.algorithm.SampleKMeans");
                }
                break;
            case PRECOMPUTED:
                if (agentURN == AMBULANCE_TEAM)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Ambulance", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Ambulance", "adf.sample.module.algorithm.SampleKMeans");
                }
                else if (agentURN == FIRE_BRIGADE)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Fire", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Fire", "adf.sample.module.algorithm.SampleKMeans");
                }
                else if (agentURN == POLICE_FORCE)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Police", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Police", "adf.sample.module.algorithm.SampleKMeans");
                }
                break;
            case NON_PRECOMPUTE:
                if (agentURN == AMBULANCE_TEAM)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Ambulance", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Ambulance", "adf.sample.module.algorithm.SampleKMeans");
                }
                else if (agentURN == FIRE_BRIGADE)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Fire", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Fire", "adf.sample.module.algorithm.SampleKMeans");
                }
                else if (agentURN == POLICE_FORCE)
                {
                    this.pathPlanning = moduleManager.getModule("SampleSearch.PathPlanning.Police", "adf.sample.module.algorithm.SamplePathPlanning");
                    this.clustering = moduleManager.getModule("SampleSearch.Clustering.Police", "adf.sample.module.algorithm.SampleKMeans");
                }
                break;
        }
        this.wsg = moduleManager.getModule("knd.AuraWorldGraph", "AUR.util.knd.AURWorldGraph");
        this.rescueInfo = moduleManager.getModule("ambulance.RescueInfo", "AUR.util.ambulance.Information.RescueInfo");

        registerModule(this.pathPlanning);
        registerModule(this.clustering);

    }

    // init *************************************************************************************



    // update  *************************************************************************************
    @Override
    public Search updateInfo(MessageManager messageManager)
    {
        super.updateInfo(messageManager);
        if (this.getCountUpdateInfo() >= 2)
        {
            return this;
        }
        if(agentInfo.getTime() < 2 ){
            return this;
        }

        this.removevisitedBulding();
        this.removeBuringBulding();
        return this;
    }

    private void removeBuringBulding() {

        boolean flag = false;
        for(EntityID id : worldInfo.getChanged().getChangedEntities()){
            StandardEntity entity = worldInfo.getEntity(id);
            if(entity instanceof Building){
                Building b = (Building) entity;
                if(b.isOnFire()){
                    flag = true;
                    break;
                }
            }
        }
        if(flag == true){
            for(EntityID id : worldInfo.getChanged().getChangedEntities()){
                StandardEntity entity = worldInfo.getEntity(id);
                if(entity instanceof Building){
                    this.rescueInfo.buildingsInfo.get(id).rate = 0;
                }
            }
        }
    }

    public void removevisitedBulding(){

        boolean intersect = false;
        for(EntityID id : agentInfo.getChanged().getChangedEntities()) {
            StandardEntity se = worldInfo.getEntity(id);
            if(se instanceof Building
                    && worldInfo.getDistance(agentInfo.getID(), id) < scenarioInfo.getPerceptionLosMaxDistance() ) {
                Building building = (Building)se;
                intersect = false;
                this.rescueInfo.testLine.add(new Line2D(new Point2D(agentInfo.getX(), agentInfo.getY()) , new Point2D(building.getX(), building.getY()) ));
                for (StandardEntity entity : worldInfo.getObjectsInRange(agentInfo.getID(), scenarioInfo.getPerceptionLosMaxDistance())) {

                    if (entity instanceof Area) {
                        Area area = (Area) entity;


                        if (entity instanceof Road) {
                            continue;
                        }
                        for (Edge edge : area.getEdges()) {
                            double[] d = new double[2];
                            if (edge.isPassable()) {
                                continue;
                            }
                            if (AURGeoUtil.getIntersection(
                                    edge.getStartX(), edge.getStartY(),
                                    edge.getEndX(), edge.getEndY(),
                                    agentInfo.getX(), agentInfo.getY(),
                                    building.getX(), building.getY(),
                                    d)) {
                                intersect = true;
                                rescueInfo.areasInter.add(edge);
                                break;
                            }
                        }
                        if (intersect == true) {
                            break;
                        }
                    }

                }
                if( intersect == false) {
                    this.rescueInfo.buildingsInfo.get(building.getID()).rate = 0;
                    this.rescueInfo.visitedList.add(rescueInfo.buildingsInfo.get(building.getID()));
                    this.rescueInfo.searchList.remove(rescueInfo.buildingsInfo.get(building.getID()));
                }
            }
        }
    }



    // calc *************************************************************************************

    @Override
    public Search calc()
    {

        this.result = this.calcTarget();
        this.rescueInfo.ambo.searchTarget = rescueInfo.buildingsInfo.get(result);

        return this;
    }

    private EntityID calcTarget(){

        List<BuildingInfo> builidngs  = new LinkedList<>();
        builidngs.addAll(rescueInfo.buildingsInfo.values());
        Collections.sort(builidngs , AmbulanceUtil.BuilidingRateSorter);

        // TODO HaHa:D

        this.removeLowRate(builidngs);

        if(builidngs.size() > 0 ){

            return builidngs.get(0).me.getID();

        }
        return null;
    }


    private List<BuildingInfo> removeLowRate(List<BuildingInfo> buildings){

        Collection<BuildingInfo> temp = new LinkedList<>();
        for(BuildingInfo building : buildings){
            if(building.rate >= 1){
                temp.add(building);
            }
        }
//        buildings.removeAll(temp);
        this.rescueInfo.searchList.clear();
        this.rescueInfo.searchList.addAll(temp);
        return buildings;
    }


    private void reset()
    {

    }

    @Override
    public EntityID getTarget()
    {
        return this.result;
    }

    @Override
    public Search precompute(PrecomputeData precomputeData)
    {
        super.precompute(precomputeData);
        if (this.getCountPrecompute() >= 2)
        {
            return this;
        }
        return this;
    }

    @Override
    public Search resume(PrecomputeData precomputeData)
    {
        super.resume(precomputeData);
        if (this.getCountResume() >= 2)
        {
            return this;
        }
        this.worldInfo.requestRollback();
        this.rescueInfo.initCalc();
        return this;
    }

    @Override
    public Search preparate()
    {
        super.preparate();
        if (this.getCountPreparate() >= 2)
        {
            return this;
        }

        this.worldInfo.requestRollback();
        this.rescueInfo.initCalc();
        return this;
    }
}