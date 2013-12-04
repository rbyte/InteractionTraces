package matt.myProcessing;

import java.awt.Color;

import matt.ui.PAppletPlus;

public class AquaBubbleTest extends PAppletPlus {
	private static final long serialVersionUID = 7980529412411211427L;
	
	public void drawPlus() {
		background(255);
		Color innerColour = Color.getHSBColor(136f/255f, 153f/255f, 255f/255f);
		Color outterColour = Color.getHSBColor(162f/255f, 255f/255f, 69f/255f);
		float diameter = mouseY+5;
		drawBubble(diameter/2, diameter/2, diameter, innerColour, outterColour,
				(double) mouseX/(double) width, true, true);
	}
}
