package matt.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jbox2d.common.Vec2;

import matt.meta.AuthorInformation;
import matt.parameters.Params;
import matt.util.Circle;
import matt.util.PolarPoint;
import matt.util.Square;
import matt.util.StringHandling;
import matt.util.Util;

import processing.core.*;
import static org.junit.Assert.*;

@AuthorInformation
public class PAppletPlus extends PApplet {

	private static final long serialVersionUID = 8226582026367215744L;
	
	protected ScreenCapture screenVideoCapture = new ScreenCapture(this);
	public MousePath mousePath = new MousePath();
	public long drawCycles = 0;
	private PImagePlus cursor = loadImage(Params.pathToTextures+"cursor.png");
	protected PFont pfont = createFont(Params.visualS.kTextFamily(), 30, true);
	protected PFont pfontBold = createFont(Params.visualS.kTextFamilyBold(), 30, true);
	private boolean runSetupPlusFinishedSuccessfully = false;
	private Thread runSetupPlusThread = new Thread(new Runnable() {
		@Override public void run() {
			try {
				setupPlus();
				runSetupPlusFinishedSuccessfully = true;
			} catch (Exception e) {
				e.printStackTrace();
				shutdownProgram();
			}
		}
	});
	
	private boolean stillLoading() {
		return runSetupPlusThread.isAlive();
	}
	
	// overwrite these in child instead of non-plus equivalents
	public void setupPlus() throws Exception {}
	public void drawPlus() {}
	public void keyPressedPlus() {}
	
	public void mousePressedPlus() {}
	public void mouseDraggedPlus() {}
	public void mouseReleasedPlus() {}
	public void mouseClickedPlus() {}
	public void mouseMovedPlus() {}
	
	public final void setup() {
		System.out.println("Running PAppletPlus ...");
		if (Params.visualS.useFullscreen()) {
			size(screenWidth, screenHeight);
		} else {
			size(Params.visualS.screenWidth(), Params.visualS.screenHeight());
		}
		if (Params.visualS.hideSystemCursor())
			noCursor();
		smooth();
		textFont(pfont);
		
		runSetupPlusThread.start();
	}
	
	public final void draw() {
		if (stillLoading()) {
			drawLoadingScreen();
		} else {
			if (!runSetupPlusFinishedSuccessfully)
				shutdownProgram();
			try {
				drawPlus();
				if (Params.visualS.showFramerate())
					drawFps();
				if (Params.visualS.drawCustomCursor())
					image(cursor, mousePath.getCurrent().x - 4, mousePath.getCurrent().y-4);
				if (!screenVideoCapture.isStopped())
					screenVideoCapture.videoScreenCapture();
				mousePath.cycleEnd();
			} catch (AssertionError e) {
				e.printStackTrace();
				shutdownProgram();
			}
		}
		drawCycles++;
	}
	
	private void drawFps() {
		textSize(10);
		noStroke();
//		fill(0);
//		rect(0, 0, 40, 12);
		fill(Params.visualS.faintOnBackground());
		text("fps: "+(int) frameRate, 2, 10);
	}
	
	// may be overwritten
	public void drawLoadingScreen() {
		background(255);
		fill(0);
		text("Loading"+StringHandling.concat(".", (int) (drawCycles / 10 % 4)), width/2, height/2);
	}
	
	public final void keyPressed() {
		if (!stillLoading())
			keyPressedPlus();
		
		switch (key) {
		case 's': screenVideoCapture.screenshot(); break;
		case 'v': screenVideoCapture.switchOnOff(); break;
		case '\u001b': // escape key
		case 'q':
			shutdownProgram();
			break;
		default:
			break;
		}
	}
	
	public void shutdownProgram() {
		if (!screenVideoCapture.isStopped())
			screenVideoCapture.stop();
		if (runSetupPlusThread.isAlive())
			runSetupPlusThread.interrupt();
		exit();
		System.exit(1);
	}
	
	public final void mousePressed() {
		if (stillLoading()) return;
		mousePressedPlus();
	}
	
	public final void mouseDragged() {
		if (stillLoading()) return;
		mouseDraggedPlus();
	}
	
	public final void mouseReleased() {
		if (stillLoading()) return;
		mouseReleasedPlus();
	}
	
	public final void mouseClicked() {
		if (stillLoading()) return;
		mouseClickedPlus();
	}
	
