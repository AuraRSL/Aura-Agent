package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURBorder;
import AUR.util.knd.AUREdge;
import AUR.util.knd.AUREdgeToSee;
import AUR.util.knd.AURNode;
import AUR.util.knd.AURWorldGraph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class K_ReachableEdgeToSeesFromCenter extends K_ViewerLayer {

	@Override
	public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
		if (selected_ag == null) {
			return;
		}

		g2.setColor(new Color(255, 0, 0, 100));
		g2.setStroke(new BasicStroke(2));
		ArrayList<AUREdgeToSee> edges = selected_ag.getReachabeEdgeToSees(selected_ag.getX(), selected_ag.getY());

		
		for(AUREdgeToSee edge : edges) {
			kst.drawTransformedLine(g2, edge.fromNode.x, edge.fromNode.y, edge.standX, edge.standY);
		}
		

	}

}
