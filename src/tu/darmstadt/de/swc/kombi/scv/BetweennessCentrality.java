
package tu.darmstadt.de.swc.kombi.scv;

import java.io.IOException;
import java.util.Arrays;

import tu.darmstadt.de.swc.kombi.reader.GraphReader;
import tu.darmstadt.de.swc.kombi.reader.MatrixToList;

/**
 * 
 * Source: https://github.com/mbalassi/msc-thesis - Accessed on November 22nd, 2016.
 * URL - Adapted from https://github.com/mbalassi/msc-thesis/blob/master/src/main/java/hu/elte/inf/mbalassi/msc/seq/betweenness/BetweennessCentrality.java
 * 
 */

/**
 * Calculates the betweenness centrality of each node. The number of shortest
 * paths going through it is returned for each node. If k shortest paths exist
 * between two nodes, each path contributes only 1/k to the score of the nodes
 * it passes through. A path does not contribute to the score of its end points.
 * 
 */
public final class BetweennessCentrality implements ISelectionCriteria{

	public static double[] compute(int n, int[][] adjacencyMatrix)
			throws NumberFormatException, IOException {
		String graphInput = "";
		for (int i = 0; i < n; i++) {
			graphInput = graphInput + i;
			for (int j = 0; j < n; j++) {
				if (adjacencyMatrix[i][j] == 1) {
					graphInput = graphInput + " " + j;
				}
			}
			graphInput = graphInput + "\n";
		}

		GraphReader graph = new GraphReader(graphInput);
		int[][] graphArray = graph.getGraph();

		// Calculate betweenness centrality
		double[] betw = BetweennessCentrality.of(graphArray);

		return MatrixToList.printNodeValues(graph, betw);

	}

	/**
	 * Does the actual calculation of Betweenness Centrality.
	 * 
	 * @param graph the graph
	 * @return betweenness centrality of each node
	 */
	public static double[] of(int[][] graph) {

		int n = graph.length;
		double[] res = new double[n];
		for (int i = 0; i < n; ++i) {
			double[] partialRes = calculateFromOneNode(graph, i);
			for (int j = 0; j < n; ++j) {
				res[j] += partialRes[j];
			}			
		}

		return res;
	}

	/**
	 * Calculates contributions of paths from source node.
	 * 
	 * @param graph graph.
	 * @param s source node.
	 * @return partial scores.
	 */
	private static double[] calculateFromOneNode(int[][] graph, int s) {
		int n = graph.length;
		// Initialize
		int[] queue = new int[n]; // queue containing s
		queue[0] = s;
		int qf = 0; // queue front
		int qb = 1; // queue back
		int[] stack = new int[n]; // empty stack
		int sp = 0; // stack pointer
		double[] pathsTo = new double[n]; // number of shortest paths from s to
											// each node
		pathsTo[s] = 1.0;
		int[] dist = new int[n]; // distance from s for each node
		Arrays.fill(dist, -1);
		dist[s] = 0;
		// BFS
		while (qf < qb) {
			int u = queue[qf++];
			for (int i = 0; i < graph[u].length; ++i) {
				int v = graph[u][i];
				if (dist[v] < 0) {
					dist[v] = dist[u] + 1;
					queue[qb++] = v;
				}
				if (dist[v] == dist[u] + 1) {
					pathsTo[v] += pathsTo[u];
				}
			}
			stack[sp++] = u;
		}
		// Calculate scores
		double[] res = new double[n];
		while (sp > 0) {
			int u = stack[--sp];
			if (u == s)
				continue;
			for (int i = 0; i < graph[u].length; ++i) {
				int v = graph[u][i];
				if (dist[v] == dist[u] + 1) {
					res[u] += (res[v] + 1.0) * (pathsTo[u] / pathsTo[v]);
				}
			}
		}
		return res;
	}

}
