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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.DateHelper;

/**
 * Tests for the date and age precedence rules.
 *
 * <p>
 * These rules used to live in getters, where the only way to check one was to run an export
 * against a database and inspect a CSV column. Each is now a method over an
 * {@link EpipulseDiseaseExportEntryDto}, so a case can be built with a few setters and the rule
 * asserted on its own. That makes it possible to pin each tier individually, including tiers that
 * only differ when several dates are present at once.
 */
public class EpipulseCaseDatesTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 2, 10);
	private static final Date POSITIVE_TEST = DateHelper.getDateZero(2024, 2, 14);
	private static final Date NOTIFICATION_REPORT = DateHelper.getDateZero(2024, 2, 12);
	private static final Date DOCTOR_DIAGNOSIS = DateHelper.getDateZero(2024, 2, 11);
	private static final Date CASE_REPORT = DateHelper.getDateZero(2024, 7, 20);

	// Vaccination dates are constants because DateHelper.getDateZero zeroes hours, minutes, and
	// seconds but not milliseconds. Two calls with the same arguments therefore produce unequal
	// Dates. Using the same instance avoids that issue in these tests.
	private static final Date DOSE_LATEST_BEFORE_ONSET = DateHelper.getDateZero(2024, 1, 20);
	private static final Date DOSE_REPORTED_LATER = DateHelper.getDateZero(2024, 2, 1);

	// --- DateOfOnset ---

	@Test
	@DisplayName("DateOfOnset is the recorded symptom onset date")
	public void dateOfOnsetIsTheOnsetDate() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setSymptomOnsetDate(ONSET);

		assertEquals(ONSET, EpipulseCaseDates.dateOfOnset(entry));
	}

	@Test
	@DisplayName("DateOfOnset is blank for an asymptomatic case even when an onset date is recorded")
	public void dateOfOnsetIsBlankWhenAsymptomatic() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setSymptomOnsetDate(ONSET);
		entry.setAsymptomatic(SymptomState.YES);

		assertNull(EpipulseCaseDates.dateOfOnset(entry));
	}

	@Test
	@DisplayName("DateOfOnset is reported for a case explicitly recorded as not asymptomatic")
	public void dateOfOnsetIsReportedWhenNotAsymptomatic() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setSymptomOnsetDate(ONSET);
		entry.setAsymptomatic(SymptomState.NO);

		assertEquals(ONSET, EpipulseCaseDates.dateOfOnset(entry));
	}

	@Test
	@DisplayName("DateOfOnset has no fallback: no onset date means blank")
	public void dateOfOnsetHasNoFallback() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setFirstPositiveTestDate(POSITIVE_TEST);
		entry.setReportDate(CASE_REPORT);

		assertNull(EpipulseCaseDates.dateOfOnset(entry));
	}

	// --- DateUsedForStatistics ---

	@Test
	@DisplayName("DateUsedForStatistics prefers the oldest positive pathogen test")
	public void statisticsDatePrefersThePositiveTest() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setFirstPositiveTestDate(POSITIVE_TEST);
		entry.setFirstNotificationReportDate(NOTIFICATION_REPORT);
		entry.setReportDate(CASE_REPORT);

		// the notification is older, and still loses: the tiers rank by how firmly they establish
		// the case, not by which happened first
		assertEquals(POSITIVE_TEST, EpipulseCaseDates.dateUsedForStatistics(entry));
	}

	@Test
	@DisplayName("DateUsedForStatistics falls to the oldest surveillance report without a positive test")
	public void statisticsDateFallsToTheNotification() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setFirstNotificationReportDate(NOTIFICATION_REPORT);
		entry.setReportDate(CASE_REPORT);

		assertEquals(NOTIFICATION_REPORT, EpipulseCaseDates.dateUsedForStatistics(entry));
	}

	@Test
	@DisplayName("DateUsedForStatistics falls back to the case report date")
	public void statisticsDateFallsBackToTheReportDate() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setReportDate(CASE_REPORT);

		assertEquals(CASE_REPORT, EpipulseCaseDates.dateUsedForStatistics(entry));
	}

	// --- DateOfDiagnosis ---

	@Test
	@DisplayName("DateOfDiagnosis is the earlier of the positive test and the doctor's diagnosis")
	public void diagnosisDateIsTheEarlierOfTheTwo() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setFirstPositiveTestDate(POSITIVE_TEST);
		entry.setDoctorDateOfDiagnosis(DOCTOR_DIAGNOSIS);

		// unlike DateUsedForStatistics this one genuinely takes the earliest: both tiers are
		// diagnoses of equal standing
		assertEquals(DOCTOR_DIAGNOSIS, EpipulseCaseDates.dateOfDiagnosis(entry));
	}

	@Test
	@DisplayName("DateOfDiagnosis reports whichever of the two exists on its own")
	public void diagnosisDateTakesTheOnlyTierPresent() {

		EpipulseDiseaseExportEntryDto onlyTest = entry();
		onlyTest.setFirstPositiveTestDate(POSITIVE_TEST);
		assertEquals(POSITIVE_TEST, EpipulseCaseDates.dateOfDiagnosis(onlyTest));

		EpipulseDiseaseExportEntryDto onlyDoctor = entry();
		onlyDoctor.setDoctorDateOfDiagnosis(DOCTOR_DIAGNOSIS);
		assertEquals(DOCTOR_DIAGNOSIS, EpipulseCaseDates.dateOfDiagnosis(onlyDoctor));
	}

	@Test
	@DisplayName("DateOfDiagnosis is blank with neither a positive test nor a doctor's diagnosis")
	public void diagnosisDateIsBlankWithoutEitherTier() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setReportDate(CASE_REPORT);

		assertNull(EpipulseCaseDates.dateOfDiagnosis(entry));
	}

	// --- the date a dose counts as given on ---

	@Test
	@DisplayName("a dose with no date of administration falls back to the date it was reported")
	public void doseWithoutAnAdministrationDateUsesTheReportDate() {

		EpipulseVaccinationCheckDto undated = new EpipulseVaccinationCheckDto();
		undated.setReportDate(DOSE_LATEST_BEFORE_ONSET);

		// it still happened, and it was recorded at some point, so the dose is dated rather than
		// dropped. Which column uses that date is decided in EpipulseMapping.
		assertEquals(DOSE_LATEST_BEFORE_ONSET, EpipulseCaseDates.vaccinationDate(undated));
	}

	@Test
	@DisplayName("the date of administration wins over the report date when both are recorded")
	public void administrationDateWinsOverTheReportDate() {

		EpipulseVaccinationCheckDto vaccination = new EpipulseVaccinationCheckDto();
		vaccination.setVaccinationDate(DOSE_LATEST_BEFORE_ONSET);
		vaccination.setReportDate(DOSE_REPORTED_LATER);

		assertEquals(DOSE_LATEST_BEFORE_ONSET, EpipulseCaseDates.vaccinationDate(vaccination));
	}

	@Test
	@DisplayName("a dose with neither date has no date at all")
	public void doseWithNoDatesAtAllHasNoDate() {
		assertNull(EpipulseCaseDates.vaccinationDate(new EpipulseVaccinationCheckDto()));
	}

	// --- the date Age is measured against ---

	@Test
	@DisplayName("Age is measured against the onset date when there is one")
	public void ageIsMeasuredFromOnset() {

		EpipulseDiseaseExportEntryDto entry = bornIn2000();
		entry.setSymptomOnsetDate(ONSET);
		entry.setFirstPositiveTestDate(POSITIVE_TEST);

		assertEquals(ONSET, EpipulseCaseDates.ageReferenceDate(entry));
	}

	@Test
	@DisplayName("Age falls back to DateUsedForStatistics when no onset date was recorded")
	public void ageFallsBackToTheStatisticsDate() {

		EpipulseDiseaseExportEntryDto entry = bornIn2000();
		entry.setFirstPositiveTestDate(POSITIVE_TEST);
		entry.setReportDate(CASE_REPORT);

		assertEquals(POSITIVE_TEST, EpipulseCaseDates.ageReferenceDate(entry));
	}

	@Test
	@DisplayName("an asymptomatic case reports a blank DateOfOnset but still has an Age")
	public void asymptomaticCaseStillHasAnAge() {

		EpipulseDiseaseExportEntryDto entry = bornIn2000();
		entry.setSymptomOnsetDate(ONSET);
		entry.setAsymptomatic(SymptomState.YES);
		entry.setReportDate(CASE_REPORT);

		// the two columns answer different questions: only DateOfOnset is a claim about symptoms.
		// The recorded onset would have given 23; falling back to the report date gives 24, so
		// this pins that the fallback really is being taken and not the onset date after all.
		assertNull(EpipulseCaseDates.dateOfOnset(entry));
		assertEquals(CASE_REPORT, EpipulseCaseDates.ageReferenceDate(entry));
		assertEquals(Integer.valueOf(24), EpipulseCaseDates.ageYears(entry));
	}

	@Test
	@DisplayName("Age is completed years between the birth date and the reference date")
	public void ageIsInCompletedYears() {

		EpipulseDiseaseExportEntryDto entry = bornIn2000();
		// born 2000-06-15, onset 2024-03-10: the birthday has not yet come round in the onset year
		entry.setSymptomOnsetDate(ONSET);

		assertEquals(Integer.valueOf(23), EpipulseCaseDates.ageYears(entry));
	}

	@Test
	@DisplayName("Age is blank when no part of the birth date is known")
	public void ageIsBlankWithoutABirthDate() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setSymptomOnsetDate(ONSET);

		assertNull(EpipulseCaseDates.ageYears(entry));
		assertNull(EpipulseCaseDates.ageMonths(entry));
	}

	@Test
	@DisplayName("a birth date with only the year known defaults to 1 January")
	public void onlyTheBirthYearKnownDefaultsToFirstOfJanuary() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setYearOfBirth(2000);
		entry.setSymptomOnsetDate(ONSET);

		// 2000-01-01 to 2024-03-10 is 24 completed years; the same person recorded with a June
		// birthday would be 23, so the default is visible in the answer rather than incidental
		assertEquals(Integer.valueOf(24), EpipulseCaseDates.ageYears(entry));
	}

	@Test
	@DisplayName("a birth date with no day known defaults to the 1st of its month")
	public void noBirthDayKnownDefaultsToTheFirstOfTheMonth() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setYearOfBirth(2000);
		entry.setMonthOfBirth(3);
		entry.setSymptomOnsetDate(ONSET);

		// 2000-03-01 to 2024-03-10: the March birthday has already passed, so 24
		assertEquals(Integer.valueOf(24), EpipulseCaseDates.ageYears(entry));
	}

	@Test
	@DisplayName("a birth day recorded without a month is dropped, not read as a day of January")
	public void aBirthDayWithoutAMonthIsDropped() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setYearOfBirth(2000);
		entry.setDayOfBirth(20);
		entry.setSymptomOnsetDate(ONSET);

		// the record supports "born in 2000" and nothing finer, so 2000-01-01 as if the day were
		// absent; reading it as 2000-01-20 would assert a birth date nobody recorded
		assertEquals(Integer.valueOf(24), EpipulseCaseDates.ageYears(entry));
		assertEquals(Integer.valueOf(290), EpipulseCaseDates.ageMonths(entry));
	}

	@Test
	@DisplayName("the day is ignored entirely without a month, whatever it is")
	public void aBirthDayWithoutAMonthNeverMovesTheDate() {

		EpipulseDiseaseExportEntryDto lateInTheMonth = entry();
		lateInTheMonth.setYearOfBirth(2000);
		lateInTheMonth.setDayOfBirth(31);
		lateInTheMonth.setSymptomOnsetDate(ONSET);

		EpipulseDiseaseExportEntryDto noDayAtAll = entry();
		noDayAtAll.setYearOfBirth(2000);
		noDayAtAll.setSymptomOnsetDate(ONSET);

		// a day the record cannot place in a month changes nothing about the answer
		assertEquals(EpipulseCaseDates.ageMonths(noDayAtAll), EpipulseCaseDates.ageMonths(lateInTheMonth));
	}

	@Test
	@DisplayName("AgeMonth counts completed months, not the months the period touches")
	public void ageMonthsCountsCompletedMonths() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setYearOfBirth(2023);
		entry.setMonthOfBirth(1);
		entry.setDayOfBirth(15);
		entry.setSymptomOnsetDate(ONSET);

		// born 2023-01-15, onset 2024-03-10: 13 completed months, the 14th falling due on the 15th.
		// DateHelper.getMonthsBetween would say 14 -- it adds one to the completed count.
		assertEquals(Integer.valueOf(13), EpipulseCaseDates.ageMonths(entry));
		assertEquals(Integer.valueOf(1), EpipulseCaseDates.ageYears(entry));
	}

	@Test
	@DisplayName("an impossible birth date blanks Age rather than rolling over into the next month")
	public void impossibleBirthDateBlanksAge() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setYearOfBirth(2000);
		entry.setMonthOfBirth(2);
		entry.setDayOfBirth(31);
		entry.setSymptomOnsetDate(ONSET);

		assertNull(EpipulseCaseDates.ageYears(entry));
	}

	private static EpipulseDiseaseExportEntryDto entry() {
		return new EpipulseDiseaseExportEntryDto();
	}

	private static EpipulseDiseaseExportEntryDto bornIn2000() {

		EpipulseDiseaseExportEntryDto entry = entry();
		entry.setYearOfBirth(2000);
		entry.setMonthOfBirth(6);
		entry.setDayOfBirth(15);

		return entry;
	}
}
