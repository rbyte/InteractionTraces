package matt.ui.screenElement;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.NoSuchElementException;

import matt.parameters.Params;
import matt.util.Square;

public class ScreenElementSet<T extends ScreenElement<?>> extends HashSet<T> {

	private static final long serialVersionUID = -3372713367771494291L;
	private Rectangle2D.Float spaceWithBorder;
	private Square space;

	protected ScreenElementSet(Rectangle2D.Float spaceWithBorder) {
		this.spaceWithBorder = spaceWithBorder;
		this.space = Square.getInnerSquare(spaceWithBorder,
				Math.min(spaceWithBorder.height, spaceWithBorder.width)
						* Params.visualS.borderPercentage());
	}

	public Rectangle2D.Float spaceWithBorder() {
		return spaceWithBorder;
	}

	public Square space() {
		return space;
	}
	
	public T get(String name) {
		for (T t : this)
			if (t.getName().equals(name))
				return t;
		return null;
	}
	
	public ScreenElementSet<T> getAllActiveScreenElements() {
		ScreenElementSet<T> result = new ScreenElementSet<T>(spaceWithBorder);
		for (T t : this) {
			if (t.isActive())
				result.add(t);
		}
		return result;
	}
	
	
	public void unactivateAll() {
		dequeueAll();
		for (T t : this)
			t.setActive(false);
	}
	
	public void dequeueAll() {
		for (T t : this)
			if (t.isInQueueToFadeIn())
				t.removeFromQueueToFadeIn();
	}
	
	public T getNextInQueueAndDequeue() {
		for (T t : this)
			if (t.isInQueueToFadeIn()) {
				t.removeFromQueueToFadeIn();
				return t;
			}
		return null;
	}
	
	public ScreenElementSet<T> getAllActiveAndQueuedNonFadingScreenElements() {
		ScreenElementSet<T> result = new ScreenElementSet<T>(spaceWithBorder);
		for (T t : this) {
			if (t.isInQueueToFadeIn() || (t.isActive() && !t.isFading()))
				result.add(t);
		}
		return result;
	}
	
	public T getClickedOn(Point2D.Float p) {
		for (T t : this) {
			if (t.isInside(p))
				return t;
		}
		return null;
	}
	
	public T getActiveClickedOn(Point2D.Float p) {
		for (T t : this) {
			if (t.isActive() && t.isInside(p))
				return t;
		}
		return null;
	}
	
	public T getRandom() {
		int number = (int) Math.round(Math.random()*(double) (size()-1));
		// disturbs randomness to create traces that tend towards certain keyword
		if (Params.randomTraceGenerationDistributionInfluence != 0)
			number = (int) (this.size()*Math.pow(number/(double) this.size(),
				Params.randomTraceGenerationDistributionInfluence));
		int current = 0;
		for (T k : this) {
			if (current++ == number)
				return k;
		}
		throw new NoSuchElementException();
	}

}
