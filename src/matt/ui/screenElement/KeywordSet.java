package matt.ui.screenElement;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import matt.parameters.Params;
import matt.setup.DatabaseQueryIssuer;
import matt.ui.MousePath;
import matt.ui.PAppletPlus;
import matt.ui.WorldPlus;
import matt.util.Circle;
import matt.util.MyConcurrentWorker;
import matt.util.ProgressBar;
import matt.util.SetSimilarity;
import matt.util.Square;
import matt.util.StringHandling;
import matt.util.MyConcurrentWorker.WorkerTemplate;

import static org.junit.Assert.*;

public class KeywordSet extends ScreenElementSet<Keyword> {
	
	public KeywordSet(WorldPlus world, PAppletPlus p, int width, int height) throws IOException {
		super(spaceWithBorder(width, height));
		
		String[][] csvInput = StringHandling.readCSV(Params.mdsSetup.getKeywordsFile(Params.databaseTableSetup));
		Point2D.Float[] mdsPositions = new Point2D.Float[csvInput.length];
		for (int i = 0; i < csvInput.length; i++)
			mdsPositions[i] = new Point2D.Float(
				Float.parseFloat(csvInput[i][2]),
				Float.parseFloat(csvInput[i][3]));
		Square mdsSpace = Square.getBoundingSquare(mdsPositions).multSizeAroundStaticCenter(1.15f);
		
		for (int i = 0; i < csvInput.length; i++) {
			String keywordName = csvInput[i][0];
			// TODO check where the keyword name needs cleansing too (this in not the only possition)
			Keyword k = new Keyword(
				this,
				p,
				world.createCircleBody(
					mdsSpace,
					new Circle(mdsPositions[i], 5),
					BodyType.STATIC),
				keywordName);
			
			if (Params.visualS.booksAndKeywordInOneView())
				k.setActive(true);
			// Long.parseLong(csvInput[i][1]) contains the bookCount of this keyword .. not needed
			add(k); 
		}
	}
	
	private static Rectangle2D.Float spaceWithBorder(int width, int height) {
		boolean vertical = width < height;
//		return new Rectangle2D.Float(-300, -300, 2000, 2000);
		return Params.visualS.booksAndKeywordInOneView()
			? new Rectangle2D.Float(0, 0,
				vertical ? width	: height,
				vertical ? width	: height)
			: new Rectangle2D.Float(
				vertical ? 0			: width/2f,
				vertical ? height/2f	: 0,
				vertical ? width		: width/2,
				vertical ? height/2f	: height);
	}
	
	public void addBooksAssociation(BookSet books) throws ClassNotFoundException, SQLException {
		DatabaseQueryIssuer dbex = new DatabaseQueryIssuer();
		for (Keyword k : this) {
			k.addBooksAssociation(books, dbex);
		}
		dbex.closeDatabaseConnection();
	}

	private static final long serialVersionUID = -8332166765507048476L;
	
	private float radiusMax;
	private float radiusMin;
	
	// complicated solution. the TransitioningScreenElement is tracing position and size of the keyword
	// text that is shown in the bookSpace (not in the keyword space).
	private ConcurrentLinkedQueue<TransitioningScreenElement<Keyword>> selected
		= new ConcurrentLinkedQueue<TransitioningScreenElement<Keyword>>();
	
	public boolean contains(String keywordName) {
		for (Keyword k : this) {
			if (k.getName().equals(keywordName)) {
				return true;
			}
		}
		return false;
	}
	
	public Keyword getClickedOn(MousePath mousePath, InteractionLog<Keyword> log) {
		return getClickedOn(mousePath, log, false);
	}
	
	public Keyword getClickedOn(MousePath mousePath, InteractionLog<Keyword> log, boolean ifDragged) {
		for (Keyword t : this)
			if (t.isInsideEnlarged(mousePath, log, ifDragged))
				return t;
		return null;
	}
	
	public void printAllStats() {
		for (Keyword k : this) {
			System.out.println(k);
		}
	}
	
	@Deprecated
	public Set<Book> getIntersectionOfSelected(Set<Book> bookSet) {
		for (Keyword k : this) {
			if (k.isSelected()) {
				// TODO remove dependency from sandbox element SetSimilarity
				bookSet = SetSimilarity.intersection(bookSet, k.getAssociatedBooks());
			}
		}
		return bookSet;
	}
	
	public boolean isSelected(Keyword k) {
		for (TransitioningScreenElement<Keyword> tsk : selected) {
			if (tsk.get() == k) {
				return true;
			}
		}
		return false;
//		return selected.contains(k);
	}
	
	public void clearSelected() {
		while (!selected.isEmpty())
			selected.remove();
	}
	
