package strohmfn.roundTrip;

import java.util.ArrayList;
import java.util.Arrays;

public class TravelingSalesPerson {

	private ArrayList<Integer> roundtripSolution = new ArrayList<Integer>();
	private int subPathCost[][], pickedNode[][], npow, numberNodes, edgeCost[][];
	public static long time;

	public ArrayList<Integer> computeRoundtrip(int[][] inputArray) {
		long start = System.currentTimeMillis();

		numberNodes = inputArray.length;
		npow = (int) Math.pow(2, numberNodes);
		subPathCost = new int[numberNodes][npow];
		pickedNode = new int[numberNodes][npow];
		edgeCost = inputArray;

		// Initialize with invalid value -1. Used to check when to break out of
		// recursion.
		for (int i = 0; i < numberNodes; i++) {
			Arrays.fill(subPathCost[i], -1);
			Arrays.fill(pickedNode[i], -1);
		}

		// Initialize first row of the table with costs from each edge i to the start
		// node.
		for (int i = 0; i < numberNodes; i++) {
			subPathCost[i][0] = inputArray[i][0];
		}

		tspRecursive(0, npow - 2);
		// Reconstruct path.
		roundtripSolution.add(0);
		getPathRecursive(0, npow - 2);
		roundtripSolution.add(0);

		long end = System.currentTimeMillis();
		time = (end - start) / 1000;
		System.out.println("Time needed for TSP computation: " + time + "s");
		return roundtripSolution;
	}

	/**
	 * 
	 * @param start
	 *            Number of the start node
	 * @param currentSet
	 *            Represents the nodes to be visited - binary encoded (example:
	 *            101101 - Visit node 1,3,4 and 6)
	 * @return Cost of path staring at node 'start' visiting each node in
	 *         'currentSet'
	 */
	private int tspRecursive(int start, int currentSet) {
		int cost = -1;
		int tempCost;
		int newSet;
		int nodeMask;

		// Check if this sub-path has already been calculated and return its cost if so.
		if (subPathCost[start][currentSet] != -1) {
			return subPathCost[start][currentSet];
		}
		// Calculate cost of sub-path.
		else {
			for (int node = 0; node < numberNodes; node++) {
				nodeMask = npow - 1 - (int) Math.pow(2, node);
				newSet = currentSet & nodeMask;
				if (newSet != currentSet) {
					tempCost = edgeCost[start][node] + tspRecursive(node, newSet);
					if (cost == -1 || cost > tempCost) {
						cost = tempCost;
						pickedNode[start][currentSet] = node;
					}
				}
			}
			subPathCost[start][currentSet] = cost;
			return cost;
		}
	}

	/**
	 * 
	 * @param start
	 *            Number of the start node
	 * @param currentSet
	 *            Represents the sub-set of visited nodes - binary encoded (example:
	 *            101101 - Visited nodes 1,3,4 and 6)
	 */
	private void getPathRecursive(int start, int currentSet) {
		// If 'pickedNode' is -1, this sub-path is irrelevant - stop recursion branch.
		if (pickedNode[start][currentSet] == -1) {
			return;
		}

		int node = pickedNode[start][currentSet];
		int nodeMask = npow - 1 - (int) Math.pow(2, node);
		int newSet = currentSet & nodeMask;

		roundtripSolution.add(node);
		getPathRecursive(node, newSet);
	}
}
