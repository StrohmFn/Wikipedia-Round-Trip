package strohmfn.roundTrip;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RoundTripController {

	public static void main(String[] args) {
		SpringApplication.run(RoundTripController.class, args);

	}
	
	private int[][] edges;
	private double[][] vertices;
	private int[] offsets;
	
	@PostConstruct
	private void loadMapData() throws IOException, NullPointerException {
		try {
			String graphPath = "C:\\Users\\Wolfen\\Desktop\\Baden-Württemberg Map";
			BufferedReader bf = new BufferedReader(new FileReader(graphPath));
			int verticesCount;
			int edgesCount;
			// Read number of vertices and create an array with the same size to store them.
			String line = bf.readLine();
			verticesCount = Integer.parseInt(line);
			vertices = new double[verticesCount][2];
			// Read number of edges and create an array with the same size to store them.
			line = bf.readLine();
			edgesCount = Integer.parseInt(line);
			edges = new int[edgesCount][3];
			// Create offset array and init with -1 (no outgoing edge)
			offsets = new int[verticesCount];
			Arrays.fill(offsets, -1);
			line = bf.readLine();
			int counterVertices = 0;
			int counterEdges = 0;
			// Read all vertices from file. Store geographic coordinates in 'vertices' array
			while (counterVertices < verticesCount) {
				String vertexData[] = line.split(" ");
				vertices[counterVertices][0] = Double.parseDouble(vertexData[2]);
				vertices[counterVertices][1] = Double.parseDouble(vertexData[3]);
				counterVertices++;
				line = bf.readLine();
			}
			// Read all edges from file.
			counterVertices = -1;
			while (counterEdges < edgesCount) {
				String edgeData[] = line.split(" ");
				// Store start-node, dest-node and weighting in 'edges' array.
				edges[counterEdges][0] = Integer.parseInt(edgeData[0]);
				edges[counterEdges][1] = Integer.parseInt(edgeData[1]);
				edges[counterEdges][2] = Integer.parseInt(edgeData[2]);
				/*
				 * Checks if the current edge starts from a new vertex and if so, stores the
				 * offset of that new vertex in the 'offsets' array. 'counterVertices' stores
				 * the ID of the current start node. So if the next edge starts from a new node,
				 * 'counterVertices < edges[counterEdges][0]' is true (since edges are sorted by
				 * their starting node).
				 */
				if (counterVertices < edges[counterEdges][0]) {
					counterVertices = edges[counterEdges][0];
					offsets[counterVertices] = counterEdges;
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
					"Number of edges or vertices is not consistent with the specification at the beginning of the graph file, exiting!");
			System.exit(3);

		}
	}

	@RequestMapping("/calc/{startNode}/{roundTripDuration}/{visitNodesLat}/{visitNodesLng}")
	public String calculateRoundTrip(@PathVariable double[] startNode, @PathVariable int roundTripDuration,
			@PathVariable double[] visitNodesLat, @PathVariable double[] visitNodesLng) {
		double[][] visitNodes = { visitNodesLat, visitNodesLng };
		String roundTripPath = "TEST --- start node (lat/lng): " + startNode[0] + "/" + startNode[1]
				+ "; Round trip duration: " + roundTripDuration + "; visit node (2. node lat/lng): " + visitNodes[0][1]
				+ "/" + visitNodes[1][1];
		System.out.println(roundTripPath);
		return roundTripPath;
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
			HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> prev = new HashMap<Integer, Integer>();
			dist.put(start, 0);
			int startVertex[] = { start, 0 };
			frontier.add(startVertex);
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
				return "No path found from vertex '" + start + "' to vertex '" + dest + "'!";
			}
			return generateSolutionString(start, prev, dest) + "||| Total Dist: " + dist.get(dest);
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
		int cnt = 0;
		String solution = "";
		for (int i = solutionPath.size() - 1; i >= 0; i--) {
			solution += solutionPath.get(i) + " -> ";
			cnt++;
		}
		System.out.println(cnt);
		solution += dest;
		return solution;
	}
}
