package matt.java;

import static org.junit.Assert.*;

public class TestClass {
	TestClass() {
		try {
			assert(false);
			throw new RuntimeException();
		} catch (AssertionError e) {
			// assertions are enabled
			System.out.println("success2!");
		} catch (RuntimeException e) {
			// assertions are disabled
			fail();
		}
	}
}
