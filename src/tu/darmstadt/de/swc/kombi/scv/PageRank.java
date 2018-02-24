package tu.darmstadt.de.swc.kombi.scv;

/**
 * Original author: Shravan Chitpady 
 * Adapted from
 * http://codispatch.blogspot.com/2015/12/java-program-implement-google-page-rank-algorithm.html
 * Accessed on November 22, 2016
 */

/**
 * Compute page rank
 * 
 * @author suhas
 *
 */
public class PageRank implements ISelectionCriteria {

	//public double pagerank[] = null;

	/*public PageRank(int n) {
		pagerank = new double[n];
	}*/

	/**
	 * Return page rank of each node
	 * 
	 * @param n Number of nodes
	 * @param path Path to destination node
	 * @return array containing page rank
	 */
	public static double[] compute(double n, int[][] path) {
		double init;
		double c = 0;
		n = path[0].length;
		double[] pagerank = new double[(int)n];
		double temp[] = new double[path.length];
		int i, j, u = 1, k = 1, iterations = 16;
		init = 1 / n;
		for (i = 0; i < n; i++)
			pagerank[i] = init;

		while (u <= iterations) {
			for (i = 0; i < n; i++) {
				temp[i] = pagerank[i];
				pagerank[i] = 0;
			}

			for (j = 0; j < n; j++)
				for (i = 0; i < n; i++)
					if (path[i][j] == 1) {
						k = 0;
						c = 0;
						while (k < n) {
							if (path[i][k] == 1)
								c = c + 1;
							k = k + 1;
						}
						pagerank[j] = pagerank[j] + temp[i] * (1 / c);
					}

			u = u + 1;
		}

		return pagerank;
	}
}
