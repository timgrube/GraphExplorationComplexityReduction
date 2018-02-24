package tu.darmstadt.de.swc.kombi.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import tu.darmstadt.de.swc.kombi.community.CommunityDetectionAlgorithms;

/**
 * This class deals with css of all graphs
 * 
 * @author suhas
 *
 */
public class GraphUIAttributes {

	String styleSheetPath = "style.css";

	/**
	 * Change the attributes of nodes in graphs.
	 * 
	 * @param graph Instance of graph
	 * @param communityDetection Instance of CommunityDetectionAlgorithms
	 * @param communityAlgorithm Name of community algorithm
	 */
	public void changeNodeAttributes(Graph graph, CommunityDetectionAlgorithms communityDetection,
			String communityAlgorithm) {
		@SuppressWarnings("unused")
		List<String> list = new ArrayList<String>();
		@SuppressWarnings("unused")
		ClassLoader classLoader = this.getClass().getClassLoader();
		URL url = GraphUIAttributes.class.getResource(styleSheetPath);

		switch (communityAlgorithm.trim().toUpperCase()) {
		case "HIERARCHICAL CLUSTERING":
		case "ORIGINAL LOUVAIN":
		case "LOUVAIN WITH MULTILEVEL REFINEMENT":
		case "SMART LOCAL MOVING":
			try {
				// InputStream in =
				// getClass().getResourceAsStream("/resources/style.css");

				graph.addAttribute("ui.stylesheet", "url('" + url + "')");
				for (Node node : graph) {
					String val = CommunityDetectionAlgorithms.mapNodesToClusters.get(node.getId());
					int length = val.length();
					node.setAttribute("ui.color", Double.parseDouble(val) / Math.pow(10, length));
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			break;

		default:
			graph.addAttribute("ui.stylesheet", "url('" + url + "')");
			for (Node node : graph) {
				String val = String.valueOf(node.getAttribute(communityDetection.getEpidemicCommunity().getMarker()));
				int length = val.length();
				node.setAttribute("ui.color", Double.parseDouble(val) / Math.pow(10, length));
			}
			break;

		}

	}

}
