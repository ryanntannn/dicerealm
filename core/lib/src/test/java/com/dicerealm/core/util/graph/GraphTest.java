package com.dicerealm.core.util.graph;

import org.junit.jupiter.api.Test;

public class GraphTest {
	@Test
	public void testAddNode() {
		Graph<Node, Edge<Node>> graph = new Graph<Node, Edge<Node>>();

		Node nodeA = new Node();
		graph.addN(nodeA);

		Node nodeB = new Node();
		graph.addN(nodeB);

		Node nodeC = new Node();
		graph.addN(nodeC);

		assert(graph.getN(nodeA.getId()) == nodeA);
		assert(graph.getN(nodeB.getId()) == nodeB);
		assert(graph.getN(nodeC.getId()) == nodeC);
	}

	@Test
	public void testAddEdge() {
		Graph<Node, Edge<Node>> graph = new Graph<Node, Edge<Node>>();

		Node nodeA = new Node();
		graph.addN(nodeA);

		Node nodeB = new Node();
		graph.addN(nodeB);

		Node nodeC = new Node();
		graph.addN(nodeC);

		Edge<Node> edgeAB = new Edge<Node>(nodeA, nodeB);
		graph.addE(edgeAB);

		Edge<Node> edgeBC = new Edge<Node>(nodeB, nodeC);
		graph.addE(edgeBC);

		Edge<Node> edgeCA = new Edge<Node>(nodeC, nodeA);
		graph.addE(edgeCA);

		assert(graph.getE(edgeAB.getId()) == edgeAB);
		assert(graph.getE(edgeBC.getId()) == edgeBC);
		assert(graph.getE(edgeCA.getId()) == edgeCA);
	}

	@Test
	public void testShortestPath(){
		Graph<Node, Edge<Node>> graph = new Graph<Node, Edge<Node>>();

		Node nodeA = new Node();
		graph.addN(nodeA);

		Node nodeB = new Node();
		graph.addN(nodeB);

		Node nodeC = new Node();
		graph.addN(nodeC);

		Node nodeD = new Node();
		graph.addN(nodeD);

		Node nodeE = new Node();
		graph.addN(nodeE);


		graph.addE(new Edge<Node>(nodeA, nodeB));
		graph.addE(new Edge<Node>(nodeA, nodeC));
		graph.addE(new Edge<Node>(nodeB, nodeD));
		graph.addE(new Edge<Node>(nodeC, nodeD));
		graph.addE(new Edge<Node>(nodeD, nodeE));

		Node[] path = graph.shortestPath(nodeA, nodeE);

		assert(path.length == 4);
	}
}
