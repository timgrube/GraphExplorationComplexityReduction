package tu.darmstadt.de.swc.kombi.results;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import tu.darmstadt.de.swc.kombi.generate.GraphGenerate;
import tu.darmstadt.de.swc.kombi.scv.BetweennessCentrality;
import tu.darmstadt.de.swc.kombi.scv.ClosenessCentrality;
import tu.darmstadt.de.swc.kombi.scv.ClusteringCoefficient;
import tu.darmstadt.de.swc.kombi.scv.DegreeCentrality;
import tu.darmstadt.de.swc.kombi.scv.PageRank;
import tu.darmstadt.de.swc.kombi.storage.HashStorage;

/**
 * Stores values of all properties
 * 
 * @author suhas
 *
 */
public class ResultsStore {

	// List of nodes
	List<String> arrNodes = new ArrayList<String>();
	// Betweenness
	HashMap<Integer, Double> bcMap = new HashMap<Integer, Double>();
	// PageRank
	HashMap<Integer, Double> prMap = new HashMap<Integer, Double>();
	// Clustering Coefficient
	HashMap<Integer, Double> cCoeffMap = new HashMap<Integer, Double>();
	// Degree
	HashMap<Integer, Integer> dcMap = new HashMap<Integer, Integer>();
	// Closeness
	HashMap<Integer, Double> ccMap = new HashMap<Integer, Double>();

	// File
	private String filename;

	public ResultsStore() {

	}

	/**
	 * Get result file name
	 * 
	 * @param startNode Node ID of start node
	 * @return filename Path to filename
	 */
	public String getFileName(String startNode) {
		String sFileName = null;
		Date dateNow = new Date();
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");

		sFileName = formatDate.format(dateNow).toString();
		sFileName = "StartNode-" + startNode + "-" + sFileName + ".txt";
		filename = sFileName;
		return filename;
	}

	/**
	 * Get file
	 * 
	 * @return file
	 */
	public String getFile() {
		return filename;
	}

	/**
	 * Setup all parameters
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param selectedProperty Type of selected property
	 * @param startNode Node ID of start node
	 * @param isDirected TRUE if graph is directed else FALSE
	 * @throws NumberFormatException Throws NumberFormatException
	 * @throws IOException Throws IOException
	 */
	public void setupDataForResults1(HashStorage hashStorage, String selectedProperty, String startNode,
			Boolean isDirected) throws NumberFormatException, IOException {
		System.out.println("Computing results...");

		System.out.println("Capturing nodes...");
		// Store node-id
		for (Node node : GraphGenerate.GRAPHS.get(GraphGenerate.GRAPHS.size() - 1)) {
			arrNodes.add(node.getId());
		}

		System.out.println("Capturing betweenness...");
		// Store betweenness
		if (selectedProperty.trim().toUpperCase().contains("BETWEENNESS CENTRALITY")) {
			for (String node : arrNodes) {
				bcMap.put(Integer.parseInt(node), hashStorage.getNodeBetweenness(0, Integer.parseInt(node)));
			}

		} else {
			int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(0);
			double[] bc = BetweennessCentrality.compute(adjacencyMatrix.length, adjacencyMatrix);
			for (String node : arrNodes) {
				bcMap.put(Integer.parseInt(node), bc[Integer.parseInt(node)]);
			}
		}

		System.out.println("Capturing pagerank...");
		// Store PageRank
		if (selectedProperty.trim().toUpperCase().contains("PAGERANK")) {
			for (String node : arrNodes) {
				prMap.put(Integer.parseInt(node), hashStorage.getPageRank(0, Integer.parseInt(node)));
			}

		} else {
			int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(0);
			//PageRank pageRank = new PageRank(adjacencyMatrix[0].length);
			//double[] pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
			//pageRank = new PageRank(adjacencyMatrix[0].length);
			//pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
			double[] pr = PageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
			
			//double[] pr = PageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
			
			for (String node : arrNodes) {
				prMap.put(Integer.parseInt(node), pr[Integer.parseInt(node)]);
			}
		}

		System.out.println("Capturing clustering coeffcient...");
		// Store Clustering Coefficient
		if (selectedProperty.trim().toUpperCase().contains("CLUSTERING COEFFICIENT")) {
			for (String node : arrNodes) {
				cCoeffMap.put(Integer.parseInt(node), hashStorage.getClusteringCoefficient(0, Integer.parseInt(node)));
			}

		} else {
			int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(0);
			double[] ccoeff = ClusteringCoefficient.compute(adjacencyMatrix, isDirected);
			for (String node : arrNodes) {
				cCoeffMap.put(Integer.parseInt(node), ccoeff[Integer.parseInt(node)]);
			}

		}

		System.out.println("Capturing degree centrality...");
		// Store Degree Centrality
		if (selectedProperty.trim().toUpperCase().contains("DEGREE CENTRALITY")) {
			for (String node : arrNodes) {
				dcMap.put(Integer.parseInt(node), hashStorage.getDegree(0, Integer.parseInt(node)));
			}

		} else {
			int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(0);
			for (String node : arrNodes) {
				dcMap.put(Integer.parseInt(node), DegreeCentrality.getDegree(adjacencyMatrix, Integer.parseInt(node)));
			}

		}

		System.out.println("Capturing closeness centrality...");
		// Store Closeness Centrality
		if (selectedProperty.trim().toUpperCase().contains("CLOSENESS CENTRALITY")) {
			for (String node : arrNodes) {
				ccMap.put(Integer.parseInt(node), hashStorage.getNodeCloseness(0, Integer.parseInt(node)));
			}

		} else {
			int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(0);
			double[] cc = ClosenessCentrality.compute(adjacencyMatrix, adjacencyMatrix.length);
			for (String node : arrNodes) {
				ccMap.put(Integer.parseInt(node), cc[Integer.parseInt(node)]);
			}

		}
	}

