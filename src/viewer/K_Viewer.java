package viewer;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import viewer.layers.knd.K_CommonWalls;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.StyleConstants;
import viewer.fromMisc.PanZoomListener;
import viewer.layers.AmboLayers.*;
import viewer.layers.knd.*;

/**
 *
 * @author Alireza Kandeh - 2017 & 2018
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
	addLayer(K_LayerWorldGraph.class, false);
	addLayer(K_AreaVertices.class, false);
	addLayer(K_AreaExtinguishableRange.class, false);
	addLayer(K_ShortestPath.class, false);
	addLayer(CivilianLayer.class, true);
	addLayer(K_AreaPropery.class, true);
	addLayer(K_AirCells.class, false);
	addLayer(K_BuildingAirCells.class, false);
	addLayer(K_AreaGrid.class, false);
	addLayer(K_AreaPassableSegments.class, false);
	addLayer(K_AreaGraph.class, false);

	addLayer(K_BuildingPerceptibleAreas.class, true);
	addLayer(K_PerceptibleAreaPolygon.class, false);
	addLayer(K_PerceptibleBuildings.class, true);
	addLayer(K_ShortestPathToCheckFire.class, true);
	addLayer(K_RoadScore.class, false);
	addLayer(K_SmallAreas.class, false);
	addLayer(K_MediumAreas.class, false);
	addLayer(K_BigAreas.class, false);
	addLayer(K_CommonWalls.class, false);
	addLayer(K_AgentsLayer.class, true);
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
    		colors_list.add(new Color((int) (Math.random() * 255), (int) ( Math.random() * 255), (int) (Math.random() * 255)));
    	}
    }
    
    private static K_Viewer _instance = null;
    
    public static synchronized K_Viewer getInstance() {
        if(_instance == null) {
            _instance = new K_Viewer();
        }
        return _instance;
    }
    
    public AURAreaGraph selected_ag = null;
    K_ScreenTransform kst = null;
    
    AURWorldGraph selected = null;
    
    ArrayList<AURWorldGraph> wsgs = new ArrayList<AURWorldGraph>();
    DefaultListModel<String> model = new DefaultListModel();
	
    JComboBox list = new JComboBox();
    
    int lastUpdateTimeSelected = -2;
    
    public synchronized void update(AURWorldGraph wsg) {
        if(wsgs.contains(wsg) == false) {
            wsgs.add(wsg);
            list.addItem(wsg.ai.me().getURN().replace("urn:rescuecore2.standard:entity:", "") + ": " + wsg.ai.getID().getValue());
        }
        
        if(selected == null) {
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
    
    JTextArea textArea = new JTextArea();
            
    public K_Viewer() {

        addLayers();
        
//        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout(StyleConstants.ALIGN_LEFT));
        
        
        
        
        

            
        JPanel panel = new JPanel(new BorderLayout());
            
            

        list.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getItem() != null && wsgs.size() > 0) {
                    selected = wsgs.get(list.getSelectedIndex());
                    selected_ag = null;
                    repaint();
                }                
            }

        });
            
        JPanel panel_layers = new JPanel();
        BoxLayout bxl = new BoxLayout(panel_layers, BoxLayout.Y_AXIS);
        panel_layers.setLayout(bxl);


        for(JCheckBox chb : layersCheckBox) {
            panel_layers.add(chb);
            chb.addItemListener(repaintEvent);
        }
        
        this.setLayout(new BorderLayout());
        
        this.add(new DrawPanel(), BorderLayout.CENTER);
        
        this.add(panel_layers, BorderLayout.WEST);
        
        
        JScrollPane jsp = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(250, 250));
        this.add(jsp, BorderLayout.EAST);

        

        this.setVisible(true);

        this.pack();

        this.setVisible(true);
            
    }
    
    class DrawPanel extends JPanel {

        int w = 1000;
        int h = 800;
        
        JCheckBox agent = new JCheckBox("Agent", true);
        
        public DrawPanel() {
            
            setDoubleBuffered(true);
            
            setPreferredSize(new Dimension(w, h));
            
            kst = new K_ScreenTransform(0, 0, w, h);
            kst.rescale(w, h);
            this.setLayout(new FlowLayout(StyleConstants.ALIGN_LEFT));
            JButton resetBtn = new JButton("reset zoom");
            resetBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    kst.resetZoom();
                    new PanZoomListener(DrawPanel.this).setScreenTransform(kst);
                    repaint();
                }
            });

            //list.setPreferredSize(new Dimension(190, 30));
            this.add(list);
            this.add(resetBtn);
            
            this.setDoubleBuffered(true);
            

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

		public Color getAgentColor() {
			if(selected == null) {
				return null;
			}
			switch(selected.ai.me().getStandardURN()) {
				case AMBULANCE_TEAM: {
					return Color.WHITE;
				}
				case POLICE_FORCE: {
					return Color.BLUE;
				}
				case FIRE_BRIGADE: {
					return Color.RED;
				}
			}
			return Color.BLACK;
		}
		
        @Override
        public void paint(Graphics g) {
            if(selected == null) {
                return;
            }
			
			
        	Graphics2D g2 = (Graphics2D) g;
			
			//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g.setColor(new Color(170, 170, 170));
        	g.fillRect(0, 0, getWidth(), getHeight());
			
			for(JCheckBox chb : layersCheckBox) {
				if(chb.isSelected()) {
					K_ViewerLayerFactory.getInstance().getLayer(chb.getText()).paint(g2, kst, selected, selected_ag);
				}
			}

			if(agent.isSelected() == true) {
				int agentX = (int) (selected.ai.getX());
				int agentY = (int) (selected.ai.getY());
				Color color = getAgentColor();
				g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
				g2.setStroke(new BasicStroke(1));
				g.drawLine(-100000, kst.yToScreen(agentY - 0), w + 100000, kst.yToScreen(agentY - 0));
				g.drawLine(kst.xToScreen(agentX - 0), -100000, kst.xToScreen(agentX - 0), h + 100000);

				g.setColor(color);
				g.fillOval(kst.xToScreen(agentX - 500), kst.yToScreen(agentY + 500), (int) (1000 * kst.zoom), (int) (1000 * kst.zoom));
			}

			if(arr.size() > 0) {
				for(double[][] arr_ : arr) {
					for(int i = 1; i < arr_.length; i++) {
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
			
            super.paintComponents(g);
            updateStringData();
        } 
    }
    
    
    private void updateStringData() {
        String str = "";
        for(JCheckBox chb : layersCheckBox) {
            if(chb.isSelected()) {
                String s = K_ViewerLayerFactory.getInstance().getLayer(chb.getText()).getString(selected, selected_ag);
                if(s != null) {
                    str += chb.getText() + ": ";
                    str += "\n";
                    str += s;
                    str += "\n";
                    str += "---------------------------------------------------------\n";
                }
            }
        }
        
        textArea.setText(str);
    }
    
}
