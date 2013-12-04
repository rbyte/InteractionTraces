package matt.ui.screenElement;

import matt.util.Util;

import org.jbox2d.common.Vec2;

import static org.junit.Assert.*;

public class TransitioningScreenElement<T> {
	
	private T elem;
	
	private float size;
	private float sizeOld;
	private float sizeToBe;
	private long sizeToBeStartTick = 0;
	private long sizeToBeEndTick = 0;
	
	private float cDiameter;
	private float cDiameterOld;
	private float cDiameterToBe;
	private long cDiameterToBeStartTick = 0;
	private long cDiameterToBeEndTick = 0;
	
	private Vec2 position;
	private Vec2 positionOld;
	private Vec2 positionToBe;
	private long positionToBeStartTick = 0;
	private long positionToBeEndTick = 0;
	
	private Vec2 cPos;
	private Vec2 cPosOld;
	private Vec2 cPosToBe;
	private long cPosToBeStartTick = 0;
	private long cPosToBeEndTick = 0;
	
	public TransitioningScreenElement(T elem) {
		this(elem, new Vec2(0, 0), new Vec2(0, 0), 0, 0);
	}
	
	public TransitioningScreenElement(T elem, Vec2 position, Vec2 cPos, float size, float cDiameter) {
		this.elem = elem;
		
		this.size = size;
		sizeToBe = size;
		sizeOld = size;
		
		this.position = position;
		positionToBe = position;
		positionOld = position;
		
		this.cPos = cPos;
		cPosToBe = cPos;
		cPosOld = cPos;
		
		this.cDiameter = cDiameter;
		cDiameterToBe = cDiameter;
		cDiameterOld = cDiameter;
	}
	
	public T get() {
		return elem;
	}
	
	private float getSizeTransitionPercentage() {
		return sizeToBeStartTick == sizeToBeEndTick ? 1 :
			(float) Util.percentiseIn(System.currentTimeMillis(), sizeToBeStartTick, sizeToBeEndTick);
	}
	
	private void updateSize() {
		size = Util.linearInterpolation(sizeOld, sizeToBe, getSizeTransitionPercentage());
	}
	
	public float getSize() {
		updateSize();
		return size;
	}
	
	public void setSize(float newSize, long durationTicks) {
		updateSize();
		assertTrue(durationTicks >= 0);
		sizeOld = size;
		sizeToBe = newSize;
		sizeToBeStartTick = System.currentTimeMillis();
		sizeToBeEndTick = sizeToBeStartTick + durationTicks;
	}
	
	// TODO method duplication
	private float getCDiameterTransitionPercentage() {
		return cDiameterToBeStartTick == cDiameterToBeEndTick ? 1 :
			(float) Util.percentiseIn(System.currentTimeMillis(), cDiameterToBeStartTick, cDiameterToBeEndTick);
	}
	
	private void updateCDiameter() {
		cDiameter = Util.linearInterpolation(cDiameterOld, cDiameterToBe, getCDiameterTransitionPercentage());
	}
	
	public float getCDiameter() {
		updateCDiameter();
		return cDiameter;
	}
	
	public void setCDiameter(float newCDiameter, long durationTicks) {
		updateCDiameter();
		assertTrue(durationTicks >= 0);
		cDiameterOld = cDiameter;
		cDiameterToBe = newCDiameter;
		cDiameterToBeStartTick = System.currentTimeMillis();
		cDiameterToBeEndTick = cDiameterToBeStartTick + durationTicks;
	}
	
	// TODO method duplication
	private void updatePosition() {
		float p = positionToBeStartTick == positionToBeEndTick ? 1 :
			(float) Util.percentiseIn(System.currentTimeMillis(), positionToBeStartTick, positionToBeEndTick);
		position = Util.linearInterpolation(positionOld, positionToBe, p);
	}
	
	public Vec2 getPosition() {
		updatePosition();
		return position;
	}
	
	public void setPosition(Vec2 newPosition, long durationTicks) {
		updatePosition();
		assertTrue(durationTicks >= 0);
		positionOld = position;
		positionToBe = newPosition;
		positionToBeStartTick = System.currentTimeMillis();
		positionToBeEndTick = positionToBeStartTick + durationTicks;
	}
	
	// TODO method duplication
	private void updateCPos() {
		float p = cPosToBeStartTick == cPosToBeEndTick ? 1 :
			(float) Util.percentiseIn(System.currentTimeMillis(), cPosToBeStartTick, cPosToBeEndTick);
		cPos = Util.linearInterpolation(cPosOld, cPosToBe, p);
	}
	
	public Vec2 getCPos() {
		updateCPos();
		return cPos;
	}
	
	public void setCPos(Vec2 newCPos, long durationTicks) {
		updateCPos();
		assertTrue(durationTicks >= 0);
		cPosOld = cPos;
		cPosToBe = newCPos;
		cPosToBeStartTick = System.currentTimeMillis();
		cPosToBeEndTick = cPosToBeStartTick + durationTicks;
	}

}
