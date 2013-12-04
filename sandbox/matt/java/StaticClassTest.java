package matt.java;

import static org.junit.Assert.*;

import org.junit.Test;

public class StaticClassTest {
	
	private static int numberOfCalls = 0;
	
	@Test
	public void test() {
		assertTrue(numberOfCalls == 0);
		assertTrue(IntegerCache.val == 50);
		assertTrue(numberOfCalls == 1);
		assertTrue(IntegerCache.val == 50);
		assertTrue(numberOfCalls == 1);
	}
	
    private static class IntegerCache {
    	static final int val;
    	
        static {
        	numberOfCalls++;
            val = 50;
        }
    }

}