	public void updateToLog(Square bookSpace, InteractionLog<Keyword> iLog) {
		clearSelected();
		try {
			for (int i=0; i<Params.visualS.maxKeywordsSelected(); i++)
				switchSelected(bookSpace, iLog.getFromTail(i).v);			
		} catch (NoSuchElementException e) {}
	}
	
	/**
	 * @return the Keyword that got pushed out of selected keywords queue, or null if nothing got pushed out
	 */
	public Keyword switchSelected(Square bookSpace, Keyword k) {
		boolean wasSelectedBeforehand = removeFromSelected(k);
		Keyword kickedOut = null;
		TransitioningScreenElement<Keyword> newOne = new TransitioningScreenElement<Keyword>(k);
		if (!wasSelectedBeforehand) {
			selected.add(newOne);
			if (selected.size() > Params.visualS.maxKeywordsSelected())
				// kick out oldest selected
				kickedOut = selected.remove().get();
		}
		repositionSelected(bookSpace, newOne);
		return wasSelectedBeforehand ? k : (kickedOut != null ? kickedOut : null);
	}
	
	private void repositionSelected(Square bookSpace, TransitioningScreenElement<Keyword> newOne) {
		for (TransitioningScreenElement<Keyword> tsk : selected) {
			Vec2 pos = getSelectedScaledPosition(bookSpace, tsk.get());
			Vec2 shiftedPos = pos.clone();
			float size = bookSpace.getSize()*Params.visualS.kTextOnBookSpaceHeightRatio();
			
			if (bookSpace.getCenterY() > pos.y) {
				shiftedPos.y = bookSpace.getMinY()-size;
			} else {
				shiftedPos.y = bookSpace.getMaxY()+size;
			}
			
			if (tsk == newOne) {
				tsk.setPosition(pos, 0);
				tsk.setCPos(pos, 0);
				tsk.setSize(size*6, 0);
				tsk.setCDiameter(0, 0);
			}
			float bubbleSize = bookSpace.getSize()*Params.visualS.kTextOnBookBubbleSpaceHeightRatio();
			tsk.setPosition(shiftedPos, Params.kTextOnBookSpaceFadingTicks);
			tsk.setCPos(pos, Params.kTextOnBookSpaceFadingTicks);
			tsk.setSize(size, Params.kTextOnBookSpaceFadingTicks);
			tsk.setCDiameter(bubbleSize, Params.kTextOnBookSpaceFadingTicks);
		}
	}
	
