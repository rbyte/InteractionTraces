package matt.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import matt.parameters.Params;
import matt.ui.PToolbox.ColourMergeMethod;
import matt.ui.screenElement.Book;
import matt.util.Circle;
import matt.util.PolarPoint;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import static org.junit.Assert.*;

public class PImagePlus extends PImage {
	
	// for a PImage p and the function p.filter(BLUR, x), maxBlur gives the maximum
	// amount of x according to the size of the image, in particular Math.min(p.width, p.height),
	// which is the index of the array. example: maxBlur(10) = 2 means that an image
	// where Math.min(p.width, p.height) = 10 can be blurred at most with p.filter(BLUR, 2)
	// otherwise, if x is set higher, an ArithmeticException is thrown
	private static final int[] maxBlur = {-1, -1, 0, 0, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5,
		5, 5, 5, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12,
		13, 13, 13, 13, 14, 14, 14, 15, 15, 15, 15, 16, 16, 16, 17, 17, 17, 17, 18, 18, 18, 19, 19,
		19, 19, 20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 23, 23, 23, 23, 24, 24, 24, 25, 25, 25, 25,
		26, 26, 26, 27, 27, 27, 27, 28};
	
	private PAppletPlus parentPlus;
	
	public PImagePlus(int width, int height, PAppletPlus parentPlus) {
		this(width, height, PConstants.ARGB, parentPlus);
	}
	
	public PImagePlus(int width, int height, PAppletPlus parentPlus, int colour) {
		this(width, height, PConstants.ARGB, parentPlus);
		for (int i=0; i<pixels.length; i++) {
			pixels[i] = colour;
		}
	}
	
	public PImagePlus(int width, int height, int format, PAppletPlus parentPlus) {
	    this.width = width;
	    this.height = height;
	    this.pixels = new int[width*height];
	    this.format = format;
	    this.parentPlus = parentPlus;
	    this.parent = parentPlus;
	}
	
	PImagePlus(PImage image, PAppletPlus parentPlus) {
//		this.format = image.format;
		this.format = PConstants.ARGB;
		this.height = image.height;
		this.parent = image.parent;
		this.pixels = image.pixels;
		this.width = image.width;
		this.parentPlus = parentPlus;
	}
	
	public PImagePlus blur(float blurPercentage) {
		assertTrue((0f <= blurPercentage && blurPercentage <= 1f));
		if (blurPercentage != 0)
			filter(PConstants.BLUR, getMaxBlurRadius()*blurPercentage);
		return this;
	}
	
	private int getMaxBlurRadius() {
		int minDim = Math.min(width, height);
		int maxBlurAdjusted = (int) (maxBlur.length*Params.maxBlurFactor);
		assertTrue(1 <= maxBlurAdjusted && maxBlurAdjusted < maxBlur.length);
		int result = minDim < maxBlurAdjusted ? maxBlur[minDim] : maxBlur[maxBlurAdjusted-1]; //(int) minDim/4;
		return result < 0 ? 0 : result;
	}
	
	public PImagePlus cloneAndResize(int wide, int high) {
		if (wide <= 0 && high <= 0) {
			return new PImagePlus(0, 0, parentPlus);
		}
		
		if (wide == 0) { // Use height to determine relative size
			float diff = (float) high / (float) height;
			wide = (int) (width * diff);
		} else if (high == 0) { // Use the width to determine relative size
			float diff = (float) wide / (float) width;
			high = (int) (height * diff);
		}
		PImagePlus temp = new PImagePlus(wide, high, parentPlus);
		temp.copy(this, 0, 0, width, height, 0, 0, wide, high);
		return temp;
	}

	void fastblur(int radius) {
		if (radius < 1) {
			return;
		}
		
		int w = width;
		int h = height;
		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, p1, p2, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];
		int vmax[] = new int[Math.max(w, h)];
		int dv[] = new int[256 * div];
		for (i = 0; i < 256 * div; i++) {
			dv[i] = (i / div);
		}
		
