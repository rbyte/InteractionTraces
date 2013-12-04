package matt.myProcessing;

import matt.parameters.Params;
import matt.ui.PAppletPlus;
import matt.ui.PImagePlus;

public class TextureGradientTest extends PAppletPlus {
	
	private static final long serialVersionUID = 3478794202638891804L;
//	PImagePlus texture = loadImage(Params.pathToTextures+"2979967880_d779f83253_mask_400_400.png");
	PImagePlus texture = loadImage(Params.pathToTextures+"2979967880_d779f83253_o_gramma3_mask_300x300.png");
	
	boolean automaticRun = true;
	float drawCyclesMax = 200;
	
	public void drawPlus() {
		if (drawCycles == 0 && automaticRun)
			screenVideoCapture.switchOnOff();
		
		float percentage = automaticRun ? 1-(drawCycles/drawCyclesMax % 1) : mouseX/(float) width;
		
		background(255);
		// readjusts for darkening of texture
		fill(200-percentage*30);
		noStroke();
		
		float insetX = 20;
		
		ellipse(texture.width/2+insetX, texture.height/2+insetX, Math.min(texture.width, texture.height), Math.min(texture.width, texture.height));
		
		if (drawCycles == drawCyclesMax && automaticRun)
			screenVideoCapture.switchOnOff();
		image(
			texture
				.clonePlus()
				.blur(0.5f*percentage)
				.setLowKeyTo(percentage)
				.roundEdges(1)
			, insetX, insetX);
		
		fill(0);
		float range = texture.width;
		float y = percentage*range;
		triangle(range+insetX, y+insetX, range+20+insetX, -20+y+insetX, range+20+insetX, 20+y+insetX);
		textSize(18);
		text(1-percentage, range+25+insetX, y+7+insetX);
		
	}

}
