package matt.jbox2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.Random;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

import matt.parameters.Params;
import matt.ui.PAppletPlus;
import matt.ui.WorldPlus;
import matt.ui.screenElement.BodyPlus;
import matt.ui.screenElement.ScreenElement;
import matt.ui.screenElement.ScreenElementSet;
import matt.util.Circle;

public class TestWorld extends PAppletPlus {
	
	private static final long serialVersionUID = 1L;
	WorldPlus world = new WorldPlus();
	TestScreenElemSet tset = new TestScreenElemSet(new Rectangle2D.Float(50, 50, 300, 300));
	
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
		System.out.println(tset.space());

		for (int i=0; i<200; i++) {
			TestScreenElem e = new TestScreenElem(
					tset, world.createCircleBody(
						tset.space(),
						new Circle(new Point2D.Float(130, 130), 5),
						BodyType.DYNAMIC)
					);
			tset.add(e);
		}
			
	}
	
	public void keyPressedPlus() {
		switch (key) {
		case 'r': world.step(1f/60f, Params.velocityIterations, Params.positionIterations); break;
		default:
			break;
		}
	}
	
	public void drawPlus() {
		background(255);
		
		fill(200);
		circle(tset.space().getCenterX(), tset.space().getCenterY(), tset.space().getSize());
		Random randomGenerator = new Random();
		
		world.step(1f/60f, Params.velocityIterations, Params.positionIterations);
		
		fill(0);
		for (Body body : world.getAllBodiesExceptGround()) {
			
			body.setActive(randomGenerator.nextBoolean());
			
			if (randomGenerator.nextBoolean() && randomGenerator.nextBoolean()) {
				
//				for (Fixture f = body.m_fixtureList; f != null; f = f.m_next) {
//					f.m_proxy = null;
//				}
				
				
//				Fixture f = body.getFixtureList();
//				int count = 0;
//				while (f != null) {
//					try {
//						body.destroyFixture(f);
//					} catch (AssertionError e) {
//						System.err.println("err");
//					}
//					
//					f = f.getNext();
//					count++;
//				}
//				System.out.println("number of fixtures: "+count);
				
			}

			
//			System.out.println(body.getPosition().x+", "+body.getPosition().y);
		}
		
		
		
		for (TestScreenElem e : tset) {
//			System.out.println(e.getCircle());
//			e.setActive(randomGenerator.nextBoolean());
			
//			e.setLinearVelocityAdd(new Vec2(
//				randomGenerator.nextFloat()-0.5f,
//				randomGenerator.nextFloat()-0.5f));
			if (e.isActive())
				circle(e.getCircle());
		}
		

	}
	
	
}
