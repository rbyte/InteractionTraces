package matt.myProcessing;

import java.awt.geom.Rectangle2D;

import matt.ui.PAppletPlus;
import matt.ui.screenElement.Schweif;

public class RectangleProximityForSchweifTest extends PAppletPlus {
	
	private static final long serialVersionUID = 8127112628086564545L;
	Schweif schweif = new Schweif();
	Rectangle2D.Float rect = new Rectangle2D.Float(350, 350, 100, 100);
	Rectangle2D.Float sens = new Rectangle2D.Float(250, 250, 300, 300);
	
	public void drawPlus() {
		background(255);
		fill(200);
		rect(sens);
		fill(0);
		rect(rect);
		float prox = schweif.getSchweifProximityAndUpdate(mousePath, rect, 100);
		text(prox, 300, 100);
	}

}
