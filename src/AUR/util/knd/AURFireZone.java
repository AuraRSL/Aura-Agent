package AUR.util.knd;

import AUR.util.ConcaveHull;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import viewer.K_ScreenTransform;

/**
 *
 * @author Alireza Kandeh - 2018
 */

public class AURFireZone {
	
	public ArrayList<AURBuilding> buildings = null;

	public AURFireZone() {
		this.buildings = new ArrayList<>();
	}
	
	public boolean contains(AURBuilding b) {
		return this.buildings.contains(b);
	}
	
	public void add(AURBuilding b) {
		if(contains(b) == false) {
			this.buildings.add(b);
		}
	}
	
	public Polygon getConvexHullPolygon() {
//		ArrayList<int[]> points = new ArrayList<>();
		ArrayList<ConcaveHull.Point> points = new ArrayList<>();
		
		for(AURBuilding b : buildings) {
//			for(int i = 0; i < b.ag.polygon.npoints; i++) {
//				points.add(new int[] {b.ag.polygon.xpoints[i], b.ag.polygon.ypoints[i]});
//			} 
			points.add(new ConcaveHull.Point((double) b.ag.getX(), (double) b.ag.getY()));
		}
		ConcaveHull ch = new ConcaveHull();
		ArrayList<ConcaveHull.Point> rps = ch.calculateConcaveHull(points, 10);
		Polygon result = new Polygon();
		for(ConcaveHull.Point p : rps) {
			result.addPoint((int) ((double) p.getX()), (int) ((double) p.getY()));
		}
		return result;
		//return AURConvexHull.calc(points);
	}
	
	public void paint(Graphics2D g2, K_ScreenTransform kst) {
		g2.setColor(new Color(200, 0, 0, 70));
		for(AURBuilding b : this.buildings) {
			g2.fill(kst.getTransformedPolygon(b.ag.polygon));
		}
		g2.setStroke(new BasicStroke(3));
		g2.setColor(new Color(150, 0, 0, 255));
		g2.draw(kst.getTransformedPolygon(getConvexHullPolygon()));
	}
	
}
