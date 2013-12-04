package matt.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static org.junit.Assert.*;
import org.jbox2d.common.Vec2;

public class Square {
	
	private Vec2 center;
	// seitenlaenge
	private float size;
	
//	private Square(float topLeftX, float topLeftY, float size) {
//		this(new Vec2(topLeftX+size/2, topLeftY+size/2), size);
//	}
	
//	public Square(double centerX, double centerY, float size) {
//		this(new Vec2((float) centerX, (float) centerY), size);
//	}
	
	public Square(float centerX, float centerY, float size) {
		this(new Vec2(centerX, centerY), size);
	}
	
	public Square(Vec2 center, float size) {
		assertTrue( size > 0);
		this.center = center;
		this.size = size;
	}
	
	public Vec2 getCenter() { return center; }
	public float getSize() { return size; }
	public float getMaxX() { return center.x+size/2; }
	public float getMinX() { return center.x-size/2; }
	public float getMaxY() { return center.y+size/2; }
	public float getMinY() { return center.y-size/2; }
	public float getCenterX() { return center.x; }
	public float getCenterY() { return center.y; }
	
//	public double area() {
//		return Math.PI * size * size;
//	}
	
	public double innerCircleArea() {
		return Math.PI * size/2 * size/2;
	}
	
	public float getArea() {
		return size*size;
	}	
	
	public Square clone() {
		return new Square(center, size);
	}
	
	public Square multSizeAroundStaticCenter(float x) {
		assertTrue( x > 0);
		size *= x;
		return this;
	}
	
	public boolean contains(Vec2 p) {
		return contains(new Point2D.Float(p.x, p.y));
	}
	
	private static final float rundungsScheisse = 0.00001f;
	
	public boolean contains(Point2D.Float p) {
		return getMinX()-rundungsScheisse <= p.x && p.x <= getMaxX()+rundungsScheisse
				&& getMinY()-rundungsScheisse <= p.y && p.y <= getMaxY()+rundungsScheisse;
	}
	
	public Rectangle2D.Float asRect() {
		return new Rectangle2D.Float(getMinX(), getMinY(), size, size);
	}
	
	public Vec2 translatePointFrom(final Square reference, final Point2D.Float p) {
		return translatePointFrom(reference, new Vec2(p.x, p.y));
	}
	
	/**
	 * translates p from the reference square into this square
	 */
	public Vec2 translatePointFrom(final Square reference, final Vec2 p) {
		if(!reference.contains(p)) {
			System.err.println("Warning: Square.translatePointFrom: refences does not contain given Point!");
			System.err.println(reference+" ... p: "+p.x+", "+p.y);
		}
		Vec2 result = p.sub(reference.getCenter()).mulLocal(size/reference.getSize()).addLocal(center);
//		assertTrue( contains(result));
		return result;
	}
	
	/**
	 * translates p from the reference square into this square
	 */
	public float translateLengthFrom(final Square reference, final float p) {
		return p / reference.size * size;
	}
	
	public Vec2 translateLengthFrom(final Square reference, final Vec2 p) {
		return p.mul(size / reference.size);
	}
	
	public boolean equals(Square s) {
		return center.x == s.getCenterX() && center.y == s.getCenterY() && size == s.getSize();
	}
	
	public String toString() {
		return "center: ("+center.x+", "+center.y+") @ size: "+size;
	}
	
	public double getInnerCircleArea() {
		return Math.PI*size/2*size/2;
	}
	
	public static Square getInnerSquare(Rectangle2D.Float frame, float padding) {
		float minDim = Math.min(frame.width, frame.height);
		float squareDim = minDim-2*padding;
		if (squareDim < 0) squareDim = 0;
		return new Square(new Vec2((float) frame.getCenterX(), (float) frame.getCenterY()), squareDim);
	}
	
	public Vec2[] scalePointsIntoThisSquare(final Vec2[] points, Square origin, boolean fullyUseTargetSquare) {
		return scalePointsIntoThisSquare(points, origin, fullyUseTargetSquare, 0);
	}
	
