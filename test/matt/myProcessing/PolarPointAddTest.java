package matt.myProcessing;

import matt.ui.PAppletPlus;
import matt.util.PolarPoint;

public class PolarPointAddTest extends PAppletPlus {
	
	private static final long serialVersionUID = 8890928091463998582L;

	public void setupPlus() {
		
	}
	
	public void drawPlus() {
		pointAddTest();
	}
	
	public void pointAddTest() {
		background(255);
		stroke(0, 0, 0);
		line(0, screenHeight/2, screenWidth, screenHeight/2);
		line(screenWidth/2, 0, screenWidth/2, screenHeight);
		
		fill(100);
		PolarPoint p = new PolarPoint(45, "°", 300);
		PolarPoint mouse = new PolarPoint(mouseX-screenWidth/2, mouseY-screenHeight/2, "xy");
		PolarPoint add = mouse.clone().add(p);
		
		drawPolarPoint(p);
		drawPolarPoint(mouse);
		drawPolarPoint(add);
		
//		drawPolarLine(mouse, p);
		drawPolarLine(mouse, add);
		drawPolarLine(p, add);
		
//		text(""+PolarPoint.radiantsToDegrees(p.clone().angleDelta(mouse)), 200, 200);
	}
	
}


