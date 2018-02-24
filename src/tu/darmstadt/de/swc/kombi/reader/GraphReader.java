
package tu.darmstadt.de.swc.kombi.reader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Original author:Márton Balassi 
 * Adapted from https://github.com/mbalassi/msc-thesis
 * Accessed on November 22, 2016.
 */

/**
 * Utility class for reading graphs from file.
 * 
 */
public class GraphReader {

	/**
	 * Creates a GraphReader object for the specified file. For a graph with n
	 * nodes, the input file should contain n lines. Each line should contain
	 * d+1 integers separated by whitespace, where d is the outdegree of the
	 * corresponding node. The first integer is the ID of the vertex, the rest
	 * are the IDs of the endpoints of the outgoing edges.
	 * 
	 * @param inputGraph the name of the input file
	 * 
	 * @exception NumberFormatException Throws NumberFormatException
	 * @exception IOException Throws IOException
	 */
	public GraphReader(String inputGraph) throws NumberFormatException, IOException {

		ArrayList<int[]> graph = new ArrayList<int[]>();
		int maxId = 0;

		String[] arrGraphNodes = inputGraph.split("\n");
		String line;
		// Read graph
		for (int k = 0; k < arrGraphNodes.length; k++) {
			if (arrGraphNodes[k].trim().length() > 0) {
				line = arrGraphNodes[k];
				String[] nums = line.split("\\s+");
				int[] edges = new int[nums.length - 1];
				for (int i = 0; i < edges.length; ++i) {
					edges[i] = Integer.parseInt(nums[i + 1]);
				}
				graph.add(edges);
				int id = Integer.parseInt(nums[0]);
				if (maxId < id)
					maxId = id;
			}
		}

		// create sequential ids & count edges
		n = graph.size();
		m = 0;
		int[] toInternalId = new int[maxId + 1];
		fromInternalId = new int[graph.size()];
		int internalId = 0;
		for (int k = 0; k < arrGraphNodes.length; k++) {
			if (arrGraphNodes[k].trim().length() > 0) {
				line = arrGraphNodes[k];
				String[] nums = line.split("\\s+", 2);
				int id = Integer.parseInt(nums[0]);
				toInternalId[id] = internalId;
				fromInternalId[internalId] = id;
				internalId++;
			}
		}

		for (int[] edges : graph) {
			m += edges.length;
			for (int i = 0; i < edges.length; ++i) {
				edges[i] = toInternalId[edges[i]];
			}
		}

		graphArray = new int[n][];
		graph.toArray(graphArray);
	}

	/**
	 * Returns the parsed graph in adjacency list representation. This stored
	 * graph is returned without cloning, so it should not be modified.
	 * 
	 * @return the parsed graph
	 */
	public int[][] getGraph() {
		return graphArray;
	}

	/**
	 * Get the number of nodes in the graph.
	 * 
	 * @return the number of nodes
	 */
	public int getVertexCount() {
		return n;

	}

	/**
	 * Get the number of edges in the graph.
	 * 
	 * @return the number of edges
	 */
	public int getEdgeCount() {
		return m;
	}

	/**
	 * Get the original ID of the node at the specified index in the adjacency
	 * list representation.
	 * 
	 * @param index The index of a node in the adjacency list
	 * @return the original ID (in the parsed file) of the node
	 */
	public int originalIdOf(int index) {
		return fromInternalId[index];
	}

	// graph structure
	private int[][] graphArray;
	// number of nodes
	private int n;
	// number of edges
	private int m;
	// fromInternalId[i] is the original ID of the node with internal ID i
	private int[] fromInternalId;

}