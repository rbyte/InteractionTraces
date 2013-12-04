package matt.forceDL;

import matt.ui.PAppletPlus;

public class Main extends PAppletPlus {
	
	private static final long serialVersionUID = 538755759368373964L;
	
	Graph graph = new Graph();
	GraphLayoutManager glm = new GraphLayoutManager(graph, 50, 50);
	
	public void drawLoadingScreen() {
		for (Node n : glm.graph.nodes) {
			circle(n.x(), n.y(), 1);
//			System.out.println(n.x() + ", " + n.y());
		}
	}

	public void setupPlus() {
		
		graph.addNode(-1,-1);
		graph.addNode(2,2);

		glm.createForceDirectedLayout();
		
		for (Node n : glm.graph.nodes) {
			System.out.println(n.x() + ", " + n.y());
		}
		
		System.out.println("finished");

	}

}
