package tu.darmstadt.de.swc.kombi.generate;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import com.gml.reader.ReadFile;

import tu.darmstadt.de.swc.kombi.community.CommunityDetectionAlgorithms;
import tu.darmstadt.de.swc.kombi.matrix.AdjacencyMatrix;
import tu.darmstadt.de.swc.kombi.matrix.MatrixOperations;
import tu.darmstadt.de.swc.kombi.scv.BetweennessCentrality;
import tu.darmstadt.de.swc.kombi.scv.ClosenessCentrality;
import tu.darmstadt.de.swc.kombi.scv.ClusteringCoefficient;
import tu.darmstadt.de.swc.kombi.scv.DegreeCentrality;
import tu.darmstadt.de.swc.kombi.scv.PageRank;
import tu.darmstadt.de.swc.kombi.shortestpath.DijkstraAlgorithm;
import tu.darmstadt.de.swc.kombi.storage.HashStorage;

/**
 * Class deals with reduced graph generation
 * 
 * @author suhas
 *
 */
public class GraphGenerate {

	public int noOfReductions = 0;

	static int nodesPerCluster = 1;
	protected static Graph ROOT_GRAPH;
	protected static Graph INTERMEDIATE_GRAPH;
	protected static Graph FINAL_GRAPH;
	protected static Graph TEMP_GRAPH;
	protected static ArrayList<String> ROOT_GRAPH_SELECTED_NODES = new ArrayList<String>();
	public static int CURRENT_ITERATION = 0;
	@SuppressWarnings("unused")
	private Boolean commandline = false;

	public static ArrayList<Graph> GRAPHS = new ArrayList<Graph>();
	private ArrayList<Graph> graphsCommandline = new ArrayList<Graph>();
	protected Graph previous_level_graph;

	private Task task;

	Viewer viewer;
	Viewer intermediateViewer;
	ViewerPipe fromViewer;
	public Viewer[] arrViewers = new Viewer[noOfReductions + 1];

	protected boolean loop = true;

	FactorsCalculation factorsCalculation = new FactorsCalculation();

	public GraphGenerate() {
		CURRENT_ITERATION = 0;
		ROOT_GRAPH = new SingleGraph("Root Graph");

		GRAPHS.add(CURRENT_ITERATION, ROOT_GRAPH);
		ROOT_GRAPH.setAutoCreate(true);

		arrViewers[0] = new Viewer(ROOT_GRAPH, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);

	}

	public GraphGenerate(Boolean commandline) {
		this.commandline = commandline;
		graphsCommandline.add(0, new SingleGraph("Root Graph"));
	}

	/**
	 * Get list of graphs
	 * 
	 * @return graphs Returns all the reduced graphs
	 */
	public ArrayList<Graph> getGraphs() {
		return graphsCommandline;
	}

	/**
	 * Intermediate viewer handler
	 */
	public void intermediateGraphDisplay() {
		intermediateViewer = null;
		intermediateViewer = GRAPHS.get(CURRENT_ITERATION).display();

	}

	/**
	 * Resetting viewer to null
	 * 
	 * @param level Reduction level
	 */
	public void intermediateGraphDisplay(int level) {
		intermediateViewer = null;
	}

	/**
	 * Get viewer handler
	 * 
	 * @return Viewer handler
	 */
	public Viewer getViewer() {
		return viewer;
	}

	/**
	 * Add styling to graphs
	 */
	public void addStyling() {
		ROOT_GRAPH.addAttribute("ui.antialias");
		ROOT_GRAPH.addAttribute("ui.stylesheet",
				"node{" + "fill-color:red;" + "size:6px;}" + "edge{" + "fill-color:black;" + "size:0.3px;" + "}");

	}

