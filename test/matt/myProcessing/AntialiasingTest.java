package matt.myProcessing;

import matt.ui.PAppletPlus;
import matt.ui.PImagePlus;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class AntialiasingTest extends PAppletPlus {
	private static final long serialVersionUID = 7980529412411211427L;
	
	private PImage text = getTextSmooth(20);
	
	public void setupPlus() {
		textFont(createFont("Arial", 40, true));
	}
	
	public void drawPlus() {
		background(255);
		smooth();
		fill(0);
		noStroke();
		
		
		ellipse(10.5f	, 10,		1.5f,	1.5f);
		ellipse(10f		, 20.5f,	1.5f,	1.5f);
		ellipse(10.5f	, 30.5f,	1.5f,	1.5f);
		ellipse(10f		, 40,		1.5f,	1.5f);
		
		
		ellipse(10.5f	, 50,		2f,		2f);
		ellipse(10f		, 60.5f,	2f,		2f);
		ellipse(10.5f	, 70.5f,	2f,		2f);
		ellipse(10f		, 80,		2f,		2f);
		
		ellipse(10.5f	, 90,		2.5f,	2.5f);
		ellipse(10f		, 100.5f,	2.5f,	2.5f);
		ellipse(10.5f	, 110.5f,	2.5f,	2.5f);
		ellipse(10f		, 120,		2.5f,	2.5f);
		
		ellipse(10.5f	, 130,		3f,		3f);
		ellipse(10f		, 140.5f,	3f,		3f);
		ellipse(10.5f	, 150.5f,	3f,		3f);
		ellipse(10f		, 160,		3f,		3f);
		
		ellipse(10.5f	, 170,		3.5f,	3.5f);
		ellipse(10f		, 180.5f,	3.5f,	3.5f);
		ellipse(10.5f	, 190.5f,	3.5f,	3.5f);
		ellipse(10f		, 200,		3.5f,	3.5f);
		
		PImagePlus px = loadImage("C:\\Matt\\Project\\Presentation\\blackPixel.png");
		// will result in it being drawn at (0,0) !
		image(px, 0.9f, 0.9f);
		
		System.out.println(drawCycles);
//		textFont(createFont("Arial", 10+drawCycles/10f, true));
		textSize(10+drawCycles/10f);
		text("bla", 300, 100);
		
		textSize(10);
		text("bla", 300, 150);
		
		textSize(10.9f);
		text("bla", 300, 170);
		
		PImage text2;
		try {
			text2 = (PImage) text.clone();
			text2.resize(0, (int) (10+drawCycles/10f));
			image(text2, 300, 200);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
	}
	
	public PImage getTextSmooth(int size) {
		
		PGraphics buf = createGraphics(100, 50, PConstants.JAVA2D);
		buf.beginDraw();
		buf.smooth();
		buf.noStroke();
		buf.background(255);
		buf.fill(0);
		buf.textFont(createFont("Gill Sans MT", 40, true));
		buf.text("bla", buf.width/2f, buf.height/2f);
		
		buf.endDraw();
		
		PImage img = buf.get(0, 0, buf.width, buf.height);
		return img;
	}
	
	
	
	
}
