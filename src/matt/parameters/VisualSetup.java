package matt.parameters;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Properties;

import org.contract4j5.contract.*;

import matt.ui.MousePath;
import matt.ui.PImagePlus;
import matt.ui.screenElement.Book;
import matt.ui.screenElement.InteractionLog;
import matt.ui.screenElement.Keyword;
import matt.util.Circle;
import matt.util.Util;

@Contract
public class VisualSetup extends ColourSetupNight implements VisualSetupInterface {
	
	@SuppressWarnings("unused")
	private Properties properties;
	private boolean useTouchInsteadOfMouse;
	
	public VisualSetup(Properties properties) {
		this.properties = properties;
		useTouchInsteadOfMouse = properties.getProperty("useTouchInsteadOfMouse", "true").equals("true");
	}
	
	@Override public boolean useFullscreen() { return false; }
	// this needs to be above around 800
	@Override public int screenWidth() { return 1200; }
	// this should ideally be larger than width*0.55 or so (e.g. 1920x1080)
	@Override public int screenHeight() { return 700; }
	@Override public boolean useFullscreenCapture() { return true; }
	@Override public Rectangle captureScreenRect() { return new Rectangle(0, 0, 500, 500); }
	@Override public boolean drawCustomCursor() { return !useTouchInsteadOfMouse; }
	@Override public boolean hideSystemCursor() { return true; }
	@Override public boolean captureOnlyClickedMouseMovement() { return useTouchInsteadOfMouse; }
	@Override public boolean showFramerate() { return false; }
	@Override public boolean booksAndKeywordInOneView() { return false; }
	// beware, drawing an image with an alpha channel is very time consuming!
	@Override public boolean drawBackgroundTexture() { return true; }
	@Override public float coverAspect() { return 2f/3f; }
	@Override @Post(Params.returnIn0to1) public float borderPercentage() { return 0.00f; }
	
//	@Override public String kTextFamily() { return "Georgia"; }
//	@Override public String kTextFamilySketchy() { return "FFF Tusj"; }
	
//	@Override public String kTextFamily() { return "Helvetica LT Std Cond Blk"; }
//	@Override public String kTextFamilySketchy() { return "HandVetica"; }
	
//	@Override public String kTextFamily() { return "Rockwell"; }
//	@Override public String kTextFamilySketchy() { return "Sketch Block"; }
	
//	@Override public String kTextFamily() { return "Helvetica LT Std"; }
//	@Override public String kTextFamily() { return "Arial"; }
//	@Override public String kTextFamilyBold() { return "Arialbd"; }
	
	
	@Override public String kTextFamily() { return "Linux Libertine"; }
	@Override public String kTextFamilyBold() { return "Linux Libertine"; }
	@Override public String kTextFamilySketchy() { return "Linux Libertine"; }
//	@Override public String kTextFamilyBold() { return "HelveticaLTStd-Bold"; }
//	@Override public String kTextFamilyBold() { return "Helvetica LT Std Black"; }
//	@Override public String kTextFamilySketchy() { return "Sketchetik"; }
	
	// e.g. if only 2 keywords are selected, that are close together, their magnets position is scaled up into
	// the books space to be as are from each other as possible
	@Override public boolean kMagnetDynamicScalingIntoAvailableSpace() { return true; }
	@Override @Post(Params.returnIn0to1) public float kMagnetPosInset() { return 0.5f; }
	@Override @Post(Params.returnIn0to1) public float bCoverSizeFactor() { return 0.85f; }
	
	// speed of bubbling in
	@Override @Post(Params.returnIn0to1) public float kProximityCutoffDistanceRatioToSpaceWidth() { return 0.1f; }
	@Override @Post(Params.returnIn0to1) public float booksToAreaRatio() { return 0.55f; }
	@Override public float schweifTimeout() { return 3000; }
	@Override public int amountOfBooksComingInThroughFountainAtOnce() { return 50; }
	@Override public int amountOfBooksComingInThroughFountainEachFrame() { return 3; }
	@Override public int maxKeywordsSelected() { return 5; }
	
	@Override public float kTextOnBookSpaceHeightRatio() { return 0.02f; }
	// setting this high has a huge performance impact because of transparent image drawing
	@Override public float kTextOnBookBubbleSpaceHeightRatio() { return 0.3f; }
	@Override @Post(Params.returnIn0to1) public float bookDetailsOverlaySizeRatio() { return 0.25f; }
	
	@Override @Post(Params.returnIn0to1) public float
		areaRatioBetweenKeywordCircleAreaAndTotalKeywordArea() { return 0.25f; }
	@Override @Post(Params.returnIn0to1) public float
		jiggleMinDistBetweenCircleBordersRatioToSpaceWidth() { return 0.023f; }
	@Override public int absoluteMinimumCircleRadius() { return 1; }
	@Override public int absoluteMinimumKeywordRadius() { return 5; }
	
	@Override public long bDetailsFadeInTimeSpanInTicks() { return 1000; }
	@Override public long fadingBooksOutTimeSpanInTicks() { return 30000; }
	@Override public long radiusChangeTimeSpanInTicks() { return 3000; }
	
