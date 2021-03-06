package matt.jbox2D;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Main {
	
	public static void main(String[] args) {
		// Make a World
		Vec2 gravity = new Vec2(0.0f, -10.0f);
		boolean doSleep = true;
		World world = new World(gravity, doSleep);
		
		// Make a Body for the ground via definition and shape binding that gives it a boundary
		BodyDef groundBodyDef = new BodyDef(); // body definition
		groundBodyDef.position.set(0.0f, -10.0f); // set bodydef position
		Body groundBody = world.createBody(groundBodyDef); // create body based on definition
		PolygonShape groundBox = new PolygonShape(); // make a shape representing ground
		groundBox.setAsBox(50.0f, 10.0f); // shape is a rect: 100 wide, 20 high
		groundBody.createFixture(groundBox, 0.0f); // bind shape to ground body
		
		// Make another Body that is dynamic, and will be subject to forces.
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC; // dynamic means it is subject to forces
		bodyDef.position.set(0.0f, 4.0f);
		Body body = world.createBody(bodyDef);
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1.0f, 1.0f);
		FixtureDef fixtureDef = new FixtureDef(); // fixture def that we load up with the following info:
		fixtureDef.shape = dynamicBox; // ... its shape is the dynamic box (2x2 rectangle)
		fixtureDef.density = 1.0f; // ... its density is 1 (default is zero)
		fixtureDef.friction = 0.3f; // ... its surface has some friction= coefficient
		body.createFixture(fixtureDef); // bind the dense, friction-laden fixture to the body
		
		// Simulate the world
		float timeStep = 1.0f / 60.f;
		int velocityIterations = 6;
		int positionIterations = 2;
		for (int i = 0; i < 60; ++i) {
			world.step(timeStep, velocityIterations, positionIterations);
			Vec2 position = body.getPosition();
			float angle = body.getAngle();
			System.out.printf("%4.2f %4.2f %4.2f\n", position.x, position.y, angle);
		}

		System.out.println("Done.");
	}

}
