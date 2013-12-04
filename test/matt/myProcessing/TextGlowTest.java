package matt.myProcessing;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;

import processing.core.PFont;
import processing.core.PShape;
import matt.parameters.Params;
import matt.setup.SVGconverter;
import matt.ui.PAppletPlus;

public class TextGlowTest extends PAppletPlus {
	
	private static final long serialVersionUID = 8344928628825975531L;
	PFont font = createFont("Arial", 32);
	PShape pshape;
	
	public void setupPlus() {
		try {
			pshape = loadShape(SVGconverter.run("Fiction", Params.visualS.kTextFamily()));
//			pshape = loadShape("/media/archiv/2012/MattsProjectCalgary2011_12/textShapes/Linux Libertine/raw_inkOut_World.svg");
			
			pshape.disableStyle();
		} catch (IOException e) {
			e.printStackTrace();
		}
		size(800, 500);
		smooth();
	}

	public void drawPlus() {
		background(125);
		g.fill(255);
		g.noStroke();
		g.shape(pshape, 70, 65);
		filter(BLUR, 5);
		translate(-2, -2);
		g.fill(0);
		g.shape(pshape, 70, 65);
	}
	
	public void drawPlus2() {
		background(255);
		shape(pshape, new Color(0,0,0), 100, new Point2D.Float(50, 50), true, new Color(255,0,0), 2, 2);
		shape(pshape, new Color(0,0,0), 100, new Point2D.Float(50, 150));
	}

}