	/**
	 * Call reduction operations
	 * 
	 * @param startNodeId Node ID of start node
	 * @param clustering TRUE if clustering is applied else FALSE
	 * @param booleanIncludeNativeCommunity TRUE if start node's community is included else FALSE
	 * @param communityDetection Instance of Community Detection
	 * @param communityAlgorithm Name of Community Algorithm
	 * @param selectedProperty Graph-Theoretic Property Or Centrality
	 * @param hashStorage Instance of HashStorage
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	public void invokeReductionCalls(String startNodeId, Boolean clustering, Boolean booleanIncludeNativeCommunity,
			CommunityDetectionAlgorithms communityDetection, String communityAlgorithm, String selectedProperty,
			HashStorage hashStorage, Boolean isDirected) {

		try {

			for (int i = 1; i <= noOfReductions; i++) {
				if (i > 1) {
					clustering = false;
				}

				if (hashStorage.getAdjacencyMatrix(hashStorage.getCurrentLevel()) != null) {
					if (hashStorage.getAdjacencyMatrix(hashStorage.getCurrentLevel()).length > 0) {
						int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(hashStorage.getCurrentLevel());
						Integer indexOfStartNode = -1;
						if (hashStorage.getCurrentLevel() == 0) {
							indexOfStartNode = Integer.parseInt(startNodeId);
						} else {
							indexOfStartNode = 0;
						}

						applyReductions(hashStorage.getCurrentLevel(), adjacencyMatrix, hashStorage, clustering,
								communityDetection, communityAlgorithm, selectedProperty, indexOfStartNode, isDirected,
								startNodeId);

					}
				} else {
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Begin reduction operations
	 * 
	 * @param level Reduction level
	 * @param adjacencyMatrix Adjacency matrix of the graph
	 * @param hashStorage Instance of HashStorage
	 * @param clustering TRUE if clustering is used else FALSE
	 * @param communityDetection Instance of Community Detection
	 * @param communityAlgorithm Type of community algorithm
	 * @param selectedProperty Graph-Theoretic Property Or Centrality
	 * @param indexOfStartNode Index of start node in Adjacency Matrix
	 * @param isDirected TRUE if graph is directed else FALSE
	 * @param startNodeId Node ID of start node
	 * @throws NumberFormatException Throws number format exception
	 * @throws IOException Throws IOException
	 */
	public void applyReductions(int level, int[][] adjacencyMatrix, HashStorage hashStorage, Boolean clustering,
			CommunityDetectionAlgorithms communityDetection, String communityAlgorithm, String selectedProperty,
			Integer indexOfStartNode, Boolean isDirected, String startNodeId)
			throws NumberFormatException, IOException {

		double[] bc = null;
		double[] pr = null;
		double[] ccoeff = null;
		int[] dc = null;
		double[] cc = null;

		HashMap<Integer, Double> bcMap;
		HashMap<Integer, Double> prMap;
		HashMap<Integer, Integer> dcMap;
		HashMap<Integer, Double> ccoeffMap;
		HashMap<Integer, Double> ccMap;

		PageRank pageRank = null;
		@SuppressWarnings("unused")
		int[][] adjacencyList = null;

		System.out.println("Applying " + selectedProperty.trim().toUpperCase());

		if (level == 0 && hashStorage.getHashAdjacencyMatrix().size() == 1
				&& hashStorage.getHashBetweenness().size() == 0 && hashStorage.getHashCloseness().size() == 0
				&& hashStorage.getHashClusteringCoefficient().size() == 0 && hashStorage.getHashDegree().size() == 0
				&& hashStorage.getHashPageRank().size() == 0) {

			System.out.println("Setting up parameters...");
			// Compute BC
			bc = BetweennessCentrality.compute(adjacencyMatrix.length, adjacencyMatrix);
			bcMap = hashStorage.setupBetweennessFormat(bc);
			hashStorage.storeNodeBetweenness(level, bcMap);

			// Compute PR
			//pageRank = new PageRank(adjacencyMatrix[0].length);
			//pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
			pr = PageRank.compute((double)adjacencyMatrix[0].length, adjacencyMatrix);
			
			prMap = hashStorage.setupPRFormat(pr);
			hashStorage.storePageRank(level, prMap);

			// Compute CL-CO
			ccoeff = ClusteringCoefficient.compute(adjacencyMatrix, isDirected);
			ccoeffMap = hashStorage.setupCCoeffFormat(ccoeff);
			hashStorage.storeClusteringCoefficient(level, ccoeffMap);

			// Compute DC
			dc = DegreeCentrality.compute(adjacencyMatrix);
			dcMap = hashStorage.setupDCFormat(dc);
			hashStorage.storeNodeDegree(level, dcMap);

			// Compute CC
			cc = ClosenessCentrality.compute(adjacencyMatrix, adjacencyMatrix.length);
			ccMap = hashStorage.setupCCFormat(cc);
			hashStorage.storeClosenessCentrality(level, ccMap);

		} else {
			switch (selectedProperty.trim().toUpperCase()) {

			case "BETWEENNESS CENTRALITY":

				bc = BetweennessCentrality.compute(adjacencyMatrix.length, adjacencyMatrix);
				bcMap = hashStorage.setupBetweennessFormat(bc);
				hashStorage.storeNodeBetweenness(level, bcMap);

				break;

			case "PAGERANK":

				//pageRank = new PageRank(adjacencyMatrix[0].length);
				//pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
				
				//pageRank = new PageRank(adjacencyMatrix[0].length);
				//pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
				pr = PageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
				
				prMap = hashStorage.setupPRFormat(pr);
				hashStorage.storePageRank(level, prMap);

				break;

			case "PAGERANK+BETWEENNESS CENTRALITY":

				//pageRank = new PageRank(adjacencyMatrix[0].length);
				//pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
				
				//pageRank = new PageRank(adjacencyMatrix[0].length);
				//pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
				pr = PageRank.compute((double)adjacencyMatrix[0].length, adjacencyMatrix);
			
				prMap = hashStorage.setupPRFormat(pr);
				hashStorage.storePageRank(level, prMap);

				bc = BetweennessCentrality.compute(adjacencyMatrix.length, adjacencyMatrix);
				bcMap = hashStorage.setupBetweennessFormat(bc);
				hashStorage.storeNodeBetweenness(level, bcMap);

				break;

			case "CLUSTERING COEFFICIENT":
				ccoeff = ClusteringCoefficient.compute(adjacencyMatrix, isDirected);
				ccoeffMap = hashStorage.setupCCoeffFormat(ccoeff);
				hashStorage.storeClusteringCoefficient(level, ccoeffMap);

				break;

			case "DEGREE CENTRALITY":
				dc = DegreeCentrality.compute(adjacencyMatrix);
				dcMap = hashStorage.setupDCFormat(dc);
				hashStorage.storeNodeDegree(level, dcMap);

				break;

			case "CLOSENESS CENTRALITY":
				cc = ClosenessCentrality.compute(adjacencyMatrix, adjacencyMatrix.length);
				ccMap = hashStorage.setupCCFormat(cc);
				hashStorage.storeClosenessCentrality(level, ccMap);

				break;

			case "PAGERANK+DEGREE CENTRALITY":
				//pageRank = new PageRank(adjacencyMatrix[0].length);
				//pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
				
				//pageRank = new PageRank(adjacencyMatrix[0].length);
				//pr = pageRank.compute(adjacencyMatrix[0].length, adjacencyMatrix);
				pr = PageRank.compute((double)adjacencyMatrix[0].length, adjacencyMatrix);
				
				prMap = hashStorage.setupPRFormat(pr);
				hashStorage.storePageRank(level, prMap);
				dc = DegreeCentrality.compute(adjacencyMatrix);
				dcMap = hashStorage.setupDCFormat(dc);
				hashStorage.storeNodeDegree(level, dcMap);

				break;

			case "BETWEENNESS CENTRALITY+DEGREE CENTRALITY":
				bc = BetweennessCentrality.compute(adjacencyMatrix.length, adjacencyMatrix);
				bcMap = hashStorage.setupBetweennessFormat(bc);
				hashStorage.storeNodeBetweenness(level, bcMap);
				dc = DegreeCentrality.compute(adjacencyMatrix);
				dcMap = hashStorage.setupDCFormat(dc);
				hashStorage.storeNodeDegree(level, dcMap);

				break;

			default:
				break;

			}
		}

		System.out.println("End of calculation");
		DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm();
		dijkstraAlgorithm.computeShortestPath(adjacencyMatrix, adjacencyMatrix[0].length, indexOfStartNode);
		HashMap<Integer, Integer> nodeListMap = new HashMap<Integer, Integer>();
		ArrayList<ArrayList<String>> arrNodeListPaths = new ArrayList<ArrayList<String>>();
		int iterIndex = 0;
		for (int i = 0; i < adjacencyMatrix[0].length; i++) {
			if (i != indexOfStartNode) {
				if (clustering == true) {
					if (!communityDetection.getStartNodeCommunity()
							.equals(communityDetection.getNodeCommunity(String.valueOf(i), communityAlgorithm))) {
						if (communityDetection.validateNodeInclusion(String.valueOf(i), nodesPerCluster,
								communityAlgorithm)) {
							// Reverse path is obtained

							ArrayList<String> arrPathList = performGraphPropertyComputation(level, i, selectedProperty,
									indexOfStartNode, hashStorage, dijkstraAlgorithm);

							if (arrPathList.size() > 0) {
								arrNodeListPaths.add(arrPathList);
								iterIndex = nodeListMap.size();
								for (int z = arrPathList.size() - 1; z >= 0; z--) {
									if (!nodeListMap.containsValue(Integer.parseInt(arrPathList.get(z)))) {
										nodeListMap.put(iterIndex, Integer.parseInt(arrPathList.get(z)));
										iterIndex++;

									}
								}
							}

						}
					}
				} else {

					// Reverse path is obtained
					ArrayList<String> arrPathList = performGraphPropertyComputation(level, i, selectedProperty,
							indexOfStartNode, hashStorage, dijkstraAlgorithm);

					if (arrPathList.size() > 0) {
						arrNodeListPaths.add(arrPathList);
						iterIndex = nodeListMap.size();
						for (int z = arrPathList.size() - 1; z >= 0; z--) {
							if (!nodeListMap.containsValue(Integer.parseInt(arrPathList.get(z)))) {
								nodeListMap.put(iterIndex, Integer.parseInt(arrPathList.get(z)));
								iterIndex++;

							}
						}
					}

				}
			}
		}

		int[][] newAdjacencyMatrix = new int[nodeListMap.size()][nodeListMap.size()];
		for (int m = 0; m < arrNodeListPaths.size(); m++) {
			ArrayList<String> list = arrNodeListPaths.get(m);
			for (int p = list.size() - 1; p > 0; p--) {
				int src = Integer.parseInt(list.get(p));
				int x = p - 1;
				int dest = Integer.parseInt(list.get(x));
				int srcIndex = -1;
				int destIndex = -1;
				// http://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
				for (Map.Entry<Integer, Integer> entry : nodeListMap.entrySet()) {
					Integer value = entry.getValue();
					if (src == value) {
						srcIndex = entry.getKey();
						break;
					}
				}
				for (Map.Entry<Integer, Integer> entry : nodeListMap.entrySet()) {
					Integer value = entry.getValue();
					if (dest == value) {
						destIndex = entry.getKey();
						break;
					}
				}
				newAdjacencyMatrix[srcIndex][destIndex] = 1;
				if (!isDirected) {
					newAdjacencyMatrix[destIndex][srcIndex] = 1;
				}
			}

		}
		if (newAdjacencyMatrix.length > 0) {

			hashStorage.storeAdjacencyMatrix(level + 1, newAdjacencyMatrix);
			hashStorage.storeIterToNodeId(level + 1, nodeListMap);
		}

	}

