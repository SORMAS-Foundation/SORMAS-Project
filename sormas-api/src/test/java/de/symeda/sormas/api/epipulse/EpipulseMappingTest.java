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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.utils.DateHelper;

/**
 * Tests for the multi-tier rules: which vaccination course the two vaccination columns report, how
 * its dose count is encoded and which dose is dated, which detection methods a case's tests amount
 * to for a given subject code, and which NUTS code the two place columns report.
 */
public class EpipulseMappingTest {

	/** The onset date every case below is measured against. */
	private static final Date REFERENCE = DateHelper.getDateZero(2024, 2, 10);

	private static final Date BEFORE = DateHelper.getDateZero(2023, 5, 1);
	private static final Date DOSE_OLDEST = DateHelper.getDateZero(2022, 5, 1);
	private static final Date DOSE_OLDER = DateHelper.getDateZero(2023, 0, 5);
	private static final Date DOSE_LATEST = DateHelper.getDateZero(2024, 1, 20);
	private static final Date LONG_BEFORE = DateHelper.getDateZero(2019, 5, 1);
	private static final Date AFTER = DateHelper.getDateZero(2024, 6, 1);

	/** How SORMAS stores lifelong protection, per {@code ImmunizationValidityCalculator}. */
	private static final Date LIFELONG = DateHelper.getDateZero(9999, 11, 31);

	private static final int STANDARD = 10;
	private static final int MENI = 4;
	private static final int MPOX = 3;

	@Test
	@DisplayName("an acquired course in force at onset reports the doses recorded on it")
	public void aCourseInForceReportsItsDoses() {

		// the course carries the number; nothing counts the doses under it, because SORMAS only
		// marks a course acquired once its vaccinations have reached that number
		assertEquals("2DOSE", status(entry(course(BEFORE, AFTER, 2)), STANDARD));
	}

	@Test
	@DisplayName("a patient with no acquired course in force is NOTVACC")
	public void noQualifyingCourseIsNotVacc() {
		assertEquals("NOTVACC", status(entry(), STANDARD));
	}

	@Test
	@DisplayName("the window is inclusive at both ends")
	public void theWindowIsInclusive() {

		// protection that starts the day symptoms begin, and protection that ends that day, both
		// covered the patient on the day in question
		assertEquals("1DOSE", status(entry(course(REFERENCE, AFTER, 1)), STANDARD));
		assertEquals("1DOSE", status(entry(course(BEFORE, REFERENCE, 1)), STANDARD));
	}

	@Test
	@DisplayName("a course that had not started, or had expired, does not count")
	public void aCourseOutsideTheWindowDoesNotCount() {

		assertEquals("NOTVACC", status(entry(course(AFTER, LIFELONG, 3)), STANDARD));
		assertEquals("NOTVACC", status(entry(course(LONG_BEFORE, BEFORE, 3)), STANDARD));
	}

	@Test
	@DisplayName("a course with no validity period recorded does not count")
	public void aCourseWithoutAValidityPeriodDoesNotCount() {

		// SORMAS reads a blank validUntil as no match rather than as unbounded (BR0012), and a
		// blank validFrom cannot be shown to precede the onset. Lifelong protection is a date in
		// the year 9999, not a blank, so nothing real is lost by requiring both.
		assertEquals("NOTVACC", status(entry(course(BEFORE, null, 2)), STANDARD));
		assertEquals("NOTVACC", status(entry(course(null, AFTER, 2)), STANDARD));
		assertEquals("2DOSE", status(entry(course(BEFORE, LIFELONG, 2)), STANDARD));
	}

	@Test
	@DisplayName("of several courses in force, the one that started most recently is reported")
	public void theLatestCourseInForceIsReported() {

		// the older course is the longer one and still loses: among courses that all covered the
		// patient, the most recent is the protection they were actually under
		EpipulseDiseaseExportEntryDto entry = entry(course(LONG_BEFORE, LIFELONG, 4), course(BEFORE, LIFELONG, 1));

		assertEquals("1DOSE", status(entry, STANDARD));
	}

	@Test
	@DisplayName("a course outside the window never wins, however recent")
	public void aLaterCourseOutsideTheWindowDoesNotWin() {

		// vaccinated again after falling ill: real, but not their status when they fell ill
		EpipulseDiseaseExportEntryDto entry = entry(course(BEFORE, LIFELONG, 2), course(AFTER, LIFELONG, 3));

		assertEquals("2DOSE", status(entry, STANDARD));
	}

