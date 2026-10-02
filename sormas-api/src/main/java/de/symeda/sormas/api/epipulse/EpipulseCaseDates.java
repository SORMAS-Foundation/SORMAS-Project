/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.epipulse;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.UtilDate;

/**
 * Resolves the date-related values required by an EpiPulse disease export.
 *
 * <p>
 * Some EpiPulse date fields are not stored directly in SORMAS. This class centralizes precedence
 * and calculation rules used to derive them from an {@link EpipulseDiseaseExportEntryDto}.
 * Keeping these rules separate from DTO getters to make them independently unit-testable.
 *
 * <p>
 * In general methods accept partially populated entries and return {@code null}
 * when the required source data is unavailable.
 */
public final class EpipulseCaseDates {

	private EpipulseCaseDates() {
	}

	/**
	 * {@code DateOfOnset} - when the patient first showed symptoms.
	 *
	 * <ol>
	 * <li>blank if {@code Symptoms.asymptomatic} is {@code YES}</li>
	 * <li>{@code Symptoms.onsetDate}</li>
	 * <li>blank - there is no fallback</li>
	 * </ol>
	 *
	 * <p>
	 * Deliberately no fallback to a notification or report date.
	 */
	public static Date dateOfOnset(EpipulseDiseaseExportEntryDto entry) {

		if (entry.getAsymptomatic() == SymptomState.YES) {
			return null;
		}

		return entry.getSymptomOnsetDate();
	}

	/**
	 * {@code DateUsedForStatistics} - the date this case is counted under.
	 *
	 * <ol>
	 * <li>the oldest positive pathogen test on the case</li>
	 * <li>the oldest surveillance report of it, whether a doctor's declaration or a lab message</li>
	 * <li>{@code Case.reportDate}</li>
	 * </ol>
	 *
	 * <p>
	 * The last tier is a fallback rather than a choice, so this returns {@code null} only for a
	 * case with no report date at all, which the schema does not allow.
	 */
	public static Date dateUsedForStatistics(EpipulseDiseaseExportEntryDto entry) {

		if (entry.getFirstPositiveTestDate() != null) {
			return entry.getFirstPositiveTestDate();
		}

		if (entry.getFirstNotificationReportDate() != null) {
			return entry.getFirstNotificationReportDate();
		}

		return entry.getReportDate();
	}

	/**
	 * {@code DateOfDiagnosis} - the earliest of the first positive laboratory result and the date
	 * of diagnosis on a DD, whichever came first.
	 *
	 * <p>
	 * Earliest-of, not precedence: which happened first.
	 * A case with only one of them reports that one; a case with neither reports blank.
	 */
	public static Date dateOfDiagnosis(EpipulseDiseaseExportEntryDto entry) {
		return earliest(entry.getFirstPositiveTestDate(), entry.getDoctorDateOfDiagnosis());
	}

	/**
	 * The date one vaccination counts as having been administered on.
	 *
	 * <ol>
	 * <li>{@code Vaccination.vaccinationDate}</li>
	 * <li>{@code Vaccination.reportDate}</li>
	 * </ol>
	 *
	 * <p>
	 * A dose recorded without a vaccination date defaults to it's report date.
	 */
	public static Date vaccinationDate(EpipulseVaccinationCheckDto vaccination) {

		return vaccination.getVaccinationDate() != null ? vaccination.getVaccinationDate() : vaccination.getReportDate();
	}

	/**
	 * The date {@code Age} and {@code AgeMonth} are measured against.
	 *
	 * <ol>
	 * <li>{@link #dateOfOnset}</li>
	 * <li>{@link #dateUsedForStatistics}</li>
	 * </ol>
	 */
	public static Date ageReferenceDate(EpipulseDiseaseExportEntryDto entry) {

		Date onset = dateOfOnset(entry);
		if (onset != null) {
			return onset;
		}

		return dateUsedForStatistics(entry);
	}

	/**
	 * {@code Age} - completed years between the patient's birth date and
	 * {@link #ageReferenceDate}, or {@code null} when either is unknown.
	 */
	public static Integer ageYears(EpipulseDiseaseExportEntryDto entry) {
		return completedUnitsSinceBirth(entry, ChronoUnit.YEARS);
	}

	/**
	 * {@code AgeMonth} - completed months between the patient's birth date and
	 * {@link #ageReferenceDate}, or {@code null} when either is unknown.
	 *
	 */
	public static Integer ageMonths(EpipulseDiseaseExportEntryDto entry) {
		// Counted with ChronoUnit#MONTHS rather than DateHelper.getMonthsBetween which is an inclusive period helper,
		// similar to listMonthsBetween, getDaysBetween, eg. "how many months does this period touch".
		// What we need is "how many months has this person completed". 
		// Using it previously in age calculation reported every infant a month too old.

		return completedUnitsSinceBirth(entry, ChronoUnit.MONTHS);
	}

	private static Integer completedUnitsSinceBirth(EpipulseDiseaseExportEntryDto entry, ChronoUnit unit) {

		LocalDate birthDate = birthDate(entry);
		Date reference = ageReferenceDate(entry);
		if (birthDate == null || reference == null) {
			return null;
		}

		return (int) unit.between(birthDate, UtilDate.toLocalDate(reference));
	}

	/**
	 * The patient's date of birth, with the parts SORMAS did not record filled in.
	 *
	 * <ul>
	 * <li>no year - {@code null}, and the age columns stay blank</li>
	 * <li>no month - 1/Jan/year</li>
	 * <li>no day - 1/month/year</li>
	 * </ul>
	 *
	 * <p>
	 * The parts are filled in from the year downwards, and stop at the first missing part.
	 * A day is only meaningful within a month, a record having a year and a day but no month is
	 * 1 January of that year.
	 *
	 * <p>
	 * An wrong combination - 31 February - yields {@code null} instead of rolling it into March.
	 * A corrupt birth date blanks the column instead of giving a wrong date.
	 */
	private static LocalDate birthDate(EpipulseDiseaseExportEntryDto entry) {

		if (entry.getYearOfBirth() == null) {
			return null;
		}

		int month = entry.getMonthOfBirth() == null ? 1 : entry.getMonthOfBirth();
		// the day is dropped along with a missing month, not carried over into January
		int day = entry.getMonthOfBirth() == null || entry.getDayOfBirth() == null ? 1 : entry.getDayOfBirth();

		try {
			return LocalDate.of(entry.getYearOfBirth(), month, day);
		} catch (DateTimeException e) {
			return null;
		}
	}

	/**
	 * The earlier of two dates, ignoring whichever is absent.
	 */
	private static Date earliest(Date first, Date second) {

		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}

		return first.before(second) ? first : second;
	}
}
