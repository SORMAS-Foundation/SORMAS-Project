/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.epipulse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.utils.DateHelper;

/**
 * Tests for the multi-tier rules: which doses the two vaccination columns count and date and what
 * they report when there are none, which detection methods a case's tests amount to for a given
 * subject code, and which NUTS code the two place columns report.
 */
public class EpipulseMappingTest {

	/** The onset date every case below is measured against: 2024-03-10. */
	private static final Date REFERENCE = DateHelper.getDateZero(2024, 2, 10);

	private static final Date BEFORE = DateHelper.getDateZero(2023, 5, 1);
	private static final Date DOSE_OLDEST = DateHelper.getDateZero(2022, 5, 1);
	private static final Date DOSE_OLDER = DateHelper.getDateZero(2023, 0, 5);
	private static final Date DOSE_LATEST = DateHelper.getDateZero(2024, 1, 20);
	private static final Date AFTER = DateHelper.getDateZero(2024, 6, 1);

	private static final int STANDARD = 10;
	private static final int MENI = 4;
	private static final int MPOX = 3;

	// --- VaccinationStatus and DateOfLastVaccination, in the order the rules apply ---

	@Test
	@DisplayName("only vaccination and vaccination-recovery immunizations hold the patient's doses")
	public void onlyVaccinationMeansHoldDoses() {

		assertEquals(EnumSet.of(MeansOfImmunization.VACCINATION, MeansOfImmunization.VACCINATION_RECOVERY), EpipulseMapping.VACCINATION_DOSE_MEANS);

		for (MeansOfImmunization notTheirs : new MeansOfImmunization[] {
			MeansOfImmunization.MATERNAL_VACCINATION,
			MeansOfImmunization.MONOCLONAL_ANTIBODY }) {

			assertTrue(MeansOfImmunization.isVaccination(notTheirs), notTheirs.name());
			assertFalse(EpipulseMapping.VACCINATION_DOSE_MEANS.contains(notTheirs), notTheirs.name());
		}
	}

	// rule 1: no onset

	@Test
	@DisplayName("a case with no onset date reports both columns blank, whatever is on record")
	public void noOnsetReportsBlank() {

		// with no onset there is no date to have been vaccinated by; NOTVACC would assert the
		// patient was never vaccinated
		for (EpipulseDiseaseExportEntryDto entry : Arrays.asList(
			withDoses(entry(), dose(DOSE_OLDER)),
			entry(immunization(MeansOfImmunization.VACCINATION, BEFORE)),
			entry(immunization(MeansOfImmunization.NOT_IMMUNIZED, BEFORE)))) {

			entry.setSymptomOnsetDate(null);

			assertEquals("", status(entry, STANDARD));
			assertEquals("", lastVaccination(entry));
		}
	}

	// rule 2: doses up to the onset day

	@Test
	@DisplayName("the status is the number of doses received, and the date is the last of them")
	public void dosesAreCountedAndTheLastIsDated() {

		EpipulseDiseaseExportEntryDto entry = withDoses(entry(), dose(DOSE_OLDER), dose(DOSE_LATEST), dose(DOSE_OLDEST));

		assertEquals("3DOSE", status(entry, STANDARD));
		assertEquals("2024-02-20", lastVaccination(entry));
	}

	@Test
	@DisplayName("a dose given on the onset day counts, and is the last vaccination")
	public void aDoseOnTheOnsetDayCounts() {

		EpipulseDiseaseExportEntryDto entry = withDoses(entry(), dose(DOSE_OLDER), dose(REFERENCE));

		assertEquals("2DOSE", status(entry, STANDARD));
		assertEquals("2024-03-10", lastVaccination(entry));
	}

	@Test
	@DisplayName("a dose after the onset day is neither counted nor dated")
	public void aDoseAfterOnsetIsIgnored() {

		// vaccinated again after falling ill: real, but not their status when they fell ill
		EpipulseDiseaseExportEntryDto entry = withDoses(entry(), dose(DOSE_OLDER), dose(DOSE_LATEST), dose(AFTER));

		assertEquals("2DOSE", status(entry, STANDARD));
		assertEquals("2024-02-20", lastVaccination(entry));
	}

	@Test
	@DisplayName("doses under different immunizations are added together")
	public void dosesUnderDifferentImmunizationsAreAdded() {

		// The doses arrive flat, whatever immunization they were recorded under: a course followed
		// by another shot recorded separately is still three doses received.
		EpipulseDiseaseExportEntryDto entry = withDoses(
			entry(immunization(MeansOfImmunization.VACCINATION, DOSE_OLDEST), immunization(MeansOfImmunization.VACCINATION, DOSE_LATEST)),
			dose(DOSE_OLDEST),
			dose(DOSE_OLDER),
			dose(DOSE_LATEST));

		assertEquals("3DOSE", status(entry, STANDARD));
	}

