package matt.ui;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import matt.parameters.Params;
import matt.ui.screenElement.BodyPlus;
import matt.ui.screenElement.Book;
import matt.ui.screenElement.BookSet;
import matt.ui.screenElement.InteractionLog;
import matt.ui.screenElement.Keyword;
import matt.ui.screenElement.KeywordSet;
import matt.ui.screenElement.ScreenElement;
import matt.ui.screenElement.ScreenElementSet;
import matt.util.Circle;
import matt.util.PolarPoint;
import matt.util.Square;
import matt.util.Util;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import static org.junit.Assert.*;

// playing billard is done here. 
// to keep the physics independent of the screen dimensions, position and size will be static at origin
// that means that a mapping from screen coordinates to world will be necessary
public class WorldPlus extends World {
	
	private Body ground;
	// world space is constant, i.e. independent of the screen resolution, to ensure predictable physics
	private final Square worldSpace = new Square(new Vec2(0, 0), Params.radiusWorldBubble*2);
//	private ConcurrentLinkedQueue<Book> fountainQueue = new ConcurrentLinkedQueue<Book>();
	public Vec2 fountainPosition;
	public Vec2 touchedBooksMagnet = new Vec2(0, 0);
	public ScreenElement<?> lastPressedOnBody;
	
	public WorldPlus() {
		// no gravity
		super(new Vec2(0.0f, 0.0f), true);
		// create world boundary
		BodyDef bd = new BodyDef();
		bd.position.set(0, 0);
		ground = createBody(bd);
		if (Params.worldGroundBodyIsSphere) {
			createRegularNEdgeFixture(ground, worldSpace.getSize()/2, Params.worldGroundSqhereEdges);			
		} else {
			createRect(ground, worldSpace.getSize()/2);			
		}
	}
	
	public Square space() {
		return worldSpace;
	}
	
	public ScreenElementSet<Book> step(float dt, KeywordSet kwds, BookSet books, InteractionLog<Book> iLogBooks,
			MousePath mousePath, long ticksDeltaSinceBooksAddedLastTime) {
		runFountain(iLogBooks, mousePath, kwds, books);
		ScreenElementSet<Book> activeBooks = books.getAllActiveScreenElements();
		updateFadingOut(activeBooks);
		
		for (ScreenElement<?> b : activeBooks)
			b.updateRadius();
		updateMagnetising(kwds, books, activeBooks, ticksDeltaSinceBooksAddedLastTime);
		step(dt);
		return activeBooks;
	}
	
	private void step(float dt) {
		super.step(dt, Params.velocityIterations, Params.positionIterations);
	}
	
	// dampen force over time, if no interaction happened, to prevent book shaking
	private float getForceDampeningFactor(long ticksDeltaSinceBooksAddedLastTime) {
		return Params.forceDampeningFactor*(1-(float) Util.percentiseIn(
			ticksDeltaSinceBooksAddedLastTime, Params.ticksTilDampeningFactorFactorTurnsZero));
	}
	
	private void updateMagnetising(KeywordSet kwds, BookSet books, ScreenElementSet<Book> activeBooks, long ticksDeltaSinceBooksAddedLastTime) {
		Keyword[] selected = kwds.getSelected();
		Vec2[] selectedPosScaled = kwds.getSelectedScaledPositions(books.space());
		for (Book b : activeBooks)
			for (int i=0; i<selected.length; i++)
				if (b.hasKeyword(selected[i]))
					b.magnetiseTowards(getForceDampeningFactor(ticksDeltaSinceBooksAddedLastTime), selectedPosScaled[i]);
	}
	
	private void updateFadingOut(ScreenElementSet<Book> se) {
		Iterator<Book> bookIterator = se.iterator();
		while(bookIterator.hasNext()) {
			Book b = bookIterator.next();
			if (b.isFading()) {
				float fadingDegree = b.getFadingDegree();
				assertTrue(fadingDegree >= 0);
				// add random movement to fading out books
				b.setLinearVelocityAdd(new Vec2((float) (Math.random()-0.5)*12, (float) (Math.random()-0.5)*12));
				float newRadius = b.getRadius()*(1-fadingDegree);
				if (fadingDegree >= 1 || newRadius < Params.visualS.absoluteMinimumCircleRadius()) {
					b.setActive(false);
					bookIterator.remove();
				} else {
					b.setRadius(newRadius);
				}
			}
		}
	}
	
