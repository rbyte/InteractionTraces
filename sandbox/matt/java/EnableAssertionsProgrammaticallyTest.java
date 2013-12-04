package matt.java;

import static org.junit.Assert.*;

import org.junit.Test;

// enable assertions (assert) programatically
public class EnableAssertionsProgrammaticallyTest {

	@Test
	public void test() {
		try {
			assert false;
			try {
				ClassLoader loader = ClassLoader.getSystemClassLoader();
				Class<?> c = loader.loadClass("matt.independent.TestClass");
				assertTrue(!c.desiredAssertionStatus());
				loader.setDefaultAssertionStatus(true);
				assertTrue(c.desiredAssertionStatus());
				c = loader.loadClass("matt.independent.TestClass");

				@SuppressWarnings("unused")
				TestClass myObj = (TestClass) c.newInstance();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		} catch (AssertionError e) {
			fail("Assertions are enabled. Rerun test without enabled assertions.");
		}
	}

}