	@Test
	@DisplayName("two doses on the same day count once")
	public void dosesOnTheSameDayCountOnce() {

		// Guard against the same dose recorded under two immunizations.
		EpipulseDiseaseExportEntryDto entry = withDoses(entry(), dose(DOSE_OLDER), dose(DOSE_LATEST), dose(DOSE_LATEST));

		assertEquals("2DOSE", status(entry, STANDARD));
	}

	@Test
	@DisplayName("a dose with no date of administration is dated by its report date")
	public void anUndatedDoseUsesItsReportDate() {

		EpipulseVaccinationCheckDto undated = new EpipulseVaccinationCheckDto();
		undated.setReportDate(DOSE_LATEST);

		EpipulseDiseaseExportEntryDto entry = withDoses(entry(), dose(DOSE_OLDER), undated);

		assertEquals("2DOSE", status(entry, STANDARD));
		assertEquals("2024-02-20", lastVaccination(entry));
	}

	@Test
	@DisplayName("a dose with neither date is skipped rather than blanking the columns")
	public void aDoseWithNoDatesIsSkipped() {

		EpipulseDiseaseExportEntryDto entry = withDoses(entry(), new EpipulseVaccinationCheckDto(), dose(DOSE_LATEST));

		assertEquals("1DOSE", status(entry, STANDARD));
		assertEquals("2024-02-20", lastVaccination(entry));
	}

	@Test
	@DisplayName("a count above the subject code's scale reports that code's highest value")
	public void countAboveTheScaleReportsTheCap() {

		EpipulseDiseaseExportEntryDto fiveDoses = withDoses(
			entry(),
			dose(DateHelper.getDateZero(2020, 0, 1)),
			dose(DateHelper.getDateZero(2020, 1, 1)),
			dose(DateHelper.getDateZero(2020, 2, 1)),
			dose(DateHelper.getDateZero(2020, 3, 1)),
			dose(DateHelper.getDateZero(2020, 4, 1)));

		// the same doses under three scales: the cap is the only thing that differs between
		// diseases, which is why they share one method
		assertEquals("5DOSE", status(fiveDoses, STANDARD));
		assertEquals("4DOSE", status(fiveDoses, MENI));
		assertEquals("3DOSE", status(fiveDoses, MPOX));
	}

	@Test
	@DisplayName("a count at the cap is reported as itself, not as an overflow")
	public void countAtTheCapIsExact() {

		EpipulseDiseaseExportEntryDto fourDoses = withDoses(
			entry(),
			dose(DateHelper.getDateZero(2020, 0, 1)),
			dose(DateHelper.getDateZero(2020, 1, 1)),
			dose(DateHelper.getDateZero(2020, 2, 1)),
			dose(DateHelper.getDateZero(2020, 3, 1)));

		assertEquals("4DOSE", status(fourDoses, MENI));
	}

	// rule 3: doses on record, all after onset

	@Test
	@DisplayName("doses on record that all came after onset make the patient NOTVACC")
	public void dosesOnlyAfterOnsetAreNotVacc() {

		// the dates themselves show no dose had been given by onset: evidence, not a missing record
		EpipulseDiseaseExportEntryDto entry = withDoses(entry(immunization(MeansOfImmunization.VACCINATION, AFTER)), dose(AFTER));

		assertEquals("NOTVACC", status(entry, STANDARD));
		assertEquals("", lastVaccination(entry));
	}

	@Test
	@DisplayName("any dose on record rules out UNKDOSE, even one given after onset")
	public void anyDoseRulesOutUnknownDose() {

		// UNKDOSE is for a vaccination recorded with no doses entered at all. Here doses were
		// entered; they just came later.
		EpipulseDiseaseExportEntryDto entry = withDoses(entry(immunization(MeansOfImmunization.VACCINATION, BEFORE)), dose(AFTER));

		assertEquals("NOTVACC", status(entry, STANDARD));
	}

	// rule 4: a vaccination recorded with no doses

	@Test
	@DisplayName("a vaccination recorded up to onset with no doses entered is UNKDOSE")
	public void aVaccinationWithoutDosesIsUnknownDose() {

		// exactly what UNKDOSE is for: vaccinated, number of doses unknown
		assertEquals("UNKDOSE", status(entry(immunization(MeansOfImmunization.VACCINATION, BEFORE)), STANDARD));
		assertEquals("UNKDOSE", status(entry(immunization(MeansOfImmunization.VACCINATION_RECOVERY, BEFORE)), STANDARD));
		assertEquals("UNKDOSE", status(entry(immunization(MeansOfImmunization.VACCINATION, REFERENCE)), STANDARD));

		assertEquals("", lastVaccination(entry(immunization(MeansOfImmunization.VACCINATION, BEFORE))));
	}