	@Override public float edgeThickness(float edgeCount, float edgeTotal, long ticksPassedSinceCreation) {
		return 1f+3*(ticksPassedSinceCreation > edgeTimeoutMS ? 0 : (1-ticksPassedSinceCreation/edgeTimeoutMS));
	}
	
	@Override @Post(Params.returnIn0to1) public float kGlowAmount(
			Keyword k, MousePath mousePath, InteractionLog<Keyword> log, Book currentlyHoveredOverBook) {
		if (k.getAssociatedActiveBooks().contains(currentlyHoveredOverBook)) {
			return hoverColour().getAlpha()/255f;
		} else {
			return k.getSchweifProximityAndUpdate(mousePath);
		}
	}
	
	@Override public float kGlowRadius(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, Book currentlyHoveredOverBook) {
		return k.getRmax()*(float) (Math.pow(k.getRadiusPercentage(), 0.35)*0.35f);
	}
	
	@Override @Post(Params.returnIn0to1) public float pointBetweenOutterAndInnerThatsFlat() { return 1; }
	
	@Override public float kRadius(Keyword k, MousePath mousePath, InteractionLog<Keyword> log) {
		return k.getRadius()+k.getRmax()/10f*kGlowAmount(k, mousePath, log, null);
	}
	
	@Override @Post(Params.returnIn0to1) public float
	kFlatness(Keyword k, MousePath mousePath, InteractionLog<Keyword> log) {
		float prox = k.getSchweifProximityAndUpdate(mousePath);
		return k.isSelected() ? 0 : 1-prox;
	}
	
	@Override @Post(Params.returnIn0to1) public float
	kTextureOpacity(Keyword k, MousePath mousePath, InteractionLog<Keyword> log) {
//		return 1-clickWeightOfKeyword(k, log);
		return 0.66f-clickWeightOfKeyword(k, log)*0.66f;
	}
	
	@Override @Post(Params.returnIn0to1) public float kTextMinSizeRatioToSpaceSize() { return 0.0035f; }
	@Override @Post(Params.returnIn0to1) public float kTextMaxSizeRatioToSpaceSize() { return 0.05f; }
	
	@Override @Post(Params.returnIn0to1) public float
	kTextSize(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, boolean isHoverHighlighted) {
		float prox = k.getSchweifProximityAndUpdate(mousePath);
		prox = Math.max(prox, kTextFadeoutTicksRatio(k, log));
		if (isHoverHighlighted)
			prox = 0.6f;
		return k.isSelected()
			? 0.2f+(k.getRadiusPercentage()+prox)/2.5f
			: (k.getRadiusPercentage() + prox)/2;
	}
	
	private static long ticksTilClickedBookDerectangularises() { return 1000000; }
	
	public static class BookDisplayProperties {
		Book b;
		float roundedness;
		Circle c;
		Color hoverColour = Params.visualS.hoverColourBooks();
		float blurPercentage;
		
		public BookDisplayProperties(Book val, Point2D.Float mousePos, InteractionLog<Book> log) {
			b = val;
			float distance = (float) mousePos.distance(val.getCircle().getCentreAsPoint2DFloat());
			float cutoffDist = val.getCircle().getRadius();
			float factor = distance > cutoffDist ? 0 : (cutoffDist-distance)/cutoffDist;
			// good idea, however, since books are drawn in parallel, overlapping flicker will occur unless an order is established
//			float factor = val.getSchweifProximityAndUpdate(mousePath);
			float overlaySize = Params.visualS.bookDetailsOverlaySizeRatio()*val.getParent().space().getSize()/2;
			
//			roundedness = log.containsV(val) ? 0.2f : 1f;
			roundedness = log.containsV(val) ? (float) log.getMostRecent(val).getTimeFactor(
					ticksTilClickedBookDerectangularises())*0.8f+0.2f : 1f;
			
			blurPercentage = 1-(float) Util.percentiseIn(val.getCircle().getRadius(), val.getParent().space().getSize()/50);
			if (factor > 0)
				blurPercentage = 0;
			
			// in the physical world, the covers are modeled as spheres. if they are drawn as rectangles,
			// they may overlap, so shrink the size with decreasing roundedness
			c = val.getCircle().multRadius(Params.visualS.bCoverSizeFactor()*(roundedness/3f+0.66f));
			
			c.setRadius(Util.linearInterpolation(c.getRadius(), overlaySize, factor));
			roundedness = Util.linearInterpolation(0, roundedness, 1-factor);
		}
		
		public Circle getCircle() { return c; }
		public Color getHoverColour() { return hoverColour; }
		public PImagePlus getCover() { return b.getCover(); }
		public float getBlurPercentage() { return blurPercentage; }
		public float getRoundedness() { return roundedness; }
		public float getRoundednessForFade(float fadeFactor) {
			// never fully rectangularise
			return Util.linearInterpolation(0.05, roundedness < 0.05 ? 0.05 : roundedness, 1-fadeFactor); }
	}
	
}

