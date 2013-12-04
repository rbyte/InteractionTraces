package matt.ui;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;

import matt.parameters.Params;
import matt.util.Util;

import static org.junit.Assert.*;

public class MousePath {

	private class PastMouseState {
		public Point2D.Float position;
		public final long time;
		public final int eventId;

		public PastMouseState(Point2D.Float p, int eventId) {
			position = p;
			this.eventId = eventId;
			time = System.currentTimeMillis();
		}
		
		@SuppressWarnings("unused")
		public long getTimeDeltaTilNow() {
			return System.currentTimeMillis()-time;
		}

		public String toString() {
			return position.toString() + ", time: " + time + "\n";
		}
	}
	
	// maximum age of any given PastMouseState kept in the path (ms)
	private static final long CUTOFFTIME = 5000;
	private static final boolean ENABLE_TESTING = false;
	private static final Point2D.Float DEFAULT_POSITION = new Point2D.Float(0, 0);
	private LinkedList<PastMouseState> path = new LinkedList<PastMouseState>();
	@SuppressWarnings("unused")
	private boolean nothingAddedInCurrentCycle = false;

	MousePath() {}

	public int getLength() {
		return path.size();
	}
	
	public MousePath add(float x, float y, int eventId) {
		if (!Params.visualS.captureOnlyClickedMouseMovement()
				|| eventId == MouseEvent.MOUSE_DRAGGED
				|| eventId == MouseEvent.MOUSE_RELEASED
				|| eventId == MouseEvent.MOUSE_CLICKED
				|| eventId == MouseEvent.MOUSE_PRESSED) {
			nothingAddedInCurrentCycle = false;
			return add(new Point2D.Float(x, y), eventId);	
		} else {
			nothingAddedInCurrentCycle = true;
			return this;
		}
	}
	
	// signal cycle end
	public void cycleEnd() {
		nothingAddedInCurrentCycle = true;
	}
	
	private boolean notValid() {
		return path.isEmpty();
//				|| (
//				Params.visualS.captureOnlyClickedMouseMovement()
//				&& nothingAddedInCurrentCycle
//				&& !isPureDrag(path.getFirst().eventId));
	}
	
	public boolean lastMouseIsPureDrag() {
		return !path.isEmpty() && isPureDrag(path.getFirst().eventId);
	}
	
	private boolean isPureDrag(int eventid) {
		return !Params.visualS.captureOnlyClickedMouseMovement()
			|| (eventid == MouseEvent.MOUSE_DRAGGED
			&& eventid != MouseEvent.MOUSE_RELEASED
			&& eventid != MouseEvent.MOUSE_CLICKED);
	}
	
	public Point2D.Float getCurrent() {
		return notValid() ? DEFAULT_POSITION : path.getFirst().position;
	}
	
	// get last position that the mouse was in dragged state
	public Point2D.Float getLastDragged() {
		if (!Params.visualS.captureOnlyClickedMouseMovement()) {
			return getCurrent();
		}
		
		for (PastMouseState pms : path) {
			if (isPureDrag(pms.eventId))
				return pms.position;
		}
		return DEFAULT_POSITION;
	}
	
	// get current mouse position if it is dragged
	public Point2D.Float getCurrentDragged() {
		if (path.isEmpty())
			return DEFAULT_POSITION;
		PastMouseState pms = path.getFirst();
		// check for released is important when captureOnlyClickedMouseMovement is true
		return isPureDrag(pms.eventId)
			? pms.position
			: DEFAULT_POSITION;
	}
	
	public double getDistanceFactorFrom(Point2D.Float p, float cutoffDist) {
		return 1-Util.percentiseIn(getDistanceFrom(p), 0, cutoffDist);
	}
	
	public double getDistanceFrom(Point2D.Float p) {
		return p.distance(getCurrent());
	}
	
	public double getDistanceFromSmoothedMousePosition(Point2D.Float p, int smoothing) {
		return p.distance(getSmoothedCurrentPosition(smoothing));
	}

	public long getTickDeltaTilLast() {
		for (PastMouseState pms : path) {
			return System.currentTimeMillis() - pms.time;
		}
		return System.currentTimeMillis();
	}
	
	public long getTickDeltaTilLastRelease() {
		for (PastMouseState pms : path) {
			if (pms.eventId == MouseEvent.MOUSE_RELEASED)
				return System.currentTimeMillis() - pms.time;
		}
		return System.currentTimeMillis();
	}
	
	public synchronized MousePath add(Point2D.Float p, int eventId) {
		path.addFirst(new PastMouseState(p, eventId));
		cutAwayOld();
		return this;
	}

	public Point2D.Float[] getLastXpoints(int numberOfPoints) {
		assertTrue(numberOfPoints > 0);
		Point2D.Float[] ps = new Point2D.Float[Math.min(numberOfPoints,
				path.size())];
		for (int i = 0; i < ps.length; i++) {
			ps[i] = path.get(i).position;
		}
		return ps;
	}

	public Point2D.Float getSmoothedCurrentPosition(int smoothing) {
		assertTrue(smoothing > 0);
		Point2D.Float[] pfs = new Point2D.Float[Math.min(smoothing, path.size())];
		for (int i = 0; i < pfs.length; i++) {
			pfs[i] = path.get(i).position;
		}
		return mean(pfs);
	}

	// public String printTimeDeltaBetweenPoints() {
	//
	// }

	private Point2D.Float mean(Point2D.Float... pfs) {
		float[] fsx = new float[pfs.length];
		float[] fsy = new float[pfs.length];
		for (int i = 0; i < pfs.length; i++) {
			fsx[i] = pfs[i].x;
			fsy[i] = pfs[i].y;
		}
		return new Point2D.Float(mean(fsx), mean(fsy));
	}

	private float mean(float... fs) {
		float mean = 0;
		for (float f : fs) {
			mean += f;
		}
		return fs.length > 0 ? mean / fs.length : 0;
	}

	private void cutAwayOld() {
		cutAwayOld(System.currentTimeMillis());
	}

	private void cutAwayOld(long currentTimeMillis) {
//		for (int i = path.size() - 1; i >= 0; i--) {
//			if (currentTimeMillis - path.get(i).time >= CUTOFFTIME) {
//				path.remove(i);
//			}
//		}
		
		while (path.size() > 0 && (currentTimeMillis - path.getLast().time >= CUTOFFTIME))
			path.removeLast();
		test(currentTimeMillis);
	}

	private void test(long currentTimeMillis) {
		if (ENABLE_TESTING) {
			PastMouseState pre = null;
			for (PastMouseState s : path) {
				if (pre != null) {
					assertTrue(s.time <= pre.time);
				}
				assertTrue(currentTimeMillis - s.time < CUTOFFTIME);
				s = pre;
			}
		}
	}

	public String toString() {
		return Arrays.toString(path.toArray(new PastMouseState[0]));
	}

	public float getSchweifDistance(float x, float y) {
		return getSchweifDistance(new Point2D.Float(x, y));
	}
	
	public float getSchweifDistance(Point2D.Float p) {
		long currentTimeMillis = System.currentTimeMillis();
		cutAwayOld(currentTimeMillis);
		// TODO handle path.size == 0
		float result = path.size() > 0 ? 0 : Float.MAX_VALUE;
		for (PastMouseState s : path) {
			float sInfluence = 1f - ((currentTimeMillis - s.time) / (float) CUTOFFTIME);
			assertTrue(0 <= sInfluence && sInfluence <= 1);
			result += sInfluence * p.distance(s.position);
		}
		return result;
	}

}
