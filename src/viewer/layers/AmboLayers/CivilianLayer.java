package viewer.layers.AmboLayers;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import adf.agent.info.WorldInfo;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.StandardEntity;

import java.awt.*;

/**
 * Created by armanaxh on 12/20/17.
 */

public class CivilianLayer extends K_ViewerLayer{

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
        WorldInfo wi = wsg.wi;
        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.green);
        for (StandardEntity e : wi.getAllEntities()) {
            if (e instanceof Civilian) {
                Civilian c = (Civilian) e;
                int r = 250;//agent size!
                g2.fillOval(
                        kst.xToScreen(c.getX() - r), kst.yToScreen(c.getY() + r),
                        (int) (2 * r * kst.zoom), (int) (2 * r * kst.zoom)
//                         (2 * r ), (2 * r )
                 );

            }
        }
        g2.setStroke(new BasicStroke(1));

        if (selected_ag != null) {
            g2.setColor(Color.magenta);
            g2.setStroke(new BasicStroke(2));
            Area area = selected_ag.area;
            for (Edge e : area.getEdges()) {
                g2.drawLine(
                        kst.xToScreen(e.getStartX()), kst.yToScreen(e.getStartY()),
                        kst.xToScreen(e.getEndX()), kst.yToScreen(e.getEndY())
                );
            }
            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.black);
        }
    }
}