	@Test
	@DisplayName("separate courses are never added together")
	public void separateCoursesAreNeverAddedTogether() {

		// two two-dose courses is not a four-dose patient. Summing the patient's doses was the
		// previous behavior and produced a number nobody ever received.
		EpipulseDiseaseExportEntryDto entry = entry(course(LONG_BEFORE, LIFELONG, 2), course(BEFORE, LIFELONG, 2));

		assertEquals("2DOSE", status(entry, STANDARD));
	}

	@Test
	@DisplayName("a course in force with no dose count is UNKDOSE")
	public void aCourseWithoutADoseCountIsUnknown() {

		// exactly what UNKDOSE is for: vaccinated, number of doses unknown. Counting doses could
		// never produce it, so the value was unreachable until the course became the source.
		assertEquals("UNKDOSE", status(entry(course(BEFORE, AFTER, null)), STANDARD));
	}

	@Test
	@DisplayName("a course recording zero doses reports the number that was entered")
	public void aCourseRecordingZeroDosesIsNotVacc() {

		// contradictory data, acquired, but no doses, and the column reports what was recorded
		// rather than deciding which half to believe
		assertEquals("NOTVACC", status(entry(course(BEFORE, AFTER, 0)), STANDARD));
	}

	@Test
	@DisplayName("a count above the subject code's scale reports that code's highest value")
	public void countAboveTheScaleReportsTheCap() {

		EpipulseDiseaseExportEntryDto fiveDoses = entry(course(BEFORE, AFTER, 5));

		// the same course under three scales: the cap is the only thing that differs between
		// diseases, which is why they share one method
		assertEquals("5DOSE", status(fiveDoses, STANDARD));
		assertEquals("4DOSE", status(fiveDoses, MENI));
		assertEquals("3DOSE", status(fiveDoses, MPOX));
	}

	@Test
	@DisplayName("a count at the cap is reported as itself, not as an overflow")
	public void countAtTheCapIsExact() {
		assertEquals("4DOSE", status(entry(course(BEFORE, AFTER, 4)), MENI));
	}

	@Test
	@DisplayName("a case with no onset date reports blank, not NOTVACC")
	public void noOnsetReportsBlank() {

		EpipulseDiseaseExportEntryDto entry = entry(course(BEFORE, AFTER, 2));
		entry.setSymptomOnsetDate(null);

		// with no onset there is no date to have been vaccinated by, and NOTVACC would assert the
		// patient was never vaccinated
		assertEquals("", status(entry, STANDARD));
	}

	@Test
	@DisplayName("doses on record do not make a patient vaccinated on their own")
	public void dosesAloneDoNotMakeAPatientVaccinated() {

		// DateOfLastVaccination would report this dose; VaccinationStatus does not, because no
		// course SORMAS considers acquired was in force. The two columns answer different
		// questions and are allowed to disagree.
		assertEquals("NOTVACC", status(withDoses(entry(), dose(1L, BEFORE)), STANDARD));
	}

	// --- DateOfLastVaccination ---

	@Test
	@DisplayName("DateOfLastVaccination is the last dose of the reported course before onset")
	public void lastVaccinationIsTheLastDoseOfTheReportedCourse() {

		EpipulseDiseaseExportEntryDto entry =
			withDoses(entry(course(11L, LONG_BEFORE, LIFELONG, 2)), dose(11L, DOSE_OLDER), dose(11L, DOSE_LATEST), dose(11L, DOSE_OLDEST));

		assertEquals("2024-02-20", lastVaccination(entry));
	}

	@Test
	@DisplayName("a dose under another course is not reported, however recent")
	public void aDoseFromAnotherCourseIsNotReported() {

		// course 12 was not in force at onset, so neither its dose count nor its dates describe
		// this case. Reporting its date beside course 11's count would describe neither course.
		EpipulseDiseaseExportEntryDto entry =
			withDoses(entry(course(11L, LONG_BEFORE, LIFELONG, 2), course(12L, AFTER, LIFELONG, 1)), dose(11L, DOSE_OLDER), dose(12L, DOSE_LATEST));

		assertEquals("2DOSE", status(entry, STANDARD));
		assertEquals("2023-01-05", lastVaccination(entry));
	}

	@Test
	@DisplayName("a dose on or after the onset date is not the last vaccination")
	public void aDoseOnOrAfterOnsetIsNotReported() {

		// strictly before: a dose given the day symptoms began counts towards the course's doses
		// without being eligible to be the last one, because it did not precede the illness
		EpipulseDiseaseExportEntryDto onTheDay = withDoses(entry(course(11L, LONG_BEFORE, LIFELONG, 2)), dose(11L, DOSE_OLDER), dose(11L, REFERENCE));

		assertEquals("2023-01-05", lastVaccination(onTheDay));

		EpipulseDiseaseExportEntryDto after = withDoses(entry(course(11L, LONG_BEFORE, LIFELONG, 2)), dose(11L, DOSE_OLDER), dose(11L, AFTER));

		assertEquals("2023-01-05", lastVaccination(after));
	}

