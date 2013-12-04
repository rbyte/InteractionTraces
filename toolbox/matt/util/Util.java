package matt.util;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import matt.parameters.Params;

import org.contract4j5.contract.Post;
import org.jbox2d.common.Vec2;

public class Util {
	
	private enum SortOrder {none, byModificationDate, byFileName}
	
	public static File[] getAllFiles(String basePath, String prefix, String suffix) {
		return getAllFiles(basePath, prefix, suffix, SortOrder.none);
	}
	
	public static File[] getAllFilesSortedByModificationDate(String basePath, String prefix, String suffix) {
		return getAllFiles(basePath, prefix, suffix, SortOrder.byModificationDate);
	}
	
	public static File[] getAllFilesLexicographicallySorted(String basePath, String prefix, String suffix) {
		return getAllFiles(basePath, prefix, suffix, SortOrder.byFileName);
	}
	
	private static File[] getAllFiles(String basePath, String prefix, String suffix, SortOrder sortOrder) {
		File file = new File(basePath);
		if (!file.exists()) {
			throw new RuntimeException("basePath not found.");
		}
		File[] files = file.listFiles();

		ArrayList<File> result = new ArrayList<File>();
		for (File f : files) {
			if (f.isFile() && f.getName().startsWith(prefix) && f.getName().endsWith(suffix)) {
				result.add(f);
			}
		}
		
		switch (sortOrder) {
		case none: break;
		case byModificationDate:
			Collections.sort(result, new Comparator<File>() {
				@Override public int compare(File o1, File o2) {
					return new Long(o1.lastModified()).compareTo(new Long(o2.lastModified()));
				}
			});
			break;
		case byFileName:
			Collections.sort(result, new Comparator<File>() {
				@Override public int compare(File o1, File o2) {
					try {
						// TODO not a beautiful solution. consider using an alphanum algorithm
						return new Integer(Integer.parseInt(StringHandling.getFileNameWithoutExtension(o1)))
						.compareTo(Integer.parseInt(StringHandling.getFileNameWithoutExtension(o2)));
					} catch (Exception e) {
						return o1.getName().compareTo(o2.getName());
					}
				}
			});
			break;
		default: break;
		}
		
		return result.toArray(new File[] {});
	}
	
	public static String runProgram(String[] command) {
		String result = "";
		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(command);
			
//			OutputStream out = p.getOutputStream();
//			InputStream err = p.getErrorStream();
			String line;
		    BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));
		    
		    while ((line = input.readLine()) != null) {
		    	result += line;
		    }
		    input.close();
			
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Deprecated
	public static String runProgram(String command) {
		String result = "";
		try {
			Runtime rt = Runtime.getRuntime();
			System.out.println("running: "+command);
			Process p = rt.exec(command);
			
//			OutputStream out = p.getOutputStream();
//			InputStream err = p.getErrorStream();
			String line;
		    BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));
		    
		    while ((line = input.readLine()) != null) {
		    	result += line;
		    }
		    input.close();
			
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void writeCSV(File csvFile, Object[][] mat) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));
		for (int i=0; i<mat.length; i++) {
			bufferedWriter.write(matt.util.StringHandling.concat(mat[i], ","));
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
	}
	
	public static int[] convert(Integer[] arr) {
		int[] ret = new int[arr.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = arr[i];
		return ret;
	}
	
	public static float linearInterpolation(double d, double e, double roundPercentage) {
		return linearInterpolation((float) d, (float) e, (float) roundPercentage);
	}

	public static float linearInterpolation(float start, float stop, float amt) {
		return start + (stop - start) * amt;
	}
	
	public static Vec2 linearInterpolation(Vec2 start, Vec2 stop, float amt) {
		return new Vec2(
			linearInterpolation(start.x, stop.x, amt),
			linearInterpolation(start.y, stop.y, amt));
	}

	public static int linearInterpolation(int start, int stop, double amt) {
		return (int) (start + (stop - start) * amt);
	}
	
	public static Color linearInterpolation(Color c1, Color c2, double amt) {
		assertTrue(0 <= amt && amt <= 1);
		return new Color(
			linearInterpolation(c1.getRed(), c2.getRed(), amt),
			linearInterpolation(c1.getGreen(), c2.getGreen(), amt),
			linearInterpolation(c1.getBlue(), c2.getBlue(), amt),
			linearInterpolation(c1.getAlpha(), c2.getAlpha(), amt));
	}
	
	/**
	 * enlargeRectangle((0, 0, 15, 10), 0.5) -> (-2.5, -2.5, 20, 15)
	 * @param rect
	 * @param -1 < factor < 1
	 * @return
	 */
	public static Rectangle2D.Float resizeRectangle(Rectangle2D.Float rect, float factor) {
		assertTrue(-1 < factor);
		Rectangle2D.Float result = (Rectangle2D.Float) rect.clone();
		float borderDelta = Math.min(result.width, result.height) * factor;
		result.x -= borderDelta/2f;
		result.y -= borderDelta/2f;
		result.width += borderDelta;
		result.height += borderDelta;
		return result;
	}
	
	/**
	 * cap in [0,1]
	 * @return in [0,1] (cap to bounds if beyond)
	 */
	@Post(Params.returnIn0to1) public static double percentiseIn(double value) {
		return percentiseIn(value, 0, 1);
	}

	/**
	 * value/high
	 * @return in [0,1] (cap to bounds if beyond)
	 * @param high > 0
	 */
	@Post(Params.returnIn0to1) public static double percentiseIn(double value, double high) {
		return percentiseIn(value, 0, high);
	}
	
	/**
	 * (value-low)/(high-low)
	 * @return in [0,1] (cap to bounds if beyond)
	 */
	@Post(Params.returnIn0to1) public static double percentiseIn(double value, double low, double high) {
		assertTrue(low < high);
		return low < value && value < high
				? (value-low)/(high-low)
				: low >= value ? 0 : 1;
	}
	
	public static float max(float...fs) {
		float result = Float.NEGATIVE_INFINITY;
		for (float f : fs)
			result = f > result ? f : result;
		return result;
	}
	
}
