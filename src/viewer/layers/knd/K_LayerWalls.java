package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWall;
import AUR.util.knd.AURWorldGraph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author Alireza Kandeh - 2017
 */

public class K_LayerWalls extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(2));
        for(AURWall wall : wsg.walls) {
            g2.drawLine(
                kst.xToScreen(wall.x0), kst.yToScreen(wall.y0),
                kst.xToScreen(wall.x1), kst.yToScreen(wall.y1)
            );
        }
        g2.setStroke(new BasicStroke(1));
    }
    
}