	private boolean removeFromSelected(Keyword k) {
		Iterator<TransitioningScreenElement<Keyword>> iterator = selected.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().get() == k) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}
	
	public ConcurrentLinkedQueue<TransitioningScreenElement<Keyword>> getTSelected() {
		return selected;
	}
	
	public Keyword[] getSelected() {
		Keyword[] result = new Keyword[selected.size()];
		Iterator<TransitioningScreenElement<Keyword>> iterator = selected.iterator();
		for (int i=0; i<result.length; i++) {
			result[i] = iterator.next().get();
		}
		return result;
	}
	
	public Vec2 getSelectedScaledPosition(Square bookSpace, Keyword k) {
//		assertTrue(selected.contains(k));
		Keyword[] selArr = getSelected();
		Vec2[] pos = getSelectedScaledPositions(bookSpace);
		for (int i=0; i<selArr.length; i++)
			if (selArr[i] == k)
				return pos[i];
		throw new NoSuchElementException();
	}
	
	public Vec2[] getSelectedScaledPositions(Square bookSpace) {
		Keyword[] selArr = getSelected();
		Vec2[] selectedPos = new Vec2[selArr.length];
		for (int i=0; i<selArr.length; i++)
			selectedPos[i] = selArr[i].getPositionAsVec2();
		// do not put them right at the border: leave some space (0.8)
		return bookSpace.scalePointsIntoThisSquare(selectedPos, space(),
			Params.visualS.kMagnetDynamicScalingIntoAvailableSpace(),
			Params.visualS.kMagnetPosInset());
	}
	
	public Keyword getMostRecentSelected() {
		// TODO 
		return getSelected()[selected.size()-1];
	}
	
	public float getInSelectedAmountPercentage(Book b) {
		return getInSelectedAmount(b)/selected.size();
	}
	
	public int getInSelectedAmount(Book b) {
		int result = 0;
		for (TransitioningScreenElement<Keyword> k : selected)
			if (b.hasKeyword(k.get()))
				result++;
		return result;
	}
	
	@Deprecated
	public Set<Book> getAllSelectedBooks() {
		Set<Book> books = new HashSet<Book>();
		for (Keyword k : this) {
			if (k.isSelected()) {
				books.addAll(k.getAssociatedBooks());
			}
		}
		return books;
	}
	
	public float getRmax() {
		return radiusMax;
	}
	
	private int getHighestBookCountOfAllKeywords() {
		int high = Integer.MIN_VALUE;
		for (Keyword k : this) {
			high = k.getBooksCount() > high ? k.getBooksCount() : high;
		}
		assertTrue(0 < high && high < Integer.MAX_VALUE);
		return high;
	}
	
	private int getLowestBookCountOfAllKeywords() {
		int low = Integer.MAX_VALUE;
		for (Keyword k : this) {
			low = k.getBooksCount() < low ? k.getBooksCount() : low;
		}
		// TODO low was 0. disabled this assertion.
//		assertTrue(0 < low && low < Integer.MAX_VALUE);
		return low;
	}
	
	public float getRelativeSize(Keyword k) {
		float relativeSize = (float) ( k.getBooksCount()-getLowestBookCountOfAllKeywords() )
			/ (float) (getHighestBookCountOfAllKeywords()-getLowestBookCountOfAllKeywords());
		assertTrue(0 <= relativeSize && relativeSize <= 1);
		return relativeSize;
	}
	
	public void loadTextShapes() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InterruptedException {
		new MyConcurrentWorker<Keyword>(this, LoadTextShapeWorkunit.class,
			new ProgressBar("Loading Text Shapes", this.size())).run(Params.loadShapesNumberOfProcesses);
	}
	
	public static class LoadTextShapeWorkunit extends WorkerTemplate<Keyword> {
		public void run() {
			try {
				val.setTextShape();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void adjustRadiusSizes() {
		// calculate the radius so that the area of all keyword circles = space area
		// this assumes that all circles will have the maximum radius (which is not the case)
		radiusMax = (float) (Math.sqrt((space().getInnerCircleArea()/ (2*Math.PI*(float) size()))));
		radiusMin = radiusMax / 10f;
		if (radiusMin < Params.visualS.absoluteMinimumKeywordRadius()) {
			radiusMin = Params.visualS.absoluteMinimumKeywordRadius();
		}
		
		float areaTakenByKCircles = 0;
		
		for (Keyword k : this) {
			k.setRadius((float) (radiusMin+(radiusMax-radiusMin)*Math.sqrt(getRelativeSize(k))));
			areaTakenByKCircles += k.getCircleArea();
		}
		
		// the area of the circles should take up a certain percentage of the whole space area
		float radiusAdjustment = (float) Math.sqrt((space().innerCircleArea()) / areaTakenByKCircles
				* Params.visualS.areaRatioBetweenKeywordCircleAreaAndTotalKeywordArea());
		radiusMax *= radiusAdjustment;
		
		areaTakenByKCircles = 0;
		for (Keyword k : this) {
			k.setRadius((float) (k.getRadius()*radiusAdjustment));
			areaTakenByKCircles += k.getCircleArea();
		}
		assertEquals(Params.visualS.areaRatioBetweenKeywordCircleAreaAndTotalKeywordArea(),
				areaTakenByKCircles/space().innerCircleArea(), 0.05f);
	}
	
	private Keyword[] getAsSortedArray() {
		Keyword[] result = toArray(new Keyword[0]);
		Arrays.sort(result, new Comparator<Keyword>() {
			@Override
			public int compare(Keyword o1, Keyword o2) {
				return o1.getBooksCount() - o2.getBooksCount();
			}
		});
		return result;
	}
	
	public float jiggleMinDist() {
		return spaceWithBorder().width*Params.visualS.jiggleMinDistBetweenCircleBordersRatioToSpaceWidth();
	}
	
	public boolean jiggleKeywords(int maximumNumberOfIterations) {
		return jiggleKeywords(
			getAsSortedArray(),
			jiggleMinDist(),
			maximumNumberOfIterations);
	}
	
	/**
	 * @return whether a state of no overlapping could be achieved
	 */
	private boolean jiggleKeywords(Keyword[] k1, double minimumDistanceBetweenCircleBorders, int maximumNumberOfIterations) {
		// TODO this algorithm does not guarantee a result without overlaps
		assertTrue( minimumDistanceBetweenCircleBorders > 0 && maximumNumberOfIterations > 0);
		boolean needSomeMoreJiggeling = true;
//		ProgressBar progress = new ProgressBar("jiggling around", maximumNumberOfIterations);
		int numberOfInterations = 0;
		while (needSomeMoreJiggeling && numberOfInterations++ < maximumNumberOfIterations) {
			needSomeMoreJiggeling = false;
			for (Keyword k : k1) {
				for (Keyword j : k1) {
					if (k != j) {
						Circle kC = k.getCircle();
						if (kC.repelFrom(j.getCircle(), minimumDistanceBetweenCircleBorders)) {
							k.setPosition(kC.getCentreAsPoint2DFloat());
							needSomeMoreJiggeling = true;
						}
					}
				}
			}
//			progress.progress();
		}
//		progress.done();
		return !needSomeMoreJiggeling;
	}

}
