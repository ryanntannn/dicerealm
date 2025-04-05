package com.dicerealm.core.util.graph;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class DistanceGraphTest {
	@Test
	public void testAddNode() {
		DistanceGraph<Node, DistanceEdge<Node>> graph = new DistanceGraph<Node, DistanceEdge<Node>>();

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
		DistanceGraph<Node, DistanceEdge<Node>> graph = new DistanceGraph<Node, DistanceEdge<Node>>();

		Node nodeA = new Node();
		graph.addN(nodeA);

		Node nodeB = new Node();
		graph.addN(nodeB);

		Node nodeC = new Node();
		graph.addN(nodeC);

		DistanceEdge<Node> edgeAB = new DistanceEdge<Node>(nodeA, nodeB, 5);
		graph.addE(edgeAB);

		DistanceEdge<Node> edgeBC = new DistanceEdge<Node>(nodeB, nodeC, 10);
		graph.addE(edgeBC);

		DistanceEdge<Node> edgeCA = new DistanceEdge<Node>(nodeC, nodeA, 15);
		graph.addE(edgeCA);

		assert(graph.getE(edgeAB.getId()) == edgeAB);
		assert(graph.getE(edgeBC.getId()) == edgeBC);
		assert(graph.getE(edgeCA.getId()) == edgeCA);
	}

	@Test
	public void testShortestPath() {
		DistanceGraph<Node, DistanceEdge<Node>> graph = new DistanceGraph<Node, DistanceEdge<Node>>();

		Node nodeA = new Node();
		graph.addN(nodeA);

		Node nodeB = new Node();
		graph.addN(nodeB);

		Node nodeC = new Node();
		graph.addN(nodeC);

		Node nodeD = new Node();
		graph.addN(nodeD);

		DistanceEdge<Node> edgeAB = new DistanceEdge<Node>(nodeA, nodeB, 5);
		graph.addE(edgeAB);

		DistanceEdge<Node> edgeBC = new DistanceEdge<Node>(nodeB, nodeC, 7);
		graph.addE(edgeBC);

		DistanceEdge<Node> edgeCA = new DistanceEdge<Node>(nodeC, nodeA, 15);
		graph.addE(edgeCA);

		Node[] path = graph.shortestPath(nodeA, nodeC);

		assert(path.length == 3);
		assert(path[0] == nodeA);
		assert(path[1] == nodeB);
		assert(path[2] == nodeC);

		// Test with no path to throw IllegalArgumentException
		assertThrows(IllegalArgumentException.class, () -> {
			graph.shortestPath(nodeA, nodeD);
		});
	}
}


