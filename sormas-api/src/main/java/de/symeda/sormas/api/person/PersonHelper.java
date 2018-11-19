/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.person;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import de.symeda.sormas.api.utils.DataHelper;

public class PersonHelper {
	
	private final static double SIMILARITY_THRESHOLD = 0.65;
	private final static double LOWER_THRESHOLD = 0.33;
	
	/**
	 * Calculates a modified Levenshtein distance between both names and returns true
	 * if the similarity is high enough to consider them a possible match.
	 */
	public static boolean areNamesSimilar(String firstName, String secondName) {
		firstName = firstName.toLowerCase();
		secondName = secondName.toLowerCase();
		
		// Split names at whitespaces and the symbols _ and -
		String[] firstNameParts = StringUtils.split(firstName.trim(), " _-", 0);
		String[] secondNameParts = StringUtils.split(secondName.trim(), " _-", 0);
		
		if (firstNameParts.length <= secondNameParts.length) {
			return getAverageSimilarity(firstNameParts, secondNameParts) >= SIMILARITY_THRESHOLD;
		} else {
			return getAverageSimilarity(secondNameParts, firstNameParts) >= SIMILARITY_THRESHOLD;
		}
	}
	
	/**
	 * Average similarity is calculated by comparing each element in the shorter name array with
	 * each element in the longer name array. The highest achieved similarity is stored and
	 * the element in the longer array with which this score was achieved is set to null to
	 * make sure it can't be used again. After the comparison, the average of the achieved
	 * scores is returned. If any part of the name deceeds the lower threshold, 0 is returned by
	 * default.
	 */
	private static double getAverageSimilarity(String[] shorterArray, String[] longerArray) {
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
			
			if (highestValue < LOWER_THRESHOLD) {
				return 0;
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
	
	public static String buildAgeString(Integer approximateAge, ApproximateAgeType approximateAgeType) {
		if (approximateAge == null) {
			return "";
		}

		if (approximateAgeType == ApproximateAgeType.MONTHS) {
			return approximateAge + " " + approximateAgeType;
		} else if (approximateAgeType == null || approximateAgeType == ApproximateAgeType.YEARS) {
			return approximateAge.toString();
		} else { 
			throw new IllegalArgumentException(approximateAgeType.toString());
		}
	}
	
	public static String buildPhoneString(String phone, String phoneOwner) {
		StringBuilder result = new StringBuilder();
		if (!DataHelper.isNullOrEmpty(phone)) {
			result.append(phone);
		}
		if (!DataHelper.isNullOrEmpty(phoneOwner)) {
			if (result.length() > 0) {
				result.append(" - ");
			}
			result.append(phoneOwner);
		}
		return result.toString();
	}

	public static String buildOccupationString(OccupationType occupationType, String occupationDetails, String occupationFacilityName) {
		StringBuilder result = new StringBuilder();
		if (occupationType == OccupationType.OTHER) {
			result.append(occupationDetails);
		} else if (occupationType != null) {
			result.append(occupationType);
		}
		if (!DataHelper.isNullOrEmpty(occupationFacilityName)) {
			if (result.length() > 0) {
				result.append(", ");
			}
			result.append(occupationFacilityName);
		}
		return result.toString();
	}
}
