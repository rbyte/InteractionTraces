package matt.ui.screenElement;

import java.awt.geom.Rectangle2D;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.jbox2d.dynamics.BodyType;

import matt.parameters.Params;
import matt.setup.DatabaseConnection;
import matt.setup.DatabaseQueryIssuer;
import matt.ui.PAppletPlus;
import matt.ui.PImagePlus;
import matt.ui.PToolbox;
import matt.ui.WorldPlus;
import matt.util.Circle;
import matt.util.MyConcurrentWorker;
import matt.util.RedistributeSet;
import matt.util.MyConcurrentWorker.WorkerTemplate;
import matt.util.ProgressBar;

public class BookSet extends ScreenElementSet<Book> {
	
	private static final long serialVersionUID = 4324708220872852728L;
	
	public BookSet(WorldPlus world, PAppletPlus pplus, int width, int height, KeywordSet kwds)
			throws ClassNotFoundException, SQLException {
		super(spaceWithBorder(width, height));
		
		System.out.print("Getting book tables from database... ");
		DatabaseQueryIssuer dbex = new DatabaseQueryIssuer(new DatabaseConnection(Params.databaseAccessSetup));
		HashMap<String, String[]> masterTable = dbex.getTable(Params.databaseTableSetup.masterTable);
		HashMap<String, String[]> articlesTable = dbex.getTable("articles");
		long booksCount = dbex.count(Params.databaseTableSetup.masterTable);
		dbex.closeDatabaseConnection();
		System.out.println("done.");
		
		// shrink the world space a bit so that all creations are entirely in it
		Circle[] pCircles = PToolbox.getPhyllotacticLayout(
			space().clone().multSizeAroundStaticCenter(0.9f),
			(int) booksCount);
		
		for (int i=0; i<booksCount; i++) {
			String articleID = masterTable.get("article")[i].substring(1);
			String articleText = articlesTable.get("text")[getPositionInArray(articlesTable.get("id"), articleID)];
			
			Book b = new Book(
				this,
				pplus,
				world.createCircleBody(space(), pCircles[i], BodyType.DYNAMIC),
				masterTable.get("id")[i],
				masterTable.get("title")[i],
				masterTable.get("author")[i],
				masterTable.get("isbn")[i],
				masterTable.get("pages")[i],
				masterTable.get("pubDate")[i],
				masterTable.get("dewey")[i],
				masterTable.get("subject")[i],
				masterTable.get("genre")[i],
				articleText,
				kwds);
			
			add(b);
		}
		
		
		if (Params.loadDummyCover) {
			System.out.println("Loading Dummy As Cover.");
			PImagePlus cover = pplus.loadImage(Params.dummyCover);
			loadDummyCover(cover);
		} else {
			loadCoversInParallel();
		}
	}
	
	public void updateSignificanceAndRadiusToBees(KeywordSet kwds) {
		Book[] bs = getAllActiveAndQueuedNonFadingScreenElements().toArray(new Book[] {});
		if (bs.length == 0)
			return;
		
		float[] result = new float[bs.length];
		for (int i=0; i<result.length; i++)
			result[i] = kwds.getInSelectedAmount(bs[i]);
		
		result = RedistributeSet.scaleIntoMinusOneToOneWithMeanOfZero(result);
		
		for (int i=0; i<result.length; i++)
			bs[i].setSignificanceInSelection(result[i]);
		
		float areaThatBooksOccupy = (float) (
				(Params.worldGroundBodyIsSphere ? space().innerCircleArea(): space().getArea())
				*Params.visualS.booksToAreaRatio());
		float meanAreaOfEachBook = areaThatBooksOccupy/bs.length;
		float maxBookRadius = space().getSize()/10f;
		float maxSingleBookArea = (float) (Math.PI*maxBookRadius*maxBookRadius);
		float maxMuliplier = maxSingleBookArea/meanAreaOfEachBook;
//		System.out.println("maxMuliplier: "+maxMuliplier);
		
		float[] adjusted = new RedistributeSet(result).rescale(0.05f, 1, maxMuliplier < 1 ? 2f : maxMuliplier, true);
		
		for (int i=0; i<result.length; i++) {
			float areaThisBookOccupies = adjusted[i] * meanAreaOfEachBook;
			bs[i].setRadiusToBe((float) Math.sqrt(areaThisBookOccupies/Math.PI));
		}
	}
	
	private static int getPositionInArray(String[] arr, String id) {
		for (int i=0; i<arr.length; i++) {
			if (arr[i].equals(id))
				return i;
		}
		throw new NoSuchElementException();
	}
	
	private static Rectangle2D.Float spaceWithBorder(int width, int height) {
		boolean vertical = width < height;
		return Params.visualS.booksAndKeywordInOneView()
			? new Rectangle2D.Float(0, 0,
				vertical ? width	: height,
				vertical ? width	: height)
			: new Rectangle2D.Float(0, 0,
				vertical ? width		: width/2f,
				vertical ? height/2f	: height);
	}
	
	public Book get(long isbn) {
		for (Book b : this) {
			if (b.getIsbn() == isbn) {
				return b;
			}
		}
		return null;
	}
	
	public boolean contains(Long isbn) {
		for (Book b : this) {
			if (b.getIsbn() == isbn) {
				return true;
			}
		}
		return false;
	}
	
	public void loadDummyCover(PImagePlus cover) {
		for (Book b : this) {
			b.loadCover(cover.clonePlus());
		}
	}
	
	public void loadCoversInParallel() {
		try {
			new MyConcurrentWorker<Book>(this, LoadCoverWorkunit.class,
				new ProgressBar("Loading Covers", this.size())).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class LoadCoverWorkunit extends WorkerTemplate<Book> {
		public void run() {
			val.loadCover();
		}
	}

}
