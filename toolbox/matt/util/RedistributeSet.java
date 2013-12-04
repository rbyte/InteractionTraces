package matt.util;

import static org.junit.Assert.*;
import matt.parameters.Params;

import org.contract4j5.contract.Post;


public class RedistributeSet {
	
	private float[] arr;
	
	private float min;
	private float max;
	private float mean;
	
	// yes, it needs to be that big
	private static final float DELTA = 0.0001f;
	private static final boolean ENABLE_TESTING = false;
	
	public RedistributeSet(float[] array) {
		this.arr = array;
		updateMinMaxMean();
	}
	
	public float min() { return min; }
	public float max() { return max; }
	public float mean() { return mean; }
	
	private void setMinMaxMean(float allToThis) {
		for (int i=0; i<arr.length; i++)
			arr[i] = allToThis;
		setMinMaxMean(allToThis, allToThis, allToThis);
	}
	
	private void setMinMaxMean(float min, float max, float mean) {
		this.min = min; this.max = max; this.mean = mean;
	}
	
	private void minMaxMeanAreValid() {
		if (ENABLE_TESTING) {
			float oldMin = min;
			float oldMax = max;
			float oldMean = mean;
//			System.out.println(min+", "+max+", "+mean);
			updateMinMaxMean();
			assertEquals(oldMin, min, DELTA);
			assertEquals(oldMax, max, DELTA);
			assertEquals(oldMean, mean, DELTA);
		}
	}
	
	private void updateMinMaxMean() {
		if (arr.length == 0) {
			handleEmptyArray();
			return;
		}
		
		min = Float.POSITIVE_INFINITY;
		max = Float.NEGATIVE_INFINITY;
		mean = 0;
		for (int i=0; i<arr.length; i++) {
			if (arr[i] < min) min = arr[i];
			if (arr[i] > max) max = arr[i];
			mean += arr[i];
		}
		// this is actually not the best way of calculating it. huge rounding errors happening here.
		mean /= arr.length;
		assertTrue(min <= mean && mean <= max);
	}
	
	public float minMaxDelta() {
		return max-min;
	}
	
	private boolean minMaxDeltaIsCloseToZero() {
		return minMaxDelta() < DELTA;
	}
	
	private float[] handleEmptyArray() {
		min = 0;
		max = 0;
		mean = 0;
		arr = new float[] {};
		return arr;
	}
	
	/**
	 * calculates data set bounds (min & max) and scales it into [0,1]
	 */
	public static float[] scaleIntoZeroToOne(float[] arr) {
		return new RedistributeSet(arr).scaleIntoZeroToOne();
	}
	
	public float[] scaleIntoZeroToOne() {
		return min != 0 || max != 0 ? scaleInto(0, 1) : arr;
	}
	
	public float[] scaleInto(float lowBound, float highBound) {
		if (arr.length == 0)
			return handleEmptyArray();
		
		assertTrue(lowBound < highBound);
		float newMean = 0;
		for (int i=0; i<arr.length; i++) {
			arr[i] = (arr[i]-min) / minMaxDelta() * (highBound-lowBound) + lowBound;
			newMean += arr[i];
		}
		min = lowBound;
		max = highBound;
		mean = newMean / arr.length;
		minMaxMeanAreValid();
		return arr;
	}
	
	public static float[] shiftToHaveAMeanOfZero(float[] arr) {
		return new RedistributeSet(arr).shiftMeanTo(0);
	}
	
	/**
	 * calculates mean and subtracts it from every element, than adds new mean
	 */
	public float[] shiftMeanTo(float newMean) {
		if (arr.length == 0)
			return handleEmptyArray();
		
		float adjustment = - mean + newMean;
		for (int i=0; i<arr.length; i++)
			arr[i] += adjustment;
		mean = newMean;
		min += adjustment;
		max += adjustment;
		minMaxMeanAreValid();
		return arr;
	}
	
	public static float[] scaleIntoMinusOneToOneWithMeanOfZero(float[] arr) {
		return rescale(arr, -1, 0, 1);
	}
	
