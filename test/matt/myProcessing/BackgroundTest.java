package matt.myProcessing;

import matt.parameters.Params;
import matt.ui.PAppletPlus;
import matt.ui.PImagePlus;

public class BackgroundTest extends PAppletPlus {
	
	private static final long serialVersionUID = 8890928091463998582L;
	PImagePlus bg = loadImage(Params.pathToTextures+"body_bg_010.jpg");
	
	public void drawLoadingScreen() {
		// http://ricardo-langner.de/
//		linearGradient(g, new Color(255, 255, 255), new Color(230, 230, 230), true);
//		background(255);
//		smooth();
//		stroke(0, 10);
//		for (int x=-height; x<width; x+=5) {
//			line(0+x, 0, height+x, height);
//			line(width-x, 0, width-x-height, height);
//		}
	}
	
	public void drawPlus() {
		backgroundImage(bg);
	}
	
}


