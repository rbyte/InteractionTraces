package matt.myProcessing;

import matt.ui.PAppletPlus;
import matt.ui.PImagePlus;

public class PImagePlusTest extends PAppletPlus {
	
	private static final long serialVersionUID = 1568059373643207318L;
	private static int underlayingRectColour = 0;
	
	
	public void drawPlus() {
		background(255);
		noStroke();
		fill(underlayingRectColour++ % 255);
		rect(50, 50, 100, 100);
		
		test1(50, 50, 100, 100);
//		test1(50, -50, 100, 100);
//		test1(-50, 50, 100, 100);
//		test1(-50, -50, 100, 100);
//		test1(25, 25, 25, 25);
//		test1(50, 25, 300, 220);
	}
	
	public void test1(int x, int y, int w, int h) {
		PImagePlus bg = new PImagePlus(100, 100, this, 0xFFFF0000);
		PImagePlus add = new PImagePlus(100, 100, this, 0x550000FF);
		bg.overlay(add, x, y);
		image(bg, 50, 50);
	}
	
}
