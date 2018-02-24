package tu.darmstadt.de.swc.kombi.matrix;

import java.util.HashMap;
import java.util.List;

import tu.darmstadt.de.swc.kombi.storage.HashStorage;

/**
 * Operations on a matrix
 * 
 * @author suhas
 *
 */
public class MatrixOperations {

	public MatrixOperations() {

	}

	/**
	 * Get size of adjacency matrix
	 * 
	 * @param lines Number of lines in the file
	 * @return matrix size Size of matrix
	 */
	private int getAdjacencyMatrixSize(List<String> lines) {
		int max = 0;
		for (int i = 0; i < lines.size(); i++) {
			try{
				if(lines.get(0).startsWith("DGS")){
					if (lines.get(i).startsWith("an")){
						String[] arrLine = lines.get(i).split("\\s+");
						int nid = Integer.parseInt(arrLine[1].replace("\"", ""));
						if (nid > max) {
							max = nid;
						}
					}
				}else if (!lines.get(i).contains("#") && lines.get(i) != null && lines.get(i).length() > 0) {
					String[] arrLine = lines.get(i).split("\\s+");
					if (Integer.parseInt(arrLine[0]) > max) {
						max = Integer.parseInt(arrLine[0]);
					}
					if (Integer.parseInt(arrLine[1]) > max) {
						max = Integer.parseInt(arrLine[1]);
					}
				}
			} catch (Exception e){
				System.err.println("Inner Error in line: " + i + ":\t" + lines.get(i));
				e.printStackTrace();
				System.exit(1);
			}
		}
		return max + 1;
	}

	/**
	 * Create adjacency matrix
	 * 
	 * @param lines Number of lines in the input file
	 * @param isDirected TRUE if graph is directed else FALSE
	 * @param hashStorage Instance of HashStorage
	 */
	public void createAdjacencyMatrixFromFile(List<String> lines, Boolean isDirected, HashStorage hashStorage) {

		
			int n = getAdjacencyMatrixSize(lines);
			// System.out.println("n:"+n);
			int[][] adjacencyMatrix = new int[n][n];
			for (int i = 0; i < lines.size(); i++) {
				try{
					if (!lines.get(i).contains("#") && (!lines.get(i).contains("%")) && lines.get(i) != null
							&& lines.get(i).length() > 0 && !lines.get(0).startsWith("DGS")) {
						String[] arrLine = lines.get(i).split("\\s+");
						int src = Integer.parseInt(arrLine[0]);
						int destination = Integer.parseInt(arrLine[1]);
						adjacencyMatrix[src][destination] = 1;
						if (!isDirected) {
							adjacencyMatrix[destination][src] = 1;
	
						}
					} else if (lines.get(i).startsWith("ae")) {
						String[] arrLine = lines.get(i).split("\\s+");
						int src = Integer.parseInt(arrLine[2].replace("\"", ""));
						int destination = Integer.parseInt(arrLine[3].replace("\"", ""));
						adjacencyMatrix[src][destination] = 1;
						if (!isDirected) {
							adjacencyMatrix[destination][src] = 1;
						}
					} else if (lines.get(i).startsWith("de")) {
						String[] arrLine = lines.get(i).split("\\s+");
						int src = Integer.parseInt(arrLine[2].replace("\"", ""));
						int destination = Integer.parseInt(arrLine[3].replace("\"", ""));
						adjacencyMatrix[src][destination] = 0;
						if (!isDirected) {
							adjacencyMatrix[destination][src] = 0;
						}
					}
				} catch (Exception e){
					System.err.println("Outer Error in line " + i + ":\t" + lines.get(i));
					e.printStackTrace();
					System.exit(1);
				}
			}

			hashStorage.storeAdjacencyMatrix(0, adjacencyMatrix);
			HashMap<Integer, Integer> map = hashStorage.storeMatrixIndexAsNodeId(adjacencyMatrix);
			hashStorage.storeIterToNodeId(0, map);
		
		
	}

}
