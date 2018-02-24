package tu.darmstadt.de.swc.kombi.generate;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Graph;

/**
 * Compute shortest path
 * 
 * @author suhas
 *
 */
public class ShortestPathForMatrix {

	Dijkstra dijkstra;

	public ShortestPathForMatrix() {
	}

	public ShortestPathForMatrix(Graph graph, String source) {
		dijkstra = new Dijkstra(Dijkstra.Element.NODE, null, null);
		dijkstra.init(graph);
		dijkstra.setSource(graph.getNode(source));
		dijkstra.compute();
	}

	/**
	 * Return shortest path
	 * 
	 * @param graph Instance of graph
	 * @param destination Destination node
	 * @param source Source node
	 * @return path length
	 */
	public Integer calculateShortestPathDijkstra(Graph graph, String destination, String source) {
		return (int) dijkstra.getPathLength(graph.getNode(destination));
	}

}
