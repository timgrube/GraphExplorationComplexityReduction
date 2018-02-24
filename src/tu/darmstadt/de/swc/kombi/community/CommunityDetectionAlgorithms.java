package tu.darmstadt.de.swc.kombi.community;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.graphstream.algorithm.community.DecentralizedCommunityAlgorithm;
import org.graphstream.algorithm.community.EpidemicCommunityAlgorithm;
import org.graphstream.algorithm.community.SyncEpidemicCommunityAlgorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import edu.ucla.sspace.clustering.Assignments;
import edu.ucla.sspace.clustering.DirectClustering;
import edu.ucla.sspace.clustering.HierarchicalAgglomerativeClustering;
import edu.ucla.sspace.matrix.ArrayMatrix;
import edu.ucla.sspace.matrix.Matrix;
import main.graph.louvain.ModularityOptimizer;

/**
 * Selection of Community Detection Algorithms 
 * 
 * @author suhas
 *
 */
public class CommunityDetectionAlgorithms {

	DecentralizedCommunityAlgorithm community;
	public static ArrayList<String> arrCommunities = new ArrayList<String>();
	public static ArrayList<String> arrColors = new ArrayList<String>();
	public static HashMap<String, String> colorCode = new HashMap<String, String>();
	public static HashMap<String, Integer> nodesInGraphCount = new HashMap<String, Integer>();
	public static Map<String, String> mapNodesToClusters = new HashMap<String, String>();
	String startNodeCommunity = "";

	public void clearAllValues() {
		arrCommunities.clear();
		arrColors.clear();
		colorCode.clear();
		nodesInGraphCount.clear();
		mapNodesToClusters.clear();
		startNodeCommunity = "";
	}

	/**
	 * Set the community of start node
	 * 
	 * @param graph Input graph
	 * @param startNodeId node-id of the start node
	 * @param communityAlgorithm Type of community algorithm
	 */

	public void setStartNodeCommunity(Graph graph, String startNodeId, String communityAlgorithm) {

		switch (communityAlgorithm.trim().toUpperCase()) {

		case "HIERARCHICAL CLUSTERING":
		case "ORIGINAL LOUVAIN":
		case "LOUVAIN WITH MULTILEVEL REFINEMENT":
		case "DIRECT CLUSTERING":
		//case "HC":
			startNodeCommunity = String.valueOf(mapNodesToClusters.get(startNodeId));
			break;
		default:
			break;

		}
	}

	/**
	 * Get start node community
	 * 
	 * @return Community of start node
	 */
	public String getStartNodeCommunity() {
		return startNodeCommunity;
	}

	/**
	 * Get community of each node
	 * 
	 * @param nodeId Node ID
	 * @param communityAlgorithm Type of Community algorithm
	 * @return Community of the node
	 */
	public String getNodeCommunity(String nodeId, String communityAlgorithm) {
		String comm = null;
		switch (communityAlgorithm.trim().toUpperCase()) {
		case "HIERARCHICAL CLUSTERING":
		case "ORIGINAL LOUVAIN":
		case "LOUVAIN WITH MULTILEVEL REFINEMENT":
		case "DIRECT CLUSTERING":
			comm = String.valueOf(mapNodesToClusters.get(nodeId));
			break;
		default:
			break;

		}
		return comm;
	}

	/**
	 * Validate whether node is included or not in the community
	 * 
	 * @param nodeId Node ID
	 * @param nodesPerCluster Number of nodes in a cluster
	 * @param communityAlgorithm Type of Community algorithm
	 * @return TRUE if node is included in the next level else FALSE
	 */
	public Boolean validateNodeInclusion(String nodeId, Integer nodesPerCluster, String communityAlgorithm) {
		String comm = null;
		switch (communityAlgorithm.trim().toUpperCase()) {
		case "HIERARCHICAL CLUSTERING":
		case "ORIGINAL LOUVAIN":
		case "LOUVAIN WITH MULTILEVEL REFINEMENT":
		case "DIRECT CLUSTERING":		
			comm = String.valueOf(mapNodesToClusters.get(nodeId));
			break;
		default:
			break;

		}

		if (!nodesInGraphCount.containsKey(comm)) {
			nodesInGraphCount.put(comm, 1);
			return true;
		} else {
			if (nodesInGraphCount.get(comm) < nodesPerCluster) {
				int val = nodesInGraphCount.get(comm);
				val = val + 1;
				nodesInGraphCount.put(comm, val);
				return true;
			} else {
				return false;
			}
		}

	}
	
