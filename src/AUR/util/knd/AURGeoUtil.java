package AUR.util.knd;

import java.awt.Polygon;
import java.awt.Rectangle;

import rescuecore2.misc.geometry.Line2D;
import rescuecore2.standard.entities.Edge;

/**
 *
 * @author Alireza Kandeh - 2017 & 2018
 */

public class AURGeoUtil {

	public static final int COLINEAR = 0;
	public static final int CLOCKWISE = 1;
	public static final int COUNTER_CLOCKWISE = -1;

	public final static double INF = 1e50;
	public final static double EPS = 1e-8;
	
	public static void setAirCellPercent(AURFireSimulator fs, int airCell[], int airCellSize, Polygon buildingPolygon) {
		double dw = (double) airCellSize / 10;
		double dh = (double) airCellSize / 10;
		int xy[] = fs.getCell_xy(airCell[0], airCell[1]);
		int count = 0;
		double x = 0;
		double y = 0;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				x = xy[0] + j * dw;
				y = xy[1] + i * dh;
				if(buildingPolygon.contains(x, y)) {
					count++;
				}
			}
		}
		airCell[2] = count;
	}
        
	public static double dist(double Ax, double Ay, double Bx, double By) {
		return Math.hypot(Ax - Bx, Ay - By);
	}

	public static Rectangle getOffsetRect(Rectangle rect, double off) {
		Rectangle result = new Rectangle(
			(int) (rect.getMinX() - off),
			(int) (rect.getMinY() - off),
			(int) (rect.getWidth() + 2 * off),
			(int) (rect.getHeight() + 2 * off)
		);
		return result;
	}

	public static double getArea(Polygon p) {
		double sum = 0;
		for (int i = 0; i < p.npoints; i++) {
			sum += (
				((double) (p.xpoints[i]) * (p.ypoints[(i + 1) % p.npoints])) -
				((double) (p.ypoints[i]) * (p.xpoints[(i + 1) % p.npoints]))
			);
		}
		return Math.abs(sum / 2);
	}

	public static int getOrientation(double Ax1, double Ay1, double Ax2, double Ay2, double Bx1, double By1) {
		double v = (Ay2 - Ay1) * (Bx1 - Ax2) - (Ax2 - Ax1) * (By1 - Ay2);
		if (Math.abs(v) < EPS) {
			return AURGeoUtil.COLINEAR;
		}
		return v > 0 ? AURGeoUtil.CLOCKWISE : AURGeoUtil.COUNTER_CLOCKWISE;
	}

	public static boolean getIntersection(Line2D a, Line2D b, double[] result) {
		return getIntersection(
			a.getOrigin().getX(),
			a.getOrigin().getY(),
			a.getEndPoint().getX(),
			a.getEndPoint().getY(),
			b.getOrigin().getX(),
			b.getOrigin().getY(),
			b.getEndPoint().getX(),
			b.getEndPoint().getY(),
			result
		);
	}

	public static boolean getIntersection(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double[] intersection) {
		double l1nx = y0 - y1;
		double l1ny = x1 - x0;
		double l2dx = x3 - x2;
		double l2dy = y3 - y2;
		double numer = (x0 - x2) * l1nx + (y0 - y2) * l1ny;
		double denom = l1nx * l2dx + l1ny * l2dy;
		if(Math.abs(denom) < EPS) {
			// TODO
			return false;
		}
		double t = numer / denom;
		if(t < 0 - EPS || t > 1 + EPS) {
			return false;
		}
		intersection[0] = t * l2dx + x2;
		intersection[1] = t * l2dy + y2;
		return true;
    }

	public static boolean equals(Edge e1, Edge e2) {
		if (true&& e1.getStartX() == e2.getStartX()
				&& e1.getEndX() == e2.getEndX()
				&& e1.getStartY() == e2.getStartY()
				&& e1.getEndY() == e2.getEndY()) {
			return true;
		}
		if (true&& e1.getStartX() == e2.getEndX()
				&& e1.getEndX() == e2.getStartX()
				&& e1.getStartY() == e2.getEndY()
				&& e1.getEndY() == e2.getStartY()) {
			return true;
		}
		return false;
	}
	
	public static boolean equals(double Ax, double Ay, double Bx, double By) {
		return _equalsFast(Ax, Ay, Bx, By);
	}
	
	private static boolean _equalsFast(double Ax, double Ay, double Bx, double By) {
		return Math.abs(Ax - Bx) < EPS && Math.abs(Ay - By) < EPS;
	}
	
	private static boolean _equals(double Ax, double Ay, double Bx, double By) {
		return dist(Ax, Ay, Bx, By) < EPS;
	}

}