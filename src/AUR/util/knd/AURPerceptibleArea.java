package AUR.util.knd;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURPerceptibleArea {
	
	// the main idea is the same as the SensibleArea of SOS (2014)
	public static Polygon getPerceptibleArea(AURBuilding building) {
		
		double maxViewDistance = building.wsg.si.getPerceptionLosMaxDistance() - AURConstants.AGENT_RADIUS;
		
		Polygon result = new Polygon();
		Polygon bp = building.ag.polygon;
		Rectangle bounds = bp.getBounds();
		
		bounds = new Rectangle(
				(int) (bounds.getMinX() - maxViewDistance),
				(int) (bounds.getMinY() - maxViewDistance),
				(int) (bounds.getWidth() + 2 * maxViewDistance),
				(int) (bounds.getHeight() + 2 * maxViewDistance)
		);
		
		
		Collection<StandardEntity> cands = building.wsg.wi.getObjectsInRectangle(
			(int) bounds.getMinX(),
			(int) bounds.getMinY(),
			(int) bounds.getMaxX(),
			(int) bounds.getMaxY()
		);
		int r_ = (int) Math.max(bounds.getHeight(), bounds.getWidth()) / 2;
		
		cands.remove(building.ag.area);
		
		ArrayList<Polygon> q1 = new ArrayList<>();
		ArrayList<Polygon> q2 = new ArrayList<>();
		ArrayList<Polygon> q3 = new ArrayList<>();
		ArrayList<Polygon> q4 = new ArrayList<>();
		
		
		double cx = building.ag.area.getX();
		double cy = building.ag.area.getY();
		
		Rectangle bounds1 = new Rectangle((int) cx, (int) cy, r_, r_);
		Rectangle bounds2 = new Rectangle((int) cx - r_, (int) cy, r_, r_);
		Rectangle bounds3 = new Rectangle((int) cx - r_, (int) cy - r_, r_, r_);
		Rectangle bounds4 = new Rectangle((int) cx, (int) cy - r_, r_, r_);
		
		for(StandardEntity sent : cands) {
			if(sent.getStandardURN().equals(StandardEntityURN.BUILDING) == false) {
				continue;
			}
			Polygon p = (Polygon) ((Building) sent).getShape();
			Rectangle2D pBounds = p.getBounds();
			if(pBounds.intersects(bounds1)) {
				q1.add(p);
			}
			if(pBounds.intersects(bounds2)) {
				q2.add(p);
			}
			if(pBounds.intersects(bounds3)) {
				q3.add(p);
			}
			if(pBounds.intersects(bounds4)) {
				q4.add(p);
			}
		}
		
		double lastX = 0;
		double lastY = 0;
		double lastInsertedX = 0;
		double lastInsertedY = 0;
		
		int count = 0;

		double rx = 0;
		double ry = 0;

		double r = 0;
		double dr = (2 * Math.PI) / 64;
		
		double smallINF = 1e5;
		
		double p[] = new double[2];
		double ray[] = new double[4];

		while(r < Math.PI * 2) {
			double max_ = 0;
			double d = 0;
			double fx = 0;
			double fy = 0;
			rx = cx + Math.cos(r) * smallINF;
			ry = cy + Math.sin(r) * smallINF;
			boolean commonW = false;
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
						commonW = building.commonWall[i];
					}
				}
			}
			
			if(commonW) {
				rx = fx;
				ry = fy;
			} else {
				rx = fx + Math.cos(r) * maxViewDistance;
				ry = fy + Math.sin(r) * maxViewDistance;
				ArrayList<Polygon> candi = null;
				if(r >= 0 && r <= Math.PI / 2) {
					candi = q1;
				} else if(r >= Math.PI / 2 && r <= Math.PI / 1) {
					candi = q2;
				} else if(r >= Math.PI / 1 && r <= 3 * Math.PI / 2) {
					candi = q3;
				} else if(r >= 3 * Math.PI / 2 && r <= 2 * Math.PI / 1) {
					candi = q4;
				}
				
				for(Polygon po : candi) {
					ray[0] = fx;
					ray[1] = fy;
					ray[2] = rx;
					ray[3] = ry;

					if(AURGeoUtil.hitRayAllEdges(po, ray)) {
						rx = ray[2];
						ry = ray[3];
						if(Math.abs(rx - fx) < 1 && Math.abs(ry - fy) < 1) {
							continue;
						}
					}
				}
			}
			
			if(count > 0) {
				boolean co = isCollinear(lastInsertedX, lastInsertedY, lastX, lastY, rx, ry);

				if(co) {
					lastX = rx;
					lastY = ry;
				} else {
					result.addPoint((int) lastX, (int) lastY);
					count++;
					lastInsertedX = lastX;
					lastInsertedY = lastY;
					lastX = rx;
					lastY = ry;
				}
			} else {
				lastInsertedX = rx;
				lastInsertedY = ry;
				lastX = rx;
				lastY = ry;
				result.addPoint((int) lastInsertedX, (int) lastInsertedY);
				count++;
			}
			r += dr;
		}
		
		return result;
	}
	
	private static boolean isCollinear(double x1, double y1, double x2, double y2, double x3, double y3) {
		double v1x = x2 - x1;
		double v1y = y2 - y1;
		double v2x = x3 - x2;
		double v2y = y3 - y2;
		double l1 = Math.hypot(v1x, v1y);
		double l2 = Math.hypot(v2x, v2y);
		if(Math.abs(l1) < AURGeoUtil.EPS || Math.abs(l2) < AURGeoUtil.EPS) {
			return true;
		}
		v1x /= l1;
		v1y /= l1;
		v2x /= l2;
		v2y /= l2;
		double v3x = v2x - v1x;
		double v3y = v2y - v1y;
		double l3 = Math.hypot(v3x, v3y);
		if(l3 < 0.20) {
			return true;
		}
		return false;
	}
	
}
