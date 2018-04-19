package strohmfn.roundTrip;

import java.util.HashMap;
import java.util.LinkedList;

public class Grid {
	private double[][] nodes;

	// Singleton
	private static Grid instance;

	public Grid(double[][] nodes) {
		this.nodes = nodes;
	}

	public static Grid getInstance(double[][] nodes) {
		if (Grid.instance == null) {
			Grid.instance = new Grid(nodes);
			Grid.instance.createGrid();
		}
		return Grid.instance;
	}

	private HashMap<String, LinkedList<Integer>> gridCells;

	public int getClosestNode(double[] latLng) {
		String gridKey = (double) Math.round(latLng[0] * 10) / 10 + "-" + (double) Math.round(latLng[1] * 10) / 10;
		LinkedList<Integer> gridCell;
		if (gridCells.containsKey(gridKey)) {
			gridCell = gridCells.get(gridKey);
		} else {
			return -1;
		}
		int nearestNodeIndex = -1;
		double shortestDist = Double.MAX_VALUE;
		for (int nodeID : gridCell) {
			double dist = euclideanDist(nodes[0][nodeID], nodes[1][nodeID], latLng[0], latLng[1]);
			if (dist < shortestDist) {
				shortestDist = dist;
				nearestNodeIndex = nodeID;
			}
		}
		return nearestNodeIndex;
	}

	private void createGrid() {
		gridCells = new HashMap<String, LinkedList<Integer>>();
		System.out.println("Creating grid...");
		for (int i = 0; i < nodes[0].length; i++) {
			String gridKey = (double) Math.round(nodes[0][i] * 10) / 10 + "-"
					+ (double) Math.round(nodes[1][i] * 10) / 10;
			if (gridCells.containsKey(gridKey)) {
				gridCells.get(gridKey).add(i);
			} else {
				LinkedList<Integer> nodesInCell = new LinkedList<Integer>();
				nodesInCell.add(i);
				gridCells.put(gridKey, nodesInCell);
			}
		}
	}

	/**
	 * 
	 * @param lat
	 *            Latitude of the two points.
	 * @param lng
	 *            Longitude of the two points.
	 * @return Approximation of the distance between two points.
	 * 
	 *         This method approximates the distance between two points using the
	 *         euclidean distance. Since the points are on a sphere we have to
	 *         adjust the values. The error is negligible for small distances (which
	 *         we deal with).
	 */
	public static double euclideanDist(double node1_lat, double node1_lng, double node2_lat, double node2_lng) {
		double degLen = 110.25;
		double x = node1_lat - node2_lat;
		double y = (node1_lng - node2_lng) * Math.cos(node2_lat);
		double result = Math.sqrt(x * x + y * y) * degLen;
		return result;
	}
}
