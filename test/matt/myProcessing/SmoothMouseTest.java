package matt.myProcessing;

import matt.ui.PAppletPlus;

public class SmoothMouseTest extends PAppletPlus {
	
	private static final long serialVersionUID = 7081989453169334183L;
	private static final float CIRCLE_SIZE = 5;

	public void drawPlus() {
		background(255);
		fill(0);
//		text(frameRate, 100, 100);
		noStroke();
		
		fill(255, 0, 0);
		circle(mousePath.getSmoothedCurrentPosition(20), CIRCLE_SIZE);
		fill(0, 180, 180);
		circle(mousePath.getCurrent(), CIRCLE_SIZE);
	}

}
