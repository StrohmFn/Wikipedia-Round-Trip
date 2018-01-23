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

	public int getClosestNode(double[] latLng) {
		String gridKey = (double) Math.round(latLng[0] * 10) / 10 + "-" + (double) Math.round(latLng[1] * 10) / 10;
		LinkedList<Integer> gridCell;
		if(gridCells.containsKey(gridKey)) {
			gridCell = gridCells.get(gridKey);
		}else {
			return -1;
		}
		int nearestNodeIndex = -1;
		double shortestDist = Double.MAX_VALUE;
		for (int nodeID : gridCell) {
			double dist = euclideanDist(nodes[nodeID], latLng);
			if(dist < shortestDist) {
				shortestDist = dist;
				nearestNodeIndex = nodeID;
			}
		}
		return nearestNodeIndex;
	}
	
	public String dijkstra(int start, int dest) {
		// Check if start equals destination -> shortest path is trivial.
		if (start == dest) {
			return "Start == Destination";
		} else {
			/*
			 * Create priority queue with a custom comparator. Entry with the lowest
			 * array[1] value (cost of the path) is prioritized.
			 */
			Queue<int[]> frontier = new PriorityQueue<int[]>((a, b) -> a[1] - b[1]);
			HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> prev = new HashMap<Integer, Integer>();
			dist.put(start, 0);
			int startNode[] = { start, 0 };
			frontier.add(startNode);
			while (!frontier.isEmpty()) {
				int[] currentNode = frontier.poll();
				if (currentNode[0] == dest) {
					break;
				}
				int offset = offsets[currentNode[0]];
				if (offsets[currentNode[0]] != -1) {
					int[] edge = edges[offset];
					while (edge[0] == currentNode[0]) {
						int newDist = dist.get(edge[0]) + edge[2];
						if (!dist.containsKey(edge[1]) || newDist < dist.get(edge[1])) {
							dist.put(edge[1], newDist);
							prev.put(edge[1], edge[0]);
							int newNode[] = { edge[1], newDist };
							frontier.add(newNode);
						}
						offset++;
						if (offset >= edges.length) {
							break;
						}
						edge = edges[offset];
					}
				}
			}
			if (!prev.containsKey(dest)) {
				return "No path found from Node '" + start + "' to Node '" + dest + "'!";
			}
			return generateSolutionString(start, prev, dest);
		}
	}
	
	private String generateSolutionString(int start, HashMap<Integer, Integer> prev, int dest) {
		ArrayList<Integer> solutionPath = new ArrayList<Integer>();
		int prevNode = prev.get(dest);
		solutionPath.add(prevNode);
		while (prevNode != start) {
			prevNode = prev.get(prevNode);
			solutionPath.add(prevNode);
		}
		System.out.println(solutionPath.size());
		String solution = nodes[dest][0] + "_" + nodes[dest][1];
		for (int i = 0; i < solutionPath.size(); i+=1) {
			solution = nodes[solutionPath.get(i)][0] + "_" + nodes[solutionPath.get(i)][1] + "," + solution;
		}
		solution += dest;
		return solution;
	}
	
	/**
	 * 
	 * @param lat Latitude of the two points.
	 * @param lng Longitude of the two points.
	 * @return Approximation of the distance between two points.
	 * 
	 * This method approximates the distance between two points using the euclidean
	 * distance. Since the points are on a sphere we have to adjust the values. The
	 * error is negligible for small distances (which we deal with).
	 */
	private double euclideanDist(double[] node_1, double[] node_2) {
		double degLen = 110.25;
		double x = node_1[0] - node_2[0];
		double y = (node_1[1] - node_2[1]) * Math.cos(node_2[0]);
		return Math.sqrt(x * x + y * y) * degLen;
	}

	private void createGrid() {
		for (int i = 0; i < nodes.length; i++) {
			String gridKey = (double) Math.round(nodes[i][0] * 10) / 10 + "-"
					+ (double) Math.round(nodes[i][1] * 10) / 10;
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
		try {
			String graphPath = "C:\\Users\\Wolfen\\Desktop\\Baden-Württemberg Map";
			BufferedReader bf = new BufferedReader(new FileReader(graphPath));
			int nodesCount;
			int edgesCount;
			// Read number of nodes and create an array with the same size to store them.
			String line = bf.readLine();
			nodesCount = Integer.parseInt(line);
			nodes = new double[nodesCount][2];
			// Read number of edges and create an array with the same size to store them.
			line = bf.readLine();
			edgesCount = Integer.parseInt(line);
			edges = new int[edgesCount][3];
			// Create offset array and init with -1 (no outgoing edge)
			offsets = new int[nodesCount];
			Arrays.fill(offsets, -1);
			line = bf.readLine();
			int counterNodes = 0;
			int counterEdges = 0;
			// Read all nodes from file. Store geographic coordinates in 'nodes' array
			while (counterNodes < nodesCount) {
				String vertexData[] = line.split(" ");
				nodes[counterNodes][0] = Double.parseDouble(vertexData[2]);
				nodes[counterNodes][1] = Double.parseDouble(vertexData[3]);
				counterNodes++;
				line = bf.readLine();
			}
			// Read all edges from file.
			counterNodes = -1;
			while (counterEdges < edgesCount) {
				String edgeData[] = line.split(" ");
				// Store start-node, dest-node and weighting in 'edges' array.
				edges[counterEdges][0] = Integer.parseInt(edgeData[0]);
				edges[counterEdges][1] = Integer.parseInt(edgeData[1]);
				edges[counterEdges][2] = Integer.parseInt(edgeData[2]);
				/*
				 * Checks if the current edge starts from a new vertex and if so, stores the
				 * offset of that new vertex in the 'offsets' array. 'counternodes' stores
				 * the ID of the current start node. So if the next edge starts from a new node,
				 * 'counternodes < edges[counterEdges][0]' is true (since edges are sorted by
				 * their starting node).
				 */
				if (counterNodes < edges[counterEdges][0]) {
					counterNodes = edges[counterEdges][0];
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
