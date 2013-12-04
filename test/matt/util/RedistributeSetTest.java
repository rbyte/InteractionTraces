package matt.util;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class RedistributeSetTest {
	
	public static final float d = 0.00001f;
	
	@Test
	public void scaleAndShift() {
		assertArrayEquals(RedistributeSet.scaleIntoZeroToOne(new float[] {4,5}), new float[] {0, 1}, d);
		assertArrayEquals(RedistributeSet.scaleIntoZeroToOne(new float[] {1,4,7}), new float[] {0, 0.5f, 1}, d);
		
		assertArrayEquals(RedistributeSet.shiftToHaveAMeanOfZero(new float[] {0,1}), new float[] {-0.5f, 0.5f}, d);
		assertArrayEquals(RedistributeSet.shiftToHaveAMeanOfZero(new float[] {-2,1,1}), new float[] {-2,1,1}, d);
		assertArrayEquals(RedistributeSet.shiftToHaveAMeanOfZero(new float[] {0,1,1}), new float[] {-2/3f,1/3f,1/3f}, d);
		
		assertArrayEquals(RedistributeSet.scaleIntoMinusOneToOneWithMeanOfZero(new float[] {0,1}), new float[] {-1,1}, d);
		assertArrayEquals(RedistributeSet.scaleIntoMinusOneToOneWithMeanOfZero(new float[] {3,4,5}), new float[] {-1,0,1}, d);
		assertArrayEquals(RedistributeSet.scaleIntoMinusOneToOneWithMeanOfZero(new float[] {0,1,1}), new float[] {-1f,1/2f,1/2f}, d);
		
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,1}, 4,5,6), new float[] {4,6}, d);
		assertArrayEquals(RedistributeSet.rescale(new float[] {9}, 4,5,6), new float[] {5}, d);
		assertArrayEquals(RedistributeSet.rescale(new float[] {}, 4,5,6), new float[] {}, d);
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,0,0}, 4,5,6), new float[] {5, 5, 5}, d);
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,1,1}, 4,5,6), new float[] {4, 5.5f, 5.5f}, d);
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,0,1}, 4,5,6), new float[] {4.5f, 4.5f, 6}, d);

		assertArrayEquals(RedistributeSet.rescale(new float[] {0,0,3}, 4, 5, 7), new float[] {4, 4, 7}, d);
		
//		System.out.println(Arrays.toString(RedistributeSet.rescale(new float[] {0,0,3}, -2, 0, 4)));
		
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,0,3}, -2, 0, 4), new float[] {-2, -2, 4}, d);
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,0,3}, 4, 6, 10), new float[] {4, 4, 10}, d);
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,0,1}, 4, 4+1/3f, 5), new float[] {4, 4, 5}, d);
		
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,0,1}, 4, 6, 12), new float[] {4, 4, 10}, d);
		assertArrayEquals(RedistributeSet.rescale(new float[] {0,0,1}, 0, 6, 10), new float[] {4, 4, 10}, d);
		
		assertArrayEquals(new RedistributeSet(new float[] {0,0,1}).scaleInto(0, 2), new float[] {0,0,2}, d);
		assertArrayEquals(new RedistributeSet(new float[] {-1,0,1}).scaleInto(0, 2), new float[] {0,1,2}, d);
		assertArrayEquals(new RedistributeSet(new float[] {-1,0,1}).scaleInto(-5, 5), new float[] {-5,0,5}, d);
	}
	
	@Test
	public void test() {
		RedistributeSet rs = new RedistributeSet(new float[] {-1,0,1});
		assertTrue(rs.min() == -1);
		assertTrue(rs.max() == 1);
		assertTrue(rs.mean() == 0);
		assertArrayEquals(rs.scaleIntoZeroToOne(), new float[] {0,0.5f,1}, d);
		assertTrue(rs.min() == 0);
		assertTrue(rs.max() == 1);
		assertTrue(rs.mean() == 0.5f);
//		System.out.println(Arrays.toString(rs.shiftMean(-5)));
		assertArrayEquals(rs.shiftMeanTo(-5), new float[] {-5.5f,-5,-4.5f}, d);
		assertTrue(rs.min() == -5.5f);
		assertTrue(rs.max() == -4.5f);
		assertTrue(rs.mean() == -5);
		assertArrayEquals(rs.rescale(0, 0.5f, 1), new float[] {0, 0.5f, 1}, d);
		assertTrue(rs.min() == 0f);
		assertTrue(rs.max() == 1);
		assertTrue(rs.mean() == 0.5f);
		assertArrayEquals(rs.rescale(0, 0.75f, 1), new float[] {0.5f, 0.75f, 1}, d);
		assertTrue(rs.min() == 0.5);
		assertTrue(rs.max() == 1);
		assertTrue(rs.mean() == 0.75f);
		
		System.out.println(Arrays.toString(new RedistributeSet(new float[] {-1,0,1}).rescale(0, 0.75f, 1, true)));
		System.out.println(Arrays.toString(new RedistributeSet(new float[] {-1,0,1}).rescale(0, 0.8f, 1, true)));
		System.out.println(Arrays.toString(new RedistributeSet(new float[] {-1,0,1}).rescale(0, 0.9f, 1, true)));
	}
	
}
