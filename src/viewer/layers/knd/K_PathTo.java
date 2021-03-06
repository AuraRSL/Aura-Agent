package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURNode;
import AUR.util.knd.AURWorldGraph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;
import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author Alireza Kandeh - 2017 && 2018
 */

public class K_PathTo extends K_ViewerLayer {

	@Override
	public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
		if (selected_ag == null) {
			return;
		}
		int lastX = 0;
		int lastY = 0;
		int X, Y;
//		wsg.dijkstra(selected_ag.area.getID());
		wsg.KStar(wsg.ai.getPosition());
		g2.setColor(Color.orange);
		g2.setStroke(new BasicStroke(3));

		AURNode node = selected_ag.lastDijkstraEntranceNode;
		if (node == null) {
			return;
		}
		lastX = kst.xToScreen(node.x);
		lastY = kst.yToScreen(node.y);
		while (node.pre != wsg.startNullNode) {

			node = node.pre;

			if (node == null) {
				return;
			}

			X = kst.xToScreen(node.x);
			Y = kst.yToScreen(node.y);

			g2.drawLine(
				lastX, lastY,
				X, Y
			);

			lastX = X;
			lastY = Y;
		}

		g2.drawLine(
			lastX, lastY,
			kst.xToScreen(wsg.ai.getX()), kst.yToScreen(wsg.ai.getY())
		);

		g2.setStroke(new BasicStroke(1));
	}
	
	@Override
	public String getString(AURWorldGraph wsg, AURAreaGraph selected_ag) {
		if(selected_ag == null) {
		    return null;
		}
		String result = "";
		Collection<EntityID> targets = new ArrayList<>();
		targets.add(selected_ag.area.getID());
		ArrayList<EntityID> path = wsg.getPathToClosest(wsg.ai.getPosition(), targets);
		
		if(path != null) {
			for(int i = 0; i < path.size(); i++) {
				result += path.get(i).getValue();
				result += "\n";
			}
		}
		return result;
	}
	
}
