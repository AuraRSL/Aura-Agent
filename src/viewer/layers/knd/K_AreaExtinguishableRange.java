package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author Alireza Kandeh - 2017
 */

public class K_AreaExtinguishableRange extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
        if(selected_ag == null) {
            return;
        }
        double r = selected_ag.wsg.si.getFireExtinguishMaxDistance();
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(
            kst.xToScreen(selected_ag.cx) - (int) (1 * r * kst.zoom),
            kst.yToScreen(selected_ag.cy) - (int) (1 * r * kst.zoom),
            (int) (2 * r * kst.zoom),
            (int) (2 * r * kst.zoom)
        );
        g2.setStroke(new BasicStroke(1));
    }

}
