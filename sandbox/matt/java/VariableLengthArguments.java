package matt.java;

import static org.junit.Assert.*;

import org.junit.Test;

public class VariableLengthArguments {
	
	@Test
	public void test() {
		assertTrue(sum(3,4,5,6,7) == 3+4+5+6+7);
	}
	
	static int sum (int ... numbers) {
	   int total = 0;
	   for (int i = 0; i < numbers.length; i++)
	        total += numbers [i];
	   return total;
	}

}
