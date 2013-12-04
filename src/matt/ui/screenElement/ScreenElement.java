package matt.ui.screenElement;

import java.awt.geom.Point2D;

import matt.parameters.Params;
import matt.ui.MousePath;
import matt.ui.PAppletPlus;
import matt.util.Circle;
import matt.util.Square;

import org.contract4j5.contract.*;
import org.jbox2d.common.Vec2;

import static org.junit.Assert.*;

@Contract
public class ScreenElement<T extends ScreenElementSet<? extends ScreenElement<T>>> extends Schweif {
	
	private BodyPlus bodyPlus;
	
	private T parent;
	private PAppletPlus p;
	
	private String name;
	
	private boolean queuedToFadeIn = false;
	private Vec2 positionToFadeIn = new Vec2(0, 0);
	private boolean fadeAway = false;
	private long fadeAwayStartTick = 0;
	
	private float radiusToBe = 0;
	private float radiusOld = 0;
	private long radiusToBeStartTick = 0;
	
	protected ScreenElement(T parent, PAppletPlus p, BodyPlus bodyPlus, String name) {
		this.parent = parent;
		this.bodyPlus = bodyPlus;
		radiusToBe = getRadius();
		this.name = name;
		this.p = p;
		setActive(false);
	}

	public T getParent() {
		return parent;
	}
	
	protected PAppletPlus getPPlus() {
		return p;
	}
	
	public String getName()	{
		return name;
	}
	
	public Square getSpace() {
		return parent.space();
	}
	
	public void setPosition(Point2D.Float position) {
		bodyPlus.setPosition(getSpace(), position);
	}
	
	public void setPosition(float x, float y) {
		bodyPlus.setPosition(getSpace(), x, y);
	}
	
	public Circle getCircle() {
		return bodyPlus.getCircleIn(getSpace());
	}
	
	public float getCircleArea() {
		return getCircle().getArea();
	}
	
	public Point2D.Float getPosition() {
		return bodyPlus.getPositionIn(getSpace());
	}
	
	public Vec2 getPositionAsVec2() {
		return bodyPlus.getPositionAsVec2In(getSpace());
	}
	
	public Vec2 getPositionAsVec2In(Square reference) {
		return bodyPlus.getPositionAsVec2In(reference);
	}

	public float getRadius() {
		return bodyPlus.getRadiusIn(getSpace());
	}
	
	public void updateRadius() {
		// this transition is not linear, since the changed radius feeds back to this, slowing down the change
		if (isActive() && radiusToBe != getRadius()) {
			float rChangeDegree = (float) (System.currentTimeMillis()-radiusToBeStartTick) 
				/ (float) Params.visualS.radiusChangeTimeSpanInTicks();
			float newRadius = radiusOld+(radiusToBe - radiusOld)*(rChangeDegree);
			boolean belowMinR = newRadius < Params.visualS.absoluteMinimumCircleRadius();
			if (rChangeDegree >= 1 || belowMinR) {
				setRadius(belowMinR ? Params.visualS.absoluteMinimumCircleRadius() : newRadius);
			} else {
				setRadiusButNotRadiusToBe(newRadius);
			}
		}
	}
	
	public void setRadius(float newRadius) {
		setRadiusButNotRadiusToBe(newRadius);
		setRadiusToBe(getRadius());
	}
	
	private void setRadiusButNotRadiusToBe(float newRadius) {
		bodyPlus.setRadius(getSpace(), upToMinIfLower(newRadius));
	}
	
	private float upToMinIfLower(float radius) {
		return radius < Params.visualS.absoluteMinimumCircleRadius()
			? Params.visualS.absoluteMinimumCircleRadius()
			: radius;
	}
	
	public void setRadiusToBe(float newRadiusToBe) {
		if (newRadiusToBe != radiusToBe) {
			radiusOld = getRadius();
			radiusToBe = upToMinIfLower(newRadiusToBe);
			radiusToBeStartTick = System.currentTimeMillis();
		}
	}
	
	public Vec2 getScreenPositionFromBodyOfItsLinearVelocityTarget() {
		return bodyPlus.getPositionOfLinearVelocityTargetIn(getSpace());
	}
	
	public Vec2 getPositionOfLinearVelocityTarget() {
		return bodyPlus.getPositionOfLinearVelocityTargetIn(getSpace());
	}
	
