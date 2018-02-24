package tu.darmstadt.de.swc.kombi.scv;

import java.util.ArrayList;


/**
 * Adapted from following website:
 * http://med.bioinf.mpi-inf.mpg.de/netanalyzer/help/2.7/
 * Local clustering coefficient for undirected graphs : 2N/k(k-1)
 * Local clustering coefficient for directed graphs : N/k(k-1)
 * Accessed on November 22, 2016
 */
/**
 * Computes clustering coeffcient of a node in a graph
 * 
 * @author suhas
 *
 */
public class ClusteringCoefficient implements ISelectionCriteria {

	
	/**
	 * Get clustering coefficient of a node
	 * 
	 * @param adjacencyMatrix  Adjacency matrix of an input graph
	 * @param node  Node ID
	 * @param isDirected  TRUE if the graph is directed else FALSE
	 * @return clustering coefficient of a node
	 */
	private static double getNodeClusteringCoefficient(int[][] adjacencyMatrix, int node, Boolean isDirected) {
		// Pass the right iter i since you will be storing node-id and
		// appropriate iter for every level
		double coefficient = 0.0;

		if (isDirected) {
			int n = DegreeCentrality.getDegree(adjacencyMatrix, node);
			if (n > 1) {
				ArrayList<Integer> arrNodes = new ArrayList<Integer>();
				for (int j = 0; j < adjacencyMatrix[node].length; j++) {
					if (adjacencyMatrix[node][j] == 1) {
						arrNodes.add(j);
					}
				}
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						if (j != i) {
							if (adjacencyMatrix[arrNodes.get(j)][arrNodes.get(i)] == 1) {
								coefficient++;
							}
						}
						coefficient /= (n * (n - 1)) / 2.0;
					}
				}
			}

		} else {
			int n = DegreeCentrality.getDegree(adjacencyMatrix, node);
			if (n > 1) {
				ArrayList<Integer> arrNodes = new ArrayList<Integer>();
				for (int j = 0; j < adjacencyMatrix[node].length; j++) {
					if (adjacencyMatrix[node][j] == 1) {
						arrNodes.add(j);
					}
				}
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						if (j != i) {
							if (adjacencyMatrix[arrNodes.get(j)][arrNodes.get(i)] == 1) {
								coefficient++;
							}
						}
						coefficient /= (n * (n - 1));
					}
				}
			}

		}

		return coefficient;
	}

	/**
	 * Compute clustering coefficient of all nodes in a graph
	 * 
	 * @param adjacencyMatrix  Adjacency matrix of an input graph
	 * @param isDirected  TRUE if the graph is directed else FALSE
	 * @return clustering coefficients of all nodes
	 */
	public static double[] compute(int[][] adjacencyMatrix, Boolean isDirected) {
		int n = adjacencyMatrix.length;
		double[] coeff = new double[n];
		for (int i = 0; i < n; i++) {
			coeff[i] = getNodeClusteringCoefficient(adjacencyMatrix, i, isDirected);
		}
		return coeff;
	}

}