	public Vec2[] scalePointsIntoThisSquare(final Vec2[] points, Square origin,
			boolean fullyUseTargetSquare, float borderPercentage) {
		assertTrue(-1 <= borderPercentage && borderPercentage <= 1);
		
		Vec2[] result = new Vec2[points.length];		
		if (points.length == 0)
			return result;
		Square boundingSquare;
		try {
			boundingSquare = fullyUseTargetSquare ? getBoundingSquare(points) : origin.clone();
		} catch (AssertionError e) {
			boundingSquare = origin.clone();
		}
		
		if (borderPercentage != 0)
			boundingSquare.multSizeAroundStaticCenter(1+borderPercentage);
		
		for (int i=0; i<points.length; i++) {
			result[i] = translatePointFrom(boundingSquare, points[i]);
		}
		
		return result;
	}
	
	public static Square getBoundingSquare(Point2D.Float[] points) {
		return getBoundingSquare(convert(points));
	}
	
	public static Vec2[] convert(Point2D.Float[] points) {
		Vec2[] v = new Vec2[points.length];
		for (int i=0; i<points.length; i++)
			v[i] = new Vec2(points[i].x, points[i].y);
		return v;
	}
	
	public static Point2D.Float[] convert(Vec2[] points) {
		Point2D.Float[] v = new Point2D.Float[points.length];
		for (int i=0; i<points.length; i++)
			v[i] = new Point2D.Float(points[i].x, points[i].y);
		return v;
	}
	
	public static Square getBoundingSquare(Rectangle2D.Float rect) {
		return new Square(
			(float) rect.getCenterX(),
			(float) rect.getCenterY(),
			Math.max(rect.width, rect.width));
	}
	
	// for some reason, this contains errors
//	public static Square getBoundingSquare(Vec2[] points) {
//		return getBoundingSquare(getBoundingRect(points));
//	}
	
	// TODO see above: simplify
	public static Square getBoundingSquare(Vec2[] points) {
		assertTrue("Cannot infer bounding box of only 1 or 0 points.", points.length >= 2);
		boolean allPointsOverlap = true;
		for (int i = 1; i<points.length; i++)
			allPointsOverlap &= points[0].x == points[i].x && points[0].y == points[i].y;
		assertTrue("All points are in the same position. Cannot calculate bounding box.", !allPointsOverlap);
		
		float x_h = Float.NEGATIVE_INFINITY;
		float x_l = Float.POSITIVE_INFINITY;
		float y_h = Float.NEGATIVE_INFINITY;
		float y_l = Float.POSITIVE_INFINITY;
		
		for (Vec2 p : points) {
			x_h = p.x > x_h ? p.x : x_h;
			x_l = p.x < x_l ? p.x : x_l;
			y_h = p.y > y_h ? p.y : y_h;
			y_l = p.y < y_l ? p.y : y_l;
		}
		assertTrue(x_h > x_l);
		assertTrue(y_h > y_l);
		
		float width = x_h-x_l;
		float height = y_h-y_l;
		
		return new Square(x_l+width/2, y_l+height/2, Math.max(width, height));
	}
	
	public static Rectangle2D.Float getBoundingRect(PolarPoint ... points) {
		return getBoundingRect(convert(points));
	}
	
	public static Vec2[] convert(PolarPoint ... points) {
		Vec2[] v = new Vec2[points.length];
		for (int i=0; i<points.length; i++)
			v[i] = new Vec2((float) points[i].getX(), (float) points[i].getY());
		return v;
	}
	
	public static Rectangle2D.Float getBoundingRect(Vec2[] points) {
		assertTrue("Cannot infer bounding box of only 1 or 0 points.", points.length >= 2);
		boolean allPointsOverlap = true;
		for (int i = 1; i<points.length; i++)
			allPointsOverlap &= points[0].x == points[i].x && points[0].y == points[i].y;
		assertTrue("All points are in the same position. Cannot calculate bounding box.", !allPointsOverlap);
		
		float x_h = Float.NEGATIVE_INFINITY;
		float x_l = Float.POSITIVE_INFINITY;
		float y_h = Float.NEGATIVE_INFINITY;
		float y_l = Float.POSITIVE_INFINITY;
		
		for (Vec2 p : points) {
			x_h = p.x > x_h ? p.x : x_h;
			x_l = p.x < x_l ? p.x : x_l;
			y_h = p.y > y_h ? p.y : y_h;
			y_l = p.y < y_l ? p.y : y_l;
		}
		assertTrue(x_h > x_l);
		assertTrue(y_h > y_l);
		
		float width = x_h-x_l;
		float height = y_h-y_l;
		
		return new Rectangle2D.Float(x_l, y_l, width, height);
	}

}
