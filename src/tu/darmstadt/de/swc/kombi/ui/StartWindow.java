package tu.darmstadt.de.swc.kombi.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.DefaultMouseManager;

import tu.darmstadt.de.swc.kombi.community.CommunityDetectionAlgorithms;
import tu.darmstadt.de.swc.kombi.generate.GraphGenerate;
import tu.darmstadt.de.swc.kombi.html.ResultLogger;
import tu.darmstadt.de.swc.kombi.matrix.MatrixOperations;
import tu.darmstadt.de.swc.kombi.results.ResultsStore;
import tu.darmstadt.de.swc.kombi.storage.HashStorage;

/**
 * Program begins from this class
 * 
 * @author suhas
 *
 */
public class StartWindow implements Runnable {

	// Viewer handler
	Viewer viewer;
	// Reduced graphs' viewer handler
	Viewer reducedViewer;

	// Default View handler
	DefaultView defaultView;
	// Mouse handler
	DefaultMouseManager defaultMouseManager = new DefaultMouseManager();

	// Temporary list containing viewers
	ArrayList<Viewer> tempViewer = new ArrayList<Viewer>();
	// List containing pipe handlers
	ArrayList<ViewerPipe> pipe = new ArrayList<ViewerPipe>();
	// List containing views
	ArrayList<View> view = new ArrayList<View>();
	// List containing steps
	private List<String> listSteps = new ArrayList<String>();
	// Viewer pipe handler
	private ViewerPipe viewerPipe;
	// File handler
	File file;
	// Main content pane
	Container contentPane;

	JFrame frameStartWindow, frameReducedGraphs;

	JPanel panelControl, panelGraph, panelReducedGraph, panelStackReducedGraph, panelGridReducedGraph,
			panelCommunityConfiguration, panelDropDown, panelComplexityEvaluation, rightPanel, rightSubPanelTop,
			rightSubPanelBottom, rightSubPanelCenter, panelForReducedGraphs;

	JLabel labelNoOfReductions;

	JTextField textFieldNoOfReductions, textFieldRandomNumber;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItem;

	@SuppressWarnings("rawtypes")
	DefaultComboBoxModel propertyNames = new DefaultComboBoxModel();
	@SuppressWarnings("rawtypes")
	JComboBox comboBoxPropertyNames, generatorCombo;
	JScrollPane propertyNamesScrollPane, scrollPaneReducedGraphs, communityNamesScrollPane, scrollPaneCommunityCombo,
			scrollPaneGeneratorCombo;

	JTabbedPane tabbedPaneGraphs;

	JCheckBox checkboxCommunity, checkBoxCommunity, checkBoxIncludeNativeCommunity, checkBoxIsDirected;

	@SuppressWarnings("rawtypes")
	DefaultComboBoxModel communityNames = new DefaultComboBoxModel();

	@SuppressWarnings("rawtypes")
	JComboBox comboBoxCommunityNames, communityCombo;

	JButton buttonGenerateCommunities, buttonChartGeneration, buttonCustomize, buttonStart, buttonOpen;

	DefaultComboBoxModel<String> generatorNames = new DefaultComboBoxModel<String>();

	ButtonGroup radioButtonGroup = new ButtonGroup();
	JRadioButton radioButtonFromFile, radioButtonAutoGenerator;

	private String startNode, selectedProperty, frameName, selectedGenerator, filename, commandlineReductions, stepType,
			algorithm;

	private Boolean showReducedGraphs, booleanClustering, booleanIncludeNativeCommunity, isDirected, directedGraph;
	private int sliderMinimum, sliderMaximum, sliderInit;

	private String[] graphProperties = { "Betweenness Centrality", "PageRank", "PageRank+Betweenness Centrality",
			"Closeness Centrality", "Clustering Coefficient", "Degree Centrality", "PageRank+Degree Centrality",
			"Betweenness Centrality+Degree Centrality" };

	private String[] communities = { "Hierarchical Clustering", "Original Louvain",
			"Louvain with Multilevel refinement", "Direct Clustering" };
	private String[] generators = { "Barabasi-Albert", "Dorogovtsev-Mendes", "Watts-Strogatz" };

	static String generatingMethod = "AUTO GENERATOR";
	static int numberOfIterationsForClusteringStep = 2;
	long startTime = 0, stopTime = 0;

	public static Boolean containsCommandLineArguments = false;
	Object rowData[][] = { 
			{ "Very Low", "R", "R", "C & R", "C & R", "C & R" },
			{ "Low", "R", "R", "C & R", "C & R", "C & R" }, 
			{ "Medium", "R", "R", "C & R", "C & R", "C & R" },
			{ "High", "R", "R", "C & R", "C & R", "C & R" }, 
			{ "Very High", "R", "R", "C & R", "C & R", "C & R" } 
			};
	Object columnNames[] = { "# of nodes", "Very Low", "Low", "Medium", "High", "Very High" };

	CommunityDetectionAlgorithms communityDetectionAlgorithms = new CommunityDetectionAlgorithms();
	GraphGenerate graphGenerate = new GraphGenerate();
	GraphGenerate graphGenerateCommandline = null;
	MatrixOperations matrixOperations = new MatrixOperations();
	HashStorage hashStorage = new HashStorage();
	CustomizeStepsWindow customizeWindow;
	ResultsStore results = null;

	public StartWindow() {
		startNode = "";
		selectedProperty = "";
		frameName = "Graph Analyzer";
		showReducedGraphs = false;
		booleanClustering = false;
		booleanIncludeNativeCommunity = false;
		sliderMinimum = 0;
		sliderMaximum = 100;
		sliderInit = 0;

	}

