package matt.myProcessing;

import java.awt.Color;

import matt.ui.PAppletPlus;
import matt.ui.PToolbox;
import matt.util.PolarPoint;

public class RadialGradientTest extends PAppletPlus {
	
	private static final long serialVersionUID = -2817999020467736174L;

	public void drawPlus() {
		background(255);
		
//		fill(255, 0, 0);
//		rect(50,50, 10, 10);
		
		drawRadialGradient(100, 100, 100, 0, new Color(0, 0, 0, 255), new Color(0, 0, 0, 0), 1d);
		drawRadialGradient(150, 150, 100, 0, new Color(0, 0, 0, 255), new Color(0, 0, 0, 0), 1d);
		
//		drawRadialGradient(500, 500, 490, 0, new Color(0, 0, 0, 255), new Color(0, 0, 0, 0), 1d);
		
//		loadPixels();
//		assert pixels != null;
//		drawRadialGradient(pixels, 1000, 1000, 500, 500, 490, 0, new Color(0, 0, 0, 255), new Color(0, 0, 0, 0), 1d);
//		updatePixels();
	}
	
	public void drawRadialGradient(int[] pix, int width, int height,
			float centerX, float centerY, float outterRadius, float innerRadius,
			Color innerColour, Color outterColour, double pow) {
		PolarPoint center = new PolarPoint(centerX, centerY, "xy");
		for (int px=0; px<width; px++) {
			for (int py=0; py<height; py++) {
				double n = (center.distanceTo(new PolarPoint((double) px, (double) py, "xy"))-innerRadius)/(outterRadius-innerRadius);
				if (n<0) n = 0;
				if (n>1) n = 1;
				if (pow != 1 && n != 0) n = Math.pow(n, pow);
				pix[py*width+px] = PToolbox.mingleColours(innerColour, outterColour, n).getRGB();
			}
		}
	}

}
