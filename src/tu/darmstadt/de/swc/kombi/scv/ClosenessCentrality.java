package tu.darmstadt.de.swc.kombi.scv;

/**
 * 
 * Adapted from following websites:
 * http://www.thecrazyprogrammer.com/2014/03/dijkstra-algorithm-for-finding-shortest-path-of-a-graph.html
 * http://med.bioinf.mpi-inf.mpg.de/netanalyzer/help/2.7/
 * Accessed on November 22, 2016
 */
/**
 * Computes the closeness centrality of adjacency matrix
 * 
 * @author suhas
 *
 */
public class ClosenessCentrality implements ISelectionCriteria {

	/**
	 * Calculate closeness centrality
	 * 
	 * @param adjacencyMatrix Instance of AdjacencyMatrix
	 * @param n number of nodes
	 * @return array containing closeness
	 */
	public static double[] compute(int[][] adjacencyMatrix, int n) {
		// No of vertices
		n = adjacencyMatrix[0].length;
		double[] closeness = new double[n];
		for (int i = 0; i < n; i++) {
			int[] result = getShortestPathInLength(adjacencyMatrix, n, i);
			double sum = 0;
			for (int k = 0; k < result.length; k++) {
				sum += result[k];
			}
			// System.out.println("Sum:"+sum);
			double val = sum / (n - 1);
			double cc = 1 / val;
			closeness[i] = cc;

		}

		return closeness;
	}

	/**
	 * Get shortest path between two nodes
	 * 
	 * @param adjacencyMatrix Instance of AdjacencyMatrix
	 * @param n Number of nodes
	 * @param startnode Node ID of start node
	 * @return array containing all the shortest paths
	 */
	public static int[] getShortestPathInLength(int[][] adjacencyMatrix, int n, int startnode) {

		int[][] cost = new int[n][n];
		int[] distance = new int[n];
		int[] pred = new int[n];
		Boolean[] visited = new Boolean[n];

		int count = 0, mindistance = 0, nextnode = 0, i = 0, j = 0;

		// pred[] stores the predecessor of each node
		// count gives the number of nodes seen so far
		// create the cost matrix
		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				if (adjacencyMatrix[i][j] == 0)
					cost[i][j] = 99999;
				else
					cost[i][j] = adjacencyMatrix[i][j];

		// initialize pred[],distance[] and visited[]
		for (i = 0; i < n; i++) {
			distance[i] = cost[startnode][i];
			pred[i] = startnode;
			visited[i] = false;
		}

		distance[startnode] = 0;
		visited[startnode] = true;
		count = 1;

		while (count < n - 1) {
			mindistance = 99999;

			// nextnode gives the node at minimum distance
			for (i = 0; i < n; i++)
				if ((distance[i] < mindistance) && (!visited[i])) {
					mindistance = distance[i];
					nextnode = i;
				}

			// check if a better path exists through nextnode
			visited[nextnode] = true;
			for (i = 0; i < n; i++)
				if (!visited[i])
					if (mindistance + cost[nextnode][i] < distance[i]) {
						distance[i] = mindistance + cost[nextnode][i];
						pred[i] = nextnode;
					}
			count++;
		}

		return distance;

	}
}