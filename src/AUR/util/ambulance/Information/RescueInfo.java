package AUR.util.ambulance.Information;


import AUR.util.knd.AURWorldGraph;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.component.module.AbstractModule;
import rescuecore2.standard.entities.*;
import rescuecore2.worldmodel.EntityID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by armanaxh on 3/3/18.
 */
public class RescueInfo extends AbstractModule {


    public static final int simulationTime = 420;
    public static final int maxBuriedness = 100;
    public static final int maxDamage = 500;
    public static final int maxHp = 10000;
    public static final int thresholdRestDmg = 60;
    public static final int gasStationExplosionRange = 50000;
    public static int maxTravelTime = 20;
    public static int moveDistance = 40000;
    public final int losDamge;
    public final int losHp;


    public AURWorldGraph wsg;
    public AmbulanceInfo ambo;
    public int agentSpeed;


    public Set<StandardEntity> clusterEntity;
    public HashMap<EntityID, CivilianInfo> civiliansInfo;
    public HashMap<EntityID, RefugeInfo> refugesInfo;
    public Set<CivilianInfo> canNotRescueCivilian;

    public RescueInfo(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData){
        super(ai, wi, si, moduleManager, developData);
        this.wsg = moduleManager.getModule("knd.AuraWorldGraph", "AUR.util.knd.AURWorldGraph");
        this.ambo = new AmbulanceInfo((AmbulanceTeam)ai.me());
        this.refugesInfo = new HashMap<>();
        this.civiliansInfo = new HashMap<>();
        this.clusterEntity = new HashSet<>();
        this.losDamge = wsg.si.getPerceptionLosPrecisionDamage();
        this.losHp = wsg.si.getPerceptionLosPrecisionHp();
        this.canNotRescueCivilian = new HashSet<>();


        init();
    }

    // init *************************************************************************************

    private void init(){
        this.agentSpeed = 30000;
        this.initRefuge();
    }


    private void initRefuge(){

        for(StandardEntity entity: wsg.wi.getEntitiesOfType(StandardEntityURN.REFUGE)){
            if(entity instanceof Refuge){
                Refuge refuge = (Refuge)entity;
                RefugeInfo refugeInfo = new RefugeInfo(refuge, this);
                this.refugesInfo.put(refuge.getID(), refugeInfo);
            }
        }

    }
    public void preprate(){

    }

    // Update ***********************************************************************************
    public void updateInformation(){

        this.updateChanges();
        this.updateCycle();

    }

    private void updateCycle(){
        for(CivilianInfo c : civiliansInfo.values()){
            c.updateCycle();
        }
    }
    private void updateChanges(){

        for(EntityID id: worldInfo.getChanged().getChangedEntities()){
            StandardEntity entity = worldInfo.getEntity(id);
            if(entity.getStandardURN().equals(StandardEntityURN.CIVILIAN)){
                Civilian civilian = (Civilian) entity;
                updateCivilianInfo(civilian);
                //TODO
            }
        }
    }

    private void updateCivilianInfo(Civilian civilian){
        //just when see it

        CivilianInfo civilianInfo = null;
        if(!civiliansInfo.containsKey(civilian.getID())) {
            civilianInfo =  new CivilianInfo(civilian, this);
            civiliansInfo.put(civilian.getID(), civilianInfo);
        }else{
            civilianInfo = civiliansInfo.get(civilian.getID());
        }

        if(civilianInfo != null){
            civilianInfo.updateInformation();
        }

    }


    public void updateAgentSpeed(){

    }

    // Calc ***************************************************************************************
    @Override
    public AbstractModule calc() {
        return null;
    }
}
