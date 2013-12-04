package matt.jbox2D;

import java.util.Random;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class AssertionErrorReproductionForSetActive {
	
	public static void main(String[] args) {
		checkForEnabledAssertion();
		int n = 200;
		Body[] bodies = new Body[n];
		Random randomGenerator = new Random();
	    World world = new World(new Vec2(0, -10), true);
	    
	    for (int i=0; i<n; i++)
	    	bodies[i] = addBodyToWorld(world);
	    
	    while(true) {
	        world.step(1.0f/60.0f, 6, 2);
	        bodies[randomGenerator.nextInt(n)].setActive(randomGenerator.nextBoolean()); 
	    }
	}
	
	public static Body addBodyToWorld(World world) {
	    BodyDef bodyDef = new BodyDef();
	    bodyDef.type = BodyType.DYNAMIC;
	    bodyDef.position.set(0, 0);
	    
	    Body b = world.createBody(bodyDef);
	    PolygonShape dynamicBox = new PolygonShape();
	    dynamicBox.setAsBox(1, 1);
	    FixtureDef fixtureDef = new FixtureDef();
	    fixtureDef.shape = dynamicBox;
	    fixtureDef.density=1;
	    fixtureDef.friction=0.3f;
	    b.createFixture(fixtureDef);
	    return b;
	}
	
	public static boolean checkForEnabledAssertion() throws AssertionError {
		try {
			assert false;
		} catch (AssertionError e) {
			// they are enabled ... good
			return true;
		}
		throw new AssertionError("Assertions are diabled!");
	}

}
