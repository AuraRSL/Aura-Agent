package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURAreaGrid;
import AUR.util.knd.AURBorder;
import AUR.util.knd.AURWorldGraph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import rescuecore2.standard.entities.Blockade;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author Alireza Kandeh - 2017 & 2018
 */

public class K_AreaPassableSegments extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
		if(selected_ag == null) {
			return;
		}
		
        g2.setColor(Color.CYAN);
        g2.setStroke(new BasicStroke(2));
        
        wsg.dijkstra(wsg.ai.getPosition());
        
        
		for(AURBorder border : selected_ag.borders) {
			g2.drawLine(
				kst.xToScreen(border.Ax), kst.yToScreen(border.Ay),
				kst.xToScreen(border.Bx), kst.yToScreen(border.By)
			);
		}
		
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(3));
		
		for(AURAreaGraph nag : selected_ag.neighbours) {
			for(Blockade b : wsg.wi.getBlockades(nag.area)) {
				Polygon polygon = kst.getTransformedPolygon(b.getShape());
				g2.drawPolygon(polygon);
				g2.fillPolygon(polygon);
			}	
		}
		
		for(Blockade b : wsg.wi.getBlockades(selected_ag.area)) {
			Polygon polygon = kst.getTransformedPolygon(b.getShape());
			g2.drawPolygon(polygon);
			g2.fillPolygon(polygon);
		}
		
    }

}