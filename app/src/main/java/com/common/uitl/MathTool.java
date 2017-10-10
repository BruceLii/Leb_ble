package com.common.uitl;

import android.graphics.Point;
import android.graphics.PointF;

public class MathTool {

	
	
	public static double computeDistance(Point p1, Point p2) {
		double d = (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
		return Math.sqrt(d);
	}
	
	public static double computeDistance(Point p1, PointF p2) {
		double d = (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
		return Math.sqrt(d);
	}

	/**
	 * 求经过2点的直线
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Line computeLineBy2Point(Point p1, Point p2) {
		float k;
		float b;
		if (p1.x == p2.x) {
			b = 0;
			k = 0;
		} else {
			k = (float) (p1.y - p2.y) / (p1.x - p2.x);
			b = p1.y - k * p1.x;
		}
		Line line = new Line();
		line.setK(k);
		line.setB(b);
		return line;
	}

	/**
	 * 求直线的垂线斜率
	 * 
	 * @param k
	 * @return
	 */
	public static float getVerticalSlop(float k) {
		if (0 != k) {
			return (-1 / k);
		}
		return 0;
	}

	public static Line getLineByKP(float k, Point p) {
		Line line = new Line();
		line.setK(k);
		if (k == 0) {
			if (p.x != 0) {
				line.setB(0);
			}
		} else {
			line.setB(p.y - k * p.x);
		}
		return line;
	}
}
