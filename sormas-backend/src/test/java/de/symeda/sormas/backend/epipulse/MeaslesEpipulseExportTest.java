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

package de.symeda.sormas.backend.epipulse;

import static de.symeda.sormas.api.epipulse.EpipulseVariable.AGE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.AGE_MONTH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CASE_CLASSIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CAUSE_OF_DEATH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLUSTER_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLUSTER_RELATED;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLUSTER_SETTING;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COMPLICATION_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_INVESTIGATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_LAB_RESULT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_LAST_VACCINATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ONSET;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_SPECIMEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENOTYPE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HOSPITALISATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.IMPORTED_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_IG_G;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_IG_M;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_VIR_DETECT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SPECIMEN_SERO;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SPECIMEN_VIR_DETECT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.VACCINATION_STATUS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Tests the Measles export against a real database.
 * 
 * All 36 EpiPulse variables for MEAS are derived - no declared-blank columns.
 * Tests what {@code MeasSql} decides: which specimens count for serology vs. general collection,
 * which tests each laboratory column selects, and which exposures fall in the incubation window.
 * 
 * MEAS has five repeatable groups, four floored with {@code ValueDef.atLeastOneColumn}.
 * A case with nothing recorded keeps its declared column rather than an empty group.
 * 
 * Dates are fixed, not relative to today. SORMAS assigns measles a 7-21 day incubation period,
 * so exposures count for {@code PlaceOfInfection} when overlapping [2024-05-20, 2024-06-03].
 */
public class MeaslesEpipulseExportTest extends AbstractEpipulseExportTest {

	// Five repeatable groups in declaration order
	private static final EpipulseVariable[] REPEATABLE_GROUPS = {
		COMPLICATION_DIAGNOSIS,
		CLUSTER_SETTING,
		PLACE_OF_INFECTION,
		SPECIMEN_VIR_DETECT,
		SPECIMEN_SERO };

	/**
	 * Four groups that would narrow to nothing without {@code ValueDef.atLeastOneColumn}.
	 * {@code ComplicationDiagnosis} reports NONE for cases without complications, so it needs no floor.
	 */
	private static final EpipulseVariable[] FLOORED_GROUPS = {
		CLUSTER_SETTING,
		PLACE_OF_INFECTION,
		SPECIMEN_VIR_DETECT,
		SPECIMEN_SERO };

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	private static final Date INVESTIGATED = DateHelper.getDateZero(2024, 5, 11);

	// Inside incubation window [2024-05-20, 2024-06-03]
	private static final Date EXPOSURE_START = DateHelper.getDateZero(2024, 4, 25);
	private static final Date EXPOSURE_END = DateHelper.getDateZero(2024, 4, 30);

	private static final Date FIRST_SPECIMEN = DateHelper.getDateZero(2024, 5, 11);
	private static final Date SECOND_SPECIMEN = DateHelper.getDateZero(2024, 5, 12);

	private static final Date EARLIER_TEST = DateHelper.getDateZero(2024, 5, 12);
	private static final Date LATER_TEST = DateHelper.getDateZero(2024, 5, 13);

	private static final Date FIRST_DOSE = DateHelper.getDateZero(2023, 0, 15);
	private static final Date LAST_DOSE = DateHelper.getDateZero(2023, 5, 15);

	// Wide enough to contain onset, which makes vaccination count for a case
	private static final Date COURSE_VALID_FROM = DateHelper.getDateZero(2023, 0, 10);
	private static final Date COURSE_VALID_UNTIL = DateHelper.getDateZero(2030, 0, 10);

	/**
	 * Mapping of all cluster types to their EpiPulse codes, null when EpiPulse has no value.
	 * Defined explicitly rather than derived from {@code EpipulseClusterSettingRef}
	 * to verify actual mappings rather than circular consistency.
	 */
	private static final Map<ClusterType, String> CLUSTER_SETTING_CODES = new LinkedHashMap<>();

	static {
		CLUSTER_SETTING_CODES.put(ClusterType.KINDERGARTEN_OR_CHILDCARE, "CHILDCARE");
		CLUSTER_SETTING_CODES.put(ClusterType.FAMILY, "FAM");
		CLUSTER_SETTING_CODES.put(ClusterType.MILITARY, "MIL");
		CLUSTER_SETTING_CODES.put(ClusterType.NOSOCOMIAL, "NOS");
		CLUSTER_SETTING_CODES.put(ClusterType.SCHOOL, "SCH");
		CLUSTER_SETTING_CODES.put(ClusterType.SPORTS_TEAM, "SPORT");
		CLUSTER_SETTING_CODES.put(ClusterType.UNIVERSITY, "UNI");
		CLUSTER_SETTING_CODES.put(ClusterType.OTHER, "OTH");
		// OTH means setting exists but not in EpiPulse's list.
		// These mean no setting or unknown setting, so report nothing.
		CLUSTER_SETTING_CODES.put(ClusterType.UNKNOWN, null);
		CLUSTER_SETTING_CODES.put(ClusterType.NOT_APPLICABLE, null);
	}

	private CaseFixture fixture;
	private CountryReferenceDto france;
	private CountryReferenceDto italy;

