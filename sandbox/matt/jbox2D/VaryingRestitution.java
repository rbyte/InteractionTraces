/*******************************************************************************
 * Copyright (c) 2011, Daniel Murphy
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright notice,
 * 	  this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright notice,
 * 	  this list of conditions and the following disclaimer in the documentation
 * 	  and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
/**
 * Created at 1:59:32 PM Jan 23, 2011
 */
package matt.jbox2D;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import matt.parameters.Params;
import matt.ui.PToolbox;
import matt.util.Circle;
import matt.util.PolarPoint;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;


/**
 * @author Daniel Murphy
 */
public class VaryingRestitution {
	
	int n = 150;
	CircleShape[] shape = new CircleShape[n];
	FixtureDef[] fd = new FixtureDef[n];
	BodyDef[] bdz = new BodyDef[n];
	Body[] body = new Body[n];

	World world = new World(new Vec2(0, 0), true);
	
	public static void main(String[] args) {
		VaryingRestitution vr = new VaryingRestitution();
		vr.initTest(false);
		while(true) vr.step();
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
		createEdgeFixture(ground, (float) p1.getX(), (float) p1.getY(),
				(float) p2.getX(), (float) p2.getY());
	}

	private void createEdgeFixture(Body ground, float ax, float ay, float ex,
			float ey) {
		PolygonShape shape = new PolygonShape();
		shape.setAsEdge(new Vec2(ax, ay), new Vec2(ex, ey));
		ground.createFixture(shape, 0.0f);
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

	public void initTest(boolean argDeserialized) {
		if (argDeserialized) return;
		
		checkForEnabledAssertion();
		
		world.setGravity(new Vec2(0, 0));
		
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
//			setUp(i, new Ellipse2D.Float(0, 0, 4, 4));
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
	
	Random randomGenerator = new Random();
	
	public void step() {
		body[randomGenerator.nextInt(body.length-1)].setActive(randomGenerator.nextBoolean());
		
		
		world.step(1f/60f, Params.velocityIterations, Params.positionIterations);
	}
	
	private void changeRadius(int i, float newRadius) {
		shape[i].m_radius = newRadius;
		body[i].destroyFixture(body[i].getFixtureList());
		body[i].createFixture(fd[i]);
	}

}
