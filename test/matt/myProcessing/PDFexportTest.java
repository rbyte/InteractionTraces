package matt.myProcessing;

import matt.parameters.Params;
import processing.core.PApplet;
import processing.pdf.*;

@SuppressWarnings("unused")
public class PDFexportTest extends PApplet {

	private static final long serialVersionUID = 8554791615408968512L;

	public void setup() {
		size(400, 400, PDF, Params.pathToProcessing + "filename.pdf");
	}

	public void draw() {
		
		line(0, 0, width / 2, height);
		
		println("Finished.");
		exit();
	}

}