	public final void mouseMoved() {
		if (stillLoading()) return;
		mouseMovedPlus();
	}
	
	// utility functions
	
	public void linearGradient(PGraphics g, Color colour1, Color colour2, boolean vertical) {
		g.pixels = linearGradient(g.width, g.height, colour1, colour2, vertical);
		g.updatePixels();
	}
	
	public PImagePlus linearGradient(Color colour1, Color colour2, int w, int h, boolean vertical) {
		PImagePlus img = new PImagePlus(w, h, this);
		img.pixels = linearGradient(w, h, colour1, colour2, vertical);
		return img;
	}
	
	private int[] linearGradient(int w, int h, Color colour1, Color colour2, boolean vertical) {
		int[] pixels = new int[w*h];
		for (int i = 0; i < pixels.length; i++) {
			double n = vertical ? (double) ((i / w) / (double) h) : (double) ((i % w) / (double) h);
			if (n>1) n=1;
			if (n<0) n=0;
			pixels[i] = PToolbox.mingleColours(colour1, colour2, n).getRGB();
		}
		return pixels;
	}
	
	public void drawBubble(Circle c, Color innerColour, Color outterColour,
			double flattenGradient, boolean drawShadow, boolean drawHighlight) {
		drawBubble(c, innerColour, outterColour, flattenGradient, drawShadow, drawHighlight, null, 0);
	}
	
	public void drawBubble(Circle c, Color innerColour, Color outterColour,
			double flattenGradient, boolean drawShadow, boolean drawHighlight, PImagePlus texture, float textureOpacity) {
		drawBubble((float) (c.getCentre().getX()), (float) (c.getCentre().getY()), 
			c.getRadius()*2, innerColour, outterColour, flattenGradient, drawShadow, drawHighlight, texture, textureOpacity);
	}
	
	public void drawBubble(float centerX, float centerY, float diameter, Color innerColour, Color outterColour,
			double flattenGradient, boolean drawShadow, boolean drawHighlight) {
		drawBubble(centerX, centerY, diameter, innerColour, outterColour, flattenGradient, drawShadow, drawHighlight, null, 0);
	}
	
	public void drawBubble(float centerX, float centerY, float diameter, Color innerColour, Color outterColour,
			double flattenGradient, boolean drawShadow, boolean drawHighlight, PImagePlus texture, float textureOpacity) {
		assertTrue(0 <= flattenGradient && flattenGradient <= 1);
		assertTrue(diameter > 2);

		if (drawShadow && flattenGradient != 1)
			drawRadialGradient(
				centerX+1+diameter/30f,
				centerY+1+diameter/18f,
				diameter/2f,
				0,
				new Color(0, 0, 0, (int) ((1-flattenGradient)*255f)),
				new Color(0, 0, 0, 0),
				5
			);
		
		// actual bubble
		drawRadialGradient(centerX, centerY, diameter/2, 0f, innerColour, outterColour, 1d);
		
		// texture
		// offset for (small) shadow!
		// the center should not move, so we need to crop on both sides of the texture
		if (texture != null && textureOpacity != 0)
			imageDrawWithRounding(texture.cloneAndCropInCentre(getNextHigherEvenInt(diameter))
				.setLowKeyTo(1-textureOpacity)
			, centerX+0.8f, centerY+0.8f, diameter);
		
		if (drawHighlight && flattenGradient != 1)
			drawRadialGradient(
				centerX-diameter/10f,
				centerY-diameter/6f,
				diameter/2.7f,
				0,
				new Color(255, 255, 255, (int) ((1-flattenGradient)*50)),
				new Color(255, 255, 255, 0),
				1
			);
	}
	
	public int getNextHigherEvenInt(float f) {
		return (int) (Math.ceil(f/2)*2);
	}
	
	public void drawRadialGradient(Circle c, float outterRadiusAdd, Color innerColour, double pow) {
		drawRadialGradient(c.getCenterX(), c.getCenterY(), c.getRadius()+outterRadiusAdd, c.getRadius(), innerColour, pow);
	}
	
	public void drawRadialGradient(
			float centerX, float centerY, float outterRadius, float innerRadius,
			Color innerColour, double pow) {
		drawRadialGradient(centerX, centerY, outterRadius, innerRadius, innerColour,
			new Color(innerColour.getRed(), innerColour.getGreen(), innerColour.getBlue(), 0), pow);
	}
	
