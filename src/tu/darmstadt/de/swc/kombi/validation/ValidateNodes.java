package tu.darmstadt.de.swc.kombi.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import tu.darmstadt.de.swc.kombi.generate.FactorsCalculation;
import tu.darmstadt.de.swc.kombi.generate.GraphGenerate;
import tu.darmstadt.de.swc.kombi.generate.ShortestPathCalculator;

/**
 * Class dealing with values of resultant nodes
 * 
 * @author suhas
 *
 */
public class ValidateNodes {

	FactorsCalculation factorsCalculation = new FactorsCalculation();

	public ValidateNodes() {

	}

	/**
	 * Get Resultant In degree
	 * 
	 * @param level Reduction level
	 * @param graph Instance of Graph
	 * @param startNode Node ID of start node
	 * @return map containing nodes and degree
	 */
	public TreeMap<Integer, Integer> getResultantInDegree(int level, Graph graph, String startNode) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Node node : graph) {
			if (!node.getId().equals(startNode))
				map.put(Integer.parseInt(node.getId()),
						GraphGenerate.GRAPHS.get(level).getNode(node.getId()).getInDegree());
		}
		Map<Integer, Integer> treeMap = new TreeMap<Integer, Integer>(map);
		return (TreeMap<Integer, Integer>) treeMap;

	}

	/**
	 * Get Resultant out degree
	 * 
	 * @param level Reduction level
	 * @param graph Instance of Graph
	 * @param startNode Node ID of start node
	 * @return map containing nodes and degree
	 */
	public TreeMap<Integer, Integer> getResultantOutDegree(int level, Graph graph, String startNode) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Node node : graph) {
			if (!node.getId().equals(startNode))
				map.put(Integer.parseInt(node.getId()),
						GraphGenerate.GRAPHS.get(level).getNode(node.getId()).getOutDegree());
		}

		Map<Integer, Integer> treeMap = new TreeMap<Integer, Integer>(map);
		return (TreeMap<Integer, Integer>) treeMap;

	}

	/**
	 * Get betweenness
	 * 
	 * @param level Reduction level
	 * @param graph Instance of Graph
	 * @param startNode Node ID of start node
	 * @return map containing nodes and betweenness
	 */
	public TreeMap<Integer, Double> getResultantBetweenness(int level, Graph graph, String startNode) {
		factorsCalculation.calculateBetweennessCentrality(level);
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for (Node node : graph) {
			if (!node.getId().equals(startNode))
				map.put(Integer.parseInt(node.getId()), factorsCalculation.getNodeBetweenness(node.getId(), 0));
		}

		Map<Integer, Double> treeMap = new TreeMap<Integer, Double>(map);
		return (TreeMap<Integer, Double>) treeMap;

	}

	/**
	 * Get Page Rank
	 * 
	 * @param level Reduction level
	 * @param graph Instance of Graph
	 * @param startNode Node ID of start node
	 * @return map containing nodes and page rank
	 */
	public TreeMap<Integer, Double> getResultantPageRank(int level, Graph graph, String startNode) {
		factorsCalculation.calculatePageRankRootGraph(level);
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for (Node node : graph) {
			if (!node.getId().equals(startNode)) {
				map.put(Integer.parseInt(node.getId()), factorsCalculation.getPageRankRootGraph()
						.getRank(GraphGenerate.GRAPHS.get(level).getNode(node.getId())));
			}
		}
		Map<Integer, Double> treeMap = new TreeMap<Integer, Double>(map);
		return (TreeMap<Integer, Double>) treeMap;

	}

	/**
	 * Get distance
	 * 
	 * @param level Reduction level
	 * @param graph Instance of Graph
	 * @param startNode Node ID of start node
	 * @return map containing nodes and distance
	 */
	public TreeMap<Integer, Double> getResultantDistance(int level, Graph graph, String startNode) {
		// Since we would need the distance from the root graph in order to tell
		// the what kind of nodes are present in the resultant graph, level = 0
		ShortestPathCalculator shortestPath = new ShortestPathCalculator(startNode, 0);
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for (Node node : graph) {
			if (!node.getId().equals(startNode)) {
				Double pathLength = shortestPath.getLengthOfShortestPath(level, node.getId());
				map.put(Integer.parseInt(node.getId()), pathLength);
			}

		}
		Map<Integer, Double> treeMap = new TreeMap<Integer, Double>(map);
		return (TreeMap<Integer, Double>) treeMap;
	}

}
