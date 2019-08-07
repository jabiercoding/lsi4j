package lsi4j;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Human Interfaces Example
 * 
 * http://www-db.deis.unibo.it/courses/SI-M/slides/03.2.LSI.pdf
 * 
 * @author Nicolas Ordonez Chala
 */
class HumanInterfacesExampleTest {

	// Set query
	private static List<String> query;

	// Set documents
	private static List<List<String>> documents;

	public void setUp() {

		query = new ArrayList<String>();
		documents = new ArrayList<List<String>>();
		query.add("human");
		query.add("computer");
		query.add("interaction");

		// Set document number 1
		List<String> c1 = new ArrayList<String>();
		c1.add("human");
		c1.add("interface");
		c1.add("computer");

		// Set document number 2
		List<String> c2 = new ArrayList<String>();
		c2.add("computer");
		c2.add("user");
		c2.add("system");
		c2.add("response");
		c2.add("time");
		c2.add("survey");

		// Set document number 3
		List<String> c3 = new ArrayList<String>();
		c3.add("interface");
		c3.add("user");
		c3.add("system");
		c3.add("EPS");

		// Set document number 4
		List<String> c4 = new ArrayList<String>();
		c4.add("human");
		c4.add("system");
		c4.add("system");
		c4.add("EPS");

		// Set document number 5
		List<String> c5 = new ArrayList<String>();
		c5.add("user");
		c5.add("response");
		c5.add("time");

		// Set document number 6
		List<String> m1 = new ArrayList<String>();
		m1.add("tree");

		// Set document number 7
		List<String> m2 = new ArrayList<String>();
		m2.add("tree");
		m2.add("graph");

		// Set document number 8
		List<String> m3 = new ArrayList<String>();
		m3.add("tree");
		m3.add("graph");
		m3.add("minors");

		// Set document number 9
		List<String> m4 = new ArrayList<String>();
		m4.add("survey");
		m4.add("graph");
		m4.add("minors");

		// Add documents to the set of documents
		documents.add(c1);
		documents.add(c2);
		documents.add(c3);
		documents.add(c4);
		documents.add(c5);
		documents.add(m1);
		documents.add(m2);
		documents.add(m3);
		documents.add(m4);
	}

	@Test
	void humanInterfaceApplyLSIwithFixedAndPredefinedSort() {
		// Step 0 Set Up the variables
		setUp();

		// Step 1 Get similarity between the query and the documents
		LSI4J lsiTechnique = new LSI4J(documents, LSI4J.APPROXIMATION_K_VALUE, 2.0);
		double[] answer = lsiTechnique.applyLSI(query);

		// Step 2 Show answer and their content round to 4 decimals
		System.out.println("------------- HumanInterface Example: FIXED and PREDIFINED -------------");
		for (double d : answer) {
			System.out.print(d + " = ");
			System.out.println(setNumberOfDecimals((d + 1) / 2, SCALE));
		}

		// Step 3 Round to SCALE decimal positions
		assertTrue((setNumberOfDecimals(answer[0], SCALE)) >= (setNumberOfDecimals(answer[3], SCALE)));
		assertTrue((setNumberOfDecimals(answer[2], SCALE)) >= (setNumberOfDecimals(answer[3], SCALE)));
		assertTrue((setNumberOfDecimals(answer[3], SCALE)) >= (setNumberOfDecimals(answer[1], SCALE)));
		assertTrue((setNumberOfDecimals(answer[1], SCALE)) >= (setNumberOfDecimals(answer[4], SCALE)));
		assertTrue((setNumberOfDecimals(answer[4], SCALE)) >= (setNumberOfDecimals(answer[8], SCALE)));
		assertTrue((setNumberOfDecimals(answer[8], SCALE)) >= (setNumberOfDecimals(answer[7], SCALE)));
		assertTrue((setNumberOfDecimals(answer[7], SCALE)) >= (setNumberOfDecimals(answer[6], SCALE)));
		assertTrue((setNumberOfDecimals(answer[6], SCALE)) >= (setNumberOfDecimals(answer[5], SCALE)));
	}

	@Test
	void humanInterfaceApplyLSIwithoutLowerRankAproximation() {
		// Step 0 Set Up the variables
		setUp();

		// Step 1 Get similarity between the query and the documents
		LSI4J lsiTechnique = new LSI4J(documents, LSI4J.APPROXIMATION_K_VALUE, 2.0);
		double[] answer = lsiTechnique.applyLSI(query);

		// Step 2 Show answer and their content round to 4 decimals
		System.out.println("------------- HumanInterface Example: Withouot Lower Rank K Value -------------");
		for (double d : answer) {
			System.out.print(d + " = ");
			System.out.println(setNumberOfDecimals((d + 1) / 2, SCALE));
		}

		// Step 3 Round to SCALE decimal positions
		assertTrue((setNumberOfDecimals(answer[0], SCALE)) >= (setNumberOfDecimals(answer[3], SCALE)));
		assertTrue((setNumberOfDecimals(answer[2], SCALE)) >= (setNumberOfDecimals(answer[3], SCALE)));
		assertTrue((setNumberOfDecimals(answer[3], SCALE)) >= (setNumberOfDecimals(answer[1], SCALE)));
		assertTrue((setNumberOfDecimals(answer[1], SCALE)) >= (setNumberOfDecimals(answer[4], SCALE)));
		assertTrue((setNumberOfDecimals(answer[4], SCALE)) >= (setNumberOfDecimals(answer[8], SCALE)));
		assertTrue((setNumberOfDecimals(answer[8], SCALE)) >= (setNumberOfDecimals(answer[7], SCALE)));
		assertTrue((setNumberOfDecimals(answer[7], SCALE)) >= (setNumberOfDecimals(answer[6], SCALE)));
		assertTrue((setNumberOfDecimals(answer[6], SCALE)) >= (setNumberOfDecimals(answer[5], SCALE)));
	}
	
	/**
	 * Delete extra decimals e.g.1 input = 0.10000000 - number of decimals = 3 -
	 * output: 0.1 e.g.2 input = 0.10100001 - number of decimals = 3 - output: 0.101
	 * 
	 * @param input
	 *            - Double number
	 * @param scale
	 *            - Number of decimals
	 * @return double with scale decimal positions
	 */
	public static double setNumberOfDecimals(double input, int scale) {
		double output = Double.parseDouble("" + BigDecimal.valueOf(input).setScale(scale, BigDecimal.ROUND_HALF_UP));
		return output;
	}

	private final static int SCALE = 4;

}
