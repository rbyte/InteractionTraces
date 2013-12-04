package matt.myProcessing;

import java.awt.geom.Point2D;

import matt.ui.PAppletPlus;

public class SchweifTest extends PAppletPlus {
	
	private static final long serialVersionUID = 7081989918169334183L;

	public void drawPlus() {
		background(255);
		fill(0);
		text(frameRate, 100, 100);
		noStroke();
		
		int xAmount = 100;
		int yAmount = 100;
		for (int i=0; i<xAmount; i++) {
			for (int j=0; j<yAmount; j++) {
				Point2D.Float position = new Point2D.Float(i*width/xAmount, j*height/yAmount);
				float size = 10000/(1+mousePath.getSchweifDistance(position));
				ellipse(position.x, position.y, size, size);
			}
		}
	}

}
