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
	
	private static double __temp__[] = new double[2];
	
	public static boolean intersectsOrContains(Polygon p1, Polygon p2) {
		
		if(p1.getBounds2D().intersects(p2.getBounds()) == false) {
			return false;
		}
		
		for(int i = 0; i < p1.npoints; i++) {
			for(int j = 0; j < p2.npoints; j++) {
				boolean b = AURGeoUtil.getIntersection(
						p1.xpoints[i],
						p1.ypoints[i],
						p1.xpoints[(i + 1) % p1.npoints],
						p1.ypoints[(i + 1) % p1.npoints],
						p2.xpoints[j],
						p2.ypoints[j],
						p2.xpoints[(j + 1) % p2.npoints],
						p2.ypoints[(j + 1) % p2.npoints],
						__temp__
				);
				if(b) {
					return true;
				}
			}
		}
		
		for(int i = 0; i < p1.npoints; i++) {
			if(p2.contains(p1.xpoints[i], p1.ypoints[i])) {
				return true;
			}
		}
		for(int j = 0; j < p2.npoints; j++) {
			if(p1.contains(p2.xpoints[j], p2.ypoints[j])) {
				return true;
			}
		}
		
		return false;
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
	
	public static double getPerimeter(Polygon p) {
		double sum = 0;
		for (int i = 0; i < p.npoints; i++) {
			sum += AURGeoUtil.dist(p.xpoints[i], p.ypoints[i], p.xpoints[(i + 1) % p.npoints], p.ypoints[(i + 1) % p.npoints]);
		}
		return sum;
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

	private static boolean equals(double a, double b, double limit) {
		return Math.abs(a - b) < limit;
	}

	private static boolean equals(double a, double b) {
		return equals(a, b, 1.0e-5);
	}

	private static double min(double a, double b, double c, double d) {
		return Math.min(Math.min(a, b), Math.min(c, d));
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

	private static double max(double a, double b, double c, double d) {
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	public static boolean getIntersection(double x0, double y0, double x1, double y1, double x2, double y2,
			double x3, double y3, double[] intersection) {
		// TODO: Make limit depend on input domain
		final double LIMIT = 1e-5;
		final double INFINITY = 1e10;

		double x, y;

		//
		// Convert the lines to the form y = ax + b
		//

		// Slope of the two lines
		double a0 = equals(x0, x1, LIMIT) ? INFINITY : (y0 - y1) / (x0 - x1);
		double a1 = equals(x2, x3, LIMIT) ? INFINITY : (y2 - y3) / (x2 - x3);

		double b0 = y0 - a0 * x0;
		double b1 = y2 - a1 * x2;

		// Check if lines are parallel
		if (equals(a0, a1)) {
			if (!equals(b0, b1))
				return false; // Parallell non-overlapping

			else {
				if (equals(x0, x1)) {
					if (Math.min(y0, y1) < Math.max(y2, y3) || Math.max(y0, y1) > Math.min(y2, y3)) {
						double twoMiddle = y0 + y1 + y2 + y3 - min(y0, y1, y2, y3) - max(y0, y1, y2, y3);
						y = (twoMiddle) / 2.0;
						x = (y - b0) / a0;
					} else
						return false; // Parallell non-overlapping
				} else {
					if (Math.min(x0, x1) < Math.max(x2, x3) || Math.max(x0, x1) > Math.min(x2, x3)) {
						double twoMiddle = x0 + x1 + x2 + x3 - min(x0, x1, x2, x3) - max(x0, x1, x2, x3);
						x = (twoMiddle) / 2.0;
						y = a0 * x + b0;
					} else
						return false;
				}

				intersection[0] = x;
				intersection[1] = y;
				return false;
			}
		}

		// Find correct intersection point
		if (equals(a0, INFINITY)) {
			x = x0;
			y = a1 * x + b1;
		} else if (equals(a1, INFINITY)) {
			x = x2;
			y = a0 * x + b0;
		} else {
			x = -(b0 - b1) / (a0 - a1);
			y = a0 * x + b0;
		}

		intersection[0] = x;
		intersection[1] = y;

		// Then check if intersection is within line segments
		double distanceFrom1;
		if (equals(x0, x1)) {
			if (y0 < y1)
				distanceFrom1 = y < y0 ? length(x, y, x0, y0) : y > y1 ? length(x, y, x1, y1) : 0.0;
			else
				distanceFrom1 = y < y1 ? length(x, y, x1, y1) : y > y0 ? length(x, y, x0, y0) : 0.0;
		} else {
			if (x0 < x1)
				distanceFrom1 = x < x0 ? length(x, y, x0, y0) : x > x1 ? length(x, y, x1, y1) : 0.0;
			else
				distanceFrom1 = x < x1 ? length(x, y, x1, y1) : x > x0 ? length(x, y, x0, y0) : 0.0;
		}

		double distanceFrom2;
		if (equals(x2, x3)) {
			if (y2 < y3)
				distanceFrom2 = y < y2 ? length(x, y, x2, y2) : y > y3 ? length(x, y, x3, y3) : 0.0;
			else
				distanceFrom2 = y < y3 ? length(x, y, x3, y3) : y > y2 ? length(x, y, x2, y2) : 0.0;
		} else {
			if (x2 < x3)
				distanceFrom2 = x < x2 ? length(x, y, x2, y2) : x > x3 ? length(x, y, x3, y3) : 0.0;
			else
				distanceFrom2 = x < x3 ? length(x, y, x3, y3) : x > x2 ? length(x, y, x2, y2) : 0.0;
		}

		return equals(distanceFrom1, 0.0) && equals(distanceFrom2, 0.0) ? true : false;
	}

	public static double length(double x0, double y0, double x1, double y1) {
		double dx = x1 - x0;
		double dy = y1 - y0;

		return Math.sqrt(dx * dx + dy * dy);
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