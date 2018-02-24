package tu.darmstadt.de.swc.kombi.generate;

import java.util.HashMap;
import java.util.Map;

import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.algorithm.PageRank;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;

/**
 * Calculate scalar values of graph properties
 * 
 * @author suhas
 *
 */
public class FactorsCalculation {

	PageRank pageRankRootGraph;
	BetweennessCentrality betweenness;

	/**
	 * Calculate page rank
	 * 
	 * @param level Reduction level
	 */
	public void calculatePageRankRootGraph(int level) {
		pageRankRootGraph = new PageRank();
		pageRankRootGraph.setVerbose(false);
		pageRankRootGraph.init(GraphGenerate.GRAPHS.get(level));
	}

	/**
	 * Get page rank instance
	 * 
	 * @return page rank instance
	 */
	public PageRank getPageRankRootGraph() {
		if (pageRankRootGraph != null) {
			return pageRankRootGraph;
		}
		return null;
	}

	/**
	 * Calculate Betweenness Centrality
	 */
	public void calculateBetweennessCentrality() {
		betweenness = new BetweennessCentrality();
		betweenness.computeEdgeCentrality(false);
		if (GraphGenerate.CURRENT_ITERATION == 0) {
			betweenness.betweennessCentrality(GraphGenerate.ROOT_GRAPH);
		} else {
			betweenness.betweennessCentrality(GraphGenerate.TEMP_GRAPH);
		}
	}

	/**
	 * Calculate betweenness centrality of a level
	 * 
	 * @param level Reduction level
	 */
	public void calculateBetweennessCentrality(int level) {
		betweenness = new BetweennessCentrality();
		betweenness.computeEdgeCentrality(false);
		betweenness.betweennessCentrality(GraphGenerate.GRAPHS.get(level));
	}

	/**
	 * Get betweenness of a node
	 * 
	 * @param nodeId Node ID
	 * @return Betweenness centrality of the node
	 */
	public double getNodeBetweenness(String nodeId) {
		if (GraphGenerate.CURRENT_ITERATION == 0) {
			return (double) GraphGenerate.ROOT_GRAPH.getNode(nodeId).getAttribute("Cb");
		}
		return (double) GraphGenerate.TEMP_GRAPH.getNode(nodeId).getAttribute("Cb");
	}

	/**
	 * Get betweenness of a node at a particular level
	 * 
	 * @param nodeId Node ID
	 * @param level Reduction level
	 * @return Betweenness centrality of a node
	 */
	public double getNodeBetweenness(String nodeId, int level) {
		if (GraphGenerate.CURRENT_ITERATION == 0) {
			return (double) GraphGenerate.ROOT_GRAPH.getNode(nodeId).getAttribute("Cb");
		}
		return (double) GraphGenerate.GRAPHS.get(level).getNode(nodeId).getAttribute("Cb");
	}

	/**
	 * Get clustering coefficient
	 * 
	 * @param nodeId Node ID
	 * @param level Reduction level
	 * @return Clustering coefficient of a node
	 */
	public double getClusteringCoefficient(String nodeId, int level) {
		return Toolkit.clusteringCoefficient(GraphGenerate.GRAPHS.get(level).getNode(nodeId));
	}

	/**
	 * Get distance between nodes
	 * 
	 * @param source Source node
	 * @param level Reduction level
	 * @return HashMap containing nodes and their distances from the source node
	 */

	public HashMap<String, Double> getNodeDistances(String source, int level) {
		Map<String, Double> map = new HashMap<String, Double>();
		ShortestPathCalculator shortestPath = new ShortestPathCalculator(source, level);
		for (Node node : GraphGenerate.GRAPHS.get(level)) {
			if (!node.getId().equals(source)) {
				map.put(node.getId(), shortestPath.getLengthOfShortestPath(level, node.getId()));
			}
		}
		return (HashMap<String, Double>) map;
	}

	/**
	 * Compare value of src and dest
	 * 
	 * @param source Source node value
	 * @param destination Destination node value
	 * @return Greatest value
	 */
	public double compareValues(double source, double destination) {
		return (destination > source) ? destination : source;
	}
}
