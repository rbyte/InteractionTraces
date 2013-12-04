package matt.java;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;

import org.junit.Test;

public class RectIntersection {

	@Test
	public void test() {
		Rectangle2D.Double r1 = new Rectangle2D.Double(0, 0, 10, 10);
		Rectangle2D.Double r2 = new Rectangle2D.Double(5, 5, 10, 10);
		Rectangle2D.intersect(r1, r2, r1);
		assertTrue(r1.x == 5);
		assertTrue(r1.y == 5);
		assertTrue(r1.width == 5);
		assertTrue(r1.height == 5);
		
	}
	
}
