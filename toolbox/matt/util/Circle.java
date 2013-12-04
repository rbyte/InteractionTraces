package matt.util;

import java.awt.geom.Point2D;

import org.contract4j5.contract.*;
import org.jbox2d.common.Vec2;
import static org.junit.Assert.*;

@Contract
public class Circle {
	
	private PolarPoint centre;
	@Invar("$this.radius > 0")
	private float radius = 1;
	
	public Circle(Point2D.Float centre, float radius) {
		this(new PolarPoint(centre.x, centre.y, "xy"), radius);
	}
	
	public Circle(double thetaDegrees, double r, float radius) {
		this(new PolarPoint(thetaDegrees, "�", r), radius);
	}
	
	public Circle(Vec2 centre, float radius) {
		this(new PolarPoint(centre.x, centre.y, "xy"), radius);
	}
	
	public Circle(PolarPoint centre, float radius) {
		setCentre(centre);
		setRadius(radius);
	}
	
	public Point2D.Float getCentreAsPoint2DFloat() {
		return centre.asFloatPoint2D();
	}
	
	public PolarPoint getCentre() {
		return centre;
	}
	
	public Vec2 getCentreVec2() {
		return new Vec2((float) centre.getX(), (float) centre.getY());
	}
	
	public Circle setCentre(Point2D.Float centre) {
		return setCentre(new PolarPoint(centre));
	}

	public Circle setCentre(PolarPoint centre) {
		this.centre = centre;
		return this;
	}
	
	public float getCenterX() {
		return (float) getCentre().getX();
	}
	
	public Point2D.Float getBoundingBoxLowCorner() {
		return new Point2D.Float(getCenterX()-getRadius(), getCenterY()-getRadius());
	}
	
	public float getCenterY() {
		return (float) getCentre().getY();
	}
	
	public float getRadius() {
		return radius;
	}
	
	public Circle setRadius(float radius) {
		assertTrue("radius: "+radius+" <= 0", radius > 0);
		this.radius = radius;
		return this;
	}
	
	public Circle addToRadius(float radius) {
		return setRadius(this.radius+radius);
	}
	
	public Circle multRadius(float mult) {
		return setRadius(radius*mult);
	}
	
	public float getArea() {
		return 2*(float) Math.PI*radius*radius;
	}
	
	public Circle clone() {
		return new Circle(getCentre(), getRadius());
	}
	
	public boolean isInside(Point2D.Float p) {
		return isInside(new PolarPoint(p.x, p.y, "xy"));
	}
	
	public boolean isInside(PolarPoint p) {
		return radius >= centre.clone().subtract(p).getR();
	}
	
	public boolean intersects(Circle c) {
		return centre.distanceTo(c.getCentre()) <= radius+c.getRadius();
	}
	
	@Pre("minimumDistanceBetweenCircleBorders >= 0")
	@Post("$this.intersects(c) == false")
	/**
	 * 
	 * @param c Circle to repel this from.
	 * @param minimumDistanceBetweenCircleBorders
	 * @return whether this needed to be repelled due to proximity
	 */
	public boolean repelFrom(Circle c, double minimumDistanceBetweenCircleBorders) {
		double distanceBetween = centre.distanceTo(c.getCentre());
		double minDistanceBetween = radius+c.getRadius()+minimumDistanceBetweenCircleBorders;
		boolean areTooClose = distanceBetween < minDistanceBetween;
		if (areTooClose) {
			double distanceToAdd = minDistanceBetween-distanceBetween;
			if (distanceBetween > 0) {
				double factor = distanceToAdd / distanceBetween;
				centre.add(centre.clone().subtract(c.getCentre()).multiplyR(factor));
			} else {
				centre.add(new PolarPoint(0, distanceToAdd));
			}
		}
		return areTooClose;
	}
	
	public PolarPoint getTangentPoint(Circle p, double multiplier) {
		assertTrue( centre.distanceTo(p.getCentre()) > radius+p.getRadius());
		assertTrue( -1 <= multiplier && multiplier <= 1);
		double angle = centre.angleOfLineBetween(p.getCentre());
		return centre.clone().add(new PolarPoint(
				angle + multiplier*tangentAngle(p),
				radius)
			);
	}
	
	// getAngleBetweenMidpointCombiningLinesAndTangent
	public double tangentAngle(Circle p) {
		return Math.acos( (p.getRadius()+radius) / centre.distanceTo(p.getCentre()));
	}
	
	public String toString() {
		return centre + ", r: " + radius;
	}

}
