package tu.darmstadt.de.swc.kombi.matrix;

import java.util.HashMap;
import org.graphstream.graph.Graph;

import tu.darmstadt.de.swc.kombi.shortestpath.DijkstraAlgorithm;

/**
 * Adjacency matrix
 * 
 * @author suhas
 *
 */
public class AdjacencyMatrix {
	int[][] adjacencyMatrix;
	double[][] distanceMatrix;

	public void generateAdjacencyMatrixFromFile(String file) {

	}

	/**
	 * Create adjacency matrix from graph
	 * 
	 * @param graph Instance of Graph
	 * @return adjacency matrix Instance of AdjancencyMatrix
	 */
	public int[][] createAdjacencyMatrixFromGraph(Graph graph) {
		int n = graph.getNodeCount();
		int[][] matrix = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				matrix[i][j] = graph.getNode(i).hasEdgeBetween(j) ? 1 : 0;
			}
		}

		return matrix;
	}

	/**
	 * Create adjacency matrix based on clustering
	 * 
	 * @param graph Instance of graph
	 * @param mapNodesToIter HashMap of nodes and indices
	 * @param clusteringFirstStep TRUE if clustering is being performed else FALSE
	 */
	public void createAdjacencyMatrix(Graph graph, HashMap<Integer, String> mapNodesToIter,
			Boolean clusteringFirstStep) {
		int n = graph.getNodeCount();
		adjacencyMatrix = new int[n][n];

		if (clusteringFirstStep) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					adjacencyMatrix[i][j] = graph.getNode(i).hasEdgeBetween(j) ? 1 : 0;
				}
			}
		} else {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					adjacencyMatrix[i][j] = graph.getNode(mapNodesToIter.get(i)).hasEdgeBetween(mapNodesToIter.get(j))
							? 1 : 0;
				}
			}
		}

	}

	/**
	 * Get adjacency matrix
	 * 
	 * @return Adjacency matrix
	 */
	public int[][] getadjacencyMatrix() {
		return adjacencyMatrix;
	}

	/**
	 * Create distance matrix
	 * 
	 * @param adjacencyMatrix Instance of Adjacency matrix
	 * @param arrNodes Array of nodes
	 */
	public void createDistanceMatrix(int[][] adjacencyMatrix, String[] arrNodes) {
		/** int n = graph.getNodeCount(); */
		int n = adjacencyMatrix.length;
		distanceMatrix = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				distanceMatrix[i][j] = DijkstraAlgorithm.getShortestPathInLength(adjacencyMatrix, n, i, j);
			}
		}

	}

	/**
	 * Get distance matrix
	 * 
	 * @return distance matrix
	 */
	public double[][] getDistanceMatrix() {
		return distanceMatrix;
	}

}