	/**
	 * Perform operation based on property type
	 * 
	 * @param previouslevel Previous reduction level
	 * @param index Index of current node
	 * @param selectedProperty Graph-Theoretic Property or Centrality
	 * @param indexOfStartNode Index of start node in adjacency matrix
	 * @param hashStorage Instance of HashStorage
	 * @param dijkstraAlgorithm Instance of DijkstraAlgorithm
	 * @return List of nodes
	 */
	public ArrayList<String> performGraphPropertyComputation(int previouslevel, int index, String selectedProperty,
			int indexOfStartNode, HashStorage hashStorage, DijkstraAlgorithm dijkstraAlgorithm) {
		ArrayList<String> arrPathList = new ArrayList<String>();
		switch (selectedProperty.trim().toUpperCase()) {
		case "BETWEENNESS CENTRALITY":
			if (hashStorage.getNodeBetweenness(previouslevel, index) > hashStorage.getNodeBetweenness(previouslevel,
					indexOfStartNode)) {
				arrPathList = dijkstraAlgorithm.getShortestPathAsList(indexOfStartNode, index);

			}
			break;

		case "PAGERANK":
			if(hashStorage.getPageRank(previouslevel, index) !=null && hashStorage.getPageRank(previouslevel, indexOfStartNode)!=null){
			if (hashStorage.getPageRank(previouslevel, index) > hashStorage.getPageRank(previouslevel,
					indexOfStartNode)) {
				arrPathList = dijkstraAlgorithm.getShortestPathAsList(indexOfStartNode, index);
			}
			}
			break;

		case "PAGERANK+BETWEENNESS CENTRALITY":
			if ((hashStorage.getPageRank(previouslevel, index) > hashStorage.getPageRank(previouslevel,
					indexOfStartNode))
					&& (hashStorage.getNodeBetweenness(previouslevel, index) > hashStorage
							.getNodeBetweenness(previouslevel, indexOfStartNode))) {
				arrPathList = dijkstraAlgorithm.getShortestPathAsList(indexOfStartNode, index);
			}
			break;
		case "CLUSTERING COEFFICIENT":
			if (hashStorage.getClusteringCoefficient(previouslevel, index) > hashStorage
					.getClusteringCoefficient(previouslevel, indexOfStartNode)) {
				arrPathList = dijkstraAlgorithm.getShortestPathAsList(indexOfStartNode, index);
			}

			break;
		case "DEGREE CENTRALITY":
			if (hashStorage.getDegree(previouslevel, index) > hashStorage.getDegree(previouslevel, indexOfStartNode)) {
				arrPathList = dijkstraAlgorithm.getShortestPathAsList(indexOfStartNode, index);
			}
			break;
		case "CLOSENESS CENTRALITY":
			if (hashStorage.getNodeCloseness(previouslevel, index) > hashStorage.getNodeCloseness(previouslevel,
					indexOfStartNode)) {
				arrPathList = dijkstraAlgorithm.getShortestPathAsList(indexOfStartNode, index);
			}
			break;

		case "PAGERANK+DEGREE CENTRALITY":
			if ((hashStorage.getPageRank(previouslevel, index) > hashStorage.getPageRank(previouslevel,
					indexOfStartNode))
					&& (hashStorage.getDegree(previouslevel, index) > hashStorage.getDegree(previouslevel,
							indexOfStartNode))) {
				arrPathList = dijkstraAlgorithm.getShortestPathAsList(indexOfStartNode, index);
			}

			break;

		case "BETWEENNESS CENTRALITY+DEGREE CENTRALITY":
			if ((hashStorage.getNodeBetweenness(previouslevel, index) > hashStorage.getNodeBetweenness(previouslevel,
					indexOfStartNode))
					&& (hashStorage.getDegree(previouslevel, index) > hashStorage.getDegree(previouslevel,
							indexOfStartNode))) {
				arrPathList = dijkstraAlgorithm.getShortestPathAsList(indexOfStartNode, index);
			}

			break;

		}

		return arrPathList;

	}