	private void runFountain(InteractionLog<Book> iLogBooks, MousePath mousePath, KeywordSet kwds, BookSet books) {
		int count = 0;
		while (count++ < Params.visualS.amountOfBooksComingInThroughFountainEachFrame()) {
			Book next = books.getNextInQueueAndDequeue();
			if (next == null)
				break;
			
			Vec2 kPos = next.getPositionToFadeIn();
			next.setPosition(
				// easing physics through small displacements
				kPos.x+(float) ((Math.random()-0.5f)*5),
				kPos.y+(float) ((Math.random()-0.5f)*5));
			next.setLinearVelocityAddTowardsPosition(kPos, -50*(float) (Math.random()+0.5));
			next.setActive(true);
		}
	}
	
	public void updateLastPressedOnBody(Point2D.Float mousePosition, BookSet books) {
		for (ScreenElement<?> b : books) {
			if (b.isActive()) {
				Circle pos = b.getCircle();
				if (pos.isInside(mousePosition)) {
					lastPressedOnBody = b;
				}
			}
		}
	}
	
//	public void addToFountain(Book e) {
//		assertTrue(!e.isActive());
//		fountainQueue.add(e);
//	}
	
	// not tested yet
	@SuppressWarnings("unused")
	private BodyPlus createRectBody(Square reference, Rectangle2D.Float rect, BodyType bodyType) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(
			space().translateLengthFrom(reference, rect.width/2f),
			space().translateLengthFrom(reference, rect.height/2f));
		
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1.0f;

		BodyDef bdz = new BodyDef();
		bdz.linearDamping = 1;
		bdz.angularDamping = 5;
		bdz.type = bodyType;
		bdz.position.set(space().translatePointFrom(reference,
			new Vec2((float) rect.getCenterX(), (float) rect.getCenterY())));
		
		Body b = createBody(bdz);
		b.createFixture(fd);
		return new BodyPlus(b, this);
	}
	
	public BodyPlus createCircleBody(Square reference, Circle circle, BodyType bodyType) {
		CircleShape shape = new CircleShape();
		shape.m_radius = space().translateLengthFrom(reference, circle.getRadius());
		
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1.0f;

		BodyDef bdz = new BodyDef();
		bdz.linearDamping = 1;
		bdz.angularDamping = 5;
		bdz.type = bodyType;
		bdz.position.set(space().translatePointFrom(reference, circle.getCentreAsPoint2DFloat()));
		
		Body b = createBody(bdz);
		b.createFixture(fd);
		return new BodyPlus(b, this);
	}
	
	protected void deactivateAllBodies() {
		for (Body b : getAllBodiesExceptGround()) {
			b.setActive(false);
		}
	}
	
	public ArrayList<Body> getAllBodiesExceptGround() {
		ArrayList<Body> bs = new ArrayList<Body>(getBodyCount());
		Body wbdy = getBodyList();
		while (wbdy != null) {
			if (wbdy != ground) {
				bs.add(wbdy);
			}
			wbdy = wbdy.getNext();
		}
		return bs;
	}
	
	private void createRegularNEdgeFixture(Body ground, float radius, int edges) {
		createRegularNEdgeFixture(ground, radius, edges, 0);
	}
	
	private void createRect(Body ground, float radius) {
		createEdgeFixture(ground, -radius, -radius, -radius, radius);
		createEdgeFixture(ground, -radius, radius, radius, radius);
		createEdgeFixture(ground, radius, radius, radius, -radius);
		createEdgeFixture(ground, radius, -radius, -radius, -radius);
	}

	private void createRegularNEdgeFixture(Body ground, float radius, int edges, double initialRotationInRadiants) {
		assertTrue( edges > 2);
		double degrees = Math.PI * 2d / edges;
		for (int i = 0; i < edges; i++)
			createEdgeFixture(ground, new PolarPoint(degrees * i + initialRotationInRadiants, radius),
					new PolarPoint(degrees * (i + 1) + initialRotationInRadiants, radius));
	}

	private void createEdgeFixture(Body ground, PolarPoint p1, PolarPoint p2) {
		createEdgeFixture(ground, (float) p1.getX(), (float) p1.getY(), (float) p2.getX(), (float) p2.getY());
	}

	private void createEdgeFixture(Body ground, float ax, float ay, float ex, float ey) {
		PolygonShape shape = new PolygonShape();
		shape.setAsEdge(new Vec2(ax, ay), new Vec2(ex, ey));
		ground.createFixture(shape, 0.0f);
	}

}
