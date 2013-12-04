package matt.myProcessing;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import matt.ui.PAppletPlus;
import matt.ui.PToolbox;
import matt.util.Square;

import org.junit.Test;

public class ToolboxTest extends PAppletPlus {
	
	private static final long serialVersionUID = 6154284150816778778L;
	
	@Test
	public void test() {
		assertTrue(new Color(150f/255f, 100f/255f, 50f/255f, 200f/255f).getRGB() == color(150, 100, 50, 200));
		PToolbox.convertRGBtoJavaHSL(102, 224, 255);
		PToolbox.convertRGBtoJavaHSL(0, 13, 69);
	}
	
	@Test
	public void test2() {
		Rectangle2D.Float r1 = new Rectangle2D.Float(0, 0, 100, 200);
		Square r2 = Square.getInnerSquare(r1, 30);
		System.out.println(r2.getMinX());
		assertTrue(r2.getMinX() == 30 && r2.getMinY() == 80 && r2.getSize() == 40);
		
		r1 = new Rectangle2D.Float(0, 0, 100, 100);
		r2 = Square.getInnerSquare(r1, 0);
		assertTrue(r2.getMinX() == 0 && r2.getMinY() == 0 && r2.getSize() == 100);
		
		r1 = new Rectangle2D.Float(-10, -10, 80, 80);
		r2 = Square.getInnerSquare(r1, -10);
		assertTrue(r2.getMinX() == -20 && r2.getMinY() == -20 && r2.getSize() == 100);
	}

}
