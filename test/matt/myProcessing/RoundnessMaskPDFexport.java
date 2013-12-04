package matt.myProcessing;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import matt.parameters.Params;
import matt.ui.PImagePlus;
import matt.util.Util;
import processing.core.PApplet;
import processing.core.PImage;
import processing.pdf.*;

@SuppressWarnings("unused")
public class RoundnessMaskPDFexport extends PApplet {

	private static final long serialVersionUID = 8554791615408968512L;

	private Rectangle2D.Float imageRect = new Rectangle2D.Float(50, 50, 300, 450);
	private Rectangle2D.Float mouseActionRect = new Rectangle2D.Float(500, 125, 300, 300);
	
	public void setup() {
		size(900, 550, PDF, Params.pathToProcessing + "filename.pdf");
	}

	public void draw() {
//		float roundPercentage = mouseX/(float) width;
//		double roundPercentage = Util.percentiseIn(mouseX, mouseActionRect.getMinX(), mouseActionRect.getMaxX());
		double roundPercentage = 0.5;
//		float shrinkPercentage = mouseY/(float) height;
//		double shrinkPercentage = Util.percentiseIn(mouseY, mouseActionRect.getMinY(), mouseActionRect.getMaxY());
		double shrinkPercentage = 0.5;
		
		background(255);
		noStroke();
		fill(200);
		rect(imageRect.x, imageRect.y, imageRect.width, imageRect.height);
		rect(mouseActionRect.x, mouseActionRect.y, mouseActionRect.width, mouseActionRect.height);
		fill(150);
		textSize(30);
		text("roundness",
			(float) mouseActionRect.getMaxX()-141,
			(float) mouseActionRect.getMinY()+23
			);
		text(String.format("%.2f", roundPercentage),
			(float) Util.linearInterpolation(mouseActionRect.getMinX(), mouseActionRect.getMaxX(), roundPercentage),
			(float) mouseActionRect.getMinY()
			);
		text("shrink",
			(float) mouseActionRect.getMinX(),
			(float) mouseActionRect.getMaxY()
			);
		text(String.format("%.2f", shrinkPercentage),
			(float) (mouseActionRect.getMinX()-60),
			(float) Util.linearInterpolation(mouseActionRect.getMinY(), mouseActionRect.getMaxY(), shrinkPercentage)+20
			);
		
		
		final float two = 2f;
		float d = (float) (Math.min(imageRect.height, imageRect.width) * roundPercentage);
		float cx = 0, cy = 0;
		if (imageRect.height > imageRect.width) {
			cy = (float) (Math.abs(imageRect.height-imageRect.width) * shrinkPercentage);
		} else {
			cx = (float) (Math.abs(imageRect.height-imageRect.width) * shrinkPercentage);
		}
		
		fill(80);
		ellipse(imageRect.x+d/two+cx/two, imageRect.y+d/two+cy/two, d, d);
		ellipse(imageRect.x+imageRect.width-d/two-cx/two, imageRect.y+d/two+cy/two, d, d);
		ellipse(imageRect.x+d/two+cx/two, imageRect.y+imageRect.height-d/two-cy/two, d, d);
		ellipse(imageRect.x+imageRect.width-d/two-cx/two, imageRect.y+imageRect.height-d/two-cy/two, d, d);
		fill(100);
		rect(imageRect.x+cx/two, imageRect.y+d/two+cy/two, imageRect.width-cx, imageRect.height-d-cy);
		rect(imageRect.x+d/two+cx/two, imageRect.y+0+cy/two, imageRect.width-d-cx, imageRect.height-cy);
//		image(image, imageRect.x, imageRect.y);
		
		println("Finished.");
		exit();
	}

}
