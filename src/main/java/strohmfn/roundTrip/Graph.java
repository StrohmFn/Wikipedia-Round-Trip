package strohmfn.roundTrip;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Graph {
	private int[][] edges;
	private double[][] nodes;
	private int[] offsets;

	private HashMap<String, LinkedList<Integer>> gridCells = new HashMap<String, LinkedList<Integer>>();

	public Graph() {
		loadMapData();
		createGrid();
	}

	public String calcTour(double[][] visitNodes) {
		int numberNodes = visitNodes.length;

		int[][] costs = new int[numberNodes][numberNodes];
		for (int i = 0; i < numberNodes; i++) {
			for (int j = 0; j < numberNodes; j++) {
				if (i == j) {
					costs[i][j] = 0;
				} else {
					costs[i][j] = (int) euclideanDist(visitNodes[i][0],visitNodes[i][1],visitNodes[j][0],visitNodes[j][1]);
				}
			}
		}
		
		ArrayList<Integer> result = new TravelingSalesPerson().computeRoundtrip(costs);
		
		int[] nodes = new int[numberNodes];
		for (int i = 0; i < numberNodes; i++) {
			nodes[i] = getClosestNode(visitNodes[i]);
		}
		
		String solution = "";
		for (int i = 0; i < result.size()-1; i++) {
			int from = nodes[result.get(i)];
			int to = nodes[result.get(i+1)];
			solution += dijkstra(from, to) + ",";
		}
		solution = solution.substring(0, solution.length() - 1);
		System.out.println("Shortest Roundtrip Calculated!");
		return solution;
	}

	private int getClosestNode(double[] latLng) {
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

	private String dijkstra(int start, int dest) {
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
			return generateSolutionString(start, prev, dest);
		}
	}

	private String generateSolutionString(int start, int[] prev, int dest) {
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
	private double euclideanDist(double node1_lat, double node1_lng, double node2_lat, double node2_lng) {
		double degLen = 110.25;
		double x = node1_lat - node2_lat;
		double y = (node1_lng - node2_lng) * Math.cos(node2_lat);
		double result = Math.sqrt(x * x + y * y) * degLen;
		return result;
	}

	private void createGrid() {
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

	private void loadMapData() {
		System.out.println("Loading map data...");
		try {
			String nodespath = "resources/de_nodes.fs";
			String edgesPath = "resources/de_edges.fs";
			BufferedReader bf = new BufferedReader(new FileReader(nodespath));
			int nodesCount;
			int edgesCount;
			// Read number of nodes and create an array with the same size to store them.
			String line = bf.readLine();
			nodesCount = Integer.parseInt(line);
			nodes = new double[2][nodesCount];
			// Create offset array and init with -1 (no outgoing edge)
			offsets = new int[nodesCount];
			Arrays.fill(offsets, -1);
			line = bf.readLine();
			int counterNodes = 0;
			// Read all nodes from file. Store geographic coordinates in 'nodes' array
			while (counterNodes < nodesCount) {
				String vertexData[] = line.split(" ");
				nodes[0][counterNodes] = Double.parseDouble(vertexData[1]);
				nodes[1][counterNodes] = Double.parseDouble(vertexData[2]);
				counterNodes++;
				line = bf.readLine();
			}
			bf.close();
			bf = new BufferedReader(new FileReader(edgesPath));
			// Read number of edges and create an array with the same size to store them.
			line = bf.readLine();
			edgesCount = Integer.parseInt(line);
			edges = new int[3][edgesCount];
			int counterEdges = 0;
			// Read all edges from file.
			counterNodes = -1;
			line = bf.readLine();
			while (counterEdges < edgesCount) {
				String edgeData[] = line.split(" ");
				// Store start-node, dest-node and weighting in 'edges' array.
				edges[0][counterEdges] = Integer.parseInt(edgeData[0]);
				edges[1][counterEdges] = Integer.parseInt(edgeData[1]);
				edges[2][counterEdges] = Integer.parseInt(edgeData[2]);
				/*
				 * Checks if the current edge starts from a new vertex and if so, stores the
				 * offset of that new vertex in the 'offsets' array. 'counternodes' stores the
				 * ID of the current start node. So if the next edge starts from a new node,
				 * 'counternodes < edges[counterEdges][0]' is true (since edges are sorted by
				 * their starting node).
				 */
				if (counterNodes < edges[0][counterEdges]) {
					counterNodes = edges[0][counterEdges];
					offsets[counterNodes] = counterEdges;
				}
				counterEdges++;
				line = bf.readLine();
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Graph file not found, exiting!");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Can not read graph file, exiting!");
			System.exit(2);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println(
					"Number of edges or nodes is not consistent with the specification at the beginning of the graph file, exiting!");
			System.exit(3);

		}
	}
	
	private void loadMapData_old() {
		System.out.println("Loading map data...");
		try {
			String graphPath = "de.fs";
			BufferedReader bf = new BufferedReader(new FileReader(graphPath));
			int nodesCount;
			int edgesCount;
			// Read number of nodes and create an array with the same size to store them.
			String line = bf.readLine();
			nodesCount = Integer.parseInt(line);
			nodes = new double[2][nodesCount];
			// Read number of edges and create an array with the same size to store them.
			line = bf.readLine();
			edgesCount = Integer.parseInt(line);
			edges = new int[2][edgesCount];
			// Create offset array and init with -1 (no outgoing edge)
			offsets = new int[nodesCount];
			Arrays.fill(offsets, -1);
			line = bf.readLine();
			int counterNodes = 0;
			int counterEdges = 0;
			// Read all nodes from file. Store geographic coordinates in 'nodes' array
			while (counterNodes < nodesCount) {
				String vertexData[] = line.split(" ");
				nodes[0][counterNodes] = Double.parseDouble(vertexData[1]);
				nodes[1][counterNodes] = Double.parseDouble(vertexData[2]);
				counterNodes++;
				line = bf.readLine();
			}
			// Read all edges from file.
			counterNodes = -1;
			while (counterEdges < edgesCount) {
				String edgeData[] = line.split(" ");
				// Store start-node, dest-node and weighting in 'edges' array.
				edges[0][counterEdges] = Integer.parseInt(edgeData[0]);
				edges[1][counterEdges] = Integer.parseInt(edgeData[1]);
				//edges[2][counterEdges] = Integer.parseInt(edgeData[2]);
				/*
				 * Checks if the current edge starts from a new vertex and if so, stores the
				 * offset of that new vertex in the 'offsets' array. 'counternodes' stores the
				 * ID of the current start node. So if the next edge starts from a new node,
				 * 'counternodes < edges[counterEdges][0]' is true (since edges are sorted by
				 * their starting node).
				 */
				if (counterNodes < edges[0][counterEdges]) {
					counterNodes = edges[0][counterEdges];
					offsets[counterNodes] = counterEdges;
				}
				counterEdges++;
				line = bf.readLine();
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Graph file not found, exiting!");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Can not read graph file, exiting!");
			System.exit(2);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println(
					"Number of edges or nodes is not consistent with the specification at the beginning of the graph file, exiting!");
			System.exit(3);

		}
	}

	public int[][] getEdges() {
		return edges;
	}

	public double[][] getNodes() {
		return nodes;
	}

	public int[] getOffsets() {
		return offsets;
	}
}
