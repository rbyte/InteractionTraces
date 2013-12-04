package matt.java;

import java.io.File;
import java.net.URISyntaxException;

public class GetCurrentDirectory {
	
	public static void main(String[] args) {
		// 1
		System.out.println(System.getProperty("user.dir"));
		// 2
		File file2 = new File(".");
		System.out.println(file2.getAbsolutePath());
		// 3
		try {
			File file = new File(Thread.currentThread().getContextClassLoader().getResource(".").toURI());
			System.out.println(file);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
