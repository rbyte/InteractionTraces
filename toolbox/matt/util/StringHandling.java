package matt.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.assertTrue;

public class StringHandling {
	
	public static String concat(String elem, int times) {
		return concat(elem, times, "");
	}
	
	public static String concat(String elem, int times, String separator) {
		return concat(elem, times, separator, "");
	}
	
	public static String concat(String elem, int times, String separator, String elementSurrounder) {
		return concat(elem, times, separator, elementSurrounder, elementSurrounder);
	}
	
	public static String concat(String elem, int times, String separator,
			String elementSurrounderBefore, String elementSurrounderAfter) {
		assert times >= 0;
		String[] array = new String[times];
		Arrays.fill(array, elem);
		return concat(array, separator, elementSurrounderBefore, elementSurrounderAfter);
	}
	
	public static String concat(Object[] array) {
		return concat(array, "", "", "");
	}
	
	public static String concat(Object[] array, String separator) {
		return concat(array, separator, "", "");
	}
	
	public static String concat(Object[] array, String separator, String elementSurrounder) {
		return concat(array, separator, elementSurrounder, elementSurrounder);
	}
	
	public static String cutOut(String text, String start, String end) {
		int mStart = text.indexOf(start);
		int mEnd = text.indexOf(end, mStart);
		return mStart != -1 && mEnd != -1
			? text.substring(0, mStart)+text.substring(mEnd+end.length(), text.length())
			: text;
	}
	
	public static String concat(Object[] array, String separator,
			String elementSurrounderBefore, String elementSurrounderAfter) {
		String result = "";
		if (array.length > 0) {
			result += elementSurrounderBefore+array[0]+elementSurrounderAfter;
		}
		for (int i = 1; i < array.length; i++) {
			result += separator+elementSurrounderBefore+array[i]+elementSurrounderAfter;
		}
		return result;
	}
	
	public static String wrap(String in, int charCount) {
		assertTrue(charCount >= 1);
		StringBuffer sb = new StringBuffer(in);
		int times = 0;
		int pos;
		while ((pos = times+charCount*(1+times++)) < sb.length())
			sb.insert(pos, '\n');
		return sb.toString();
	}

	public static String readFileAsString(File file) throws IOException {
		byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(file.getPath()));
	        f.read(buffer);
	    } finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    return new String(buffer);
	}

	public static void writeStringToFile(File fileToWriteTo, String content, boolean deleteExisting) throws IOException {
		if (!fileToWriteTo.exists()) {
			fileToWriteTo.createNewFile();
		} else {
			if (deleteExisting) {
				fileToWriteTo.delete();
			} else {
				throw new RuntimeException("File already existing.");
			}
			try {
				fileToWriteTo.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		FileWriter fstream = new FileWriter(fileToWriteTo);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(content);
		out.close();
		fstream.close();
	}

	public static String getFileNameWithoutExtension(File file) {
		return getFileNameWithoutExtension(file.getName());
	}
	
	public static String getFileNameWithoutExtension(String filename) {
		try {
			return Regex.runRegex("(.*)\\.[^.]*", filename, 1, 1);
		} catch (Exception e) {
			System.err.println("getFileNameWithoutExtension: no extension found, using full name.");
			return filename;
		}
	}
	
	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}

	public static String getFileExtension(String filename) {
		try {
			return Regex.runRegex(".*\\.([^.]*)", filename, 1, 1);
		} catch (Exception e) {
			System.err.println("getFileNameWithoutExtension: no extension found, using full name.");
			return filename;
		}
	}
	
	@Deprecated
	public static File subdir(File file, String subdirectory) {
		return new File(file.getParent()+"\\"+subdirectory+"\\"+file.getName());
	}
	
	public static File appendToFileName(File file, String appendix) {
		return appendToFileName(file, appendix, true);
	}
	
	public static File appendToFileName(File file, String appendix, boolean keepExtension) {
		return new File(file.getParent() + "/" + appendToFileName(file.getName(), appendix, keepExtension));
	}
	
	public static String appendToFileName(String filename, String appendix, boolean keepExtension) {
		String result = getFileNameWithoutExtension(filename) + appendix;
		result += keepExtension ? "."+getFileExtension(filename) : "";
		return result;
	}
	
	public static Float[][] parse(String[][] mat) {
		Float[][] result = new Float[mat.length][];
		int i=0,k=0;
		for (String[] strings : mat) {
			result[i] = new Float[strings.length];
			for (String string : strings) {
				result[i][k++] = Float.parseFloat(string);
			}
			i++; k = 0;
		}
		return result;
	}
	
	public static String[][] readCSV(File csvFile) throws IOException {
		return readCSV(csvFile, false, -1, false, false);
	}
	
	public static String[][] readCSV(File csvFile, boolean restrictLines, int lines) throws IOException {
		return readCSV(csvFile, restrictLines, lines, false, false);
	}
	
	public static String[][] readCSV(File csvFile, boolean ignoreFirstRow, boolean ignoreFirstColumn) throws IOException {
		return readCSV(csvFile, false, -1, ignoreFirstRow, ignoreFirstColumn);
	}
	
	public static String[][] readCSV(File csvFile, boolean restrictLines, int lines, boolean ignoreFirstRow,
			boolean ignoreFirstColumn) throws IOException {
		String line;
	    BufferedReader input;
	    ArrayList<String[]> result = new ArrayList<String[]>();
		input = new BufferedReader(new FileReader(csvFile));
		int rowNo = 0;
	    while ((line = input.readLine()) != null && (!restrictLines || lines-- > 0)) {
	    	if (ignoreFirstRow && rowNo++ == 0) {
	    		// continue;
	    	} else {
		    	String[] split = line.split(",");
		    	int columnShift = ignoreFirstColumn ? 1 : 0;
		    	String[] row = new String[split.length-columnShift];
		    	for (int i = 0; i < split.length-columnShift; i++) {
		    		row[i] = split[i+columnShift];
		    	}
		    	result.add(row);
	    	}
	    }
	    input.close();
		return result.toArray(new String[0][0]);
	}
}
