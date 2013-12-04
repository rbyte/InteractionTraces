package matt.forceDL;

import processing.core.*;


/**
 * CPSC 583, Fall 2011, Tutorial 08
 * Manages the layout, drawing, and interaction of a graph
 * using a force-directed layout based on Coulomb's and Hooke's laws.
 *  
 * You can change the values for Coulomb's constant, the spring constant, and the equilibrium 
 * length in the global variables.
 *  
 * The code for the force-directed layout is modified from Sean McCullough's implementation
 * found at http://www.cricketschirping.com/processing/GraphLayout/
 */
public class GraphLayoutManager {
	Graph graph;

	Node selectedNode;
	Node draggedNode;
	Node hoverNode;
	
	float coulConst = 40; //original: 100
	float springConstant = 0.02f; //original: 0.1f
	float equilibriumLength = 2; 
	
	int canvasWidth;
	int canvasHeight;
	
	int iterations;

	PFont labelFont;

	public GraphLayoutManager(Graph g, int w, int h) {
		graph = g;

		selectedNode = null;
		draggedNode = null;
		hoverNode = null;
		
		canvasWidth=w;
		canvasHeight=h;
		
		iterations = 0;
	}
	

	/* #####################################################################
	 * CHANGE SETTINGS
	 * Coulomb's constant
	 * Spring constant
	 * Equilibrium constant
	 */
	
	public void setCoulombConstant(float value) { coulConst = value; }
	public void resetCoulombConstant() { coulConst = 100; }
	
	public void setSpringConstant(float value) { springConstant = value; }
	public void resetSpringConstant() { springConstant = 0.1f; }
	
	public void setEquilibriumLength(float value) { equilibriumLength = value; }
	public void resetEquilibriumLength() { equilibriumLength = 100; }
	
	
	/* #####################################################################
	 * FORCE-DIRECTED LAYOUT
	 * using Hooke's Law (attraction) and Coulomb's Law (repulsion)
	 * to achieve equilibrium
	 * 
	 * will achieve crossing-free layout for most planar graphs
	 */
	public void createForceDirectedLayout() {
		//note: nodes start out in random position 
		//(this is calculated in the initial layout)

		if (iterations < 10000) {
		
		//reset the force for each node to 0
		resetForcesForNodes();

		//pull the nodes together
		setAttractionForceForEdges();

		//move nodes apart
		setRepulsionForceForNodes();

		//move nodes according to forces calculated
		moveNodesUsingForces();
		iterations++;
		}
		
	}
	
	

	private void resetForcesForNodes() {
		for(Node node : graph.getNodes() ) {
			ParticleNode pnode = (ParticleNode)node;
			pnode.setForce(new PVector(0,0));
		}
	}

	private void setAttractionForceForEdges() {
		//calculate the spring force for each edge (Hooke's law)
		//this is the force that brings the nodes together
		//this happens inside the Spring class
		for(Edge edge : graph.getEdges()) {
			Spring spring = (Spring) edge;
			PVector forceForStartNode = spring.getForceFromStart(springConstant, equilibriumLength);
			PVector forceForEndNode = spring.getForceFromEnd(springConstant, equilibriumLength);

			ParticleNode startNode = (ParticleNode)spring.getStartNode();
			ParticleNode endNode = (ParticleNode)spring.getEndNode();

			startNode.applyForce(forceForStartNode);
			endNode.applyForce(forceForEndNode);
		}
	}

	private void setRepulsionForceForNodes() {
		for (Node nA : graph.getNodes()) {
			ParticleNode nodeA = (ParticleNode)nA;

			for (Node nB : graph.getNodes()) {
				ParticleNode nodeB = (ParticleNode)nB;

				if (nodeB != nodeA) {
					nodeA.applyForce(calcForceCoulombsLaw(nodeA, nodeB));

				} 
			}
		}
	}

	private void moveNodesUsingForces() {
		for (Node node : graph.getNodes() ) {
			ParticleNode pnode = (ParticleNode) node;
			if (pnode != draggedNode) { //dragged node's position depends on mouse position, not force
				PVector nodePos = pnode.position();
				PVector force = pnode.getForce();
				nodePos.add(force);
				//nodePos = adjustForOutOfBounds(nodePos);
				pnode.setPosition(nodePos);
			}
		}
	}
	

	/**
	 * A crude way of getting nodes to stay within bounds of the screen
	 * @param pos
	 * @return
	 */
	private PVector adjustForOutOfBounds(PVector pos) {
		
		if (pos.x < 0) { pos.x=0; }
		if (pos.y < 0) { pos.y = 0; }
		if (pos.x > canvasWidth) { pos.x = canvasWidth; }
		if (pos.y > canvasHeight) { pos.y = canvasHeight; }
		
		return pos;
	}

	private PVector calcForceCoulombsLaw(ParticleNode nodeA, ParticleNode nodeB) {

		//coulomb's law: f = k * q1 * q2 / r^2
		//our application: f = 100 * mass1 * mass2 / distance squared

		PVector force = new PVector(0,0); //return 0 force if length = 0

		float lengthX = nodeB.x() - nodeA.x(); 
		float lengthY = nodeB.y() - nodeA.y();
		float length = PApplet.sqrt(lengthX*lengthX + lengthY*lengthY);

		if (length!=0) { //div by zero errors
			float length_sq = length*length;
			float mass1mass2 = nodeA.getMass() * nodeB.getMass();
			float f = coulConst*(mass1mass2 / length_sq);

			force = new PVector(-lengthX*f, -lengthY*f);
		}

		return force;
	}

	public Node getHoverNode() { return hoverNode; }

	/**
	 * If no node is currently being dragged, determines 
	 * which node is being hovered over. (Returns null if none)
	 * @param mouseX
	 * @param mouseY
	 */
	public void determineHoverNode(float mouseX, float mouseY) {
		if (draggedNode == null) {
			for (Node n : graph.getNodes()) {
				if (n.containsPoint(mouseX, mouseY)) {
					hoverNode = n;
					return;
				}
			}
		}
		hoverNode = null;
	}

	/**
	 * Figures out which node is currently being dragged.
	 * @param mouseX
	 * @param mouseY
	 */
	public void findMovingNode(float mouseX, float mouseY) {
		for (Node n : graph.getNodes()) {
			if (n.containsPoint(mouseX, mouseY)) {
				draggedNode = n;
				iterations=0;
				return;
			}
		}
		draggedNode = null;
	}

	/**
	 * Updates position of node currently being dragged.
	 * @param mouseX
	 * @param mouseY
	 */
	public void updateMovingNodePosition(float mouseX, float mouseY) {
		if (draggedNode != null) {
			draggedNode.setPosition(new PVector(mouseX, mouseY));
		}
	}

	public void resetMovingNode() { draggedNode = null; }





}
