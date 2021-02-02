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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.person;

import java.text.Normalizer;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.simmetrics.metrics.StringMetrics;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.utils.DateHelper;

public final class PersonHelper {

	private PersonHelper() {
		// Hide Utility Class Constructor
	}

	public static final double DEFAULT_NAME_SIMILARITY_THRESHOLD = 0.65D;

	/**
	 * Calculates the trigram distance between both names and returns true
	 * if the similarity is high enough to consider them a possible match.
	 * Uses a default of {@link PersonHelper#DEFAULT_NAME_SIMILARITY_THRESHOLD} for the threshold.
	 */
	protected static boolean areFullNamesSimilar(final String firstName, final String secondName, Double similarityThreshold) {
		final String name = normalizeString(firstName);
		final String otherName = normalizeString(secondName);
		return StringMetrics.qGramsDistance().compare(name, otherName)
			>= (similarityThreshold != null ? similarityThreshold : DEFAULT_NAME_SIMILARITY_THRESHOLD);
	}

	/**
	 * Calculates the trigram distance between firstName/lastname (also viceversa lastname/firstname) and otherFirstName/otherLastName,
	 * returns true if the similarity is high enough to consider them a possible match.
	 */
	public static boolean areNamesSimilar(
		final String firstName,
		final String lastName,
		final String otherFirstName,
		final String otherLastName,
		Double similarityThreshold) {
		final String name = createFullName(firstName, lastName);
		final String nameInverted = createFullName(lastName, firstName);
		final String otherName = createFullName(otherFirstName, otherLastName);
		return areFullNamesSimilar(name, otherName, similarityThreshold) || areFullNamesSimilar(nameInverted, otherName, similarityThreshold);
	}

	private static String createFullName(String firstName, String lastName) {
		return firstName + StringUtils.SPACE + lastName;
	}

	public static String normalizeString(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD).toLowerCase();
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public static String formatBirthdate(Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, Language language) {

		if (birthdateDD == null && birthdateMM == null && birthdateYYYY == null) {
			return "";
		} else {
			String birthDate = DateHelper.getLocalDateFormat(language).toPattern();
			birthDate = birthDate.replaceAll("d+", birthdateDD != null ? birthdateDD.toString() : "");
			birthDate = birthDate.replaceAll("M+", birthdateMM != null ? birthdateMM.toString() : "");
			birthDate = birthDate.replaceAll("y+", birthdateYYYY != null ? birthdateYYYY.toString() : "");
			birthDate = birthDate.replaceAll("^[^\\d]*", "").replaceAll("[^\\d]*$", "");

			return birthDate;
		}
	}

	public static BirthDateDto parseBirthdate(String birthDate, Language language) {

		if (StringUtils.isEmpty(birthDate)) {
			return null;
		}

		String dateFormat = language.getDateFormat();
		List<String> dateFormatFields = DateHelper.getDateFields(dateFormat);
		List<String> dateFields = DateHelper.getDateFields(birthDate);

		if (dateFormatFields == null || dateFields == null) {
			return null;
		}

		Integer birthdateDD = null;
		Integer birthdateMM = null;
		Integer birthdateYYYY = null;

		for (int i = 0; i < dateFormatFields.size(); i++) {
			String dateField = dateFields.get(i);
			String formatField = dateFormatFields.get(i);

			if (!StringUtils.isEmpty(dateField)) {
				if (formatField.toLowerCase().startsWith("d")) {
					birthdateDD = Integer.parseInt(dateField);
				} else if (formatField.toLowerCase().startsWith("m")) {
					birthdateMM = Integer.parseInt(dateField);
				} else if (formatField.toLowerCase().startsWith("y")) {
					birthdateYYYY = Integer.parseInt(dateField);
				}

			}
		}

		return new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
	}

	public static String getAgeAndBirthdateString(
		Integer age,
		ApproximateAgeType ageType,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY,
		Language language) {

		String ageStr = ApproximateAgeHelper.formatApproximateAge(age, ageType);
		String birthdateStr = formatBirthdate(birthdateDD, birthdateMM, birthdateYYYY, language);
		return !StringUtils.isEmpty(ageStr)
			? (ageStr + (!StringUtils.isEmpty(birthdateStr) ? " (" + birthdateStr + ")" : ""))
			: !StringUtils.isEmpty(birthdateStr) ? birthdateStr : "";
	}

	public static String buildBurialInfoString(BurialInfoDto dto, Language language) {
		StringBuilder result = new StringBuilder();

		Date burialDate = dto.getBurialDate();
		if (burialDate != null) {
			result.append(DateHelper.formatLocalDate(burialDate, language));
		}

		BurialConductor burialConductor = dto.getBurialConductor();
		if (burialConductor != null) {
			if (result.length() > 0) {
				result.append(" ");
			}
			result.append(burialConductor);
		}

		String burialPlaceDescription = dto.getBurialPlaceDescription();
		if (burialPlaceDescription != null) {
			if (result.length() > 0) {
				result.append(" ");
			}
			result.append(burialPlaceDescription);
		}
		return result.toString();
	}

	public static String buildOccupationString(OccupationType occupationType, String occupationDetails) {

		StringBuilder result = new StringBuilder();
		if (occupationType == OccupationType.OTHER) {
			result.append(occupationDetails);
		} else if (occupationType != null) {
			result.append(occupationType);
		}
		return result.toString();
	}

	public static String buildEducationString(EducationType educationType, String educationDetails) {

		StringBuilder result = new StringBuilder();
		if (educationType == EducationType.OTHER) {
			result.append(educationDetails);
		} else if (educationType != null) {
			result.append(educationType);
		}
		return result.toString();
	}
}