	/**
	 * Show label on node
	 * 
	 * @param nodeId Node ID
	 * @param label Label of node
	 */
	public void showLabelOnRootGraph(String nodeId, String label) {
		ROOT_GRAPH.getNode(nodeId).addAttribute("ui.label", label);
	}

	/**
	 * Read input file for graph generation
	 * 
	 * @param matrixOperations Instance of MatrixOperations
	 * @param inputFile Path to input file
	 * @param isDirected TRUE if graph is directed else FALSE
	 * @param viewerPipe Instance of ViewerPipe
	 * @param hashStorage Instance of HashStorage
	 * @return Graph object
	 * @throws IOException Throws IOException
	 */
	public Graph readInputFile(MatrixOperations matrixOperations, String inputFile, Boolean isDirected,
			ViewerPipe viewerPipe, HashStorage hashStorage) throws IOException {

		List<String> fileLines = new ArrayList<String>();
		Graph g = null;
		if (inputFile.toUpperCase().endsWith(".GML")) {	
			//ReadFile is in the GMLReader jar.
			//Written by me and then exported to a jar file
			ReadFile readFile = new ReadFile(inputFile);
			String content = readFile.read();
			ArrayList<String> listEdges = readFile.getEdges(content);
			for (String edge : listEdges) {
				String arrEdge = edge.replace(",", " ");
				fileLines.add(arrEdge);
			}

		} else if (inputFile.toUpperCase().endsWith(".DGS")) {
			
			System.out.println(">> " + inputFile);
			BufferedReader fr = new BufferedReader(new FileReader(new File(inputFile)));
			try{				
				String line;
				while ((line = fr.readLine()) != null){
					if(line.startsWith("ae")){
						String[] splitLine = line.split("\\s+");
						fileLines.add(splitLine[2].replace("\"", "") + " " + splitLine[3].replace("\"", ""));					 
					}
				}
			} catch( IOException e) {
				e.printStackTrace();
			} finally {
				fr.close();
			}
			
		
		} else {

			fileLines = Files.readAllLines(Paths.get(inputFile), StandardCharsets.UTF_8);
		}
		matrixOperations.createAdjacencyMatrixFromFile(fileLines, isDirected, hashStorage);

		if (viewerPipe != null) {
			Graph graph = new SingleGraph("Temp");
			task = new Task(fileLines, isDirected, graph, viewerPipe);
			task.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					// TODO Auto-generated method stub
					if ("STATE".equals(evt.getPropertyName().toUpperCase())) {
						// ROOT_GRAPH = (Graph)evt.getNewValue();

					}
				}

			});
			task.execute();

			try {
				g = (Graph) task.get();
				viewerPipe.addAttributeSink(g);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(0);
			Graph tempgraph = new SingleGraph("Temp");
			for (int x = 0; x < adjacencyMatrix.length; x++) {
				createNodeFromMatrix(String.valueOf(x), tempgraph);
				for (int y = 0; y < adjacencyMatrix.length; y++) {
					createNodeFromMatrix(String.valueOf(y), tempgraph);
					if (adjacencyMatrix[x][y] == 1) {
						createEdgeFromMatrix(String.valueOf(x), String.valueOf(y), isDirected, tempgraph);
					}
				}
			}
			g = tempgraph;

		}

		return g;

	}

	/**
	 * Create a node in graph from adjacency matrix
	 * 
	 * @param nodeId Node ID
	 * @param graph Instance of Graph
	 */
	public void createNodeFromMatrix(String nodeId, Graph graph) {
		if (graph.getNode(nodeId) == null) {
			graph.addNode(nodeId);
			showLabelOnRootGraphFromMatrix(nodeId, nodeId, graph);
		}
	}

	/**
	 * Show label on a node
	 * 
	 * @param nodeId Node ID
	 * @param label Label of node
	 * @param graph Instance of Graph
	 */
	public void showLabelOnRootGraphFromMatrix(String nodeId, String label, Graph graph) {
		graph.getNode(nodeId).addAttribute("ui.label", label);
	}

	/**
	 * Create edge between nodes
	 * 
	 * @param source Source ID
	 * @param destination Destination ID
	 * @param isDirected TRUE if graph is directed else FALSE
	 * @param graph Instance of graph
	 */
	public void createEdgeFromMatrix(String source, String destination, Boolean isDirected, Graph graph) {
		if (graph.getNode(source).hasEdgeBetween(destination) == false) {
			graph.addEdge("S" + source + "D" + destination, source, destination, isDirected);
		}
	}

	/**
	 * Creation of root graph
	 * 
	 * @param fileContent Content of input file
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	public void constructRootGraph(List<String> fileContent, Boolean isDirected) {

		int count = 0;
		for (int i = 0; i < fileContent.size(); i++) {
			// System.out.println(fileContent.get(i));
			if ((!fileContent.get(i).startsWith("#")) && (fileContent.get(i).length() > 1)) {
				count++;
				String[] arrLine = fileContent.get(i).trim().split("\\s+");
				String source = arrLine[0].trim();
				String destination = arrLine[1].trim();
				createNodeFromFile(source);
				createNodeFromFile(destination);
				createEdgeFromFile(source, destination, isDirected);
			}
			if (count % 1000 == 0) {
				System.out.println("Finished processing " + count + " lines");
			}
		}
	}

	/**
	 * Create node
	 * 
	 * @param nodeId Node ID
	 */
	public void createNodeFromFile(String nodeId) {
		if (ROOT_GRAPH.getNode(nodeId) == null) {
			ROOT_GRAPH.addNode(nodeId);
			showLabelOnRootGraph(nodeId, nodeId);

		}
	}

	/**
	 * Create edge
	 * 
	 * @param source Source ID
	 * @param destination Destination ID
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	public void createEdgeFromFile(String source, String destination, Boolean isDirected) {
		if (ROOT_GRAPH.getNode(source).hasEdgeBetween(destination) == false) {
			ROOT_GRAPH.addEdge("S" + source + "D" + destination, source, destination, isDirected);
		}
	}

	/**
	 * Retrieve root graph
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param matrixOperations Instance of MatrixOperations
	 * @param sourceName Name of generator
	 * @param isDirected TRUE if graph is directed else FALSE
	 * @param viewerPipe Instance of ViewerPipe
	 * @return Instance of Graph
	 */
	public Graph fetchMainGraph(HashStorage hashStorage, MatrixOperations matrixOperations, String sourceName,
			Boolean isDirected, ViewerPipe viewerPipe) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		hashStorage.resetAllHashMaps();
		int[][] adjacencyMatrix = null;
		GraphGenerate graphGenerate = new GraphGenerate();
		graphGenerate.addStyling();
		Graph g = null;

		AdjacencyMatrix matrix = null;
		ClassicalGenerators classicalGenerators;
		switch (sourceName.trim().toUpperCase()) {

		case "BARABASI-ALBERT":
			classicalGenerators = new ClassicalGenerators();
			classicalGenerators.applyBarabasiAlbert();
			if (GRAPHS.get(0) != null && GRAPHS.get(0).getNodeCount() > 0) {
				matrix = new AdjacencyMatrix();
				adjacencyMatrix = matrix.createAdjacencyMatrixFromGraph(GRAPHS.get(0));
				g = GRAPHS.get(0);
				StoreGraphDetailsAsMatrix(hashStorage, adjacencyMatrix, viewerPipe, GRAPHS.get(0));
			}
			break;
		case "DOROGOVTSEV-MENDES":
			classicalGenerators = new ClassicalGenerators();
			classicalGenerators.applyDorogovtsevMendes();
			if (GRAPHS.get(0) != null && GRAPHS.get(0).getNodeCount() > 0) {
				matrix = new AdjacencyMatrix();
				adjacencyMatrix = matrix.createAdjacencyMatrixFromGraph(GRAPHS.get(0));
				g = GRAPHS.get(0);
				StoreGraphDetailsAsMatrix(hashStorage, adjacencyMatrix, viewerPipe, g);
			}
			break;
		case "WATTS-STROGATZ":
			classicalGenerators = new ClassicalGenerators();
			classicalGenerators.applySmallWorldWattsStrogatz();
			if (GRAPHS.get(0) != null && GRAPHS.get(0).getNodeCount() > 0) {
				matrix = new AdjacencyMatrix();
				adjacencyMatrix = matrix.createAdjacencyMatrixFromGraph(GRAPHS.get(0));
				g = GRAPHS.get(0);
				StoreGraphDetailsAsMatrix(hashStorage, adjacencyMatrix, viewerPipe, g);
			}
			break;

		default:
			try {
				// adjacencyMatrix = readInputFile(matrixOperations,sourceName,
				// isDirected, viewerPipe);
				g = readInputFile(matrixOperations, sourceName, isDirected, viewerPipe, hashStorage);
				ROOT_GRAPH = g;
				graphsCommandline.add(0, g);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		}

		this.addStyling();
		return g;
	}

	/**
	 * Store graph details as adjacency matrix
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param adjacencyMatrix Instance of AdjacencyMatrix
	 * @param viewerPipe Instance of ViewerPipe
	 * @param g Instance of graph
	 */
	public void StoreGraphDetailsAsMatrix(HashStorage hashStorage, int[][] adjacencyMatrix, ViewerPipe viewerPipe,
			Graph g) {

		hashStorage.storeAdjacencyMatrix(0, adjacencyMatrix);
		HashMap<Integer, Integer> map = hashStorage.storeMatrixIndexAsNodeId(adjacencyMatrix);
		hashStorage.storeIterToNodeId(0, map);
		viewerPipe.addAttributeSink(g);
	}

	/**
	 * Get all graphs
	 * 
	 * @return Array list of graphs
	 */

	public ArrayList<Graph> getAllGraphs() {
		return GRAPHS;
	}

	/**
	 * Set no of reductions
	 * 
	 * @param noOfReductions Number of Reductions
	 */
	public void setNumberOfReductions(int noOfReductions) {
		this.noOfReductions = noOfReductions;
	}

	/**
	 * Call this method when program is executed from command line
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param startNode Node ID of start node
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	public void createAllReducedGraphsCommandline(HashStorage hashStorage, String startNode, Boolean isDirected) {

		HashMap<Integer, HashMap<Integer, Integer>> hashIter = hashStorage.getHashIterToNodeId();
		int n = hashIter.size();
		for (int i = 1; i <= n - 1; i++) {
			if (hashIter.get(i).size() > 0) {
				// GraphGenerate.GRAPHS.add(new SingleGraph("Level "+i));
				graphsCommandline.add(new SingleGraph("Level " + i));

			}
		}

		for (int i = n - 1; i > 0; i--) {
			if (hashIter.get(i).size() > 0) {
				HashMap<Integer, Integer> map = hashIter.get(i);
				for (Integer node : map.values()) {
					@SuppressWarnings("unused")
					Boolean nodeFound = false;
					int k = 0;
					if (i > -1) {
						k = i - 1;
					}

					int nodeId = node;
					while (k > -1) {
						HashMap<Integer, Integer> tempMap = hashIter.get(k);
						nodeId = tempMap.get(nodeId);
						k--;
					}

					if (nodeId != -1) {
						if (graphsCommandline.get(i).getNode(String.valueOf(nodeId)) == null) {
							createNodeCommandline(i, String.valueOf(nodeId), startNode);
						}
					}

				}

				if (i >= 2) {
					int[][] tempAdjacencyMatrix = hashStorage.getAdjacencyMatrix(i);
					for (int y = 0; y < tempAdjacencyMatrix[0].length; y++) {
						for (int z = 0; z < tempAdjacencyMatrix[0].length; z++) {
							if (tempAdjacencyMatrix[y][z] == 1) {

								String src = String.valueOf(getIdOfNode(hashStorage, hashStorage.getNodeId(i, y), i));
								String dest = String.valueOf(getIdOfNode(hashStorage, hashStorage.getNodeId(i, z), i));
								if (!src.equals(dest)) {
									if (graphsCommandline.get(i).getNode(src) != null
											&& graphsCommandline.get(i).getNode(dest) != null) {
										if (graphsCommandline.get(i).getNode(src).hasEdgeBetween(dest) == false) {
											graphsCommandline.get(i).addEdge("S" + src + "D" + dest, src, dest,
													isDirected);
										}
									}
								}
							}
						}
					}
				}
			}

			createEdgeCommandline(hashStorage, isDirected);

		}

	}

	/**
	 * Creation of reduced graphs
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param startNode Node ID of start node
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	public static void createAllReducedGraphs(HashStorage hashStorage, String startNode, Boolean isDirected) {
		HashMap<Integer, HashMap<Integer, Integer>> hashIter = hashStorage.getHashIterToNodeId();
		int n = hashIter.size();
		for (int i = 1; i <= n - 1; i++) {
			if (hashIter.get(i).size() > 0) {
				GraphGenerate.GRAPHS.add(new SingleGraph("Level " + i));

			}
		}

		for (int i = n - 1; i > 0; i--) {
			if (hashIter.get(i).size() > 0) {
				HashMap<Integer, Integer> map = hashIter.get(i);
				for (Integer node : map.values()) {
					@SuppressWarnings("unused")
					Boolean nodeFound = false;
					int k = 0;
					if (i > -1) {
						k = i - 1;
					}

					int nodeId = node;
					while (k > -1) {
						HashMap<Integer, Integer> tempMap = hashIter.get(k);
						nodeId = tempMap.get(nodeId);
						k--;
					}

					if (nodeId != -1) {
						if (GRAPHS.get(i).getNode(String.valueOf(nodeId)) == null) {
							createNode(i, String.valueOf(nodeId), startNode);
						}
					}

				}

				if (i >= 2) {
					int[][] tempAdjacencyMatrix = hashStorage.getAdjacencyMatrix(i);
					for (int y = 0; y < tempAdjacencyMatrix[0].length; y++) {
						for (int z = 0; z < tempAdjacencyMatrix[0].length; z++) {
							if (tempAdjacencyMatrix[y][z] == 1) {

								String src = String.valueOf(getIdOfNode(hashStorage, hashStorage.getNodeId(i, y), i));
								String dest = String.valueOf(getIdOfNode(hashStorage, hashStorage.getNodeId(i, z), i));
								if (!src.equals(dest)) {
									if (GRAPHS.get(i).getNode(src) != null && GRAPHS.get(i).getNode(dest) != null) {
										if (GRAPHS.get(i).getNode(src).hasEdgeBetween(dest) == false) {
											GRAPHS.get(i).addEdge("S" + src + "D" + dest, src, dest, isDirected);
										}
									}
								}
							}
						}
					}
				}
			}

			createEdge(hashStorage, isDirected);

		}

	}

	/**
	 * Get node id from adjacency matrix
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param index Index of node in the adjacency matrix
	 * @param level Reduction level
	 * @return node id
	 */
	private static Integer getIdOfNode(HashStorage hashStorage, int index, int level) {
		int val = index;
		for (int i = level - 1; i >= 1; i--) {
			val = hashStorage.getNodeId(i, val);
		}
		return val;
	}

	/**
	 * Create edge
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	private static void createEdge(HashStorage hashStorage, Boolean isDirected) {
		int[][] tempAdjacencyMatrix = hashStorage.getAdjacencyMatrix(1);
		for (int i = 1; i < 2; i++) {
			for (int j = 0; j < tempAdjacencyMatrix[0].length; j++) {
				for (int k = 0; k < tempAdjacencyMatrix[0].length; k++) {
					if (tempAdjacencyMatrix[j][k] == 1) {
						HashMap<Integer, Integer> map = hashStorage.getHashIter(1);
						String src = String.valueOf(map.get(j));
						String dest = String.valueOf(map.get(k));
						if (GRAPHS.get(i).getNode(src) != null && GRAPHS.get(i).getNode(dest) != null) {
							if (GRAPHS.get(i).getNode(src).hasEdgeBetween(dest) == false) {
								GRAPHS.get(i).addEdge("S" + src + "D" + dest, src, dest, isDirected);
							}
						}

					}

				}

			}
		}
	}

	/**
	 * Edge creation if program is executed from command line
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	private void createEdgeCommandline(HashStorage hashStorage, Boolean isDirected) {
		int[][] tempAdjacencyMatrix = hashStorage.getAdjacencyMatrix(1);
		for (int i = 1; i < 2; i++) {
			for (int j = 0; j < tempAdjacencyMatrix[0].length; j++) {
				for (int k = 0; k < tempAdjacencyMatrix[0].length; k++) {
					if (tempAdjacencyMatrix[j][k] == 1) {
						HashMap<Integer, Integer> map = hashStorage.getHashIter(1);
						String src = String.valueOf(map.get(j));
						String dest = String.valueOf(map.get(k));
						if (graphsCommandline.get(i).getNode(src) != null
								&& graphsCommandline.get(i).getNode(dest) != null) {
							if (graphsCommandline.get(i).getNode(src).hasEdgeBetween(dest) == false) {
								graphsCommandline.get(i).addEdge("S" + src + "D" + dest, src, dest, isDirected);
							}
						}

					}

				}

			}
		}
	}

	/**
	 * Create node if program executed from command line
	 * 
	 * @param level Reduction level
	 * @param node Node ID
	 * @param startNodeId Node ID of start node
	 */
	public void createNodeCommandline(int level, String node, String startNodeId) {

		if (graphsCommandline.get(level).getNode(node) == null) {
			graphsCommandline.get(level).addNode(node);
			graphsCommandline.get(level).getNode(node).addAttribute("ui.style", "size:15;");
			if (node.equals(startNodeId)) {
				graphsCommandline.get(level).getNode(node).addAttribute("ui.style", "fill-color:green;");
			} else {
				graphsCommandline.get(level).getNode(node).addAttribute("ui.style", "fill-color:red;");
			}
			graphsCommandline.get(level).getNode(node).addAttribute("ui.label", node);

		}

	}

	/**
	 * Create node
	 * 
	 * @param level Reduction level
	 * @param node Node ID
	 * @param startNodeId Node ID of start node
	 */
	public static void createNode(int level, String node, String startNodeId) {

		if (GRAPHS.get(level).getNode(node) == null) {
			GRAPHS.get(level).addNode(node);
			GRAPHS.get(level).getNode(node).addAttribute("ui.style", "size:15;");
			if (node.equals(startNodeId)) {
				GRAPHS.get(level).getNode(node).addAttribute("ui.style", "fill-color:green;");
			} else {
				GRAPHS.get(level).getNode(node).addAttribute("ui.style", "fill-color:red;");
			}
			GRAPHS.get(level).getNode(node).addAttribute("ui.label", node);

		}
	}

}
