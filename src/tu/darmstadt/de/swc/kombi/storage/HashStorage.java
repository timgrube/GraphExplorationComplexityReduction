package tu.darmstadt.de.swc.kombi.storage;

import java.util.HashMap;

/**
 * Class dealing with hashmaps of all properties and matrices
 * 
 * @author suhas
 *
 */
public class HashStorage {
	// 1. Store betweenness : nodeid, betweenness
	// 2. Store pagerank : nodeid, pagerank
	// 3. Store degree : nodeid, degree
	// 4. Store clustering coefficient : nodeid, cc
	// 5. Store nodeid : nodeid, iter
	// 6. Store adjacency matrix : matrix, level
	// Level, Integer[][]
	private HashMap<Integer, int[][]> hashAdjacencyMatrix = new HashMap<Integer, int[][]>();
	// Level, <index, node>
	private HashMap<Integer, HashMap<Integer, Integer>> hashIterToNodeId = new HashMap<Integer, HashMap<Integer, Integer>>();
	// Level, <node-id, betweenness>
	private HashMap<Integer, HashMap<Integer, Double>> hashBetweenness = new HashMap<Integer, HashMap<Integer, Double>>();
	// Level, <node-id, pagerank>
	private HashMap<Integer, HashMap<Integer, Double>> hashPageRank = new HashMap<Integer, HashMap<Integer, Double>>();
	// Level, <node-id, degree>
	private HashMap<Integer, HashMap<Integer, Integer>> hashDegree = new HashMap<Integer, HashMap<Integer, Integer>>();
	// Level, <node-id, cc>
	private HashMap<Integer, HashMap<Integer, Double>> hashClusteringCoefficient = new HashMap<Integer, HashMap<Integer, Double>>();

	private HashMap<Integer, HashMap<Integer, Double>> hashCloseness = new HashMap<Integer, HashMap<Integer, Double>>();

	/**
	 * Store adjacency matrix of the corresponding level
	 * 
	 * @param level Reduction level
	 * @param adjacencyMatrix Adjacency matrix of the input graph
	 */
	public void storeAdjacencyMatrix(int level, int[][] adjacencyMatrix) {
		hashAdjacencyMatrix.put(level, adjacencyMatrix);

	}

	/**
	 * Store hashmap of the iteration level and the node's hashmap
	 * 
	 * @param level Reduction level
	 * @param hashMap Instance of HashMap
	 */
	public void storeIterToNodeId(int level, HashMap<Integer, Integer> hashMap) {
		hashIterToNodeId.put(level, hashMap);

	}

	/**
	 * Store a node's betweenness based on it's level
	 * 
	 * @param level Reduction level
	 * @param hashMap Instance of HashMap
	 */
	public void storeNodeBetweenness(int level, HashMap<Integer, Double> hashMap) {
		hashBetweenness.put(level, hashMap);

	}

	/**
	 * Store a node's page rank based on it's level
	 * 
	 * @param level Reduction level
	 * @param hashMap Instance of HashMap
	 */

	public void storePageRank(int level, HashMap<Integer, Double> hashMap) {
		hashPageRank.put(level, hashMap);

	}

	/**
	 * Store a node's degree based on it's level
	 * 
	 * @param level Reduction level
	 * @param hashMap Instance of HashMap
	 */
	public void storeNodeDegree(int level, HashMap<Integer, Integer> hashMap) {
		hashDegree.put(level, hashMap);

	}

	/**
	 * Store a node's clustering coefficient based on it's level
	 * 
	 * @param level Reduction level
	 * @param hashMap Instance of HashMap
	 */
	public void storeClusteringCoefficient(int level, HashMap<Integer, Double> hashMap) {
		hashClusteringCoefficient.put(level, hashMap);

	}

	/**
	 * Store a node's closeness based on it's level
	 * 
	 * @param level Reduction level
	 * @param hashMap Instance of HashMap
	 */
	public void storeClosenessCentrality(int level, HashMap<Integer, Double> hashMap) {
		hashCloseness.put(level, hashMap);

	}

