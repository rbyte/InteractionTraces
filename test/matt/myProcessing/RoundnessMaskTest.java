package matt.myProcessing;

import java.awt.geom.Rectangle2D;

import matt.ui.PAppletPlus;
import matt.util.Util;

public class RoundnessMaskTest extends PAppletPlus {
	
	private static final long serialVersionUID = 6889955184890560629L;

	private Rectangle2D.Float imageRect = new Rectangle2D.Float(50, 50, 300, 450);
	private Rectangle2D.Float mouseActionRect = new Rectangle2D.Float(500, 125, 300, 300);
	
	public void drawPlus() {
//		float roundPercentage = mouseX/(float) width;
		double roundPercentage = Util.percentiseIn(mouseX, mouseActionRect.getMinX(), mouseActionRect.getMaxX());
//		float shrinkPercentage = mouseY/(float) height;
		double shrinkPercentage = Util.percentiseIn(mouseY, mouseActionRect.getMinY(), mouseActionRect.getMaxY());
		
		background(255);
		noStroke();
		fill(200);
		rect(imageRect);
		rect(mouseActionRect);
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
		
//		PImagePlus image = new PImagePlus(
//				(int) imageRect.width,
//				(int) imageRect.height,
//				this,
//				new Color(100, 100, 100).getRGB());
//		image.roundEdges((float) roundPercentage, (float) shrinkPercentage);
		
		
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
	}
	
}
