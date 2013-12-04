package matt.parameters;

import java.awt.Rectangle;

import matt.ui.MousePath;
import matt.ui.screenElement.Book;
import matt.ui.screenElement.InteractionLog;
import matt.ui.screenElement.Keyword;

public interface VisualSetupInterface extends ColourSetupInterface {
	
	float borderPercentage();
	
	float areaRatioBetweenKeywordCircleAreaAndTotalKeywordArea();
	float jiggleMinDistBetweenCircleBordersRatioToSpaceWidth();
	int absoluteMinimumCircleRadius();
	
	float edgeThickness(float edgeCount, float edgeTotal, long ticksPassedSinceCreation);
	
	float kProximityCutoffDistanceRatioToSpaceWidth();
	
	float kGlowAmount(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, Book currentlyHoveredOverBook);
	float kGlowRadius(Keyword k, MousePath mousePos, InteractionLog<Keyword> log, Book currentlyHoveredOverBook);
	
	// 1 = outter, 0 = inner
	float pointBetweenOutterAndInnerThatsFlat();
	float kRadius(Keyword k, MousePath mousePath, InteractionLog<Keyword> log);
	float kFlatness(Keyword k, MousePath mousePath, InteractionLog<Keyword> log);
	float kTextureOpacity(Keyword k, MousePath mousePath, InteractionLog<Keyword> log);
	
	float kTextSize(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, boolean isHoverHighlighted);
	float kTextMinSizeRatioToSpaceSize();
	float kTextMaxSizeRatioToSpaceSize();
	
	float schweifTimeout();
	long fadingBooksOutTimeSpanInTicks();
	long radiusChangeTimeSpanInTicks();
	String kTextFamily();
	String kTextFamilySketchy();
	long bDetailsFadeInTimeSpanInTicks();
	float bookDetailsOverlaySizeRatio();
	int amountOfBooksComingInThroughFountainAtOnce();
	int maxKeywordsSelected();
	
	float kMagnetPosInset();
	int absoluteMinimumKeywordRadius();
	boolean kMagnetDynamicScalingIntoAvailableSpace();
	boolean useFullscreen();
	int screenWidth();
	int screenHeight();
	boolean useFullscreenCapture();
	Rectangle captureScreenRect();
	boolean drawCustomCursor();
	boolean hideSystemCursor();
	boolean showFramerate();
	float coverAspect();
	boolean booksAndKeywordInOneView();
	float bCoverSizeFactor();
	float booksToAreaRatio();
	String kTextFamilyBold();
	int amountOfBooksComingInThroughFountainEachFrame();
	boolean captureOnlyClickedMouseMovement();
	boolean drawBackgroundTexture();

	float kTextOnBookSpaceHeightRatio();
	float kTextOnBookBubbleSpaceHeightRatio();
	
}
