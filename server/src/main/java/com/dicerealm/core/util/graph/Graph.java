package com.dicerealm.core.util.graph;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.util.queue.Queue;

public class Graph<N extends Node, E extends Edge<N>> {
	private Map<UUID, N> nodes = new HashMap<UUID, N>();
	private Map<UUID, E> edges = new HashMap<UUID, E>();

	public void addN(N node) {
		nodes.put(node.getId(), node);
	}

	public N getN(UUID id) {
		return nodes.get(id);
	}

	public void addE(E edge) {
		edges.put(edge.getId(), edge);
	}

	public E getE(UUID id) {
		return edges.get(id);
	}

	@SuppressWarnings("unchecked")
	public N[] getNeighbors(N node) {
		ArrayList<N> neighbors = new ArrayList<N>();
		for (E edge : edges.values()) {
			if (edge.getSource().equals(node)) {
				neighbors.add(edge.getTarget());
			}
		}
		return neighbors.toArray((N[]) Array.newInstance(node.getClass(), neighbors.size()));
	}

	@SuppressWarnings("unchecked")
	public N[] shortestPath(N start, N end) {
		Queue<ArrayList<N>> queue = new Queue<ArrayList<N>>();
		ArrayList<N> visited = new ArrayList<N>();
		ArrayList<N> path = new ArrayList<N>();

		path.add(start);
		queue.enqueue(path);

		while (!queue.isEmpty()) {
			path = queue.dequeue();
			N node = path.get(path.size() - 1);

			if (node.equals(end)) {
				return path.toArray((N[]) Array.newInstance(node.getClass(), path.size()));
			}

			if (!visited.contains(node)) {
				visited.add(node);
				for (N neighbor : getNeighbors(node)) {
					ArrayList<N> newPath = new ArrayList<N>(path);
					newPath.add(neighbor);
					queue.enqueue(newPath);
				}
			}
		}

		// No path found
		return null;
	}
}
