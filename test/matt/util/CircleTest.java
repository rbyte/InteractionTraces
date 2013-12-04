package matt.util;

import static org.junit.Assert.*;

import matt.util.Circle;
import matt.util.PolarPoint;

import org.contract4j5.errors.ContractError;
import org.junit.Test;

public class CircleTest {
	
	public static void eqC(Circle c1, Circle c2) {
		PolarPointTest.eqPPoints(c1.getCentre(), c2.getCentre());
		PolarPointTest.eq(c1.getRadius(), c2.getRadius());
	}
	
	@Test
	public void getAndSet() {
		PolarPointTest.eq(new Circle(new PolarPoint(0, 0), 1).getRadius(), 1);
		PolarPointTest.eqPPoints(new Circle(new PolarPoint(0, 0), 1).getCentre(), new PolarPoint(0,0));
	}
	
	@Test(expected=AssertionError.class) public void testE1() { new Circle(1, 1, -1); }
	@Test(expected=AssertionError.class) public void testE2() {
		new Circle(1, 1, 1).getTangentPoint(new Circle(1, 1, 1), 1); }
	
	@Test
	public void test0() {
		Circle c1 = new Circle(new PolarPoint(0, 0), 1);
		Circle c2 = new Circle(new PolarPoint(0, 3), 1);
		
		PolarPointTest.eq(c1.tangentAngle(c2), Math.acos(2d/3d));
		PolarPointTest.eq(c1.getCentre().angleOfLineBetween(c2.getCentre()), 0);
		
		PolarPointTest.eqPPoints(c1.getTangentPoint(c2, 1), new PolarPoint(Math.acos(2d/3d), 1));
	}
	
	@Test
	public void test1() {
		Circle c1 = new Circle(new PolarPoint(0, 0), 1);
		Circle c2 = new Circle(new PolarPoint(1, 3), 1);
		
		PolarPointTest.eq(c1.tangentAngle(c2), Math.acos(2d/3d));
		PolarPointTest.eq(c1.getCentre().angleOfLineBetween(c2.getCentre()), 1);
		
		PolarPointTest.eqPPoints(c1.getTangentPoint(c2, 1), new PolarPoint(Math.acos(2d/3d)+1, 1));
	}
	
	@Test
	public void test2() {
		Circle c1 = new Circle(new PolarPoint(0, 0), 2);
		Circle c2 = new Circle(new PolarPoint(0, 4), 1);
		
		PolarPointTest.eq(c1.tangentAngle(c2), Math.acos(3d/4d));
		PolarPointTest.eq(c1.getCentre().angleOfLineBetween(c2.getCentre()), 0);
		
		PolarPointTest.eqPPoints(c1.getTangentPoint(c2, 1), new PolarPoint(Math.acos(3d/4d), 2));
	}
	
	@Test
	public void test3() {
		Circle c1 = new Circle(new PolarPoint(0, 0), 2);
		Circle c2 = new Circle(new PolarPoint(300, "°", 4), 1);
		
		PolarPointTest.eq(c1.tangentAngle(c2), Math.acos(3d/4d));
		PolarPointTest.eq(c1.getCentre().angleOfLineBetween(c2.getCentre()), PolarPoint.degreesToRadiants(300));
		
		PolarPointTest.eqPPoints(c1.getTangentPoint(c2, 1), new PolarPoint(Math.acos(3d/4d)+PolarPoint.degreesToRadiants(300), 2));
	}
	
	@Test
	public void test4() {
		Circle c1 = new Circle(new PolarPoint(0, 1), 2);
		Circle c2 = new Circle(new PolarPoint(0, "°", 5), 1);
		
		PolarPointTest.eq(c1.tangentAngle(c2), Math.acos(3d/4d));
		PolarPointTest.eq(c1.getCentre().angleOfLineBetween(c2.getCentre()), 0);
		
		PolarPointTest.eqPPoints(c1.getTangentPoint(c2, 1), new PolarPoint(Math.acos(3d/4d), 2).add(new PolarPoint(0, 1)));
	}
	
	@Test
	public void isInside() {
		assertTrue(new Circle(new PolarPoint(0, 1), 2).isInside(new PolarPoint(0, 1)));
		assertTrue(!new Circle(new PolarPoint(0, 1), 2).isInside(new PolarPoint(0, 4)));
		assertTrue(new Circle(new PolarPoint(0, 0), 1).isInside(new PolarPoint(6, 1)));
		assertTrue(new Circle(new PolarPoint(0, 0), 1).isInside(new PolarPoint(6, 0.999)));
		assertTrue(!new Circle(new PolarPoint(0, 0), 1).isInside(new PolarPoint(6, 1.001)));
		assertTrue(!new Circle(new PolarPoint(2, 3), 2).isInside(new PolarPoint(0, 0)));
	}
	
	@Test
	public void intersects() {
		assertTrue(!new Circle(new PolarPoint(0, 0), 2).intersects(new Circle(new PolarPoint(0, 5), 2.9f)));
		assertTrue(new Circle(new PolarPoint(0, 0), 1).intersects(new Circle(new PolarPoint(0, 2), 1)));
		assertTrue(new Circle(new PolarPoint(0, 0), 1).intersects(new Circle(new PolarPoint(0, 2), 7)));	
	}
	
	@Test
	public void repell1() {
		Circle c = new Circle(new PolarPoint(0, 0), 1);
		assertTrue(c.repelFrom(new Circle(new PolarPoint(0, 0), 1), 1));
		CircleTest.eqC(c, new Circle(new PolarPoint(0, 3), 1));
		assertFalse(c.repelFrom(new Circle(new PolarPoint(0, 0), 1), 1));
	}
	
	@Test
	public void repell2() {
		Circle c = new Circle(new PolarPoint(0, 0), 1);
		assertTrue(!c.repelFrom(new Circle(new PolarPoint(0, 3.1), 1), 1));
		CircleTest.eqC(c, new Circle(new PolarPoint(0, 0), 1));
	}
	
	@Test
	public void repell3() {
		Circle c = new Circle(new PolarPoint(0, 0), 1);
		assertTrue(c.repelFrom(new Circle(new PolarPoint(0, 1), 1), 1));
		CircleTest.eqC(c, new Circle(new PolarPoint(0, -2), 1));
	}
	
	@Test
	public void repell4() {
		Circle c = new Circle(new PolarPoint(0, 0), 1);
		assertTrue(c.repelFrom(new Circle(new PolarPoint(0, 2.5), 1), 1));
		CircleTest.eqC(c, new Circle(new PolarPoint(0, -0.5), 1));
	}
	
	@Test(expected = ContractError.class)
	public void repellCrash() {
		new Circle(new PolarPoint(0, 0), 1).repelFrom(new Circle(new PolarPoint(0, 2.1), 1), -1);
	}

}
