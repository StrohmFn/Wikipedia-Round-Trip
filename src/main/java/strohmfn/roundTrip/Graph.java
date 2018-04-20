package strohmfn.roundTrip;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Graph {
	private int[][] edges;
	private double[][] nodes;
	private int[] offsets;

	// Singleton
	private static Graph instance;

	public static Graph getInstance() {
		if (Graph.instance == null) {
			Graph.instance = new Graph();
			Graph.instance.loadMapData();
		}
		return Graph.instance;
	}

	private void loadMapData() {
		System.out.println("Loading map data...");
		try {
			loadNodes();
			loadEdges();
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

	private void loadEdges() throws FileNotFoundException, IOException {
		String line;
		String edgesPath = "resources/de_edges.fs";
		BufferedReader bf = new BufferedReader(new FileReader(edgesPath));
		// Read number of edges and create an array with the same size to store them.
		line = bf.readLine();
		int edgesCount = Integer.parseInt(line);
		edges = new int[3][edgesCount];
		int counterEdges = 0;
		// Read all edges from file.
		// Create offset array and init with -1 (no outgoing edge)
		offsets = new int[nodes[0].length];
		Arrays.fill(offsets, -1);
		int counterNodes = -1;
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
	}

	private void loadNodes() throws FileNotFoundException, IOException {
		String nodespath = "resources/de_nodes.fs";
		BufferedReader bf = new BufferedReader(new FileReader(nodespath));
		// Read number of nodes and create an array with the same size to store them.
		String line = bf.readLine();
		int nodesCount = Integer.parseInt(line);
		nodes = new double[2][nodesCount];
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