	public static float[] rescale(float[] arr, float lowBound, float newMean, float highBound) {
		return new RedistributeSet(arr).rescale(lowBound, newMean, highBound);
	}
	
	public float[] rescale(float lowBound, float newMean, float highBound) {
		return rescale(lowBound, newMean, highBound, false);
	}
	
	private static final int MAX_REDISTRIBUTE_ITERATIONS = 5;
	
	private void redistribute(float lowBound, float supposedToMean, float highBound) {
		scaleIntoZeroToOne();
		float meanRelativePositionDif = mean - (float) Util.percentiseIn(supposedToMean, lowBound, highBound);
		int counter = 0;
		while (Math.abs(meanRelativePositionDif) > 0.01 && counter++ < MAX_REDISTRIBUTE_ITERATIONS) {
//			System.out.println(meanRelativePositionDif);
			if (Math.abs(meanRelativePositionDif) > 0.45)
				meanRelativePositionDif = Math.signum(meanRelativePositionDif)*0.45f;
			float y = (float) (Math.log(-meanRelativePositionDif+0.5)/Math.log(0.5));
//			System.out.println("mean before: "+mean+", y: "+y);
//			System.out.println("mean wanna: "+ percentiseIn(supposedToMean, lowBound, highBound));
			float newMean = 0;
			for (int i=0; i<arr.length; i++) {
				arr[i] = (float) Math.pow(arr[i], y);
				assertTrue(0 <= arr[i] && arr[i] <= 1);
				newMean += arr[i];			
			}
			newMean /= arr.length;
			mean = newMean;
			float oldMeanRelativePositionDif = meanRelativePositionDif;
			meanRelativePositionDif = mean - (float) Util.percentiseIn(supposedToMean, lowBound, highBound);
			float improvement = Math.abs(oldMeanRelativePositionDif-meanRelativePositionDif);
//			System.out.println("improvement: "+improvement);
			assertTrue(improvement >= 0);
			minMaxMeanAreValid();
//			System.out.println("mean after: "+mean);
		}
	}
	
	/**
	 * @return all values in [lowBound,highBound], with a mean of newMean (bounds may not be touched on one side)
	 */
	public float[] rescale(float lowBound, float newMean, float highBound, boolean allowRedistribution) {
		if (arr.length == 0)
			return handleEmptyArray();
		
		assertTrue(lowBound < newMean && newMean < highBound);
		if (minMaxDeltaIsCloseToZero()) {
			setMinMaxMean(newMean);
		} else {
			if (allowRedistribution) {
				redistribute(lowBound, newMean, highBound);
			}
			
			shiftMeanTo(0);
			float nMinM0 = lowBound-newMean;
			float nMaxM0 = highBound-newMean;
			assertTrue(nMinM0 < 0 && 0 < nMaxM0);
			boolean scaleOnMax = -min/max < -nMinM0/nMaxM0;
			
			float actualMaxBound = scaleOnMax ? highBound : (max/-min*-nMinM0+newMean);
			float actualMinBound = scaleOnMax ? (min/max*nMaxM0+newMean) : lowBound;
			
			for (int i=0; i<arr.length; i++) {
				arr[i] /= scaleOnMax ? max : -min;
				arr[i] *= scaleOnMax ? nMaxM0 : -nMinM0;
				arr[i] += newMean;
				assertTrue(lowBound <= arr[i] && arr[i] <= highBound);
			}
			
//			System.out.println(actualMinBound+", "+lowBound+", "+highBound+", "+actualMaxBound);
			
//			System.out.println("Accuracy %: "+(scaleOnMax
//				? 1-percentiseIn(actualMinBound, lowBound, highBound)
//				: percentiseIn(actualMaxBound, lowBound, highBound)));
			max = actualMaxBound;
			min = actualMinBound;
			mean = newMean;
		}
		minMaxMeanAreValid();
		return arr;
	}

	@Post(Params.returnIn0to1) public float percentiseInMinMax(float val) {
		return (float) Util.percentiseIn(val, min, max);
	}

}
