package matt.ui.screenElement;

import static org.junit.Assert.*;
import matt.parameters.Params;
import matt.util.Util;

public class TimestampedObject<V> {
	public V v;
	public int counter;
	public long timestamp;
	public double random;
	public double random2;
	
	TimestampedObject(V v, int counter) {
		this.v = v;
		this.counter = counter;
		random = Math.random();
		random2 = Math.random();
		timestamp = System.currentTimeMillis();
	}
	
	TimestampedObject(V v, int counter, long timestamp) {
		assertTrue( timestamp <= System.currentTimeMillis());
		this.v = v;
		this.counter = counter;
		random = Math.random();
		random2 = Math.random();
		this.timestamp = timestamp;
	}
	
	/**
	 * If the details overlay is, while fading in, deselected, the fade out should start off there.
	 */
	public TimestampedObject<V> adjustTimestampForResumingFadeState() {
		timestamp = (long) (System.currentTimeMillis()
			-Params.visualS.bDetailsFadeInTimeSpanInTicks()*(1-getTimeFactor()));
		return this;
	}
	
	public long getTimeDeltaTilNow() {
		return System.currentTimeMillis()-timestamp;
	}
	
	public double getTimeFactor() {
		return getTimeFactor(Params.visualS.bDetailsFadeInTimeSpanInTicks());
	}
	
	public double getTimeFactor(long cap) {
		return  Util.percentiseIn(getTimeDeltaTilNow(), cap);
	}
	
	public String toString(String delim) {
		return v.toString() + delim + timestamp;
	}
}