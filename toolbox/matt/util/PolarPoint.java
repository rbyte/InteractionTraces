package matt.util;

import java.awt.geom.Point2D;

import matt.meta.AuthorInformation;

import org.contract4j5.contract.*;
import static org.junit.Assert.*;

/**
 * 2-dimensional point in a polar coordinate system.
 */
@AuthorInformation
@Contract
public class PolarPoint {
	
	private static final double MAX_RADIANTS = 2*Math.PI;
	private static final double MAX_DEGREES = 360;
	private static final String NUMBER_FORMAT = "%.1f";
	// without rounding
//	private static final String NUMBER_FORMAT = "%f";
	
	@Invar("0 <= $this.theta && $this.theta < $this.MAX_RADIANTS")
	private double theta;
	@Invar("$this.r >= 0")
	private double r;
	
	/**
	 * @param theta Values <0� or >=Pi*2 are allowed but converted. E.g. -1 == Pi*2-1 and Pi*3 == Pi.
	 * @param r If r is negative, theta is mirrored to the origin.
	 */
	public PolarPoint(double theta, double r) {
		setTheta(theta);
		setR(r);
	}
	
	/**
	 * @param thetaInDegrees Values <0� or >=360� are allowed but converted. E.g. -30� == 330� and 400� == 30�.
	 * @param degreesIndicator is recommended to be "�" to indicate to the programmer that theta is given in degrees.
	 * 	Programmatically it is ignored and can therefore be any value, even null.
	 * @param r If r is negative, theta is mirrored to the origin.
	 */
	public PolarPoint(double thetaInDegrees, String degreesIndicator, double r) {
		setThetaDegrees(thetaInDegrees);
		setR(r);
	}
	
	/**
	 * Create a new PolarPoint by converting the given Cartesian coordinates into polar coordinates.
	 * @param x
	 * @param y
	 * @param xyIndicator is recommended to be "xy" to indicate to the programmer that Cartesian coordinates are supplied.
	 * 	Programmatically it is ignored and can therefore be any value, even null.
	 */
	public PolarPoint(double x, double y, String xyIndicator) {
		setXY(x, y);
	}
	
	public PolarPoint(Point2D.Double p) {
		setXY(p.x, p.y);
	}
	
	public PolarPoint(Point2D.Float p) {
		setXY(p.x, p.y);
	}
	
	public double getTheta() {
		return theta;
	}
	
	@Post("0 <= $return && $return < $this.MAX_DEGREES")
	public double getThetaDegrees() {
		return radiantsToDegrees(theta);
	}
	
	public PolarPoint setThetaDegrees(double theta) {
		return setTheta(degreesToRadiants(theta % MAX_DEGREES));
	}
	
	@Pre("PolarPoint.isProperDouble(theta)")
	public PolarPoint setTheta(double theta) {
		theta = theta % MAX_RADIANTS;
		// actually, == should not be possible, but yet is:
		try {
			assertTrue(-MAX_RADIANTS <= theta && theta <= MAX_RADIANTS);
		} catch (AssertionError e) {
			System.out.println(theta);
			throw e;
		}
		
		if (theta < 0) theta = MAX_RADIANTS + theta;
		if (theta == MAX_RADIANTS) theta = 0;
		assertTrue( 0 <= theta && theta < MAX_RADIANTS);
		this.theta = theta;
		return this;
	}

	public double getR() {
		return r;
	}
	
	@Pre("PolarPoint.isProperDouble(r)")
	public PolarPoint setR(double r) {
		this.r = Math.abs(r);
		if (r < 0) mirrorToOrigin();
		return this;
	}
	
	public PolarPoint multiplyR(double factor) {
		return setR(r*factor);
	}
	
	public double getX() {
		return r*Math.cos(theta);
	}
	
	public double getY() {
		return r*Math.sin(theta);
	}
	
	public Point2D.Double asDoublePoint2D() {
		return new Point2D.Double(getX(), getY());
	}
	
	public Point2D.Float asFloatPoint2D() {
		return new Point2D.Float((float) getX(), (float) getY());
	}
	
	public PolarPoint clone() {
		return new PolarPoint(theta, r);
	}
	
	public PolarPoint setX(double x) {
		return setXY(x, getY());
	}
	
	public PolarPoint setXadd(double x) {
		return setXY(getX()+x, getY());
	}
	
	public PolarPoint setY(double y) {
		return setXY(getX(), y);
	}
	
	public PolarPoint setYadd(double y) {
		return setXY(getX(), getY()+y);
	}
	
