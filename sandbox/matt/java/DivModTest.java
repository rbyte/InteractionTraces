package matt.java;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DivModTest {
	
	@Test
	public void test() {
		assertTrue(7d % 10d == 7d);
		assertTrue(-7d % 10d == -7d);
		assertTrue(Math.abs(-7d % 10d) == 7d);
		assertTrue(Math.abs(-0d % 10d) == 0d);
		assertTrue(Math.abs(-11d % 10d) == 1d);
		assertTrue(Math.abs(10d % 10d) == 0d);
		assertTrue(Math.abs(22d % 10d) == 2d);
		assertTrue(-4d % 4d == 0d);
	}

}
