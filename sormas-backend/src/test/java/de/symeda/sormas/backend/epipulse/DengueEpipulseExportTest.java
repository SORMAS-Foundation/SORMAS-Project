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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CASE_CLASSIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLUSTER_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ONSET;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HOSPITALISATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.IMPORTED;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MODE_OF_TRANSMISSION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN_DETECTION_METHOD;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SEROTYPE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Tests Dengue export against a real database, column by column.
 * 
 * Three complex areas dominate:
 * - {@code SCONV}: appended to PathogenDetectionMethod from two flags, not a test type
 * - {@code Serotype}: one value from multiple tests; which tests allowed matters
 * - {@code ModeOfTransmission}: DENGUE's value set (folds four constants into one)
 * 
 * {@link #assertEveryDeclaredColumn} verifies expectations cover all declared variables,
 * catching metadata changes immediately.
 * 
 * Dates are fixed, not relative, so expected values are verifiable literals.
 */
public class DengueEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	// Inside incubation window [2024-05-27, 2024-06-08] (2 to 14 days)
	// Much narrower than malaria's 7-60 days; cannot share travel dates
	private static final Date TRAVEL_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date TRAVEL_END = DateHelper.getDateZero(2024, 5, 5);

	private static final Date TEST_DATE = DateHelper.getDateZero(2024, 5, 12);
	private static final Date LATER_TEST_DATE = DateHelper.getDateZero(2024, 5, 14);

	private static final String CLUSTER = "DENV-2024-07";

	private TestDataCreator.RDCF rdcf;
	private CaseFixture fixture;
	private CountryReferenceDto thailand;
	private CountryReferenceDto brazil;

	@BeforeEach
	public void setUpDengueExport() {

		configureLuxembourgFor(EpipulseSubjectCode.DENGUE);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto surveillanceSupervisor = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR);
		fixture = fixtureFor(Disease.DENGUE, rdcf, surveillanceSupervisor);

		thailand = countryWithNutsCode("Thailand", "THA", "TH");
		brazil = countryWithNutsCode("Brazil", "BRA", "BR");
	}

	// ---
	// Whole file
	// ---

	@Test
	@DisplayName("a fully populated case fills every one of the 25 columns DENGUE declares")
	public void aFullyPopulatedCaseFillsEveryColumnDengueDeclares() {

		assignNutsCodes(rdcf, "LU10", "LU101", "LU1010");

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15, p -> {
			p.getAddress().setRegion(rdcf.region);
			p.getAddress().setDistrict(rdcf.district);
			p.getAddress().setCommunity(rdcf.community);
		}).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setOutcome(CaseOutcome.RECOVERED);
			c.setClinicalConfirmation(YesNoUnknown.YES);
			hospitalisedForTheReportedDisease().accept(c);
			c.getEpiData().setImportedCase(YesNoUnknown.YES);
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.MOSQUITOES_FROM_ENDEMIC_COUNTRY);
			c.getEpiData().setClusterIdentifier(CLUSTER);
			c.getEpiData().getExposures().add(travelTo(thailand));
		});

		fixture.pathogenTest(caze, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, TEST_DATE, typed(Serotype.DENV_1));

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		Map<EpipulseVariable, List<String>> expected = new LinkedHashMap<>();

		// from the configuration rather than from the case
		expected.put(DISEASE, value("DENGUE"));
		expected.put(SUBJECT_CODE, value("DENGUE"));
		expected.put(REPORTING_COUNTRY, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(DATA_SOURCE, value(SERVER_DATA_SOURCE));
		expected.put(STATUS, value("NEW/UPDATE"));

		// from the case record
		expected.put(NATIONAL_RECORD_ID, value(caze.getUuid()));
		expected.put(CASE_CLASSIFICATION, value("CONF"));
		expected.put(OUTCOME, value("A"));
		expected.put(HOSPITALISATION, value("true"));
		expected.put(CLINICAL_CRITERIA_STATUS, value("true"));
		// One derived value (ASY) means no symptoms, blanks DateOfOnset
		// Tested in asymptomaticIsTheOneClinicalCriterionDerived
		expected.put(CLINICAL_CRITERIA, blank());
		expected.put(DATE_OF_ONSET, value("2024-06-10"));
		expected.put(DATE_OF_NOTIFICATION, value("2024-06-10"));
		// No doctor's notification: first positive test is diagnosis date
		expected.put(DATE_OF_DIAGNOSIS, value("2024-06-12"));
		expected.put(DATE_USED_FOR_STATISTICS, value("2024-06-12"));

		// from the person
		expected.put(AGE, value("33"));
		expected.put(GENDER, value("F"));
		expected.put(PLACE_OF_RESIDENCE, value("LU1010"));
		expected.put(PLACE_OF_NOTIFICATION, value("LU1010"));

		// the epidemiology DENGUE owns
		expected.put(IMPORTED, value("true"));
		expected.put(MODE_OF_TRANSMISSION, value("MOSQ"));
		expected.put(CLUSTER_ID, value(CLUSTER));
		expected.put(PLACE_OF_INFECTION, value("TH"));

		// the laboratory: one test answers both columns
		expected.put(SEROTYPE, value("DENV1"));
		expected.put(PATHOGEN_DETECTION_METHOD, value("NUC"));

		assertEveryDeclaredColumn(export, entry, expected);
	}

	@Test
	@DisplayName("a case with nothing recorded still writes every declared column")
	public void aCaseWithNothingRecordedStillWritesEveryDeclaredColumn() {

		// Both of DENGUE's repeatable groups are easy to have no values for - PlaceOfInfection
		// needs dated travel inside a 12-day window and PathogenDetectionMethod needs a verified
		// positive test - so an unfloored group would drop a column here and nowhere else.
		fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);
		assertThat(export.all(), hasSize(1));

		for (EpipulseVariable variable : EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.DENGUE)) {
			assertThat("DENGUE dropped " + variable.getVariableName(), export.header(), hasItem(variable.getVariableName()));
		}
	}

	// ---
	// PathogenDetectionMethod and SCONV
	// ---

	@Test
	@DisplayName("a seroconversion and a fourfold titre rise both report SCONV, beside the methods that are test types")
	public void bothFlagsReportSeroconversion() {

		// SCONV from two flags: fourfold rise OR seroconversion
		// Reading only one would miss cases diagnosed the other way
		CaseDataDto fourfold = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(fourfold, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, TEST_DATE, test -> {
			test.setFourFoldIncreaseAntibodyTiter(true);
		});

		CaseDataDto seroconverted = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(seroconverted, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, TEST_DATE, test -> {
			test.setSeroConversion(true);
		});

		CaseDataDto neither = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(neither, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, TEST_DATE);

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		// SIGM from test type, SCONV from flag: two entries for one test
		assertThat(export.values(export.entryFor(fourfold.getUuid()), PATHOGEN_DETECTION_METHOD), containsInAnyOrder("SIGM", "SCONV"));
		assertThat(export.values(export.entryFor(seroconverted.getUuid()), PATHOGEN_DETECTION_METHOD), containsInAnyOrder("SIGM", "SCONV"));
		// the same test without either flag reports the test type alone
		assertThat(export.values(export.entryFor(neither.getUuid()), PATHOGEN_DETECTION_METHOD), contains("SIGM"));
	}

	@Test
	@DisplayName("a negative or unverified test contributes neither a detection method nor a SCONV")
	public void onlyAVerifiedPositiveTestContributesADetectionMethod() {

		// Flag on unverified test is not evidence the case was diagnosed that way
		CaseDataDto unverified = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(unverified, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, false, TEST_DATE, test -> {
			test.setFourFoldIncreaseAntibodyTiter(true);
		});

		CaseDataDto negative = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(negative, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, TEST_DATE, test -> {
			test.setFourFoldIncreaseAntibodyTiter(true);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		// Group floored: case with no method keeps one blank column, not none
		assertThat(export.values(export.entryFor(unverified.getUuid()), PATHOGEN_DETECTION_METHOD), is(blank()));
		assertThat(export.values(export.entryFor(negative.getUuid()), PATHOGEN_DETECTION_METHOD), is(blank()));
	}

	@Test
	@DisplayName("a test done for another disease contributes neither a detection method, a SCONV nor a serotype")
	public void aTestForAnotherDiseaseContributesNothing() {

		// an arbovirus panel on the same serum: the chikungunya result is no evidence of dengue
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(caze, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, TEST_DATE, test -> {
			test.setTestedDisease(Disease.CHIKUNGUNYA);
			test.setFourFoldIncreaseAntibodyTiter(true);
		});
		fixture.pathogenTest(caze, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, TEST_DATE, test -> {
			test.setTestedDisease(Disease.CHIKUNGUNYA);
			test.setSerotype(Serotype.DENV_1);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto entry = export.only();
		assertThat(export.values(entry, PATHOGEN_DETECTION_METHOD), is(blank()));
		assertThat(export.value(entry, SEROTYPE), is(""));
	}

	// ---
	// Serotype
	// ---

	@Test
	@DisplayName("Serotype is the newest determination, and an inconclusive later test does not erase it")
	public void serotypeIsTheNewestDetermination() {

		// Newer test refines earlier; but later test without serotype must not erase the answer
		CaseDataDto refined = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(refined, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, TEST_DATE, typed(Serotype.DENV_1));
		fixture.pathogenTest(refined, PathogenTestType.NAAT, PathogenTestResultType.POSITIVE, true, LATER_TEST_DATE, typed(Serotype.DENV_3));

		CaseDataDto laterInconclusive = fixture.caze(newPerson(), ONSET);
		fixture
			.pathogenTest(laterInconclusive, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, TEST_DATE, typed(Serotype.DENV_2));
		fixture.pathogenTest(laterInconclusive, PathogenTestType.NAAT, PathogenTestResultType.POSITIVE, true, LATER_TEST_DATE);

		// a later UNKNOWN is a serotype recorded as not determined, which DENGUE has no code for
		CaseDataDto laterUnknown = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(laterUnknown, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, TEST_DATE, typed(Serotype.DENV_2));
		fixture.pathogenTest(laterUnknown, PathogenTestType.NAAT, PathogenTestResultType.POSITIVE, true, LATER_TEST_DATE, typed(Serotype.UNKNOWN));

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(refined.getUuid()), SEROTYPE), is("DENV3"));
		assertThat(export.value(export.entryFor(laterInconclusive.getUuid()), SEROTYPE), is("DENV2"));
		assertThat(export.value(export.entryFor(laterUnknown.getUuid()), SEROTYPE), is("DENV2"));
	}

	@Test
	@DisplayName("a serotype on a test that could not have determined one is not reported")
	public void aSerotypeOnAnIneligibleTestIsNotReported() {

		// SORMAS shows serotype field on three test types, positive result only. Values elsewhere are leftovers.
		String wrongTestType = caseWithOneTest(PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, Serotype.DENV_1);
		String negative = caseWithOneTest(PathogenTestType.PCR_RT_PCR, PathogenTestResultType.NEGATIVE, true, Serotype.DENV_1);
		String unverified = caseWithOneTest(PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, false, Serotype.DENV_1);
		String eligible = caseWithOneTest(PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestResultType.POSITIVE, true, Serotype.DENV_4);

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(wrongTestType), SEROTYPE), is(""));
		assertThat(export.value(export.entryFor(negative), SEROTYPE), is(""));
		assertThat(export.value(export.entryFor(unverified), SEROTYPE), is(""));
		assertThat(export.value(export.entryFor(eligible), SEROTYPE), is("DENV4"));
	}

	@Test
	@DisplayName("a serotype outside the four is DENVOTH, and one never established is blank")
	public void anUnplaceableSerotypeIsDenvoth() {

		String other = caseWithOneTest(PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, Serotype.OTHER);
		String unknown = caseWithOneTest(PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, Serotype.UNKNOWN);

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		// Established but outside list (OTH) vs. not established (blank) are different claims
		assertThat(export.value(export.entryFor(other), SEROTYPE), is("DENVOTH"));
		assertThat(export.value(export.entryFor(unknown), SEROTYPE), is(""));
	}

	// ---
	// Epidemiology columns
	// ---

	@Test
	@DisplayName("ModeOfTransmission folds every mosquito constant into one code")
	public void modeOfTransmissionFoldsEveryMosquitoConstant() {

		// MALA reports four different codes; DENGUE reports one (asks how virus reached patient)
		String endemic = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.MOSQUITOES_FROM_ENDEMIC_COUNTRY));
		String local = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.MOSQUITOES_WITHOUT_EVIDENCE));
		String transfusion = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.TRANSFUSION_TRANSPLANT_RECIPIENT));
		String notEstablished = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.UNKNOWN));

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(endemic), MODE_OF_TRANSMISSION), is("MOSQ"));
		assertThat(export.value(export.entryFor(local), MODE_OF_TRANSMISSION), is("MOSQ"));
		assertThat(export.value(export.entryFor(transfusion), MODE_OF_TRANSMISSION), is("SOHO"));
		// UNKNOWN is not OTH: missing answer <> claim about the route
		assertThat(export.value(export.entryFor(notEstablished), MODE_OF_TRANSMISSION), is(""));
	}

	@Test
	@DisplayName("asymptomatic is the one clinical criterion derived, and it blanks the onset date beside it")
	public void asymptomaticIsTheOneClinicalCriterionDerived() {

		// TODO: only ASY is mapped. Telling NSEV from SEV requires WHO severity definition (not recorded).
		String asymptomatic = caseWith(c -> c.getSymptoms().setAsymptomatic(SymptomState.YES));
		String symptomatic = caseWith(c -> c.getSymptoms().setAsymptomatic(SymptomState.NO));
		String notRecorded = caseWith(c -> {
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(asymptomatic), CLINICAL_CRITERIA), is("ASY"));
		assertThat(export.value(export.entryFor(symptomatic), CLINICAL_CRITERIA), is(""));
		assertThat(export.value(export.entryFor(notRecorded), CLINICAL_CRITERIA), is(""));

		// Flag suppresses DateOfOnset: both columns cannot be filled together
		assertThat(export.value(export.entryFor(asymptomatic), DATE_OF_ONSET), is(""));
		assertThat(export.value(export.entryFor(symptomatic), DATE_OF_ONSET), is("2024-06-10"));
	}

	@Test
	@DisplayName("PlaceOfInfection counts travel inside dengue's 2-to-14-day window and nothing outside it")
	public void placeOfInfectionCountsTravelInsideTheIncubationWindow() {

		// Window is [onset - 14, onset - 2], much narrower than malaria's
		String inside = caseWith(c -> c.getEpiData().getExposures().add(travelTo(thailand)));

		String twoCountries = caseWith(c -> {
			c.getEpiData().getExposures().add(travelTo(thailand));
			c.getEpiData().getExposures().add(travelTo(brazil));
		});

		String tooEarly = caseWith(c -> {
			ExposureDto exposure = travelTo(thailand);
			exposure.setStartDate(DateHelper.subtractDays(ONSET, 40));
			exposure.setEndDate(DateHelper.subtractDays(ONSET, 30));
			c.getEpiData().getExposures().add(exposure);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.DENGUE, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(inside), PLACE_OF_INFECTION), contains("TH"));
		assertThat(export.values(export.entryFor(twoCountries), PLACE_OF_INFECTION), containsInAnyOrder("TH", "BR"));
		assertThat(export.values(export.entryFor(tooEarly), PLACE_OF_INFECTION), is(blank()));
	}

	// ---
	// Assertions
	// ---

	/**
	 * Asserts one case column-by-column, after verifying expectations cover all declared columns.
	 * 
	 * The set check ensures expectations are a contract, not a sample.
	 * Metadata changes (new/dropped variables) fail here naming the difference.
	 */
	private void assertEveryDeclaredColumn(
		ExportedEntries export,
		EpipulseDiseaseExportEntryDto entry,
		Map<EpipulseVariable, List<String>> expected) {

		assertThat(
			"the expectations must name every variable EpiPulse declares for DENGUE, and no other",
			expected.keySet(),
			is(EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.DENGUE)));

		for (Map.Entry<EpipulseVariable, List<String>> column : expected.entrySet()) {
			assertThat(column.getKey().getVariableName(), export.values(entry, column.getKey()), is(column.getValue()));
		}
	}

	private static List<String> value(String value) {
		return Collections.singletonList(value);
	}

	/** One column, present and empty -- which is what a declared variable with no answer writes. */
	private static List<String> blank() {
		return Collections.singletonList("");
	}

	// ---
	// Fixture
	// ---

	private PersonReferenceDto newPerson() {
		return creator.createPerson().toReference();
	}

	private String caseWith(Consumer<CaseDataDto> extraConfig) {
		return fixture.caze(newPerson(), ONSET, extraConfig).getUuid();
	}

	/** Case carrying exactly one test that names a serotype regardless of result. */
	private String caseWithOneTest(PathogenTestType testType, PathogenTestResultType result, boolean verified, Serotype serotype) {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(caze, testType, result, verified, TEST_DATE, typed(serotype));

		return caze.getUuid();
	}

	private static Consumer<PathogenTestDto> typed(Serotype serotype) {
		return test -> test.setSerotype(serotype);
	}

	/** Travel exposure during incubation window. */
	private ExposureDto travelTo(CountryReferenceDto country) {

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setStartDate(TRAVEL_START);
		exposure.setEndDate(TRAVEL_END);
		exposure.getLocation().setCountry(country);

		return exposure;
	}

	/** Country with NUTS code for export reporting. TestDataCreator doesn't set this. */
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
}
