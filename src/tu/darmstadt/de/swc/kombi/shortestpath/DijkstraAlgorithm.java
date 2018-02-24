package tu.darmstadt.de.swc.kombi.shortestpath;

import java.util.ArrayList;

/**
 * Original author: Neeraj Mishra 
 * Adapted from the following website
 * http://www.thecrazyprogrammer.com/2014/03/dijkstra-algorithm-for-finding-shortest-path-of-a-graph.html
 * Accessed on November 23, 2016
 */
public class DijkstraAlgorithm {

	int[] pathpred = null;

	// Optimize this such that you perform initial computation
	// just once for all paths and store it.
	

	/**
	 * 
	 * @param adjacencyMatrix Instance of AdjacencyMatrix
	 * @param n Number of nodes
	 * @param startnode Node ID of start node
	 */
	static void dijkstra(int[][] adjacencyMatrix, int n, int startnode) {

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

		// print the path and distance of each node
		for (i = 0; i < n; i++)
			if (i != startnode) {
				System.out.println("\nDistance of node:" + i + " = " + distance[i]);
				System.out.print("Path=" + i);

				j = i;
				do {
					j = pred[j];
					System.out.print("<-" + j);
				} while (j != startnode);
			}
	}

	/**
	 * 
	 * @param adjacencyMatrix Instance of AdjancencyMatrix
	 * @param n Number of nodes
	 * @param startnode Node ID of start node
	 * @param destinationNode Node ID of destination node
	 * @return
	 */
	static ArrayList<String> getShortestPathInNodes(int[][] adjacencyMatrix, int n, int startnode,
			int destinationNode) {

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

		System.out.println("\n\n");
		// print the path and distance of each node
		// for (i = 0; i < n; i++){
		ArrayList<String> arrPathNodes = new ArrayList<String>();
		if (destinationNode != startnode) {
			System.out.println("\nDistance of node:" + destinationNode + " = " + distance[destinationNode]);
			// System.out.print("Path=" + destinationNode);
			arrPathNodes.add(String.valueOf(destinationNode));
			j = destinationNode;
			do {
				j = pred[j];
				// System.out.print("<-" + j);
				arrPathNodes.add(String.valueOf(j));
			} while (j != startnode);
		}
		return arrPathNodes;
		// }
	}

	/**
	 * 
	 * @param adjacencyMatrix Instance of AdjacencyMatrix
	 * @param n Number of nodes
	 * @param startnode Node ID of start node
	 */
	public void computeShortestPath(int[][] adjacencyMatrix, int n, int startnode) {
		int[][] cost = new int[n][n];
		int[] distance = new int[n];
		pathpred = new int[n];
		Boolean[] visited = new Boolean[n];

		int count = 0, mindistance = 0, nextnode = 0, i = 0, j = 0;

		// pathpred[] stores the predecessor of each node
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
			pathpred[i] = startnode;
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
						pathpred[i] = nextnode;
					}
			count++;
		}
	}

	/**
	 * 
	 * @param startnode Node ID of start node
	 * @param destinationNode Node ID of destination node
	 * @return List of nodes along the path
	 */
	public ArrayList<String> getShortestPathAsList(int startnode, int destinationNode) {
		ArrayList<String> arrPathNodes = new ArrayList<String>();
		int j = 0;
		if (destinationNode != startnode) {
			// System.out.print("Path=" + destinationNode);
			arrPathNodes.add(String.valueOf(destinationNode));
			j = destinationNode;
			do {
				j = pathpred[j];
				// System.out.print("<-" + j);
				arrPathNodes.add(String.valueOf(j));
			} while (j != startnode);
		}

		return arrPathNodes;
	}
	
	
	/**
	 * 
	 * @param adjacencyMatrix Instance of AdjacencyMatrix
	 * @param n Number of nodes
	 * @param startnode Node ID of start node
	 * @param destinationNode Node ID of destination node
	 * @return Length of shortest path
	 */
	public static int getShortestPathInLength(int[][] adjacencyMatrix, int n, int startnode, int destinationNode) {

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

		return distance[destinationNode];

	}

}