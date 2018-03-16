package viewer.layers.aslan;

import AUR.util.aslan.AURDijkstra;
import AUR.util.aslan.AURGeoTools;
import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURConstants;
import AUR.util.knd.AURGeoUtil;
import AUR.util.knd.AURWorldGraph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import rescuecore2.misc.Pair;
import rescuecore2.misc.geometry.Line2D;
import rescuecore2.misc.geometry.Point2D;
import rescuecore2.standard.entities.Area;
import rescuecore2.worldmodel.EntityID;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author Amir Aslan Aslani - Mar 2018
 */
public class GuidPoints extends K_ViewerLayer {
        public static Polygon pol = null;
        public static HashMap<EntityID, ArrayList<Pair<Point2D, EntityID>>> guidPoints = new HashMap<>();
        public static HashMap<EntityID, ArrayList<Point2D>> guidPointsAdded = new HashMap<>();

        @Override
        public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
                if (selected_ag == null) {
                        return;
                }
                g2.setColor(Color.RED);
                g2.setFont(new Font("Arial", 0, 9));
                if(guidPoints.containsKey(wsg.ai.getID())){
                        for(Pair<Point2D, EntityID> pair : guidPoints.get(wsg.ai.getID())){
//                                System.out.println(pair + " Drawen!! ");
                                g2.drawOval(
                                        kst.xToScreen( pair.first().getX() ) - 5 ,
                                        kst.yToScreen( pair.first().getY() ) - 5,
                                        10,
                                        10
                                );
                        }
                }
                else
                {
//                        System.out.println("WTF!!");
                }
                
                g2.setColor(Color.BLUE);
                
//                if(pol != null){
//                        g2.fill(kst.getTransformedPolygon(pol));
//                }
                System.out.println("---------------------------");
                int counter = 1;
                if(guidPointsAdded.containsKey(wsg.ai.getID())){
                        for(Point2D pair : guidPointsAdded.get(wsg.ai.getID())){
//                                System.out.println(pair + " Drawen Kir!! ");
                                System.out.println(pair);
                                g2.drawOval(
                                        kst.xToScreen( pair.getX() ) - 5 ,
                                        kst.yToScreen( pair.getY() ) - 5,
                                        10,
                                        10
                                );
                                g2.setColor(Color.RED);
                                g2.drawString(
                                        String.valueOf(counter), 
                                        kst.xToScreen( pair.getX() ),
                                        kst.yToScreen( pair.getY() )
                                );
                                g2.setColor(Color.BLUE);
                                
                                g2.setColor(new Color(0,255,255,100));
                                g2.fill(
                                        kst.getTransformedPolygon(
                                                getClearPolygon(
                                                        pair,
                                                        new Point2D(
                                                                wsg.ai.getX(),
                                                                wsg.ai.getY()
                                                        )
                                                )
                                        )
                                );
                                counter++;
                        }
                }
                else
                {
//                        System.out.println("WTF2!!");
                }
                
//                EntityID a2 = new EntityID(976);
//                Area a = (Area) wsg.wi.getEntity(a2);
//                
//                ArrayList<Point2D> points = new ArrayList<>();
//                
//                Point2D p1 = AURGeoTools.getEdgeMid(a.getEdgeTo(new EntityID(274)));
//                Point2D p2 = AURGeoTools.getEdgeMid(a.getEdgeTo(new EntityID(921)));
//                points.add(p1);
//                points.add(p2);
//                int counter = 2;
//                for(int i = 2;i < a.getApexList().length;i += 2){
//                        for(int j = 0;j < i;j += 2){
//                                if(
//                                        Math.abs(j - i) > 2 &&
//                                        a.getShape().contains(
//                                                (a.getApexList()[i] + a.getApexList()[j]) / 2,
//                                                (a.getApexList()[i + 1] + a.getApexList()[j + 1]) / 2
//                                        )                                                
//                                ){
//                                        
//                                        g2.drawOval(
//                                                kst.xToScreen( ( (a.getApexList()[i] + a.getApexList()[j]) / 2 ) ) - 5 ,
//                                                kst.yToScreen( ( (a.getApexList()[i + 1] + a.getApexList()[j + 1]) / 2 )) - 5,
//                                                10,
//                                                10
//                                        );
//                                        g2.setFont(new Font("Arial", 0, 9));
//                                        g2.setColor(Color.BLACK);
//                                        g2.drawString(
//                                                String.valueOf(counter), 
//                                                kst.xToScreen( ( (a.getApexList()[i] + a.getApexList()[j]) / 2 ) ),
//                                                kst.yToScreen( ( (a.getApexList()[i + 1] + a.getApexList()[j + 1]) / 2 ))
//                                        );
//                                        counter ++;
//                                        g2.setColor(Color.ORANGE);
//                                        points.add(new Point2D(
//                                                (a.getApexList()[i] + a.getApexList()[j]) / 2,
//                                                (a.getApexList()[i + 1] + a.getApexList()[j + 1]) / 2)
//                                        );
//                                        
//                                        
//                                }
//                        }
//                }
//                
//                g2.setColor(Color.RED);
//                
//                int matrix[][] = new int[points.size()][points.size()];
//                for(int i = 0;i < points.size();i ++){
//                        for(int j = 0;j < points.size();j ++){
//                                if(
//                                        i != j &&
//                                        ! AURGeoTools.intersect(
//                                                AURGeoTools.getClearPolygon(points.get(i), points.get(j), wsg.si.getClearRepairRad()),
//                                                a
//                                        )
//                                ){
//                                        g2.drawLine(
//                                                kst.xToScreen(points.get(i).getX()),
//                                                kst.yToScreen(points.get(i).getY()),
//                                                kst.xToScreen(points.get(j).getX()),
//                                                kst.yToScreen(points.get(j).getY())
//                                        );
//                                        matrix[i][j] = matrix[j][i] = (int) AURGeoUtil.dist(points.get(i).getX(), points.get(i).getY(), points.get(j).getX(), points.get(j).getY());
//                                }
//                                else
//                                        matrix[i][j] = 0;
//                        }
//                }
//                
//                AURDijkstra dijkstra = new AURDijkstra();
//                dijkstra.dijkstra(matrix, 0, points.size());
//                ArrayList<Integer> nodes = dijkstra.getPathTo(1);
                
        }
        
        private Polygon getClearPolygon(Point2D p1, Point2D p2) {
                return AURGeoTools.getClearPolygon(p1, p2, AURConstants.AGENT_RADIUS * 3);
        }
}
