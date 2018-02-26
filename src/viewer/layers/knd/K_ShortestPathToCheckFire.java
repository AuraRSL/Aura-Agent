package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURNode;
import AUR.util.knd.AURWorldGraph;
import adf.agent.action.common.ActionMove;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class K_ShortestPathToCheckFire extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
		
        if(selected_ag == null) {
            return;
        }
		
		ActionMove ma = wsg.getMoveActionToSee___New(wsg.ai.getPosition(), selected_ag.area.getID());
		

		if(ma == null) {
			return;
		}

		
        int lastX = 0;
        int lastY = 0;
		
		
        int X, Y;
        g2.setColor(Color.PINK);
        g2.setStroke(new BasicStroke(3));
        
        
        
        AURNode node = wsg.getAreaGraph(ma.getPath().get(ma.getPath().size() - 1)).lastDijkstraEntranceNode;
		
        if(node == null) {
            return;
        }
		
        lastX = kst.xToScreen(node.x);
        lastY = kst.yToScreen(node.y);
		
		if(ma.getUsePosition()) {
			if(ma.getPath().size() == 1) {
				g2.drawLine(
					kst.xToScreen(wsg.ai.getX()), kst.yToScreen(wsg.ai.getY()),
					kst.xToScreen(ma.getPosX()), kst.yToScreen(ma.getPosY())
				);
			} else {
				g2.drawLine(
					lastX, lastY,
					kst.xToScreen(ma.getPosX()), kst.yToScreen(ma.getPosY())
				);
			}
			

		}
		

		
        while (node.pre != wsg.startNullNode) {

            node = node.pre;
            
            if(node == null) {
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

}
