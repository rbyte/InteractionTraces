package matt.util;

import static org.junit.Assert.*;

import matt.util.PolarPoint;

import org.contract4j5.errors.ContractError;
import org.junit.Test;

public class PolarPointTest {
	
	static final double delta = 1E-13;
	
	// assertEqualsDoubles
	public static void eq(double actual, double expected) {
		try {
			assertEquals(null, expected, actual, delta);
		} catch(AssertionError e) {
			System.err.println(actual + " != " + expected);
			throw e;
		}
	}
	
	// assertEqualsPolarPoints
	public static void eqPPoints(PolarPoint actual, PolarPoint expected) {
		try {
			eq(expected.getR(), actual.getR());
			if (expected.getR() != 0) {
				eq(expected.getTheta(), actual.getTheta());
			}
		} catch(AssertionError e) {
			System.err.println(actual + " != " + expected);
			throw e;
		}
	}
	
	@Test
	public void radDeg() {
		eq(PolarPoint.degreesToRadiants(180), Math.PI);
		eq(PolarPoint.degreesToRadiants(360), Math.PI*2);
		eq(PolarPoint.degreesToRadiants(0), 0);
		eq(PolarPoint.degreesToRadiants(-90), -Math.PI/2);
		
		eq(PolarPoint.radiantsToDegrees(Math.PI*3/2), 270);
		eq(PolarPoint.radiantsToDegrees(Math.PI/2), 90);
		eq(PolarPoint.radiantsToDegrees(0), 0);
		eq(PolarPoint.radiantsToDegrees(-Math.PI*4), -720);
	}
	
	@Test
	public void setAndGet() {
		PolarPoint p1 = new PolarPoint(0, "°", 1);
		eq(p1.getR(), 1);
		eq(p1.getThetaDegrees(), 0);
		eq(p1.getX(), 1);
		eq(p1.getY(), 0);
		assertTrue(p1.equals(p1));
		assertTrue(p1.equals(p1.clone()));
		
		eq(p1.clone().getR(), 1);
		eq(p1.clone().getThetaDegrees(), 0);
		eq(p1.clone().getX(), 1);
		eq(p1.clone().getY(), 0);
		
		PolarPoint p2 = new PolarPoint(90, "°", 2);
		assertTrue(p2.equals(p2));
		assertTrue(!p1.equals(p2));
		eq(p2.getR(), 2);
		eq(p2.getThetaDegrees(), 90);
		eq(p2.getX(), 0);
		eq(p2.getY(), 2);
		
		eq(new PolarPoint(Math.PI, 2).getThetaDegrees(), 180);
		eq(new PolarPoint(45, "°", Math.sqrt(2)).getX(), 1);
		
		eqPPoints(new PolarPoint(0, -2), new PolarPoint(180, "°", 2));
		eqPPoints(new PolarPoint(45, "°", -1), new PolarPoint(225, "°", 1));
		eqPPoints(new PolarPoint(45, null, -1), new PolarPoint(225, "°", 1));
		eqPPoints(new PolarPoint(1, 1, "xy"), new PolarPoint(45, "°", Math.sqrt(2)));
		eqPPoints(new PolarPoint(1, 1, null), new PolarPoint(45, "°", Math.sqrt(2)));
		
		eqPPoints(new PolarPoint(1,1).set(new PolarPoint(2,2)), new PolarPoint(2,2));
		eqPPoints(new PolarPoint(1,1).set(2, 2), new PolarPoint(2,2));
		eqPPoints(new PolarPoint(1,1).set(2, 0), new PolarPoint(2,0));
		eqPPoints(new PolarPoint(1,1).setR(-2), new PolarPoint(1,-2));
		eqPPoints(new PolarPoint(1,1).setR(-2), new PolarPoint(1,2).mirrorToOrigin());
		
		eqPPoints(new PolarPoint(0,0).setXY(1, 1), new PolarPoint(45, "°", Math.sqrt(2)));
		eqPPoints(new PolarPoint(0,0).setXY(0, 1), new PolarPoint(90, "°", 1));
		eqPPoints(new PolarPoint(0,0).setXY(1, 0), new PolarPoint(0, "°", 1));
		eqPPoints(new PolarPoint(0,0).setXY(0, 0), new PolarPoint(0, "°", 0));
		eqPPoints(new PolarPoint(0,0).setXY(0, -1), new PolarPoint(270, "°", 1));
		eqPPoints(new PolarPoint(0,0).setXY(-1, 0), new PolarPoint(180, "°", 1));
		eqPPoints(new PolarPoint(0,0).setXY(-1, -1), new PolarPoint(225, "°", Math.sqrt(2)));
		
		eq(new PolarPoint(0,0).setXY(-1, -1).getX(), -1);
		eq(new PolarPoint(0,0).setXY(-1, -1).getY(), -1);
		eq(new PolarPoint(0,0).setXY(-4.567, 1.1234).getX(), -4.567);
		eq(new PolarPoint(0,0).setXY(-4.567, 1.1234).getY(), 1.1234);
		
		eqPPoints(new PolarPoint(0, "°", 1).add(new PolarPoint(90, "°", 2)), new PolarPoint(Math.atan(2), Math.sqrt(5)));
		eq(new PolarPoint(0, "°", 1).add(new PolarPoint(90, "°", 2)).getX(), 1);
		eq(new PolarPoint(0, "°", 1).add(new PolarPoint(90, "°", 2)).getY(), 2);
		
		eq(new PolarPoint(0, 0).getX(), 0);
		eq(new PolarPoint(1, 0).getY(), 0);
		eq(new PolarPoint(45, "°", 1).getX(), Math.sqrt(0.5));
		eq(new PolarPoint(45, "°", 1).getY(), Math.sqrt(0.5));
		
		eqPPoints(new PolarPoint(0, -1), new PolarPoint(Math.PI, 1));
		
		eqPPoints(new PolarPoint(-30, "°", 3), new PolarPoint(330, "°", 3));
		eqPPoints(new PolarPoint(2*Math.PI+1, 1), new PolarPoint(1, 1));
		eqPPoints(new PolarPoint(-1, 1), new PolarPoint(2*Math.PI-1, 1));
		eqPPoints(new PolarPoint(2*Math.PI, 4), new PolarPoint(0, 4));
		eqPPoints(new PolarPoint(390, "°", 2), new PolarPoint(30, "°", 2));
		eqPPoints(new PolarPoint(360, "°", -1), new PolarPoint(180, "°", 1));
		eqPPoints(new PolarPoint(400, "°", -1), new PolarPoint(40, "°", 1).mirrorToOrigin());
		eqPPoints(new PolarPoint(-40, "°", -2), new PolarPoint(320, "°", 2).mirrorToOrigin());
	}
	