	/** 
	 * Maps nodes to clusters
	 * 
	 * @param edges No of edges in the input graph
	 * @param adjacencyMatrix Adjacency matrix of the input graph
	 * @param level Reduction level
	 * @param algorithm Integer value assigned to the algorithm 
	 */
	public void applyLouvainOrSLM(int edges, int[][] adjacencyMatrix, int level, int algorithm) {
		
		try {
			// Fourth parameter - 1 for Louvain, 2 for Louvain with refinement, 3
			// for SLM
			// For more info check http://www.ludowaltman.nl/slm/
			mapNodesToClusters = ModularityOptimizer.modularityOptimizer(edges, adjacencyMatrix, algorithm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Apply Hierarchical clustering on adjacency matrix
	 * 
	 * @param adjacencyMatrix Adjacency matrix of the input graph
	 */
	public void applyHC(int[][] adjacencyMatrix) {

		HierarchicalAgglomerativeClustering hc = new HierarchicalAgglomerativeClustering();

		double[][] dMatrix = new double[adjacencyMatrix.length][adjacencyMatrix.length];
		int n = adjacencyMatrix.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				dMatrix[i][j] = (double) adjacencyMatrix[i][j];
			}
		}
		Properties props = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream("config.proprties");
			props.setProperty(HierarchicalAgglomerativeClustering.MIN_CLUSTER_SIMILARITY_PROPERTY, "0.8");
			// props.setProperty("MIN_CLUSTER_SIMILARITY_PROPERTY", "0.2");
			props.store(output, "Simple");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Matrix matrix = new ArrayMatrix(dMatrix);

		Assignments assignment = hc.cluster(matrix, props);
		List<Set<Integer>> clusters = assignment.clusters();
		for (int i = 0; i < clusters.size(); i++) {
			Set<Integer> set = clusters.get(i);
			@SuppressWarnings("rawtypes")
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				String value = String.valueOf(iter.next());
				mapNodesToClusters.put(value, String.valueOf(i));
			}
		}

	}

	/**
	 * Apply direct clustering
	 * 
	 * @param adjacencyMatrix Adjacency matrix of the input graph
	 */
	public void applyDirectClustering(int[][] adjacencyMatrix) {
		double[][] dMatrix = new double[adjacencyMatrix.length][adjacencyMatrix.length];
		int n = adjacencyMatrix.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				dMatrix[i][j] = (double) adjacencyMatrix[i][j];
			}
		}
		int numberOfClusters = adjacencyMatrix.length;
		DirectClustering dClustering = new DirectClustering();
		Properties props = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream("config.proprties");
			props.setProperty(DirectClustering.REPEAT_PROPERTY, "1");

			// props.setProperty("MIN_CLUSTER_SIMILARITY_PROPERTY", "0.2");
			props.store(output, "Simple");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Matrix matrix = new ArrayMatrix(dMatrix);

		Assignments assignment = dClustering.cluster(matrix, numberOfClusters, props);
		List<Set<Integer>> clusters = assignment.clusters();

		for (int i = 0; i < clusters.size(); i++) {
			Set<Integer> set = clusters.get(i);

			@SuppressWarnings("rawtypes")
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				String value = String.valueOf(iter.next());
				mapNodesToClusters.put(value, String.valueOf(i));
			}
		}

	}
	
	/**
	 * Get handler to Epidemic community
	 * @return Epidemic community handler
	 */

	public EpidemicCommunityAlgorithm getEpidemicCommunity() {
		return (EpidemicCommunityAlgorithm) community;
	}

	/**
	 * Get SynchronousEpidemic Community 
	 * @return Synchronous Epidemic community handler
	 */
	public SyncEpidemicCommunityAlgorithm getSyncEpidemicCommunity() {
		return (SyncEpidemicCommunityAlgorithm) community;
	}

	
	/**
	 * Store a color for each community
	 * 
	 * @param graph Input graph
	 * @param communityAlgorithm Type of community algorithm
	 */
	public void storeCommunityColors(Graph graph, String communityAlgorithm) {
		String comm;
		switch (communityAlgorithm.trim().toUpperCase()) {
		case "HIERARCHICAL CLUSTERING":
		case "ORIGINAL LOUVAIN":
		case "LOUVAIN WITH MULTILEVEL REFINEMENT":
		case "DIRECT CLUSTERING":
			for (String key : mapNodesToClusters.keySet()) {
				comm = String.valueOf(mapNodesToClusters.get(key));
				if (arrCommunities.contains(comm) == false) {
					arrCommunities.add(comm);
					nodesInGraphCount.put(comm, 0);
				}
			}

			Iterator<Integer> randomNumbers = ThreadLocalRandom.current().ints(0, graph.getNodeCount()).distinct()
					.limit(arrCommunities.size()).iterator();
			while (randomNumbers.hasNext()) {
				arrColors.add(Integer.toHexString(randomNumbers.next()));

			}
			System.out.println("Number of communities:" + arrCommunities.size());

			for (int i = 0; i < arrCommunities.size(); i++) {
				String strCommunity = arrCommunities.get(i);
				Double dCommunityVal = 0.0;
				dCommunityVal = Double.parseDouble(strCommunity) / 10;
				colorCode.put(arrCommunities.get(i).toString(), String.valueOf(dCommunityVal));

			}

			break;
		default:
			for (Node node : graph) {
				String val = String.valueOf(node.getAttribute(getEpidemicCommunity().getMarker()));
				if (arrCommunities.contains(val) == false) {
					arrCommunities.add(val);
					nodesInGraphCount.put(val, 0);
				}
			}
			System.out.println("No of Communities:" + arrCommunities.size());

			break;

		}

	}

	/**
	 * Reset all communities
	 */
	public void resetCommunities() {
		@SuppressWarnings("rawtypes")
		Iterator iter = nodesInGraphCount.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, Integer> mapValue = (Map.Entry<String, Integer>) iter.next();
			nodesInGraphCount.put(mapValue.getKey(), 0);
		}
	}

	/**
	 * Get the color code
	 * 
	 * @return Color code of each community
	 */
	public HashMap<String, String> getColorCode() {
		return colorCode;
	}

	/**
	 * Get the type of community
	 * 
	 * @return Community Handler
	 */

	public DecentralizedCommunityAlgorithm getCommunity() {
		return community;
	}

}
