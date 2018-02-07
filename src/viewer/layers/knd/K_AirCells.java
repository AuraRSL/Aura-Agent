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

public class K_AirCells extends K_ViewerLayer {

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
        g2.setColor(new Color(255, 255, 0, 50));
        g2.setStroke(new BasicStroke(1));
        wsg.fireSimulator.paintJustCells(g2, kst);
    }

    @Override
    public String getString(AURWorldGraph wsg, AURAreaGraph selected_ag) {
        String result = "";
        result += "cellSize: " + wsg.fireSimulator.getCellSize() + "x" + wsg.fireSimulator.getCellSize();
        result += "\n";
        result += "rows: " + wsg.fireSimulator.getCells().length;
        result += "\n";
        result += "cols: " + wsg.fireSimulator.getCells()[0].length;
        return result;
    }

}