	public StartWindow(String filename, String commandlineReductions, String startNode, String directed,
			String selectedProperty, String stepType, String algorithm) {
		this.filename = filename;
		file = new File(this.filename);
		this.commandlineReductions = commandlineReductions;
		this.startNode = startNode;
		if (directed.toUpperCase().equals("TRUE")) {
			this.directedGraph = true;
		} else {
			this.directedGraph = false;
		}

		this.selectedProperty = getTypeOfProperty(selectedProperty);
		this.stepType = stepType;
		if (stepType.toUpperCase().equals("S1")) {
			this.algorithm = null;
		} else {
			this.algorithm = getTypeOfCommunityAlgorithm(algorithm);
		}
		graphGenerateCommandline = new GraphGenerate(true);
		hashStorage = new HashStorage();
	}

	/**
	 * Call setupWindow()
	 */
	private void startUI() {
		setupWindow();
	}

	/**
	 * Begin Execution for programs running from command line
	 * 
	 * @param filename Path to input file
	 * @param reductions Number of reductions
	 * @param randomNumber Start node ID
	 * @param directed TRUE if graph is directed else FALSE
	 * @param propertyType Graph-Theoretic Property or Centrality
	 * @param stepType Type of Step : S1 or S2
	 * @param algorithm Name of community algorithm
	 * 
	 * @return Nothing
	 */
	private void startExecutionWithoutUI(String filename, String reductions, String randomNumber, Boolean directed,
			String propertyType, String stepType, String algorithm) {
		startTime = System.currentTimeMillis(); 
		System.out.println("You selected:" + randomNumber);

		
		graphGenerateCommandline.setNumberOfReductions(0);
		// GraphGenerate.GRAPHS.clear();
		graphGenerateCommandline.getGraphs().clear();
		// GraphGenerate.CURRENT_ITERATION = 0;
		hashStorage.resetAllHashMaps();

		@SuppressWarnings("unused")
		Graph g = null;
		g = graphGenerateCommandline.fetchMainGraph(hashStorage, matrixOperations, filename, directed, null);
		if (stepType.toUpperCase().equals("S1")) {
			begin(null, null, false, false, reductions, hashStorage, getStartNode(), directed);
		} else {
			begin(communityDetectionAlgorithms, algorithm, true, true, reductions, hashStorage, getStartNode(),
					directedGraph);
		}

		graphGenerateCommandline.createAllReducedGraphsCommandline(hashStorage, getStartNode(), directed);
		results = new ResultsStore();
		try {
			results.setupDataForResultsCommandline(hashStorage, selectedProperty, startNode, directed,
					graphGenerateCommandline);
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		generateResults(results);
		stopTime = System.currentTimeMillis();
		long timeTaken = stopTime - startTime;
		calculateTimeForGeneration(timeTaken);
		calculateMemoryConsumption();
	}

	/**
	 * Create the main window containing all the necessary controls
	 * 
	 */
	private void setupWindow() {

		frameStartWindow = new JFrame(frameName);
		frameStartWindow.setPreferredSize(new Dimension(800, 600));
		frameStartWindow.setMaximumSize(new Dimension(sliderMaximum * 600, 600));
		frameStartWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frameStartWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameStartWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});

		//frameStartWindow.setJMenuBar(addMenuBar());
		contentPane = frameStartWindow.getContentPane();
		contentPane.setLayout(new BorderLayout());

		setupPanelGraph();
		setupTabbedPanes();
		setupControlPanel();
		setupRightPanel();

		contentPane.add(panelControl, BorderLayout.PAGE_START);
		contentPane.add(tabbedPaneGraphs, BorderLayout.CENTER);
		contentPane.add(rightPanel, BorderLayout.LINE_END);
		setupRootGraphTab(new SingleGraph(""));

		frameStartWindow.pack();
		frameStartWindow.setVisible(true);

