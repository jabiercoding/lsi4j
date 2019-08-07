package lsi4j;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;

/**
 * Controlability example
 * 
 * http://www.cs.haifa.ac.il/~rita/uml_course/lectures/LSI.pdf
 * 
 * @author Nicolas Ordonez Chala
 */
class ControlabillityExampleTest {

	// Set query
	private static List<String> query;

	// Set documents
	private static List<List<String>> documents;

	@Before
	public void setUp() {

		query = new ArrayList<String>();
		documents = new ArrayList<List<String>>();
		query.add("feedback");
		query.add("controller");

		// Set document number 1
		List<String> ch2 = new ArrayList<String>();
		ch2.add("controllability");
		ch2.add("observability");
		ch2.add("realization");

		// Set document number 2
		List<String> ch3 = new ArrayList<String>();
		ch3.add("controllability");
		ch3.add("feedback");
		ch3.add("controller");
		ch3.add("observer");

		// Set document number 3
		List<String> ch4 = new ArrayList<String>();
		ch4.add("realization");
		ch4.add("observer");

		// Set document number 4
		List<String> ch5 = new ArrayList<String>();

		// Set document number 5
		List<String> ch6 = new ArrayList<String>();
		ch6.add("controllability");
		ch6.add("observability");
		ch6.add("realization");
		ch6.add("controller");
		ch6.add("observer");
		ch6.add("transferfunction");
		ch6.add("polynomial");
		ch6.add("matrices");

		// Set document number 6
		List<String> ch7 = new ArrayList<String>();
		ch7.add("observability");
		ch7.add("feedback");
		ch7.add("controller");
		ch7.add("observer");
		ch7.add("transferfunction");

		// Set document number 7
		List<String> ch8 = new ArrayList<String>();
		ch8.add("realization");
		ch8.add("polynomial");
		ch8.add("matrices");

		// Set document number 8
		List<String> ch9 = new ArrayList<String>();
		ch9.add("controllability");
		ch9.add("observability");
		ch9.add("matrices");

		// Add documents to the set of documents
		documents.add(ch2);
		documents.add(ch3);
		documents.add(ch4);
		documents.add(ch5);
		documents.add(ch6);
		documents.add(ch7);
		documents.add(ch8);
		documents.add(ch9);
	}

	@Test
	void controlabillityApplyLSIwithInstance() {
		// Step 0 Set Up the variables
		setUp();

		// Step 1 Get similarity between the query and the documents
		LSI4J lsiTechnique = new LSI4J(documents, LSI4J.APPROXIMATION_K_VALUE, 2, LSI4J.SORT_NONE);
		double[] answer = lsiTechnique.applyLSI(query);

		// Step 2 Show answer and their content round to 4 decimals
		System.out.println(" --------- Controlabillity Example: FIXED AND PREDIFINED SORT --------- ");
		for (double d : answer) {
			System.out.print(d + " = ");
			System.out.println(setNumberOfDecimals(d, SCALE));
		}

		// Step 3 Compare answer with their expect result with an accepted error of +-
		// 0.001
		assertEquals(-0.3747, setNumberOfDecimals(answer[0], SCALE), 0.001);
		assertEquals(0.9671, setNumberOfDecimals(answer[1], SCALE), 0.001);
		assertEquals(0.1735, setNumberOfDecimals(answer[2], SCALE), 0.001);
		assertEquals(-0.9413, setNumberOfDecimals(answer[3], SCALE), 0.06);
		assertEquals(0.0851, setNumberOfDecimals(answer[4], SCALE), 0.001);
		assertEquals(0.9642, setNumberOfDecimals(answer[5], SCALE), 0.001);
		assertEquals(-0.7265, setNumberOfDecimals(answer[6], SCALE), 0.001);
		assertEquals(-0.3805, setNumberOfDecimals(answer[7], SCALE), 0.001);
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