		yw = yi = 0;

		for (y = 0; y < h; y++) {
			rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pixels[yi + Math.min(wm, Math.max(i, 0))];
				rsum += (p & 0xff0000) >> 16;
				gsum += (p & 0x00ff00) >> 8;
				bsum += p & 0x0000ff;
			}
			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
					vmax[x] = Math.max(x - radius, 0);
				}
				p1 = pixels[yw + vmin[x]];
				p2 = pixels[yw + vmax[x]];

				rsum += ((p1 & 0xff0000) - (p2 & 0xff0000)) >> 16;
				gsum += ((p1 & 0x00ff00) - (p2 & 0x00ff00)) >> 8;
				bsum += (p1 & 0x0000ff) - (p2 & 0x0000ff);
				yi++;
			}
			yw += w;
		}

		for (x = 0; x < w; x++) {
			rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;
				rsum += r[yi];
				gsum += g[yi];
				bsum += b[yi];
				yp += w;
			}
			yi = x;
			for (y = 0; y < h; y++) {
				pixels[yi] = 0xff000000 | (dv[rsum] << 16) | (dv[gsum] << 8)
						| dv[bsum];
				if (x == 0) {
					vmin[y] = Math.min(y + radius + 1, hm) * w;
					vmax[y] = Math.max(y - radius, 0) * w;
				}
				p1 = x + vmin[y];
				p2 = x + vmax[y];

				rsum += r[p1] - r[p2];
				gsum += g[p1] - g[p2];
				bsum += b[p1] - b[p2];

				yi += w;
			}
		}
	}
	
	@SuppressWarnings("unused")
	private int[] getMaxBlurArray(int iterations) {
		int maxBlur[] = new int[iterations];
		// blurring of images of size 0x0 or 1x1 is not possible anyway
		maxBlur[0] = -1;
		maxBlur[1] = -1;
		for (int i = 2; i < iterations; i++) {
			for (int k = maxBlur[i-1]; k < iterations; k++) {
				try {
					new PImage(i, i).filter(PConstants.BLUR, k);
				} catch (ArithmeticException e) {
					maxBlur[i] = k-1;
					break;
				}
			}
		}
		return maxBlur;
	}
	
	public PImagePlus overlay(PImage img) {
		return overlay(img, 0, 0);
	}
	
	public PImagePlus overlay(PImage img, int x, int y) {
		return overlay(img, x, y, ColourMergeMethod.overlay);
	}
	
	public PImagePlus overlay(PImage img, ColourMergeMethod mergeMethod) {
		return overlay(img, 0, 0, mergeMethod);
	}
	
	public PImagePlus overlay(PImage img, int x, int y, ColourMergeMethod mergeMethod) {
		Rectangle2D.Float r1 = new Rectangle2D.Float(0, 0, width, height);
		Rectangle2D.Float r2 = new Rectangle2D.Float(x, y, img.width, img.height);
		Rectangle2D.Float intersectionRect = new Rectangle2D.Float();
		Rectangle2D.intersect(r1, r2, intersectionRect);
//		System.out.println(intersectionRect);
		
		for (int px=0; px<intersectionRect.width; px++) {
			for (int py=0; py<intersectionRect.height; py++) {
				int thisPos = (int) ((py+intersectionRect.y)*width+(px+intersectionRect.x));
				int overlayPos = (int) ((py+intersectionRect.y-r2.y)*img.width+(px+intersectionRect.x-r2.x));
					pixels[thisPos] = PToolbox.mingleColours(pixels[thisPos], img.pixels[overlayPos], mergeMethod);
			}
		}
		return this;
	}
	
	public PImagePlus roundEdges(float percentage) {
		return overlay(getRoundEdgesMask(percentage), ColourMergeMethod.mask);
	}
	
	public PImagePlus roundEdges(float roundPercentage, float shrinkPercentage) {
		return overlay(getRoundEdgesMask(roundPercentage, shrinkPercentage), ColourMergeMethod.mask);
	}
	
	public PImagePlus roundEdges(float percentage, Circle c) {
		return roundEdges(percentage, c.getCenterX(), c.getCenterY(), c.getRadius()*2);
	}
	
	public PImagePlus roundEdges(float percentage, float centerX, float centerY, float diameter) {
		assertTrue(new Rectangle(0, 0, width, height).contains(
			new Rectangle(
				(int) (centerX-diameter/2f),
				(int) (centerY-diameter/2f),
				(int) (diameter),
				(int) (diameter))));
		
		return overlay(getRoundEdgesMask(percentage, percentage, centerX, centerY, diameter), ColourMergeMethod.mask);
	}
	
	public PImage getRoundEdgesMask(float percentage) {
		return getRoundEdgesMask(percentage, percentage);
	}
	
	private PImage getRoundEdgesMask(float roundPercentage, float shrinkPercentage) {
		return getRoundEdgesMask(roundPercentage, shrinkPercentage, width/2f, height/2f, Math.min(width, height)-1);
	}
	
	/**
	 * Creates black and white image. The brighter, the more transparent.
	 */
	private PImage getRoundEdgesMask(float roundPercentage, float shrinkPercentage, float centerX, float centerY, float diameter) {
		assertTrue(0f <= roundPercentage && roundPercentage <= 1f);
		assertTrue(0f <= shrinkPercentage && shrinkPercentage <= 1f);
		
		PGraphics buf = parent.createGraphics(width, height, PConstants.JAVA2D);
		buf.beginDraw();
		if (roundPercentage == 0 && shrinkPercentage == 0) {
			buf.background(0, 255);
		} else {
			buf.background(255, 255);
			buf.fill(0, 255);
			buf.smooth();
			buf.noStroke();
			final float two = 2f;
			if (roundPercentage == 1 && shrinkPercentage == 1) {
				// optimised because faster + smoother edges due to avoiding the overlapping of 4 circles
				buf.ellipse(centerX, centerY, diameter, diameter);
			} else {
				float d = Math.min(height, width) * roundPercentage;
				float cx = 0, cy = 0;
				if (height > width) {
					cy = Math.abs(height-width) * shrinkPercentage;
				} else {
					cx = Math.abs(height-width) * shrinkPercentage;
				}
				buf.rect(cx/two, d/two+cy/two, buf.width-cx, buf.height-d-cy);
				buf.rect(d/two+cx/two, 0+cy/two, buf.width-d-cx, buf.height-cy);
				buf.ellipse(d/two+cx/two, d/two+cy/two, d, d);
				buf.ellipse(buf.width-d/two-cx/two, d/two+cy/two, d, d);
				buf.ellipse(d/two+cx/two, buf.height-d/two-cy/two, d, d);
				buf.ellipse(buf.width-d/two-cx/two, buf.height-d/two-cy/two, d, d);
			}
		}
		buf.endDraw();
		
		return buf;
	}
	
	public PImage desaturate(float percentage) {
		assertTrue(0 <= percentage && percentage <= 1);
		for (int i = 0; i<pixels.length; i++) {
			float[] cacheHsbValue = Color.RGBtoHSB(pixels[i] >> 16 & 0xFF, pixels[i] >> 8 & 0xFF, pixels[i] & 0xFF, null);
			Color c = new Color(Color.HSBtoRGB(cacheHsbValue[0], cacheHsbValue[1]*percentage, cacheHsbValue[2]));
			pixels[i] = new Color(c.getRed(), c.getGreen(), c.getBlue(), pixels[i] >> 24 & 0xFF).getRGB();
		}
		return this;
	}
	
	public PImage setSaturationLightnessAlphaMultiply(float saturation, float lightness, float alpha) {
		assertTrue(0 <= saturation && saturation <= 1);
		assertTrue(0 <= lightness && lightness <= 1);
		for (int i = 0; i<pixels.length; i++) {
			float[] cacheHsbValue = Color.RGBtoHSB(pixels[i] >> 16 & 0xFF, pixels[i] >> 8 & 0xFF, pixels[i] & 0xFF, null);
			Color c = new Color(Color.HSBtoRGB(cacheHsbValue[0], cacheHsbValue[1]*saturation, cacheHsbValue[2]*lightness));
			pixels[i] = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) ((pixels[i] >> 24 & 0xFF)*alpha)).getRGB();
		}
		return this;
	}
	
	public PImage addTransparency(float percentage) {
		assertTrue(0 <= percentage && percentage <= 1);
		for (int i = 0; i<pixels.length; i++) {
			int alpha = pixels[i] >> 24 & 0xFF;
			alpha *= percentage;
			
			pixels[i] = pixels[i] & 0x00_FF_FF_FF | (alpha << 24);
		}
		return this;
	}
	
	public PImage colour(Color colour) {
		for (int i = 0; i<pixels.length; i++) {
			int alpha = pixels[i] >> 24 & 0xFF;
			alpha *= colour.getAlpha()/255f;
			pixels[i] = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha).getRGB();
		}
		return this;
	}
	
	/**
	 * If this image is a mask, controls the opacity of that mask.
	 * Value is in between 0 and 1.
	 * Returns this altered image.
	 */
	public PImagePlus setLowKeyTo(float value) {
		assertTrue( 0 <= value && value <= 1);
		if (value != 0)
			for (int i = 0; i<pixels.length; i++) {
				pixels[i] = new Color(
					pixels[i] >> 16 & 0xFF,
					pixels[i] >> 8 & 0xFF,
					pixels[i] & 0xFF,
					(int) ((pixels[i] >> 24 & 0xFF)*(1f-value)) ).getRGB();
			}
		return this;
	}
	
	public PImagePlus cloneAndCropInCentre(int a) {
		return cloneAndCropInCentre(a, a);
	}
	
	public PImagePlus cloneAndCropInCentre(int width, int height) {
		return cloneAndCrop(new Rectangle((this.width-width)/2, (this.height-height)/2, width, height));
	}
	
	public PImagePlus cloneAndCrop(final Rectangle rect) {
		Rectangle r = new Rectangle(0, 0, width, height).intersection(rect);
		// see also:
		// get() is doing the same trick
		assert 0 <= r.x && r.x < width
			&& 0 <= r.y && r.y < height
			&& 0 < r.width && r.width <= width
			&& 0 < r.height && r.height <= height;
		PImagePlus result = new PImagePlus(r.width, r.height, parentPlus);
		// pixels[1] is at (x=0,y=1)
		for (int y=0; y<r.height; y++)
			for (int x=0; x<r.width; x++)
				result.pixels[y*r.width+x] = pixels[(r.y+y)*width+(r.x+x)];
		
		return result;
	}
	
	public PImagePlus clonePlus() {
		PImagePlus clonedImage = null;
		try {
//			PImagePlus img = parentPlus.createImage(width, height, ARGB);
			clonedImage = new PImagePlus((PImage) this.clone(), this.parentPlus);
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		return clonedImage;
	}
	
	public void drawCover(Book b, PolarPoint p) {
		parent.image(this, (float) (p.getX()-width/2f), (float) (p.getY()-height/2f));
	}
	
	public Color computeMeanColour() {
		float r=0,g=0,b=0;
		for (int pixel : pixels) {
//			r += p.red(pixel);
//			g += p.green(pixel);
//			b += p.blue(pixel);
			// faster
			assertTrue((parent.g.colorMode == PConstants.RGB));
			r += pixel >> 16 & 0xFF; // in [0, 255]
			g += pixel >> 8 & 0xFF;
			b += pixel & 0xFF;
		}
		
		r = r/pixels.length/255f;
		g = g/pixels.length/255f;
		b = b/pixels.length/255f;
		assertTrue((r <= 1f && g <= 1f && b <= 1f));
		return new Color(r, g, b, 1.0f);
	}
	
}
