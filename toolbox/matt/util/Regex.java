package matt.util;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

	public static String runRegex(String pattern, String text, int numberOfFindRuns, int groupIndex) throws NoSuchElementException {
		assert(numberOfFindRuns > 0 && groupIndex >= 0);
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		while (numberOfFindRuns-- > 0) {
			if (!m.find()) {
				throw new NoSuchElementException("not found");
			}
		}
		if (m.groupCount() >= groupIndex) {
			StringBuffer sb = new StringBuffer("bla");
			return m.group(groupIndex);
		} else {
			throw new NoSuchElementException("no such group");
		}
	}
	
	public static void checkRegexFail(String pattern, String text, int numberOfFindRuns, int groupIndex) {
		try {
			String result = checkRegex(pattern, text, numberOfFindRuns, groupIndex, "");
			throw new AssertionError("Expected failure, but got: "+result);
		} catch (NoSuchElementException e) {
			return;
		}
	}
	
	public static String checkRegex(String pattern, String text, int numberOfFindRuns, int groupIndex, String expected) throws NoSuchElementException {
		String actualResult = Regex.runRegex(pattern, text, numberOfFindRuns, groupIndex);
		if (!actualResult.equals(expected)) {
			throw new AssertionError("Not matching!\n\tExpected result:\n"+expected+"\n\tActual:\n"+actualResult+"\n\tfor text:\n"+text);
		}
		return actualResult;
	}
	
	public static String regexReplace(String pattern, String text, String replacement) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		return m.replaceAll(replacement);
	}
	
	public static String checkRegexReplace(String pattern, String text, String replacement, String expected) throws NoSuchElementException {
		String actualResult = Regex.regexReplace(pattern, text, replacement);
		if (!actualResult.equals(expected)) {
			throw new AssertionError("Not matching!\n\tExpected result:\n"+expected+"\n\tActual:\n"+actualResult+"\n\tfor text:\n"+text);
		}
		return actualResult;
	}

}
