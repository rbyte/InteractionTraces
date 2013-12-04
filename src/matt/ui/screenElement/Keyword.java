package matt.ui.screenElement;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.contract4j5.contract.*;

import processing.core.PShape;
import matt.parameters.Params;
import matt.setup.DatabaseQueryIssuer;
import matt.setup.SVGconverter;
import matt.ui.MousePath;
import matt.ui.PAppletPlus;
import matt.util.Square;
import matt.util.Util;
import static org.junit.Assert.assertTrue;

@Contract
public class Keyword extends ScreenElement<KeywordSet> {
	
	private ScreenElementSet<Book> booksAssociated;
	private PShape textShape;
	private PShape textShapeSketchy;
	
	Keyword(KeywordSet parent, PAppletPlus p, BodyPlus b, String name) throws IOException {
		super(parent, p, b, name);
	}
	
	protected void addBooksAssociation(BookSet allBooks, DatabaseQueryIssuer dbex) {
		booksAssociated = new ScreenElementSet<Book>(allBooks.spaceWithBorder());
		String query = "SELECT isbn FROM "+Params.databaseTableSetup.linkedTable
				+" WHERE "+Params.databaseTableSetup.columnName+" = \""+getName()+"\"";
		Book b;
		for (long isbn : dbex.getLongArray(query)) {
			if ((b = allBooks.get(isbn)) != null) {
				booksAssociated.add(b);
			}
		}
	}
	
	
	public boolean isSelected() {
		return getParent().isSelected(this);
	}
	
	/**
	 * @return the Keyword that got pushed out of selected keywords queue, or null if nothing got pushed out
	 */
	public Keyword switchSelected(Square bookSpace) {
		return getParent().switchSelected(bookSpace, this);
	}
	
	public void setTextShape() throws IOException {
//		System.out.println(this.getName());
		textShape = getPPlus().loadShape(SVGconverter.run(getName(), Params.visualS.kTextFamily()));
		if (Params.loadSketchyTextShapes)
			textShapeSketchy = getPPlus().loadShape(SVGconverter.run(getName(), Params.visualS.kTextFamilySketchy()));
		if (Params.colourKTextThroughFill) {
			textShape.disableStyle();
			if (Params.loadSketchyTextShapes)
				textShapeSketchy.disableStyle();
		}
	}
	
	public boolean isInsideEnlarged(MousePath mousePath, InteractionLog<Keyword> log, boolean pureDrag) {
		Point2D.Float pos = pureDrag ? mousePath.getCurrentDragged() : mousePath.getCurrent();
		if (isSelected()) {
			if (getTextBox(mousePath, log).contains(pos))
				return true;
		}
		return getCircle().addToRadius(getSpace().getSize()
			*Params.visualS.jiggleMinDistBetweenCircleBordersRatioToSpaceWidth()/2).isInside(pos);
	}
	
	public Rectangle2D.Float getTextBox(MousePath mousePath, InteractionLog<Keyword> log) {
		float width = getTextWidth(mousePath, log);
		float actualTextHeight = getTextHeight(width);
		return new Rectangle2D.Float(
				getCircle().getCenterX(), getCircle().getCenterY()-actualTextHeight/2, width, actualTextHeight);
	}
	
	public float getTextWidth(MousePath mousePath, InteractionLog<Keyword> log) {
		float size = Util.linearInterpolation(
			getSpace().getSize()*Params.visualS.kTextMinSizeRatioToSpaceSize(),
			getSpace().getSize()*Params.visualS.kTextMaxSizeRatioToSpaceSize(),
			Params.visualS.kTextSize(this, mousePath, log, false));
		return (float) (Math.pow(getName().length(), 0.73)*size*1.1);
	}
	
	public float getTextHeight(float width) {
		return textShape.height / textShape.width * width;
	}
	
	public PShape getTextShape() {
		return textShape;
	}
	
	public PShape getTextShapeSketchy() {
		assertTrue(Params.loadSketchyTextShapes);
		return textShapeSketchy;
	}
	
	public int getBooksCount() {
		return booksAssociated.size();
	}
	
	public float getRmax() {
		return getParent().getRmax();
	}
	
	@Post(Params.returnIn0to1)
	public float getRadiusPercentage() {
		return getRadius()/getRmax();
	}
	
	public ScreenElementSet<Book> getAssociatedBooks() {
		return booksAssociated;
	}
	
	public ScreenElementSet<Book> getAssociatedActiveBooks() {
		ScreenElementSet<Book> result = new ScreenElementSet<Book>(getAssociatedBooks().spaceWithBorder());
		for (Book b : getAssociatedBooks()) {
			if (b.isActive())
				result.add(b);
		}
		return result;
	}
	
	public ScreenElementSet<Book> getAssociatedActiveBooksThatAreNotInOtherSelected() {
		ScreenElementSet<Book> result = new ScreenElementSet<Book>(getAssociatedBooks().spaceWithBorder());
		Keyword[] selectedKeywords = getParent().getSelected();
		loop: for (Book b : getAssociatedBooks()) {
			if (b.isActive()) {
				// add only if not in other selected keywords
				for (Keyword k : selectedKeywords)
					if (k.getAssociatedBooks().contains(b))
						continue loop;
				result.add(b);
			}
		}
		return result;
	}
	
	public ScreenElementSet<Book> getAssociatedInactiveUnqueuedBooks() {
		ScreenElementSet<Book> result = new ScreenElementSet<Book>(getAssociatedBooks().spaceWithBorder());
		for (Book b : getAssociatedBooks()) {
			if (!b.isActive() && !b.isInQueueToFadeIn())
				result.add(b);
		}
		return result;
	}

}
