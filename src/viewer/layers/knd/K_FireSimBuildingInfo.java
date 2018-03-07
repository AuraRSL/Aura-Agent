package viewer.layers.knd;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURFireSimBuilding;
import AUR.util.knd.AURWorldGraph;
import java.awt.Graphics2D;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class K_FireSimBuildingInfo extends K_ViewerLayer {

	@Override
	public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
	}

	@Override
	public String getString(AURWorldGraph wsg, AURAreaGraph selected_ag) {
		if(selected_ag == null) {
			return null;
		}
		String result = null;
		if(selected_ag.isBuilding()) {
			result = "";
			AURFireSimBuilding b = selected_ag.getBuilding().fireSimBuilding;
			result += "Estimated Temperature:\t" + b.getEstimatedTemperature();
			result += "\n";
			result += "Estimated Energy:\t" + b.getEstimatedEnergy();
			result += "\n";
			result += "Estimated Fieryness:\t" + b.getEstimatedFieryness();
			result += "\n";
		}
		return result;
	}
	
}