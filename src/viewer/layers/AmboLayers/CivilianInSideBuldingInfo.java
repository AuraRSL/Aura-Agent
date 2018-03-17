package viewer.layers.AmboLayers;

import AUR.util.ambulance.Information.CivilianInfo;
import AUR.util.ambulance.Information.RefugeInfo;
import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Civilian;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

import java.awt.*;

/**
 * Created by armanaxh on 12/20/17.
 */

public class CivilianInSideBuldingInfo extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {


    }


    public String getString(AURWorldGraph wsg, AURAreaGraph selected_ag){
        String s = "";
        if(selected_ag != null) {
            Area area = selected_ag.area;
            if (wsg.rescueInfo != null) {
                for (CivilianInfo ci : wsg.rescueInfo.civiliansInfo.values()) {
                    if (ci.getPosition().equals(area.getID())) {
                        s += calc(wsg, ci);

                        s += "\n==============================\n";
                    }
                }
            }
        }
        return s;
    }

    public String calc(AURWorldGraph wsg, CivilianInfo civilian){
        String rate = "";


        double areaType = civilian.rateDeterminer.effectAreaType(0.01);
        double saveTime = civilian.rateDeterminer.effectReverseSaveCycle(2.3);
        double damage = civilian.rateDeterminer.effectDamage(0.5);

        double hp = civilian.rateDeterminer.effectHp(0.5);
        double burid = civilian.rateDeterminer.effectBuridness(0.5);
        double travelTime = civilian.rateDeterminer.effectTravelTime(0.25);
        double travelTimetoRefuge = civilian.rateDeterminer.effectTravelTimeToRefuge(0.25);



        rate += "\nareaType :"+areaType;
        rate += "\nsaveTime :"+saveTime+"  > " + (double)(civilian.saveTime) ;
        rate += "\nburid :"+burid + "  > " + civilian.getBuriedness();
        rate += "\ntravelTime :"+travelTime + " > "+civilian.travelTimeToMe;
        rate += "\ntravelTimetoRefuge :"+travelTimetoRefuge + " > "+civilian.travelTimeToRefuge;
        rate += "\ndamage :"+damage + "  > "+ civilian.getDmg();
        rate += "\nhp :"+hp + "  > "+ civilian.getHp() ;

        double ratea = 0;
        if(civilian.rateDeterminer.ignoreCivilian()){
            rate += " \n Rate :: " + ratea;
            return rate;
        }
        ratea += areaType;
        ratea += saveTime;
        ratea += damage;

        if(ratea > 1 ) {
            ratea += burid;
            ratea += travelTime;
            ratea += travelTimetoRefuge;
            ratea += hp;
        }

//
//        rate += " \n Rate 1:: " + ratea ;
//        if(hp == 0
//                || AmbulanceUtil.inSideRefuge(wsg, civilian.me)
//                ) {
//            ratea = 0;
//        }

        rate += " \n Rate :: " + ratea;

        return rate;
    }

}
