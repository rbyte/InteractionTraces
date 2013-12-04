package matt.ui;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import matt.util.Circle;
import matt.util.PolarPoint;
import matt.util.Square;
import matt.util.Util;
import static org.junit.Assert.*;

public class PToolbox {
	
	public static enum ColourMergeMethod {overlay, mask, merge}
	
	public static Color convertRGBtoJavaHSL(int r, int g, int b) {
		return convertRGBtoJavaHSL(new Color(r, g, b));
	}
	
	public static Color convertRGBtoJavaHSL(Color c1n) {
		// inkscape hsl != java hsl
		float[] hsbvals = null;
		hsbvals = Color.RGBtoHSB(c1n.getRed(), c1n.getGreen(), c1n.getBlue(), hsbvals);
		System.out.printf("hsb 0-1: %f, %f, %f\n", hsbvals[0], hsbvals[1], hsbvals[2]);
		System.out.printf("hsb 0-255: %.0f, %.0f, %.0f\n", hsbvals[0]*255, hsbvals[1]*255, hsbvals[2]*255);
		return Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
	}
	
	public static Color setAlpha(Color c1n, int alpha) {
		assertTrue(0 <= alpha && alpha <= 255);
		return new Color(c1n.getRed(), c1n.getGreen(), c1n.getBlue(), alpha);
	}
	
	public static Color setAlphaMultiply(Color c1n, float factor) {
		assertTrue(0 <= factor);
		int newAlpha = (int) (c1n.getAlpha()*factor);
		return new Color(c1n.getRed(), c1n.getGreen(), c1n.getBlue(), newAlpha > 255 ? 255 : newAlpha);
	}
	
	public static Color setSaturation(Color c1n, float saturation) {
		assertTrue(0 <= saturation && saturation <= 1);
		float[] hsbvals = null;
		hsbvals = Color.RGBtoHSB(c1n.getRed(), c1n.getGreen(), c1n.getBlue(), hsbvals);
		return Color.getHSBColor(hsbvals[0], saturation, hsbvals[2]);
	}
	
	public static Color setSaturationMultiply(Color c1n, float saturation) {
		assertTrue(0 <= saturation);
		float[] hsbvals = null;
		hsbvals = Color.RGBtoHSB(c1n.getRed(), c1n.getGreen(), c1n.getBlue(), hsbvals);
		Color hsbC = Color.getHSBColor(hsbvals[0], (float) Util.percentiseIn(hsbvals[1]*saturation), hsbvals[2]);
		return setAlpha(hsbC, c1n.getAlpha());
//		return Color.getHSBColor(hsbvals[0], hsbvals[1]*saturation > 1 ? 1 : hsbvals[1]*saturation, hsbvals[2]);
	}
	
	public static Color setLightness(Color c1n, float lightness) {
		assertTrue(0 <= lightness && lightness <= 1);
		float[] hsbvals = null;
		hsbvals = Color.RGBtoHSB(c1n.getRed(), c1n.getGreen(), c1n.getBlue(), hsbvals);
		return Color.getHSBColor(hsbvals[0], hsbvals[1], lightness);
	}
	
	public static Color setLightnessMultiply(Color c1n, float lightness) {
		assertTrue( 0 <= lightness);
		float[] hsbvals = null;
		hsbvals = Color.RGBtoHSB(c1n.getRed(), c1n.getGreen(), c1n.getBlue(), hsbvals);
		Color hsbC = Color.getHSBColor(hsbvals[0], hsbvals[1], (float) Util.percentiseIn(hsbvals[2]*lightness));
		return setAlpha(hsbC, c1n.getAlpha());
//		return Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]*lightness > 1 ? 1 : hsbvals[2]*lightness);
	}
	
	public static int mingleColours(int colour1, int colour2, double gradient) {
		return mingleColours(new Color(colour1, true), new Color(colour2, true), gradient).getRGB();
	}
	
	public static Color mingleColours(Color colour1, Color colour2, double gradient) {
		return mingleColours(colour1, colour2, gradient, ColourMergeMethod.merge);
	}
	
	public static int mingleColours(int colour1, int colour2, ColourMergeMethod mergeMethod) {
		return mingleColours(new Color(colour1, true), new Color(colour2, true), mergeMethod).getRGB();
	}
	
	public static Color mingleColours(Color colour1, Color colour2, ColourMergeMethod mergeMethod) {
		assertTrue( mergeMethod == ColourMergeMethod.mask || mergeMethod == ColourMergeMethod.overlay);
		return mingleColours(colour1, colour2, 0, mergeMethod);
	}
	
	public static int mingleColours(int colour1, int colour2, double gradient, ColourMergeMethod mergeMethod) {
		return mingleColours(new Color(colour1, true), new Color(colour2, true), gradient, mergeMethod).getRGB();
	}
	
	public static Color mingleColours(Color c1, Color c2, double gradient, ColourMergeMethod mergeMethod) {
		assertTrue( 0 <= gradient && gradient <= 1);
		
		switch (mergeMethod) {
		case mask:
			return new Color(
				c1.getRed(),
				c1.getGreen(),
				c1.getBlue(),
				c1.getAlpha()-c2.getBlue() < 0 ? 0 : c1.getAlpha()-c2.getBlue()); // use blue as Alpha
		case overlay:
			return new Color(
				Util.linearInterpolation(c1.getRed(), c2.getRed(), c2.getAlpha()/255d),
				Util.linearInterpolation(c1.getGreen(), c2.getGreen(), c2.getAlpha()/255d),
				Util.linearInterpolation(c1.getBlue(), c2.getBlue(), c2.getAlpha()/255d),
				c1.getAlpha()+c2.getAlpha() > 255 ? 255 : c1.getAlpha()+c2.getAlpha());
		case merge:
			return Util.linearInterpolation(c1, c2, gradient);
		default:
			throw new AssertionError("Fell through switch case.");
		}
	}
	
	public static Circle[] getPhyllotacticLayout(Rectangle2D.Float space, int n) {
		return getPhyllotacticLayout(Square.getInnerSquare(space, 0), n);
	}
	
	public static Circle[] getPhyllotacticLayout(Square space, int n) {
		double alpha = 137.5d/360d*2*Math.PI;
		// r < Min(width, height)/2 and r=c*sqrt(n)
		float c = (float) ((float) space.getSize()/(2*Math.sqrt(n)));
		
		Circle[] result = new Circle[n];
		for (int i=0; i<n; i++) {
			result[i] = new Circle(new PolarPoint(i*alpha, c*Math.sqrt(i)), c);
			if (i==0) // shift the center point slightly to the right
				result[i].getCentre().setXadd(c*2f/3f);
			result[i].getCentre().setXYadd(space.getCenterX(), space.getCenterY());
		}
		
		return result;
	}
	
}
