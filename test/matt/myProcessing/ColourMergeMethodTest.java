package matt.myProcessing;

import java.awt.Color;

import matt.ui.PAppletPlus;

public class ColourMergeMethodTest extends PAppletPlus {
	
	private static final long serialVersionUID = -8237827285426310229L;

	public void drawPlus() {
		background(255);
		strokeWeight(10);
		
		float y = 100;
		float x = 100;
		float l = 500;
		stroke(new Color(255, 0, 0, 10));
		for (int i=0; i<30; i++) {
			line(x, y, x+l, y);
		}
		
		y = 112;
		stroke(new Color(255, 0, 0, 255));
		for (int i=0; i<1; i++) {
			line(x, y, x+l, y);
		}
	}

}
