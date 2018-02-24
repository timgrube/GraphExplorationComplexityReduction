package tu.darmstadt.de.swc.kombi.scv;

/**
 * Computes Degree Centrality
 * 
 * @author suhas
 *
 */
public class DegreeCentrality implements ISelectionCriteria {
	/**
	 * Get node's in degree
	 * 
	 * @param adjacencyMatrix Adjacency matrix of the input graph
	 * @param nodeId Node ID
	 * @return degree Return degree of the node
	 */

	public static int getDegree(int[][] adjacencyMatrix, int nodeId) {
		int degree = 0;
		for (int j = 0; j < adjacencyMatrix[nodeId].length; j++) {
			if (adjacencyMatrix[nodeId][j] == 1) {
				degree += adjacencyMatrix[nodeId][j];
			}
		}

		return degree;
	}

	/**
	 * Compute Degree Centrality of adjacency matrix
	 * 
	 * @param adjacencyMatrix Adjacency matrix of the input graph
	 * @return array containing degree centrality of all nodes
	 */
	public static int[] compute(int[][] adjacencyMatrix) {
		int n = adjacencyMatrix.length;
		int[] degree = new int[n];
		for (int i = 0; i < n; i++) {
			degree[i] = getDegree(adjacencyMatrix, i);
		}

		return degree;
	}

}
