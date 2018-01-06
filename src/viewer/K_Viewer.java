package viewer;

import kndStuff.fromMisc.PanZoomListener;
import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.StyleConstants;
import viewer.layers.knd.K_AreaExtinguishableRange;
import viewer.layers.knd.K_AreaVertices;
import viewer.layers.knd.K_ClosestPath;
import viewer.layers.knd.K_LayerAliveBlockades;
import viewer.layers.knd.K_LayerAllBlockades;
import viewer.layers.knd.K_LayerAreaCenters;
import viewer.layers.knd.K_LayerBuildings;
import viewer.layers.knd.K_LayerBuildingsClusterColor;
import viewer.layers.knd.K_LayerRoads;
import viewer.layers.knd.K_LayerTravelCost;
import viewer.layers.knd.K_LayerWalls;
import viewer.layers.knd.K_LayerWorldGraph;
import viewer.layers.knd.k_LayerReachableAreas;

/**
 *
 * @author Alireza Kandeh - 2017
 */

public class K_Viewer extends JFrame {

    private void addLayers() {
        addLayer(K_LayerRoads.class, true);
        addLayer(K_LayerBuildings.class, true);
        addLayer(K_LayerBuildingsClusterColor.class, false);
        addLayer(K_LayerAreaCenters.class, false);
        addLayer(k_LayerReachableAreas.class, false);
        addLayer(K_LayerTravelCost.class, false);
        addLayer(K_LayerAliveBlockades.class, true);
        addLayer(K_LayerAllBlockades.class, true);
        addLayer(K_LayerWalls.class, true);
        addLayer(K_LayerWorldGraph.class, true);
        addLayer(K_AreaVertices.class, false);
        addLayer(K_AreaExtinguishableRange.class, false);
        addLayer(K_ClosestPath.class, true);
    }
    
    
    ArrayList<double[][]> arr = new ArrayList<>();

    public static Color colors[] = {
        new Color(255, 000, 000),
        new Color(000, 250, 000),
        new Color(000, 000, 250),
        new Color(255, 255, 000),
        new Color(255, 000, 255),
        new Color(000, 255, 255),
        new Color(128, 000, 000),
        new Color(000, 128, 000),
        new Color(000, 000, 128),
        new Color(128, 128, 000),
        new Color(128, 000, 128),
        new Color(000, 128, 128),
    		
    };
    
    public static ArrayList<Color> colors_list = new ArrayList<>();
    
    static {
    	for(int i = 0; i < colors.length; i++) {
    		colors_list.add(colors[i]);
    	}
    	for(int i = 0; i < 350; i++) {
    		colors_list.add(new Color((int)( Math.random() * 255), (int)( Math.random() * 255), (int)( Math.random() * 255)));
    	}
    }
    
    /*public void readRays() {
        try {
            File file = new File("/home/single2admin/app/roborescue-v1.2/boot/rays/34912632.rays");
            Scanner scn = new Scanner(file);
            scn.nextFloat();
            while(scn.hasNext()) {
                double arr_[][];
                //scn.nextFloat();
                double cx, cy;
                cx = scn.nextFloat();
                cy = scn.nextFloat();
                int n = scn.nextInt();
                arr_ = new double[n + 1][3];
                arr_[0][0] = cx;
                arr_[0][1] = cy;
                for(int i = 0; i < n; i++) {
                    arr_[i + 1][0] = scn.nextInt();
                    arr_[i + 1][1] = scn.nextInt();
                    arr_[i + 1][2] = scn.nextFloat();
                }
                arr.add(arr_);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("rays.");
    }*/
    
    private static K_Viewer _instance = null;
    
    public static synchronized K_Viewer getInstance() {
        if(_instance == null) {
            _instance = new K_Viewer();
            //_instance.readRays();
        }
        return _instance;
    }
    
    public AURAreaGraph selected_ag = null;
    K_ScreenTransform kst = null;
    
    AURWorldGraph selected = null;
    
    ArrayList<AURWorldGraph> wsgs = new ArrayList<AURWorldGraph>();
    DefaultListModel<String> model = new DefaultListModel();
    
    JList list = new JList(model);
    
    int lastUpdateTimeSelected = -2;
    
    public synchronized void update(AURWorldGraph wsg) {
        if(wsgs.contains(wsg) == false) {
            wsgs.add(wsg);
            model.addElement(wsg.ai.me().getURN().replace("urn:rescuecore2.standard:entity:", "") + ": " + wsg.ai.getID().getValue());
        }
        
        if(selected == null) {
            list.setSelectedIndex(0);
            selected = wsg;
        }
        if(selected == wsg && lastUpdateTimeSelected != wsg.ai.getTime()) {
            lastUpdateTimeSelected = wsg.ai.getTime();
            repaint();
        }
    	
    }
    
