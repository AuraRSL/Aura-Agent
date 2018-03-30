package AUR.util.aslan;

import AUR.util.knd.AURConstants;
import AUR.util.knd.AURGeoUtil;
import adf.agent.action.Action;
import adf.agent.action.common.ActionMove;
import adf.agent.action.police.ActionClear;
import adf.agent.info.AgentInfo;
import java.util.ArrayList;
import rescuecore2.standard.entities.Blockade;

/**
 *
 * @author Amir Aslan Aslani - Feb 2018
 */
public class AURClearWatcher {
        private final AgentInfo ai;
        
        public final int MOVE = 1,
                         CLEAR = 2,
                         CLEAR_FROM_WATCHER = 3,
                         NULL = 4;
        
        private ArrayList<Integer> lastBlockadePList = null;
        private ArrayList<Integer> currentBlockadePList = null;
        private ArrayList<Blockade> currentBlockadeList = null;
        
        private double xCurrentPos = 0;
        private double yCurrentPos = 0;
        private double xLastPos = 0;
        private double yLastPos = 0;
        
        public double[] lastMoveVector = new double[2];
        
        public int lastAction;
        public int lastTime;
        public int currentTime;
        
        public AURClearWatcher(AgentInfo ai) {
                this.ai = ai;
                this.lastAction = this.NULL;
                
                this.lastBlockadePList = new ArrayList<>();
                this.currentBlockadePList = new ArrayList<>();
                this.currentBlockadeList = new ArrayList<>();
        }
        
        public ArrayList<Integer> getBlockadeListPropertyList(ArrayList<Blockade> list){
                ArrayList<Integer> result = new ArrayList<>();
                for(Blockade b : list){
                        result.addAll(getBlockadePropertyList(b));
                }
                return result;
        }
        
        private ArrayList<Integer> getBlockadePropertyList(Blockade b){
                ArrayList<Integer> list = new ArrayList<>();
                for(int a : b.getApexes()){
                        list.add(a);
                }
                return list;
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
                this.lastBlockadePList = (ArrayList<Integer>) currentBlockadePList.clone();
                this.currentBlockadePList = getBlockadeListPropertyList(blockades);
                this.currentBlockadeList = blockades;
        }
        
        public Action getAction(Action action){
                Action newAction = getNewAction(action);
                
                if(this.lastAction == CLEAR_FROM_WATCHER)
                        System.out.println(" -> CLEAR_FROM_WATCHER");
                else if(newAction instanceof ActionClear){
                        System.out.println(" -> CLEAR");
                        this.lastAction = this.CLEAR;
                }
                else if(newAction instanceof ActionMove){
                        ActionMove actionMove = (ActionMove)newAction;
                        
                        System.out.println(" -> MOVE");
                        this.lastAction = this.MOVE;
                        if(((ActionMove)newAction).getUsePosition()){
                                this.lastMoveVector[0] = actionMove.getPosX() - ai.getX();
                                this.lastMoveVector[1] = actionMove.getPosY() - ai.getY();
                        }
                        else{
                                // Should Fill
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
                        if(lastBlockadePList.equals(currentBlockadePList)){
                                this.lastAction = CLEAR_FROM_WATCHER;
                                return new ActionClear(getNearestBlockadeToAgentFromList(currentBlockadeList));
                        }
                }
                this.lastAction = this.NULL;
                return result;
        }
        
        private boolean isMoveLessThanAllowedValue(){
                return AURGeoUtil.dist(xLastPos, yLastPos, xCurrentPos, yCurrentPos) < AURConstants.Agent.RADIUS;
        }
        
        private Blockade getNearestBlockadeToAgentFromList(ArrayList<Blockade> list){
                Blockade selected = null;
                double dis = Double.MAX_VALUE;
                for(Blockade b : list){
                        double tdis = Math.hypot(ai.getX() - b.getX(), ai.getY() - b.getY());
                        if(tdis < dis){
                                dis = tdis;
                                selected = b;
                        }
                }
                return selected;
        }
}
