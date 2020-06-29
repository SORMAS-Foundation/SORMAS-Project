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
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.simmetrics.metrics.StringMetrics;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public final class PersonHelper {

	private PersonHelper() {
		// Hide Utility Class Constructor
	}

	public static final double NAME_SIMILARITY_THRESHOLD = 0.5D;

	/**
	 * Calculates the trigram distance between both names and returns true
	 * if the similarity is high enough to consider them a possible match.
	 * Used a default of {@link PersonHelper#NAME_SIMILARITY_THRESHOLD} for the threshold.
	 */
	protected static boolean areFullNamesSimilar(final String firstName, final String secondName) {
		final String name = normalizeString(firstName);
		final String otherName = normalizeString(secondName);
		return StringMetrics.qGramsDistance().compare(name, otherName) >= NAME_SIMILARITY_THRESHOLD;
	}

	/**
	 * Calculates the trigram distance between firstName/lastname (also viceversa lastname/firstname) and otherFirstName/otherLastName,
	 * returns true if the similarity is high enough to consider them a possible match.
	 */
	public static boolean areNamesSimilar(final String firstName, final String lastName, final String otherFirstName, final String otherLastName) {
		final String name = createFullName(firstName, lastName);
		final String nameInverted = createFullName(lastName, firstName);
		final String otherName = createFullName(otherFirstName, otherLastName);
		return areFullNamesSimilar(name, otherName) || areFullNamesSimilar(nameInverted, otherName);
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