	public void drawRadialGradient(
			float centerX, float centerY, float outterRadius, float innerRadius,
			Color innerColour, Color outterColour, double pow) {
		if (innerColour.getAlpha() > 0 || outterColour.getAlpha() > 0) {
			int imageTopLeftX = (int) Math.floor(centerX-outterRadius);
			int imageTopLeftY = (int) Math.floor(centerY-outterRadius);
			PImagePlus image = radialGradient(centerX-imageTopLeftX, centerY-imageTopLeftY,
					outterRadius, innerRadius, innerColour, outterColour, pow);
			if (outterColour.getAlpha() != 0) image.roundEdges(1, centerX-imageTopLeftX, centerY-imageTopLeftY, outterRadius*2);
			
			image(image, imageTopLeftX, imageTopLeftY);
		}
	}
	
	public PImagePlus radialGradient(float outterRadius, float innerRadius, Color innerColour, double pow) {
		int size = (int) Math.floor(outterRadius*2)+2;
		return radialGradient(size, size, size/2f, size/2f,
			outterRadius, innerRadius, innerColour,
			new Color(innerColour.getRed(), innerColour.getGreen(), innerColour.getBlue(), 0), pow);
	}
	
	public PImagePlus radialGradient(float outterRadius, float innerRadius,
			Color innerColour, Color outterColour, double pow) {
		int size = (int) Math.floor(outterRadius*2)+2;
		return radialGradient(size, size, size/2f, size/2f,
			outterRadius, innerRadius, innerColour, outterColour, pow);
	}
	
	private PImagePlus radialGradient(
			float centerX, float centerY, float outterRadius, float innerRadius,
			Color innerColour, Color outterColour, double pow) {
		int size = (int) Math.floor(outterRadius*2)+2;
		return radialGradient(size, size,
			centerX, centerY, outterRadius, innerRadius,
			innerColour, outterColour, pow);
	}
	
	private static class Settings {
		private int width, height;
	    private float centerX, centerY, outterRadius, innerRadius;
	    private Color innerColour, outterColour;
	    private double pow;
	    private PImagePlus resultingImage = null;
	    
	    public int getWidth() { return width; }
	    public int getHeight() { return height; }
	    public float getCenterX() {return centerX; }
	    public float getCenterY() { return centerY; }
	    public float getOutterRadius() { return outterRadius; }
	    public float getInnerRadius() { return innerRadius; }
	    public Color getInnerColour() { return innerColour; }
	    public Color getOutterColour() { return outterColour; }
	    public double getPow() { return pow; }
	    
	    public PImagePlus getResultingImage() { return resultingImage; }
	    public void setResultingImage(PImagePlus resultingImage) { this.resultingImage = resultingImage; }
	    
	    Settings(int width, int height,
	    		float centerX, float centerY, float outterRadius, float innerRadius,
	    		Color innerColour, Color outterColour, double pow) {
	    	this.width = width;
	    	this.height = height;
	    	this.centerX = centerX;
	    	this.centerY = centerY;
	    	this.outterRadius = outterRadius;
	    	this.innerRadius = innerRadius;
	    	this.outterColour = outterColour;
	    	this.innerColour = innerColour;
	    	this.pow = pow;
	    }
	    
		public boolean equals(Settings o) {
			return width == o.getWidth() && height == o.getHeight()
				&& centerX == o.getCenterX() && centerY == o.getCenterY()
				&& outterRadius == o.getOutterRadius() && innerRadius == o.getInnerRadius()
				&& innerColour.equals(o.getInnerColour()) && outterColour.equals(o.getOutterColour())
				&& pow == o.getPow();
		}
	}
	
	private static class Cache {
		private static final boolean ENABLE_CACHING = false;
		private ConcurrentLinkedQueue<Settings> cache = new ConcurrentLinkedQueue<Settings>();
		private long totalRequests= 0;
		private long totalHits = 0;
		
		public boolean add(Settings s) {
			if (ENABLE_CACHING) {
				assertTrue(s.getResultingImage() != null);
				if (cache.size() > 10000) {
//				System.out.println("removed last element");
					cache.poll();
				}
				return cache.add(s);
			}
			return false;
		}
		
