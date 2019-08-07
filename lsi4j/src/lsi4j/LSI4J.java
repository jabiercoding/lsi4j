package lsi4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * LSI for Java
 * 
 * @author Jabier Martinez
 * @author Arthur Joanny
 * @author Nicolás Ordoñez Chala
 * 
 *         Use Jama Matrix Library: https://math.nist.gov/javanumerics/jama/doc/
 */
public class LSI4J {

	// What kind of Low rank approximation want to apply
	public static final int APPROXIMATION_NONE = 0;
	public static final int APPROXIMATION_K_VALUE = 1;
	public static final int APPROXIMATION_PERCENTAGE = 2;

	// Sort the terms
	public static final int SORT_NONE = 0;
	public static final int SORT_ASCENDING = 1;

	// Decide if sort or not the words
	private static int sortType;

	private static List<String> allTerms;

	private SingularValueDecomposition svd;

	// Double[] documents and query
	private static double[][] documentsMatrix;
	private static double[] queryMatrix;

	// Lower Rank Decomposition (lra) Value
	private static int approximationValue;

	/**
	 * LSI4J
	 * 
	 * @param documents
	 *            A list of documents where each of them is a list of words. The
	 *            documents as they are. Words repetitions inside a document is
	 *            normal. Lowercase or uppercase will be ignored when comparing
	 *            words. Apart from that, no preprocessing of the words will be
	 *            performed such as stopwords, stemming etc. so do it (if you want)
	 *            before instantiating LSI4J
	 * @param approximationType
	 *            Use constants NONE, K_VALUE or PERCENTAGE. Default is NONE
	 * @param approximationValue
	 *            Ignored in case of NONE, The K in case of K_VALUE, or a PERCENTAGE
	 *            where 1.0 = 100%
	 * @param sortTermsFromDocuments
	 *            Use constants NONE or ASCENDING (no impact on the results).
	 *            Default is NONE
	 */
	public LSI4J(List<List<String>> documents, int approximationType, double approximationValue,
			int sortTermsFromDocuments) {
		boolean caseSensitive = false;

		// Get and sort terms
		sortType = sortTermsFromDocuments;
		allTerms = getDifferentTerms(documents, caseSensitive);

		// Get the double matrix and calculate svd
		queryMatrix = null;
		documentsMatrix = createDoubleMatrixFromDocuments(allTerms, documents, caseSensitive);
		Matrix a = new Matrix(documentsMatrix);
		svd = a.svd();

		// Calculate the Low K Final Value
		Matrix s = svd.getS();
		calculateLowKFinalValue(s, approximationType, approximationValue);
	}

	public LSI4J(List<List<String>> documents, int approximationType, double approximationValue) {
		this(documents, approximationType, approximationValue, SORT_NONE);
	}

	public LSI4J(List<List<String>> documents) {
		this(documents, APPROXIMATION_NONE, 0.0, SORT_NONE);
	}

	/**
	 * Apply the LSI technique based on a query (weights)
	 * 
	 * @param query
	 *            (weights)
	 * @return List of similarities of the query for each document
	 */
	protected double[] applyLSI(double[] query) {

		double answer[];

		try {

			// A = U*S*(V^T)
			Matrix u = svd.getU();
			Matrix s = svd.getS();
			Matrix v = svd.getV();

			// Here, rand-reduce The the U and S matrix are sorted by singular value the
			// highest to the smallest so we just remove the last rows and columns.
			s = s.getMatrix(0, approximationValue - 1, 0, approximationValue - 1);
			u = u.getMatrix(0, u.getRowDimension() - 1, 0, approximationValue - 1);
			v = v.getMatrix(0, v.getRowDimension() - 1, 0, approximationValue - 1);
			Matrix vkT = v.transpose();

			// Find new query vector q=(q^T)*uk*(sk^-1)
			Matrix q = new Matrix(query, query.length);
			q = q.transpose();
			q = q.times(u);
			q = q.times(s.inverse());

			double q_array[] = q.getColumnPackedCopy();

			// Rank in decreasing order of query-document cosine similarities.
			answer = new double[vkT.getColumnDimension()];

			for (int i = 0; i < vkT.getColumnDimension(); i++) {
				// Get each d matrix from vk
				Matrix di = vkT.getMatrix(0, vkT.getRowDimension() - 1, i, i);
				double di_array[] = di.getColumnPackedCopy();

				// cosine similarity between di and q
				answer[i] = cosine(di_array, q_array, true, 4);
			}

		} catch (Exception e) {
			answer = null;
			e.printStackTrace();
		}

		return answer;
	}

	/**
	 * Apply LSI for a given query
	 * 
	 * @param query
	 * @return
	 */
	public double[] applyLSI(List<String> query) {

		double answer[];

		try {

			// Create the query
			queryMatrix = createDoubleQueryFromList(allTerms, query, false);

			// Get the array of similarity
			answer = applyLSI(queryMatrix);

		} catch (Exception e) {
			answer = null;
			e.printStackTrace();
		}

		return answer;

	}