	@BeforeEach
	public void setUpMeaslesExport() {

		configureLuxembourgFor(EpipulseSubjectCode.MEAS);

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		fixture = fixtureFor(Disease.MEASLES, rdcf, creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		france = countryWithNutsCode("France", "FRA", "FR");
		italy = countryWithNutsCode("Italy", "ITA", "IT");
	}

	@Test
	@DisplayName("a fully populated case fills every column MEAS derives")
	public void aFullyPopulatedCaseFillsEveryColumn() {

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setOutcome(CaseOutcome.RECOVERED);
			c.setInvestigatedDate(INVESTIGATED);
			c.setClinicalConfirmation(YesNoUnknown.YES);
			c.getSymptoms().setOtitisMedia(SymptomState.YES);
			c.getSymptoms().setPneumoniaClinicalOrRadiologic(SymptomState.YES);
			c.getEpiData().setClusterRelated(true);
			c.getEpiData().setClusterTypeText("LU-MEAS-2024-07");
			c.getEpiData().setClusterType(ClusterType.SCHOOL);
			c.getEpiData().setCaseImportedStatus(CaseImportedStatus.IMPORTED_CASE);
			c.getEpiData().getExposures().add(travelTo(france));
			hospitalisedForTheReportedDisease().accept(c);
		});

		fixture.completedCourse(person, 2, COURSE_VALID_FROM, COURSE_VALID_UNTIL, FIRST_DOSE, LAST_DOSE);

		// Two specimens, each with serum for serology, swab for virus detection and genotype
		SampleDto serum = fixture.specimen(caze, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(serum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, EARLIER_TEST);

		SampleDto swab = fixture.specimen(caze, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN);
		fixture.testOn(
			swab,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST,
			test -> test.setGenoType(GenoType.GENOTYPE_D8));

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto entry = export.only();

		// from the configuration rather than the case
		assertThat(export.value(entry, DISEASE), is("MEAS"));
		assertThat(export.value(entry, SUBJECT_CODE), is("MEAS"));
		assertThat(export.value(entry, REPORTING_COUNTRY), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, DATA_SOURCE), is(SERVER_DATA_SOURCE));

		// from the case record itself
		assertThat(export.value(entry, STATUS), is("NEW/UPDATE"));
		assertThat(export.value(entry, NATIONAL_RECORD_ID), is(caze.getUuid()));
		assertThat(export.value(entry, CASE_CLASSIFICATION), is("CONF"));
		assertThat(export.value(entry, OUTCOME), is("A"));
		assertThat(export.value(entry, DATE_OF_ONSET), is("2024-06-10"));
		assertThat(export.value(entry, DATE_OF_NOTIFICATION), is("2024-06-10"));
		assertThat(export.value(entry, DATE_OF_INVESTIGATION), is("2024-06-11"));
		assertThat(export.value(entry, HOSPITALISATION), is("true"));
		assertThat(export.value(entry, CLINICAL_CRITERIA_STATUS), is("true"));
		assertThat(export.values(entry, COMPLICATION_DIAGNOSIS), contains("OME", "PNEU"));

		// worked out from the tests: the IgM on the 12th is the oldest positive one
		assertThat(export.value(entry, DATE_USED_FOR_STATISTICS), is("2024-06-12"));

