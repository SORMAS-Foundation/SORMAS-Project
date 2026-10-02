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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA_STATUS;
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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OCCUPATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PROPHYLAXIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PURPOSE_OF_TRAVEL;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
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
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.exposure.ProphylaxisAdherence;
import de.symeda.sormas.api.exposure.TravelPurpose;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Tests Malaria export against a real database, column by column.
 * 
 * MALA has no blank columns - all 25 variables have SORMAS fields behind them,
 * unlike SALM and SYPH which have "declared and empty" columns.
 * 
 * Three complex areas dominate:
 * - {@code Pathogen}: true repeatable group (coinfections with two+ species)
 * - {@code Prophylaxis} and {@code PurposeOfTravel}: describe one journey together
 * - {@code ModeOfTransmission}: MALA's value set differs from SALM's
 * 
 * {@link #assertEveryDeclaredColumn} verifies the export names every declared
 * variable for MALA and no others, catching metadata changes immediately.
 * 
 * Dates are fixed, not relative, so expected values are verifiable literals.
 */
public class MalariaEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	// Inside incubation window [2024-04-11, 2024-06-03] (7 to 60 days)
	// Widest window of any subject code; three weeks before onset counts here but not in SALM
	private static final Date TRAVEL_START = DateHelper.getDateZero(2024, 4, 20);
	private static final Date TRAVEL_END = DateHelper.getDateZero(2024, 4, 25);

	// Earlier journey, still inside window
	private static final Date EARLIER_TRAVEL_START = DateHelper.getDateZero(2024, 3, 20);
	private static final Date EARLIER_TRAVEL_END = DateHelper.getDateZero(2024, 3, 25);

	private static final Date TEST_DATE = DateHelper.getDateZero(2024, 5, 12);
	private static final Date LATER_TEST_DATE = DateHelper.getDateZero(2024, 5, 14);

	private TestDataCreator.RDCF rdcf;
	private CaseFixture fixture;
	private CountryReferenceDto ghana;
	private CountryReferenceDto nigeria;

	@BeforeEach
	public void setUpMalariaExport() {

		configureLuxembourgFor(EpipulseSubjectCode.MALA);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto surveillanceSupervisor = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR);
		fixture = fixtureFor(Disease.MALARIA, rdcf, surveillanceSupervisor);

		ghana = countryWithNutsCode("Ghana", "GHA", "GH");
		nigeria = countryWithNutsCode("Nigeria", "NGA", "NG");
	}

	// ---
	// Whole file
	// ---

	@Test
	@DisplayName("a fully populated case fills every one of the 25 columns MALA declares, none of them blank")
	public void aFullyPopulatedCaseFillsEveryColumnMalaDeclares() {

		assignNutsCodes(rdcf, "LU10", "LU101", "LU1010");

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15, p -> {
			p.getAddress().setRegion(rdcf.region);
			p.getAddress().setDistrict(rdcf.district);
			p.getAddress().setCommunity(rdcf.community);
			p.setOccupationType(occupation("WORK_IN_AIRPORT"));
		}).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setOutcome(CaseOutcome.RECOVERED);
			c.setClinicalConfirmation(YesNoUnknown.YES);
			hospitalisedForTheReportedDisease().accept(c);
			c.getEpiData().setImportedCase(YesNoUnknown.YES);
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.MOSQUITOES_FROM_ENDEMIC_COUNTRY);
			c.getEpiData().getExposures().add(travelTo(ghana, TravelPurpose.TOURISM, ProphylaxisAdherence.PROPHYLAXIS_COMPLETED));
		});

		fixture.pathogenTest(
			caze,
			PathogenTestType.THIN_BLOOD_SMEAR,
			PathogenTestResultType.POSITIVE,
			true,
			TEST_DATE,
			found(PathogenSpecie.FALCIPARUM));

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		Map<EpipulseVariable, List<String>> expected = new LinkedHashMap<>();

		// from the configuration rather than from the case
		expected.put(DISEASE, value("MALA"));
		expected.put(SUBJECT_CODE, value("MALA"));
		expected.put(REPORTING_COUNTRY, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(DATA_SOURCE, value(SERVER_DATA_SOURCE));
		expected.put(STATUS, value("NEW/UPDATE"));

		// from the case record
		expected.put(NATIONAL_RECORD_ID, value(caze.getUuid()));
		expected.put(CASE_CLASSIFICATION, value("CONF"));
		expected.put(OUTCOME, value("A"));
		expected.put(HOSPITALISATION, value("true"));
		expected.put(CLINICAL_CRITERIA_STATUS, value("true"));
		expected.put(DATE_OF_ONSET, value("2024-06-10"));
		expected.put(DATE_OF_NOTIFICATION, value("2024-06-10"));
		// No doctor's notification date: first positive test is diagnosis date
		expected.put(DATE_OF_DIAGNOSIS, value("2024-06-12"));
		expected.put(DATE_USED_FOR_STATISTICS, value("2024-06-12"));

		// from the person
		expected.put(AGE, value("33"));
		expected.put(GENDER, value("F"));
		expected.put(OCCUPATION, value("AIRW"));
		expected.put(PLACE_OF_RESIDENCE, value("LU1010"));
		expected.put(PLACE_OF_NOTIFICATION, value("LU1010"));

		expected.put(IMPORTED, value("true"));
		expected.put(MODE_OF_TRANSMISSION, blank());
		expected.put(PLACE_OF_INFECTION, value("GH"));

		// the journey
		expected.put(PROPHYLAXIS, value("true"));
		expected.put(PURPOSE_OF_TRAVEL, value("TOUR"));

		// the laboratory
		expected.put(PATHOGEN, value("PLASFALCI"));

		assertEveryDeclaredColumn(export, entry, expected);
	}

	@Test
	@DisplayName("a case with nothing recorded still writes every declared column")
	public void aCaseWithNothingRecordedStillWritesEveryDeclaredColumn() {

		// Both of MALA's repeatable groups are easy to have no values for - PlaceOfInfection needs
		// dated travel inside the window and Pathogen needs a species on a verified positive test -
		// so an unfloored group would drop a column here and nowhere else.
		fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);
		assertThat(export.all(), hasSize(1));

		for (EpipulseVariable variable : EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.MALA)) {
			assertThat("MALA dropped " + variable.getVariableName(), export.header(), hasItem(variable.getVariableName()));
		}
	}

	// ---
	// Pathogen
	// ---

	@Test
	@DisplayName("a coinfection reports one column per species, and two tests finding the same species report one")
	public void aCoinfectionReportsOneColumnPerSpecies() {

		// The case this variable is repeatable for. Reporting only the newest test's species would
		// look exactly like a case with one species, which is why it is asserted before anything
		// else about this column.
		CaseDataDto coinfected = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(
			coinfected,
			PathogenTestType.THIN_BLOOD_SMEAR,
			PathogenTestResultType.POSITIVE,
			true,
			TEST_DATE,
			found(PathogenSpecie.FALCIPARUM));
		fixture.pathogenTest(
			coinfected,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST_DATE,
			found(PathogenSpecie.VIVAX));

		// two tests, one species: the aggregate is distinct, so this is one column and not two
		// identical ones
		CaseDataDto confirmedTwice = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(
			confirmedTwice,
			PathogenTestType.THIN_BLOOD_SMEAR,
			PathogenTestResultType.POSITIVE,
			true,
			TEST_DATE,
			found(PathogenSpecie.FALCIPARUM));
		fixture.pathogenTest(
			confirmedTwice,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST_DATE,
			found(PathogenSpecie.FALCIPARUM));

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(coinfected.getUuid()), PATHOGEN), contains("PLASFALCI", "PLASVIVAX"));
		assertThat(export.values(export.entryFor(confirmedTwice.getUuid()), PATHOGEN), contains("PLASFALCI"));
	}

	@Test
	@DisplayName("a negative, unverified or deleted test names no pathogen")
	public void onlyAVerifiedPositiveTestNamesAPathogen() {

		// Species on negative result = what lab looked for, not what it found
		// Unverified = nobody's assertion it was there. Neither qualifies.
		String negative = caseWithOneSpecie(PathogenSpecie.FALCIPARUM, PathogenTestResultType.NEGATIVE, true);
		String unverified = caseWithOneSpecie(PathogenSpecie.FALCIPARUM, PathogenTestResultType.POSITIVE, false);
		String confirmed = caseWithOneSpecie(PathogenSpecie.FALCIPARUM, PathogenTestResultType.POSITIVE, true);

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		// Both floored: cases with nothing recorded keep the declared columns
		assertThat(export.values(export.entryFor(negative), PATHOGEN), is(blank()));
		assertThat(export.values(export.entryFor(unverified), PATHOGEN), is(blank()));
		assertThat(export.values(export.entryFor(confirmed), PATHOGEN), contains("PLASFALCI"));
	}

	@Test
	@DisplayName("both SORMAS spellings of an unspecified species report PLASSPP once, not twice")
	public void bothSpellingsOfAnUnspecifiedSpeciesReportOneColumn() {

		// Mapping not one-to-one: both SPP and NOT_SPECIFIED become PLASSPP
		// CTE de-duplicates before, model de-duplicates after mapping
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(caze, PathogenTestType.THIN_BLOOD_SMEAR, PathogenTestResultType.POSITIVE, true, TEST_DATE, found(PathogenSpecie.SPP));
		fixture.pathogenTest(
			caze,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST_DATE,
			found(PathogenSpecie.NOT_SPECIFIED));

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.only(), PATHOGEN), contains("PLASSPP"));
	}

	@Test
	@DisplayName("an unspecified species next to an identified one reports only the identified one")
	public void anUnspecifiedSpeciesDoesNotSitNextToAnIdentifiedOne() {

		// a screening test that could not name the species and a smear that did describe one
		// infection; PLASFALCI, PLASSPP would read as a co-infection with a second Plasmodium
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(
			caze,
			PathogenTestType.LATERAL_FLOW_ASSAY,
			PathogenTestResultType.POSITIVE,
			true,
			TEST_DATE,
			found(PathogenSpecie.NOT_SPECIFIED));
		fixture.pathogenTest(
			caze,
			PathogenTestType.THIN_BLOOD_SMEAR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST_DATE,
			found(PathogenSpecie.FALCIPARUM));

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.only(), PATHOGEN), contains("PLASFALCI"));
	}

	// ---
	// Journey: Prophylaxis and PurposeOfTravel
	// ---

	@Test
	@DisplayName("Prophylaxis and PurposeOfTravel describe one journey, the latest the case recorded")
	public void bothTravelColumnsDescribeOneJourney() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET, c -> {
			ExposureDto earlier = travelTo(ghana, TravelPurpose.BUSINESS, ProphylaxisAdherence.PROPHYLAXIS_COMPLETED);
			earlier.setStartDate(EARLIER_TRAVEL_START);
			earlier.setEndDate(EARLIER_TRAVEL_END);

			c.getEpiData().getExposures().add(earlier);
			c.getEpiData().getExposures().add(travelTo(nigeria, TravelPurpose.TOURISM, ProphylaxisAdherence.PROPHYLAXIS_NOT_STARTED));
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		// Both columns read from latest journey (same CTE row)
		// Taking them independently would pair latest purpose with latest adherence,
		// describing a trip nobody took
		assertThat(export.value(export.only(), PURPOSE_OF_TRAVEL), is("TOUR"));
		assertThat(export.value(export.only(), PROPHYLAXIS), is("false"));

		// Both journeys count for PlaceOfInfection (different question)
		assertThat(export.values(export.only(), PLACE_OF_INFECTION), contains("GH", "NG"));
	}

	@Test
	@DisplayName("only a travel exposure answers the travel columns")
	public void onlyATravelExposureAnswersTheTravelColumns() {

		// ExposureForm shows these fields on travel exposures only; clears them on type change.
		// Values on other types are leftovers, not answers.
		String travelled =
			caseWith(c -> c.getEpiData().getExposures().add(travelTo(ghana, TravelPurpose.TOURISM, ProphylaxisAdherence.PROPHYLAXIS_COMPLETED)));

		String otherExposure = caseWith(c -> {
			ExposureDto gathering = travelTo(ghana, TravelPurpose.TOURISM, ProphylaxisAdherence.PROPHYLAXIS_COMPLETED);
			gathering.setExposureType(ExposureType.GATHERING);
			c.getEpiData().getExposures().add(gathering);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(travelled), PURPOSE_OF_TRAVEL), is("TOUR"));
		assertThat(export.value(export.entryFor(travelled), PROPHYLAXIS), is("true"));

		assertThat(export.value(export.entryFor(otherExposure), PURPOSE_OF_TRAVEL), is(""));
		assertThat(export.value(export.entryFor(otherExposure), PROPHYLAXIS), is(""));
	}

	@Test
	@DisplayName("Prophylaxis is true only for a completed course, and false for the three ways not to complete one")
	public void prophylaxisIsTrueOnlyForACompletedCourse() {

		// Variable asks: was the full course taken?
		// Prescribed-not-completed, prescribed-not-taken, never-prescribed all answer no.
		Map<ProphylaxisAdherence, String> expected = new LinkedHashMap<>();
		expected.put(ProphylaxisAdherence.PROPHYLAXIS_COMPLETED, "true");
		expected.put(ProphylaxisAdherence.PROPHYLAXIS_PARTIAL, "false");
		expected.put(ProphylaxisAdherence.PROPHYLAXIS_NOT_STARTED, "false");
		expected.put(ProphylaxisAdherence.PROPHYLAXIS_NOT_PRESCRIBED, "false");
		// UNKNOWN and OTHER join blanks: free text beside OTHER may describe completed course
		expected.put(ProphylaxisAdherence.UNKNOWN, "");
		expected.put(ProphylaxisAdherence.OTHER, "");

		Map<ProphylaxisAdherence, String> caseUuids = new LinkedHashMap<>();
		for (ProphylaxisAdherence adherence : expected.keySet()) {
			caseUuids.put(adherence, caseWith(c -> c.getEpiData().getExposures().add(travelTo(ghana, TravelPurpose.TOURISM, adherence))));
		}

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		for (Map.Entry<ProphylaxisAdherence, String> column : expected.entrySet()) {
			assertThat(column.getKey().name(), export.value(export.entryFor(caseUuids.get(column.getKey())), PROPHYLAXIS), is(column.getValue()));
		}
	}

	// ---
	// ModeOfTransmission, Occupation, CaseClassification
	// ---

	@Test
	@DisplayName("ModeOfTransmission reports MALA's codes, which tell local transmission apart from imported")
	public void modeOfTransmissionReportsMalasCodes() {

		// Two of these three silently invert; asserted end-to-end
		// Reader draws indigenous-vs-introduced conclusion from this column
		String indigenous = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.MOSQUITOES_WITHOUT_EVIDENCE));
		String introduced = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.MOSQUITOES_WITH_STRONG_EPI_EVIDENCE));
		String healthcare = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.MEDICAL_CARE));

		// No code exists; not OTH. All MALA codes describe transmission within reporting country.
		String abroad = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.MOSQUITOES_FROM_ENDEMIC_COUNTRY));

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(indigenous), MODE_OF_TRANSMISSION), is("MOSQINDIG"));
		assertThat(export.value(export.entryFor(introduced), MODE_OF_TRANSMISSION), is("MOSQINTRO"));
		assertThat(export.value(export.entryFor(healthcare), MODE_OF_TRANSMISSION), is("HAI"));
		assertThat(export.value(export.entryFor(abroad), MODE_OF_TRANSMISSION), is(""));
	}

	@Test
	@DisplayName("Occupation reports the airport worker by name and everything else as Other")
	public void occupationReportsTheAirportWorkerByName() {

		// Seeded for DENGUE and MALARIA: real use case, not just a test constant
		String airportWorker = caseWithOccupation("WORK_IN_AIRPORT");
		String healthcareWorker = caseWithOccupation("HEALTHCARE_WORKER");
		String butcher = caseWithOccupation("BUTCHER");
		String notStated = caseWithOccupation("UNKNOWN");

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(airportWorker), OCCUPATION), is("AIRW"));
		assertThat(export.value(export.entryFor(healthcareWorker), OCCUPATION), is("HCW"));
		assertThat(export.value(export.entryFor(butcher), OCCUPATION), is("OTH"));
		// the exception to the fall-through: OTH would turn "not stated" into "stated, and outside
		// the list"
		assertThat(export.value(export.entryFor(notStated), OCCUPATION), is(""));
	}

	@Test
	@DisplayName("CaseClassification is written as recorded, including the two values MALA's value set will reject")
	public void caseClassificationIsWrittenAsRecorded() {

		// MALA's value set only accepts CONF; POSS and PROB below are rejected by EpiPulse.
		// Proper answer: filter suspected cases out per subject code, not blank classification.
		String confirmed = fixture.caze(newPerson(), ONSET).getUuid();
		String suspected = classifiedCase(CaseClassification.SUSPECT);
		String probable = classifiedCase(CaseClassification.PROBABLE);

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(confirmed), CASE_CLASSIFICATION), is("CONF"));
		assertThat(export.value(export.entryFor(suspected), CASE_CLASSIFICATION), is("POSS"));
		assertThat(export.value(export.entryFor(probable), CASE_CLASSIFICATION), is("PROB"));
	}

	// ---
	// PlaceOfInfection
	// ---

	@Test
	@DisplayName("PlaceOfInfection counts travel across malaria's 7-to-60-day window and nothing outside it")
	public void placeOfInfectionCountsTravelAcrossTheLongIncubationWindow() {

		// Window is [onset - 60, onset - 7]. Both bounds exercised:
		// off-by-one would go unnoticed in a 53-day window.
		String inside = caseWith(c -> c.getEpiData().getExposures().add(travelTo(ghana, null, null)));

		String tooLate = caseWith(c -> {
			ExposureDto exposure = travelTo(ghana, null, null);
			exposure.setStartDate(DateHelper.subtractDays(ONSET, 5));
			exposure.setEndDate(DateHelper.subtractDays(ONSET, 2));
			c.getEpiData().getExposures().add(exposure);
		});

		String tooEarly = caseWith(c -> {
			ExposureDto exposure = travelTo(ghana, null, null);
			exposure.setStartDate(DateHelper.subtractDays(ONSET, 120));
			exposure.setEndDate(DateHelper.subtractDays(ONSET, 90));
			c.getEpiData().getExposures().add(exposure);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.MALA, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(inside), PLACE_OF_INFECTION), contains("GH"));
		assertThat(export.values(export.entryFor(tooLate), PLACE_OF_INFECTION), is(blank()));
		assertThat(export.values(export.entryFor(tooEarly), PLACE_OF_INFECTION), is(blank()));
	}

	// ---
	// Assertions
	// ---

	/**
	 * Asserts one case column-by-column, after verifying expectations cover all declared columns.
	 * 
	 * The set check ensures expectations are a contract, not a sample.
	 * Metadata changes (new/dropped variables) fail here naming the difference,
	 * not in silent exports.
	 */
	private void assertEveryDeclaredColumn(
		ExportedEntries export,
		EpipulseDiseaseExportEntryDto entry,
		Map<EpipulseVariable, List<String>> expected) {

		assertThat(
			"the expectations must name every variable EpiPulse declares for MALA, and no other",
			expected.keySet(),
			is(EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.MALA)));

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

	private String classifiedCase(CaseClassification classification) {
		return fixture.caze(newPerson(), ONSET, c -> c.setCaseClassification(classification)).getUuid();
	}

	private String caseWithOccupation(String occupationType) {

		PersonReferenceDto person =
			creator.createPerson("Person", occupationType, p -> p.setOccupationType(occupation(occupationType))).toReference();

		return fixture.caze(person, ONSET).getUuid();
	}

	/** Case carrying exactly one test that names a species regardless of result. */
	private String caseWithOneSpecie(PathogenSpecie specie, PathogenTestResultType result, boolean verified) {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(caze, PathogenTestType.THIN_BLOOD_SMEAR, result, verified, TEST_DATE, found(specie));

		return caze.getUuid();
	}

	private static Consumer<PathogenTestDto> found(PathogenSpecie specie) {
		return test -> test.setSpecie(specie);
	}

	/**
	 * {@code CustomizableEnum} carrying only stored value - all export reads from person.occupationtype.
	 */
	private static OccupationType occupation(String value) {

		OccupationType occupationType = new OccupationType();
		occupationType.setValue(value);

		return occupationType;
	}

	/**
	 * Travel exposure during incubation window, with both travel column values.
	 */
	private ExposureDto travelTo(CountryReferenceDto country, TravelPurpose purpose, ProphylaxisAdherence adherence) {

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setStartDate(TRAVEL_START);
		exposure.setEndDate(TRAVEL_END);
		exposure.getLocation().setCountry(country);
		exposure.setTravelPurpose(purpose);
		exposure.setProphylaxisAdherence(adherence);

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
