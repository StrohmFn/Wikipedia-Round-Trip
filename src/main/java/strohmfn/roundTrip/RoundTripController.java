package strohmfn.roundTrip;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoundTripController {

	@RequestMapping("/calc/{startNode}/{roundTripDuration}/{visitNodesLat}/{visitNodesLng}")
	public String helloWorld(@PathVariable double[] startNode, @PathVariable int roundTripDuration,
			@PathVariable double[] visitNodesLat, @PathVariable double[] visitNodesLng) {
		double[][] visitNodes = {visitNodesLat, visitNodesLng};
		String roundTripPath = "TEST --- start node (lat/lng): " + startNode[0] + "/" + startNode[1]
				+ "; Round trip duration: " + roundTripDuration + "; visit node (2. node lat/lng): " + visitNodes[0][1]
				+ "/" + visitNodes[1][1];
		System.out.println(roundTripPath);
		return roundTripPath;
	}
}
