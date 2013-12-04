package matt.util;

import java.io.File;
import static org.junit.Assert.*;

public class BatchRenamer {
	
	public static void main(String[] args) {
		String basePath = "c:\\Matt\\workspace\\MattsProjectCalgary2011_12\\video\\2\\out\\";
		String ext = ".tif";
		int numberAdd = 610;
		
		File[] files = Util.getAllFilesLexicographicallySorted(basePath, "", ext);
		System.out.println(files.length);
		int last = Integer.MIN_VALUE;
		int contNumbering = 1;
		for (File f : files) {
			try {
				System.out.println(f);
				int current = Integer.valueOf(StringHandling.getFileNameWithoutExtension(f));
				assertTrue(last <= current);
				assertTrue(ext.substring(1).equals(StringHandling.getFileExtension(f)));
				f.renameTo(new File(basePath+"r"+(current+numberAdd)+ext));
				last = current;
			} catch (NumberFormatException e) {
				f.renameTo(new File(basePath+"r"+(contNumbering++)+ext));
				System.out.println("NumberFormatException");
			}
		}
	}

}
