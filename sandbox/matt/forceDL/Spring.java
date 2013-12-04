package matt.forceDL;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Data structure for a directed edge in a graph, which behaves like a spring.
 * @author jagoda
 *
 * Modified from Sean McCullough's implementation
 * http://www.cricketschirping.com/processing/GraphLayout/
 */
public class Spring extends Edge {

	float springConstant=0.1f; 
	float naturalLength=100; 


	public Spring(PApplet p, Node a, Node b) {
		super(p, a, b);
	}

	
	public void setSpringConstant(float s) { springConstant = s; }
	public float getSpringConstant() { return springConstant; }
	public void setNaturalLength(float l) { naturalLength = l; }
	public float getNaturalLength() { return naturalLength; }
	

	private PVector normalizedForceVector(float force) {
		// [distanceX()/distance, distanceY()/distance] is the (normalized) unit vector in the direction of the edge
		
		// return a vector of size force in the direction of the unit vector for the edge
		return new PVector(force*lengthX()/length(), force*lengthY()/length());
	}

	private float calcForceHookesLaw(float springConstant, float equilibriumLength) {
		return springConstant * (length() - equilibriumLength);
	}


	public PVector getForceFromStart(float springConstant, float equilibriumLength) {
		return normalizedForceVector(calcForceHookesLaw(springConstant, equilibriumLength)); 
	}
	
	public PVector getForceFromEnd(float springConstant, float equilibriumLength) {  
		return normalizedForceVector(-1 * calcForceHookesLaw(springConstant, equilibriumLength)); 
	}


}