		public PImagePlus getCachedImage(Settings s) throws NoSuchElementException {
			if (ENABLE_CACHING) {
				totalRequests++;
				for (Settings i : cache) {
					if (i.equals(s)) {
						totalHits++;
//						System.out.println("hit: "+totalHits/(float) totalRequests);
						return i.getResultingImage();
					}
				}
			}
			return null;
		}
		
	}
	
	private Cache cache = new Cache();
	
	private PImagePlus radialGradient(int width, int height,
			float centerX, float centerY, float outterRadius, float innerRadius,
			Color innerColour, Color outterColour, double pow) {
		Settings settings = new Settings(width, height, centerX, centerY, outterRadius, innerRadius, innerColour, outterColour, pow);
		PImagePlus img = cache.getCachedImage(settings);
		if (img == null) {
			assertTrue(width > 0 && height > 0 && innerRadius >= 0 && outterRadius > innerRadius);
			img = new PImagePlus(width, height, this);
			Point2D.Float center = new Point2D.Float(centerX, centerY);
			
			for (int px=0; px<width; px++) {
				for (int py=0; py<height; py++) {
					double n = (center.distance(new Point2D.Float(px, py))-innerRadius)/(outterRadius-innerRadius);
					if (n<0) n = 0;
					if (n>1) n = 1;
					if (pow != 1 && n != 0) n = Math.pow(n, pow);
					img.pixels[py*width+px] = Util.linearInterpolation(innerColour, outterColour, n).getRGB();
				}
			}
			settings.setResultingImage(img);
			cache.add(settings);
		}
		return img;
	}
	
	public void drawPolarLine(PolarPoint p1, PolarPoint p2) {
		PolarPoint center = new PolarPoint(width/2, height/2, "xy");
		line((float) center.clone().add(p1).getX(), (float) center.clone().add(p1).getY(),
			(float) center.clone().add(p2).getX(), (float) center.clone().add(p2).getY());
	}
	
	public void drawPolarPoint(PolarPoint p) {
		PolarPoint center = new PolarPoint(width/2, height/2, "xy");
		line((float) center.getX(), (float) center.getY(),
			(float) center.clone().add(p).getX(), (float) center.clone().add(p).getY());
		text(p.toString(), (float) center.clone().add(p).getX(), (float) center.clone().add(p).getY());
	}
	
	public void line(Point2D.Float p1, Point2D.Float p2) {
		line(p1.x, p1.y, p2.x, p2.y);
	}
	
	public void line(Vec2 p1, Vec2 p2) {
		line(p1.x, p1.y, p2.x, p2.y);
	}
	
	public void line(PolarPoint p1, PolarPoint p2) {
		line((float) p1.getX(), (float) p1.getY(), (float) p2.getX(), (float) p2.getY());
	}
	
	public void bezier(PolarPoint p1, PolarPoint p2, PolarPoint p3, PolarPoint p4) {
		bezier((float) p1.getX(), (float) p1.getY(), (float) p2.getX(), (float) p2.getY(),
				(float) p3.getX(), (float) p3.getY(), (float) p4.getX(), (float) p4.getY());
	}
	
	// utility functions
	
	public void traceEdgeDrawer(Circle c1, Circle c2, double position, double handleLength, float edgeThickness, Color bezierStrokeColour) {
		traceEdgeDrawer(c1, c2, position, handleLength, edgeThickness, bezierStrokeColour, false, false, false);
	}