    private void addLayer(Class c, boolean de) {
        String packageName = c.getPackage().getName();
        String className = c.getName().replace(packageName + ".", "");
        K_ViewerLayerFactory.getInstance().addLayer(className, c);
        layersCheckBox.add(new JCheckBox(className, de));
    }
    
    private ArrayList<JCheckBox> layersCheckBox = new ArrayList<JCheckBox>();

    ItemListener repaintEvent = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            repaint();
        }
    };
            
    public K_Viewer() {

        addLayers();
        
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout(StyleConstants.ALIGN_LEFT));
        
        
        this.add(new DrawPanel());

            
            JPanel panel = new JPanel(new BorderLayout());
            
            

            list.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if(e.getValueIsAdjusting() == false && wsgs.size() > 0) {
                        selected = wsgs.get(list.getSelectedIndex());
                        selected_ag = null;
                        repaint();
                    }
                }

            });
            
            
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane pane = new JScrollPane(list);
            panel.add(pane, BorderLayout.NORTH);

            pane.setPreferredSize(new Dimension(190, 800));
            

            
            this.add(panel);
            
            
            JPanel panel_layers = new JPanel();
            BoxLayout bxl = new BoxLayout(panel_layers, BoxLayout.Y_AXIS);
            panel_layers.setLayout(bxl);
            
            for(JCheckBox chb : layersCheckBox) {
                panel_layers.add(chb);
                chb.addItemListener(repaintEvent);
            }
            this.add(panel_layers);

            this.setVisible(true);

            this.pack();
            
            this.setVisible(true);
            
    }
    
    class DrawPanel extends JPanel {

        int w = 800;
        int h = 800;
        
        Checkbox agent = new Checkbox("Agent", true);
        
        public DrawPanel() {
            
            setDoubleBuffered(true);
            
            setPreferredSize(new Dimension(w, h));
            
            kst = new K_ScreenTransform(0, 0, w, h);
            kst.rescale(w, h);
            this.setLayout(new FlowLayout(StyleConstants.ALIGN_LEFT));
            Button resetBtn = new Button("reset zoom");
            resetBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    kst.resetZoom();
                    new PanZoomListener(DrawPanel.this).setScreenTransform(kst);
                    repaint();
                }
            });

            this.add(resetBtn);
            

            agent.addItemListener(repaintEvent);
            this.add(agent);
            
            new PanZoomListener(DrawPanel.this).setScreenTransform(kst);
            
            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    double x = kst.screenToX((int) (e.getX() - 0));
                    double y = kst.screenToY((int) (e.getY() - 0));
                    if(selected == null) {
                        return;
                    }
                    for(AURAreaGraph ag : selected.areas.values()) {
                        if(ag.area.getShape().contains(x, y)) {
                            selected_ag = ag;
                        }
                    }
                    repaint();

                }
            });
        }

        @Override
        public void paint(Graphics g) {
            //System.err.println("paint");
            super.paint(g);
            if(selected == null) {
                return;
            }
            //synchronized(selected) {
        	Graphics2D g2 = (Graphics2D) g;
                //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(170, 170, 170));
        	g.fillRect(0, 0, getWidth(), getHeight());


                for(JCheckBox chb : layersCheckBox) {
                    if(chb.isSelected()) {
                        K_ViewerLayerFactory.getInstance().getLayer(chb.getText()).paint(g2, kst, selected, selected_ag);
                    }
                }

                if(agent.getState() == true) {
                    int agentX = (int) (selected.ai.getX());
                    int agentY = (int) (selected.ai.getY());
                    g.setColor(Color.white);
                    g.fillOval(kst.xToScreen(agentX - 500), kst.yToScreen(agentY + 500), (int) (1000 * kst.zoom), (int) (1000 * kst.zoom));

                    g.setColor(new Color(255, 255, 255, 100));
                    g.drawLine(-100000, kst.yToScreen(agentY - 0), w + 100000, kst.yToScreen(agentY - 0));
                    g.drawLine(kst.xToScreen(agentX - 0), -100000, kst.xToScreen(agentX - 0), h + 100000);
                }
                
                if(arr.size() > 0) {
                    for(double[][] arr_ : arr) {
                        for(int i = 1; i < arr_.length; i++) {
                            /*if(arr_[0][0] != 748390 && true) {
                                continue;
                            }*/
                            double d = arr_[i][2];
                            g.setColor(new Color(255, 255, 255, (int) (Math.pow(d, 0.3) * 255)));
                            g.drawLine(kst.xToScreen(arr_[0][0]),
                                kst.yToScreen(arr_[0][1]),
                                kst.xToScreen(arr_[i][0]),
                                kst.yToScreen(arr_[i][1])
                            );
                        }
                    }
                }
            //}
        } 
    }
    
}