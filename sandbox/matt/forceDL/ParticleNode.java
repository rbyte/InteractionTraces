package matt.forceDL;
import processing.core.PVector;


/**
 * Basic data structure for a node in a graph, which behaves like a particle.
 *
 * Modified from Sean McCullough's implementation, found at
 * http://www.cricketschirping.com/processing/GraphLayout/
 */

public class ParticleNode extends Node {

	final int NODEHEIGHT = 20;
	final int NODEWIDTH = 20;
	
	PVector force = new PVector(0, 0);
	float mass = 1;

	public ParticleNode(PVector v) {
		super(v);
		nodeHeight = NODEHEIGHT;
		nodeWidth = NODEWIDTH;
		force = new PVector(0,0);
	} 
	
	public ParticleNode(PVector v, int idNum, String l) {
		super(v);
		nodeHeight = NODEHEIGHT;
		nodeWidth = NODEWIDTH;
		force = new PVector(0,0);
		
		id = idNum;
		name = l;
	}

	public float getMass() { return mass; }
	public void setMass(float m) {
		mass = m;
		nodeHeight = m*NODEHEIGHT;
		nodeWidth = m*NODEWIDTH;
	} 

	public PVector getForce() { return force; }
	public void setForce(PVector f) { force = f; }

	public void applyForce(PVector v) {
		force.add(v);  
	}
}