	@Test
	@DisplayName("a dose with no date of administration is dated by its report date")
	public void anUndatedDoseUsesItsReportDate() {

		EpipulseVaccinationCheckDto undated = new EpipulseVaccinationCheckDto();
		undated.setImmunizationId(11L);
		undated.setReportDate(DOSE_LATEST);

		EpipulseDiseaseExportEntryDto entry = withDoses(entry(course(11L, LONG_BEFORE, LIFELONG, 2)), dose(11L, DOSE_OLDER), undated);

		assertEquals("2024-02-20", lastVaccination(entry));
	}

	@Test
	@DisplayName("a dose with neither date is skipped rather than blanking the column")
	public void aDoseWithNoDatesIsSkipped() {

		EpipulseVaccinationCheckDto dateless = new EpipulseVaccinationCheckDto();
		dateless.setImmunizationId(11L);

		EpipulseDiseaseExportEntryDto entry = withDoses(entry(course(11L, LONG_BEFORE, LIFELONG, 2)), dateless, dose(11L, DOSE_LATEST));

		assertEquals("2024-02-20", lastVaccination(entry));
	}

	@Test
	@DisplayName("no qualifying course means no date, whatever doses are on record")
	public void noQualifyingCourseMeansNoDate() {

		// the pair stays consistent: NOTVACC and a blank date, rather than a date that belongs to
		// no course the export reported
		EpipulseDiseaseExportEntryDto entry = withDoses(entry(course(11L, LONG_BEFORE, BEFORE, 2)), dose(11L, DOSE_OLDER));

		assertEquals("NOTVACC", status(entry, STANDARD));
		assertEquals("", lastVaccination(entry));
	}

	@Test
	@DisplayName("a course whose doses carry no dates reports its count with a blank date")
	public void aCourseWithUndatedDosesStillReportsItsCount() {

		// numberOfDoses is written on the course, so it survives doses that cannot be dated
		EpipulseDiseaseExportEntryDto entry = entry(course(11L, LONG_BEFORE, LIFELONG, 2));

		assertEquals("2DOSE", status(entry, STANDARD));
		assertEquals("", lastVaccination(entry));
	}

	@Test
	@DisplayName("a case with no onset date reports no last vaccination")
	public void noOnsetMeansNoLastVaccination() {

		EpipulseDiseaseExportEntryDto entry = withDoses(entry(course(11L, LONG_BEFORE, LIFELONG, 2)), dose(11L, DOSE_OLDER));
		entry.setSymptomOnsetDate(null);

		assertEquals("", lastVaccination(entry));
	}

	// --- PathogenDetectionMethod ---

	@Test
	@DisplayName("the subject code decides what a test is called")
	public void theSubjectCodeDecidesTheCode() {

		assertEquals(Collections.singletonList("PCR"), methods(EpipulseSubjectCode.PERT, PathogenTestType.PCR_RT_PCR));
		assertEquals(Collections.singletonList("NUC"), methods(EpipulseSubjectCode.WNF, PathogenTestType.PCR_RT_PCR));
	}

	@Test
	@DisplayName("two tests that mean the same thing are one entry")
	public void testsThatMapToTheSameCodeAreReportedOnce() {

		// a real-time PCR and a LAMP are both nucleic acid amplification; EpiPulse asks which
		// methods were used, not how many tests were run
		assertEquals(
			Collections.singletonList("NUC"),
			methods(EpipulseSubjectCode.CHIK, PathogenTestType.PCR_RT_PCR, PathogenTestType.LAMP, PathogenTestType.Q_PCR));
	}

	@Test
	@DisplayName("different methods are all reported, in the order the aggregate produced them")
	public void differentMethodsAreAllReported() {

		assertEquals(
			Arrays.asList("CULT", "PCR", "SERO"),
			methods(EpipulseSubjectCode.PERT, PathogenTestType.CULTURE, PathogenTestType.PCR_RT_PCR, PathogenTestType.IGG_SERUM_ANTIBODY));
	}

	@Test
	@DisplayName("a test this subject code has no value for is skipped, not reported as OTH")
	public void anUnmappableTestIsSkipped() {

		// OTH exists for CHIK, but it means "some other detection method", not "a test that
		// answers a different question". Microscopy of a chikungunya sample is the latter.
		assertEquals(Collections.singletonList("NUC"), methods(EpipulseSubjectCode.CHIK, PathogenTestType.MICROSCOPY, PathogenTestType.PCR_RT_PCR));
	}

