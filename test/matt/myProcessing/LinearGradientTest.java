package matt.myProcessing;

import java.awt.Color;

import matt.ui.PAppletPlus;

public class LinearGradientTest extends PAppletPlus {
	private static final long serialVersionUID = 7980529412345211427L;
	
	public void drawPlus() {
		background(255);
		linearGradient(g, new Color(255, 120, 0), new Color(10, 45, 255), true);
//		image(generateGradient(new Color(255, 120, 0), new Color(10, 45, 255), 100, 100, true), 0, 0);
	}

}
