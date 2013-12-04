package matt.util;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jbox2d.common.Vec2;
import org.junit.Test;

public class SquareTest {
	
	@Test
	public void test() {
		Square s1 = new Square(new Vec2(2, 3), 2);
		Vec2 p = new Vec2(2.5f, 3.5f);
		
		Square s2 = new Square(new Vec2(1, -1), 1f);
		assertTrue(s2.translatePointFrom(s1, p).equals(new Vec2(1.25f, -0.75f)));
		

		Point2D.Float[] points = new Point2D.Float[3];
		points[0]= new Point2D.Float(0, 0);
		points[1] = new Point2D.Float(1, 1);
		points[2] = new Point2D.Float(0.5f, 0.5f);
		
		Square boundingRect = Square.getBoundingSquare(points);
		assertTrue(boundingRect.equals(new Square(0.5f, 0.5f, 1)));
		
		assertTrue(Square.getBoundingSquare(new Vec2[] {new Vec2(0, 0), new Vec2(10, 6)})
				.equals(new Square(5, 3, 10)));
		
		assertTrue(boundingRect.contains(points[0]));
		assertTrue(boundingRect.contains(points[1]));
		assertTrue(boundingRect.contains(points[2]));
		assertTrue(!boundingRect.contains(new Point2D.Float(-0.001f, 0.5f)));
		assertTrue(!boundingRect.contains(new Point2D.Float(0.5f, -0.001f)));
		assertTrue(!boundingRect.contains(new Point2D.Float(1.001f, 0.5f)));
		assertTrue(!boundingRect.contains(new Point2D.Float(0.5f, 1.001f)));
		
		assertTrue(new Square(new Vec2(2, 3), 2).getInnerCircleArea() == Math.PI);
		
		assertTrue(new Rectangle2D.Float(0, 0, 4, 4).contains(new Rectangle2D.Float(0, 0, 4, 4)));
		
		
	}
	
	@Test
	public void scalePointsIntoThisSquare() {
		Vec2[] points = new Vec2[] {new Vec2(0.2f,0.2f), new Vec2(0.8f,0.8f)};
		Square origin = new Square(0.5f, 0.5f, 1);
		Square target = new Square(1.5f, 0.5f, 1);
		
		assertTrue(Square.getBoundingSquare(points).equals(
				new Square(0.5f, 0.5f, 0.6f)));
		
		assertArrayEqualsVec2(target.scalePointsIntoThisSquare(points, origin, false),
				new Vec2[] {new Vec2(1.2f,0.2f), new Vec2(1.8f,0.8f)});
		
		assertArrayEqualsVec2(target.scalePointsIntoThisSquare(points, origin, true),
				new Vec2[] {new Vec2(1f,0f), new Vec2(2f,1f)});
		
		assertArrayEqualsVec2(target.scalePointsIntoThisSquare(new Vec2[] {new Vec2(0.2f,0.2f)}, origin, true),
				new Vec2[] {new Vec2(1.2f,0.2f)});
		
		assertArrayEqualsVec2(target.scalePointsIntoThisSquare(new Vec2[] {new Vec2(0.2f,0.2f)}, origin, false),
				new Vec2[] {new Vec2(1.2f,0.2f)});
		
		assertArrayEqualsVec2(target.scalePointsIntoThisSquare(new Vec2[] {}, origin, false),
				new Vec2[] {});
		
		assertArrayEqualsVec2(target.scalePointsIntoThisSquare(new Vec2[] {}, origin, true),
				new Vec2[] {});
	}
	
	private static void assertArrayEqualsVec2(Vec2[] actual, Vec2[] expected) {
		assertTrue(actual.length == expected.length);
		for (int i=0; i<actual.length; i++) {
			assertEquals(actual[i].x, expected[i].x, 0.00001);
			assertEquals(actual[i].y, expected[i].y, 0.00001);
		}
	}

}
