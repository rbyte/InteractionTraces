package matt.setup;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matt.parameters.Params;
import matt.util.Regex;
import matt.util.StringHandling;
import matt.util.Util;

public class SVGconverter {
	
	// http://docs.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html
	public static final String floatPattern = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	// numbers may be separated without the comma and whitespace, if they are negative: e.g. -3.3-2.5
	public static final String numGroupOf2 = floatPattern+",?"+floatPattern;
	// (?:...) is a non-capturing group
	// has all the command symbols of an SVG path: http://www.w3.org/TR/SVG/paths.html
	public static final String numGroupsOf2concat = "(?: ?[cCvVsShHlLqQtTaA]? ?"+numGroupOf2+")*";
	// groups the content of d=
	public static final String pathPatternOnly = "<path[^>]* d=\"([^\">]*)\"[^>]*>";
	public static final String fromMtoZ = "[mM] ?("+numGroupOf2+")"+numGroupsOf2concat+" ?z";
	public static final String fromMtoZsimplified = "[mM] ?("+numGroupOf2+")[^\">zZ]*z";
	
	public static String group(String pattern) {
		return "("+pattern+")";
	}
	
	public static void main(String[] args) throws IOException {
		run("abcdefghijklmnopqrstuvwxyz");
		
		
//		File outFile = new File(Params.pathToTextShapes+"raw_3.svg");
//		File outFileFixed = StringHandling.appendToFileName(outFile, "_fix");
//		String outFileContent = StringHandling.readFileAsString(outFile);
//		outFileContent = StringHandling.cutOut(outFileContent, "<metadata", "</metadata>");
//		StringHandling.writeStringToFile(outFileFixed, svgPathFix_mToMall(outFileContent), true);
	}
	
	public static File run(String caption) throws IOException {
		return run(caption, Params.visualS.kTextFamily());
	}
	
	public static File run(String caption, String fontFamily) throws IOException {
		return run(Params.textTemplateSVG, caption, fontFamily);
	}
	
	private static File run(File textTemplateSVG, String caption, String fontFamily) throws IOException {
		String outFileDirPath = textTemplateSVG.getParent()+Params.dirDelimiter+fontFamily;
		
		File outFileDir = new File(outFileDirPath);
		if (!outFileDir.isDirectory())
			outFileDir.mkdir();
		// matches all (Unicode) characters that are neither letters nor numbers
		String captionForFileName = caption.replaceAll("[^a-zA-Z0-9 -]", "").replaceAll("  ", " ");
		
		File outFile = new File(outFileDirPath+Params.dirDelimiter
			+StringHandling.getFileNameWithoutExtension(textTemplateSVG)+"_inkOut_"+captionForFileName+".svg");
		if (!outFile.exists()) {
			String svgContent = StringHandling.readFileAsString(textTemplateSVG);
			svgContent = svgContent.replaceFirst("replace this!", captionForFileName);
			svgContent = svgContent.replaceFirst("fill:#000000;", "fill:#000000;");
			svgContent = svgContent.replaceFirst("font-family:Helvetica LT Std", "font-family:"+fontFamily);
			
			StringHandling.writeStringToFile(outFile, svgContent, true);
//			System.out.println(outFile.getAbsolutePath());
			Util.runProgram(new String[] {Params.pathToInkscape_executable, outFile.getAbsolutePath(), "--select=text3056",
					"--verb=ObjectToPath", "--verb=FitCanvasToSelection", "--verb=FileSave", "--verb=FileClose"});
		}
		
		/*
		 * NO FIXING ANYMORE:
		 * results in an ArrayOutOfBoundsExc in PShape implementation
		 * and the reason for fixing does not seem to exist anymore
		 */
		if (false) {
			
			File outFileFixed = new File(outFileDirPath+Params.dirDelimiter
					+StringHandling.getFileNameWithoutExtension(textTemplateSVG)+"_fix_"+captionForFileName+".svg");
			if (!outFileFixed.exists()) {
				String outFileContent = StringHandling.readFileAsString(outFile);
				outFileContent = StringHandling.cutOut(outFileContent, "<metadata", "</metadata>");
				StringHandling.writeStringToFile(outFileFixed, svgPathFix_mToMall(outFileContent), true);
			}
			assertTrue(outFileFixed.exists());
		}
		
		return outFile;
	}
	
