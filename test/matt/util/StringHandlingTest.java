package matt.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class StringHandlingTest {
	
	@Test
	public void parse() {
		String[][] input = new String[][] {{"3","5.6"}, {"5.67"}};
		Float[][] expected = new Float[][] {{3f,5.6f}, {5.67f}};
		Float[][] out = StringHandling.parse(input);
		assertArrayEquals(expected, out);
	}
	
	@Test
	public void fileName() {
		assertTrue(StringHandling.getFileNameWithoutExtension(
			new File("./test/gweipguheig.fgw")).equals("gweipguheig"));
		
		assertTrue(StringHandling.getFileExtension(
			new File("./test/gweipguheig.fgw")).equals("fgw"));
		
		assertTrue(StringHandling.appendToFileName(
			new File("./test/gweipguheig.fgw"), "_ext").getName()
			.equals("gweipguheig_ext.fgw"));
		
		assertTrue(StringHandling.subdir(
				new File("E:\\gweipguheig.fgw"), "bla").getAbsolutePath()
				.equals("E:\\bla\\gweipguheig.fgw"));
	}
	
	@Test
	public void concat() {
		String[] strArr = {"ab", "c", "d"};
		assertEquals(StringHandling.concat(strArr, ", "), "ab, c, d");
		assertEquals(StringHandling.concat(strArr, ""), "abcd");
		assertEquals(StringHandling.concat("?", 4), "????");
		assertEquals(StringHandling.concat("?", 4, ","), "?,?,?,?");
		assertEquals(StringHandling.concat("?", 4, ",", "'"), "'?','?','?','?'");
		
		Double[] doubleMat = {Math.PI,2.0,3.0};
		assertTrue(StringHandling.concat(doubleMat, ",")
			.equals(Math.PI+",2.0,3.0"));
		assertTrue(StringHandling.concat(doubleMat, ",", "\"")
			.equals("\""+Math.PI+"\",\"2.0\",\"3.0\""));
		assertTrue(StringHandling.concat(new String[] {"h", "a", "l", "l", "o"})
			.equals("hallo"));
	}
	
	@Test
	public void wrap() {
		assertEquals(StringHandling.wrap("1234567", 2), "12\n34\n56\n7");
		assertEquals(StringHandling.wrap("12345678", 2), "12\n34\n56\n78");
		assertEquals(StringHandling.wrap("12345678", 10), "12345678");
		assertEquals(StringHandling.wrap("123", 1), "1\n2\n3");
	}
	
	@Test
	public void cutOut() {
		assertTrue(StringHandling.cutOut("abcdef", "c", "e").equals("abf"));
	}

}