	public void setActive(boolean flag) {
		bodyPlus.setActive(flag);
		if (!flag) {
			setFadeAway(false);	
		} else {
			radiusToBeStartTick = System.currentTimeMillis();
		}
	}
	
	public boolean isActive() {
		return bodyPlus.isActive();
	}
	
	public void setLinearVelocity(Vec2 velocity) {
		bodyPlus.setLinearVelocity(getSpace(), velocity);
	}
	
	public void setLinearVelocityAdd(Vec2 velocityToBeAdded) {
		bodyPlus.setLinearVelocityAdd(getSpace(), velocityToBeAdded);
	}
	
	public void setLinearVelocityTowardsPosition(Point2D.Float position) {
		setLinearVelocityTowardsPosition(position, 1);
	}
	
	public void setLinearVelocityTowardsPosition(Point2D.Float position, float factor) {
		setLinearVelocityTowardsPosition(new Vec2(position.x, position.y), factor);
	}
	
	public void setLinearVelocityTowardsPosition(Vec2 position, float factor) {
		bodyPlus.setLinearVelocity(getSpace(), position.sub(getPositionAsVec2()).mul(factor));
	}
	
	public void setLinearVelocityAddTowardsPosition(Point2D.Float position, float factor) {
		setLinearVelocityAddTowardsPosition(new Vec2(position.x, position.y), factor);
	}
	
	public void setLinearVelocityAddTowardsPosition(Vec2 position, float factor) {
		bodyPlus.setLinearVelocityAdd(getSpace(), position.sub(getPositionAsVec2()).mul(factor));
	}
	
	public void setCircle(Circle c) {
		bodyPlus.setCircle(getSpace(), c);
	}
	
	public boolean isInside(Point2D.Float p) {
		return getCircle().isInside(p);
	}
	
	public float getFadingDegree() {
		return (float) (System.currentTimeMillis()-getFadeAwayStart())
				/ (float) Params.visualS.fadingBooksOutTimeSpanInTicks();
	}
	
	public boolean isInQueueToFadeIn() {
		return queuedToFadeIn;
	}
	
	/**
	 * @return whether the queued in status changed
	 */
	public boolean setInQueueToFadeIn(Vec2 positionToFadeIn) {
		return setInQueueToFadeIn(true, positionToFadeIn);
	}
	
	public void removeFromQueueToFadeIn() {
		setInQueueToFadeIn(false, null);
	}
	
	/**
	 * @return whether the status changed (false if was false && set to false || was true && set to true)
	 */
	public boolean setInQueueToFadeIn(boolean queuedToFadeIn, Vec2 positionToFadeIn) {
		boolean oldQueuedToFadeIn = this.queuedToFadeIn;
		if (queuedToFadeIn) {
			assertTrue(!isActive());
			this.positionToFadeIn = positionToFadeIn;
		}
		this.queuedToFadeIn = queuedToFadeIn;
		if (oldQueuedToFadeIn == queuedToFadeIn)
			System.err.println("Warning. setInQueueToFadeIn set to same state.");
		return oldQueuedToFadeIn != queuedToFadeIn;
	}
	
	public Vec2 getPositionToFadeIn() {
		return positionToFadeIn;
	}
	
	public boolean isFading() {
		return fadeAway;
	}
	
	public void setFadeAway(boolean flag) {
		fadeAway = flag;
		fadeAwayStartTick = flag ? System.currentTimeMillis() : 0;
	}
	
	public long getFadeAwayStart() {
		return fadeAwayStartTick;
	}
	
	@Post(Params.returnIn0to1)
	public float getProximityToMouse(MousePath mousePath) {
		return getProximityToMouse(
			mousePath,
			getPosition(),
			getSpace().getSize()*Params.visualS.kProximityCutoffDistanceRatioToSpaceWidth()
		);
	}
	
	@Post(Params.returnIn0to1)
	public float getSchweifProximityAndUpdate(MousePath mousePath) {
		return getSchweifProximityAndUpdate(
			mousePath,
			getPosition(),
			getSpace().getSize()*Params.visualS.kProximityCutoffDistanceRatioToSpaceWidth()
		);
	}
	
	public String toString() {
		return name;
	}

}
