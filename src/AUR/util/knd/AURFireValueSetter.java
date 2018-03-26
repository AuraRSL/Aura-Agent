package AUR.util.knd;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import rescuecore2.standard.entities.Building;

/**
 *
 * @author Alireza Kandeh - 2017 & 2018
 */

public class AURFireValueSetter {

	public AURConvexHull convexHullInstance = new AURConvexHull();
	public ArrayList<AURAreaGraphValue> points = new ArrayList<AURAreaGraphValue>();
	public AURFireSimulator fireSimulatorInstance = null;

	public void calc(AURWorldGraph wsg, ArrayList<AURAreaGraphValue> points) {

                
            
		wsg.updateInfo(null);
		wsg.dijkstra(wsg.ai.getPosition());

                this.fireSimulatorInstance = wsg.fireSimulator;
                
		this.points.clear();
		this.points.addAll(points);

		for (AURAreaGraphValue p : this.points) {
			p.value = 0;
		}

//		calc_ConvexHull(this.points, 1.5);
//
//		calc_Capacity(this.points, 0.0);
		calc_EstimatedFieryness(this.points, 1.3);
		add_TravelCost(this.points, 1.5);
		calc_GasStation(this.points, 1.6);
		calc_noName(this.points, 1.6);
		mul_Color(wsg, points, 1.01);
		mul_Borders(points, 1.5);
		Collections.sort(this.points, new Comparator<AURAreaGraphValue>() {
			@Override
			public int compare(AURAreaGraphValue o1, AURAreaGraphValue o2) {
				return Double.compare(o2.value, o1.value);
			}
		});
	}
	
	private void add_TravelCost(ArrayList<AURAreaGraphValue> points, double coefficient) {
		double maxDist = 0;
		for (AURAreaGraphValue p : points) {
			p.temp_value = p.ag.getBuilding().getPerceptTime();
			if (p.temp_value > maxDist) {
				maxDist = p.temp_value;
			}
		}

		for (AURAreaGraphValue p : points) {
			p.value += (1 - (p.temp_value / maxDist)) * coefficient;
		}
	}
	public void mul_selfDistance(AURWorldGraph wsg, ArrayList<AURAreaGraphValue> points, double coefficient) {
		double max_ = 0;
		for (AURAreaGraphValue p : points) {
			p.temp_value = p.ag.distFromAgent();
			if (p.temp_value > max_) {
				max_ = p.temp_value;
			}
		}
		if (max_ > 0) {
			for (AURAreaGraphValue p : points) {
				p.temp_value /= max_;
				p.temp_value /= 10;
				p.temp_value = 0.1 - p.temp_value;
			}
		}
		for (AURAreaGraphValue p : points) {
			p.value *= (1 + p.temp_value) * coefficient;
		}
	}



	public void mul_Color(AURWorldGraph wsg, ArrayList<AURAreaGraphValue> points, double coefficient) {
		int agentColor = wsg.getAgentColor();
		for (AURAreaGraphValue p : points) {
			if (p.ag.color == agentColor) {
				p.value *= (wsg.colorCoe[p.ag.color][agentColor]) * coefficient;
			}

		}
	}

//	private void calc_Capacity(ArrayList<AURValuePoint> points, double coefficient) {
//		convexHullInstance.calc(points);
//		double max_ = 0;
//		for (AURValuePoint p : points) {
//			p.temp_value = p.areaGraph.getBuilding().fireSimBuilding.getCapacity();
//			if (p.temp_value > max_) {
//				max_ = p.temp_value;
//			}
//		}
//		if (max_ > 0) {
//			for (AURValuePoint p : points) {
//				p.temp_value /= max_;
//			}
//		}
//		for (AURValuePoint p : points) {
//			p.value += p.temp_value * coefficient;
//		}
//	}

	private void mul_Borders(ArrayList<AURAreaGraphValue> points, double coefficient) {
		for (AURAreaGraphValue agv : points) {
			if (agv.ag.isBuilding()) {
				if(agv.ag.getBuilding().fireSimBuilding.isOnFireZoneBorder() == true) {
					agv.value *= coefficient;
				}
				
			}

		}
	}

	private void calc_EstimatedFieryness(ArrayList<AURAreaGraphValue> points, double coefficient) {
		for (AURAreaGraphValue p : points) {
			AURFireSimBuilding b = p.ag.getBuilding().fireSimBuilding;
			switch (b.getEstimatedFieryness()) {
				case 1: {
					p.value += 1 * coefficient;
					break;
				}
				case 2: {
					p.value += 0.5 * coefficient;
					break;
				}
				case 3: {
					p.value += 0.001 * coefficient;
					break;
				}
			}
		}
	}

	private void calc_GasStation(ArrayList<AURAreaGraphValue> points, double coefficient) {
		double maxDist = 0;
		for (AURAreaGraphValue p : points) {
			p.temp_value = p.ag.lineDistToClosestGasStation();
			if (p.temp_value > maxDist) {
				maxDist = p.temp_value;
			}
		}

		if (maxDist > 0) {
			for (AURAreaGraphValue p : points) {
				p.value += (1 - (p.temp_value / maxDist)) * coefficient;
			}
		}
	}
	
	private void calc_noName(ArrayList<AURAreaGraphValue> points, double coefficient) {
//		double max = 0;
//		for (AURValuePoint p : points) {
//			p.temp_value = p.areaGraph.countUnburntsInGrid();
//			if (p.temp_value > max) {
//				max = p.temp_value;
//			}
//		}
//
//		if (max > 0) {
//			for (AURValuePoint p : points) {
//				p.value += ((p.temp_value / max)) * coefficient;
//			}
//		}
	}

//	public void draw(Graphics2D g) {
//		convexHullInstance.draw(g);
//		int a = 5;
//		g.setFont(new Font("TimesRoman", Font.PLAIN, 1500));
//		for (AURValuePoint point : this.points) {
//			g.setColor(Color.gray);
//			g.fillRect((int) (point.x - a), (int) (point.y - a), 2 * a, 2 * a);
//			g.setColor(Color.black);
//			g.drawString(point.value + "", (int) (point.x), (int) (point.y));
//		}
//	}
}