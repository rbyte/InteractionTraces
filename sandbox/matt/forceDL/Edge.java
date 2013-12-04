package matt.forceDL;
import processing.core.PApplet;

/**
 * Data structure for a directed link in a graph.
 * @author jagoda
 *
 */
public class Edge {
	PApplet parent;

	Node nodeEnd;
	Node nodeStart;
	Graph parentGraph;
	
	int strokeColor = 255;

	public Edge(PApplet p, Node end, Node start) {
		parent = p;

		nodeEnd = end;
		nodeStart = start;
	} 

	public void setGraph(Graph g) { parentGraph = g; }

	public Node getStartNode() { return nodeStart; }
	public void setStartNode(Node n) { nodeStart = n; }
	public Node getEndNode() { return nodeEnd; }
	public void setEndNode(Node n) { nodeEnd = n; }
	
	public void setStrokeColor(int c) { strokeColor = c; }

	public float lengthX() { return nodeEnd.x() - nodeStart.x(); }
	public float lengthY() { return nodeEnd.y() - nodeStart.y(); }
	public float length() { return PApplet.sqrt(lengthX()*lengthX() + lengthY()*lengthY()); }


}