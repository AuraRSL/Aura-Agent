package AUR.util.knd;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURBuilding {
	
	private Polygon perceptibleArea = null;
	public AURAreaGraph ag = null;
	public AURWorldGraph wsg = null;
	public AUREdgeToSee ets = null;
	public boolean commonWall[] = null;
	public AURFireSimBuilding fireSimBuilding = null;
	
	public AURBuilding(AURWorldGraph wsg, AURAreaGraph ag) {
		this.wsg = wsg;
		this.ag = ag;
		this.fireSimBuilding = new AURFireSimBuilding(this);

		commonWall = new boolean[ag.polygon.npoints];
		for(int i = 0; i < ag.polygon.npoints; i++) {
			commonWall[i] = false;
		}
	}

	public void setCommonWalls() {
		
		Polygon bp = ag.polygon;
		Rectangle bounds = bp.getBounds();
		
		bounds = new Rectangle(
				(int) (bounds.getMinX() - 2),
				(int) (bounds.getMinY() - 2),
				(int) (bounds.getWidth() + 2 * 2),
				(int) (bounds.getHeight() + 2 * 2)
		);
		int r = (int) Math.max(bounds.getWidth(), bounds.getHeight()) / 2;
		Collection<StandardEntity> cands = wsg.wi.getObjectsInRectangle(
			(int) bounds.getMinX(),
			(int) bounds.getMinY(),
			(int) bounds.getMaxX(),
			(int) bounds.getMaxY()
		);
		
		cands.remove(ag.area);

		ArrayList<AURAreaGraph> ags = new ArrayList<>();
		
		for(StandardEntity sent : cands) {
			AURAreaGraph ag_ = wsg.getAreaGraph(sent.getID());
			if(ag_ != null && ag_.isBuilding()) {
				ags.add(ag_);
			}
		}
		
		For:
		for(int i = 0; i < bp.npoints; i++) {
			if(commonWall[i] == true) {
				continue;
			}
			for(AURAreaGraph ag_ : ags) {
				Polygon po = ag_.polygon;
				for(int j = 0; j < po.npoints; j++) {
					if(ag_.getBuilding().commonWall[j]) {
						continue;
					}
					if(bp.xpoints[i] == po.xpoints[j] && bp.ypoints[i] == po.ypoints[j]) {
						if(true && bp.xpoints[(i + bp.npoints - 1) % bp.npoints] == po.xpoints[(j + 1) % po.npoints]
							&& bp.ypoints[(i + bp.npoints - 1) % bp.npoints] == po.ypoints[(j + 1) % po.npoints]) {
							commonWall[(i + bp.npoints - 1) % bp.npoints] = true;
							ag_.getBuilding().commonWall[j] = true;
							continue For;
						} else {
							if(true && bp.xpoints[(i + 1) % bp.npoints] == po.xpoints[(j + 1) % po.npoints]
								&& bp.ypoints[(i + 1) % bp.npoints] == po.ypoints[(j + 1) % po.npoints]) {
								commonWall[i] = true;
								ag_.getBuilding().commonWall[j] = true;
								continue For;
							}
						}
					}
				}
			}
		}
	}
	
	public ArrayList<AURAreaGraph> getPerceptibleAreas() {
		
		Polygon perceptibleAreaPolygon = getPerceptibleAreaPolygon();
		Rectangle2D bounds = perceptibleAreaPolygon.getBounds();
		
		Collection<StandardEntity> cands = wsg.wi.getObjectsInRectangle(
			(int) bounds.getMinX(),
			(int) bounds.getMinY(),
			(int) bounds.getMaxX(),
			(int) bounds.getMaxY()
		);
	
		ArrayList<AURAreaGraph> result = new ArrayList<>();
		
		for(StandardEntity sent : cands) {
			if(sent.getStandardURN().equals(StandardEntityURN.ROAD) == false && sent.getStandardURN().equals(StandardEntityURN.HYDRANT) == false) {
				continue;
			}
			if(AURGeoUtil.intersectsOrContains(perceptibleAreaPolygon, (Polygon) ((Area) sent).getShape())) {
				result.add(wsg.getAreaGraph(sent.getID()));
			}
		}
		return result;
	}
	
	public void init() {

	}

	public Polygon getPerceptibleAreaPolygon() {
		if(this.perceptibleArea == null) {
			this.perceptibleArea = AURPerceptibleArea.getPerceptibleArea(this);
		}
		return this.perceptibleArea;
	}
	
}