	/**
	 * In all paths of the given SVG file content, replaces relative starting points indicated by "m"
	 * with absolute references "M". This is not changing the image semantics.
	 * 
	 * This is necessary for paths to work properly in processing.
	 * See http://code.google.com/p/processing/issues/detail?id=1058
	 * 
	 * @param svgContent
	 * @return modified svg content
	 */
	public static String svgPathFix_mToMall(String svgContent) {
		Pattern p = Pattern.compile(pathPatternOnly);
		Matcher m = p.matcher(svgContent);
		StringBuffer sb = new StringBuffer("");
		int lastEnd = 0;
		while (m.find()) {
			assertTrue(!m.group().equals(""));
			sb.append(svgContent.substring(lastEnd, m.start(1)));
			lastEnd = m.end(1);
//			System.out.println(m.group(1)+"@");
			sb.append(svgPathFix_mToM(m.group(1)));
		}
		sb.append(svgContent.substring(lastEnd, svgContent.length()));
		return sb.toString();
	}
	
	/**
	 * example: (draws 2 rectangles)
	 * 		assertTrue(SVGconverter.svgPathFix_mToM("M 10,10 l 0,10 10,0 0,-10 z m 0,10 0,10 10,0 0,-10 z")
			.equals("M 10,10 l 0,10 10,0 0,-10 zM 10.0,20.0 l 0,0  0,10 10,0 0,-10 z"));
	 */
	public static String svgPathFix_mToM(String insideD) {
		Pattern p = Pattern.compile(group(fromMtoZsimplified));
		Matcher m = p.matcher(insideD);
		StringBuffer sb = new StringBuffer("");
		Float mVal1 = Float.NaN, mVal2 = Float.NaN;
		try {
			// for intense svgs, throughs java.lang.StackOverflowError
		while (m.find()) {
			String mToZ = m.group();
//			assertTrue(!mToZ.equals("") && mToZ.matches(fromMtoZ));
//			System.out.println(m.group(0)+"@");
			if (Float.isNaN(mVal1) || Float.isNaN(mVal2) || mToZ.startsWith("M")) {
				// get absolute coordinates and store for later use
				mVal1 = Float.parseFloat(Regex.runRegex(group(floatPattern), mToZ, 1, 1));
				mVal2 = Float.parseFloat(Regex.runRegex(group(floatPattern), mToZ, 2, 1));
				sb.append(mToZ);
			} else {
				// creating absolute from relative position
				mVal1 += Float.parseFloat(Regex.runRegex(group(floatPattern), mToZ, 1, 1));
				mVal2 += Float.parseFloat(Regex.runRegex(group(floatPattern), mToZ, 2, 1));
				// adding relative line to same point to make following points being treated as relative
				sb.append(mToZ.replaceAll("m ?"+numGroupOf2, "M "+mVal1+","+mVal2+" l 0,0 "));
			}
		}
		} catch (StackOverflowError e) {
			System.err.println("svgPathFix_mToM threw a StackOverflowError");
		}
		return sb.toString();
	}
	
	@Deprecated
	// turns "z" to "L <path segment starting point>" 
	public static String svgFix3(String all) {
		Pattern p = Pattern.compile(pathPatternOnly);
		Matcher m = p.matcher(all);
		StringBuffer sb = new StringBuffer("");
		int lastEnd = 0;
		while (m.find()) {
			if (!m.group(0).equals("")) {
				sb.append(all.substring(lastEnd, m.start(1)));
				lastEnd = m.end(1);
//				System.out.println(m.group(1)+"@");
				sb.append(svgFix2(m.group(1)));
			} else {
				System.out.println("warning: it does!");
			}
		}
		sb.append(all.substring(lastEnd, all.length()));
		return sb.toString();
	}
	
	@Deprecated
	public static String svgFix2(String insideD) {
		Pattern p = Pattern.compile(group(fromMtoZ));
		Matcher m = p.matcher(insideD);
		StringBuffer sb = new StringBuffer("");
		while (m.find()) {
			if (!m.group(0).equals("")) {
//				System.out.println(m.group(0)+"@");
				String mToZ = m.group(0);
				assertTrue(mToZ.matches(fromMtoZ));
				String pos = Regex.runRegex(group(fromMtoZ), mToZ, 1, 2);
				sb.append(mToZ.replaceAll("z", "L "+pos));
			} else {
				System.out.println("warning: it does! (2)");
			}
		}
		return sb.toString();
	}
	
	

}
