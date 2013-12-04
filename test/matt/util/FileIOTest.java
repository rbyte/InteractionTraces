package matt.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.*;

public class FileIOTest {

	File testFile;
	
    @Before
    public void createOutputFile() {
		testFile = new File("./test/"+Math.random());
		if (testFile.exists()) {
			fail("Test file already existing.");
		}
    }
	
	@Test
	public void file1() throws IOException {
		String testString = "blablub\npeng";
		StringHandling.writeStringToFile(testFile, testString, false);
		assertEquals(StringHandling.readFileAsString(testFile), testString);
	}
	
	@Test
	public void file2() throws IOException  {
		Float[][] testMatrix = {{(float) Math.PI,4f},{2f,1f,9f},{4f,5f,6f,7f}};
		Util.writeCSV(testFile, testMatrix);
		Float[][] back = StringHandling.parse(StringHandling.readCSV(testFile));
		assertTrue(back.length == testMatrix.length);
		for (int i = 0; i<back.length; i++) {
			assertArrayEquals(testMatrix[i], back[i]);
		}
	}
	
	@Test
	public void file3() throws IOException  {
		Float[][] testMatrix = {{0f},{1f,2f},{3f,4f,5f}};
		Util.writeCSV(testFile, testMatrix);
		Float[][] back = StringHandling.parse(StringHandling.readCSV(testFile));
		assertTrue(back.length == testMatrix.length);
		for (int i = 0; i<back.length; i++) {
			assertArrayEquals(testMatrix[i], back[i]);
		}
		
		Float[][] expected = {{2f},{4f,5f}};
		back = StringHandling.parse(StringHandling.readCSV(testFile, true, true));
		
		assertTrue(back.length == expected.length);
		for (int i = 0; i<back.length; i++) {
			assertArrayEquals(back[i], expected[i]);
		}
	}
	
	@Test
	public void file4() throws IOException  {
		String[][] testMatrix = {{"bla","bra"},{"a","bcd","e"}};
		Util.writeCSV(testFile, testMatrix);
		String[][] back = StringHandling.readCSV(testFile);
		assertTrue(back.length == testMatrix.length);
		assertArrayEquals(testMatrix, back);
		
		back = StringHandling.readCSV(testFile, true, 1);
		assertTrue(back.length == 1);
		assertArrayEquals(back[0], testMatrix[0]);
	}
	
    @After
    public void deleteOutputFile() {
		if (!testFile.delete()) {
			fail("Test file could not be deleted.");
		}
    }

}
