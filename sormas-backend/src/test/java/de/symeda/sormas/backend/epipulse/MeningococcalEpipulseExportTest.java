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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_LAST_VACCINATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ONSET;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.IMPORTED_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_SIGN_CIP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_SIGN_CTX_CFX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_SIGN_PEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_SIGN_RIF;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_VALUE_AST_CIP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_VALUE_AST_CTX_CFX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_VALUE_AST_PEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_VALUE_AST_RIF;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_FET_VR;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_POR_A1;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_POR_A2;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SEROGROUP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CIP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CTX_CFX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_PEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_RIF;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.VACCINATION_STATUS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Tests the Invasive Meningococcal Infection export against a real database.
 * 
 * MENI uses disease-specific SQL for serogroup, molecular typing, imported status,
 * and a four-antibiotic susceptibility panel ({@code MeniSql}). This suite verifies
 * which tests each CTE selects, filters, and which one wins when multiple exist.
 * Dates are fixed (not relative) so expected values can be verified by inspection.
 * 
 * SORMAS assigns meningococcal disease a 1-10 day incubation period,
 * so exposures count for {@code PlaceOfInfection} within [2024-05-31, 2024-06-09].
 */
public class MeningococcalEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	// Inside the incubation period
	private static final Date EXPOSURE_START = DateHelper.getDateZero(2024, 5, 2);
	private static final Date EXPOSURE_END = DateHelper.getDateZero(2024, 5, 5);

	// Case's newest grouping test, older test must not win
	private static final Date GROUPED = DateHelper.getDateZero(2024, 5, 12);
	private static final Date GROUPED_EARLIER = DateHelper.getDateZero(2024, 5, 11);

	private static final Date SEQUENCED = DateHelper.getDateZero(2024, 5, 13);
	private static final Date EARLIER_AST = DateHelper.getDateZero(2024, 5, 13);
	private static final Date LATEST_AST = DateHelper.getDateZero(2024, 5, 14);

	private static final Date FIRST_DOSE = DateHelper.getDateZero(2023, 0, 15);
	private static final Date LAST_DOSE = DateHelper.getDateZero(2023, 5, 15);

	// Wide enough to contain onset, which makes vaccination count for a case
	private static final Date COURSE_VALID_FROM = DateHelper.getDateZero(2023, 0, 10);
	private static final Date COURSE_VALID_UNTIL = DateHelper.getDateZero(2030, 0, 10);

	/**
	 * Mapping of all serogroup values to their EpiPulse codes, null when EpiPulse has no value.
	 * Defined explicitly rather than derived from {@code EpipulseMeningococcalSerogroupRef}
	 * to verify actual mappings rather than circular consistency.
	 */
	private static final Map<SeroGroupSpecification, String> SEROGROUP_CODES = new LinkedHashMap<>();

	static {
		SEROGROUP_CODES.put(SeroGroupSpecification.SEROGROUP_A, "NEIMENI_A");
		SEROGROUP_CODES.put(SeroGroupSpecification.SEROGROUP_B, "NEIMENI_B");
		SEROGROUP_CODES.put(SeroGroupSpecification.SEROGROUP_C, "NEIMENI_C");
		SEROGROUP_CODES.put(SeroGroupSpecification.SEROGROUP_W, "NEIMENI_W");
		SEROGROUP_CODES.put(SeroGroupSpecification.SEROGROUP_X, "NEIMENI_X");
		SEROGROUP_CODES.put(SeroGroupSpecification.SEROGROUP_Y, "NEIMENI_Y");
		// SORMAS records Z and 29E separately; EpiPulse combines them as Z/29E
		SEROGROUP_CODES.put(SeroGroupSpecification.SEROGROUP_Z, "NEIMENI_Z");
		SEROGROUP_CODES.put(SeroGroupSpecification.SEROGROUP_29E, "NEIMENI_29E");
		SEROGROUP_CODES.put(SeroGroupSpecification.NOT_GROUPABLE, "NEIMENI_NGA");
		SEROGROUP_CODES.put(SeroGroupSpecification.OTHER, "NEIMENI_OTH");
		// These indicate no serogroup was found, so they report nothing (not NEIMENI_OTH)
		SEROGROUP_CODES.put(SeroGroupSpecification.UNKNOWN, null);
		SEROGROUP_CODES.put(SeroGroupSpecification.NOT_UNDER_SURVEILLANCE, null);
	}

	/**
	 * Five MENI variables declared by EpiPulse but SORMAS cannot answer.
	 * Present in export output and always empty.
	 */
	private static final List<EpipulseVariable> DECLARED_AND_BLANK = Arrays.asList(
		EpipulseVariable.MAIN_PATHOGEN_DETECTION_METHOD,
		EpipulseVariable.SECOND_PATHOGEN_DETECTION_METHOD,
		EpipulseVariable.ISOLATE_ID,
		EpipulseVariable.REPORTED_EMERTII,
		EpipulseVariable.RESULT_MLST);

	private CaseFixture fixture;
	private CountryReferenceDto france;
	private CountryReferenceDto italy;

	@BeforeEach
	public void setUpMeningococcalExport() {

		configureLuxembourgFor(EpipulseSubjectCode.MENI);

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		fixture = fixtureFor(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, rdcf, creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		france = countryWithNutsCode("France", "FRA", "FR");
		italy = countryWithNutsCode("Italy", "ITA", "IT");
	}

	@Test
	@DisplayName("a fully populated case fills every column MENI derives, and leaves the declared-blank ones empty")
	public void aFullyPopulatedCaseFillsEveryColumn() {

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setOutcome(CaseOutcome.RECOVERED);
			c.getSymptoms().setMeningitis(SymptomState.YES);
			c.getEpiData().setCaseImportedStatus(CaseImportedStatus.IMPORTED_CASE);
			c.getEpiData().getExposures().add(travelTo(france));
		});

		fixture.completedCourse(person, 2, COURSE_VALID_FROM, COURSE_VALID_UNTIL, FIRST_DOSE, LAST_DOSE);

		groupedAs(caze, SeroGroupSpecification.SEROGROUP_B, GROUPED);
		sequenced(caze, "P1.7", PathogenTestResultType.POSITIVE);
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, LATEST_AST, panel());

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto entry = export.only();

		// from the configuration rather than the case
		assertThat(export.value(entry, DISEASE), is("MENI"));
		assertThat(export.value(entry, SUBJECT_CODE), is("MENI"));
		assertThat(export.value(entry, REPORTING_COUNTRY), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, DATA_SOURCE), is(SERVER_DATA_SOURCE));

		// from the case record itself
		assertThat(export.value(entry, STATUS), is("NEW/UPDATE"));
		assertThat(export.value(entry, NATIONAL_RECORD_ID), is(caze.getUuid()));
		assertThat(export.value(entry, CASE_CLASSIFICATION), is("CONF"));
		assertThat(export.value(entry, OUTCOME), is("A"));
		assertThat(export.value(entry, CLINICAL_CRITERIA), is("MENI"));
		assertThat(export.value(entry, DATE_OF_ONSET), is("2024-06-10"));
		assertThat(export.value(entry, DATE_OF_NOTIFICATION), is("2024-06-10"));

		// both worked out from the tests: the serogrouping on the 12th is the oldest positive one
		assertThat(export.value(entry, DATE_OF_DIAGNOSIS), is("2024-06-12"));
		assertThat(export.value(entry, DATE_USED_FOR_STATISTICS), is("2024-06-12"));

		// from the person and their address
		assertThat(export.value(entry, GENDER), is("F"));
		assertThat(export.value(entry, AGE), is("33"));
		assertThat(export.value(entry, AGE_MONTH), is(blankOrNullString()));
		assertThat(export.value(entry, PLACE_OF_RESIDENCE), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, PLACE_OF_NOTIFICATION), is(SERVER_COUNTRY_NUTS_CODE));

		// from the epidemiological data
		assertThat(export.value(entry, IMPORTED_STATUS), is("IMP"));
		assertThat(export.values(entry, PLACE_OF_INFECTION), contains("FR"));

		// the vaccination summary pair
		assertThat(export.value(entry, VACCINATION_STATUS), is("2DOSE"));
		assertThat(export.value(entry, DATE_OF_LAST_VACCINATION), is("2023-06-15"));

		// MENI's own laboratory columns
		assertThat(export.value(entry, SEROGROUP), is("NEIMENI_B"));
		assertThat(export.value(entry, RESULT_POR_A1), is("P1.7"));
		assertThat(export.value(entry, SIR_CIP), is("S"));

		// Declared and blank. The fully populated case is where that is worth asserting: it carries a
		// typed isolate and a full panel, so a column filled by accident would show here.
		for (EpipulseVariable declaredAndBlank : DECLARED_AND_BLANK) {
			assertThat(declaredAndBlank.getVariableName(), export.value(entry, declaredAndBlank), is(blankOrNullString()));
			assertThat(export.header(), hasItem(declaredAndBlank.getVariableName()));
		}
	}

	/**
	 * Every rung of the ladder in one export - one case per presentation, told apart by
	 * {@code NationalRecordId}. Splitting these into a test each would multiply the database cycles
	 * without adding an assertion.
	 */
	@Test
	@DisplayName("each case reports the one presentation highest on the ladder")
	public void eachCaseReportsTheOnePresentationHighestOnTheLadder() {

		List<Scenario> scenarios = new ArrayList<>();

		scenarios.add(scenario("meningitis and septicaemia together", "MENISEPTI", symptoms -> {
			symptoms.setMeningitis(SymptomState.YES);
			symptoms.setSepticaemia(SymptomState.YES);
			symptoms.setPneumoniaClinicalOrRadiologic(SymptomState.YES);
		}));

		scenarios.add(scenario("meningitis alone", "MENI", symptoms -> symptoms.setMeningitis(SymptomState.YES)));

		// septicaemia outranks pneumonia, so the pneumonia code must not surface here
		scenarios.add(scenario("septicaemia over pneumonia", "SEPTI", symptoms -> {
			symptoms.setSepticaemia(SymptomState.YES);
			symptoms.setPneumoniaClinicalOrRadiologic(SymptomState.YES);
		}));

		// the one code MENI and PNEU disagree on
		scenarios.add(scenario("pneumonia alone", "PNEU", symptoms -> symptoms.setPneumoniaClinicalOrRadiologic(SymptomState.YES)));

		// Both value sets carry an OTH code for a presentation that is none of the three, and the
		// export deliberately does not derive it - see InvasiveClinicalCriteriaFields.
		scenarios.add(scenario("a symptom outside the three", null, symptoms -> symptoms.setFever(SymptomState.YES)));

		scenarios.add(scenario("symptoms recorded, all negative", null, symptoms -> {
			symptoms.setMeningitis(SymptomState.NO);
			symptoms.setSepticaemia(SymptomState.NO);
			symptoms.setPneumoniaClinicalOrRadiologic(SymptomState.NO);
		}));

		// asymptomatic outranks everything, including a presentation recorded beside it
		scenarios.add(scenario("asymptomatic despite meningitis", null, symptoms -> {
			symptoms.setAsymptomatic(SymptomState.YES);
			symptoms.setMeningitis(SymptomState.YES);
		}));

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		assertThat(export.all(), hasSize(scenarios.size()));

		for (Scenario scenario : scenarios) {
			EpipulseDiseaseExportEntryDto entry = export.entryFor(scenario.caseUuid);
			String reported = export.value(entry, CLINICAL_CRITERIA);

			if (scenario.expectedCode == null) {
				assertThat("ClinicalCriteria for a case with " + scenario.label, reported, is(blankOrNullString()));
			} else {
				assertThat("ClinicalCriteria for a case with " + scenario.label, reported, is(scenario.expectedCode));
			}
		}
	}

	@Test
	@DisplayName("a case with no symptoms recorded at all still reaches the export")
	public void aCaseWithNoSymptomsRecordedAtAllStillReachesTheExport() {

		// The column used to inner-join symptoms, so relied on SORMAS always creating that row.
		// Now it reads from a common left join and doesn't.
		String caseUuid = scenario("nothing recorded", null, symptoms -> {
		}).caseUuid;

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(caseUuid), CLINICAL_CRITERIA), is(blankOrNullString()));
	}

	/**
	 * Serogroup reports the newest grouping test, only tests that determined a serogroup count.
	 */
	@Test
	@DisplayName("Serogroup reports the newest grouping test, and only a test that determined one")
	public void serogroupReportsTheNewestGroupingTest() {

		Map<String, String> expectedByCase = new LinkedHashMap<>();

		for (Map.Entry<SeroGroupSpecification, String> mapping : SEROGROUP_CODES.entrySet()) {
			CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
			groupedAs(caze, mapping.getKey(), GROUPED);
			expectedByCase.put(caze.getUuid(), mapping.getValue());
		}

		// Two grouping tests, newest wins (created first to exclude insertion order bias)
		CaseDataDto groupedTwice = fixture.caze(creator.createPerson().toReference(), ONSET);
		groupedAs(groupedTwice, SeroGroupSpecification.SEROGROUP_C, GROUPED);
		groupedAs(groupedTwice, SeroGroupSpecification.SEROGROUP_A, GROUPED_EARLIER);
		expectedByCase.put(groupedTwice.getUuid(), "NEIMENI_C");

		// Slide agglutination also groups isolates, read alongside serogrouping
		CaseDataDto byAgglutination = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(
			byAgglutination,
			PathogenTestType.SLIDE_AGGLUTINATION,
			PathogenTestResultType.POSITIVE,
			true,
			GROUPED,
			test -> test.setSeroGroupSpecification(SeroGroupSpecification.SEROGROUP_Y));
		expectedByCase.put(byAgglutination.getUuid(), "NEIMENI_Y");

		// Culture is not a grouping test, so serogroup on culture is ignored
		CaseDataDto onACulture = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(
			onACulture,
			PathogenTestType.CULTURE,
			PathogenTestResultType.POSITIVE,
			true,
			GROUPED,
			test -> test.setSeroGroupSpecification(SeroGroupSpecification.SEROGROUP_W));
		expectedByCase.put(onACulture.getUuid(), null);

		// Grouped but no serogroup recorded: reads newest test with a result, not newest test
		CaseDataDto groupedWithoutResult = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(groupedWithoutResult, PathogenTestType.SEROGROUPING, PathogenTestResultType.POSITIVE, true, GROUPED);
		expectedByCase.put(groupedWithoutResult.getUuid(), null);

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		for (Map.Entry<String, String> expected : expectedByCase.entrySet()) {

			String reported = export.value(export.entryFor(expected.getKey()), SEROGROUP);

			if (expected.getValue() == null) {
				assertThat("Serogroup for case " + expected.getKey(), reported, is(blankOrNullString()));
			} else {
				assertThat("Serogroup for case " + expected.getKey(), reported, is(expected.getValue()));
			}
		}
	}

	/**
	 * Typing columns are filled by typing identifier shape: PorA1 has comma, PorA2 is numeric,
	 * FetVr has dash, ST- prefix excluded. NST reported for indeterminate results.
	 */
	@Test
	@DisplayName("the typing columns are filled by the shape of the typing identifier")
	public void theTypingColumnsAreFilledByTheShapeOfTheTypingIdentifier() {

		CaseDataDto porA1 = sequencedCase("P1.7,16");
		// all digits: PorA2 takes the numeric shape
		CaseDataDto porA2Numeric = sequencedCase("16");
		// PorA2 is the widest of the three: anything claimed by neither of the others lands there
		CaseDataDto porA2Other = sequencedCase("cc32");
		CaseDataDto fetVr = sequencedCase("F3-3");

		// 'ST-' is excluded from PorA2 (it's a sequence type, not an allele)
		CaseDataDto sequenceType = sequencedCase("ST-11");

		// Indeterminate result means the strain is not subtypeable: a reportable answer
		CaseDataDto indeterminate = fixture.caze(creator.createPerson().toReference(), ONSET);
		sequenced(indeterminate, null, PathogenTestResultType.INDETERMINATE);

		// Typing ID on non-sequencing test is ignored
		CaseDataDto onACulture = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture
			.pathogenTest(onACulture, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, SEQUENCED, test -> test.setTypingId("P1.22"));

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(porA1.getUuid()), RESULT_POR_A1), is("P1.7,16"));
		assertThat(export.value(export.entryFor(porA2Numeric.getUuid()), RESULT_POR_A2), is("16"));
		assertThat(export.value(export.entryFor(porA2Other.getUuid()), RESULT_POR_A2), is("cc32"));
		assertThat(export.value(export.entryFor(fetVr.getUuid()), RESULT_FET_VR), is("F3-3"));

		// each shape fills its own column and leaves the other two alone
		assertThat(export.value(export.entryFor(porA1.getUuid()), RESULT_POR_A2), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(porA1.getUuid()), RESULT_FET_VR), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(fetVr.getUuid()), RESULT_POR_A1), is(blankOrNullString()));

		EpipulseDiseaseExportEntryDto sequenceTypeEntry = export.entryFor(sequenceType.getUuid());
		assertThat(export.value(sequenceTypeEntry, RESULT_POR_A1), is(blankOrNullString()));
		assertThat(export.value(sequenceTypeEntry, RESULT_POR_A2), is(blankOrNullString()));
		assertThat(export.value(sequenceTypeEntry, RESULT_FET_VR), is(blankOrNullString()));

		EpipulseDiseaseExportEntryDto notSubtypeable = export.entryFor(indeterminate.getUuid());
		assertThat(export.value(notSubtypeable, RESULT_POR_A1), is("NST"));
		assertThat(export.value(notSubtypeable, RESULT_POR_A2), is("NST"));
		assertThat(export.value(notSubtypeable, RESULT_FET_VR), is("NST"));

		EpipulseDiseaseExportEntryDto cultureEntry = export.entryFor(onACulture.getUuid());
		assertThat(export.value(cultureEntry, RESULT_POR_A1), is(blankOrNullString()));
		assertThat(export.value(cultureEntry, RESULT_POR_A2), is(blankOrNullString()));
	}

	@Test
	@DisplayName("ImportedStatus maps each recorded status, and PlaceOfInfection is reported only for an imported case")
	public void importedStatusAndPlaceOfInfection() {

		/**
		 * ImportedStatus maps each case status; PlaceOfInfection reports only for imported cases
		 * (IMP) and only countries visited during the incubation period.
		 */
		CaseDataDto imported = travelledCase(CaseImportedStatus.IMPORTED_CASE);
		CaseDataDto importRelated = travelledCase(CaseImportedStatus.IMPORT_RELATED_CASE);
		CaseDataDto unknownStatus = travelledCase(CaseImportedStatus.UNKNOWN_IMPORTATION_STATUS);
		CaseDataDto notImported = travelledCase(CaseImportedStatus.NOT_IMPORTED_CASE);
		CaseDataDto noStatus = travelledCase(null);

		// Two countries in window: one column each, one entry per country (not per stay)
		CaseDataDto twoCountries = fixture.caze(creator.createPerson().toReference(), ONSET, c -> {
			c.getEpiData().setCaseImportedStatus(CaseImportedStatus.IMPORTED_CASE);
			c.getEpiData().getExposures().add(travelTo(france));
			c.getEpiData().getExposures().add(travelTo(italy));
			c.getEpiData().getExposures().add(travelTo(france));
		});

		// Two months before onset is outside incubation period for meningococcal (1-10 days)
		CaseDataDto travelledTooEarly = fixture.caze(creator.createPerson().toReference(), ONSET, c -> {
			c.getEpiData().setCaseImportedStatus(CaseImportedStatus.IMPORTED_CASE);
			ExposureDto exposure = travelTo(france);
			exposure.setStartDate(DateHelper.getDateZero(2024, 3, 1));
			exposure.setEndDate(DateHelper.getDateZero(2024, 3, 5));
			c.getEpiData().getExposures().add(exposure);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(imported.getUuid()), IMPORTED_STATUS), is("IMP"));
		assertThat(export.value(export.entryFor(importRelated.getUuid()), IMPORTED_STATUS), is("IMPREL"));
		assertThat(export.value(export.entryFor(unknownStatus.getUuid()), IMPORTED_STATUS), is("IMPUNK"));
		assertThat(export.value(export.entryFor(notImported.getUuid()), IMPORTED_STATUS), is("NOTIMP"));
		assertThat(export.value(export.entryFor(noStatus.getUuid()), IMPORTED_STATUS), is(blankOrNullString()));

		assertThat(export.values(export.entryFor(imported.getUuid()), PLACE_OF_INFECTION), contains("FR"));
		assertThat(export.values(export.entryFor(twoCountries.getUuid()), PLACE_OF_INFECTION), containsInAnyOrder("FR", "IT"));

		// The group is floored: cases with no place keep one blank column, not zero columns
		for (CaseDataDto notReported : Arrays.asList(importRelated, unknownStatus, notImported, noStatus, travelledTooEarly)) {
			assertThat(
				"PlaceOfInfection for case " + notReported.getUuid(),
				export.values(export.entryFor(notReported.getUuid()), PLACE_OF_INFECTION),
				is(blank()));
		}
	}

	@Test
	@DisplayName("VaccinationStatus stops at 4DOSE, which is as high as EpiPulse counts for MENI")
	public void vaccinationStatusStopsAtFourDoses() {

		/**
		 * VaccinationStatus caps at 4DOSE, the maximum EpiPulse counts for MENI.
		 * Six doses report 4DOSE, not higher codes EpiPulse would reject.
		 */
		PersonReferenceDto sixDoses = creator.createPerson().toReference();
		CaseDataDto capped = fixture.caze(sixDoses, ONSET);
		courseCoveringOnset(sixDoses, 6);

		PersonReferenceDto threeDoses = creator.createPerson().toReference();
		CaseDataDto belowTheCap = fixture.caze(threeDoses, ONSET);
		courseCoveringOnset(threeDoses, 3);

		PersonReferenceDto noCount = creator.createPerson().toReference();
		CaseDataDto unknownDoses = fixture.caze(noCount, ONSET);
		courseWithNoDoseCount(noCount);

		CaseDataDto neverVaccinated = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(capped.getUuid()), VACCINATION_STATUS), is("4DOSE"));
		assertThat(export.value(export.entryFor(belowTheCap.getUuid()), VACCINATION_STATUS), is("3DOSE"));
		assertThat(export.value(export.entryFor(unknownDoses.getUuid()), VACCINATION_STATUS), is("UNKDOSE"));
		assertThat(export.value(export.entryFor(neverVaccinated.getUuid()), VACCINATION_STATUS), is("NOTVACC"));

		// the last dose comes from the course VaccinationStatus reports, so it follows it
		assertThat(export.value(export.entryFor(capped.getUuid()), DATE_OF_LAST_VACCINATION), is("2023-06-15"));
		assertThat(export.value(export.entryFor(neverVaccinated.getUuid()), DATE_OF_LAST_VACCINATION), is(blankOrNullString()));
	}

	@Test
	@DisplayName("the susceptibility columns come from the newest panel, across all four antibiotics")
	public void theSusceptibilityColumnsComeFromTheNewestPanel() {

		/**
		 * Susceptibility columns come from the newest panel, all four antibiotics covered.
		 * MIC formats vary (comma, operator+unit, spaced operator, text-only).
		 */
		CaseDataDto testedTwice = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(testedTwice, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, LATEST_AST, panel());
		fixture.pathogenTest(testedTwice, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, EARLIER_AST, test -> {
			DrugSusceptibilityDto earlier = DrugSusceptibilityDto.build();
			earlier.setCiprofloxacinMic("8");
			earlier.setCiprofloxacinSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setCeftriaxoneMic("16");
			earlier.setCeftriaxoneSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setPenicillinMic("4");
			earlier.setPenicillinSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setRifampicinMic("2");
			earlier.setRifampicinSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(earlier);
		});

		CaseDataDto untested = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto newest = export.entryFor(testedTwice.getUuid());

		// "0,004": decimal comma, no operator
		assertThat(export.value(newest, MIC_SIGN_CIP), is(blankOrNullString()));
		assertThat(export.value(newest, MIC_VALUE_AST_CIP), is("0.004"));
		assertThat(export.value(newest, SIR_CIP), is("S"));
		// "\u22640.125 mg/L": operator and unit around the number
		assertThat(export.value(newest, MIC_SIGN_CTX_CFX), is("<="));
		assertThat(export.value(newest, MIC_VALUE_AST_CTX_CFX), is("0.125"));
		assertThat(export.value(newest, SIR_CTX_CFX), is("S"));
		// "> 0.25": space between operator and number
		assertThat(export.value(newest, MIC_SIGN_PEN), is(">"));
		assertThat(export.value(newest, MIC_VALUE_AST_PEN), is("0.25"));
		assertThat(export.value(newest, SIR_PEN), is("I"));
		// Free text with no number reports neither sign nor value
		assertThat(export.value(newest, MIC_SIGN_RIF), is(blankOrNullString()));
		assertThat(export.value(newest, MIC_VALUE_AST_RIF), is(blankOrNullString()));
		assertThat(export.value(newest, SIR_RIF), is("R"));

		EpipulseDiseaseExportEntryDto none = export.entryFor(untested.getUuid());
		for (EpipulseVariable astColumn : new EpipulseVariable[] {
			MIC_SIGN_CIP,
			MIC_VALUE_AST_CIP,
			SIR_CIP,
			MIC_SIGN_CTX_CFX,
			MIC_VALUE_AST_CTX_CFX,
			SIR_CTX_CFX,
			MIC_SIGN_PEN,
			MIC_VALUE_AST_PEN,
			SIR_PEN,
			MIC_SIGN_RIF,
			MIC_VALUE_AST_RIF,
			SIR_RIF }) {
			assertThat(astColumn.getVariableName() + " for a case never tested", export.value(none, astColumn), is(blankOrNullString()));
		}
	}

	@Test
	@DisplayName("a newer test without results for the MENI drugs does not hide an older panel that has them")
	public void aNewerTestWithoutPanelResultsDoesNotHideAnOlderPanel() {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, LATEST_AST, test -> {
			DrugSusceptibilityDto otherDrugsOnly = DrugSusceptibilityDto.build();
			otherDrugsOnly.setAmikacinSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(otherDrugsOnly);
		});
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, EARLIER_AST, test -> {
			DrugSusceptibilityDto earlier = DrugSusceptibilityDto.build();
			earlier.setCiprofloxacinMic("8");
			earlier.setCiprofloxacinSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(earlier);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto entry = export.entryFor(caze.getUuid());
		assertThat(export.value(entry, MIC_VALUE_AST_CIP), is("8"));
		assertThat(export.value(entry, SIR_CIP), is("R"));
	}

	@Test
	@DisplayName("a newer unverified panel does not replace an older verified one")
	public void aNewerUnverifiedPanelDoesNotReplaceAVerifiedOne() {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, false, LATEST_AST, test -> {
			DrugSusceptibilityDto unverified = DrugSusceptibilityDto.build();
			unverified.setCiprofloxacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			test.setDrugSusceptibility(unverified);
		});
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, EARLIER_AST, test -> {
			DrugSusceptibilityDto verified = DrugSusceptibilityDto.build();
			verified.setCiprofloxacinSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(verified);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(caze.getUuid()), SIR_CIP), is("R"));
	}

	@Test
	@DisplayName("only cases reported within the period are selected")
	public void onlyCasesWithinThePeriodAreSelected() {

		fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.caze(creator.createPerson().toReference(), DateHelper.subtractDays(PERIOD_START, 30));

		ExportedEntries export = runExport(EpipulseSubjectCode.MENI, PERIOD_START, PERIOD_END);

		assertThat(export.all(), hasSize(2));
	}

	// ---------------------------------------------------------------------------------------
	// Fixture helpers: common logic on CaseFixture, MENI-specific shortcuts below
	// ---------------------------------------------------------------------------------------

	private Scenario scenario(String label, String expectedCode, Consumer<SymptomsDto> symptoms) {
		return new Scenario(
			label,
			expectedCode,
			fixture.caze(creator.createPerson().toReference(), ONSET, caze -> symptoms.accept(caze.getSymptoms())).getUuid());
	}

	private void groupedAs(CaseDataDto caze, SeroGroupSpecification serogroup, Date testDate) {
		fixture.pathogenTest(
			caze,
			PathogenTestType.SEROGROUPING,
			PathogenTestResultType.POSITIVE,
			true,
			testDate,
			test -> test.setSeroGroupSpecification(serogroup));
	}

	private CaseDataDto sequencedCase(String typingId) {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		sequenced(caze, typingId, PathogenTestResultType.POSITIVE);

		return caze;
	}

	private void sequenced(CaseDataDto caze, String typingId, PathogenTestResultType result) {
		fixture.pathogenTest(caze, PathogenTestType.SEQUENCING, result, true, SEQUENCED, test -> test.setTypingId(typingId));
	}

	private CaseDataDto travelledCase(CaseImportedStatus importedStatus) {

		return fixture.caze(creator.createPerson().toReference(), ONSET, c -> {
			c.getEpiData().setCaseImportedStatus(importedStatus);
			c.getEpiData().getExposures().add(travelTo(france));
		});
	}

	/**
	 * Vaccination course with protection window containing onset.
	 * Reports declared dose count; last dose is always LAST_DOSE.
	 */
	private void courseCoveringOnset(PersonReferenceDto person, int numberOfDoses) {
		fixture.completedCourse(person, numberOfDoses, COURSE_VALID_FROM, COURSE_VALID_UNTIL, doseDates(numberOfDoses));
	}

	/**
	 * Acquired course with no dose count (reports UNKDOSE).
	 * Created in two steps: declared count for acquisition, then cleared.
	 * Simulates imported course without dose count.
	 */
	private void courseWithNoDoseCount(PersonReferenceDto person) {

		ImmunizationDto course = fixture.completedCourse(person, 2, COURSE_VALID_FROM, COURSE_VALID_UNTIL, doseDates(2));

		ImmunizationDto acquired = getImmunizationFacade().getByUuid(course.getUuid());
		acquired.setNumberOfDoses(null);
		getImmunizationFacade().save(acquired);
	}

	/** {@code numberOfDoses} dose dates, one month apart, last date is LAST_DOSE. */
	private static Date[] doseDates(int numberOfDoses) {

		Date[] dates = new Date[numberOfDoses];
		for (int i = 0; i < numberOfDoses; i++) {
			dates[i] = DateHelper.subtractDays(LAST_DOSE, (numberOfDoses - 1 - i) * 30);
		}

		return dates;
	}

	/** Travel exposure in one country during incubation window. */
	private ExposureDto travelTo(CountryReferenceDto country) {

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setStartDate(EXPOSURE_START);
		exposure.setEndDate(EXPOSURE_END);
		exposure.getLocation().setCountry(country);

		return exposure;
	}

	/**
	 * Four antibiotics MENI reports with MICs in various formats:
	 * decimal comma, operator+unit, spaced operator, and text-only.
	 */
	private static Consumer<PathogenTestDto> panel() {

		return test -> {
			DrugSusceptibilityDto panel = DrugSusceptibilityDto.build();
			panel.setCiprofloxacinMic("0,004");
			panel.setCiprofloxacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			panel.setCeftriaxoneMic("\u22640.125 mg/L");
			panel.setCeftriaxoneSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			panel.setPenicillinMic("> 0.25");
			panel.setPenicillinSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
			panel.setRifampicinMic("not determined");
			panel.setRifampicinSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(panel);
		};
	}

	/**
	 * Create a country with NUTS code for export reporting.
	 * TestDataCreator doesn't set NUTS codes, making countries invisible to place columns.
	 */
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

	private static List<String> blank() {
		return Collections.singletonList("");
	}

	/** One case, what was recorded on it, and the code it is expected to report. */
	private static final class Scenario {

		private final String label;
		private final String expectedCode;
		private final String caseUuid;

		private Scenario(String label, String expectedCode, String caseUuid) {
			this.label = label;
			this.expectedCode = expectedCode;
			this.caseUuid = caseUuid;
		}
	}
}
