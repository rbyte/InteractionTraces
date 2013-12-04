package matt.forceDL;

import java.util.ArrayList;

import processing.core.PVector;


/**
 * Basic graph data structure with Nodes and Edges
 * @author jagoda
 *
 */
class Graph {

	ArrayList<Node> nodes;
	ArrayList<Edge> edges;

	public Graph() {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
	}

	public ArrayList<Node> getNodes() { return nodes; }
	public ArrayList<Edge> getEdges() { return edges; }

	public void addNode(float x, float y) {
		addNode(new ParticleNode(new PVector(x, y)));
	}

	public void addNode(Node n) {
		nodes.add(n);
		n.setGraph(this); 
	}
	
	

	public void addEdge(Edge e) {
		edges.add(e); 

		e.setGraph(this);
	}

	public boolean isConnected(Node a, Node b) {
		for(Edge e : edges) {
			if (e.getStartNode() == a && e.getEndNode() == b   ||   
					e.getStartNode() == b && e.getEndNode() == a) {
				return true;
			}
		}
		return false; 
	}

	public Node getNodeWithID(int id) {
		
		Node n = null;
		
		for (Node node : nodes) {
			if (node.id() == id) {
				return node;
			}
		}
		
		return n;
	}





}