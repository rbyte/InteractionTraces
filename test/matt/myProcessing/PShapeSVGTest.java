package matt.myProcessing;

import matt.parameters.Params;
import matt.ui.PAppletPlus;

import processing.core.PShape;

public class PShapeSVGTest extends PAppletPlus {
	
	private static final long serialVersionUID = 8890928091463998582L;
	PShape text;
	
	public void setupPlus() {
		text = loadShape(Params.pathToProcessing+"example.svg");
//		text.disableStyle();
	}
	
	public void drawPlus() {
		background(255);
		smooth();
		float width = mouseX*2f;
		float height = text.height/text.width*width;
		fill(230);
		rect(10, 10, width, height);
		fill(255,0,0);
		shape(text, 10, 10, width, height);
	}
}
