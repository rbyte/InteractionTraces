package matt.jbox2D;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import matt.parameters.Params;
import matt.ui.PAppletPlus;
import matt.ui.PToolbox;
import matt.ui.screenElement.BodyPlus;
import matt.ui.screenElement.ScreenElement;
import matt.ui.screenElement.ScreenElementSet;
import matt.util.Circle;
import matt.util.PolarPoint;

public class TestWorldSimpified extends PAppletPlus {
	
	private static final long serialVersionUID = 1L;
	World world = new World(new Vec2(0, 0), true);
	TestScreenElemSet tset = new TestScreenElemSet(new Rectangle2D.Float(50, 50, 300, 300));
	
	int n = 150;
	CircleShape[] shape = new CircleShape[n];
	FixtureDef[] fd = new FixtureDef[n];
	BodyDef[] bdz = new BodyDef[n];
	Body[] body = new Body[n];
	
	private static class TestScreenElemSet extends ScreenElementSet<TestScreenElem> {
		private static final long serialVersionUID = 1L;
		TestScreenElemSet(Float spaceWithBorder) {
			super(spaceWithBorder);
		}
	}
	
	private static class TestScreenElem extends ScreenElement<TestScreenElemSet> {
		TestScreenElem(TestScreenElemSet parent, BodyPlus body) {
			super(parent, null, body, "bla");
		}
	}
	
	public void setupPlus() {
		
		float radius = 15;
		float radiusP = radius * 0.95f;
		int borderEdges = 30;
		
		BodyDef bd = new BodyDef();
		Body ground = world.createBody(bd);
		createRegularNEdgeFixture(ground, radius, borderEdges);
		
		Circle[] positions = PToolbox.getPhyllotacticLayout(
				new Rectangle2D.Float(-radiusP, -radiusP, radiusP * 2, radiusP * 2), n);
		
		for (int i=0; i<n; i++) {
			setUp(i, positions[i]);
		}
		
	}
	
	public void keyPressedPlus() {
		switch (key) {
		case 'r': world.step(1f/60f, 2, 2); break;
		default:
			break;
		}
	}
	
	private void setUp(int i, Circle position) {
		shape[i] = new CircleShape();
		shape[i].m_radius = 0.5f + (float) Math.random();
		
		fd[i] = new FixtureDef();
		fd[i].shape = shape[i];
		fd[i].density = 1.0f;
		
		bdz[i] = new BodyDef();
		bdz[i].linearDamping = 5;
		bdz[i].angularDamping = 5;
		bdz[i].type = BodyType.DYNAMIC;
		bdz[i].position.set(position.getCenterX(), position.getCenterY());
		
		body[i] = world.createBody(bdz[i]);
//		body[i].setGravityScale(0);
		body[i].createFixture(fd[i]);
	}
	
	public void drawPlus() {
		background(255);
		
		fill(200);
		circle(tset.space().getCenterX(), tset.space().getCenterY(), tset.space().getSize());
		Random randomGenerator = new Random();
		
		if (drawCycles%10 == 0) {
			body[randomGenerator.nextInt(body.length-1)].setActive(randomGenerator.nextBoolean());
		}
		
		world.step(1f/60f, Params.velocityIterations, Params.positionIterations);
		


//		fill(0);
		
//		for (Body b : getAllBodies()) {
//			circle(b.getPosition().x, b.getPosition().y, b.getFixtureList().getShape().m_radius);
//		}

//		for (TestScreenElem e : tset) {
//			if (e.isActive())
//				circle(e.getCircle());
//		}
		

	}
	
	
	ArrayList<Body> getAllBodies() {
		ArrayList<Body> bs = new ArrayList<Body>(world.getBodyCount());
		Body wbdy = world.getBodyList();
		while (wbdy != null) {
			bs.add(wbdy);
			wbdy = wbdy.getNext();
		}
		return bs;
	}
	

	private void createRegularNEdgeFixture(Body ground, float radius, int edges) {
		createRegularNEdgeFixture(ground, radius, edges, 0);
	}

	private void createRegularNEdgeFixture(Body ground, float radius,
			int edges, double initialRotation) {
		assert edges > 2;
		double degrees = Math.PI * 2d / edges + initialRotation;
		for (int i = 0; i < edges; i++)
			createEdgeFixture(ground, new PolarPoint(degrees * i, radius),
					new PolarPoint(degrees * (i + 1), radius));
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