		while (true) {
			viewerPipe.pump();

		}

	}

	/**
	 * Translate short hand of property type to full name
	 * 
	 * @param property Short form of Property type
	 * @return Name of graph theoretic property
	 */
	private String getTypeOfProperty(String property) {
		String val = null;
		switch (property.toUpperCase()) {
		case "P1":
			val = "BETWEENNESS CENTRALITY";
			break;
		case "P2":
			val = "PAGERANK";
			break;
		case "P3":
			val = "PAGERANK+BETWEENNESS CENTRALITY";
			break;
		case "P4":
			val = "CLOSENESS CENTRALITY";
			break;
		case "P5":
			val = "CLUSTERING COEFFICIENT";
			break;
		case "P6":
			val = "DEGREE CENTRALITY";
			break;

		default:
			break;

		}
		return val;
	}

	/**
	 * Translate short hand of community type to full name
	 * 
	 * @param algorithm Short form community algorithm
	 * @return Name of Community Detection Algorithm
	 */
	private String getTypeOfCommunityAlgorithm(String algorithm) {
		String val = null;

		switch (algorithm.toUpperCase()) {
		case "A1":
			val = "HIERARCHICAL CLUSTERING";
			break;
		case "A2":
			val = "ORIGINAL LOUVAIN";
			break;
		case "A3":
			val = "LOUVAIN WITH MULTILEVEL REFINEMENT";
			break;
		case "A4":
			val = "DIRECT CLUSTERING";
			break;

		default:
			break;

		}
		return val;
	}

	/**
	 * Add menu
	 * 
	 * @return Menu Bar
	 */
	private JMenuBar addMenuBar() {
		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("File");
		menuBar.add(menu);

		menuItem = new JMenuItem("Exit", KeyEvent.VK_Q);
		menuItem.getAccessibleContext().setAccessibleDescription("Exit");
		menu.add(menuItem);
		menuBar.add(menu);

		/*
		 * menu = new JMenu("Settings"); menu.setMnemonic(KeyEvent.VK_T);
		 * menu.getAccessibleContext().setAccessibleDescription("File");
		 * menuBar.add(menu);
		 * 
		 * menu = new JMenu("Help"); menu.setMnemonic(KeyEvent.VK_H);
		 * menu.getAccessibleContext().setAccessibleDescription("File");
		 * menuBar.add(menu);
		 */

		return menuBar;
	}

	/**
	 * Remove all frame handles to display a clean window
	 */
	private void performCleanupOperation() {
		if (frameReducedGraphs != null) {
			frameReducedGraphs.dispose();
			// showReducedGraphs = false;
			setShowReducedGraphs(false);
		}

		if (tabbedPaneGraphs.getTabCount() > 1) {
			for (int z = tabbedPaneGraphs.getTabCount() - 1; z > 0; z--) {
				tabbedPaneGraphs.remove(z);
			}
		}
		panelGraph.removeAll();
		panelGraph.revalidate();
		panelGraph.updateUI();

		graphGenerate.setNumberOfReductions(0);
		GraphGenerate.GRAPHS.clear();
		GraphGenerate.CURRENT_ITERATION = 0;
		hashStorage.resetAllHashMaps();

	}

	/**
	 * Setup panel for displaying the graph
	 */
	private void setupPanelGraph() {
		panelGraph = new JPanel();
		BorderLayout borderLayout = new BorderLayout();
		panelGraph.setLayout(borderLayout);
		panelGraph.setVisible(true);
	}

	/**
	 * Setup pane for tabs
	 */
	private void setupTabbedPanes() {
		tabbedPaneGraphs = new JTabbedPane();
		tabbedPaneGraphs.addTab("Root Graph", panelGraph);
		tabbedPaneGraphs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPaneGraphs.setVisible(true);
	}

	/**
	 * Setup panel for displaying child controls
	 */
	private void setupControlPanel() {
		panelControl = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		panelControl.setLayout(flowLayout);

		setupIsDirectedCheckBox();
		setupFileChooser();
		setupRadioButtons();
		setupGeneratorComboBox();
		setupControlButtons();
		setupPropertyComboBox();

		panelControl.setVisible(true);

	}

	/**
	 * Setup right panel to display evaluation table and other controls
	 */
	private void setupRightPanel() {
		rightPanel = new JPanel(new BorderLayout());
		rightPanel.setPreferredSize(new Dimension(400, panelGraph.getHeight()));

		rightSubPanelTop = new JPanel();
		rightSubPanelTop.setLayout(new GridLayout());

		buttonCustomize = new JButton("Steps");
		buttonCustomize.setPreferredSize(new Dimension(100, 27));
		buttonCustomize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (ValidateCustomizeWindow.booleanAllow == true) {
					ValidateCustomizeWindow.booleanAllow = false;
					customizeWindow = new CustomizeStepsWindow(communities, sliderMinimum, sliderMaximum, sliderInit);
					customizeWindow.setVisible(true);
				}
			}

		});

		rightSubPanelCenter = new JPanel(new BorderLayout());
		rightSubPanelCenter.setPreferredSize(new Dimension(300, 180));

		JTable table = new JTable(rowData, columnNames);

		JScrollPane scrollPaneTable = new JScrollPane(table);

		JLabel labelEvaluation = new JLabel("Evaluation Technique");
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new FlowLayout());
		labelPanel.add(labelEvaluation);

		labelPanel.add(buttonCustomize);

		JPanel panelRandomNodeControls = new JPanel();
		panelRandomNodeControls.setLayout(new FlowLayout());
		panelRandomNodeControls.add(new JLabel("Node Number:"));
		textFieldRandomNumber = new JTextField();
		textFieldRandomNumber.setColumns(6);
		panelRandomNodeControls.add(textFieldRandomNumber);
		JButton buttonBegin = new JButton();
		buttonBegin.setText("Begin");
		buttonBegin.setSize(new Dimension(40, 40));
		buttonBegin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (textFieldRandomNumber.getText().trim().length() > 0
						&& hashStorage.getHashAdjacencyMatrix().size() > 0) {
					setStartNode(textFieldRandomNumber.getText().trim());
					startTime = System.currentTimeMillis();
					begin(textFieldRandomNumber.getText().trim());
					stopTime = System.currentTimeMillis();
					long timeTaken = stopTime - startTime;
					calculateTimeForGeneration(timeTaken);
					calculateMemoryConsumption();
				} else {
					JOptionPane.showMessageDialog(frameStartWindow,
							"Either you have entered an incorrect random node-id or there is no graph loaded.");

				}
			}

		});

		panelRandomNodeControls.add(buttonBegin);
		rightSubPanelCenter.add(labelPanel, BorderLayout.PAGE_START);
		rightSubPanelCenter.add(scrollPaneTable, BorderLayout.CENTER);
		rightSubPanelCenter.add(panelRandomNodeControls, BorderLayout.PAGE_END);
		rightSubPanelBottom = new JPanel();
		rightSubPanelBottom.setLayout(new BorderLayout());
		rightSubPanelBottom.setPreferredSize(new Dimension(300, 200));
		rightPanel.add(rightSubPanelCenter, BorderLayout.PAGE_START);

	}

	/**
	 * Setup tab containing the parent graph
	 * 
	 * @param g Instance of Graph
	 */
	private void setupRootGraphTab(Graph g) {
		try {

			viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
			viewer.enableAutoLayout();

			panelGraph.add(viewer.addDefaultView(false), 0);
			viewerPipe = viewer.newViewerPipe();

			viewerPipe.addAttributeSink(GraphGenerate.GRAPHS.get(0));

			viewerPipe.addViewerListener(new ViewerListener() {

				@Override
				public void buttonPushed(String arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void buttonReleased(String arg0) {
					// TODO Auto-generated method stub
					startTime = System.currentTimeMillis();
					System.out.println("You clicked:" + arg0);
					// String startNodeId = arg0;					
					setStartNode(arg0);					
					begin(arg0);
					stopTime = System.currentTimeMillis();
					long timeTaken = stopTime - startTime;					
					calculateTimeForGeneration(timeTaken);
					calculateMemoryConsumption();
				}

				@Override
				public void viewClosed(String arg0) {
					// TODO Auto-generated method stub

				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * Begin computation of child graphs based on the parameters passed ( Called
	 * when program is executed from command line)
	 * 
	 * @param communityDetection Instance of CommunityDetectionAlgorithms
	 * @param communityAlgorithm Name of community algorithm
	 * @param booleanClustering TRUE if clustering else FALSE
	 * @param includeNativeCommunity TRUE
	 * @param numberOfReductions Number of reductions
	 * @param hashStorage Instance of HashStorage
	 * @param startNode Node ID of start node
	 * @param directed TRUE if graph is directed else FALSE
	 */
	private void begin(CommunityDetectionAlgorithms communityDetection, String communityAlgorithm,
			Boolean booleanClustering, Boolean includeNativeCommunity, String numberOfReductions,
			HashStorage hashStorage, String startNode, Boolean directed) {

		if (booleanClustering == false) {
			callCommandLineReduction(null, null, false, false, numberOfReductions, hashStorage, getStartNode(),
					directed);
		} else {
			for (int i = 0; i < numberOfIterationsForClusteringStep; i++) {
				int level = hashStorage.getCurrentLevel();
				int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(level);
				communityDetectionAlgorithms.clearAllValues();

				switch (communityAlgorithm.trim().toUpperCase()) {

				case "HIERARCHICAL CLUSTERING":
					communityDetectionAlgorithms.applyHC(adjacencyMatrix);
					break;
				case "ORIGINAL LOUVAIN":
					communityDetectionAlgorithms.applyLouvainOrSLM(
							hashStorage.getAdjacencyMatrixEdgeCount(adjacencyMatrix), adjacencyMatrix, level, 1);

					break;
				case "LOUVAIN WITH MULTILEVEL REFINEMENT":
					communityDetectionAlgorithms.applyLouvainOrSLM(
							hashStorage.getAdjacencyMatrixEdgeCount(adjacencyMatrix), adjacencyMatrix, level, 2);

					break;

				case "DIRECT CLUSTERING":
					communityDetectionAlgorithms.applyDirectClustering(adjacencyMatrix);
					break;

				default:
					break;

				}

				callCommandLineReduction(communityDetectionAlgorithms, communityAlgorithm, true, true,
						numberOfReductions, hashStorage, getStartNode(), directed);
			}

		}

	}

	/**
	 * Begin computation of child graphs ( Called when program is executed from
	 * the GUI )
	 * 
	 * @param startNodeId Node ID of start node
	 */
	private void begin(String startNodeId) {
		GraphGenerate.CURRENT_ITERATION = 0;
		if (GraphGenerate.GRAPHS.size() > 1) {
			while (GraphGenerate.GRAPHS.size() != 1) {
				GraphGenerate.GRAPHS.remove(1);
			}
		}
		// hashStorage.resetAllHashMapsExceptRootAdjacency();
		// hashStorage.clearHashMapsExceptRootAdjacency();

		hashStorage.clearHashMapsExceptRoot();

		try {

			if (ValidateCustomizeWindow.booleanAllow == false) {
				listSteps.clear();
				setupTabbedPaneForReducedGraphs();
				getAllComponents(customizeWindow);

				@SuppressWarnings("unused")
				int count_steps = 0;
				@SuppressWarnings("unused")
				Boolean clusteringFirstStep = false;
				@SuppressWarnings("unused")
				HashMap<Integer, String> mapNodesToIter = null;
				for (String step : listSteps) {

					if (step.contains(",")) {

						String[] arrCluster = step.split(",");
						String community = arrCluster[0];
						String includeCommunity = arrCluster[1];
						String reductionValue = arrCluster[2];
						int level = hashStorage.getCurrentLevel();
						int[][] adjacencyMatrix = hashStorage.getAdjacencyMatrix(level);
						communityDetectionAlgorithms.clearAllValues();
						System.out.println("Applying algorithm:" + community.trim().toUpperCase());
						switch (community.trim().toUpperCase()) {
						
						case "HIERARCHICAL CLUSTERING":
							communityDetectionAlgorithms.applyHC(adjacencyMatrix);
							break;
						case "ORIGINAL LOUVAIN":
							communityDetectionAlgorithms.applyLouvainOrSLM(
									hashStorage.getAdjacencyMatrixEdgeCount(adjacencyMatrix), adjacencyMatrix, level,
									1);

							break;
						case "LOUVAIN WITH MULTILEVEL REFINEMENT":
							communityDetectionAlgorithms.applyLouvainOrSLM(
									hashStorage.getAdjacencyMatrixEdgeCount(adjacencyMatrix), adjacencyMatrix, level,
									2);

							break;
						case "DIRECT CLUSTERING":
							communityDetectionAlgorithms.applyDirectClustering(adjacencyMatrix);
							break;

						default:
							break;

						}

						callReduction(communityDetectionAlgorithms, community, true, Boolean.valueOf(includeCommunity),
								reductionValue, hashStorage, getStartNode());

					} else {

						count_steps++;
						String reductionValue = step;
						callReduction(null, null, false, false, reductionValue, hashStorage, getStartNode());
					}
				}

				setupReducedGraphs(hashStorage, getStartNode());
				tabbedPaneGraphs.setEnabledAt(tabbedPaneGraphs.getTabCount() - 1, true);
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setup isDirected check box
	 */
	private void setupIsDirectedCheckBox() {
		checkBoxIsDirected = new JCheckBox();
		checkBoxIsDirected.setEnabled(false);
		checkBoxIsDirected.setSelected(false);
		checkBoxIsDirected.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {

					isDirected = true;
					setIsDirected(true);
				} else {
					isDirected = false;
					setIsDirected(false);

				}

			}

		});
		panelControl.add(new JLabel("Is Directed:"));
		panelControl.add(checkBoxIsDirected);
	}

	/**
	 * Setup input file chooser controls
	 */
	private void setupFileChooser() {
		buttonOpen = new JButton("Open");
		buttonOpen.setEnabled(false);
		buttonOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fileChooser = new JFileChooser();

				int returnVal = fileChooser.showOpenDialog(frameStartWindow);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();
					performCleanupOperation();
					Graph g = graphGenerate.fetchMainGraph(hashStorage, matrixOperations, file.getAbsolutePath(),
							checkBoxIsDirected.isSelected(), viewerPipe);

					setupRootGraphTab(g);

				}

			}

		});
		panelControl.add(buttonOpen);
	}

	/**
	 * Setup radio buttons for auto / normal generation of graph
	 */
	private void setupRadioButtons() {

		radioButtonFromFile = new JRadioButton("From file");
		radioButtonFromFile.setActionCommand("From file");
		radioButtonFromFile.setSelected(false);

		radioButtonAutoGenerator = new JRadioButton("Auto Generator");
		radioButtonAutoGenerator.setActionCommand("Auto Generator");
		radioButtonAutoGenerator.setSelected(true);

		radioButtonFromFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				buttonOpen.setEnabled(true);
				buttonStart.setEnabled(false);
				generatorCombo.setEnabled(false);
				checkBoxIsDirected.setEnabled(true);
				Object item = e.getActionCommand();
				generatingMethod = item.toString().toUpperCase();
			}

		});

		radioButtonAutoGenerator.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				buttonOpen.setEnabled(false);
				buttonStart.setEnabled(true);
				generatorCombo.setEnabled(true);
				checkBoxIsDirected.setSelected(false);
				checkBoxIsDirected.setEnabled(false);

				Object item = e.getActionCommand();
				generatingMethod = item.toString().toUpperCase();
			}

		});

		radioButtonGroup.add(radioButtonFromFile);
		radioButtonGroup.add(radioButtonAutoGenerator);
		panelControl.add(radioButtonFromFile);
		panelControl.add(radioButtonAutoGenerator);
	}

	/**
	 * Setup drop down for generators
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setupGeneratorComboBox() {
		for (int i = 0; i < generators.length; i++) {
			generatorNames.addElement(generators[i]);
		}

		generatorCombo = new JComboBox(generatorNames);
		generatorCombo.setSelectedIndex(0);
		setGeneratorName(getSelectedGeneratorFromComboBox());
		generatorCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				setGeneratorName(e.getItem().toString());
			}

		});

		scrollPaneGeneratorCombo = new JScrollPane(generatorCombo);
		panelControl.add(scrollPaneGeneratorCombo);

	}

	/**
	 * Setup drop down for property types
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupPropertyComboBox() {

		for (int i = 0; i < graphProperties.length; i++) {
			propertyNames.addElement(graphProperties[i]);
		}
		comboBoxPropertyNames = new JComboBox(propertyNames);
		comboBoxPropertyNames.setSelectedIndex(0);
		setSelectedPropertyName(getSelectedPropertyFromComboBox());
		// SELECTED_PROPERTY_NAME = getSelectedPropertyName();
		comboBoxPropertyNames.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				Object selectedItem = e.getItem();

				setSelectedPropertyName(selectedItem.toString());
			}

		});

		propertyNamesScrollPane = new JScrollPane(comboBoxPropertyNames);
		panelControl.add(propertyNamesScrollPane);

	}

	/**
	 * Setup generate button
	 */
	private void setupControlButtons() {

		buttonStart = new JButton("Generate");
		buttonStart.setSize(new Dimension(40, 40));
		buttonStart.setActionCommand("Start");
		buttonStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				performCleanupOperation();
				Graph g = graphGenerate.fetchMainGraph(hashStorage, matrixOperations, selectedGenerator, isDirected,
						viewerPipe);
				// results = null;
				setupRootGraphTab(g);

			}

		});

		panelControl.add(buttonStart);

	}

	/**
	 * Set isDirected
	 * 
	 * @param isDirected TRUE if graph is directed else FALSE
	 */
	private void setIsDirected(Boolean isDirected) {
		this.isDirected = isDirected;
	}

	/**
	 * Get isDirected
	 * 
	 * @return isDirected TRUE if graph is directed else FALSE
	 * 
	 *  */
	@SuppressWarnings("unused")
	private Boolean getIsDirected() {
		return isDirected;
	}

	/**
	 * Return the property type
	 * 
	 * @return Name of Property
	 */
	protected String getSelectedPropertyFromComboBox() {
		return comboBoxPropertyNames.getItemAt(comboBoxPropertyNames.getSelectedIndex()).toString();
	}

	/**
	 * Return generator
	 * 
	 * @return Name of Generator
	 */
	protected String getSelectedGeneratorFromComboBox() {
		return generatorCombo.getItemAt(generatorCombo.getSelectedIndex()).toString();
	}

	/**
	 * Generate result file
	 * 
	 * @param resultsStore Instance of ResultsStore
	 */
	public void generateResults(ResultsStore resultsStore) {
		ResultLogger resultLogger = null;
		if (stepType == null && algorithm == null) {
			resultLogger = new ResultLogger(startNode);
		} else {
			resultLogger = new ResultLogger(startNode + "-" + selectedProperty + "-" + stepType + "-" + algorithm);
		}
		System.out.println("File:" + resultLogger.getFile());

		// Generic Table
		resultLogger.createGenericDetailsTable();

		for (int i = hashStorage.getHashAdjacencyMatrix().size() - 1; i >= 0; i--) {
			int[][] am = hashStorage.getAdjacencyMatrix(i);
			int len = am.length;
			if (len > 0) {
				resultLogger.writeToLoggerGenericDetails(i + "," + len);
			}
		}

		resultLogger.closeTableWriter();
		// Source Node Details
		if (file == null) {
			resultLogger.createSourceNodeTableDetails(startNode, selectedProperty, "Generator");

		} else {
			resultLogger.createSourceNodeTableDetails(startNode, selectedProperty, file.getAbsolutePath());

		}
		resultLogger.createNodeDetailsTable();

		// Even though you are printing details of final level node,
		// You should fetch the root graph's details for results
		resultLogger.writeToLoggerFinalGraphDetails(
				getStartNode() + "," + resultsStore.getDcMap().get(Integer.parseInt(getStartNode())) + ","
						+ resultsStore.getcCoeffMap().get(Integer.parseInt(getStartNode())) + ","
						+ resultsStore.getBcMap().get(Integer.parseInt(getStartNode())) + ","
						+ resultsStore.getPrMap().get(Integer.parseInt(getStartNode())) + ","
						+ resultsStore.getCcMap().get(Integer.parseInt(getStartNode())));

		resultLogger.closeTableWriter();
		// Final Graph Details
		resultLogger.createNodeDetailsTable();

		for (String node : resultsStore.getArrNodes()) {
			resultLogger.writeToLoggerFinalGraphDetails(node + "," + resultsStore.getDcMap().get(Integer.parseInt(node))
					+ "," + resultsStore.getcCoeffMap().get(Integer.parseInt(node)) + ","
					+ resultsStore.getBcMap().get(Integer.parseInt(node)) + ","
					+ resultsStore.getPrMap().get(Integer.parseInt(node)) + ","
					+ resultsStore.getCcMap().get(Integer.parseInt(node)));

		}
		resultLogger.closeTableWriter();
		resultLogger.createStepsTable();
		if (listSteps.size() == 0) {
			if (stepType.toUpperCase().equals("S1")) {
				resultLogger.writeStepsToLogger("No of reductions:" + commandlineReductions);
			} else {
				resultLogger.writeStepsToLogger("Algorithm:" + algorithm + "," + "No of reductions:"
						+ commandlineReductions + "," + "No of iterations:" + numberOfIterationsForClusteringStep);
			}

			try {
				String tempFileName;
				if (stepType.toUpperCase().equals("S1"))
					tempFileName = startNode + "-" + selectedProperty + "-" + stepType;
				else {
					tempFileName = startNode + "-" + selectedProperty + "-" + stepType + "-" + algorithm;
				}

				results.createResultsScreenshot(
						graphGenerateCommandline.getGraphs().get(graphGenerateCommandline.getGraphs().size() - 1),
						results.getFileName(tempFileName));
				System.out.println("EdgeList:" + results.getFile());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			for (int i = 0; i < listSteps.size(); i++) {
				resultLogger.writeStepsToLogger(listSteps.get(i));
			}

			try {
				results.createResultsScreenshot(GraphGenerate.GRAPHS.get(GraphGenerate.GRAPHS.size() - 1),
						results.getFileName(startNode));
				System.out.println("EdgeList:" + results.getFile());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		resultLogger.closeTableWriter();
		resultLogger.closeLoggingGeneration();

	}

	/**
	 * Return all components of steps window
	 * 
	 * @param c Instance of Contaoner
	 */
	public void getAllComponents(final Container c) {
		Component[] comps = c.getComponents();
		for (Component comp : comps) {
			if (comp instanceof JPanel) {
				if (((JPanel) comp).getToolTipText() != null) {
					JPanel panel = (JPanel) comp;
					if (panel.getToolTipText().trim().toUpperCase().equals("REDUCE")) {
						Component[] reduceComp = panel.getComponents();
						for (Component reducePanelComps : reduceComp) {
							if (reducePanelComps instanceof JTextField) {
								JTextField textFieldReduce = (JTextField) reducePanelComps;
								listSteps.add(textFieldReduce.getText().trim());
							}

						}
					} else if (panel.getToolTipText().trim().toUpperCase().equals("CLUSTER AND REDUCE")) {
						String tempValue = "";
						Component[] clusterAndReduceComp = panel.getComponents();
						for (Component clusterPanelComps : clusterAndReduceComp) {
							if (clusterPanelComps instanceof JScrollPane) {
								Component[] scrollPaneComps = ((JScrollPane) clusterPanelComps).getComponents();

								for (Component viewport : scrollPaneComps) {
									if (viewport instanceof JViewport) {
										@SuppressWarnings("unused")
										JViewport viewPort = (JViewport) viewport;
										Component[] viewComps = ((JViewport) viewport).getComponents();
										for (Component combo : viewComps) {
											if (combo instanceof JComboBox) {
												@SuppressWarnings("rawtypes")
												JComboBox comboBox = (JComboBox) combo;
												tempValue = comboBox.getSelectedItem().toString() + ",";

											}
										}
									}

								}

							} else if (clusterPanelComps instanceof JCheckBox) {
								JCheckBox checkBox = (JCheckBox) clusterPanelComps;
								tempValue = tempValue + String.valueOf(checkBox.isSelected()) + ",";
							} else if (clusterPanelComps instanceof JTextField) {
								JTextField textField = (JTextField) clusterPanelComps;
								tempValue = tempValue + textField.getText();
							}
						}
						listSteps.add(tempValue);
					}

				}
			}

			// compList.add(comp);
			if (comp instanceof Container) {
				getAllComponents((Container) comp);
			}
		}

	}

	/**
	 * Return community name based on index
	 * 
	 * @return Name of Community Detection algorithm
	 */
	public String getSelectedCommunityName() {
		return communityCombo.getItemAt(communityCombo.getSelectedIndex()).toString();
	}

	/**
	 * Call reduction operation when program is executed from command line
	 * 
	 * @param communityDetection Instance of CommunityDetectionAlgorithms
	 * @param communityAlgorithm Name of community algorithm
	 * @param booleanClustering TRUE if graph is directed else FALSE
	 * @param includeNativeCommunity TRUE
	 * @param numberOfReductions Number of reductions
	 * @param hashStorage Instance of HashStorage
	 * @param startNode Node ID of start node
	 * @param directed TRUE if graph is directed else FALSE
	 */

	private void callCommandLineReduction(CommunityDetectionAlgorithms communityDetection, String communityAlgorithm,
			Boolean booleanClustering, Boolean includeNativeCommunity, String numberOfReductions,
			HashStorage hashStorage, String startNode, Boolean directed) {

		graphGenerate.setNumberOfReductions(Integer.parseInt(numberOfReductions));

		graphGenerate.invokeReductionCalls(startNode, booleanClustering, includeNativeCommunity, communityDetection,
				communityAlgorithm, getSelectedPropertyName(), hashStorage, directed);
	}

	/**
	 * Call reduction operation when program is executed from GUI
	 * 
	 * @param communityDetection Instance of CommunityDetectionAlgorithms
	 * @param communityAlgorithm Type of Community Algorithm
	 * @param booleanClustering TRUE if clustering else FALSE
	 * @param includeNativeCommunity TRUE
	 * @param numberOfReductions Number of reductions
	 * @param hashStorage Instance of HashStorage
	 * @param startNode Node ID of start node
	 */
	private void callReduction(CommunityDetectionAlgorithms communityDetection, String communityAlgorithm,
			Boolean booleanClustering, Boolean includeNativeCommunity, String numberOfReductions,
			HashStorage hashStorage, String startNode) {

		graphGenerate.setNumberOfReductions(Integer.parseInt(numberOfReductions));

		graphGenerate.invokeReductionCalls(startNode, booleanClustering, includeNativeCommunity, communityDetection,
				communityAlgorithm, getSelectedPropertyName(), hashStorage, checkBoxIsDirected.isSelected());

		tempViewer.clear();
		// pipe.clear();
		// view.clear();

	}

	/**
	 * Set start node
	 * 
	 * @param startNode Node ID of start node
	 */
	private void setStartNode(String startNode) {
		Integer node = hashStorage.getNodeId(0, Integer.parseInt(startNode));
		this.startNode = String.valueOf(node);
	}

	/**
	 * Return start node
	 * 
	 * @return start node
	 */

	protected String getStartNode() {
		return startNode;
	}

	/**
	 * Set show reduced graphs
	 * 
	 * @param showReducedGraphs TRUE if reduced graphs are to be shown else FALSE
	 */
	private void setShowReducedGraphs(Boolean showReducedGraphs) {
		this.showReducedGraphs = showReducedGraphs;
	}

	/**
	 * Get value of show reduced graphs
	 * 
	 * @return TRUE if reduced graphs are to be shown else FALSE
	 */
	protected Boolean getShowReducedGraphs() {
		return showReducedGraphs;
	}

	/**
	 * Set selected property name
	 * 
	 * @param selectedProperty Graph-Theoretic Property or Centrality
	 */
	private void setSelectedPropertyName(String selectedProperty) {
		this.selectedProperty = selectedProperty;
	}

	/**
	 * Get selected property name
	 * 
	 * @return selectedproperty Graph-Theoretic Property or Centrality
	 */
	protected String getSelectedPropertyName() {
		return selectedProperty;
	}

	/**
	 * Set type of auto generator
	 * 
	 * @param selectedGenerator Generator Name
	 */
	private void setGeneratorName(String selectedGenerator) {
		this.selectedGenerator = selectedGenerator;
	}

	/**
	 * Get type of auto generator
	 * 
	 * @return selectedGenerator Generator Name
	 */
	protected String getSelectedGeneratorName() {
		return selectedGenerator;
	}

	/**
	 * Set clustering to true or false
	 * 
	 * @param booleanClustering TRUE if clustering else FALSE
	 */
	@SuppressWarnings("unused")
	private void setClustering(Boolean booleanClustering) {
		this.booleanClustering = booleanClustering;
	}

	/**
	 * Get value of clustering
	 * 
	 * @return booleanclustering
	 */

	protected Boolean getClustering() {
		return booleanClustering;
	}

	/**
	 * Set Include native community
	 * 
	 * @param booleanIncludeNativeCommunity TRUE
	 */
	@SuppressWarnings("unused")
	private void setIncludeNativeCommunity(Boolean booleanIncludeNativeCommunity) {
		this.booleanIncludeNativeCommunity = booleanIncludeNativeCommunity;
	}

	/**
	 * Get include native community
	 * 
	 * @return includeNativeCommunity
	 */
	protected Boolean getIncludeNativeCommunity() {
		return booleanIncludeNativeCommunity;
	}

	/**
	 * Setup panel for reduced graphs
	 * 
	 */
	private void setupReducedGraphLayout() {
		panelForReducedGraphs = new JPanel();
		panelForReducedGraphs.setBackground(Color.white);
		GridBagLayout gridBagLayout = new GridBagLayout();
		panelForReducedGraphs.setLayout(gridBagLayout);
		panelForReducedGraphs.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		scrollPaneReducedGraphs = new JScrollPane(panelForReducedGraphs);
		scrollPaneReducedGraphs.setMinimumSize(new Dimension(160, 600));

		scrollPaneReducedGraphs.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPaneReducedGraphs.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

	}

	/**
	 * Setup tabs for reduced graphs
	 * 
	 */
	private void setupTabbedPaneForReducedGraphs() {
		setupReducedGraphLayout();
		tabbedPaneGraphs.add("Graph Grid", scrollPaneReducedGraphs);
		tabbedPaneGraphs.setSelectedIndex(tabbedPaneGraphs.getTabCount() - 1);

	}

	/**
	 * Create all reduced graphs
	 * 
	 * @param hashStorage Instance of HashStorage
	 * @param startNode Node ID of start node
	 */
	private void setupReducedGraphs(HashStorage hashStorage, String startNode) {
		GraphGenerate.createAllReducedGraphs(hashStorage, startNode, checkBoxIsDirected.isSelected());
		repaintGridReducedGraphs(startNode);

	}

	/**
	 * Paint all reduced graphs
	 * 
	 * @param startNode Node ID of start node
	 */
	private void repaintGridReducedGraphs(String startNode) {
		tempViewer.clear();
		for (int i = 1; i <= GraphGenerate.GRAPHS.size() - 1; i++) {
			if (GraphGenerate.GRAPHS.get(i).getNodeCount() > 0) {
				tempViewer.add(new Viewer(GraphGenerate.GRAPHS.get(i), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD));
				tempViewer.get(i - 1).enableAutoLayout();
			}
		}

		int row = 0, col = 0;
		for (int i = GraphGenerate.GRAPHS.size() - 1; i > 0; i--) {
			if (panelForReducedGraphs != null) {
				if (GraphGenerate.GRAPHS.get(i).getNodeCount() > 0) {
					JPanel tempPanel = new JPanel();

					tempPanel.setLayout(new BorderLayout());
					tempPanel.setPreferredSize(new Dimension(300, 300));
					GridBagConstraints gridBagConstraints = new GridBagConstraints();
					gridBagConstraints.fill = GridBagConstraints.BOTH;

					gridBagConstraints.gridx = col;
					gridBagConstraints.gridy = row;
					tempPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
					tempPanel.add(new JLabel("Level:" + i), BorderLayout.PAGE_START);
					tempPanel.add(tempViewer.get(i - 1).addDefaultView(false), BorderLayout.CENTER);
					panelForReducedGraphs.add(tempPanel, gridBagConstraints);
					col++;
					if (col == 2) {
						col = 0;
						row++;
					}

				}
			}

		}

		panelForReducedGraphs.revalidate();
		panelForReducedGraphs.updateUI();

		results = new ResultsStore();
		try {
			results.setupDataForResults(hashStorage, selectedProperty, startNode, checkBoxIsDirected.isSelected());
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		generateResults(results);
	}

	/**
	 * Program begins from here.
	 * 
	 * @param args Command line arguments
	 */

	public static void main(String[] args) {
	
		if (args.length == 0) {
			StartWindow startWindow = new StartWindow();
			startWindow.startUI();
		} else {
			
			if (args.length == 6) {
				String filename = args[0];
				String propertyType = args[1];
				String stepType = args[2];
				if (stepType.toUpperCase().equals("S1")) {
					String reductions = args[3];
					String randomNumbers = args[4];
					String directed = args[5];
					String[] arrRandomNumbers = randomNumbers.split(",");
					String algorithm = null;
					for (int i = 0; i < arrRandomNumbers.length; i++) {
						// StartWindow startWindow = new StartWindow();
						Thread t = new Thread(new StartWindow(filename, reductions, arrRandomNumbers[i], directed,
								propertyType, stepType, algorithm));
						t.start();
						// startWindow.startExecutionWithoutUI(filename,
						// reductions, arrRandomNumbers[i], directed);

					}
				} else {
					System.out.println("Enter the correct set of arguments.");
				}
			} else if (args.length == 7) {
				String filename = args[0];
				String propertyType = args[1];
				String algorithm = args[2];
				String stepType = args[3];
				// if(!startWindow.getTypeOfCommunityAlgorithm(algorithm).equals(null)
				// && stepType.toUpperCase().equals("S2")){
				if (stepType.toUpperCase().equals("S2")) {
					String reductions = args[4];
					String randomNumbers = args[5];
					String directed = args[6];
					String[] arrRandomNumbers = randomNumbers.split(",");
					
					for (int i = 0; i < arrRandomNumbers.length; i++) {

						Thread t = new Thread(new StartWindow(filename, reductions, arrRandomNumbers[i], directed,
								propertyType, stepType, algorithm));
						t.start();
					}
				} else {
					System.out.println("Enter the correct set of arguments.");
				}

			} else {
				System.out.println("Enter the correct set of arguments.");
			}
		}

	}

	/**
	 * Run method
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		startExecutionWithoutUI(filename, commandlineReductions, startNode, directedGraph, selectedProperty, stepType,
				algorithm);

	}
	
	/**
	 * Calculates time taken to generate final graph
	 * @param millisecs Time in milliseconds
	 */
	public void calculateTimeForGeneration(long millisecs){
		
		int sec = (int) (millisecs / 1000) % 60 ;
		int min = (int) ((millisecs / (1000*60)) % 60);
		int hrs   = (int) ((millisecs / (1000*60*60)) % 24);
		try {
			PrintWriter writer = new PrintWriter("Time-"+startNode + "-" +selectedProperty+ "-" 
						+(String) algorithm + "-"+stepType + ".txt" );
			writer.println("Time Taken(hrs:mins:secs:millisecs)="+hrs+":"+min+":"+
						sec+":"+millisecs);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	
	/**
	 * Calculates memory consumption
	 * Adapted from http://www.vogella.com/tutorials/JavaPerformance/article.html
	 */
	public void calculateMemoryConsumption(){
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long memoryConsumption = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Bytes:"+memoryConsumption+","
				+ " Megabytes:"+memoryConsumption/(1024L * 1024L));
		try {
			PrintWriter writer = new PrintWriter("Memory-"+startNode + "-" +selectedProperty+ "-" 
						+(String) algorithm + "-"+stepType + ".txt" );
			writer.println("Memory Consumption - "+"Bytes:"+memoryConsumption+","
					+ " Megabytes:"+memoryConsumption/(1024L * 1024L));
			writer.close();
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
