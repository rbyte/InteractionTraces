package matt.ui;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import matt.meta.AuthorInformation;
import matt.parameters.Params;
import matt.parameters.VisualSetup.BookDisplayProperties;
import matt.ui.screenElement.Book;
import matt.ui.screenElement.BookSet;
import matt.ui.screenElement.InteractionLog;
import matt.ui.screenElement.InteractionLog.Combi;
import matt.ui.screenElement.Keyword;
import matt.ui.screenElement.KeywordSet;
import matt.ui.screenElement.Schweif;
import matt.ui.screenElement.ScreenElementSet;
import matt.ui.screenElement.TimestampedObject;
import matt.ui.screenElement.TransitioningScreenElement;
import matt.util.Circle;
import matt.util.MyConcurrentWorker;
import matt.util.MyConcurrentWorker.WorkerTemplate;
import matt.util.StringHandling;
import matt.util.Util;

import org.jbox2d.common.Vec2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;

@AuthorInformation
public class PMain extends PAppletPlus {
	
	private static final long serialVersionUID = 8890928091463998582L;
	private InteractionLog<Keyword> interactionLogKeywords = new InteractionLog<Keyword>();
	private InteractionLog<Book> interactionLogBooks = new InteractionLog<Book>();
	private InteractionLog<Book> booksDetailsSelected = new InteractionLog<Book>();
	private InteractionLog<Book> booksDetailsClosing = new InteractionLog<Book>();
	private PImagePlus texture = loadImage(Params.pathToTextures+"2979967880_d779f83253_o_gramma3_mask_300x300.png");
	private PImagePlus bookspaceCircle = radialGradient(300, 0, PToolbox.setAlpha(Params.visualS.kTextOnBookSpaceColor(), 150), 1);
	private PImagePlus bg = loadImage(Params.pathToTextures+Params.visualS.bgImageName());
	
	private WorldPlus physicsWorld = new WorldPlus();
	private Keyword currentlyHoveredOverKeyword;
	private Keyword currentlyClickedOnKeyword;
	private boolean currentlyHoveredOverKeywordFromBookSpace = false;
	private Book currentlyHoveredOverBook;
	private long tickBooksAddedOrRemovedLastTime = 0;
	
	private KeywordSet kwds;
	private BookSet books;
	private MyConcurrentWorker<Keyword> drawKeywordBubbleCW;
	private MyConcurrentWorker<Keyword> drawKeywordGlowCW;
	private MyConcurrentWorker<Keyword> drawKeywordTextCW;
	
	private boolean sketchy = false;
	private ScreenElementSet<Book> activeBooks;
	private Schweif nameTagSchweif = new Schweif(6500);
	private PImagePlus germanFlagImg = loadImage(Params.pathToTextures+"FlagSketch_040.png");
	private PImagePlus footsteps = loadImage(Params.pathToTextures+"footsteps_040.png");
	private PImagePlus germanFlagImgStreched = loadImage(Params.pathToTextures+"FlagSketch_050_streched.png");
	
	public static void main(String args[]) {
		// start in fullscreen mode
		PApplet.main(new String[] {"--present", "matt.ui.PMain"});
	}
	
	public void setupPlus() throws Exception {
		new Params().test();
		kwds = new KeywordSet(physicsWorld, this, width, height);
		kwds.loadTextShapes();
		books = new BookSet(physicsWorld, this, width, height, kwds);
		kwds.addBooksAssociation(books);
		kwds.adjustRadiusSizes();
		loadLogs();
		// TODO not ideal: guarantee that keywords do not end up outside of space
		kwds.jiggleKeywords(Params.jigglingMaxNumberOfIterations);
		physicsWorld.fountainPosition = books.space().getCenter();
		
		drawKeywordBubbleCW = new MyConcurrentWorker<Keyword>(kwds, DrawKeywordBubbleWU.class, this);
		drawKeywordGlowCW = new MyConcurrentWorker<Keyword>(kwds, DrawKeywordGlowWU.class, this);
		drawKeywordTextCW = new MyConcurrentWorker<Keyword>(kwds, DrawKeywordTextWU.class, this);
	}
	
