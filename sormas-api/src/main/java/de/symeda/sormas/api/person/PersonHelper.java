package de.symeda.sormas.api.person;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class PersonHelper {
	
	private final static double SIMILARITY_THRESHOLD = 0.65;
	
	/**
	 * Calculates a modified Levenshtein distance between both names and returns true
	 * if the similarity is high enough to consider them a possible match.
	 */
	public static boolean areNamesSimilar(String firstName, String secondName) {
		// Split names at whitespaces and the symbols _ and -
		String[] firstNameParts = firstName.split("[\\s_-]");
		String[] secondNameParts = secondName.split("[\\s_-]");
		
		if (firstNameParts.length <= secondNameParts.length) {
			return getAverageDistance(firstNameParts, secondNameParts) >= SIMILARITY_THRESHOLD;
		} else {
			return getAverageDistance(secondNameParts, firstNameParts) >= SIMILARITY_THRESHOLD;
		}
	}
	
	/**
	 * Average distance is calculated by comparing each element in the shorter name array with
	 * each element in the longer name array. The highest achieved similarity is stored and
	 * the element in the longer array with which this score was achieved is set to null to
	 * make sure it can't be used again. After the comparison, the average of the achieved
	 * scores is returned.
	 */
	private static double getAverageDistance(String[] shorterArray, String[] longerArray) {
		double[] similarityResults = new double[shorterArray.length];
		
		for (int i = 0; i < shorterArray.length; i++) {
			String str1 = shorterArray[i];
			double highestValue = 0;
			int entryPosition = -1;
			for (int j = 0; j < longerArray.length; j++) {
				if (longerArray[j] == null) {
					continue;
				}
				
				String str2 = longerArray[j];
				double levenshteinDistance = getSimilarity(str1, str2);
				if (levenshteinDistance > highestValue) {
					highestValue = levenshteinDistance;
					entryPosition = j;
				}
			}
			
			similarityResults[i] = highestValue;
			if (entryPosition >= 0) {
				longerArray[entryPosition] = null;
			}
		}
		
		double totalDistance = 0;
		for (double d : similarityResults) {
			totalDistance += d;
		}
		
		return totalDistance / similarityResults.length;
	}
	
	private static double getSimilarity(String str1, String str2) {
		if (str1 == null || str2 == null) {
			return 0;
		}
		
		int len = Math.max(str1.length(), str2.length());
        if (len == 0) {
              return 0;
        }

        int levenshteinDistance = new LevenshteinDistance().apply(str1, str2);
        
        return 1 - ((double) levenshteinDistance / (double) len);
	}

}
