package matt.myProcessing;

import java.awt.Rectangle;

import matt.parameters.Params;
import matt.ui.PAppletPlus;
import matt.ui.PImagePlus;

public class ImageCroppingTest extends PAppletPlus {
	
	private static final long serialVersionUID = -5519840770787301241L;
	PImagePlus texture = loadImage(Params.pathToProject+"./textures/2979967880_d779f83253_o_gramma3_mask_300x300.png");
	
	public void setupPlus() {
		background(255);
		
		image(texture, 0, 0);
//		image(texture.get(0, 0, 80, 80), 0, 0);
		image(texture.cloneAndCrop(new Rectangle(0, 0, 80, 80)), 0, 0);
		image(texture.cloneAndCrop(new Rectangle(-10, -10, 40, 40)), 0, 0);
		image(texture.cloneAndCrop(new Rectangle(150, 150, 150, 150)), 150, 150);
		image(texture.cloneAndCrop(new Rectangle(250, 250, 150, 150)), 250, 250);
		image(texture.cloneAndCrop(new Rectangle(250, -50, 150, 150)), 250, 0);
		image(texture.cloneAndCrop(new Rectangle(0, 110, 80, 100)), 0, 110);
	}
	
}
