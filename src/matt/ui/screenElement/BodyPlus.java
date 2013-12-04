package matt.ui.screenElement;

import java.awt.geom.Point2D;

import matt.ui.WorldPlus;
import matt.util.Circle;
import matt.util.Square;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.FixtureDef;
import static org.junit.Assert.*;

public class BodyPlus {

	private WorldPlus worldPlus;
	private Body b;
	
	public BodyPlus(Body b, WorldPlus worldPlus) {
		this.b = b;
		this.worldPlus = worldPlus;
	}
	
	private Square worldSpace() {
		return worldPlus.space();
	}
	
	public Vec2 getPositionOfLinearVelocityTargetIn(Square reference) {
		return positionTo(reference, b.getPosition().add(b.getLinearVelocity()));
	}
	
	public Circle getCircleIn(Square reference) {
		return new Circle(getPositionIn(reference), getRadiusIn(reference));
	}
	
	public Point2D.Float getPositionIn(Square reference) {
		Vec2 p = getPositionAsVec2In(reference);
		return new Point2D.Float(p.x, p.y);
	}
	
	public Vec2 getPositionAsVec2In(Square reference) {
		return positionTo(reference, b.getPosition());
	}

	public float getRadiusIn(Square reference) {
		return scalingTo(reference, getRadius());
	}
	
	public void setCircle(Square reference, Circle c) {
		setPosition(reference, c.getCentreAsPoint2DFloat());
		setRadius(reference, c.getRadius());
	}
	
	public void setPosition(Square reference, Point2D.Float position) {
		setPosition(reference, position.x, position.y);
	}
	
	public void setPosition(Square reference, float x, float y) {
		b.setTransform(positionFrom(reference, new Vec2(x, y)), 0);
	}
	
	public void setRadiusAdd(Square reference, float newRadius) {
		setRadius(reference, getRadiusIn(reference) + newRadius);
	}
	
	public void setRadius(Square reference, float newRadius) {
		assertTrue(newRadius > 0);
		assertTrue(reference.getSize() > newRadius);
		Vec2 linearVelocity = b.getLinearVelocity();
		Shape shape = b.getFixtureList().getShape();
		shape.m_radius = scalingFrom(reference, newRadius);
		b.destroyFixture(b.getFixtureList());
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1.0f;
		b.createFixture(fd);
		b.setLinearVelocity(linearVelocity);
	}
	
	public boolean isActive() {
		return b.isActive();
	}
	
	public void setActive(boolean flag) {
		b.setActive(flag);
	}
	
	private float getRadius() {
		return b.getFixtureList().getShape().m_radius;
	}
	
	private Vec2 positionTo(Square target, Vec2 p) {
		return target.translatePointFrom(worldSpace(), p);
	}
	
	private Vec2 positionFrom(Square source, Vec2 p) {
		return worldSpace().translatePointFrom(source, p);
	}
	
	private float scalingTo(Square target, float x) {
		return target.translateLengthFrom(worldSpace(), x);
	}

	private float scalingFrom(Square source, float x) {
		return worldSpace().translateLengthFrom(source, x);
	}
	
	@SuppressWarnings("unused")
	private Vec2 scalingTo(Square target, Vec2 x) {
		return target.translateLengthFrom(worldSpace(), x);
	}

	private Vec2 scalingFrom(Square source, Vec2 x) {
		return worldSpace().translateLengthFrom(source, x);
	}
	
	public void setLinearVelocityAdd(Square reference, Vec2 velocityToBeAdded) {
		b.setLinearVelocity(b.getLinearVelocity().add(scalingFrom(reference, velocityToBeAdded)));
	}

	public void setLinearVelocity(Square reference, Vec2 velocityToBeAdded) {
		b.setLinearVelocity(scalingFrom(reference, velocityToBeAdded));
	}

}
