package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURAreaGrid;
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

public class K_WorldGrid extends K_ViewerLayer {

	@Override
	public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
//		g2.setColor(Color.cyan);
//		g2.setStroke(new BasicStroke(1));
//		
//		for(int i = 0; i < wsg.gridRows; i++) {
//			kst.drawTransformedLine(g2, wsg.g, i, i, i);
//		}
//		for(int j = 0; j < wsg.gridCols; j++) {
//
//		}

	}

}
