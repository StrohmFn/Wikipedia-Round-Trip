package strohmfn.roundTrip;

import java.util.ArrayList;
import java.util.Arrays;

public class TravelingSalesPerson {


	private ArrayList<Integer> roundtrip = new ArrayList<Integer>();
	private int subPathCost[][], pickedNode[][], npow, numberNodes, edgeCost[][];
	public static long time;

	public ArrayList<Integer> computeRoundtrip(int[][] inputArray) {
		long start = System.currentTimeMillis();

		numberNodes = inputArray.length;
		npow = (int) Math.pow(2, numberNodes);
		subPathCost = new int[numberNodes][npow];
		pickedNode = new int[numberNodes][npow];
		edgeCost = inputArray;

		// Initialize with invalid value -1. Used to check when to break out of recursion.
		for (int i = 0; i < numberNodes; i++) {
			Arrays.fill(subPathCost[i], -1);
			Arrays.fill(pickedNode[i], -1);
		}

		// Initialize first row of the table with costs from each edge i to the start node.
		for (int i = 0; i < numberNodes; i++) {
			subPathCost[i][0] = inputArray[i][0];
		}

		tspRecursive(0, npow - 2);
		// Reconstruct path.
		roundtrip.add(0);
		getPathRecursive(0, npow - 2);
		roundtrip.add(0);

		long end = System.currentTimeMillis();
		time = (end - start) / 1000;
		System.out.println("Time needed for TSP computation: " + time + "s");
		return roundtrip;
	}

	private int tspRecursive(int start, int currentSet) {
		int cost = -1;
		int tempCost;
		int newSet;
		int nodeMask;
		
		if(subPathCost[start][currentSet] != -1) {
			return subPathCost[start][currentSet];
		}
		else {
			for(int node = 0; node < numberNodes; node++) {
				nodeMask =  npow - 1 - (int) Math.pow(2, node);
				newSet = currentSet & nodeMask;
				if(newSet != currentSet) {
					tempCost = edgeCost[start][node] + tspRecursive(node,newSet);
					if(cost == -1 || cost > tempCost) {
						cost = tempCost;
						pickedNode[start][currentSet] = node;
					}
				}
			}
			subPathCost[start][currentSet] = cost;
			return cost;
		}
	}
	
	private void getPathRecursive(int start, int set) {
		if(pickedNode[start][set] == -1) {
			return;
		}
		
		int node = pickedNode[start][set];
		int nodeMask = npow - 1 - (int) Math.pow(2, node);
		int newSet = set & nodeMask;
		
		roundtrip.add(node);
		getPathRecursive(node,newSet);
	}
}
