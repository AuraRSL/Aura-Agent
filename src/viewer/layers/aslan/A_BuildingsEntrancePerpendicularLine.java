/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.layers.aslan;

import AUR.util.aslan.AURGeoMetrics;
import AUR.util.aslan.AURGeoTools;
import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURGeoUtil;
import AUR.util.knd.AURWorldGraph;
import adf.agent.info.WorldInfo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import rescuecore2.misc.geometry.Point2D;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Edge;
import rescuecore2.worldmodel.EntityID;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

/**
 *
 * @author mrse
 */
public class A_BuildingsEntrancePerpendicularLine extends K_ViewerLayer {

        WorldInfo wi;
        Graphics2D g2;
        K_ScreenTransform kst;
        
        @Override
        public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
                this.wi = wsg.wi;
                this.g2 = g2;
                this.kst = kst;
                
                g2.setStroke(new BasicStroke(2));
                g2.setFont(new Font("Arial", 0, 9));

                if(selected_ag == null || ! selected_ag.area.isEdgesDefined() || ! (selected_ag.area instanceof Building))
                        return;
                
                for(Edge e : selected_ag.area.getEdges()){
                        if(! e.isPassable())
                                continue;
                        double[] line = getLine(selected_ag.area, e);
                        
                        g2.setColor(Color.orange);
                        g2.drawLine(
                                kst.xToScreen(line[0]),
                                kst.yToScreen(line[1]),
                                kst.xToScreen(line[2]),
                                kst.yToScreen(line[3])
                        );
                }
        }

        public double[] getLine(Area a,Edge e){
                double[] result = new double[4];
                double[] pME = AURGeoMetrics.getPointFromPoint2D(
                        AURGeoTools.getEdgeMid(e)
                );
                
                double vE[] = new double[]{
                        e.getEndX() - e.getStartX(),
                        e.getEndY() - e.getStartY()
                };
                double[] perpendicularVector = AURGeoMetrics.getPerpendicularVector(vE);
                perpendicularVector = AURGeoMetrics.getVectorNormal(perpendicularVector);
                
                double[] p1 = AURGeoMetrics.getPointsPlus(pME, AURGeoMetrics.getVectorScaled(perpendicularVector, 25000));
                double[] p2 = AURGeoMetrics.getPointsPlus(pME, AURGeoMetrics.getVectorScaled(perpendicularVector, -25000));
                
                result[0] = p1[0];
                result[1] = p1[1];
                result[2] = p2[0];
                result[3] = p2[1];
                
                for(EntityID aid : wi.getObjectIDsInRectangle((int) p1[0], (int) p1[1], (int) p2[0], (int) p2[1])){
                        if(! (wi.getEntity(aid) instanceof Area))
                                continue;
                        
                        Area area = (Area) wi.getEntity(aid);
                        
                        if(area.isEdgesDefined()){
                                for(Edge edge : area.getEdges()){
                                        if(edge.isPassable())
                                                continue;
                                        
                                        double[] intersect = new double[]{-1,-1};
                                        AURGeoUtil.getIntersection(
                                                edge.getLine().getOrigin().getX(),
                                                edge.getLine().getOrigin().getY(),
                                                edge.getLine().getEndPoint().getX(),
                                                edge.getLine().getEndPoint().getY(),
                                                result[0],
                                                result[1],
                                                result[2],
                                                result[3],
                                                intersect
                                        );
                                        
                                        boolean linesIntersect = Line2D.linesIntersect(
                                                edge.getLine().getOrigin().getX(),
                                                edge.getLine().getOrigin().getY(),
                                                edge.getLine().getEndPoint().getX(),
                                                edge.getLine().getEndPoint().getY(),
                                                result[0],
                                                result[1],
                                                result[2],
                                                result[3]
                                        );
                                        
                                        System.out.println(intersect[0] + " :: " + intersect[1]);
                                        
                                        if(intersect[0] != -1 && linesIntersect){
                                                if(AURGeoUtil.length(intersect[0], intersect[1], p1[0], p1[1]) < AURGeoUtil.length(intersect[0], intersect[1], p2[0], p2[1])){
                                                        result[0] = intersect[0];
                                                        result[1] = intersect[1];
                                                }
                                                else{
                                                        result[2] = intersect[0];
                                                        result[3] = intersect[1];
                                                }
                                        }
                                }
                        }
                }
                
                return result;
        }
}