	@SuppressWarnings("unused")
	public void traceEdgeDrawer(
			Circle c1, Circle c2,
			double position, double handleLength, float edgeThickness,
			Color bezierStrokeColour,
			boolean drawBoth, boolean drawTangents, boolean drawHandles) {
		// not a huge difference in speed
		if (false) {
			traceEdgeDrawerSimple(c1, c2, position, handleLength, edgeThickness, bezierStrokeColour, drawBoth, drawTangents, drawHandles);
			return;
		}
		
		assertTrue(-1 <= position && position <= 1);
		assertTrue(0 <= handleLength && handleLength <= 0.5);
		PolarPoint t1u = c1.getTangentPoint(c2, position);
		PolarPoint t2u = c2.getTangentPoint(c1, position);
		PolarPoint t1l = c1.getTangentPoint(c2, -position);
		PolarPoint t2l = c2.getTangentPoint(c1, -position);
		
		if (drawTangents) {
			stroke(255, 0, 0);
			line(t1u, t2u);
			line(t1l, t2l);
		}
		
		PolarPoint handle1 = t1u.clone().add(t2u.clone().subtract(t1u).multiplyR(handleLength));
		PolarPoint handle2 = t2l.clone().add(t1l.clone().subtract(t2l).multiplyR(handleLength));
		PolarPoint handle3 = t2u.clone().add(t1u.clone().subtract(t2u).multiplyR(handleLength));
		PolarPoint handle4 = t1l.clone().add(t2l.clone().subtract(t1l).multiplyR(handleLength));
		
		if (drawHandles) {
			stroke(0, 0, 255);
			line(t1u, handle1);
			line(t2u, handle3);
			line(t2l, handle2);
			line(t1l, handle4);
		}
		
		if (Params.drawLogInParallel || Params.drawLogInAdditiveMode) {
			bezierParallel(t1u, handle1, handle2, t2l, edgeThickness, bezierStrokeColour);
		} else {
			strokeWeight(edgeThickness);
			stroke(bezierStrokeColour);
			noFill();
			bezier(t1u, handle1, handle2, t2l);
			if (Params.drawLogEdgesAsInnerAndOutterLine) {
				strokeWeight(edgeThickness/3f);
				stroke(PToolbox.setLightnessMultiply(bezierStrokeColour, 1.5f));
				bezier(t1u, handle1, handle2, t2l);				
			}
		}
		if (drawBoth) {
			bezier(t2u, handle3, handle4, t1l);
		}
	}
	
	public void traceEdgeDrawerSimple(
			Circle c1, Circle c2,
			double position, double handleLength, float edgeThickness,
			Color bezierStrokeColour,
			boolean drawBoth, boolean drawTangents, boolean drawHandles) {
		strokeWeight(edgeThickness);
		stroke(bezierStrokeColour);
		noFill();
		line(c1.getCenterX(), c1.getCenterY(), c2.getCenterX(), c2.getCenterY());
	}
	
	public void bezierParallel(PolarPoint p1, PolarPoint p2, PolarPoint p3, PolarPoint p4, float edgeThickness, Color bezierStrokeColour) {
		Rectangle2D.Float boundingRect = Square.getBoundingRect(p1, p2, p3, p4);
		int imageTopLeftX = (int) Math.floor(boundingRect.x);
		int imageTopLeftY = (int) Math.floor(boundingRect.y);
		int bufWidth = (int) Math.ceil(boundingRect.width);
		int bugHeight =  (int) Math.ceil(boundingRect.height);
		
		PGraphics buf = createGraphics(bufWidth, bugHeight, PConstants.JAVA2D);
		buf.beginDraw();
//		buf.background(200, 200);
		buf.smooth();
		buf.strokeWeight(edgeThickness);
		buf.stroke(bezierStrokeColour.getRGB());
		buf.noFill();
		buf.bezier(
			(float) p1.getX()-imageTopLeftX,
			(float) p1.getY()-imageTopLeftY,
			(float) p2.getX()-imageTopLeftX,
			(float) p2.getY()-imageTopLeftY,
			(float) p3.getX()-imageTopLeftX,
			(float) p3.getY()-imageTopLeftY,
			(float) p4.getX()-imageTopLeftX,
			(float) p4.getY()-imageTopLeftY);
		if (Params.drawLogEdgesAsInnerAndOutterLine) {
			// this throws ArithmeticExceptions
			// also, for blur, the buffer pic needs to be bigger.
//			buf.filter(BLUR, 2);
			buf.strokeWeight(edgeThickness/3f);
			buf.stroke(PToolbox.setLightnessMultiply(bezierStrokeColour, 1.5f).getRGB());
			buf.bezier(
				(float) p1.getX()-imageTopLeftX,
				(float) p1.getY()-imageTopLeftY,
				(float) p2.getX()-imageTopLeftX,
				(float) p2.getY()-imageTopLeftY,
				(float) p3.getX()-imageTopLeftX,
				(float) p3.getY()-imageTopLeftY,
				(float) p4.getX()-imageTopLeftX,
				(float) p4.getY()-imageTopLeftY);
		}
		
		buf.endDraw();
		if (Params.drawLogInAdditiveMode) {
			image(buf, imageTopLeftX, imageTopLeftY, ADD);	
		} else {
			image(buf, imageTopLeftX, imageTopLeftY);
		}
	}
	
	public MousePath getMousePath() {
		return mousePath;
	}
	
