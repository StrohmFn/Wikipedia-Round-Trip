package strohmfn.roundTrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

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
	
	Graph graph = new Graph();

	//TODO handle nullpointer (node id -1)
	@RequestMapping("/calc/{startNode}/{roundTripDuration}/{visitNodesLat}/{visitNodesLng}")
	public String calculateRoundTrip(@PathVariable double[] startNode, @PathVariable int roundTripDuration,
			@PathVariable double[] visitNodesLat, @PathVariable double[] visitNodesLng) {
		double[][] visitNodes = { visitNodesLat, visitNodesLng };
		System.out.println("Mapping coodinates to nearsest node...");
		int node = graph.getClosestNode(startNode);
		int testDest = 15515186;
		System.out.println("Calculating shortest path from start node " + node + " to destination node " + testDest + "...");
		double startTime = System.currentTimeMillis();
		String  solution = graph.dijkstra(7791852, testDest);
		System.out.println("It took " + (System.currentTimeMillis()-startTime)/1000 + " seconds to calculated the shortest path!");
		return solution;
	}
}
