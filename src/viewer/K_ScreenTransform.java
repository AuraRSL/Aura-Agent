package viewer;

import kndStuff.fromMisc.ScreenTransform;
import java.awt.Polygon;
import java.awt.Shape;

/**
 *
 * @author Alireza Kandeh - 2017
 */

public class K_ScreenTransform extends ScreenTransform {

    public K_ScreenTransform(double minX, double minY, double maxX, double maxY) {
        super(minX, minY, maxX, maxY);
    }
    
    public Polygon getTransformedPolygon(Shape shape) {
        Polygon polygon = (Polygon) shape;
        Polygon result = new Polygon();
        for(int i = 0; i < polygon.npoints; i++) {
            result.addPoint(this.xToScreen(polygon.xpoints[i]), this.yToScreen(polygon.ypoints[i]));
        }
        return result;
    }
    
}