	public void updateHoveredOverKeyword() {
		currentlyClickedOnKeyword = kwds.getClickedOn(mousePath, interactionLogKeywords, false);
		currentlyHoveredOverKeyword = kwds.getClickedOn(mousePath, interactionLogKeywords, true);
		currentlyHoveredOverKeywordFromBookSpace = false;
		
		// if not found yet, search in selected keyword text on book side (click on left side keyword)
		if (currentlyHoveredOverKeyword == null)
			for (TransitioningScreenElement<Keyword> val : kwds.getTSelected()) {
				// increase hover area
				Rectangle2D.Float textBox = Util.resizeRectangle(getTextOnBookSpaceRect(val), 2f);
				if (textBox.contains(mousePath.getCurrent())) {
					currentlyHoveredOverKeyword = val.get();
					currentlyHoveredOverKeywordFromBookSpace = true;
					break;
				}
			}
	}
	
	public void drawPlus() {
		background(Params.visualS.backgroundColour());
		if (Params.visualS.drawBackgroundTexture())
			backgroundImage(bg);
		
		activeBooks = physicsWorld.step(
			Params.tieWorldPhysicsSpeedToSystemTickInsteadOfFramerate ? 1/frameRate: 1f/15f,
			kwds, books, interactionLogBooks, mousePath,
			System.currentTimeMillis()-tickBooksAddedOrRemovedLastTime);
		
		updateHoveredOverKeyword();
		currentlyHoveredOverBook = books.getActiveClickedOn(mousePath.getCurrent());
		
		drawKeywords();
		drawKtext();
		drawSelectedKeywordsTextOnBookSpace();
		try {
			new MyConcurrentWorker<TransitioningScreenElement<Keyword>>(
				kwds.getTSelected(), DrawSelectedKeywordsBGbubbleOnBookSpaceWorkunit.class, this).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		drawBooks(activeBooks);
		drawNameTag();
	}
	
	@SuppressWarnings("unused")
	public void drawNameTag() {
		textSize(14);
		Rectangle2D.Float nameTagBox = new Rectangle2D.Float(width-200, height-40, 160, 11);
		float prox = (float) nameTagSchweif.getSchweifProximityAndUpdate(mousePath, nameTagBox, width/8f);
		
		if (false)
		image(germanFlagImg.clonePlus()
			.setSaturationLightnessAlphaMultiply(prox, prox*0.5f+0.2f, prox),
			(float) nameTagBox.getMinX()-45, (float) nameTagBox.getMinY()-2);
		if (false)
		image(germanFlagImgStreched.clonePlus()
			.setSaturationLightnessAlphaMultiply(prox, prox*0.5f+0.2f, prox),
			(float) nameTagBox.getMinX()+80, (float) nameTagBox.getMaxY()+2);
		
//		if (false)
		image(footsteps.clonePlus()
			.setSaturationLightnessAlphaMultiply(prox, prox*0.6f+0.2f, prox),
			(float) nameTagBox.getMaxX()-35, (float) nameTagBox.getMinY()-20);
		// shadow
		String text = "Designed by Matthias Graf\n        	& Uta Hinrichs";
		fill(PToolbox.setAlpha(Params.visualS.shadowColour(), (int) (prox*255f)));
		text(text, nameTagBox.x+1, (float) nameTagBox.getMaxY());
		// actual
		fill(PToolbox.mingleColours(Params.visualS.faintOnBackground(), Params.visualS.genericHightlightColor(), prox*0.7));
		text(text, nameTagBox.x, (float) nameTagBox.getMaxY());
	}
	
	public class DrawSelectedKeywordsBGbubbleOnBookSpaceWorkunit extends WorkerTemplate<TransitioningScreenElement<Keyword>> {
		@Override public void run() {
			Circle c = new Circle(new Point2D.Float(val.getCPos().x, val.getCPos().y), val.getCDiameter()/2);
			imageDrawWithResizeAndCopy(bookspaceCircle, c);
		}
	}
	
	private Rectangle2D.Float getTextOnBookSpaceRect(TransitioningScreenElement<Keyword> val) {
		PShape textShape = val.get().getTextShape();
		float height = val.getSize();
		float width = textShape.width / textShape.height * height;
		Vec2 shiftedPos = val.getPosition();
		return new Rectangle2D.Float(shiftedPos.x-width/2f, shiftedPos.y-height/2f, width, height);
	}
	
	@SuppressWarnings("unused")
	public void drawSelectedKeywordsTextOnBookSpace() {
		for (TransitioningScreenElement<Keyword> val : kwds.getTSelected()) {
			Color textColour = Params.visualS.kTextOnBookSpaceColor();
			Rectangle2D.Float textBox = getTextOnBookSpaceRect(val);
			// +line from circle to keyword text
			stroke(PToolbox.setAlphaMultiply(textColour, 0.8f));
			strokeWeight(1);
			line(val.getCPos().x, val.getCPos().y, (float) textBox.getCenterX(), (float) textBox.getCenterY());
			noStroke();
			if (val.get() == currentlyHoveredOverKeyword && false) {
				// slow when text is big
				shape(sketchy ? val.get().getTextShapeSketchy() : val.get().getTextShape(), textColour, textBox.width,
					new Point2D.Float(textBox.x, textBox.y), true, Params.visualS.hoverColourBooks(), 3, 2);		
			} else {
				shape(sketchy ? val.get().getTextShapeSketchy() : val.get().getTextShape(),
					val.get() == currentlyHoveredOverKeyword ? Params.visualS.hoverColourBooks() : textColour, textBox.width,
					new Point2D.Float(textBox.x, textBox.y));				
			}
		}
	}
	
	public void drawKtext() {
		try {
			noStroke();
			if (Params.loadKTextInParallel) {
				drawKeywordTextCW.run();
			} else {
				drawKeywordTextCW.runSequentially();
			}
		} catch (Exception e) {
			e.printStackTrace();
			shutdownProgram();
		}
	}
	
	private static final int removeAmount = 50;
	
	public void keyPressedPlus() {
		switch (key) {
		case 'r':
			interactionLogKeywords.addRandomInteractionToLog(removeAmount, 1000000, kwds);
			System.out.println("Added "+removeAmount+" random keywords to log, new size: "
				+interactionLogKeywords.size()+", current fps: "+frameRate);
			break;
		case 'q': shutdownProgram(); break;
		case 'k': saveLogs(); break;
		case 'l':
			try {
				loadLogs();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 'j': System.out.println(kwds.jiggleKeywords(1)); break;
		case 'p':
			if (Params.loadSketchyTextShapes) {
				sketchy = !sketchy;
				System.out.println("switched");			
			}
			break;
		case 'c': clearLogs(); break;
		case 'd':
			interactionLogKeywords.removeABunch(removeAmount);
			System.out.println("Removed "+removeAmount+" entries from logs, new size: books: "
				+interactionLogBooks.size()+", keywords: "+interactionLogKeywords.size()
				+", current fps: "+frameRate);
			break;
		case 'f':
			interactionLogKeywords.shiftLogTime(-5000);
			break;
		default:
			break;
		}
	}
	
	public void shutdownProgram() {
		saveLogs();
		super.shutdownProgram();
	}
	
	public void saveLogs() {
		if (interactionLogBooks.size() != 0 || interactionLogKeywords.size() != 0) {
			interactionLogBooks.saveLog("Books");			
			interactionLogKeywords.saveLog("Keywords");
		}
	}
	
	public void loadLogs() throws IOException {
		interactionLogBooks.loadLog(books, "Books", true, true);
		interactionLogKeywords.loadLog(kwds, "Keywords", true, true);
		kwds.clearSelected();
		books.unactivateAll();
	}
	
	public void clearLogs() {
		saveLogs();
		interactionLogBooks.clearLog();
		interactionLogKeywords.clearLog();
		kwds.clearSelected();
		books.unactivateAll();
	}
	
	public void mousePressedPlus() {
		// after a mouse clicked, the currently selected book is deselected (in any case)
		for (TimestampedObject<Book> selected : booksDetailsSelected)
			booksDetailsClosing.add(selected.adjustTimestampForResumingFadeState());
		booksDetailsSelected = new InteractionLog<Book>();
		
		physicsWorld.updateLastPressedOnBody(mousePath.getCurrent(), books);
	}
	
	public void mouseDraggedPlus() {
		if (physicsWorld.lastPressedOnBody != null && mouseButton == RIGHT)
			physicsWorld.lastPressedOnBody.setLinearVelocityTowardsPosition(mousePath.getCurrent(), 10);
	}
	
	public void mouseReleasedPlus() {
		updateHoveredOverKeyword();
		
		if (currentlyClickedOnKeyword != null && !currentlyHoveredOverKeywordFromBookSpace) {
			interactionLogKeywords.addV(currentlyClickedOnKeyword);
			Keyword gotPushedOut = currentlyClickedOnKeyword.switchSelected(books.space());
			tickBooksAddedOrRemovedLastTime = System.currentTimeMillis();
			
			if (gotPushedOut != null) {
				books.dequeueAll();
				// this removes only those books from the deselected keyword
				// that are not in any other of the remaining selected keywords
				for (Book book : gotPushedOut.getAssociatedActiveBooksThatAreNotInOtherSelected())
				// this removes all books that belong to the deselected keyword in the current selection
//				for (Book book : gotPushedOut.getAssociatedActiveBooks())
					book.setFadeAway(true);
			}
			
			int counter = 0;
			incomingBooks: if (gotPushedOut != currentlyClickedOnKeyword) {
				// pop in books that have been selected beforehand preferably, but max half of all that come in
				for (Book book : getFromInactiveMostRecentInLogBelongingTo(interactionLogBooks, currentlyClickedOnKeyword,
						(Params.visualS.amountOfBooksComingInThroughFountainAtOnce())/2)) {
					book.setRadius(Params.visualS.absoluteMinimumKeywordRadius());
					assertTrue(book.setInQueueToFadeIn(kwds.getSelectedScaledPosition(books.space(), currentlyClickedOnKeyword)));
					counter++;
				}
				
				// ... because we still want to have some new random ones.
				for (Book book : currentlyClickedOnKeyword.getAssociatedInactiveUnqueuedBooks())
					if (counter++ < Params.visualS.amountOfBooksComingInThroughFountainAtOnce()) {
						book.setRadius(Params.visualS.absoluteMinimumKeywordRadius());
						assertTrue(book.setInQueueToFadeIn(kwds.getSelectedScaledPosition(books.space(), currentlyClickedOnKeyword)));
					} else {
						break incomingBooks;
					}
			}
			
			books.updateSignificanceAndRadiusToBees(kwds);
		}
		
		// after a mouse released, the currently selected book is deselected (in any case)
		for (TimestampedObject<Book> selected : booksDetailsSelected)
			booksDetailsClosing.add(selected.adjustTimestampForResumingFadeState());
		
		Book clickedOnBook = books.getActiveClickedOn(mousePath.getCurrent());
		if (clickedOnBook != null) {
			interactionLogBooks.addV(clickedOnBook);
			boolean clickedOnIsNotCurrentlySelected = !booksDetailsSelected.containsV(clickedOnBook);
			// there is always only one book selected at all times
			booksDetailsSelected = new InteractionLog<Book>();
			if (clickedOnIsNotCurrentlySelected) { // add book to newly selected
				TimestampedObject<Book> thisInDeselected = booksDetailsClosing.getMostRecent(clickedOnBook);
				if (thisInDeselected != null) {
					// clicked on book is currently fading out (closing)
					booksDetailsClosing.remove(thisInDeselected);
					booksDetailsSelected.add(thisInDeselected.adjustTimestampForResumingFadeState());
				} else {
					booksDetailsSelected.addV(clickedOnBook);
				}
			}
			// non-balanced resizing
//			clickedOnBook.setRadiusToBe(clickedOnBook.getRadius()*1.5f);
		} else {
			booksDetailsSelected = new InteractionLog<Book>();
		}
	}
	
	private ArrayList<Book> getFromInactiveMostRecentInLogBelongingTo(InteractionLog<Book> log, Keyword k, int number) {
		ArrayList<Book> result = new ArrayList<Book>();
		Iterator<TimestampedObject<Book>> iterator = log.descendingIterator();
		int count = 0;
		while (iterator.hasNext() && count < number) {
			Book b = iterator.next().v;
			if (b.hasKeyword(k) && !b.isActive() && !result.contains(b)) {
				result.add(b);
				count++;
			}
		}
		return result;	
	}
	
	public void drawBooks(ScreenElementSet<Book> activeBooks) {
		try {
			new MyConcurrentWorker<Book>(activeBooks, DrawBookWorkunit.class, this).run();
		} catch (Exception e) {
			e.printStackTrace();
			shutdownProgram();
		}
		
		// draw hover over book again to account for overlaps
		if (currentlyHoveredOverBook != null
				&& !booksDetailsSelected.containsV(currentlyHoveredOverBook)
				&& !booksDetailsClosing.containsV(currentlyHoveredOverBook)) {
			new DrawBookWorkunit().run(currentlyHoveredOverBook);
		}
		
		// TODO optimisation: create image concurrently, only draw it here afterwards sequentially
		drawBookDetails();
	}
	
	private boolean currentlyHoveredOverKeywordContainsBook(Book val) {
		return currentlyHoveredOverKeyword == null ? false : currentlyHoveredOverKeyword.getAssociatedBooks().contains(val);
	}
	
	private boolean currentlyHoveredOverBookContainsKeyword(Keyword val) {
		return currentlyHoveredOverBook == null ? false : currentlyHoveredOverBook.hasKeyword(val);
	}
	
	public class DrawBookWorkunit extends WorkerTemplate<Book> {
		public void run(Book b) {
			set(b);
			run();
		}
		
		@Override public void run() {
			assertTrue(val.isActive());
			if (!booksDetailsSelected.containsV(val)
				&& !booksDetailsClosing.containsV(val)) {
				BookDisplayProperties bdp = new BookDisplayProperties(val, mousePath.getCurrentDragged(), interactionLogBooks);
				// draw hover highlighting
				float hoverRadiusAdd = bdp.getCircle().getRadius()/3f;
				// mousePath.lastMouseIsPureDrag() && 
				if (currentlyHoveredOverKeywordContainsBook(val) && bdp.getCircle().getRadius() > 4)
					drawRadialGradient(bdp.getCircle(), hoverRadiusAdd > 20 ? 20 : hoverRadiusAdd, bdp.getHoverColour(), 1);
//				cover.desaturate();
				imageDrawWithResizingAndRounding(bdp.getCover(), bdp.getCircle(), bdp.getRoundedness(), bdp.getBlurPercentage());
			}
		}
	}
	
	public void drawBookDetails() {
		{
		Iterator<TimestampedObject<Book>> iterator = booksDetailsClosing.iterator();
		while (iterator.hasNext()) {
			TimestampedObject<Book> book = iterator.next();
			double fadeOutFactor = book.getTimeFactor();
			drawBookDetails(book.v, 1-fadeOutFactor);
			if (fadeOutFactor == 1)
				iterator.remove();
		}
		}
		
		Iterator<TimestampedObject<Book>> iterator = booksDetailsSelected.iterator();
		while (iterator.hasNext()) {
			TimestampedObject<Book> book = iterator.next();
			drawBookDetails(book.v, book.getTimeFactor());
			if (!book.v.isActive())
				iterator.remove();
		}
	}
	
	public void drawBookDetails(Book b, double fadeInFactor) {
		drawBookDetails(b, (float) fadeInFactor);
	}
	
	public void drawBookDetails(Book b, float fadeInFactor) {
		BookDisplayProperties bdp = new BookDisplayProperties(b, mousePath.getCurrent(), interactionLogBooks);
		
		float maxCoverWidth = Params.visualS.bookDetailsOverlaySizeRatio()*books.space().getSize();
		
		float coverWidth = bdp.getCircle().getRadius()*2
			+(maxCoverWidth-bdp.getCircle().getRadius()*2)*fadeInFactor;
		float coverHeight = coverWidth/Params.visualS.coverAspect();
		PImagePlus cover = bdp.getCover().cloneAndResize((int) coverWidth, 0);
		
		float fullOverlayHeight = coverHeight;
		float fullOverlayWidth = coverWidth+maxCoverWidth*2.5f;
		PGraphics buf = createGraphics(
			(int) (coverWidth+maxCoverWidth*2.5f*fadeInFactor),
			(int) (coverHeight-1), // -1 to avoid flickering at bottom
			PConstants.JAVA2D);
		buf.beginDraw();
		buf.background(Params.visualS.backgroundColourOfBookDetailOverlay().getRGB());
		buf.image(cover, 0, 0);
		buf.smooth();
		buf.noStroke();
		buf.fill(0);
		// (x,y) is the lower, left corner
		float add = 22;
		float mult = 1;
		int x = (int) coverWidth+6;
		buf.textFont(pfontBold, 22);
		buf.text(b.getName(), x, add*mult++);
		buf.textFont(pfontBold, 18);
		buf.text(b.getAuthor(), x, add*mult++);
		buf.textFont(pfontBold, 14);
		add = 18.5f;
		mult += 0.45f;
//		buf.text("Dewey Index: "+b.getDewey(), x, add*5);
		buf.text("Genre(s): "+StringHandling.concat(b.getGenre().toArray(), ", "), x, add*mult++);
		buf.text("Subject(s): "+StringHandling.concat(b.getSubject().toArray(), ", "), x, add*mult++);
		buf.text("Publication year: "+b.getPubDate()+", Number of Pages: "+b.getPages(), x, add*mult++); // +", Isbn: "+b.getIsbn()
		buf.text("Abstract:", x, add*mult++);
		buf.textSize(12);
		buf.textLeading(15);
		add = 17.5f;
		buf.text(b.getArticle()+"...", x, add*mult++, fullOverlayWidth-coverWidth-14, fullOverlayHeight-3);
		buf.endDraw();
		PImagePlus p = new PImagePlus(buf, this);
		// TODO out of screen adjustment
		image(
			p.roundEdges(bdp.getRoundednessForFade(fadeInFactor), 0),
			(int) Math.floor(bdp.getCircle().getCenterX()-coverWidth/2),
			(int) Math.floor(bdp.getCircle().getCenterY()-coverHeight/2)
		);
	}
	
	@SuppressWarnings("unused")
	public void drawKeywords() {
		try {
			drawKeywordGlowCW.run();
			MyConcurrentWorker<Combi<Keyword>> lcw = new MyConcurrentWorker<Combi<Keyword>>(
				interactionLogKeywords.getCombiList(), DrawLogWU.class, this);
			if (Params.drawLogInParallel && !Params.drawLogInAdditiveMode) {
				lcw.run();
			} else {
				lcw.runSequentially();
			}
			drawKeywordBubbleCW.run();
		} catch (Exception e) {
			e.printStackTrace();
			shutdownProgram();
		}
	}
	
	public class DrawLogWU extends WorkerTemplate<Combi<Keyword>> {
		@Override public void run() {
			// do not draw traces for the first clicked keyword and for the same keyword that was subsequently clicked on
			if (val.pre != null && val.suc.v != val.pre.v) {
				assertTrue(interactionLogKeywords.size() >= 2);
				assertTrue(val.suc.counter < interactionLogKeywords.size());
				long ticksPassedSinceCreation = val.suc.getTimeDeltaTilNow();
				traceEdgeDrawer(
					val.pre.v.getCircle().clone().addToRadius(-3),
					val.suc.v.getCircle().clone().addToRadius(-3),
					(val.suc.random-0.5)*2,
					0.3f+val.suc.random2/9,
					Params.visualS.edgeThickness(val.suc.counter, interactionLogKeywords.size(), ticksPassedSinceCreation),
					Params.visualS.edgeColor(val.suc.counter, interactionLogKeywords.size(), ticksPassedSinceCreation, kwds)
				);
			}
		}
	}
	
	public class DrawKeywordBubbleWU extends WorkerTemplate<Keyword> {
		@Override public void run() {
			Circle c = val.getCircle().clone().setRadius(Params.visualS.kRadius(val, getMousePath(), interactionLogKeywords));
			drawBubble(
				c,
				Params.visualS.kBubbleInnerColour(val, getMousePath(), interactionLogKeywords),
				Params.visualS.kBubbleOutterColour(val, getMousePath(), interactionLogKeywords),
				Params.visualS.kFlatness(val, getMousePath(), interactionLogKeywords),
				true, false,
				texture,
				Params.visualS.kTextureOpacity(val, getMousePath(), interactionLogKeywords)
			);
		}
	}
	
	public class DrawKeywordGlowWU extends WorkerTemplate<Keyword> {
		@Override public void run() {
//			fill(100);
//			circle(val.getCircle());
			
			Circle c = val.getCircle().clone().setRadius(Params.visualS.kRadius(val, getMousePath(), interactionLogKeywords));
			float radiusWithGlow = c.getRadius()+Params.visualS.kGlowRadius(val, getMousePath(), interactionLogKeywords, currentlyHoveredOverBook);
			Color gC = Params.visualS.kGlowColour(val, getMousePath(), interactionLogKeywords, currentlyHoveredOverBook);
			
			drawRadialGradient(
				(float) c.getCentre().getX(),
				(float) c.getCentre().getY(),
				radiusWithGlow,
				c.getRadius(),
				new Color(gC.getRed(), gC.getGreen(), gC.getBlue(),
					(int) (255*Params.visualS.kGlowAmount(val, getMousePath(), interactionLogKeywords, currentlyHoveredOverBook))),
				1d);
		}
	}
	
	public class DrawKeywordTextWU extends WorkerTemplate<Keyword> {
		@Override public void run() {
			Color textColour = Params.visualS.kTextColour(val, getMousePath(), interactionLogKeywords, currentlyHoveredOverBookContainsKeyword(val));
			if (textColour.getAlpha() != 0) {
				Rectangle2D.Float textBox = val.getTextBox(mousePath, interactionLogKeywords);
				float outOfScreenXadjustment = (float) Math.max(0, textBox.getMaxX()-width+10);
				
				shape(sketchy ? val.getTextShapeSketchy() : val.getTextShape(), textColour, textBox.width,
					new Point2D.Float(val.getPosition().x-outOfScreenXadjustment, val.getCircle().getCenterY()-textBox.height/2f));
			}
		}
	}
	
}
