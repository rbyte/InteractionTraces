package matt.myProcessing;

import java.awt.Color;

import matt.ui.PAppletPlus;

public class LoadingProgressTest extends PAppletPlus {
	
	private static final long serialVersionUID = -9134231009280050660L;
	float progress = 0;
	
	public void drawPlus() {
		drawProgress(progress+=0.001);
	}
	
	@SuppressWarnings("unused")
	public synchronized void drawProgress(double progress) {
		assert 0 <= progress && progress <= 1;
		background(255);
		noStroke();
		int tone = (int) (100*(1-progress))+100;
		fill(tone);
		int barHeight = (int) (height*progress);
		rect(0, height-barHeight, width, barHeight);
		if (false)
			image(linearGradient(new Color(tone, tone, tone), new Color(255, 255, 255),
				width, barHeight, true), 0, height-barHeight);
	}
	
}
