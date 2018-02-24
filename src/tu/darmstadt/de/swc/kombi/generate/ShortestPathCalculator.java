package tu.darmstadt.de.swc.kombi.generate;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Path;

/**
 * Shortest path from graph-stream
 *
 */
public class ShortestPathCalculator {

	Dijkstra dijkstra;

	public ShortestPathCalculator(String source, int level) {

		dijkstra = new Dijkstra(Dijkstra.Element.NODE, null, null);
		dijkstra.init(GraphGenerate.GRAPHS.get(level));
		try {
			dijkstra.setSource(GraphGenerate.GRAPHS.get(level).getNode(source));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			dijkstra.compute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Return path
	 * 
	 * @param level Reduction level
	 * @param destination Destination Node
	 * @param source Source node
	 * @return path
	 */
	public Path calculateShortestPathDijkstra(int level, String destination, String source) {
		return dijkstra.getPath(GraphGenerate.GRAPHS.get(level).getNode(destination));
	}

	/**
	 * Length of shortest path
	 * 
	 * @param level Reduction level
	 * @param destination Destination Node
	 * @return path length
	 */
	public Double getLengthOfShortestPath(int level, String destination) {
		// dijkstra.getPathLength(GraphGenerate.GRAPHS.get(level).getNode(destination))
		// gives
		// No of edges + 1 node as the path length. So subtract 1 from the
		// answer
		return (dijkstra.getPathLength(GraphGenerate.GRAPHS.get(level).getNode(destination)) - 1);
	}
}
