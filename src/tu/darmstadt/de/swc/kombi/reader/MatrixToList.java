
package tu.darmstadt.de.swc.kombi.reader;

/**
 * Original author:Márton Balassi
 * Adapted from https://github.com/mbalassi/msc-thesis
 */
import java.io.IOException;

public class MatrixToList {
	/**
	 * Print the values associated with each node of the graph.
	 * 
	 * @param graph The graph
	 * @param values The value for each node
	 * 
	 * @return Array containing vertices
	 */
	public static double[] printNodeValues(GraphReader graph, double[] values) {
		double[] result = new double[values.length];
		for (int i = 0; i < values.length; ++i) {
			// System.out.println(Integer.toString(graph.originalIdOf(i)) + "\t"
			// + Double.toString(values[i]));
			result[i] = values[i];
		}
		return result;
	}

	/**
	 * Convert graph to string
	 * 
	 * @param adjacencyMatrix Instance of AdjacencyMatrix
	 * @return Graph Array
	 * @throws NumberFormatException Throws NumberFormatException
	 * @throws IOException Throws IOException
	 */
	public static int[][] constructGraphAsInputString(int[][] adjacencyMatrix)
			throws NumberFormatException, IOException {
		String graphInput = "";
		for (int i = 0; i < adjacencyMatrix[0].length; i++) {
			graphInput = graphInput + i;
			for (int j = 0; j < adjacencyMatrix[0].length; j++) {
				if (adjacencyMatrix[i][j] == 1) {
					graphInput = graphInput + " " + j;
				}
			}
			graphInput = graphInput + "\n";
		}

		GraphReader graph = new GraphReader(graphInput);
		int[][] graphArray = graph.getGraph();

		return graphArray;
	}

}
