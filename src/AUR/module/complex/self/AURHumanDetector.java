package AUR.module.complex.self;

import AUR.util.ambulance.AmbulanceUtil;
import AUR.util.ambulance.Information.BuldingInfo;
import AUR.util.ambulance.Information.RescueInfo;
import AUR.util.ambulance.Information.CivilianInfo;
import AUR.util.knd.AURWorldGraph;
import adf.agent.communication.MessageManager;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.agent.precompute.PrecomputeData;
import adf.component.module.algorithm.Clustering;
import adf.component.module.complex.HumanDetector;
import rescuecore2.standard.entities.*;
import rescuecore2.worldmodel.EntityID;

/**
 * Created by armanaxh on 2018.
 */

import java.util.*;

import static rescuecore2.standard.entities.StandardEntityURN.*;

/**
 *
 * @author armanaxh - 2018
 */

public class AURHumanDetector extends HumanDetector
{
    private Clustering clustering;
    private EntityID result;
    private AURWorldGraph wsg;
    private RescueInfo rescueInfo;


    public AURHumanDetector(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData)
    {
        super(ai, wi, si, moduleManager, developData);
        this.result = null;

        switch (scenarioInfo.getMode())
        {
            case PRECOMPUTATION_PHASE:
                this.clustering = moduleManager.getModule("SampleHumanDetector.Clustering", "adf.sample.module.algorithm.SampleKMeans");
                break;
            case PRECOMPUTED:
                this.clustering = moduleManager.getModule("SampleHumanDetector.Clustering", "adf.sample.module.algorithm.SampleKMeans");
                break;
            case NON_PRECOMPUTE:
                this.clustering = moduleManager.getModule("SampleHumanDetector.Clustering", "adf.sample.module.algorithm.SampleKMeans");
                break;
        }
        this.wsg = moduleManager.getModule("knd.AuraWorldGraph", "AUR.util.knd.AURWorldGraph");
        this.rescueInfo = moduleManager.getModule("ambulance.RescueInfo", "AUR.util.ambulance.Information.RescueInfo");
        this.wsg.rescueInfo = rescueInfo;

        registerModule(this.clustering);

        init();
    }

    // init *************************************************************************************

    private void init(){

    }


    // Update ***********************************************************************************

    @Override
    public HumanDetector updateInfo(MessageManager messageManager)
    {
        super.updateInfo(messageManager);
        if (this.getCountUpdateInfo() > 1)
        {
            return this;
        }

        this.wsg.updateInfo(messageManager);
        this.clustering.updateInfo(messageManager);
        this.rescueInfo.updateInformation();//2
        return this;
    }



    //DEBUG

    //TODO agent property , in bredness , in fire , in black , ...

    // Calc ***************************************************************************************
    @Override
    public HumanDetector calc()
    {

        Human transportHuman = this.agentInfo.someoneOnBoard();
        if (transportHuman != null) {
            this.result = transportHuman.getID();
            return this;
        }

        if (this.result != null)
        {
            Human target = (Human) this.worldInfo.getEntity(this.result);
            if (target != null)
            {
                if (!target.isHPDefined() || target.getHP() == 0)
                {
                    this.result = null;
                }
                else if (!target.isPositionDefined())
                {
                    this.result = null;
                }
                else
                {
                    StandardEntity position = this.worldInfo.getPosition(target);
                    if (position != null)
                    {
                        StandardEntityURN positionURN = position.getStandardURN();
                        if (positionURN == REFUGE || positionURN == AMBULANCE_TEAM)
                        {
                            this.result = null;
                        }

                        //TODO
                        if (position instanceof Building){
                            Building b = (Building)position;
                            if(b.isOnFire()) {
                                this.result = null;
                            }
                        }

                        if(!position.getID().equals(agentInfo.getPosition())){
                            this.result = null;
                        }
                    }
                }
            }
        }
        if (this.result == null)
        {
            if (clustering == null)
            {
                this.result = this.calcTargetInWorld();
                return this;
            }
            this.result = this.calcTargetInCluster(clustering);
            if (this.result == null)
            {
                this.result = this.calcTargetInWorld();
            }
        }


        this.rescueInfo.ambo.workOnIt = rescueInfo.civiliansInfo.get(result);
        return this;
    }

    private boolean chackAmboWork(){
        return true;
    }


    private EntityID calcTargetInCluster(Clustering clustering)
    {

        List<CivilianInfo> civilians = new LinkedList<>();
        civilians.addAll(rescueInfo.civiliansInfo.values());
        Collections.sort(civilians , AmbulanceUtil.RateSorter);


        // TODO HaHa:D
        this.removeCantRescue(civilians);

        this.removeLowRate(civilians);

        if(civilians.size() > 0 ){

            return civilians.get(0).getID();

        }

        return null;
    }

    private List<CivilianInfo> removeCantRescue(List<CivilianInfo> civilians){

        Collection<CivilianInfo> temp = new LinkedList<>();
        for(CivilianInfo civilian : civilians){
            if(civilian.saveTime <= 0){
                temp.add(civilian);
                //TODO remove Set list
                rescueInfo.canNotRescueCivilian.add(civilian);
            }
        }
        civilians.removeAll(temp);

        return civilians;
    }

    private List<CivilianInfo> removeLowRate(List<CivilianInfo> civilians){

        Collection<CivilianInfo> temp = new LinkedList<>();
        for(CivilianInfo civilian : civilians){
            if(civilian.rate <= 1){
                temp.add(civilian);
            }
        }
        civilians.removeAll(temp);
        return civilians;
    }
    private EntityID calcTargetInWorld()
    {

        return null;
    }

    @Override
    public EntityID getTarget()
    {
        return this.result;
    }

    @Override
    public HumanDetector precompute(PrecomputeData precomputeData)
    {
        super.precompute(precomputeData);
        if (this.getCountPrecompute() >= 2)
        {
            return this;
        }
        return this;
    }

    @Override
    public HumanDetector resume(PrecomputeData precomputeData)
    {
        super.resume(precomputeData);
        if (this.getCountResume() >= 2)
        {
            return this;
        }

        return this;
    }

    @Override
    public HumanDetector preparate()
    {
        super.preparate();
        if (this.getCountPreparate() >= 2)
        {
            return this;
        }
        clustering.preparate();
        int index = clustering.getClusterIndex(agentInfo.me());
        rescueInfo.clusterEntity.addAll(clustering.getClusterEntities(index));
        return this;
    }

}