	/**
	 * Store index and it's corresponding node
	 * 
	 * @param adjacencyMatrix Adjacency matrix of the input graph
	 * @return map containing index and node id
	 */
	public HashMap<Integer, Integer> storeMatrixIndexAsNodeId(int[][] adjacencyMatrix) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < adjacencyMatrix[0].length; i++) {
			map.put(i, i);
		}

		return map;
	}

	/**
	 * Get adjacency matrix pertaining to a level
	 * 
	 * @param level Reduction level
	 * @return array containing adjacency matrix
	 */
	public int[][] getAdjacencyMatrix(int level) {
		return hashAdjacencyMatrix.get(level);
	}

	/**
	 * Get Current level
	 * 
	 * @return Current reduction level
	 */
	public int getCurrentLevel() {
		return hashAdjacencyMatrix.size() - 1;
	}

	/**
	 * Get edge count
	 * 
	 * @param adjacencyMatrix Adjacency matrix of the input graph
	 * @return number of edges
	 */
	public int getAdjacencyMatrixEdgeCount(int[][] adjacencyMatrix) {
		int edges = 0;
		int n = adjacencyMatrix.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (adjacencyMatrix[i][j] == 1) {
					edges++;
				}
			}
		}

		return edges;
	}

	/**
	 * Get node id based on level and index
	 * 
	 * @param level Reduction level
	 * @param index Index of node
	 * @return node id
	 */
	public Integer getNodeId(int level, int index) {
		@SuppressWarnings("unused")
		HashMap<Integer, Integer> map = hashIterToNodeId.get(level);

		return hashIterToNodeId.get(level).get(index);
	}

	/**
	 * Get Node ids of a particular level
	 * 
	 * @param level Reduction level
	 * @return map containing node-ids
	 */
	public HashMap<Integer, Integer> getHashIter(int level) {
		return hashIterToNodeId.get(level);
	}

	/**
	 * Get betweenness of a node
	 * 
	 * @param level Reduction level
	 * @param index Index of node
	 * @return betweenness centrality
	 */
	public Double getNodeBetweenness(int level, int index) {
		return hashBetweenness.get(level).get(index);
	}

	/**
	 * Get closeness of a node
	 * 
	 * @param level Reduction level
	 * @param index Index of node
	 * @return closeness centrality
	 */
	public Double getNodeCloseness(int level, int index) {
		return hashCloseness.get(level).get(index);
	}

	/**
	 * Get page rank of a node
	 * 
	 * @param level Reduction level
	 * @param index Index of node
	 * @return page rank
	 */
	public Double getPageRank(int level, int index) {
		return hashPageRank.get(level).get(index);
	}

	/**
	 * Get degree
	 * 
	 * @param level Reduction level
	 * @param index Index of node
	 * @return degree Centrality
	 */
	public Integer getDegree(int level, int index) {
		return hashDegree.get(level).get(index);
	}

	/**
	 * Get Clustering coefficient
	 * 
	 * @param level Reduction level
	 * @param index Index of node
	 * @return Clustering Coefficient
	 */
	public Double getClusteringCoefficient(int level, int index) {
		return hashClusteringCoefficient.get(level).get(index);
	}

	/**
	 * Store and return betweenness
	 * 
	 * @param bc Betweenness centrality
	 * @return betweenness centrality map
	 */
	public HashMap<Integer, Double> setupBetweennessFormat(double[] bc) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		for (int i = 0; i < bc.length; i++) {
			map.put(i, bc[i]);
		}
		return map;
	}

	/**
	 * Store and return page rank
	 * 
	 * @param pr PageRank
	 * @return page rank map
	 */
	public HashMap<Integer, Double> setupPRFormat(double[] pr) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		if(pr!=null){
		for (int i = 0; i < pr.length; i++) {
			map.put(i, pr[i]);
		}
		}
		return map;
	}

	/**
	 * Store and return clustering coefficient
	 * 
	 * @param ccoeff Clustering Coefficient
	 * @return clustering coefficient map
	 */
	public HashMap<Integer, Double> setupCCoeffFormat(double[] ccoeff) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		for (int i = 0; i < ccoeff.length; i++) {
			map.put(i, ccoeff[i]);
		}
		return map;
	}

	/**
	 * Store and return degree
	 * 
	 * @param dc Degree Centrality
	 * @return degree map
	 */
	public HashMap<Integer, Integer> setupDCFormat(int[] dc) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < dc.length; i++) {
			map.put(i, dc[i]);
		}
		return map;
	}

	/**
	 * Store and return closeness
	 * 
	 * @param cc Closeness Centrality
	 * @return closeness map
	 */
	public HashMap<Integer, Double> setupCCFormat(double[] cc) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		for (int i = 0; i < cc.length; i++) {
			map.put(i, cc[i]);
		}
		return map;

	}

	/**
	 * Return adjacencyMatrix map
	 * 
	 * @return adjacency matrix map
	 */
	public HashMap<Integer, int[][]> getHashAdjacencyMatrix() {
		return hashAdjacencyMatrix;
	}

	/**
	 * Return node id map
	 * 
	 * @return node id map
	 */
	public HashMap<Integer, HashMap<Integer, Integer>> getHashIterToNodeId() {
		return hashIterToNodeId;
	}

	/**
	 * Clear all maps except level 0
	 */
	public void clearHashMapsExceptRoot() {

		if (hashAdjacencyMatrix.size() > 1) {
			int i = hashAdjacencyMatrix.size() - 1;
			while (i != 0) {
				hashAdjacencyMatrix.remove(i);
				if (hashBetweenness.get(i) != null) {
					hashBetweenness.remove(i);
				}
				if (hashPageRank.get(i) != null) {
					hashPageRank.remove(i);
				}
				if (hashDegree.get(i) != null) {
					hashDegree.remove(i);
				}
				if (hashClusteringCoefficient.get(i) != null) {
					hashClusteringCoefficient.remove(i);
				}
				if (hashCloseness.get(i) != null) {
					hashCloseness.remove(i);
				}
				i = hashAdjacencyMatrix.size() - 1;
			}
		}

		if (hashIterToNodeId.size() > 1) {
			int i = hashIterToNodeId.size() - 1;
			while (i != 0) {
				hashIterToNodeId.remove(i);

				i = hashIterToNodeId.size() - 1;
			}
		}

	}

	/**
	 * Clear all maps except adjacency matrix at root
	 */
	public void resetAllHashMapsExceptRootAdjacency() {
		hashBetweenness.clear();
		hashPageRank.clear();
		hashDegree.clear();
		hashClusteringCoefficient.clear();
		hashCloseness.clear();
	}

	/**
	 * Clear all hash maps
	 */
	public void resetAllHashMaps() {
		hashAdjacencyMatrix.clear();
		hashIterToNodeId.clear();
		hashBetweenness.clear();
		hashPageRank.clear();
		hashDegree.clear();
		hashClusteringCoefficient.clear();
		hashCloseness.clear();
	}

	/**
	 * Return betweenness centrality
	 * 
	 * @return betweenness map
	 */
	public HashMap<Integer, HashMap<Integer, Double>> getHashBetweenness() {
		return hashBetweenness;
	}

	/**
	 * Return PageRank
	 * 
	 * @return page rank map
	 */
	public HashMap<Integer, HashMap<Integer, Double>> getHashPageRank() {
		return hashPageRank;
	}

	/**
	 * Return degree
	 * 
	 * @return degree map
	 */
	public HashMap<Integer, HashMap<Integer, Integer>> getHashDegree() {
		return hashDegree;
	}

	/**
	 * Return clustering coefficient
	 * 
	 * @return clustering coefficient map
	 */

	public HashMap<Integer, HashMap<Integer, Double>> getHashClusteringCoefficient() {
		return hashClusteringCoefficient;
	}

	/**
	 * Return closeness centrality
	 * 
	 * @return closeness map
	 */
	public HashMap<Integer, HashMap<Integer, Double>> getHashCloseness() {
		return hashCloseness;
	}

}
