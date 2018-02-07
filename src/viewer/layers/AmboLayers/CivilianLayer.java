package viewer.layers.AmboLayers;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import adf.agent.info.WorldInfo;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.StandardEntity;

import java.awt.*;
import rescuecore2.standard.entities.StandardEntityURN;

/**
 * Created by armanaxh on 12/20/17.
 * modified by Alireza on Feb. 7, 2018
 */

public class CivilianLayer extends K_ViewerLayer {

	@Override
	public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
		WorldInfo wi = wsg.wi;
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.green);
		for (StandardEntity e : wi.getAllEntities()) {
			if (e.getStandardURN().equals(StandardEntityURN.CIVILIAN)) {
				Civilian c = (Civilian) e;
				if (c.isXDefined() == false || c.isYDefined() == false) {
					continue;
				}
				int r = 250; // civilian radius
				g2.fillOval(
						kst.xToScreen(c.getX() - r),
						kst.yToScreen(c.getY() + r),
						(int) (2 * r * kst.zoom),
						(int) (2 * r * kst.zoom)
				);

			}
		}
	}
	
}
