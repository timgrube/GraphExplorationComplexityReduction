package tu.darmstadt.de.swc.kombi.generate;

import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.ViewerPipe;

/**
 * Separate graph rendering operation using this class
 * 
 * @author suhas
 *
 */
public class Task extends SwingWorker<Graph, Graph> {

	List<String> list;
	Boolean isDirected;
	Graph graph;
	ViewerPipe viewerPipe;

	public Task(List<String> list, Boolean isDirected, Graph graph, ViewerPipe viewerPipe) {
		this.list = list;
		this.isDirected = isDirected;
		this.graph = graph;
		this.viewerPipe = viewerPipe;
	}

	/**
	 * Perform graph generation on a background thread
	 * return instance of graph
	 */
	@Override
	public Graph doInBackground() throws Exception {
		// TODO Auto-generated method stub
		int count = 0;
		// for (int i = 0; i < list.size(); i++) {
		// System.out.println(fileContent.get(i));
		for (@SuppressWarnings("rawtypes")
		Iterator it = list.iterator(); it.hasNext();) {
			String line = (String) it.next();
			if ((!line.startsWith("#")) && (line.length() > 1)) {
				count++;
				String[] arrLine = line.trim().split("\\s+");
				String source = arrLine[0].trim();
				String destination = arrLine[1].trim();

				createNodeFromFile(source);
				createNodeFromFile(destination);
				createEdgeFromFile(source, destination, isDirected);
				publish(graph);
			}
			if (count % 1000 == 0) {
				System.out.println("Finished processing " + count + " lines");
			}
		}

		return graph;
	}

	/**
	 * Add graph to sink
	 */
	@Override
	protected void process(List<Graph> graphs) {
		GraphGenerate.ROOT_GRAPH = graphs.get(graphs.size() - 1);
		viewerPipe.addAttributeSink(GraphGenerate.ROOT_GRAPH);
	}

	/**
	 * Create node
	 * 
	 * @param nodeId Node ID
	 */
	public void createNodeFromFile(String nodeId) {
		if (graph.getNode(nodeId) == null) {
			graph.addNode(nodeId);
			showLabelOnRootGraph(nodeId, nodeId);

		}
	}

	/**
	 * Show label
	 * 
	 * @param nodeId Node ID
	 * @param label Label of node
	 */
	public void showLabelOnRootGraph(String nodeId, String label) {
		graph.getNode(nodeId).addAttribute("ui.label", label);
	}

	/**
	 * Create edge
	 * 
	 * @param source Source node
	 * @param destination Destination node
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	public void createEdgeFromFile(String source, String destination, Boolean isDirected) {
		if (graph.getNode(source).hasEdgeBetween(destination) == false) {
			graph.addEdge("S" + source + "D" + destination, source, destination, isDirected);
		}
	}

}