	@Test
	public void mirrorToOrigin() {
		eq(new PolarPoint(Math.PI/2*3, 1).mirrorToOrigin().getThetaDegrees(), 90);
		eq(new PolarPoint(40, "°", 1).mirrorToOrigin().getThetaDegrees(), 220);
		eqPPoints(new PolarPoint(Math.PI/2*3, 1).mirrorToOrigin(), new PolarPoint(Math.PI/2, 1));
	}
	
	@Test
	public void multiplyR() {
		eqPPoints(new PolarPoint(45, "°", 1).multiplyR(-1), new PolarPoint(225, "°", 1));
		eqPPoints(new PolarPoint(45, "°", 1).multiplyR(-0.5), new PolarPoint(225, "°", 0.5));
		eqPPoints(new PolarPoint(45, "°", 1).multiplyR(0), new PolarPoint(4, 0));
		eqPPoints(new PolarPoint(45, "°", 1).multiplyR(1), new PolarPoint(45, "°", 1));
		eqPPoints(new PolarPoint(45, "°", 1).multiplyR(2), new PolarPoint(45, "°", 2));
	}

	@Test
	public void distanceTo() {
		eq(new PolarPoint(0, "°", 1).distanceTo(new PolarPoint(90, "°", 2)), Math.sqrt(5));
		eq(new PolarPoint(0, 1).distanceTo(new PolarPoint(0, 1)), 0);
		eq(new PolarPoint(0, 0).distanceTo(new PolarPoint(2, 0)), 0);
		eq(new PolarPoint(0, 0).distanceTo(new PolarPoint(0, 1)), 1);
		eq(new PolarPoint(0, 1).distanceTo(new PolarPoint(0, 3)), 2);
		eq(new PolarPoint(90, "°", 3).distanceTo(new PolarPoint(0, 4)), 5);
		eq(new PolarPoint(90, "°", 3).distanceTo(new PolarPoint(180, "°", 4)), 5);
		eq(new PolarPoint(180, "°", 3).distanceTo(new PolarPoint(90, "°", 4)), 5);
	}
	
