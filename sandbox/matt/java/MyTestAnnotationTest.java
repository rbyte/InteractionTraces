package matt.java;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class MyTestAnnotationTest {
	
	@Test
	public void main() throws Exception {
		int passed = 0, failed = 0;
		for (Method m : Class.forName("matt.independent.OwnTestAnnotation").getMethods()) {
			if (m.isAnnotationPresent(MyTestAnnotation.class)) {
				try {
					m.invoke(null);
					passed++;
				} catch (Throwable ex) {
					System.out.printf("Test %s failed: %s %n", m, ex.getCause());
					failed++;
				}
			}
		}
		System.out.printf("Passed: %d, Failed %d%n", passed, failed);
		assertTrue(passed == 2);
		assertTrue(failed == 2);
	}

	@MyTestAnnotation
	public static void m1() {
	}

	public static void m2() {
	}

	@MyTestAnnotation
	public static void m3() {
		throw new RuntimeException("Boom");
	}

	public static void m4() {
	}

	@MyTestAnnotation
	public static void m5() {
	}

	public static void m6() {
	}

	@MyTestAnnotation
	public static void m7() {
		throw new RuntimeException("Crash");
	}

	public static void m8() {
	}
}