	/**
	 * Get all different terms
	 * 
	 * @param documents
	 * 
	 * @return the list of the unique terms
	 */
	protected List<String> getDifferentTerms(List<List<String>> documents, boolean caseSensitive) {
		List<String> differentTerms = new ArrayList<String>();
		try {
			// Search inside each document
			for (List<String> document : documents) {
				// Search inside each word
				for (String term : document) {
					if (!caseSensitive) {
						term = term.toLowerCase();
					}
					// Check if s word is not in the returning list
					if (!differentTerms.contains(term)) {
						differentTerms.add(term);
					}
				}
			}
			if (sortType == SORT_ASCENDING) {
				Collections.sort(differentTerms);
			}
		} catch (Exception e) {
			differentTerms = null;
			e.printStackTrace();
		}

		return differentTerms;
	}

	/**
	 * We assign to each cell in the matrix the number of occurrences of each word
	 * in the document for each word
	 * 
	 * @param terms
	 *            - List of possible words that are inside the document
	 * @param documents
	 *            - List of documents
	 * @return Matrix where each column is each document and the rows have the
	 *         weight inside the document
	 */
	protected double[][] createDoubleMatrixFromDocuments(List<String> terms, List<List<String>> documents,
			boolean caseSensitive) {
		// terms.size() is the quantity of words that are available as rows
		// documents.size() is the number of documents as columns
		double weight[][] = new double[terms.size()][documents.size()];

		try {

			// Search inside each document
			for (int di = 0; di < documents.size(); di++) {
				// Search inside each term

				for (int ti = 0; ti < terms.size(); ti++) {
					// If the word is present in the list of words
					List<String> d = documents.get(di);
					weight[ti][di] = 0;
					if (d.size() > 0) {
						for (String w : d) {
							// verify if documents is case sensible
							if (!caseSensitive) {
								w = w.toLowerCase();
								terms.set(ti, terms.get(ti).toLowerCase());
							}

							if (terms.get(ti).equals(w))
								// Add the occurrence
								weight[ti][di]++;
						}
					}
				}
			}
		} catch (Exception e) {
			weight = null;
			e.printStackTrace();
		}

		return weight;
	}

	/**
	 * Get the weight of the query
	 * 
	 * @param list
	 * @param query
	 * @return An array
	 */
	protected double[] createDoubleQueryFromList(List<String> terms, List<String> query, boolean caseSensitive) {
		double answer[];
		try {
			answer = new double[terms.size()];
			int i = 0;
			for (String t : terms) {
				answer[i] = 0;
				for (String q : query) {
					// Verify if is case sensible
					if (!caseSensitive) {
						t = t.toLowerCase();
						q = q.toLowerCase();
					}
					if (t.equals(q))
						answer[i]++;
				}
				i++;
			}

		} catch (Exception e) {
			answer = null;
			e.printStackTrace();
		}

		return answer;
	}

	/**
	 * Calculate the cosine between two vector
	 * https://en.wikipedia.org/wiki/Cosine_similarity
	 * 
	 * @param u
	 *            The vector U
	 * @param v
	 *            The vector V
	 * @return The cosine
	 */
	protected static double cosine(double u[], double v[], boolean applyScale, int scale) {
		// cosine between vector U and V is ( U * V ) / ( ||U|| * ||V||

		double scalaire = 0.0;
		double normeU = 0.0;
		double normeV = 0.0;

		// The smallest number allowed must be at least -0.1
		if (applyScale) {
			if (scale <= 0)
				scale = 1;
			double smallestNumberAllowed = -1 / Math.pow(10, scale);

			for (int i = 0; i < u.length; i++) {
				// The value of the matrix must be higher than 0 and smaller than the smallest
				// number allowed. Otherwise, it will be rounded to zero
				if (u[i] < 0 && u[i] >= smallestNumberAllowed) {
					u[i] = 0;
				}
				if (v[i] < 0 && v[i] >= smallestNumberAllowed) {
					v[i] = 0;
				}
				scalaire += (u[i] * v[i]);
				normeU += Math.pow(u[i], 2);
				normeV += Math.pow(v[i], 2);
			}

		} else {
			for (int i = 0; i < u.length; i++) {
				scalaire += (u[i] * v[i]);
				normeU += Math.pow(u[i], 2);
				normeV += Math.pow(v[i], 2);
			}
		}

		normeU = Math.sqrt(normeU);
		normeV = Math.sqrt(normeV);
		double val = scalaire / (normeU * normeV);

		if (Double.isNaN(val)) {
			val = -1;
		}
		return val;
	}

	/**
	 * Get the k to the low rank approximation
	 */
	protected void calculateLowKFinalValue(Matrix s, int lraType, double lraValue) {
		double lowerK;
		if (lraType == APPROXIMATION_K_VALUE) {
			lowerK = (int) lraValue;
		}
		// percentage of columns to reduce
		else if (lraType == APPROXIMATION_PERCENTAGE) {
			lowerK = s.getRowDimension() * lraValue;
		}
		// if other -> not reduce
		else {
			lowerK = s.getRowDimension();
		}
		// if lowerk is greater than max possible option. Select s row dimension by
		// default
		approximationValue = (int) Math.min(lowerK, s.getRowDimension());
	}

}
