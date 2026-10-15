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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.ANTIBIOTIC_PROPHYLAXIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CASE_CLASSIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_SERVICE_TYPE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CONTACT_SW;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COUNTRY_OF_BIRTH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COUNTRY_OF_NATIONALITY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ONSET;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.EPI_LINKED;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HIV_PR_EP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HIV_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MODE_OF_TRANSMISSION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN_DETECTION_RESULT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SEX_WORKER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SITE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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
import de.symeda.sormas.api.clinicalcourse.HivStatus;
import de.symeda.sormas.api.epidata.ProbableRouteOfTransmission;
import de.symeda.sormas.api.epidata.TypeOfClinicalService;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Tests Gonorrhoea export against a real database, column by column.
 * 
 * GONO is the first subject code to fill the STI risk block (clinical service, transmission route,
 * HIV status, prophylaxis and sex-work questions) that syphilis declared but reports blank.
 * 
 * {@code SiteOfInfection} is the first repeatable group here that is genuinely many-valued:
 * gonorrhoea records nine independent yes/no symptom columns (syphilis records one enum).
 * 
 * {@link #assertEveryDeclaredColumn} verifies expectations cover all declared variables,
 * catching metadata changes immediately.
 * 
 * Dates are fixed, not relative, so expected values are verifiable literals.
 */
public class GonorrhoeaEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	// Inside incubation window [2024-05-27, 2024-06-09] (1 to 14 days)
	private static final Date TRAVEL_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date TRAVEL_END = DateHelper.getDateZero(2024, 5, 5);

	private static final Date TEST_DATE = DateHelper.getDateZero(2024, 5, 12);

	private TestDataCreator.RDCF rdcf;
	private CaseFixture fixture;
	private CountryReferenceDto france;
	private CountryReferenceDto spain;
	private CountryReferenceDto italy;

	@BeforeEach
	public void setUpGonorrhoeaExport() {

		configureLuxembourgFor(EpipulseSubjectCode.GONO);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto surveillanceSupervisor = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR);
		fixture = fixtureFor(Disease.GONOCOCCAL_INFECTION, rdcf, surveillanceSupervisor);

		france = countryWithNutsCode("France", "FRA", "FR");
		spain = countryWithNutsCode("Spain", "ESP", "ES");
		italy = countryWithNutsCode("Italy", "ITA", "IT");
	}

	// ---
	// Whole file
	// ---

	@Test
	@DisplayName("a fully populated case fills every one of the 30 columns GONO declares")
	public void aFullyPopulatedCaseFillsEveryColumnGonoDeclares() {

		assignNutsCodes(rdcf, "LU10", "LU101", "LU1010");

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15, p -> {
			// Born in one country, citizen of another: these columns stay separate
			p.setBirthCountry(france);
			p.setCitizenship(spain);
			p.getAddress().setRegion(rdcf.region);
			p.getAddress().setDistrict(rdcf.district);
			p.getAddress().setCommunity(rdcf.community);
		}).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setOutcome(CaseOutcome.RECOVERED);
			c.getSymptoms().setGonococcalInfectionSiteGenital(SymptomState.YES);
			c.getEpiData().getExposures().add(travelTo(italy));
			c.getEpiData().setTypeOfClinicalService(TypeOfClinicalService.DEDICATED_STI_CLINIC);
			c.getEpiData().setProbableRouteOfTransmission(ProbableRouteOfTransmission.MSM_HOMO_OR_BISEXUAL_MALE);
			c.getEpiData().setSexWorker(YesNoUnknown.NO);
			c.getEpiData().setContactWithSexWorker(YesNoUnknown.YES);
			c.getHealthConditions().setHivStatus(HivStatus.KNOWN_POSITIVE);
			c.getHealthConditions().setHivPrep(YesNoUnknown.YES);
			c.getHealthConditions().setStiProphylaxis(YesNoUnknown.NO);
		});

		fixture.pathogenTest(caze, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, TEST_DATE);

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		Map<EpipulseVariable, List<String>> expected = new LinkedHashMap<>();

		// from the configuration rather than from the case
		expected.put(DISEASE, value("GONO"));
		expected.put(SUBJECT_CODE, value("GONO"));
		expected.put(REPORTING_COUNTRY, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(DATA_SOURCE, value(SERVER_DATA_SOURCE));
		expected.put(STATUS, value("NEW/UPDATE"));

		// from the case record
		expected.put(NATIONAL_RECORD_ID, value(caze.getUuid()));
		expected.put(CASE_CLASSIFICATION, value("CONF"));
		expected.put(PATHOGEN_DETECTION_RESULT, value("CONF"));
		expected.put(OUTCOME, value("A"));
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
		expected.put(COUNTRY_OF_BIRTH, value("FR"));
		expected.put(COUNTRY_OF_NATIONALITY, value("ES"));

		expected.put(PLACE_OF_INFECTION, value("IT"));
		expected.put(SITE_OF_INFECTION, value("GEN"));

		// STI risk block: syphilis declared these and reports blank
		expected.put(CLINICAL_SERVICE_TYPE, value("STI"));
		expected.put(MODE_OF_TRANSMISSION, value("MSM"));
		expected.put(HIV_STATUS, value("POSKNOWN"));
		expected.put(HIV_PR_EP, value("true"));
		expected.put(ANTIBIOTIC_PROPHYLAXIS, value("false"));
		expected.put(SEX_WORKER, value("false"));
		expected.put(CONTACT_SW, value("true"));

		// EU case definition accepts only laboratory-confirmed cases
		expected.put(CLINICAL_CRITERIA_STATUS, blank());
		expected.put(EPI_LINKED, blank());

		assertEveryDeclaredColumn(export, entry, expected);
	}

	@Test
	@DisplayName("a case with nothing recorded still writes every declared column")
	public void aCaseWithNothingRecordedStillWritesEveryDeclaredColumn() {

		// Both of GONO's repeatable groups are easy to have no values for - PlaceOfInfection needs
		// dated travel and SiteOfInfection needs one of nine symptom columns answered yes - so an
		// unfloored group would drop a column here and nowhere else.
		fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);
		assertThat(export.all(), hasSize(1));

		for (EpipulseVariable variable : EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.GONO)) {
			assertThat("GONO dropped " + variable.getVariableName(), export.header(), hasItem(variable.getVariableName()));
		}
	}

	@Test
	@DisplayName("a positive test for another disease does not date the case")
	public void aPositiveTestForAnotherDiseaseDoesNotDateTheCase() {

		// an earlier verified positive syphilis test on the same case is no evidence of gonorrhoea,
		// so DateUsedForStatistics and DateOfDiagnosis come from the gonorrhoea test alone
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(
			caze,
			PathogenTestType.CULTURE,
			PathogenTestResultType.POSITIVE,
			true,
			DateHelper.getDateZero(2024, 5, 3),
			test -> test.setTestedDisease(Disease.SYPHILIS));
		fixture.pathogenTest(caze, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, TEST_DATE);

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		assertThat(export.value(entry, DATE_USED_FOR_STATISTICS), is("2024-06-12"));
		assertThat(export.value(entry, DATE_OF_DIAGNOSIS), is("2024-06-12"));
	}

	// ---
	// SiteOfInfection
	// ---

	@Test
	@DisplayName("a case infected at several sites reports a column for each, in EpiPulse's order")
	public void severalSitesReportSeveralColumns() {

		// First many-valued repeatable group in this export
		// Syphilis records one site; gonorrhoea has nine yes/no columns
		String threeSites = caseWith(c -> {
			c.getSymptoms().setGonococcalInfectionSiteAnorectal(SymptomState.YES);
			c.getSymptoms().setGonococcalInfectionSiteGenital(SymptomState.YES);
			c.getSymptoms().setGonococcalInfectionSitePharyngeal(SymptomState.YES);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(threeSites), SITE_OF_INFECTION), contains("AR", "GEN", "PH"));
	}

	@Test
	@DisplayName("the four sites EpiPulse does not name are one OTH between them, not one each")
	public void theUnnamedSitesAreOneOtherBetweenThem() {

		// EpiPulse's own description: blood, eye, joint fluid, abscess all map to OTH
		// Multiple unnamed sites must report OTH once, not one per site
		String twoOtherSites = caseWith(c -> {
			c.getSymptoms().setGonococcalInfectionSiteBlood(SymptomState.YES);
			c.getSymptoms().setGonococcalInfectionSiteEye(SymptomState.YES);
		});

		String otherAndGenital = caseWith(c -> {
			c.getSymptoms().setGonococcalInfectionSiteJointFluid(SymptomState.YES);
			c.getSymptoms().setGonococcalInfectionSiteGenital(SymptomState.YES);
		});

		String freeTextOther = caseWith(c -> c.getSymptoms().setGonococcalInfectionSiteOther(SymptomState.YES));

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(twoOtherSites), SITE_OF_INFECTION), contains("OTH"));
		assertThat(export.values(export.entryFor(otherAndGenital), SITE_OF_INFECTION), contains("GEN", "OTH"));
		assertThat(export.values(export.entryFor(freeTextOther), SITE_OF_INFECTION), contains("OTH"));
	}

	@Test
	@DisplayName("a site recorded as unknown is not OTH, and a site recorded as absent is not a site")
	public void anUnknownSiteIsNotOther() {

		// The tracker for this subject code says the columns without a code of their own map to
		// OTH. That is right for blood, CSF, eye and joint fluid and wrong for "unknown": OTH means
		// a site outside the list, which is a claim about where the infection was found, where
		// unknown says the site was never established. EpipulseSiteOfInfectionRef already states
		// that rule for the same variable on the syphilis side.
		String unknownSite = caseWith(c -> c.getSymptoms().setGonococcalInfectionSiteUnknown(SymptomState.YES));

		// Only YES counts as present: NO and UNKNOWN both mean "not this site"
		// UNKNOWN is not OTH (establishes vs. not established)
		String recordedAbsent = caseWith(c -> {
			c.getSymptoms().setGonococcalInfectionSiteGenital(SymptomState.NO);
			c.getSymptoms().setGonococcalInfectionSiteAnorectal(SymptomState.UNKNOWN);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);

		// Group floored: case with no site keeps one blank column, not none
		assertThat(export.values(export.entryFor(unknownSite), SITE_OF_INFECTION), is(blank()));
		assertThat(export.values(export.entryFor(recordedAbsent), SITE_OF_INFECTION), is(blank()));
	}

	// ---
	// STI risk block
	// ---

	@Test
	@DisplayName("the four yes/no risk columns report false for no and nothing for unknown")
	public void theRiskColumnsDistinguishNoFromUnknown() {

		// Four columns share one reader: test all four in one case (false = recorded; empty = unknown)
		String answered = caseWith(c -> {
			c.getEpiData().setSexWorker(YesNoUnknown.NO);
			c.getEpiData().setContactWithSexWorker(YesNoUnknown.NO);
			c.getHealthConditions().setHivPrep(YesNoUnknown.NO);
			c.getHealthConditions().setStiProphylaxis(YesNoUnknown.NO);
		});

		String notEstablished = caseWith(c -> {
			c.getEpiData().setSexWorker(YesNoUnknown.UNKNOWN);
			c.getEpiData().setContactWithSexWorker(YesNoUnknown.UNKNOWN);
			c.getHealthConditions().setHivPrep(YesNoUnknown.UNKNOWN);
			c.getHealthConditions().setStiProphylaxis(YesNoUnknown.UNKNOWN);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);

		for (EpipulseVariable variable : Arrays.asList(SEX_WORKER, CONTACT_SW, HIV_PR_EP, ANTIBIOTIC_PROPHYLAXIS)) {
			assertThat(variable.getVariableName(), export.value(export.entryFor(answered), variable), is("false"));
			assertThat(variable.getVariableName(), export.value(export.entryFor(notEstablished), variable), is(""));
		}
	}

	@Test
	@DisplayName("HIVStatus keeps a known positive apart from a new diagnosis")
	public void hivStatusKeepsTheTwoPositivesApart() {

		// Distinction the variable exists for: diagnosed within 3 months (NEW) vs. before (KNOWN)
		String knownPositive = caseWith(c -> c.getHealthConditions().setHivStatus(HivStatus.KNOWN_POSITIVE));
		String newDiagnosis = caseWith(c -> c.getHealthConditions().setHivStatus(HivStatus.NEW_DIAGNOSIS));
		String positiveUndated = caseWith(c -> c.getHealthConditions().setHivStatus(HivStatus.POSITIVE));
		String negative = caseWith(c -> c.getHealthConditions().setHivStatus(HivStatus.NEGATIVE));
		String notKnown = caseWith(c -> c.getHealthConditions().setHivStatus(HivStatus.UNKNOWN));

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(knownPositive), HIV_STATUS), is("POSKNOWN"));
		assertThat(export.value(export.entryFor(newDiagnosis), HIV_STATUS), is("POSNEW"));
		assertThat(export.value(export.entryFor(positiveUndated), HIV_STATUS), is("POS"));
		assertThat(export.value(export.entryFor(negative), HIV_STATUS), is("NEG"));
		// Specification: leave empty when status is not known
		assertThat(export.value(export.entryFor(notKnown), HIV_STATUS), is(""));
	}

	@Test
	@DisplayName("ModeOfTransmission and ClinicalServiceType report the STI codes, and an unknown route reports nothing")
	public void theTwoStiReferenceColumnsReportTheirCodes() {

		String msm = caseWith(c -> {
			c.getEpiData().setProbableRouteOfTransmission(ProbableRouteOfTransmission.MSM_HOMO_OR_BISEXUAL_MALE);
			c.getEpiData().setTypeOfClinicalService(TypeOfClinicalService.GENERAL_PRACTITIONER);
		});

		String hetero = caseWith(c -> {
			c.getEpiData().setProbableRouteOfTransmission(ProbableRouteOfTransmission.HETEROSEXUAL_CONTACT);
			c.getEpiData().setTypeOfClinicalService(TypeOfClinicalService.OTHER_PRIMARY_CARE);
		});

		String notEstablished = caseWith(c -> c.getEpiData().setProbableRouteOfTransmission(ProbableRouteOfTransmission.UNKNOWN));

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(msm), MODE_OF_TRANSMISSION), is("MSM"));
		assertThat(export.value(export.entryFor(msm), CLINICAL_SERVICE_TYPE), is("GP"));

		assertThat(export.value(export.entryFor(hetero), MODE_OF_TRANSMISSION), is("HETERO"));
		// EpiPulse keeps other primary care apart from plain other
		assertThat(export.value(export.entryFor(hetero), CLINICAL_SERVICE_TYPE), is("OPC"));

		// Unknown is not OTH: missing answer <> claim about the route
		assertThat(export.value(export.entryFor(notEstablished), MODE_OF_TRANSMISSION), is(""));
		assertThat(export.value(export.entryFor(notEstablished), CLINICAL_SERVICE_TYPE), is(""));
	}

	// ---
	// PlaceOfInfection
	// ---

	@Test
	@DisplayName("PlaceOfInfection counts travel inside gonorrhoea's 1-to-14-day window and nothing outside it")
	public void placeOfInfectionCountsTravelInsideTheIncubationWindow() {

		String inside = caseWith(c -> c.getEpiData().getExposures().add(travelTo(france)));

		String tooEarly = caseWith(c -> {
			ExposureDto exposure = travelTo(france);
			exposure.setStartDate(DateHelper.subtractDays(ONSET, 60));
			exposure.setEndDate(DateHelper.subtractDays(ONSET, 40));
			c.getEpiData().getExposures().add(exposure);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.GONO, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(inside), PLACE_OF_INFECTION), contains("FR"));
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
			"the expectations must name every variable EpiPulse declares for GONO, and no other",
			expected.keySet(),
			is(EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.GONO)));

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
