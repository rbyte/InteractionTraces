package matt.myProcessing;

import org.junit.Test;

import matt.setup.SVGconverter;
import matt.util.Regex;
import static org.junit.Assert.*;

public class SVGconverterTest {

	@SuppressWarnings("deprecation")
	@Test
	public void run() {
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa3aaa", 1, 1, "3");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa3,-3aaa", 1, 1, "3");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa3-3aaa", 1, 1, "3");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa3.3aaa", 1, 1, "3.3");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa-3.3aaa", 1, 1, "-3.3");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa3.333aaa", 1, 1, "3.333");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa-3.3e3aaa", 1, 1, "-3.3e3");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa-3.3e-3-3.3e-3aaa", 1, 1, "-3.3e-3");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa3.333aaa", 1, 1, "3.333");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa-3.333-1.1aaa", 1, 1, "-3.333");
		Regex.checkRegex(SVGconverter.group(SVGconverter.floatPattern), "aaa-3.333-1.1aaa", 2, 1, "-1.1");
		
		Regex.checkRegex(SVGconverter.pathPatternOnly, "<path c=\"bla\" d=\"M0.1,0.1z m0.3,0.2l0.1-0.4z\" e=\"bla\">", 1, 1, "M0.1,0.1z m0.3,0.2l0.1-0.4z");
		Regex.checkRegex(SVGconverter.group(SVGconverter.fromMtoZ), "M0.1,0.1z m0.3,0.2l0.1-0.4z", 1, 1, "M0.1,0.1z");
		Regex.checkRegex(SVGconverter.group(SVGconverter.fromMtoZ), "M0.1,0.1z m0.3,0.2l0.1-0.4z", 1, 2, "0.1,0.1");
		Regex.checkRegex(SVGconverter.group(SVGconverter.fromMtoZ), "M0.1,0.1z m0.3,0.2l0.1-0.4z", 2, 1, "m0.3,0.2l0.1-0.4z");
		
		assertTrue(SVGconverter.svgFix2("M0.1,0.1z m0.3,0.2l0.1-0.4z").equals("M0.1,0.1L 0.1,0.1m0.3,0.2l0.1-0.4L 0.3,0.2"));		
		assertTrue(SVGconverter.svgFix3("<path c=\"bla\" d=\"M0.1,0.1z\" e=\"bla\">")
			.equals("<path c=\"bla\" d=\"M0.1,0.1L 0.1,0.1\" e=\"bla\">"));
		assertTrue(SVGconverter.svgFix3("aa<path c=\"bla\" d=\"M0.1,0.1z\" e=\"bla\"> " +
				"some other code <path c=\"bla\" d=\"M0.1,0.1z m0.3,0.2l0.1-0.4z\" e=\"bla\">aa")
			.equals("aa<path c=\"bla\" d=\"M0.1,0.1L 0.1,0.1\" e=\"bla\"> " +
				"some other code <path c=\"bla\" d=\"M0.1,0.1L 0.1,0.1m0.3,0.2l0.1-0.4L 0.3,0.2\" e=\"bla\">aa"));
		
		assertTrue(SVGconverter.svgPathFix_mToMall("<path d=\"M0.1,0.1z m0.3,0.3 z\">").equals("<path d=\"M0.1,0.1zM 0.4,0.4 l 0,0  z\">"));
		assertTrue(SVGconverter.svgPathFix_mToMall("<path d=\"m 0.1,0.1zM0.1-0.2 z\">").equals("<path d=\"m 0.1,0.1zM0.1-0.2 z\">"));
		assertTrue(SVGconverter.svgPathFix_mToMall("<path d=\"m-0.1e-3-0.1e3zM0.1-0.2 z\">").equals("<path d=\"m-0.1e-3-0.1e3zM0.1-0.2 z\">"));
		assertTrue(SVGconverter.svgPathFix_mToMall("<path d=\"m-0.1-0.1zm0.2-0.2 z\">").equals("<path d=\"m-0.1-0.1zM 0.1,-0.3 l 0,0  z\">"));
		assertTrue(SVGconverter.svgPathFix_mToMall("aa<path d=\"M0.1,0.1z m0.3,0.3 z\">aa<path d=\"M0.1,0.1z m0.3,0.3 z\">aa")
			.equals("aa<path d=\"M0.1,0.1zM 0.4,0.4 l 0,0  z\">aa<path d=\"M0.1,0.1zM 0.4,0.4 l 0,0  z\">aa"));
		
		assertTrue(SVGconverter.svgPathFix_mToM("M 10,10 l 0,10 10,0 0,-10 z m 0,10 0,10 10,0 0,-10 z")
			.equals("M 10,10 l 0,10 10,0 0,-10 zM 10.0,20.0 l 0,0  0,10 10,0 0,-10 z"));
	}
	
}
