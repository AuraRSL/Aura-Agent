package viewer.layers.AmboLayers;

import AUR.util.ambulance.Information.CivilianInfo;
import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

import java.awt.*;

/**
 * Created by armanaxh on 2018.
 */

public class WorkOnIt extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
        g2.setStroke(new BasicStroke(4));
        g2.setColor(new Color(255, 251, 13, 230));

        int r = 5;
        if(wsg.rescueInfo != null) {
            if (wsg.rescueInfo.ambo != null) {
                CivilianInfo workOnIt = wsg.rescueInfo.ambo.workOnIt;
                if (workOnIt != null) {
                    if (workOnIt.me.isXDefined() && workOnIt.me.isYDefined()) {
                        g2.drawOval(kst.xToScreen(workOnIt.me.getX()) - r, kst.yToScreen(workOnIt.me.getY()) - r, 2 * r + 1, 2 * r + 1);
                        g2.setColor(new Color(255, 251, 13, 163));
                        g2.fillOval(kst.xToScreen(workOnIt.me.getX()) - r, kst.yToScreen(workOnIt.me.getY()) - r, 2 * r, 2 * r);
                    }
                }
            }
        }
        g2.setStroke(new BasicStroke(1));
    }

}