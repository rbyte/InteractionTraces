package matt.ui.screenElement;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.jbox2d.common.Vec2;

import matt.parameters.Params;
import matt.setup.DatabaseConfig;
import matt.ui.PAppletPlus;
import matt.ui.PImagePlus;
import matt.util.StringHandling;

public class Book extends ScreenElement<BookSet> implements Comparable<Book> {
	
	private String id;
	private String author;
	private long isbn;
	private int pages;
	private int pubDate;
	private int dewey;
	private String articleText;
	private HashSet<Keyword> subject;
	private HashSet<Keyword> genre;
	private PImagePlus nativeCover;
	
	private float significanceInSelection = 0f; // in [-1,1]
	
	Book(	BookSet parent,
			PAppletPlus p,
			BodyPlus b,
			String id,
			String title,
			String author,
			String isbn,
			String pages,
			String pubDate,
			String dewey,
			String subject,
			String genre,
			String articleText,
			KeywordSet kwds) {
		super(parent, p, b, title);
		
		this.id = id;
		this.author = author;
		this.isbn = Long.parseLong(isbn);
		this.pages = Integer.parseInt(pages);
		this.pubDate = Integer.parseInt(pubDate);
		this.dewey = Integer.parseInt(dewey);
		this.articleText = articleText;
		
		Keyword k;
		this.subject = new HashSet<Keyword>();
		for (String keyword : DatabaseConfig.extractKeywordsOfOneBook(subject)) {
			if ((k = kwds.get(keyword)) != null) {
				this.subject.add(k);
			}
		}
		this.genre = new HashSet<Keyword>();
		for (String keyword : DatabaseConfig.extractKeywordsOfOneBook(genre)) {
			if ((k = kwds.get(keyword)) != null) {
				this.genre.add(k);
			}
		}
	}
	
	public String getID()				{ return id; }
	public String getTitle()			{ return getName(); }
	public String getAuthor()			{ return author; }
	public long getIsbn()				{ return isbn; }
	public int getPages()				{ return pages; }
	public int getPubDate()				{ return pubDate; }
	public int getDewey()				{ return dewey; }
	public HashSet<Keyword> getSubject(){ return subject; }
	public HashSet<Keyword> getGenre()	{ return genre; }
	public String getArticle()			{ return articleText; }
	public PImagePlus getCover()		{ return nativeCover; }
	
	public float getSignificanceInSelection() {
		return significanceInSelection;
	}
	
	public void setSignificanceInSelection(float val) {
		assertTrue(-1 <= val && val <= 1);
		this.significanceInSelection = val;
	}
	
	public PImagePlus loadCover(PImagePlus img) {
		nativeCover = img;
		int newCoverWidth = (int) Math.ceil(nativeCover.width*Params.coverSizeMultiplicator);
		if ((float) nativeCover.width / (float) nativeCover.height != Params.visualS.coverAspect())
			nativeCover.resize(newCoverWidth, (int) (newCoverWidth/Params.visualS.coverAspect()));
		
		return nativeCover;
	}
	
	public boolean hasKeyword(Keyword k) {
		return getGenre().contains(k) || getSubject().contains(k);
	}
	
	public void magnetiseTowardsItsKeywords(float forceFactor) {
		for (Keyword genre : getGenre())
			magnetiseTowards(genre, forceFactor);
		for (Keyword subject : getSubject())
			magnetiseTowards(subject, forceFactor);
	}
	
	private void magnetiseTowards(Keyword k, float forceFactor) {
		magnetiseTowards(forceFactor, k.getPositionAsVec2In(getSpace()));
	}
	
	public void magnetiseTowards(float forceFactor, Vec2 position) {
		Vec2 fromKtoB = position.sub(getPositionAsVec2());
		setLinearVelocityAdd(fromKtoB.mul(fromKtoB.length()).mul(forceFactor));
	}
	
	public PImagePlus loadCover() {
		return loadCover(getPPlus().loadImage(Params.pathToCovers + isbn + ".jpg"));
	}
	
	public String toStringDetailed() {
		return StringHandling.concat(new Object[] {id, getTitle(), author, isbn, pages, pubDate, dewey, articleText}, ", ")
			+StringHandling.concat(subject.toArray(), ", ", "<", ">")
			+StringHandling.concat(genre.toArray(), ", ", "<", ">");
	}

	@Override
	public int compareTo(Book o) {
		return o.isbn == isbn ? 0 : 1;
	}
	
}
