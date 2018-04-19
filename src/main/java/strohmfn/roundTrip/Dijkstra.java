package strohmfn.roundTrip;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dijkstra {

	public static String calc(int start, int dest, Graph graph) {
		int[][] edges = graph.getEdges();
		int[] offsets = graph.getOffsets();
		double[][] nodes = graph.getNodes();

		// Check if start equals destination -> shortest path is trivial.
		if (start == dest) {
			return "Start == Destination";
		} else {
			/*
			 * Create priority queue with a custom comparator. Entry with the lowest
			 * array[1] value (cost of the path) is prioritized.
			 */
			Queue<int[]> frontier = new PriorityQueue<int[]>((a, b) -> a[1] - b[1]);
			int[] dist = new int[edges[0].length];
			int[] prev = new int[edges[0].length];
			int startNode[] = { start, 0 };
			frontier.add(startNode);
			while (!frontier.isEmpty()) {
				int[] currentNode = frontier.poll();
				if (currentNode[0] == dest) {
					break;
				}
				int offset = offsets[currentNode[0]];
				if (offsets[currentNode[0]] != -1) {
					while (edges[0][offset] == currentNode[0]) {
						int newDist = dist[edges[0][offset]] + edges[2][offset];
						if (dist[edges[1][offset]] == 0 || newDist < dist[edges[1][offset]]) {
							dist[edges[1][offset]] = newDist;
							prev[edges[1][offset]] = edges[0][offset];
							int newNode[] = { edges[1][offset], newDist };
							frontier.add(newNode);
						}
						offset++;
						if (offset >= edges[0].length) {
							break;
						}
					}
				}
			}
			if (prev[dest] == 0) {
				return "No path found from Node '" + start + "' to Node '" + dest + "'!";
			}
			return generateSolutionString(start, dest, prev, nodes);
		}
	}

	private static String generateSolutionString(int start, int dest, int[] prev, double[][] nodes) {
		ArrayList<Integer> solutionPath = new ArrayList<Integer>();
		int prevNode = prev[dest];
		solutionPath.add(prevNode);
		while (prevNode != start) {
			prevNode = prev[prevNode];
			solutionPath.add(prevNode);
		}
		String solution = nodes[0][dest] + "_" + nodes[1][dest];
		for (int i = 0; i < solutionPath.size(); i += 1) {
			solution = nodes[0][solutionPath.get(i)] + "_" + nodes[1][solutionPath.get(i)] + "," + solution;
		}
		solution += dest;
		return solution;
	}
}
