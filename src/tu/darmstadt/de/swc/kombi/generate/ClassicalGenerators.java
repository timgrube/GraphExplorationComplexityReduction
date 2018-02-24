package tu.darmstadt.de.swc.kombi.generate;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;

/**
 * Auto Generators
 * 
 * @author suhas
 *
 */
public class ClassicalGenerators {

	private int totalNodes = 1000;
	Generator generator;

	/**
	 * Dorogovtsev Mendes Generator
	 */
	public void applyDorogovtsevMendes() {
		generator = new DorogovtsevMendesGenerator();
		generator.addSink(GraphGenerate.ROOT_GRAPH);
		((BaseGenerator) generator).addNodeLabels(true);
		generator.begin();

		for (int i = 0; i < totalNodes; i++) {
			generator.nextEvents();
		}
		generator.end();
	}

	/**
	 * Barabasi Albert Generator
	 */
	public void applyBarabasiAlbert() {
		generator = new BarabasiAlbertGenerator(2);
		// barabasiAlbert.setDirectedEdges(true, false);

		generator.addSink(GraphGenerate.ROOT_GRAPH);
		((BaseGenerator) generator).addNodeLabels(true);
		generator.begin();

		for (int i = 0; i < totalNodes; i++) {
			generator.nextEvents();

		}

		generator.end();
	}

	/**
	 * Watts Strogatz Generator
	 */
	public void applySmallWorldWattsStrogatz() {
		generator = new WattsStrogatzGenerator(totalNodes, 2, 0.5);
		generator.addSink(GraphGenerate.ROOT_GRAPH);
		generator.begin();
		while (generator.nextEvents()) {
		}
		generator.end();
	}

}
