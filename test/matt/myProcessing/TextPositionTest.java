package matt.myProcessing;

import matt.ui.PAppletPlus;

public class TextPositionTest extends PAppletPlus {
	
	private static final long serialVersionUID = 2712064706503038262L;
	
	public void setupPlus() {
		background(255);
		fill(0);
		rect(0, 50, 1, 1);
		textSize(20);
		rect(0, 30, 1, 1);
		text("Ilgq^*|", 0, 50);
		
	}
	
}
