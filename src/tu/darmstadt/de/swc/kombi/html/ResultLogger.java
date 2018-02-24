package tu.darmstadt.de.swc.kombi.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log results as html report
 * 
 * @author suhas
 *
 */
public class ResultLogger {

	private String fileName = "";
	private PrintWriter fLogger = null;
	static String[] genericTableHeaders = { "Graph Level", "No of nodes" };

	static String[] finalNodeTableHeaders = { "Node ID", "Degree", "Clustering Coefficient", "Betweenness Centrality",
			"PageRank", "Closeness Centrality" };

	static String[] stepsTableHeaders = { "Technique" };

	public ResultLogger(String startNode) {
		fileName = getFileName(startNode);

		if (!fileName.equals(null) && fileName.trim().length() > 0) {
			try {
				fLogger = new PrintWriter(fileName, "UTF-8");
				fLogger.println("<!DOCTYPE html>");
				fLogger.println("<html><head><center><h1>Test Results</h1></center></head>" + "<body><center>");
				fLogger.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get html report file name
	 * 
	 * @param startNode Node ID of start node
	 * @return name of html report file
	 */
	public String getFileName(String startNode) {
		String sFileName = null;
		Date dateNow = new Date();
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");

		sFileName = formatDate.format(dateNow).toString();

		sFileName = "StartNode-" + startNode + "-" + sFileName + ".html";

		return sFileName;
	}

	/**
	 * Get filename
	 * 
	 * @return file name
	 */
	public String getFile() {
		return fileName;
	}

	/**
	 * Call generic table
	 */
	public void createGenericDetailsTable() {
		createTableInLogger(genericTableHeaders);
	}

	/**
	 * Call final table
	 */

	public void createNodeDetailsTable() {
		createTableInLogger(finalNodeTableHeaders);
	}

	/**
	 * Call steps table
	 */
	public void createStepsTable() {
		createTableInLogger(stepsTableHeaders);
	}

	/**
	 * Create table data
	 *  
	 * @param message Content of the result
	 */
	public void writeToLoggerGenericDetails(String message) {
		// 1 - Graph Level
		// 2 - Total No of nodes
		if (message.length() > 0) {
			try {
				fLogger = new PrintWriter(new FileOutputStream(new File(fileName), true));
				String[] arrMessage = message.split(",");
				fLogger.println("<tr>");
				for (String m : arrMessage) {
					fLogger.println("<td>" + m + "</td>");
				}
				fLogger.println("</tr>");
				fLogger.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * Create table headers
	 * 
	 * @param tableHeaders Array of Table headers
	 */
	public void createTableInLogger(String[] tableHeaders) {
		try {
			fLogger = new PrintWriter(new FileOutputStream(new File(fileName), true));
			fLogger.println("<br /><table border='1' cellpadding='1'>");
			for (int i = 0; i < tableHeaders.length; i++) {
				fLogger.println("<th>" + tableHeaders[i] + "</th>");
			}
			fLogger.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create steps
	 * 
	 * @param message Message to be written
	 */
	public void writeStepsToLogger(String message) {
		try {
			fLogger = new PrintWriter(new FileOutputStream(new File(fileName), true));
			String[] arrMessage = message.split(",");
			for (String s : arrMessage) {
				fLogger.println("<tr>");
				fLogger.println("<td>" + s + "</td>");
				fLogger.println("</tr>");
			}
			fLogger.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Close table tag
	 */
	public void closeTableWriter() {
		try {
			fLogger = new PrintWriter(new FileOutputStream(new File(fileName), true));
			fLogger.println("</table>");
			fLogger.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Write steps
	 * 
	 * @param message Message to be written
	 */
	public void writeToLoggerFinalGraphDetails(String message) {

		try {
			fLogger = new PrintWriter(new FileOutputStream(new File(fileName), true));
			String[] arrMessage = message.split(",");
			fLogger.println("<tr>");
			for (String s : arrMessage) {
				fLogger.println("<td>" + s + "</td>");
			}
			fLogger.println("</tr>");
			fLogger.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Details of source node
	 * 
	 * @param source Source nodde
	 * @param selectedProperty Graph-Theoretic Property or Centrality
	 * @param filename Path to file name
	 */
	public void createSourceNodeTableDetails(String source, String selectedProperty, String filename) {
		try {
			fLogger = new PrintWriter(new FileOutputStream(new File(fileName), true));
			fLogger.println("<p><h2>Source Node:" + source + "</h2></p>");
			fLogger.println("<p><h2>Selected Property:" + selectedProperty + "</h2></p>");
			fLogger.println("<p><h3>FileName:" + filename + "</h3></p>");
			fLogger.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close html tags
	 */

	public void closeLoggingGeneration() {
		try {
			fLogger = new PrintWriter(new FileOutputStream(new File(fileName), true));
			fLogger.println("</center></body></html>");
			fLogger.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