	@Test
	@DisplayName("a vaccination recorded after onset with no doses entered says nothing")
	public void aVaccinationAfterOnsetWithoutDosesIsBlank() {

		// it may well be vaccination given after the illness, so it is no evidence either way
		assertEquals("", status(entry(immunization(MeansOfImmunization.VACCINATION, AFTER)), STANDARD));
	}

	@Test
	@DisplayName("a vaccination is dated by its start date, and by its report date only without one")
	public void aVaccinationIsDatedByItsStartDate() {

		assertEquals("UNKDOSE", status(entry(immunization(MeansOfImmunization.VACCINATION, null, BEFORE)), STANDARD));

		// started after onset, recorded before it: the start date is the one that matters
		assertEquals("", status(entry(immunization(MeansOfImmunization.VACCINATION, AFTER, BEFORE)), STANDARD));
	}

	@Test
	@DisplayName("an immunization by other means is never read as a vaccination")
	public void otherMeansAreNotVaccinations() {

		for (MeansOfImmunization means : new MeansOfImmunization[] {
			MeansOfImmunization.RECOVERY,
			MeansOfImmunization.OTHER,
			MeansOfImmunization.MATERNAL_VACCINATION,
			MeansOfImmunization.MONOCLONAL_ANTIBODY }) {

			assertEquals("", status(entry(immunization(means, BEFORE)), STANDARD), means.name());
		}
	}

	// rule 5: an explicit record of no immunization

	@Test
	@DisplayName("a NOT_IMMUNIZED record makes the patient NOTVACC, whenever it was recorded")
	public void notImmunizedIsNotVacc() {

		assertEquals("NOTVACC", status(entry(immunization(MeansOfImmunization.NOT_IMMUNIZED, BEFORE)), STANDARD));
		assertEquals("NOTVACC", status(entry(immunization(MeansOfImmunization.NOT_IMMUNIZED, AFTER)), STANDARD));
	}

	@Test
	@DisplayName("recorded doses and recorded vaccinations outweigh a NOT_IMMUNIZED record")
	public void evidenceOfVaccinationOutweighsNotImmunized() {

		EpipulseDiseaseExportEntryDto withDose = withDoses(entry(immunization(MeansOfImmunization.NOT_IMMUNIZED, BEFORE)), dose(DOSE_OLDER));
		assertEquals("1DOSE", status(withDose, STANDARD));

		EpipulseDiseaseExportEntryDto withVaccination =
			entry(immunization(MeansOfImmunization.NOT_IMMUNIZED, BEFORE), immunization(MeansOfImmunization.VACCINATION, BEFORE));
		assertEquals("UNKDOSE", status(withVaccination, STANDARD));
	}

	// rule 6: nothing recorded

	@Test
	@DisplayName("a patient with nothing recorded reports blank, not NOTVACC")
	public void nothingRecordedIsBlank() {

		// the absence of a record is not evidence of non-vaccination - the same reading SORMAS
		// applies for Luxembourg (BR0080LU)
		assertEquals("", status(entry(), STANDARD));
		assertEquals("", lastVaccination(entry()));
	}

	// --- VaccinationStatusMaternal ---

	@Test
	@DisplayName("a MATERNAL_VACCINATION record reports true for a patient under 2")
	public void maternalVaccinationIsTrueUnderTwo() {

		// the record's dates are not checked: a maternal vaccination precedes birth, so it always
		// precedes onset
		assertEquals("true", maternal(bornOn(2023, 6, 1, immunization(MeansOfImmunization.MATERNAL_VACCINATION, BEFORE))));
		assertEquals("true", maternal(bornOn(2023, 6, 1, immunization(MeansOfImmunization.MATERNAL_VACCINATION, AFTER))));
		assertEquals("true", maternal(bornOn(2023, 6, 1, immunization(MeansOfImmunization.MATERNAL_VACCINATION, null, null))));
	}

	@Test
	@DisplayName("VaccinationStatusMaternal is blank from the second birthday, measured as Age measures it")
	public void maternalVaccinationIsBlankFromTwo() {

		// REFERENCE (the onset) is 2024-03-10: born a day later than two years before is still 1
		assertEquals("true", maternal(bornOn(2022, 3, 11, immunization(MeansOfImmunization.MATERNAL_VACCINATION, BEFORE))));
		assertEquals("", maternal(bornOn(2022, 3, 10, immunization(MeansOfImmunization.MATERNAL_VACCINATION, BEFORE))));
		assertEquals("", maternal(bornOn(1990, 6, 15, immunization(MeansOfImmunization.MATERNAL_VACCINATION, BEFORE))));
	}

