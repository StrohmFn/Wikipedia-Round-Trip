package strohmfn.roundTrip;

import java.util.ArrayList;

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

	Graph graph = Graph.getInstance();
	Grid grid = Grid.getInstance(graph.getNodes());

	@RequestMapping("/calc/{visitNodesLat}/{visitNodesLon:.+}")
	public String calculateRoundTrip(@PathVariable double[] visitNodesLat, @PathVariable double[] visitNodesLon) {
		System.out.println("Calculating Roundtrip...");
		// Create array of to be visited nodes.
		int len = visitNodesLat.length;
		double[][] visitNodes = new double[len][2];
		for (int i = 0; i < len; i++) {
			visitNodes[i][0] = visitNodesLat[i];
			visitNodes[i][1] = visitNodesLon[i];
		}

		int numberNodes = visitNodes.length;
		int[][] costs = calcCosts(numberNodes, visitNodes);
		ArrayList<Integer> result = new TravelingSalesPerson().computeRoundtrip(costs);

		int[] nodes = new int[numberNodes];
		for (int i = 0; i < numberNodes; i++) {
			nodes[i] = grid.getClosestNode(visitNodes[i]);
		}

		String solution = "";
		for (int i = 0; i < result.size() - 1; i++) {
			int from = nodes[result.get(i)];
			int to = nodes[result.get(i + 1)];
			solution += Dijkstra.calc(from, to, graph) + ",";
		}
		solution = solution.substring(0, solution.length() - 1);

		return solution;
	}

	private int[][] calcCosts(int numberNodes, double[][] visitNodes) {
		int[][] costs = new int[numberNodes][numberNodes];
		for (int i = 0; i < numberNodes; i++) {
			for (int j = 0; j < numberNodes; j++) {
				if (i == j) {
					costs[i][j] = 0;
				} else {
					costs[i][j] = (int) Grid.euclideanDist(visitNodes[i][0], visitNodes[i][1], visitNodes[j][0],
							visitNodes[j][1]);
				}
			}
		}
		return costs;
	}
}
