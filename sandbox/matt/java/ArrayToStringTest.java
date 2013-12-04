package matt.java;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ArrayToStringTest {

	
	@Test
	public void test() {
		long[][] arr = {{1,2},{3,4},{5,6,7}};
		assertTrue(Arrays.deepToString(arr).equals("[[1, 2], [3, 4], [5, 6, 7]]"));
		
		String[] strArr = {"ab", "c", "d"};
		assertTrue(Arrays.toString(strArr).equals("[ab, c, d]"));
		
		Long seven = 7L;
		assertTrue(seven.toString().equals("7"));
		
	}
}
