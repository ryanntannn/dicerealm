package com.dicerealm.core.util.graph;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DistanceGraph <N extends Node, E extends DistanceEdge<N>> extends Graph<N , E> {
	public DistanceGraph() {
		super();
	}
	
	@Override
	public N[] shortestPath(N source, N target) {
		// We know that there are no negative weights in this graph, so we can use Dijkstra's algorithm

		// Distance from source to each node
		Map<N, Double> d = new HashMap<>();

		// Predecessor of each node in the shortest path
		Map<N, N> pi = new HashMap<>();

		// Visited nodes
		Map<N, Boolean> visited = new HashMap<>();

		// Initialize distances
		for (N node : nodes.values()) {
			d.put(node, Double.POSITIVE_INFINITY);
			pi.put(node, null);
			visited.put(node, false);
		}

		// Distance to the source is 0
		d.put(source, 0.0);

		for (int i = 0; i < nodes.size(); i++) {
			N u = null;
			double min = Double.POSITIVE_INFINITY;

			// Find the unvisited node with the smallest distance
			for (N node : nodes.values()) {
				if (!visited.get(node) && d.get(node) < min) {
					min = d.get(node);
					u = node;
				}
			}

			if (u == null) {
				break;
			}

			// Mark the node as visited
			visited.put(u, true);

			// Update distances to neighbors
			for (E edge : edges.values()) {
				// Bidirectional edges so we check both directions
				if (edge.getSource().equals(u)) {
					N v = edge.getTarget();
					double alt = d.get(u) + edge.getDistance();

					if (alt < d.get(v)) {
						d.put(v, alt);
						pi.put(v, u);
					}
				}
				if (edge.getTarget().equals(u)) {
					N v = edge.getSource();
					double alt = d.get(u) + edge.getDistance();

					if (alt < d.get(v)) {
						d.put(v, alt);
						pi.put(v, u);
					}
				}
			}
		}

		// Check if the target is reachable
		if (d.get(target) == Double.POSITIVE_INFINITY) {
			throw new IllegalArgumentException("Target node is not reachable from source node");
		}

		// Reconstruct the shortest path
		N current = target;
		ArrayList<N> path = new ArrayList<>();
		while (current != null) {
			path.add(0, current);
			current = pi.get(current);
		}

		// Convert the ArrayList to an array
		@SuppressWarnings("unchecked")
		N[] pathArray = path.toArray((N[]) Array.newInstance(source.getClass(), path.size()));

		return pathArray;
	}
}