		// from the person and their address
		assertThat(export.value(entry, GENDER), is("F"));
		assertThat(export.value(entry, AGE), is("33"));
		assertThat(export.value(entry, AGE_MONTH), is(blankOrNullString()));
		assertThat(export.value(entry, PLACE_OF_RESIDENCE), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, PLACE_OF_NOTIFICATION), is(SERVER_COUNTRY_NUTS_CODE));

		// the patient is alive, so there is no event leading to death to describe
		assertThat(export.value(entry, CAUSE_OF_DEATH), is(blankOrNullString()));

		// from the epidemiological data
		assertThat(export.value(entry, CLUSTER_RELATED), is("true"));
		assertThat(export.value(entry, CLUSTER_ID), is("LU-MEAS-2024-07"));
		assertThat(export.values(entry, CLUSTER_SETTING), contains("SCH"));
		assertThat(export.value(entry, IMPORTED_STATUS), is("IMP"));
		assertThat(export.values(entry, PLACE_OF_INFECTION), contains("FR"));

		// the vaccination summary pair
		assertThat(export.value(entry, VACCINATION_STATUS), is("2DOSE"));
		assertThat(export.value(entry, DATE_OF_LAST_VACCINATION), is("2023-06-15"));

		// MEAS's own laboratory columns
		assertThat(export.value(entry, DATE_OF_SPECIMEN), is("2024-06-11"));
		assertThat(export.value(entry, DATE_OF_LAB_RESULT), is("2024-06-13"));
		assertThat(export.values(entry, SPECIMEN_VIR_DETECT), contains("NASALSWAB", "SER"));
		assertThat(export.values(entry, SPECIMEN_SERO), contains("SER"));
		assertThat(export.value(entry, RESULT_VIR_DETECT), is("POS"));
		assertThat(export.value(entry, GENOTYPE), is("MEASV_D8"));
		assertThat(export.value(entry, RESULT_IG_M), is("POS"));
		assertThat(export.value(entry, RESULT_IG_G), is(blankOrNullString()));
	}

	/**
	 * Every complication in one export, one case per scenario.
	 * Split into separate tests would multiply database cycles without adding assertions.
	 */
	@Test
	@DisplayName("complications accumulate, and a case with none reports NONE rather than nothing")
	public void complicationsAccumulateAndACaseWithNoneReportsNone() {

		List<Scenario> scenarios = new ArrayList<>();

		scenarios.add(complications("acute encephalitis", symptoms -> symptoms.setAcuteEncephalitis(SymptomState.YES), "ACENCE"));
		scenarios.add(complications("diarrhoea", symptoms -> symptoms.setDiarrhea(SymptomState.YES), "DIARR"));
		scenarios.add(complications("otitis media", symptoms -> symptoms.setOtitisMedia(SymptomState.YES), "OME"));
		scenarios.add(complications("pneumonia", symptoms -> symptoms.setPneumoniaClinicalOrRadiologic(SymptomState.YES), "PNEU"));
		scenarios.add(complications("another complication", symptoms -> symptoms.setOtherComplications(SymptomState.YES), "OTH"));

		// Repeatable: cases don't compete, each reports one column in reader declaration order
		scenarios.add(complications("all five at once", symptoms -> {
			symptoms.setOtherComplications(SymptomState.YES);
			symptoms.setPneumoniaClinicalOrRadiologic(SymptomState.YES);
			symptoms.setOtitisMedia(SymptomState.YES);
			symptoms.setDiarrhea(SymptomState.YES);
			symptoms.setAcuteEncephalitis(SymptomState.YES);
		}, "ACENCE", "DIARR", "OME", "PNEU", "OTH"));

		// NONE means no complications reported, not a blank. Only YES counts as a complication.
		scenarios.add(complications("nothing recorded", symptoms -> {
		}, "NONE"));

		scenarios.add(complications("every complication ruled out", symptoms -> {
			symptoms.setAcuteEncephalitis(SymptomState.NO);
			symptoms.setDiarrhea(SymptomState.NO);
			symptoms.setOtitisMedia(SymptomState.UNKNOWN);
			symptoms.setPneumoniaClinicalOrRadiologic(SymptomState.NO);
			symptoms.setOtherComplications(SymptomState.UNKNOWN);
		}, "NONE"));

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		assertThat(export.all(), hasSize(scenarios.size()));

		// Group width set by widest case; all others padded to match
		for (Scenario scenario : scenarios) {
			List<String> reported = export.values(export.entryFor(scenario.caseUuid), COMPLICATION_DIAGNOSIS);

			assertThat(
				"ComplicationDiagnosis for a case with " + scenario.label,
				reported.subList(0, scenario.expected.size()),
				is(scenario.expected));
		}
	}

	/**
	 * {@code CauseOfDeath} reports the event only if death was related to measles.
	 * SORMAS records free text beside a cause and disease; text alone doesn't establish relation.
	 */
	@Test
	@DisplayName("CauseOfDeath is reported only when the person died of measles")
	public void causeOfDeathIsReportedOnlyWhenThePersonDiedOfMeasles() {

		CaseDataDto died = deceasedCase("Respiratory failure  ", CauseOfDeath.EPIDEMIC_DISEASE, Disease.MEASLES);

		// Died of epidemic disease, but not measles
		CaseDataDto diedOfSomethingElse =
			deceasedCase("Meningococcal sepsis", CauseOfDeath.EPIDEMIC_DISEASE, Disease.INVASIVE_MENINGOCOCCAL_INFECTION);

		// Stale causeofdeathdisease left when cause was changed to OTHER_CAUSE must not read as measles death
		CaseDataDto staleDisease = deceasedCase("Road traffic accident", CauseOfDeath.OTHER_CAUSE, Disease.MEASLES);

		CaseDataDto alive = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		// Trimmed: the column carries a description, and the surrounding whitespace is not part of it.
		assertThat(export.value(export.entryFor(died.getUuid()), CAUSE_OF_DEATH), is("Respiratory failure"));

		assertThat(export.value(export.entryFor(diedOfSomethingElse.getUuid()), CAUSE_OF_DEATH), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(staleDisease.getUuid()), CAUSE_OF_DEATH), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(alive.getUuid()), CAUSE_OF_DEATH), is(blankOrNullString()));
	}

	/**
	 * {@code ClinicalCriteriaStatus} is typed BOOL with no third state.
	 * UNKNOWN reports nothing (not false) because uncertain and unmet are different claims.
	 */
	@Test
	@DisplayName("ClinicalCriteriaStatus reports unknown as nothing rather than as false")
	public void clinicalCriteriaStatusReportsUnknownAsNothing() {

		CaseDataDto met = caseWithClinicalConfirmation(YesNoUnknown.YES);
		CaseDataDto notMet = caseWithClinicalConfirmation(YesNoUnknown.NO);
		CaseDataDto unknown = caseWithClinicalConfirmation(YesNoUnknown.UNKNOWN);
		CaseDataDto unanswered = caseWithClinicalConfirmation(null);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(met.getUuid()), CLINICAL_CRITERIA_STATUS), is("true"));
		assertThat(export.value(export.entryFor(notMet.getUuid()), CLINICAL_CRITERIA_STATUS), is("false"));
		assertThat(export.value(export.entryFor(unknown.getUuid()), CLINICAL_CRITERIA_STATUS), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(unanswered.getUuid()), CLINICAL_CRITERIA_STATUS), is(blankOrNullString()));
	}

	/**
	 * {@code SpecimenVirDetect} reports all specimens collected; {@code SpecimenSero} only
	 * those with a serology test. Two populations, one for general collection, one for serology.
	 */
	@Test
	@DisplayName("SpecimenVirDetect reports every specimen collected, SpecimenSero only those carrying a serology test")
	public void theSpecimenGroupsTellCollectedApartFromCollectedForSerology() {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);

		SampleDto serum = fixture.specimen(caze, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(serum, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, EARLIER_TEST);

		SampleDto swab = fixture.specimen(caze, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN);
		fixture.testOn(swab, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, LATER_TEST);

		// Collected, never tested: still in SpecimenVirDetect (collected); nothing says it was for serology
		fixture.specimen(caze, SampleMaterial.URINE, SECOND_SPECIMEN);

		// Reports materials, not specimens (two serum samples = one SER entry)
		SampleDto secondSerum = fixture.specimen(caze, SampleMaterial.SERA, SECOND_SPECIMEN);
		fixture.testOn(secondSerum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, LATER_TEST);

		CaseDataDto noSpecimen = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto entry = export.entryFor(caze.getUuid());

		assertThat(export.values(entry, SPECIMEN_VIR_DETECT), contains("NASALSWAB", "SER", "URINE"));
		assertThat(export.values(entry, SPECIMEN_SERO), contains("SER"));

		// Both floored: cases with nothing keep the columns the file declares
		EpipulseDiseaseExportEntryDto nothing = export.entryFor(noSpecimen.getUuid());

		assertThat(export.values(nothing, SPECIMEN_VIR_DETECT).get(0), is(blankOrNullString()));
		assertThat(export.values(nothing, SPECIMEN_SERO).get(0), is(blankOrNullString()));
	}

	/**
	 * {@code DateOfSpecimen}: earliest specimen (any). {@code DateOfLabResult}: earliest
	 * virus-detection test. Different populations, both "earliest".
	 */
	@Test
	@DisplayName("DateOfLabResult is the earliest virus-detection test, dated by test date, report date, then creation")
	public void dateOfLabResultIsTheEarliestVirusDetectionTest() {

		// IFA counts toward date but not toward ResultVirDetect; these lists differ
		CaseDataDto earliestIsAnAssay = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(earliestIsAnAssay, SampleMaterial.SERA, FIRST_SPECIMEN),
			PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
			PathogenTestResultType.POSITIVE,
			true,
			EARLIER_TEST);
		fixture.testOn(
			fixture.specimen(earliestIsAnAssay, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN),
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST);

		// No virus detection, only serology: no date to report
		CaseDataDto serologyOnly = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(serologyOnly, SampleMaterial.SERA, FIRST_SPECIMEN),
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestResultType.POSITIVE,
			true,
			EARLIER_TEST);

		// Test without date must sort against others (fallback to report date, then creation)
		CaseDataDto undatedTest = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(undatedTest, SampleMaterial.NASAL_SWAB, FIRST_SPECIMEN),
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			null,
			test -> test.setReportDate(EARLIER_TEST));

		CaseDataDto noTest = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.specimen(noTest, SampleMaterial.SERA, FIRST_SPECIMEN);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(earliestIsAnAssay.getUuid()), DATE_OF_LAB_RESULT), is("2024-06-12"));
		assertThat(export.value(export.entryFor(serologyOnly.getUuid()), DATE_OF_LAB_RESULT), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(undatedTest.getUuid()), DATE_OF_LAB_RESULT), is("2024-06-12"));
		assertThat(export.value(export.entryFor(noTest.getUuid()), DATE_OF_LAB_RESULT), is(blankOrNullString()));

		// Specimen date is earliest specimen, whether tested or not
		assertThat(export.value(export.entryFor(noTest.getUuid()), DATE_OF_SPECIMEN), is("2024-06-11"));
		assertThat(export.value(export.entryFor(earliestIsAnAssay.getUuid()), DATE_OF_SPECIMEN), is("2024-06-11"));
	}

	/**
	 * {@code ResultVirDetect} asks for the validated result of virus detection.
	 * Unverified tests are not answers to this variable.
	 */
	@Test
	@DisplayName("ResultVirDetect reports the earliest verified test, from the test types the variable covers")
	public void resultVirDetectReportsTheEarliestVerifiedTest() {

		// Earlier unverified, later verified: later verified result wins despite disagreement
		CaseDataDto unverifiedFirst = virusDetectionCase(PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, false, EARLIER_TEST);
		fixture.testOn(
			fixture.specimen(unverifiedFirst, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN),
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.NEGATIVE,
			true,
			LATER_TEST);

		// Nothing verified at all: the case has a result on file and the column is still empty.
		CaseDataDto nothingVerified = virusDetectionCase(PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, false, EARLIER_TEST);

		// IFA dates the result but doesn't answer this variable; Sanger answers but doesn't date
		CaseDataDto assayOnly =
			virusDetectionCase(PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY, PathogenTestResultType.POSITIVE, true, EARLIER_TEST);
		CaseDataDto sequenced = virusDetectionCase(PathogenTestType.SANGER_SEQUENCING, PathogenTestResultType.POSITIVE, true, EARLIER_TEST);

		CaseDataDto indeterminate = virusDetectionCase(PathogenTestType.CULTURE, PathogenTestResultType.INDETERMINATE, true, EARLIER_TEST);
		CaseDataDto notDone = virusDetectionCase(PathogenTestType.ISOLATION, PathogenTestResultType.NOT_DONE, true, EARLIER_TEST);

		CaseDataDto untested = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(unverifiedFirst.getUuid()), RESULT_VIR_DETECT), is("NEG"));
		assertThat(export.value(export.entryFor(nothingVerified.getUuid()), RESULT_VIR_DETECT), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(assayOnly.getUuid()), RESULT_VIR_DETECT), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(sequenced.getUuid()), RESULT_VIR_DETECT), is("POS"));
		assertThat(export.value(export.entryFor(indeterminate.getUuid()), RESULT_VIR_DETECT), is("EQUI"));
		assertThat(export.value(export.entryFor(notDone.getUuid()), RESULT_VIR_DETECT), is("NOTEST"));
		assertThat(export.value(export.entryFor(untested.getUuid()), RESULT_VIR_DETECT), is(blankOrNullString()));
	}

	/**
	 * Genotype is a laboratory finding; a wrong one is worse than absent.
	 * Unknown genotypes (not in EpiPulse list) report nothing.
	 */
	@Test
	@DisplayName("Genotype comes from the oldest test carrying one, and reports nothing for a genotype EpiPulse does not list")
	public void genotypeComesFromTheOldestTestCarryingOne() {

		CaseDataDto typedTwice = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(typedTwice, SampleMaterial.NASAL_SWAB, FIRST_SPECIMEN),
			PathogenTestType.SEQUENCING,
			PathogenTestResultType.POSITIVE,
			true,
			EARLIER_TEST,
			test -> test.setGenoType(GenoType.GENOTYPE_B3));
		fixture.testOn(
			fixture.specimen(typedTwice, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN),
			PathogenTestType.SEQUENCING,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST,
			test -> test.setGenoType(GenoType.GENOTYPE_H1));

		// SORMAS records plain genotype B; EpiPulse lists B1, B2, B3 only
		CaseDataDto unlisted = genotypedCase(GenoType.GENOTYPE_B);

		// Wrong test type but genotypes follow genotypes, not test types
		CaseDataDto untyped = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(untyped, SampleMaterial.NASAL_SWAB, FIRST_SPECIMEN),
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			EARLIER_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(typedTwice.getUuid()), GENOTYPE), is("MEASV_B3"));
		assertThat(export.value(export.entryFor(unlisted.getUuid()), GENOTYPE), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(untyped.getUuid()), GENOTYPE), is(blankOrNullString()));
	}

	/**
	 * Serology columns read independently, each from oldest test of its kind.
	 * Fourfold antibody rise in paired samples reports positive per EpiPulse definition,
	 * regardless of test result.
	 */
	@Test
	@DisplayName("the serology columns come from the oldest test of each kind, and a fourfold titre increase reports positive")
	public void theSerologyColumnsComeFromTheOldestTestOfEachKind() {

		CaseDataDto testedTwice = fixture.caze(creator.createPerson().toReference(), ONSET);
		SampleDto serum = fixture.specimen(testedTwice, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(serum, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, EARLIER_TEST);
		fixture.testOn(serum, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, LATER_TEST);
		fixture.testOn(serum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.INDETERMINATE, true, EARLIER_TEST);
		fixture.testOn(serum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, LATER_TEST);

		// Fourfold rise is the definition itself, not a modifier on the result
		CaseDataDto fourfoldRise = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(fourfoldRise, SampleMaterial.SERA, FIRST_SPECIMEN),
			PathogenTestType.IGG_SERUM_ANTIBODY,
			PathogenTestResultType.NEGATIVE,
			true,
			EARLIER_TEST,
			test -> test.setFourFoldIncreaseAntibodyTiter(true));

		CaseDataDto untested = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto twice = export.entryFor(testedTwice.getUuid());

		assertThat(export.value(twice, RESULT_IG_G), is("NEG"));
		assertThat(export.value(twice, RESULT_IG_M), is("EQUI"));

		assertThat(export.value(export.entryFor(fourfoldRise.getUuid()), RESULT_IG_G), is("POS"));

		assertThat(export.value(export.entryFor(untested.getUuid()), RESULT_IG_G), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(untested.getUuid()), RESULT_IG_M), is(blankOrNullString()));
	}

	@Test
	@DisplayName("an unverified test reports neither a serology result nor a genotype, even when it is the oldest")
	public void anUnverifiedTestReportsNeitherSerologyNorGenotype() {

		// the same evidence rule as ResultVirDetect and the rubella serology: an entry nobody
		// verified is not a result
		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		SampleDto serum = fixture.specimen(caze, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(serum, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, false, EARLIER_TEST);
		fixture.testOn(serum, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, LATER_TEST);
		fixture.testOn(serum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, false, EARLIER_TEST);
		fixture.testOn(serum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, LATER_TEST);
		SampleDto swab = fixture.specimen(caze, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN);
		fixture.testOn(
			swab,
			PathogenTestType.SEQUENCING,
			PathogenTestResultType.POSITIVE,
			false,
			EARLIER_TEST,
			test -> test.setGenoType(GenoType.GENOTYPE_B3));
		fixture.testOn(
			swab,
			PathogenTestType.SEQUENCING,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST,
			test -> test.setGenoType(GenoType.GENOTYPE_H1));

		CaseDataDto onlyUnverified = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(onlyUnverified, SampleMaterial.SERA, FIRST_SPECIMEN),
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestResultType.POSITIVE,
			false,
			EARLIER_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto entry = export.entryFor(caze.getUuid());
		assertThat(export.value(entry, RESULT_IG_G), is("NEG"));
		assertThat(export.value(entry, RESULT_IG_M), is("NEG"));
		assertThat(export.value(entry, GENOTYPE), is("MEASV_H1"));

		assertThat(export.value(export.entryFor(onlyUnverified.getUuid()), RESULT_IG_M), is(blankOrNullString()));
	}

	/**
	 * {@code PlaceOfInfection} is at country level and only for imported cases.
	 * Any case status other than IMP reports empty columns; travel not consulted.
	 */
	@Test
	@DisplayName("PlaceOfInfection follows ImportedStatus, and reports only exposures inside the incubation window")
	public void importedStatusAndPlaceOfInfection() {

		CaseDataDto imported = travelledCase(CaseImportedStatus.IMPORTED_CASE);
		CaseDataDto importRelated = travelledCase(CaseImportedStatus.IMPORT_RELATED_CASE);
		CaseDataDto unknownImportation = travelledCase(CaseImportedStatus.UNKNOWN_IMPORTATION_STATUS);
		CaseDataDto notImported = travelledCase(CaseImportedStatus.NOT_IMPORTED_CASE);
		CaseDataDto unrecorded = travelledCase(null);

		// Two countries: one column each, in query order
		CaseDataDto twoCountries = fixture.caze(creator.createPerson().toReference(), ONSET, caze -> {
			caze.getEpiData().setCaseImportedStatus(CaseImportedStatus.IMPORTED_CASE);
			caze.getEpiData().getExposures().add(travelTo(france));
			caze.getEpiData().getExposures().add(travelTo(italy));
		});

		// Three months before onset, outside incubation window [7-21 days]
		CaseDataDto travelledLongBefore = fixture.caze(creator.createPerson().toReference(), ONSET, caze -> {
			caze.getEpiData().setCaseImportedStatus(CaseImportedStatus.IMPORTED_CASE);
			ExposureDto exposure = travelTo(france);
			exposure.setStartDate(DateHelper.getDateZero(2024, 2, 1));
			exposure.setEndDate(DateHelper.getDateZero(2024, 2, 10));
			caze.getEpiData().getExposures().add(exposure);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(imported.getUuid()), IMPORTED_STATUS), is("IMP"));
		assertThat(export.value(export.entryFor(importRelated.getUuid()), IMPORTED_STATUS), is("IMPREL"));
		assertThat(export.value(export.entryFor(unknownImportation.getUuid()), IMPORTED_STATUS), is("IMPUNK"));
		assertThat(export.value(export.entryFor(notImported.getUuid()), IMPORTED_STATUS), is("NOTIMP"));
		assertThat(export.value(export.entryFor(unrecorded.getUuid()), IMPORTED_STATUS), is(blankOrNullString()));

		assertThat(export.values(export.entryFor(imported.getUuid()), PLACE_OF_INFECTION).get(0), is("FR"));

		assertThat(export.values(export.entryFor(twoCountries.getUuid()), PLACE_OF_INFECTION).subList(0, 2), containsInAnyOrder("FR", "IT"));

		for (CaseDataDto reportsNothing : Arrays.asList(importRelated, unknownImportation, notImported, unrecorded, travelledLongBefore)) {
			assertThat(
				"PlaceOfInfection for " + reportsNothing.getUuid(),
				export.values(export.entryFor(reportsNothing.getUuid()), PLACE_OF_INFECTION).get(0),
				is(blankOrNullString()));
		}
	}

	/**
	 * {@code ClusterSetting} is repeatable because clusters can span settings.
	 * SORMAS records one, so the group carries at most one value.
	 */
	@Test
	@DisplayName("every cluster setting reports its own code, and the two that name no setting report nothing")
	public void everyClusterSettingReportsItsOwnCode() {

		Map<ClusterType, String> caseUuids = new LinkedHashMap<>();

		for (ClusterType clusterType : CLUSTER_SETTING_CODES.keySet()) {
			caseUuids.put(clusterType, fixture.caze(creator.createPerson().toReference(), ONSET, caze -> {
				caze.getEpiData().setClusterRelated(true);
				caze.getEpiData().setClusterType(clusterType);
			}).getUuid());
		}

		CaseDataDto noCluster = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		for (Map.Entry<ClusterType, String> expected : CLUSTER_SETTING_CODES.entrySet()) {
			String reported = export.values(export.entryFor(caseUuids.get(expected.getKey())), CLUSTER_SETTING).get(0);

			if (expected.getValue() == null) {
				assertThat("ClusterSetting for " + expected.getKey(), reported, is(blankOrNullString()));
			} else {
				assertThat("ClusterSetting for " + expected.getKey(), reported, is(expected.getValue()));
			}
		}

		// ClusterRelated is BOOL (no third state): unasked cases report false, not nothing
		assertThat(export.value(export.entryFor(noCluster.getUuid()), CLUSTER_RELATED), is("false"));
		assertThat(export.value(export.entryFor(noCluster.getUuid()), CLUSTER_ID), is(blankOrNullString()));
	}

	/**
	 * Vaccination pair reads from the same course.
	 * Dose scale is shared and unit-tested in {@code VaccinationFieldsTest}.
	 */
	@Test
	@DisplayName("VaccinationStatus and DateOfLastVaccination are read from the same course")
	public void theVaccinationColumnsAreReadFromTheSameCourse() {

		PersonReferenceDto vaccinated = creator.createPerson().toReference();
		CaseDataDto twoDoses = fixture.caze(vaccinated, ONSET);
		fixture.completedCourse(vaccinated, 2, COURSE_VALID_FROM, COURSE_VALID_UNTIL, FIRST_DOSE, LAST_DOSE);

		PersonReferenceDto unknownDoses = creator.createPerson().toReference();
		CaseDataDto noDoseCount = fixture.caze(unknownDoses, ONSET);
		courseWithNoDoseCount(unknownDoses);

		// Course ended before onset: neither column reports it
		PersonReferenceDto lapsed = creator.createPerson().toReference();
		CaseDataDto courseEndedFirst = fixture.caze(lapsed, ONSET);
		fixture.completedCourse(lapsed, 1, COURSE_VALID_FROM, DateHelper.getDateZero(2023, 11, 31), FIRST_DOSE);

		CaseDataDto neverVaccinated = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto two = export.entryFor(twoDoses.getUuid());

		assertThat(export.value(two, VACCINATION_STATUS), is("2DOSE"));
		assertThat(export.value(two, DATE_OF_LAST_VACCINATION), is("2023-06-15"));

		assertThat(export.value(export.entryFor(noDoseCount.getUuid()), VACCINATION_STATUS), is("UNKDOSE"));

		EpipulseDiseaseExportEntryDto lapsedEntry = export.entryFor(courseEndedFirst.getUuid());

		assertThat(export.value(lapsedEntry, VACCINATION_STATUS), is("NOTVACC"));
		assertThat(export.value(lapsedEntry, DATE_OF_LAST_VACCINATION), is(blankOrNullString()));

		assertThat(export.value(export.entryFor(neverVaccinated.getUuid()), VACCINATION_STATUS), is("NOTVACC"));
	}

	/**
	 * Soft-deleted pathogen tests are ignored by all test-reading columns.
	 * Each of five MEAS columns reads tests through four separate CTEs,
	 * each carrying its own {@code deleted = false} filter.
	 * 
	 * Exception: specimen columns report collected status regardless of deletion.
	 * Deleting a test doesn't un-collect the specimen.
	 */
	@Test
	@DisplayName("a deleted pathogen test is ignored by every column that reads tests")
	public void deletedPathogenTestsAreIgnoredByEveryColumnThatReadsThem() {

		CaseDataDto withdrawn = fixture.caze(creator.createPerson().toReference(), ONSET);

		SampleDto serum = fixture.specimen(withdrawn, SampleMaterial.SERA, FIRST_SPECIMEN);
		SampleDto swab = fixture.specimen(withdrawn, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN);

		delete(fixture.testOn(serum, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, EARLIER_TEST));
		delete(fixture.testOn(serum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, EARLIER_TEST));
		delete(
			fixture.testOn(
				swab,
				PathogenTestType.PCR_RT_PCR,
				PathogenTestResultType.POSITIVE,
				true,
				LATER_TEST,
				test -> test.setGenoType(GenoType.GENOTYPE_D8)));

		// Without this, assertions would pass against a fixture that never created the test
		CaseDataDto kept = fixture.caze(creator.createPerson().toReference(), ONSET);
		SampleDto keptSerum = fixture.specimen(kept, SampleMaterial.SERA, FIRST_SPECIMEN);
		SampleDto keptSwab = fixture.specimen(kept, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN);
		fixture.testOn(keptSerum, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, EARLIER_TEST);
		fixture.testOn(keptSerum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, EARLIER_TEST);
		fixture.testOn(
			keptSwab,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST,
			test -> test.setGenoType(GenoType.GENOTYPE_D8));

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto gone = export.entryFor(withdrawn.getUuid());

		assertThat(export.value(gone, DATE_OF_LAB_RESULT), is(blankOrNullString()));
		assertThat(export.value(gone, RESULT_VIR_DETECT), is(blankOrNullString()));
		assertThat(export.value(gone, GENOTYPE), is(blankOrNullString()));
		assertThat(export.value(gone, RESULT_IG_G), is(blankOrNullString()));
		assertThat(export.value(gone, RESULT_IG_M), is(blankOrNullString()));
		assertThat(export.values(gone, SPECIMEN_SERO).get(0), is(blankOrNullString()));

		// Withdrawn positive must not count toward DateUsedForStatistics
		assertThat(export.value(gone, DATE_USED_FOR_STATISTICS), is("2024-06-10"));

		// Specimen dates unchanged, still collected even if tests deleted
		assertThat(export.values(gone, SPECIMEN_VIR_DETECT).subList(0, 2), contains("NASALSWAB", "SER"));
		assertThat(export.value(gone, DATE_OF_SPECIMEN), is("2024-06-11"));

		EpipulseDiseaseExportEntryDto live = export.entryFor(kept.getUuid());

		assertThat(export.value(live, DATE_OF_LAB_RESULT), is("2024-06-13"));
		assertThat(export.value(live, RESULT_VIR_DETECT), is("POS"));
		assertThat(export.value(live, GENOTYPE), is("MEASV_D8"));
		assertThat(export.value(live, RESULT_IG_G), is("POS"));
		assertThat(export.value(live, RESULT_IG_M), is("POS"));
		assertThat(export.values(live, SPECIMEN_SERO).subList(0, 1), contains("SER"));
		assertThat(export.value(live, DATE_USED_FOR_STATISTICS), is("2024-06-12"));
	}

	@Test
	@DisplayName("every repeatable group keeps one column for a case with nothing recorded, and none at all for an empty export")
	public void everyRepeatableGroupIsFlooredAtOneColumn() {

		// Database truncated per test, so export twice in one method costs less than two methods

		// Width measured over entries: no cases means zero-width groups, flooring doesn't apply
		ExportedEntries empty = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		assertThat(empty.all(), is(empty()));
		assertThat(empty.header(), is(not(empty())));
		for (EpipulseVariable group : REPEATABLE_GROUPS) {
			assertThat(group + " survived an export with no cases", empty.header(), not(hasItem(group.getVariableName())));
		}

		// One case, nothing recorded: every repeatable group must hold exactly one blank column
		fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries exported = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		for (EpipulseVariable group : REPEATABLE_GROUPS) {
			assertThat(group + " left the header", exported.header(), hasItem(group.getVariableName()));
		}

		for (EpipulseVariable group : FLOORED_GROUPS) {
			assertThat(group + " did not write a blank column", exported.values(exported.only(), group), contains(""));
		}

		// Not a blank: "no complications" (NONE) vs. "not asked" are distinct in EpiPulse
		assertThat(exported.values(exported.only(), COMPLICATION_DIAGNOSIS), contains("NONE"));
	}

	@Test
	@DisplayName("only cases reported within the period are selected")
	public void onlyCasesWithinThePeriodAreSelected() {

		CaseDataDto inside = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.caze(creator.createPerson().toReference(), DateHelper.getDateZero(2024, 4, 30));
		fixture.caze(creator.createPerson().toReference(), DateHelper.getDateZero(2024, 5, 21));

		ExportedEntries export = runExport(EpipulseSubjectCode.MEAS, PERIOD_START, PERIOD_END);

		assertThat(export.all(), hasSize(1));
		assertThat(export.value(export.only(), NATIONAL_RECORD_ID), is(inside.getUuid()));
	}

	// --- Fixture helpers ---

	private Scenario complications(String label, Consumer<SymptomsDto> symptoms, String... expected) {

		String uuid = fixture.caze(creator.createPerson().toReference(), ONSET, caze -> symptoms.accept(caze.getSymptoms())).getUuid();

		return new Scenario(label, Arrays.asList(expected), uuid);
	}

	private CaseDataDto caseWithClinicalConfirmation(YesNoUnknown clinicalConfirmation) {
		return fixture.caze(creator.createPerson().toReference(), ONSET, caze -> caze.setClinicalConfirmation(clinicalConfirmation));
	}

	/**
	 * Person who died, with cause recorded beside free text.
	 * Person is updated after case creation to verify person record contents.
	 */
	private CaseDataDto deceasedCase(String details, CauseOfDeath causeOfDeath, Disease causeOfDeathDisease) {

		PersonDto person = creator.createPerson();
		CaseDataDto caze = fixture.caze(person.toReference(), ONSET, c -> c.setOutcome(CaseOutcome.DECEASED));

		PersonDto died = getPersonFacade().getByUuid(person.getUuid());
		died.setPresentCondition(PresentCondition.DEAD);
		died.setCauseOfDeath(causeOfDeath);
		died.setCauseOfDeathDisease(causeOfDeathDisease);
		died.setCauseOfDeathDetails(details);
		getPersonFacade().save(died);

		return caze;
	}

	/** Soft-deletes test the way the application does (not by touching the column). */
	private void delete(PathogenTestDto test) {
		getPathogenTestFacade().deletePathogenTest(test.getUuid(), new DeletionDetails());
	}

	private CaseDataDto virusDetectionCase(PathogenTestType testType, PathogenTestResultType result, boolean verified, Date testDate) {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(fixture.specimen(caze, SampleMaterial.NASAL_SWAB, FIRST_SPECIMEN), testType, result, verified, testDate);

		return caze;
	}

	private CaseDataDto genotypedCase(GenoType genoType) {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(caze, SampleMaterial.NASAL_SWAB, FIRST_SPECIMEN),
			PathogenTestType.SEQUENCING,
			PathogenTestResultType.POSITIVE,
			true,
			EARLIER_TEST,
			test -> test.setGenoType(genoType));

		return caze;
	}

	private CaseDataDto travelledCase(CaseImportedStatus importedStatus) {

		return fixture.caze(creator.createPerson().toReference(), ONSET, caze -> {
			caze.getEpiData().setCaseImportedStatus(importedStatus);
			caze.getEpiData().getExposures().add(travelTo(france));
		});
	}

	/** Acquired course with no dose count (reports UNKDOSE). */
	private void courseWithNoDoseCount(PersonReferenceDto person) {

		// Course must declare dose count to be settled as ACQUIRED, then count is cleared
		// via facade (simulates imported course without dose count)
		ImmunizationDto course = fixture.completedCourse(person, 1, COURSE_VALID_FROM, COURSE_VALID_UNTIL, LAST_DOSE);

		ImmunizationDto acquired = getImmunizationFacade().getByUuid(course.getUuid());
		acquired.setNumberOfDoses(null);
		getImmunizationFacade().save(acquired);
	}

	private ExposureDto travelTo(CountryReferenceDto country) {

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setStartDate(EXPOSURE_START);
		exposure.setEndDate(EXPOSURE_END);
		exposure.getLocation().setCountry(country);

		return exposure;
	}

	/** Create country with NUTS code for export reporting. */
	private CountryReferenceDto countryWithNutsCode(String name, String isoCode, String nutsCode) {

		Country country = creator.createCountry(name, isoCode, null);

		executeInTransaction(em -> {
			em.createNativeQuery("UPDATE country SET nutscode = :nutsCode WHERE uuid = :uuid")
				.setParameter("nutsCode", nutsCode)
				.setParameter("uuid", country.getUuid())
				.executeUpdate();
		});

		return new CountryReferenceDto(country.getUuid(), name, isoCode);
	}

	private static final class Scenario {

		private final String label;
		private final List<String> expected;
		private final String caseUuid;

		private Scenario(String label, List<String> expected, String caseUuid) {
			this.label = label;
			this.expected = expected;
			this.caseUuid = caseUuid;
		}
	}
}