	@Test
	@DisplayName("VaccinationStatusMaternal is blank when the age cannot be established")
	public void maternalVaccinationNeedsAnAge() {
		assertEquals("", maternal(entry(immunization(MeansOfImmunization.MATERNAL_VACCINATION, BEFORE))));
	}

	@Test
	@DisplayName("VaccinationStatusMaternal is never false: no other record speaks for the mother")
	public void maternalVaccinationIsNeverFalse() {

		assertEquals("", maternal(bornOn(2023, 6, 1)));

		for (MeansOfImmunization means : MeansOfImmunization.values()) {
			if (means != MeansOfImmunization.MATERNAL_VACCINATION) {
				assertEquals("", maternal(bornOn(2023, 6, 1, immunization(means, BEFORE))), means.name());
			}
		}
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
	@DisplayName("the two qualified CONFIRMED classifications report CONF, like a plain confirmation")
	public void qualifiedConfirmedClassificationsReportConfirmed() {

		// Symptom-qualified confirmations are still confirmed in EpiPulse.
		assertEquals("CONF", classification(CaseClassification.CONFIRMED_NO_SYMPTOMS));
		assertEquals("CONF", classification(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS));
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
		entry.setServerCountryLocationCode("LU");
		assertEquals("LU00", EpipulseMapping.placeOfNotification(entry));

		// responsibility is assigned within the reporting country, so there is no responsible
		// country to sit between the region and the fallback
		EpipulseDiseaseExportEntryDto noJurisdiction = new EpipulseDiseaseExportEntryDto();
		noJurisdiction.setServerCountryLocationCode("LU");
		assertEquals("LU", EpipulseMapping.placeOfNotification(noJurisdiction));
	}

	private static EpipulseDiseaseExportEntryDto addressed(String community, String district, String region, String country, String serverCountry) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setAddressCommunityNutsCode(community);
		entry.setAddressDistrictNutsCode(district);
		entry.setAddressRegionNutsCode(region);
		entry.setAddressCountryLocationCode(country);
		entry.setServerCountryLocationCode(serverCountry);

		return entry;
	}

	private static String status(EpipulseDiseaseExportEntryDto entry, int maxDoses) {
		return EpipulseMapping.vaccinationStatus(entry, maxDoses);
	}

	private static String lastVaccination(EpipulseDiseaseExportEntryDto entry) {
		return EpipulseMapping.dateOfLastVaccination(entry);
	}

	private static String maternal(EpipulseDiseaseExportEntryDto entry) {
		return EpipulseMapping.vaccinationStatusMaternal(entry);
	}

	/** An {@link #entry} for a patient born on the given day (month 1-12). */
	private static EpipulseDiseaseExportEntryDto bornOn(int year, int month, int day, EpipulseImmunizationCheckDto... immunizations) {

		EpipulseDiseaseExportEntryDto entry = entry(immunizations);
		entry.setYearOfBirth(year);
		entry.setMonthOfBirth(month);
		entry.setDayOfBirth(day);

		return entry;
	}

	private static EpipulseDiseaseExportEntryDto entry(EpipulseImmunizationCheckDto... immunizations) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setSymptomOnsetDate(REFERENCE);
		entry.setImmunizations(new ArrayList<>(Arrays.asList(immunizations)));

		return entry;
	}

	/** One immunization started - and recorded - on the given date. */
	private static EpipulseImmunizationCheckDto immunization(MeansOfImmunization means, Date started) {
		return immunization(means, started, started);
	}

	private static EpipulseImmunizationCheckDto immunization(MeansOfImmunization means, Date started, Date reported) {

		EpipulseImmunizationCheckDto immunization = new EpipulseImmunizationCheckDto();
		immunization.setMeansOfImmunization(means);
		immunization.setStartDate(started);
		immunization.setReportDate(reported);

		return immunization;
	}

	/** One dose, dated. Which immunization it was given under does not matter to the rule. */
	private static EpipulseVaccinationCheckDto dose(Date administered) {

		EpipulseVaccinationCheckDto vaccination = new EpipulseVaccinationCheckDto();
		vaccination.setVaccinationDate(administered);

		return vaccination;
	}

	private static EpipulseDiseaseExportEntryDto withDoses(EpipulseDiseaseExportEntryDto entry, EpipulseVaccinationCheckDto... doses) {

		entry.setVaccinations(new ArrayList<>(Arrays.asList(doses)));

		return entry;
	}
}
