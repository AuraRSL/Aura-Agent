package AUR.util.knd;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURPerceptibleArea {
	
	// the idea is the same as the SensibleArea of SOS (2014)
	public static Polygon getPerceptibleArea(AURBuilding building) {
		
		double maxViewDistance = building.wsg.si.getPerceptionLosMaxDistance();
		
		Polygon result = new Polygon();
		Polygon bp = (Polygon) building.ag.area.getShape();
		Rectangle targetBound = bp.getBounds();
		
		targetBound = new Rectangle(
				(int) (targetBound.getMinX() - maxViewDistance),
				(int) (targetBound.getMinY() - maxViewDistance),
				(int) (targetBound.getWidth() + 2 * maxViewDistance),
				(int) (targetBound.getHeight() + 2 * maxViewDistance)
		);
		
		ArrayList<AURWall> aroundWalls = new ArrayList<>();
		for (AURWall wall : building.wsg.walls) {
			if (wall.owner != building.ag && wall.inBoundOrIntersectWith(targetBound)) {
				aroundWalls.add(wall);
			}
		}
		
		double cx = building.ag.area.getX();
		double cy = building.ag.area.getY();

		double rx = 0;
		double ry = 0;

		double r = 0;
		double dr = (2 * Math.PI) / 72;
		
		double smallINF = 1e5;
		
		double p[] = new double[2];
		
		while(r < Math.PI * 2) {
			double max_ = 0;
			double d = 0;
			double fx = 0;
			double fy = 0;
			rx = cx + Math.cos(r) * smallINF;
			ry = cy + Math.sin(r) * smallINF;
			
			for(int i = 0; i < bp.npoints; i++) {
				boolean b = AURGeoUtil.getIntersection(
						cx,
						cy,
						rx,
						ry,
						bp.xpoints[i],
						bp.ypoints[i],
						bp.xpoints[(i + 1) % bp.npoints],
						bp.ypoints[(i + 1) % bp.npoints],
						p
				);
				if(b) {
					d = AURGeoUtil.dist(cx, cy, p[0], p[1]);
					if(d >= max_) {
						max_ = d;
						fx = p[0];
						fy = p[1];
					}
				}
			}
			
			rx = fx + Math.cos(r) * maxViewDistance;
			ry = fy + Math.sin(r) * maxViewDistance;

			for(AURWall w : aroundWalls) {
				boolean b = AURGeoUtil.getIntersection(
						fx,
						fy,
						rx,
						ry,
						w.x0,
						w.y0,
						w.x1,
						w.y1,
						p
				);
				if(b) {
					rx = p[0];
					ry = p[1];
				}
			}
			
			result.addPoint((int) rx, (int) ry);
			r += dr;
		}
		
		return result;
	}
	
}
