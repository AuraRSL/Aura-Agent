package AUR.util.aslan;

import AUR.util.knd.AURConstants;
import AUR.util.knd.AURGeoUtil;
import adf.agent.action.Action;
import adf.agent.action.common.ActionMove;
import adf.agent.action.police.ActionClear;
import adf.agent.info.AgentInfo;
import java.util.ArrayList;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author Amir Aslan Aslani - Feb 2018
 */
public class AURClearWatcher {
        private AgentInfo ai;
        
        public final int MOVE = 1,
                         CLEAR = 2,
                         CLEAR_FROM_WATCHER = 3,
                         NULL = 4;
        
        private ArrayList<Blockade> lastBlockadeList = null;
        private ArrayList<Blockade> currentBlockadeList = null;
        
        private double xCurrentPos = 0;
        private double yCurrentPos = 0;
        private double xLastPos = 0;
        private double yLastPos = 0;
        
        public double[] lastMoveVector = new double[2];
        
        public int lastAction;
        public int lastTime;
        public int currentTime;
        
        private EntityID blockadeTemp = null;
        
        public AURClearWatcher(AgentInfo ai) {
                this.ai = ai;
                this.lastAction = this.NULL;
                
                this.lastBlockadeList = new ArrayList<>();
                this.currentBlockadeList = new ArrayList<>();
        }
        
        public void updateAgentInformations() {
                this.xLastPos = this.xCurrentPos;
                this.yLastPos = this.yCurrentPos;
                this.xCurrentPos = this.ai.getX();
                this.yCurrentPos = this.ai.getY();
                this.lastTime = this.currentTime;
                this.currentTime = this.ai.getTime();
        }
        
        public void setBlockadeList(ArrayList<Blockade> blockades){
                this.lastBlockadeList = this.currentBlockadeList;
                this.currentBlockadeList = blockades;
        }
        
        private boolean isBlockadeListsEqual(ArrayList<Blockade> b1, ArrayList<Blockade> b2){
                if(b1 == null || b2 == null)
                        return false;
                if(b1.size() == b2.size()){
                        for(int i = 0;i < b1.size();i ++){
                                if(isTwoBlockadeEquals(b1.get(i),b2.get(i)))
                                        return false;
                        }
                        return true;
                }
                return false;
        }
        
        private boolean isTwoBlockadeEquals(Blockade b1, Blockade b2){
                if(! b1.getID().equals(b2.getID()))
                        return false;
                if(b1.getApexes().length != b2.getApexes().length)
                        return false;
                for(int i = 0;i < b1.getApexes().length;i ++){
                        if(b1.getApexes()[i] != b1.getApexes()[i])
                                return false;
                }
                return true;
        }
        
        public Action getAction(Action action){
                Action newAction = getNewAction(action);
                
                if(this.lastAction == CLEAR_FROM_WATCHER)
                        System.out.println(" -> CLEAR_FROM_WATCHER");
                else if(action instanceof ActionClear){
                        System.out.println(" -> CLEAR");
                        this.lastAction = this.CLEAR;
                }
                else if(action instanceof ActionMove){
                        System.out.println(" -> MOVE");
                        this.lastAction = this.MOVE;
                        if(((ActionMove)action).getUsePosition()){
                                this.lastMoveVector[0] = ((ActionMove)action).getPosX() - ai.getX();
                                this.lastMoveVector[1] = ((ActionMove)action).getPosY() - ai.getY();
                        }
                }
                
                return newAction;
        }
        
        private Action getNewAction(Action action){
                Action result = action;
                if(isMoveLessThanAllowedValue() &&
                   this.lastAction != CLEAR_FROM_WATCHER &&
                   currentBlockadeList != null &&
                   currentBlockadeList.size() > 0 &&
                   this.lastAction != this.NULL 
                ){
                        System.out.println("Checking blockades...");
                        if(isBlockadeListsEqual(lastBlockadeList,currentBlockadeList)){
                                System.out.println("blockades ok...");
                                this.lastAction = CLEAR_FROM_WATCHER;
                                return new ActionClear(currentBlockadeList.get(0));
                        }
                }
                this.lastAction = this.NULL;
                return result;
        }
        
        private boolean isMoveLessThanAllowedValue(){
                return AURGeoUtil.dist(xLastPos, yLastPos, xCurrentPos, yCurrentPos) < AURConstants.Agent.RADIUS;
        }
}
