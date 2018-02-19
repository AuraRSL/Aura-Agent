package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class K_PerceptibleArea extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
		if(selected_ag == null || selected_ag.isBuilding() == false) {
			return;
		}
		g2.setColor(new Color(0, 200, 0, 100));
		g2.fill(kst.getTransformedPolygon(selected_ag.getBuilding().getPerceptibleArea()));
		g2.setColor(new Color(0, 200, 0, 200));
		g2.setStroke(new BasicStroke(2));
		g2.draw(kst.getTransformedPolygon(selected_ag.getBuilding().getPerceptibleArea()));
    }

}
