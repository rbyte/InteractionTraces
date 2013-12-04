package matt.ui.screenElement;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import matt.parameters.Params;
import matt.ui.MousePath;

import org.contract4j5.contract.Post;

public class Schweif {
	
	private long lastHitTick = 0;
	private float lastClosestProx = 0;
	
	private float schweifTimeout;
	
	public Schweif() {
		this.schweifTimeout = Params.visualS.schweifTimeout();
	}
	
	public Schweif(float schweifTimeout) {
		this.schweifTimeout = schweifTimeout;
	}
	
	@Post(Params.returnIn0to1)
	public float getSchweifProximityAndUpdate(MousePath mousePath, Rectangle2D.Float rect, float cutoff) {
		float prox;
		if (rect.contains(mousePath.getCurrent())) {
			prox = 1;
		} else {
			float prox1 = getProximityToMouse(mousePath, new Point2D.Float((float) rect.getMaxX(), (float) rect.getMaxY()), cutoff);
			float prox2 = getProximityToMouse(mousePath, new Point2D.Float((float) rect.getMinX(), (float) rect.getMaxY()), cutoff);
			float prox3 = getProximityToMouse(mousePath, new Point2D.Float((float) rect.getMaxX(), (float) rect.getMinY()), cutoff);
			float prox4 = getProximityToMouse(mousePath, new Point2D.Float((float) rect.getMinX(), (float) rect.getMinY()), cutoff);
			prox = Math.max(Math.max(prox1, prox2), Math.max(prox3, prox4));
		}
		return getSchweifProximityAndUpdate(prox, mousePath.getTickDeltaTilLast());
	}
	
	@Post(Params.returnIn0to1)
	public float getSchweifProximityAndUpdate(MousePath mousePath, Point2D.Float objectPosition, float cutoff) {
		return getSchweifProximityAndUpdate(getProximityToMouse(mousePath, objectPosition, cutoff),
				mousePath.getTickDeltaTilLast());
	}
	
	@Post(Params.returnIn0to1)
	public float getProximityToMouse(MousePath mousePath, Point2D.Float objectPosition, float cutoff) {
		return getProximityToMouse((float) objectPosition.distance(mousePath.getCurrent()), cutoff);
	}
	
	@Post(Params.returnIn0to1)
	private float getProximityToMouse(float pixelDistance, float cutoff) {
		return 1 - (pixelDistance > cutoff ? 1f : pixelDistance/cutoff);
	}
	
	@Post(Params.returnIn0to1)
	private float getSchweifProximityAndUpdate(float prox, long tickDeltaTilLastMousePressed) {
		float schweifProx = getSchweifProximity();
		float factor = tickDeltaTilLastMousePressed > schweifTimeout ? 0
				: 1-(tickDeltaTilLastMousePressed / schweifTimeout);
		assertTrue( (0 <= factor && factor <= 1));
		prox *= factor;
		if (schweifProx < prox) {
			this.lastClosestProx = prox;
			lastHitTick = System.currentTimeMillis();
		}
		return schweifProx;
	}
	
	@Post(Params.returnIn0to1)
	private float getSchweifProximity() {
		return getTimeKoefficient()*lastClosestProx;
	}
	
	@Post(Params.returnIn0to1)
	private float getTimeKoefficient() {
		return getLastHitTickDeltaToNow() > schweifTimeout ?
			0 : 1-(getLastHitTickDeltaToNow()/schweifTimeout);
	}
	
	private long getLastHitTickDeltaToNow() {
		return System.currentTimeMillis()-lastHitTick;
	}

}
