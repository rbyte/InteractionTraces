package matt.java;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class InlineIfTest {

	@Test
	public void test() {
		
		assertEquals(5 + 5 > 7 ? 3 : -3, 3);
		assertEquals(5 + (5 > 7 ? 3 : -3), 2);
		
	}

}