	@Override
	protected void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);
		mousePath.add(mouseX, mouseY, event.getID());
	}
	
	public PImagePlus loadImage(String filepath) {
		assertTrue(new File(filepath).isFile());
		PImagePlus result = new PImagePlus(super.loadImage(filepath), this);
		return result;
	}
	
	public void ellipse(Ellipse2D.Float p) {
		// Ellipse2D has (x,y) in upper left corner, ellipse() draws (x,y) in the center (default)
		ellipse(p.x+p.width/2f, p.y+p.height/2f, p.width, p.height);
	}
	
	public void circle(float x, float y, float diameter) {
		ellipse(x, y, diameter, diameter);
	}
	
	public void circle(Point2D.Float centre, float r) {
		ellipse(centre.x, centre.y, r, r);
	}
	
	public void circle(Circle c) {
		ellipse((float) c.getCentre().getX(), (float) c.getCentre().getY(), c.getRadius()*2, c.getRadius()*2);
	}
	
	public void circle(PolarPoint centre, float r) {
		ellipse((float) centre.getX(), (float) centre.getY(), r, r);
	}
	
	/**
	 * Fill the given area with the background colour.
	 */
	public void clear(Rectangle2D.Float area) {
		fill(Params.visualS.backgroundColour());
		stroke(Params.visualS.backgroundColour());
		rect(area);
	}
	
	public void background(Color colour) {
		background(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getAlpha());
	}
	
	public void stroke(Color colour) {
		stroke(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getAlpha());
	}
	
	public void fill(Color colour) {
		fill(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getAlpha());
	}
	
	public void rect(Rectangle2D.Float rect) {
		rect(rect.x, rect.y, rect.width, rect.height);
	}
	
	public void image(PImage img, Point2D.Float p) {
		image(img, p.x, p.y);
	}
	
	public void image(PImage image, int imageTopLeftX, int imageTopLeftY, int mode) {
		blend(image, 0, 0, image.width, image.height, imageTopLeftX, imageTopLeftY, image.width, image.height, mode);
	}

	public void imageDrawWithRounding(PImagePlus img, Circle c) {
		imageDrawWithRounding(img, c, 1);
	}
	
	public void imageDrawWithRounding(PImagePlus img, Circle c, float roundness) {
		imageDrawWithRounding(img, c.getCenterX(), c.getCenterY(), c.getRadius()*2f, roundness);
	}
	
	public void imageDrawWithRounding(PImagePlus img, float centerX, float centerY, float diameter) {
		imageDrawWithRounding(img, centerX, centerY, diameter, 1);
	}
	
	/**
	 *  put that images centered at (x,y), as a circle of diameter size
	 */
	// although image() talkes floats (potentially non-integer) for the position,
	// it is not antialiasing the image and prints it to the Math.floor() integer position
	public void imageDrawWithRounding(PImagePlus img, float centerX, float centerY, float diameter, float roundness) {
		assertTrue( Math.min(img.width, img.height) >= diameter);
		int imageTopLeftX = (int) Math.floor(centerX-img.width/2);
		int imageTopLeftY = (int) Math.floor(centerY-img.height/2);
		image(
			img.roundEdges(roundness, centerX-imageTopLeftX, centerY-imageTopLeftY, diameter),
			imageTopLeftX,
			imageTopLeftY
		);
	}
	
	public void imageDrawWithResizingAndRounding(PImagePlus img, Circle c, float roundness) {
		imageDrawWithResizingAndRounding(img, c, roundness, 0);
	}
	
	public void imageDrawWithResizingAndRounding(final PImagePlus img, Circle c, float roundness, float blurPercentage) {
		Circle cc = c.clone();
		if (cc.getRadius() < Params.visualS.absoluteMinimumCircleRadius())
			cc.setRadius(Params.visualS.absoluteMinimumCircleRadius());
		PImagePlus resizedImg = img.cloneAndResize((int) Math.ceil(cc.getRadius()*2), 0);
		
//		img.fastblur((int) blurPercentage*110);
		resizedImg.blur(blurPercentage);
		imageDrawWithRounding(resizedImg, cc, roundness);
	}
	
	public void imageDrawWithResizeAndCopy(final PImagePlus img, Circle c) {
		image(img.cloneAndResize((int) (c.getRadius()*2), 0), c.getBoundingBoxLowCorner());
	}
	
	public void text(String string, Point2D.Float position) {
		text(string, position.x, position.y);
	}
	
	public void shape(PShape p, float x, float y, float width) {
		shape(p, x, y, width, p.height/p.width*width);
	}
	
	public PShape loadShape(File file) {
		return loadShape(file.getAbsolutePath());
	}
	
	public void backgroundImage(PImagePlus img) {
		for (int i=0; i<Math.ceil(width/(double) img.width); i++) {
			for (int j=0; j<Math.ceil(height/(double) img.height); j++) {
				image(img, i*img.width, j*img.height);
			}
		}
	}
	
	public void shape(PShape shape, Color textColour, float width, Point2D.Float pos) {
		shape(shape, textColour, width, pos, false, new Color(0,0,0,0), 0, 0);
	}
	
	public void shape(PShape shape, Color textColour, float width, Point2D.Float pos,
			boolean glow, Color glowColour, int glowBlurAmount, int glowIterations) {
		assertTrue(width > 0 && glowBlurAmount >= 0 && glowIterations >= 0);
		if (textColour.getAlpha() != 0) {
			float actualTextWidth = width;
			float actualTextHeight = shape.height/shape.width*actualTextWidth;
			int bufSizeAdd = (glow ? 4+glowBlurAmount*4 : 1);
			int bufWidth = (int) Math.ceil(actualTextWidth+bufSizeAdd);
			int bufHeight = (int) Math.ceil(actualTextHeight+bufSizeAdd);
			int imageX = (int) Math.floor(pos.x-bufSizeAdd/2f);
			int imageY = (int) Math.floor(pos.y-bufSizeAdd/2f);
			
			if (Params.loadKTextInParallel || glow) {
				PGraphics buf = createGraphics(bufWidth, bufHeight, PConstants.JAVA2D);
				buf.beginDraw();
//				buf.background(220);
				buf.smooth();
				if (Params.colourKTextThroughFill)
					buf.noStroke();
				if (glow) {
					while (glowIterations-- > 0) {
						if (Params.colourKTextThroughFill)
							buf.fill(glowColour.getRGB());
						buf.shape(
								shape,
								// relative position inside image (image box shakes, but position inside adjusts)
								pos.x-imageX,
								pos.y-imageY,
								actualTextWidth,
								actualTextHeight);
						while (true)
							try {
								buf.filter(BLUR, glowBlurAmount);
								break;
							} catch (ArithmeticException e) {
								glowBlurAmount /= 2;
								System.err.println("shape: glowBlurAmount too high.");
							}
					}
				}
				if (Params.colourKTextThroughFill)
					buf.fill(textColour.getRGB());
				buf.shape(
					shape,
					// relative position inside image (image box shakes, but position inside adjusts)
					pos.x-imageX,
					pos.y-imageY,
					actualTextWidth,
					actualTextHeight);
				buf.endDraw();
				if (Params.colourKTextThroughFill) {
					image(buf, imageX, imageY);	
				} else {
					// TODO here: if glow, glowColour is textColour
					PImagePlus bufAsImg = new PImagePlus(buf, this);
					bufAsImg.colour(textColour);
					image(bufAsImg, imageX, imageY);				
				}
			} else {
				if (Params.colourKTextThroughFill)
					fill(textColour.getRGB());
				shape(
					shape,
					pos.x,
					pos.y,
					actualTextWidth,
					actualTextHeight);
			}
		}
	}
	
	public void textGlow(PShape shape, float x, float y, int width, Color textColour) {
		float actualTextWidth = width;
		float actualTextHeight = shape.height/shape.width*actualTextWidth;
		int bufWidth = (int) Math.ceil(actualTextWidth+1);
		int bufHeight = (int) Math.ceil(actualTextHeight+1);
		int imageX = (int) Math.floor(x);
		int imageY = (int) Math.floor(y);
		
		PGraphics buf = createGraphics(bufWidth, bufHeight, PConstants.JAVA2D);
		buf.beginDraw();
		buf.smooth();
		buf.fill(textColour.getRGB());
		buf.noStroke();
		buf.shape(
			shape,
			// relative position inside image (image box shakes, but position inside adjusts)
			x-imageX,
			y-imageY,
			actualTextWidth,
			actualTextHeight);
		buf.endDraw();
		image(buf, imageX, imageY);	
	}
	
}



