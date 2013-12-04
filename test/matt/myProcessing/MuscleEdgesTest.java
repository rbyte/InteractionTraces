package matt.myProcessing;

import java.awt.Color;

import matt.ui.PAppletPlus;
import matt.util.Circle;
import matt.util.PolarPoint;

public class MuscleEdgesTest extends PAppletPlus {
	
	private static final long serialVersionUID = 8890928091463998582L;
	
	
	private int amount = 2000;
	private double[] random = new double[amount];
	private double[] random2 = new double[amount];
	
	public void setupPlus() {
		random = fillRandom(random);
		random2 = fillRandom(random2);
	}
	
	public void drawPlus() {
		circleTest();
	}
	
	public double[] fillRandom(double[] arr) {
		for (int i=0; i<arr.length; i++) {
			arr[i] = Math.random();
		}
		return arr;
	}
	
	public void circleTest() {
//		System.out.println("bla");
		background(255);
		stroke(0, 0, 0);
		
		float percentage = mouseX/(float) width;
		float percentageY = mouseY/(float) height;
		float percentageOfTracesShown = percentageY;
//		float percentageOfTracesShown = 0.5f;
//		float handleLength = 0.3f;
		float handleLength = percentage/2;
		float edgeThickness = 1f;
		
		Circle c1 = new Circle(new PolarPoint(width/2, height/2, "xy"), 50);
		circle(c1);
		
		Circle c2 = new Circle(new PolarPoint(mouseX, mouseY, "xy"), 100);
		circle(c2);
		
		for (int i=0; i<random.length*percentageOfTracesShown; i++) {
			traceEdgeDrawer(c1, c2, (random[i]-0.5)*2, handleLength, edgeThickness, new Color(0, 0, 0, 10));
		}
	}
	
}


