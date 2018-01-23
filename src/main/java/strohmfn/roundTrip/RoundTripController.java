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
		int node = graph.getClosestNode(startNode);
		String  sol = graph.dijkstra(node, 5);
		System.out.println(sol);
		return sol;
	}
}