	/**
	 * Store edge list
	 * 
	 * @param graph Instance of graph
	 * @param screenshotFilename Path to result file name
	 * @throws FileNotFoundException Throws FileNotFoundException
	 */
	public void createResultsScreenshot(Graph graph, String screenshotFilename) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(screenshotFilename);
		Collection<Edge> edgelist = graph.getEdgeSet();
		@SuppressWarnings("rawtypes")
		Iterator iter = edgelist.iterator();
		
		while (iter.hasNext()) {
			Edge edge = (Edge) iter.next();
			pw.println(edge.getNode0() + " " + edge.getNode1());
			
		}
		pw.close();
	}

	/**
	 * Setup parameters
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param selectedProperty Graph-Theoretic Property or Centrality
	 * @param startNode Node ID of start node
	 * @param isDirected TRUE if graph is directed else FALSE
	 * @throws NumberFormatException Throws NumberFormatException
	 * @throws IOException Throws IOException
	 */

	public void setupDataForResults(HashStorage hashStorage, String selectedProperty, String startNode,
			Boolean isDirected) throws NumberFormatException, IOException {

		System.out.println("Computing results...");

		System.out.println("Capturing nodes...");
		// Store node-id
		for (Node node : GraphGenerate.GRAPHS.get(GraphGenerate.GRAPHS.size() - 1)) {
			arrNodes.add(node.getId());
		}

		System.out.println("Capturing betweenness...");
		// BC
		for (String node : arrNodes) {
			bcMap.put(Integer.parseInt(node), hashStorage.getNodeBetweenness(0, Integer.parseInt(node)));
		}

		System.out.println("Capturing pagerank...");
		// PR
		for (String node : arrNodes) {
			prMap.put(Integer.parseInt(node), hashStorage.getPageRank(0, Integer.parseInt(node)));
		}

		System.out.println("Capturing clustering coefficient...");
		// CLCO
		for (String node : arrNodes) {
			cCoeffMap.put(Integer.parseInt(node), hashStorage.getClusteringCoefficient(0, Integer.parseInt(node)));
		}

		System.out.println("Capturing degree centrality...");
		// DC
		for (String node : arrNodes) {
			dcMap.put(Integer.parseInt(node), hashStorage.getDegree(0, Integer.parseInt(node)));
		}

		System.out.println("Capturing closeness...");
		// CC
		for (String node : arrNodes) {
			ccMap.put(Integer.parseInt(node), hashStorage.getNodeCloseness(0, Integer.parseInt(node)));
		}

	}

	/**
	 * Setup parameters
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param selectedProperty Graph-Theoretic Property or Centrality
	 * @param startNode Node ID of start node
	 * @param isDirected TRUE if graph is directed else FALSE
	 * @param graphGenerateCommandline Instance of GraphGenerate if executed via command line
	 * @throws NumberFormatException Throws NumberFormatException
	 * @throws IOException Throws IOException
	 */
	public void setupDataForResultsCommandline(HashStorage hashStorage, String selectedProperty, String startNode,
			Boolean isDirected, GraphGenerate graphGenerateCommandline) throws NumberFormatException, IOException {

		System.out.println("Computing results...");

		System.out.println("Capturing nodes...");
		// Store node-id
		for (Node node : graphGenerateCommandline.getGraphs().get(graphGenerateCommandline.getGraphs().size() - 1)) {
			arrNodes.add(node.getId());
		}

		System.out.println("Capturing betweenness...");
		// BC
		for (String node : arrNodes) {
			bcMap.put(Integer.parseInt(node), hashStorage.getNodeBetweenness(0, Integer.parseInt(node)));
		}

		System.out.println("Capturing pagerank...");
		// PR
		for (String node : arrNodes) {
			prMap.put(Integer.parseInt(node), hashStorage.getPageRank(0, Integer.parseInt(node)));
		}

		System.out.println("Capturing clustering coefficient...");
		// CLCO
		for (String node : arrNodes) {
			cCoeffMap.put(Integer.parseInt(node), hashStorage.getClusteringCoefficient(0, Integer.parseInt(node)));
		}

		System.out.println("Capturing degree centrality...");
		// DC
		for (String node : arrNodes) {
			dcMap.put(Integer.parseInt(node), hashStorage.getDegree(0, Integer.parseInt(node)));
		}

		System.out.println("Capturing closeness...");
		// CC
		for (String node : arrNodes) {
			ccMap.put(Integer.parseInt(node), hashStorage.getNodeCloseness(0, Integer.parseInt(node)));
		}

	}

	/**
	 * Get nodes
	 * 
	 * @return nodes
	 */
	public List<String> getArrNodes() {
		return arrNodes;
	}

	/**
	 * Get Betweenness
	 * 
	 * @return betweenness map
	 */
	public HashMap<Integer, Double> getBcMap() {
		return bcMap;
	}

	/**
	 * Get Page Rank
	 * 
	 * @return pagerank map
	 */
	public HashMap<Integer, Double> getPrMap() {
		return prMap;
	}

	/**
	 * Get Clustering coefficient
	 * 
	 * @return clustering coefficient map
	 */
	public HashMap<Integer, Double> getcCoeffMap() {
		return cCoeffMap;
	}

	/**
	 * Get degree
	 * 
	 * @return degree map
	 */
	public HashMap<Integer, Integer> getDcMap() {
		return dcMap;
	}

	/**
	 * Get Closeness
	 * 
	 * @return closeness map
	 */
	public HashMap<Integer, Double> getCcMap() {
		return ccMap;
	}
}
