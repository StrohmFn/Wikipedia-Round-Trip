package strohmfn.roundTrip;

import java.util.ArrayList;

public class TravelingSalesPerson {
	
	public static void main(String[] args) {
		TravelingSalesPerson tsp = new TravelingSalesPerson();
		int[][] in = new int[5][5];
		in[0][0] = 0;
		in[1][1] = 0;
		in[2][2] = 0;
		in[3][3] = 0;
		in[4][4] = 0;
		in[0][1] = 9;
		in[0][2] = 8;
		in[0][3] = 999;
		in[0][4] = 7;
		in[1][0] = 15;
		in[1][2] = 999;
		in[1][3] = 999;
		in[1][4] = 999;
		in[2][0] = 6;
		in[2][1] = 5;
		in[2][3] = 999;
		in[2][4] = 4;
		in[3][0] = 999;
		in[3][1] = 999;
		in[3][2] = 3;
		in[3][4] = 999;
		in[4][0] = 999;
		in[4][1] = 2;
		in[4][2] = 999;
		in[4][3] = 3;
//		for (int i = 0; i < in.length; i++) {
//			for (int j = 0; j < in.length; j++) {
//				in[i][j] = 5;
//			}
//		}
		System.out.println(tsp.computeTSP(in));
	}

	private ArrayList<Integer> outputArray = new ArrayList<Integer>();
	private int g[][], p[][], npow, N, d[][];
	public static long time;

	public ArrayList<Integer> computeTSP(int[][] inputArray) {
		long start = System.currentTimeMillis();

		N = inputArray.length;
		npow = (int) Math.pow(2, N);
		g = new int[N][npow];
		p = new int[N][npow];
		d = inputArray;

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < npow; j++) {
				g[i][j] = -1;
				p[i][j] = -1;
			}
		}

		// initialize based on distance matrix
		for (int i = 0; i < N; i++) {
			g[i][0] = inputArray[i][0];
		}

		int result = tsp(0, npow - 2);
		outputArray.add(0);
		getPath(0, npow - 2);
		outputArray.add(result);

		long end = System.currentTimeMillis();
		time = (end - start) / 1000;
		System.out.println("Time needed for TSP computation: " + time + "s");
		return outputArray;
	}

	private int tsp(int start, int set) {
		int masked, mask, result = -1, temp;
		
		if(g[start][set] != -1) {
			return g[start][set];
		}
		else {
			for(int x = 0; x < N; x++) {
				mask =  npow - 1 - (int) Math.pow(2, x);
				masked = set & mask;
				if(masked != set) {
					temp = d[start][x] + tsp(x,masked);
					if(result == -1 || result > temp) {
						result = temp;
						p[start][set] = x;
					}
				}
			}
			g[start][set] = result;
			return result;
		}
	}
	
	private void getPath(int start, int set) {
		if(p[start][set] == -1) {
			return;
		}
		
		int x = p[start][set];
		int mask = npow - 1 - (int) Math.pow(2, x);
		int masked = set & mask;
		
		outputArray.add(x);
		getPath(x,masked);
	}
}