	@Test
	public void addAndSubstract() {
		eqPPoints(new PolarPoint(90, "°", 3).add(new PolarPoint(90, "°", 3)), new PolarPoint(90, "°", 6));
		eqPPoints(new PolarPoint(90, "°", 3).add(new PolarPoint(270, "°", 3)), new PolarPoint(0, "°", 0));
		eqPPoints(new PolarPoint(1, 3).add(new PolarPoint(1, 3)), new PolarPoint(1, 3).multiplyR(2));
		eqPPoints(new PolarPoint(90, "°", 1).add(new PolarPoint(0, "°", 1)), new PolarPoint(45, "°", Math.sqrt(2)));
		eqPPoints(new PolarPoint(180, "°", 1).add(new PolarPoint(90, "°", 1)), new PolarPoint(45+90, "°", Math.sqrt(2)));
		
		eqPPoints(new PolarPoint(0, "°", 2).add(new PolarPoint(90, "°", 1)), new PolarPoint(Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(90, "°", 2).add(new PolarPoint(180, "°", 1)), new PolarPoint(Math.PI/2+Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(0, "°", 1).add(new PolarPoint(90, "°", 2)), new PolarPoint(Math.PI/2-Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(90, "°", 1).add(new PolarPoint(180, "°", 2)), new PolarPoint(Math.PI-Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(180, "°", 2).add(new PolarPoint(270, "°", 1)), new PolarPoint(Math.PI+Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(180, "°", 1).add(new PolarPoint(270, "°", 2)), new PolarPoint(Math.PI*3/2-Math.atan(0.5), Math.sqrt(5)));
		
		eqPPoints(new PolarPoint(90, "°", 1).add(new PolarPoint(0, "°", 2)), new PolarPoint(Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(180, "°", 1).add(new PolarPoint(90, "°", 2)), new PolarPoint(Math.PI/2+Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(90, "°", 2).add(new PolarPoint(0, "°", 1)), new PolarPoint(Math.PI/2-Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(180, "°", 2).add(new PolarPoint(90, "°", 1)), new PolarPoint(Math.PI-Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(270, "°", 1).add(new PolarPoint(180, "°", 2)), new PolarPoint(Math.PI+Math.atan(0.5), Math.sqrt(5)));
		eqPPoints(new PolarPoint(270, "°", 2).add(new PolarPoint(180, "°", 1)), new PolarPoint(Math.PI*3/2-Math.atan(0.5), Math.sqrt(5)));

		eqPPoints(new PolarPoint(270, "°", 1).add(new PolarPoint(180, "°", 2)), new PolarPoint(Math.atan(0.5)+Math.PI, Math.sqrt(5)));
		eqPPoints(new PolarPoint(0, 0).add(new PolarPoint(180, "°", 2)), new PolarPoint(180, "°", 2));
		
		eqPPoints(new PolarPoint(1, 3).subtract(new PolarPoint(1, 3)), new PolarPoint(0, 0));
		eqPPoints(new PolarPoint(Math.PI/2, 3).subtract(new PolarPoint(0, 4)), new PolarPoint(Math.atan(4d/3d)+Math.PI/2, 5));
		eqPPoints(new PolarPoint(Math.PI/2, 3).subtract(new PolarPoint(0, 4)).add(new PolarPoint(0, 4)), new PolarPoint(Math.PI/2, 3));
		eqPPoints(new PolarPoint(10, "°", 3).subtract(new PolarPoint(2, 0)), new PolarPoint(10, "°", 3));
		eqPPoints(new PolarPoint(10, "°", 3).subtract(new PolarPoint(10, "°", 6)), new PolarPoint(10, "°", 3).mirrorToOrigin());
		eq(new PolarPoint(1, 1).subtract(new PolarPoint(1, 1)).getR(), 0);
		eq(new PolarPoint(1, 0).subtract(new PolarPoint(1, 0)).getR(), 0);
		eqPPoints(new PolarPoint(1, 0).subtract(new PolarPoint(1, 0)), new PolarPoint(3, 0));
		
		eqPPoints(new PolarPoint(100, 100, "xy").subtract(new PolarPoint(400, 100, "xy")), new PolarPoint(-300, 0, "xy"));
		eqPPoints(new PolarPoint(45, "°", 1).add(new PolarPoint(315, "°", 1)), new PolarPoint(0, Math.sqrt(2)));
		eqPPoints(new PolarPoint(400, 100, "xy").add(new PolarPoint(-100, -100, "xy")), new PolarPoint(300, 0, "xy"));
		eqPPoints(new PolarPoint(400, 100, "xy").subtract(new PolarPoint(100, 100, "xy")), new PolarPoint(300, 0, "xy"));
		
		eqPPoints(new PolarPoint(45, "°", 3).add(new PolarPoint(315, "°", 4)), new PolarPoint(Math.atan(3d/4d)+Math.PI*2d*7d/8d, 5));
	
		// once threw an error because of rounding errors
		new PolarPoint(22.062047656998427, "°", 1424.1968807134933).add(
			new PolarPoint(208.8795904972478, "°", 4.183675628155907E-11));
	}
	
	@Test
	public void angle() {
		eq(new PolarPoint(0, "°", 1).angleDelta(new PolarPoint(90, "°", 2)), Math.PI/2);
		eq(new PolarPoint(270, "°", 3).angleDelta(new PolarPoint(270, "°", 9)), 0);
		eq(new PolarPoint(180, "°", 3).angleDelta(new PolarPoint(90, "°", 3)), Math.PI/2);
		eq(new PolarPoint(90, "°", 3).angleDelta(new PolarPoint(180, "°", 5)), Math.PI/2);
		
		eq(new PolarPoint(88, "°", 0).angleOfLineBetween(new PolarPoint(50, "°", 1)), PolarPoint.degreesToRadiants(50));
		eq(new PolarPoint(0, "°", 1).angleOfLineBetween(new PolarPoint(0, "°", 2)), 0);
		eq(new PolarPoint(0, "°", 2).angleOfLineBetween(new PolarPoint(0, "°", 1)), PolarPoint.degreesToRadiants(180));
		eq(new PolarPoint(40, "°", 2).angleOfLineBetween(new PolarPoint(220, "°", 1)), PolarPoint.degreesToRadiants(220));
		eq(new PolarPoint(220, "°", 2).angleOfLineBetween(new PolarPoint(40, "°", 1)), PolarPoint.degreesToRadiants(40));
	
		eq(new PolarPoint(135, "°", 2).angleOfLineBetween(new PolarPoint(135, "°", 1)), PolarPoint.degreesToRadiants(135+180));
		eq(new PolarPoint(135, "°", 1).angleOfLineBetween(new PolarPoint(135, "°", 2)), PolarPoint.degreesToRadiants(135));
		
		eq(new PolarPoint(90+45, "°", 1).angleOfLineBetween(new PolarPoint(180, "°", Math.sqrt(2))), PolarPoint.degreesToRadiants(180+45));
	}
	
	public void angleCounterClockwiseTest(double a, double b, double c) {
		eq(new PolarPoint(a, "°", 1).angleCounterClockwise(new PolarPoint(b, "°", 2)), PolarPoint.degreesToRadiants(c));
	}
	
	@Test
	public void repelFrom() {
		eqPPoints(new PolarPoint(0, "°", 1).repelFrom(new PolarPoint(0, "°", 2), 1), new PolarPoint(0, "°", 0));
		eqPPoints(new PolarPoint(180, "°", 1).repelFrom(new PolarPoint(0, "°", 2), 0.5), new PolarPoint(180, "°", 2.5));
	}
	
	@Test
	public void angleCounterClockwise() {
		angleCounterClockwiseTest(10, 30, 20);
		angleCounterClockwiseTest(10, 300, 290);
		angleCounterClockwiseTest(10, 5, 360-5);
		angleCounterClockwiseTest(330, 340, 10);
		angleCounterClockwiseTest(330, 10, 40);
		angleCounterClockwiseTest(330, 300, 330);
	}
	
	@Test(expected = ContractError.class)
	public void inproperDoubles1() { new PolarPoint(0,0).setTheta(Double.NaN);}
	@Test(expected = ContractError.class)
	public void inproperDoubles2() { new PolarPoint(0,0).setTheta(Double.NEGATIVE_INFINITY);}
	@Test(expected = ContractError.class)
	public void inproperDoubles3() { new PolarPoint(0,0).setTheta(Double.POSITIVE_INFINITY);}
	
}
