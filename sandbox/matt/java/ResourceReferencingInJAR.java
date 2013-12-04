package matt.java;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import matt.util.StringHandling;

public class ResourceReferencingInJAR {
	
	public static void main(String[] args) {
		
		// http://stackoverflow.com/questions/3447258/get-file-from-jar
		
		URL url = ClassLoader.getSystemResource("matt/sandbox/bla.txt");
		if (url == null) {
			System.out.println("null");
		} else {
			// this will not work when executes from a jar file!!!
			System.out.println("success"+url.getFile());
			File file = new File(url.getFile());
			try {
				System.out.println(StringHandling.readFileAsString(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

}
