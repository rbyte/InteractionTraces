package matt.util;

import org.junit.Test;
import matt.util.Regex;
import static org.junit.Assert.*;

public class RegexTest {

	@Test
	public void run() {
		Regex.checkRegex("\"cursor\": (\".*\")", "\"cursor\": \"eNq1kt1u\",", 1, 1, "\"eNq1kt1u\"");
		Regex.checkRegex("\"cursor\": (.*),", "\"cursor\": \"eNq1kt1u\",", 1, 1, "\"eNq1kt1u\"");
		Regex.checkRegex("(.*)\\.[^.]*", "ding...json", 1, 1, "ding..");
		Regex.checkRegex("<([^<>]*)>", "<ab><cd><ef>", 2, 1, "cd");
		Regex.checkRegex("( )", "a b", 1, 1, " ");
		
		Regex.checkRegexReplace("a", "bab", "c", "bcb");
		Regex.checkRegexReplace("a", "baba", "c", "bcbc");
		Regex.checkRegexReplace("a", "bab", "c", "bcb");
		
		assertTrue("q<a bla></a>r".replaceAll("<a.*</a>", "").equals("qr"));
	}
	
}
