package strohmfn.roundTrip;

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
	@RequestMapping("/calc/{visitNodesLat}/{visitNodesLon:.+}")
	public String calculateRoundTrip(@PathVariable double[] visitNodesLat, @PathVariable double[] visitNodesLon) {
		System.out.println("Calculating Roundtrip...");
		int len = visitNodesLat.length;
		double[][] visitNodes = new double[len][2];
		for (int i = 0; i < len; i++) {
			visitNodes[i][0] = visitNodesLat[i];
			visitNodes[i][1] = visitNodesLon[i];
		}
		System.out.println("Shortest Roundtrip Calculated!");
		return graph.calcTour(visitNodes);
	}
}
