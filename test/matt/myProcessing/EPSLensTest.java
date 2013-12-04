package matt.myProcessing;

import java.util.Vector;

import matt.ui.PAppletPlus;

import ca.ucalgary.cpsc.innovis.eps.EpsLens;
import ca.ucalgary.cpsc.innovis.eps.EpsLensSurround;
import ca.ucalgary.cpsc.innovis.eps.EpsVector2D;

public class EPSLensTest extends PAppletPlus {
	
	private static final long serialVersionUID = 8890928091463998582L;
	
	public void drawPlus() {
		drawEpsLens();
	}
	
	public void drawEpsLens() {
		int nrOfShapes = 500;
		Vector<Shape> shapeVector = new Vector<Shape>(nrOfShapes);
		for (int i = 0; i < (int) Math.sqrt(nrOfShapes); i++) {
			for (int j = 0; j < (int) Math.sqrt(nrOfShapes); j++) {
				int w = 20;
				int margin = 10;
				int startPointX = (width - (w + margin)* (int) Math.sqrt(nrOfShapes)) / 2;
				int startPointY = (height - (w + margin)* (int) Math.sqrt(nrOfShapes)) / 2;
				Shape s = new Shape(startPointX + (i * (w + margin)),startPointY + (j * (w + margin)), w, w);
				shapeVector.add(s);
			}
		}
		
		EpsLens lens = new EpsLens();
		@SuppressWarnings("unused")
		EpsLensSurround sur = new EpsLensSurround(); //properties of distortion area
		// TODO play with args
		background(230);
		lens.setCenter(new EpsVector2D(mouseX, mouseY));
		
		float[] in = new float[2];
		float[] out = new float[2];
		float epsMag;
		
		for (int i = 0; i < shapeVector.size(); i++) {
			Shape s = (Shape) (shapeVector.elementAt(i));
			in[0] = s.getPositionX();
			in[1] = s.getPositionY();
			lens.setMagnification(2f);
			epsMag = lens.magnify2D(in, out);
			
			s.sWidth *= (float) Math.pow(epsMag, 1.5d);
			s.setPosition(out[0], out[1]);
			s.setShapeColor(this.color(255, 0, 0));
			s.drawShape();
		}
	}
	
	class Shape {
		float xPos = 0;
		float yPos = 0;
		public float sWidth = 0;

		int sColor = color(255);
		
		public Shape(float xPosition, float yPosition, float shapeWidth, float shapeHeight) {
			xPos = xPosition;
			yPos = yPosition;
			sWidth = shapeWidth;
		}
		
		// any primitives can be drawn here using xPos, yPos,
		// sWidth, sHeight
		void drawShape() {
			ellipse(xPos, yPos, sWidth, sWidth);

		}

		void setShapeColor(int shapeColor) {
			sColor = shapeColor;
		}

		int getShapeColor() {
			return sColor;
		}

		void setPosition(float x, float y) {
			xPos = x;
			yPos = y;
		}

		float getPositionX() {
			return xPos;
		}

		float getPositionY() {
			return yPos;
		}

	}
}


