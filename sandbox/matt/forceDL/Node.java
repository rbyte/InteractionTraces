package matt.forceDL;

import processing.core.PVector;

/**
 * Basic data structure for a node in a graph.
 *
 */
public class Node {

	PVector position;
	float nodeHeight = 10;
	float nodeWidth = 10;
	int id = 0;
	String name = "";
	Graph parentGraph;
	
	public Node() {
		position = new PVector();
	}

	public Node(PVector pv) {
		position = pv;
	}
	
	public void setGraph(Graph g) { parentGraph = g; }
	public void setPosition(PVector p) { position = p; }

	public PVector position() { return position; }
	public float x() { return position.x; }
	public float y() { return position.y; }
	public float nodeHeight() { return nodeHeight; }
	public float nodeWidth() { return nodeWidth; }
	public int id() { return id; }
	public String name() { return name; }
	
	public boolean containsPoint(float x, float y) {
		float distToX = position.x - x;
		float distToY = position.y - y;
		
		return (Math.abs(distToX) < nodeWidth/2 && Math.abs(distToY) < nodeHeight/2); 
	}

	

}