	@Test
	@DisplayName("a case with no tests reports nothing at all")
	public void noTestsMeansNoColumns() {

		assertTrue(methods(EpipulseSubjectCode.PERT).isEmpty());

		EpipulseDiseaseExportEntryDto noList = new EpipulseDiseaseExportEntryDto();
		noList.setSubjectCode(EpipulseSubjectCode.PERT);
		assertTrue(EpipulseMapping.pathogenDetectionMethods(noList).isEmpty());
	}

	private static List<String> methods(EpipulseSubjectCode subjectCode, PathogenTestType... testTypes) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setSubjectCode(subjectCode);

		List<EpipulsePathogentTestCheckDto> tests = new ArrayList<>();
		for (PathogenTestType testType : testTypes) {
			EpipulsePathogentTestCheckDto test = new EpipulsePathogentTestCheckDto();
			test.setTestType(testType);
			tests.add(test);
		}
		entry.setPathogenTests(tests);

		return EpipulseMapping.pathogenDetectionMethods(entry);
	}

	// --- PathogenDetectionMethod for PNEU (serotyping techniques) ---

	@Test
	@DisplayName("each named technique reports its own code")
	public void namedSerotypingTechniquesReportTheirCode() {

		assertEquals(Arrays.asList("QUE", "MPCR"), serotyping(SerotypingMethod.QUELLUNG_REACTION, SerotypingMethod.MULTIPLEX_PCR));
	}

	@Test
	@DisplayName("two techniques EpiPulse does not name collapse to a single OTH")
	public void twoUnnamedTechniquesCollapseToOneOther() {

		// the IPI sample form offers every SerotypingMethod constant rather than the ones
		// applicable to the test, so both of these can be saved on a pneumococcal test. They map
		// to the same code, and the column must not repeat it.
		assertEquals(Collections.singletonList("OTH"), serotyping(SerotypingMethod.AGGLUTINATION, SerotypingMethod.WGS_PREDICTION));
	}

	@Test
	@DisplayName("a susceptibility method recorded as a serotyping one still reports OTH")
	public void aSusceptibilityMethodReportsOther() {

		// DISK_DIFFUSION belongs to antibiotic susceptibility, and the same form defect lets it be
		// picked here. It is a technique that was recorded, so it is reported rather than dropped.
		assertEquals(Collections.singletonList("OTH"), serotyping(SerotypingMethod.DISK_DIFFUSION));
	}

	@Test
	@DisplayName("OTH is reported once even beside a named technique")
	public void otherIsReportedOnceBesideANamedTechnique() {

		assertEquals(
			Arrays.asList("COAGG", "OTH"),
			serotyping(SerotypingMethod.COAGGLUTINATION, SerotypingMethod.AGGLUTINATION, SerotypingMethod.OTHER));
	}

	@Test
	@DisplayName("a case with no serotyping technique reports nothing at all")
	public void noSerotypingTechniqueMeansNoColumns() {

		assertTrue(serotyping().isEmpty());
		assertTrue(EpipulseMapping.serotypingMethods(new EpipulseDiseaseExportEntryDto()).isEmpty());
	}

	private static List<String> serotyping(SerotypingMethod... methods) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setSerotypingMethods(new ArrayList<>(Arrays.asList(methods)));

		return EpipulseMapping.serotypingMethods(entry);
	}

	// --- CaseClassification ---

	@Test
	@DisplayName("the three classifications EpiPulse accepts map to their codes")
	public void classificationsMapToTheirCodes() {

		assertEquals("CONF", classification(CaseClassification.CONFIRMED));
		assertEquals("POSS", classification(CaseClassification.SUSPECT));
		assertEquals("PROB", classification(CaseClassification.PROBABLE));
	}

	@Test
	@DisplayName("a case that is not classified, or is not a case, reports blank")
	public void unclassifiedReportsBlank() {

		assertNull(classification(CaseClassification.NOT_CLASSIFIED));
		assertNull(classification(CaseClassification.NO_CASE));
		assertNull(classification(null));
	}

	@Test
	@DisplayName("the two qualified CONFIRMED classifications report blank, which is correct for EpiPulse")
	public void qualifiedConfirmedClassificationsReportBlank() {

		assertNull(classification(CaseClassification.CONFIRMED_NO_SYMPTOMS));
		assertNull(classification(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS));
	}

	private static String classification(CaseClassification value) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setCaseClassification(value);

		return EpipulseMapping.caseClassification(entry);
	}

	// --- PlaceOfResidence ---

	@Test
	@DisplayName("PlaceOfResidence takes the finest NUTS level the address records")
	public void placeOfResidenceTakesTheFinestAddressLevel() {

		EpipulseDiseaseExportEntryDto entry = addressed("LU0001", "LU000", "LU00", "LU", "BE");
		assertEquals("LU0001", EpipulseMapping.placeOfResidence(entry));

		assertEquals("LU000", EpipulseMapping.placeOfResidence(addressed(null, "LU000", "LU00", "LU", "BE")));
		assertEquals("LU00", EpipulseMapping.placeOfResidence(addressed(null, null, "LU00", "LU", "BE")));
	}

	@Test
	@DisplayName("an address with only a country reports that country, not the server's")
	public void addressCountryOutranksTheServerCountry() {

		// a patient resident abroad: their own country is still a fact about the address, where the
		// server's country only says who is reporting
		assertEquals("BE", EpipulseMapping.placeOfResidence(addressed(null, null, null, "BE", "LU")));
	}

	@Test
	@DisplayName("the server country is used only when the address records nothing at all")
	public void serverCountryIsTheLastResort() {
		assertEquals("LU", EpipulseMapping.placeOfResidence(addressed(null, null, null, null, "LU")));
	}

	@Test
	@DisplayName("an empty NUTS code is skipped like a missing one")
	public void emptyNutsCodesAreSkipped() {
		assertEquals("LU00", EpipulseMapping.placeOfResidence(addressed("", "", "LU00", "LU", "BE")));
	}

	@Test
	@DisplayName("PlaceOfNotification has no country tier before the fallback")
	public void placeOfNotificationFallsStraightToTheServerCountry() {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setResponsibleRegionNutsCode("LU00");
		entry.setServerCountryNutsCode("LU");
		assertEquals("LU00", EpipulseMapping.placeOfNotification(entry));

		// responsibility is assigned within the reporting country, so there is no responsible
		// country to sit between the region and the fallback
		EpipulseDiseaseExportEntryDto noJurisdiction = new EpipulseDiseaseExportEntryDto();
		noJurisdiction.setServerCountryNutsCode("LU");
		assertEquals("LU", EpipulseMapping.placeOfNotification(noJurisdiction));
	}

	private static EpipulseDiseaseExportEntryDto addressed(String community, String district, String region, String country, String serverCountry) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setAddressCommunityNutsCode(community);
		entry.setAddressDistrictNutsCode(district);
		entry.setAddressRegionNutsCode(region);
		entry.setAddressCountryNutsCode(country);
		entry.setServerCountryNutsCode(serverCountry);

		return entry;
	}

	private static String status(EpipulseDiseaseExportEntryDto entry, int maxDoses) {
		return EpipulseMapping.vaccinationStatus(entry, maxDoses);
	}

	private static String lastVaccination(EpipulseDiseaseExportEntryDto entry) {
		return EpipulseMapping.dateOfLastVaccination(entry);
	}

	private static EpipulseDiseaseExportEntryDto entry(EpipulseImmunizationCheckDto... immunizations) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setSymptomOnsetDate(REFERENCE);
		entry.setImmunizations(new ArrayList<>(Arrays.asList(immunizations)));

		return entry;
	}

	/** One acquired vaccination course: when it started protecting, when it stops, how many doses. */
	private static EpipulseImmunizationCheckDto course(Date validFrom, Date validUntil, Integer doses) {
		return course(1L, validFrom, validUntil, doses);
	}

	/** The same, identified, for the cases where doses have to be attached to a named course. */
	private static EpipulseImmunizationCheckDto course(long id, Date validFrom, Date validUntil, Integer doses) {

		EpipulseImmunizationCheckDto immunization = new EpipulseImmunizationCheckDto();
		immunization.setId(id);
		immunization.setValidFrom(validFrom);
		immunization.setValidUntil(validUntil);
		immunization.setNumberOfDoses(doses);

		return immunization;
	}

	/** One dose under a named course, dated. */
	private static EpipulseVaccinationCheckDto dose(long course, Date administered) {

		EpipulseVaccinationCheckDto vaccination = new EpipulseVaccinationCheckDto();
		vaccination.setImmunizationId(course);
		vaccination.setVaccinationDate(administered);

		return vaccination;
	}

	private static EpipulseDiseaseExportEntryDto withDoses(EpipulseDiseaseExportEntryDto entry, EpipulseVaccinationCheckDto... doses) {

		entry.setVaccinations(new ArrayList<>(Arrays.asList(doses)));

		return entry;
	}
}