	public PolarPoint setXYadd(double x, double y) {
		return setXY(getX()+x, getY()+y);
	}
	
	public PolarPoint setXY(double x, double y) {
		// http://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
		if (Double.isNaN(x)) x = 0;
		if (Double.isNaN(y)) y = 0;
		double theta = Math.atan2(y, x);
		if (Double.isInfinite(theta))
			theta = 0;
		if (theta < 0) theta += Math.PI*2;
		assertTrue("t:"+theta, 0 <= theta && theta < MAX_RADIANTS);
		return set(theta, Math.sqrt(y*y+x*x));
	}
	
	public PolarPoint set(final PolarPoint p) {
		return set(p.getTheta(), p.getR());
	}
	
	public PolarPoint set(double theta, double r) {
		setTheta(theta);
		setR(r);
		return this;
	}
	
	public double distanceTo(final PolarPoint p) {
		return Math.sqrt( r*r + p.getR()*p.getR() - 2*r*p.getR() * Math.cos(angleDelta(p)) );
	}
	
	/**
	 * @param p subtracted from this.
	 * @return this, modified, as if p was the new origin of this (line from p to this).
	 */
	public PolarPoint subtract(final PolarPoint p) {
		return add(p.clone().mirrorToOrigin());
	}
	
	public double angleOfLineBetween(final PolarPoint p) {
		return p.clone().subtract(this).getTheta();
	}
	
	public PolarPoint repelFrom(PolarPoint p, double repelFactor) {
		return add(clone().subtract(p).multiplyR(repelFactor));
	}
	
	public PolarPoint mirrorToOrigin() {
		return setTheta(theta < MAX_RADIANTS/2 ? theta+MAX_RADIANTS/2 : theta-MAX_RADIANTS/2);
	}
	
	public PolarPoint add(final PolarPoint p) {
		try {
			// be sure before you change anything here.
			if (p.getR() == 0)
				return this;
			if (r == 0)
				return set(p);
			if (theta == p.getTheta())
				return setR(r+p.getR());
			if (angleDelta(p) == Math.PI)
				return set(	r > p.getR() ? theta : p.getTheta(),
							r > p.getR() ? r-p.getR() : p.getR()-r);
			double dist = distanceTo(p);
			// http://de.wikipedia.org/wiki/Parallelogrammgleichung
			// length of diagonal of the parallelogram that both points span
			double d = Math.sqrt( 2*(r*r + p.getR()*p.getR()) - (dist*dist));
			assertTrue( PolarPoint.isProperDouble(d, r, p.getR(), dist));
			assertTrue( d != 0 && r != 0 && p.getR() != 0 && dist != 0);
			double acosArg = (d*d + r*r - p.getR()*p.getR()) / (2*d*r);
			// adjust for rounding errors
			if (acosArg < -1) acosArg = -1;
			if (acosArg > 1) acosArg = 1;
			// angle between the diagonal and this
			double angle = Math.acos( acosArg );
			assertTrue( 0 <= angle && angle <= Math.PI);
			setR(d);
			setTheta(angleCounterClockwise(p) < Math.PI ? theta + angle : theta - angle);
		} catch (AssertionError e) {
			System.out.println("Error in add(): "+this+" add( "+p+" )");
			throw e;
		}
		return this;
	}
	
	public boolean equals(final PolarPoint p) {
		return r == p.getR() && r == 0 ? true : theta == p.getTheta();
	}
	
	public double angleDelta(final PolarPoint p) {
		return Math.abs(theta-p.getTheta());
	}
	
	@Post("0 <= $return && $return < $this.MAX_RADIANTS")
	public double angleCounterClockwise(final PolarPoint p) {
		return p.getTheta()-theta+ (theta > p.getTheta() ? MAX_RADIANTS : 0);
	}
	
	public String toString() {
		return "("	+ String.format(NUMBER_FORMAT, radiantsToDegrees(theta))	+ "�, "
					+ String.format(NUMBER_FORMAT, getR())						+ ") = ("
					+ String.format(NUMBER_FORMAT, getX())						+ ", "
					+ String.format(NUMBER_FORMAT, getY())						+ ")";
	}
	
	public static boolean isProperDouble(double... ds) {
		for (double d : ds) {
			if (Double.isNaN(d) || Double.isInfinite(d)) {
				return false;
			}
		}
		return true;
	}
	
	public static double degreesToRadiants(double degrees) {
		return degrees/MAX_DEGREES*MAX_RADIANTS;
	}
	
	public static double radiantsToDegrees(double radiants) {
		return radiants/MAX_RADIANTS*MAX_DEGREES;
	}
	
}
