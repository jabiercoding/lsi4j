package lsi4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;

/**
 * Golden Truck Example
 * 
 * http://www1.se.cuhk.edu.hk/~seem5680/lecture/LSI-Eg.pdf
 * 
 * @author Nicolas Ordonez Chala
 */
class GoldenTruckExampleTest {

	// Set query
	private static List<String> query;

	// Set documents
	private static List<List<String>> documents;

	@Before
	public void setUp() {
		query = new ArrayList<String>();
		documents = new ArrayList<List<String>>();
		query.add("gold");
		query.add("silver");
		query.add("truck");

		// Set document number 1
		List<String> d1 = new ArrayList<String>();
		d1.add("shipment");
		d1.add("of");
		d1.add("gold");
		d1.add("damaged");
		d1.add("in");
		d1.add("a");
		d1.add("fire");

		// Set document number 2
		List<String> d2 = new ArrayList<String>();
		d2.add("delivery");
		d2.add("of");
		d2.add("silver");
		d2.add("arrived");
		d2.add("in");
		d2.add("a");
		d2.add("silver");
		d2.add("truck");

		// Set document number 3
		List<String> d3 = new ArrayList<String>();
		d3.add("shipment");
		d3.add("of");
		d3.add("gold");
		d3.add("arrived");
		d3.add("in");
		d3.add("a");
		d3.add("truck");

		// Add documents to the set of documents
		documents.add(d1);
		documents.add(d2);
		documents.add(d3);
		
		System.out.println("Documents");
		for(List<String> document: documents) {
			System.out.println(document);
		}
		System.out.println("Query");
		System.out.println(query);
	}

	@Test
	void goldenTruckApplyLSIwithoutLowerRankAproximation() {
		System.out.println("U " + -1 / Math.pow(10, 4));
		// Step 0 Set Up the variables
		setUp();

		// Step 1 Get similarity between the query and the documents
		LSI4J lsiTechnique = new LSI4J(documents);
		double answer[] = lsiTechnique.applyLSI(query);

		// Step 2 Show answer and their content round to 4 decimals
		System.out.println("GoldenTruck Example: Withouot Lower Rank K Value");
		for (double d : answer) {
			System.out.print(d + " = ");
			System.out.println(setNumberOfDecimals(d, SCALE));
		}

		// Step 3 The order supposed to be the same whatever was the k
		assertTrue(setNumberOfDecimals(answer[1], SCALE) > (setNumberOfDecimals(answer[2], SCALE)));
		assertTrue(setNumberOfDecimals(answer[2], SCALE) > (setNumberOfDecimals(answer[0], SCALE)));
	}

	@Test
	void goldenTruckApplyLSIwithK2() {
		System.out.println("\nGoldenTruck Example: FIXED K = 2");
		// Step 0 Set Up the variables
		setUp();

		// Step 1 Get similarity between the query and the documents
		LSI4J lsiTechnique = new LSI4J(documents, LSI4J.APPROXIMATION_K_VALUE, 2, LSI4J.SORT_ASCENDING);
		System.out.println("Different terms in the documents");
		List<String> allTerms = lsiTechnique.getDifferentTerms(documents, false);
		System.out.println(allTerms);
		System.out.println("\nDocuments Matrix");
		double[][] matrix = lsiTechnique.createDoubleMatrixFromDocuments(allTerms, documents, false);
		for(int i=0; i<matrix.length; i++) {
			for(int j=0; j<matrix[i].length; j++) {
				System.out.print((int) matrix[i][j] + " ");
			}
			System.out.println(allTerms.get(i));
		}
		System.out.println("\nQuery Matrix");
		double[] queryw = lsiTechnique.createDoubleQueryFromList(allTerms, query, false);
		for(int i=0; i<queryw.length; i++) {
			System.out.println((int) queryw[i] + " " + allTerms.get(i));
		}
		
		double answer[] = lsiTechnique.applyLSI(query);

		// Step 2 Show answer and their content round to 4 decimals
		System.out.println("\nSimilarity query -> each document");
		for (double d : answer) {
			System.out.println(setNumberOfDecimals(d, SCALE));
		}

		// Step 3 Compare answer with their expect result with an accepted error of +-
		// 0.001
		assertEquals(-0.0541, setNumberOfDecimals(answer[0], SCALE), 0.001);
		assertEquals(0.9910, setNumberOfDecimals(answer[1], SCALE), 0.001);
		assertEquals(0.4478, setNumberOfDecimals(answer[2], SCALE), 0.001);
	}

	@Test
	void cosine() {
		
		// Step 0 Set Up the variables
		// 0.1 Query
		double q[] = { -0.2140, -0.1821 };

		// 0.2 Documents
		double d1[] = { -0.4945, 0.6492 };
		double d2[] = { -0.6458, -0.7194 };
		double d3[] = { -0.5817, 0.2469 };

		// Step 1 Get cosine distance
		double answer[] = { LSI4J.cosine(q, d1, true, 4), LSI4J.cosine(q, d2, true, 4), LSI4J.cosine(q, d3, true, 4) };

		// Step 2 Show answer and their content round to 4 decimals
		System.out.println("Test: cosine");
		for (double d : answer) {
			System.out.print(d + " = ");
			System.out.println(setNumberOfDecimals(d, SCALE));
		}

		// Step 3 Compare answer with their expect result with an accepted error of +-
		// 0.001
		assertEquals(-0.0541, setNumberOfDecimals(answer[0], SCALE), 0.001);
		assertEquals(0.9910, setNumberOfDecimals(answer[1], SCALE), 0.001);
		assertEquals(0.4478, setNumberOfDecimals(answer[2], SCALE), 0.001);
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

	private final static int SCALE = 5;

}
