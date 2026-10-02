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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ONSET;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.ECOFF_AZM;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HOSPITALISATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.IMPORTED;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.ISOLATE_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MODE_OF_TRANSMISSION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SEROTYPE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_AMP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CAZ;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CIP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CTX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_SXT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SPECIMEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUSPECTED_VEHICLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
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
import de.symeda.sormas.api.exposure.InfectionSource;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Tests shigellosis against a real database, column by column.
 *
 * <p>
 * SHIG is SALM's sibling: twenty-nine of its thirty-one columns match SALM's, so this suite does
 * not restate what {@link SalmonellosisEpipulseExportTest} already pins about the shared ones. It
 * focuses on the three differences:
 *
 * <ul>
 * <li><strong>{@code Specimen} answers a different question.</strong> SALM reports the specimen the
 * <em>reference laboratory</em> received; SHIG declares no receipt-date column and reports the
 * specimen the case was diagnosed from, wherever the test was run. A case whose only test is
 * local reports a specimen here and nothing there; this is asserted, not assumed.</li>
 * <li><strong>{@code Pathogen} is derived.</strong> It is the only column EpiPulse marks
 * {@code Required} for SHIG, and the species that answers it is usually named by a later test
 * than the one that made the diagnosis, which is why it has its own CTE.</li>
 * <li><strong>{@code ModeOfTransmission} and {@code SuspectedVehicle} actually fill.</strong> The
 * {@code epidata} fields behind them are annotated {@code @Diseases} for shigellosis, not
 * salmonellosis, so SALM's identical derivation is empty for every real case and this one is
 * not.</li>
 * </ul>
 *
 * <p>
 * {@link #assertEveryDeclaredColumn} takes the expectation for a whole file and first checks that
 * it names <em>every</em> variable the metadata declares for SHIG and no other, so a variable added
 * in a later metadata revision fails here with a list of what is unaccounted for rather than being
 * exported untested.
 *
 * <p>
 * Dates are fixed rather than relative to today, so every expected value is a literal that can be
 * verified by reading.
 */
public class ShigellosisEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	/**
	 * Inside the incubation window: SORMAS gives shigellosis 0 to 7 days, so an exposure counts
	 * when it overlaps {@code [2024-06-03, 2024-06-10]}. Wider than salmonellosis's 1 to 3 and
	 * narrower than syphilis's 10 to 90, which is why it is started here rather than reused.
	 */
	private static final Date EXPOSURE_START = DateHelper.getDateZero(2024, 5, 5);
	private static final Date EXPOSURE_END = DateHelper.getDateZero(2024, 5, 7);

	/** The culture that diagnosed the case, on a stool specimen. */
	private static final Date DIAGNOSTIC_TEST = DateHelper.getDateZero(2024, 5, 11);

	/** The serogrouping that named the species, three days later and on a different specimen. */
	private static final Date TYPING_TEST = DateHelper.getDateZero(2024, 5, 14);

	private static final Date LATER_TEST = DateHelper.getDateZero(2024, 5, 17);

	private TestDataCreator.RDCF rdcf;
	private CaseFixture fixture;
	private CountryReferenceDto france;
	private CountryReferenceDto italy;

	@BeforeEach
	public void setUpShigellosisExport() {

		configureLuxembourgFor(EpipulseSubjectCode.SHIG);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto surveillanceSupervisor = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR);
		fixture = fixtureFor(Disease.SHIGELLOSIS, rdcf, surveillanceSupervisor);

		france = countryWithNutsCode("France", "FRA", "FR");
		italy = countryWithNutsCode("Italy", "ITA", "IT");
	}

	// ---------------------------------------------------------------------------------------
	// the whole file
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("a fully populated case fills every one of the 31 columns SHIG declares")
	public void aFullyPopulatedCaseFillsEveryColumnShigDeclares() {

		assignNutsCodes(rdcf, "LU10", "LU101", "LU1010");

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15, p -> {
			p.getAddress().setRegion(rdcf.region);
			p.getAddress().setDistrict(rdcf.district);
			p.getAddress().setCommunity(rdcf.community);
		}).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setOutcome(CaseOutcome.RECOVERED);
			hospitalisedForTheReportedDisease().accept(c);
			c.getEpiData().setImportedCase(YesNoUnknown.YES);
			c.getEpiData().setCountry(italy);

			c.getEpiData().setModeOfTransmission(ModeOfTransmission.FOOD_OR_WATER);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.TAP_AND_WELL_WATER));
		});

		diagnosticCulture(caze, SampleMaterial.STOOL, DIAGNOSTIC_TEST);
		typingTest(caze, PathogenSpecie.SONNEI, TYPING_TEST);
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, TYPING_TEST, fullPanel());

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		Map<EpipulseVariable, List<String>> expected = new LinkedHashMap<>();

		// from the configuration rather than from the case
		expected.put(DISEASE, value("SHIG"));
		expected.put(SUBJECT_CODE, value("SHIG"));
		expected.put(REPORTING_COUNTRY, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(DATA_SOURCE, value(SERVER_DATA_SOURCE));
		expected.put(STATUS, value("NEW/UPDATE"));

		// from the case record
		expected.put(NATIONAL_RECORD_ID, value(caze.getUuid()));
		expected.put(CASE_CLASSIFICATION, value("CONF"));
		expected.put(OUTCOME, value("A"));
		expected.put(HOSPITALISATION, value("true"));
		expected.put(DATE_OF_ONSET, value("2024-06-10"));
		expected.put(DATE_OF_NOTIFICATION, value("2024-06-10"));
		// no doctor's notification, so the first positive test is the only candidate
		expected.put(DATE_OF_DIAGNOSIS, value("2024-06-11"));
		expected.put(DATE_USED_FOR_STATISTICS, value("2024-06-11"));

		// from the person
		expected.put(AGE, value("33"));
		expected.put(GENDER, value("F"));
		expected.put(PLACE_OF_RESIDENCE, value("LU1010"));
		expected.put(PLACE_OF_NOTIFICATION, value("LU1010"));

		// the epidemiology SHIG owns - and, unlike SALM's, reads from fields the case form shows
		expected.put(IMPORTED, value("true"));
		expected.put(PLACE_OF_INFECTION, value("IT"));
		expected.put(MODE_OF_TRANSMISSION, value("FOOD"));
		expected.put(SUSPECTED_VEHICLE, value("TAPWATER"));

		// the isolate: the species from the typing test, the material from the diagnosing one
		expected.put(PATHOGEN, value("SHISON"));
		expected.put(SPECIMEN, value("FAECES"));

		// all five antibiotics SHIG asks for are recorded, which is not true of any other code here
		expected.put(SIR_AMP, value("S"));
		expected.put(SIR_CAZ, value("I"));
		expected.put(SIR_CIP, value("R"));
		expected.put(SIR_CTX, value("S"));
		expected.put(SIR_SXT, value("R"));

		// the three columns SORMAS has no field for. See ShigFieldModel for each one's reason -
		// ECOFF_AZM in particular is not a missing drug but a different determination.
		expected.put(SEROTYPE, blank());
		expected.put(ISOLATE_ID, blank());
		expected.put(ECOFF_AZM, blank());

		assertEveryDeclaredColumn(export, entry, expected);
	}

	@Test
	@DisplayName("a case with nothing recorded still writes every declared column")
	public void aCaseWithNothingRecordedStillWritesEveryDeclaredColumn() {

		// The realistic export rather than a corner case: three of SHIG's columns are blank by
		// construction and both of its repeatable groups are easy to have no values for, so an
		// unfloored group or a forgotten blank would drop a column here and nowhere else.
		fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);
		assertThat(export.all(), hasSize(1));

		for (EpipulseVariable variable : EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.SHIG)) {
			assertThat("SHIG dropped " + variable.getVariableName(), export.header(), hasItem(variable.getVariableName()));
		}
	}

	// ---------------------------------------------------------------------------------------
	// Pathogen
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("the species comes from the typing test even when an earlier test made the diagnosis")
	public void theSpeciesComesFromTheTypingTestNotTheDiagnosingOne() {

		// The ordinary sequence for shigellosis: a culture confirms the case, and the species is
		// named later by a serogrouping on a fresh specimen. Reading the species off the diagnosing
		// test's row - which is what a single CTE for both columns would do - reports nothing here,
		// and the column EpiPulse marks Required would be empty for the common case.
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		diagnosticCulture(caze, SampleMaterial.STOOL, DIAGNOSTIC_TEST);
		typingTest(caze, PathogenSpecie.FLEXNERI, TYPING_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		assertThat(export.value(entry, PATHOGEN), is("SHIFLE"));
		// and the specimen still comes from the test that diagnosed the case, not the typing one
		assertThat(export.value(entry, SPECIMEN), is("FAECES"));
	}

	@Test
	@DisplayName("the earliest test that names a species wins, and a later revision does not override it")
	public void theEarliestNamedSpeciesWins() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		typingTest(caze, PathogenSpecie.BOYDII, TYPING_TEST);
		typingTest(caze, PathogenSpecie.SONNEI, LATER_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), PATHOGEN), is("SHIBOY"));
	}

	@Test
	@DisplayName("a species SHIG has no code for does not hide a later identified one")
	public void aSpeciesWithoutACodeDoesNotHideALaterOne() {

		// UNKNOWN names no species, so the earliest test that names one is the later typing
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		typingTest(caze, PathogenSpecie.UNKNOWN, TYPING_TEST);
		typingTest(caze, PathogenSpecie.SONNEI, LATER_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), PATHOGEN), is("SHISON"));
	}

	@Test
	@DisplayName("a species on an unverified or negative test is not what the case was found to carry")
	public void onlyAVerifiedPositiveTestNamesTheSpecies() {

		String unverified = caseWithOneTypingTest(PathogenSpecie.SONNEI, test -> {
			test.setTestResult(PathogenTestResultType.POSITIVE);
			test.setTestResultVerified(false);
		});

		// a species recorded beside a negative result is what was looked for, not what was there
		String negative = caseWithOneTypingTest(PathogenSpecie.SONNEI, test -> {
			test.setTestResult(PathogenTestResultType.NEGATIVE);
			test.setTestResultVerified(true);
		});

		String confirmed = caseWithOneTypingTest(PathogenSpecie.SONNEI, test -> {
			test.setTestResult(PathogenTestResultType.POSITIVE);
			test.setTestResultVerified(true);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(unverified), PATHOGEN), is(""));
		assertThat(export.value(export.entryFor(negative), PATHOGEN), is(""));
		assertThat(export.value(export.entryFor(confirmed), PATHOGEN), is("SHISON"));
	}

	@Test
	@DisplayName("Pathogen is one column holding one species, not a repeatable group")
	public void pathogenIsOneColumnHoldingOneSpecies() {

		// MALA reports the same variable as a group because a coinfection is several species.
		// EpiPulse marks it Repeatable=No for SHIG, and the two readings share one DTO field, so
		// nothing but the field model's choice of ValueDef stops the group from appearing here.
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		typingTest(caze, PathogenSpecie.DYSENTERIAE, TYPING_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.only(), PATHOGEN), contains("SHIDYS"));
		assertThat(export.header().stream().filter(PATHOGEN.getVariableName()::equals).count(), is(1L));
	}

	// ---------------------------------------------------------------------------------------
	// Specimen
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("Specimen is the material the case was diagnosed from, wherever the test was run")
	public void specimenIsTheDiagnosingMaterialFromAnyLaboratory() {

		// The difference from SALM in one assertion. SALM requires performedbyreferencelaboratory
		// and would report nothing for this case; SHIG declares no receipt-date column to pair with
		// the material, so a local culture is the answer rather than a weaker one.
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		SampleDto sample = fixture.specimen(caze, SampleMaterial.STOOL, DIAGNOSTIC_TEST);
		fixture.testOn(sample, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, DIAGNOSTIC_TEST, test -> {
			test.setPerformedByReferenceLaboratory(null);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), SPECIMEN), is("FAECES"));
	}

	@Test
	@DisplayName("the oldest positive verified test decides the specimen, and a later one does not")
	public void theOldestPositiveVerifiedTestDecidesTheSpecimen() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		// a specimen taken during follow-up is not what the diagnosis was made on
		diagnosticCulture(caze, SampleMaterial.BLOOD, LATER_TEST);
		diagnosticCulture(caze, SampleMaterial.STOOL, DIAGNOSTIC_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), SPECIMEN), is("FAECES"));
	}

	@Test
	@DisplayName("an unverified or negative test does not name the diagnostic specimen")
	public void onlyAVerifiedPositiveTestNamesTheSpecimen() {

		String unverified = caseWithOneCulture(SampleMaterial.STOOL, test -> {
			test.setTestResult(PathogenTestResultType.POSITIVE);
			test.setTestResultVerified(false);
		});

		String negative = caseWithOneCulture(SampleMaterial.STOOL, test -> {
			test.setTestResult(PathogenTestResultType.NEGATIVE);
			test.setTestResultVerified(true);
		});

		String confirmed = caseWithOneCulture(SampleMaterial.STOOL, test -> {
			test.setTestResult(PathogenTestResultType.POSITIVE);
			test.setTestResultVerified(true);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(unverified), SPECIMEN), is(""));
		assertThat(export.value(export.entryFor(negative), SPECIMEN), is(""));
		assertThat(export.value(export.entryFor(confirmed), SPECIMEN), is("FAECES"));
	}

	// ---------------------------------------------------------------------------------------
	// susceptibility
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("the five susceptibility columns come from the newest panel, and none of them is blank by construction")
	public void theSusceptibilityColumnsComeFromTheNewestPanel() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, LATER_TEST, fullPanel());
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, DIAGNOSTIC_TEST, test -> {
			DrugSusceptibilityDto earlier = DrugSusceptibilityDto.build();
			earlier.setAmpicillinSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setCeftazidimeSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setCefotaximeSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setCiprofloxacinSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setTrimethoprimSulfamethoxazoleSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(earlier);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		// every one of SHIG's SIR columns reports a value - SALM's file has seven that cannot
		assertThat(export.value(entry, SIR_AMP), is("S"));
		assertThat(export.value(entry, SIR_CAZ), is("I"));
		assertThat(export.value(entry, SIR_CTX), is("S"));
		assertThat(export.value(entry, SIR_CIP), is("R"));
		assertThat(export.value(entry, SIR_SXT), is("R"));
	}

	@Test
	@DisplayName("a recorded azithromycin result does not fill ECOFF_AZM")
	public void anAzithromycinResultDoesNotFillEcoffAzm() {

		// The column that looks derivable and is not. drugsusceptibility records azithromycin, so
		// the temptation is to read its susceptibility into this BOOL - but an ECOFF separates
		// acquired resistance from the wild type and a susceptibility interpretation separates
		// likely treatment response from failure, against a different cut-off. An isolate can be
		// above the ECOFF and still susceptible, which is exactly the case this column exists to
		// surface, so the two must not be read for each other. See ShigFieldModel.
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, DIAGNOSTIC_TEST, test -> {
			DrugSusceptibilityDto panel = DrugSusceptibilityDto.build();
			panel.setAzithromycinSusceptibility(DrugSusceptibilityType.RESISTANT);
			panel.setAzithromycinMic("32");
			test.setDrugSusceptibility(panel);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		// present in the header and empty, which is what a declared column with no answer writes
		assertThat(export.header(), hasItem(ECOFF_AZM.getVariableName()));
		assertThat(export.value(export.only(), ECOFF_AZM), is(""));
	}

	// ---------------------------------------------------------------------------------------
	// epidemiology
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("ModeOfTransmission and SuspectedVehicle report the EpiPulse code for what SORMAS recorded")
	public void theTwoEpidemiologyCodesAreReported() {

		// Both fields are annotated @Diseases for shigellosis, so both fill for a case entered
		// through the form. SALM declares the same two columns and derives them identically, and
		// its file carries them empty until that annotation changes; see SalmFieldModel. MALA also
		// reports ModeOfTransmission, from its own value set - SuspectedVehicle is SHIG's alone.
		// person-to-person is the commonest route for shigellosis and carries no vehicle: the form
		// offers the vehicle field only for food-or-water and animal-to-human, so this is what a
		// typical case looks like and the two columns are independent
		String personToPerson = caseWith(c -> c.getEpiData().setModeOfTransmission(ModeOfTransmission.PERSON_TO_PERSON)).getUuid();

		String food = caseWith(c -> {
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.FOOD_OR_WATER);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.confectionery));
		}).getUuid();

		String animal = caseWith(c -> {
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.ANIMAL_TO_HUMAN);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.FARM_ANIMAL_CONTACT));
		}).getUuid();

		// UNKNOWN is not OTH, and OTHER is not OTHERFOOD - both would turn a missing answer into a
		// positive claim
		String notEstablished = caseWith(c -> {
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.UNKNOWN);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.OTHER));
		}).getUuid();

		// the field is a multi-select and SuspectedVehicle takes one value: a source without a code
		// does not compete with the one that has a code, while two different vehicles report none
		// rather than one picked arbitrarily
		String vehicleAndOther = caseWith(c -> {
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.FOOD_OR_WATER);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.EGG_PRODUCTS, InfectionSource.OTHER));
		}).getUuid();

		String twoVehicles = caseWith(c -> {
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.FOOD_OR_WATER);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.EGG_PRODUCTS, InfectionSource.CHEESE));
		}).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(personToPerson), MODE_OF_TRANSMISSION), is("PTP"));
		assertThat(export.value(export.entryFor(personToPerson), SUSPECTED_VEHICLE), is(""));

		assertThat(export.value(export.entryFor(food), MODE_OF_TRANSMISSION), is("FOOD"));
		assertThat(export.value(export.entryFor(food), SUSPECTED_VEHICLE), is("SWEETSCHOC"));

		assertThat(export.value(export.entryFor(animal), MODE_OF_TRANSMISSION), is("ANIMAL"));
		assertThat(export.value(export.entryFor(animal), SUSPECTED_VEHICLE), is("FARMANIMAL"));

		assertThat(export.value(export.entryFor(notEstablished), MODE_OF_TRANSMISSION), is(""));
		assertThat(export.value(export.entryFor(notEstablished), SUSPECTED_VEHICLE), is(""));

		assertThat(export.value(export.entryFor(vehicleAndOther), SUSPECTED_VEHICLE), is("EGG"));
		assertThat(export.value(export.entryFor(twoVehicles), SUSPECTED_VEHICLE), is(""));
	}

	@Test
	@DisplayName("Imported reports nothing when the answer was not established")
	public void importedReportsNothingWhenNotEstablished() {

		String yes = caseWith(c -> c.getEpiData().setImportedCase(YesNoUnknown.YES)).getUuid();
		String no = caseWith(c -> c.getEpiData().setImportedCase(YesNoUnknown.NO)).getUuid();
		String unknown = caseWith(c -> c.getEpiData().setImportedCase(YesNoUnknown.UNKNOWN)).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(yes), IMPORTED), is("true"));
		assertThat(export.value(export.entryFor(no), IMPORTED), is("false"));
		// an EpiPulse BOOL has two states, and "not established" is neither of them
		assertThat(export.value(export.entryFor(unknown), IMPORTED), is(""));
	}

	@Test
	@DisplayName("PlaceOfInfection prefers a stated country of contamination over travel, and ignores exposures outside the incubation window")
	public void placeOfInfectionPrefersTheStatedCountryOfContamination() {

		String travelled = caseWith(c -> c.getEpiData().getExposures().add(travelTo(france))).getUuid();

		String stated = caseWith(c -> {
			c.getEpiData().setCountry(italy);
			c.getEpiData().getExposures().add(travelTo(france));
		}).getUuid();

		// shigellosis incubates 0 to 7 days, so a stay three weeks before onset cannot be where the
		// infection was acquired
		String tooEarly = caseWith(c -> {
			ExposureDto exposure = travelTo(france);
			exposure.setStartDate(DateHelper.subtractDays(ONSET, 24));
			exposure.setEndDate(DateHelper.subtractDays(ONSET, 20));
			c.getEpiData().getExposures().add(exposure);
		}).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SHIG, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(travelled), PLACE_OF_INFECTION), contains("FR"));
		assertThat(export.values(export.entryFor(stated), PLACE_OF_INFECTION), contains("IT"));
		// the group is floored, so a case with no place keeps one blank column rather than none
		assertThat(export.values(export.entryFor(tooEarly), PLACE_OF_INFECTION), is(blank()));
	}

	// ---------------------------------------------------------------------------------------
	// assertions
	// ---------------------------------------------------------------------------------------

	/**
	 * Asserts one exported case column by column, having first established that the expectation
	 * covers SHIG's whole declared column set.
	 *
	 * <p>
	 * The set check is what makes the caller's map a contract rather than a sample. A variable
	 * added to the metadata for this code, or one dropped from it, fails here naming the
	 * difference -- and it fails in the test whose job is to say what every column reports, rather
	 * than being exported with nothing asserting its value.
	 */
	private void assertEveryDeclaredColumn(
		ExportedEntries export,
		EpipulseDiseaseExportEntryDto entry,
		Map<EpipulseVariable, List<String>> expected) {

		assertThat(
			"the expectations must name every variable EpiPulse declares for SHIG, and no other",
			expected.keySet(),
			is(EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.SHIG)));

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

	// ---------------------------------------------------------------------------------------
	// fixture
	// ---------------------------------------------------------------------------------------

	private PersonReferenceDto newPerson() {
		return creator.createPerson().toReference();
	}

	private CaseDataDto caseWith(Consumer<CaseDataDto> extraConfig) {
		return fixture.caze(newPerson(), ONSET, extraConfig);
	}

	/** A positive verified culture on its own specimen, which is what {@code Specimen} reports. */
	private void diagnosticCulture(CaseDataDto caze, SampleMaterial material, Date testDate) {

		SampleDto sample = fixture.specimen(caze, material, testDate);
		fixture.testOn(sample, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, testDate);
	}

	/**
	 * A serogrouping naming a species, on a specimen of its own.
	 *
	 * <p>
	 * Deliberately a separate specimen from {@link #diagnosticCulture}: the two columns come from
	 * different CTEs precisely so that they need not describe one sample, and a shared fixture
	 * would let a regression pass.
	 */
	private void typingTest(CaseDataDto caze, PathogenSpecie specie, Date testDate) {

		SampleDto sample = fixture.specimen(caze, SampleMaterial.RECTAL_SWAB, testDate);
		fixture.testOn(sample, PathogenTestType.SEROGROUPING, PathogenTestResultType.POSITIVE, true, testDate, test -> test.setSpecie(specie));
	}

	/** A case carrying exactly one typing test, configured by the caller. */
	private String caseWithOneTypingTest(PathogenSpecie specie, Consumer<PathogenTestDto> testConfig) {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		SampleDto sample = fixture.specimen(caze, SampleMaterial.RECTAL_SWAB, TYPING_TEST);

		fixture.testOn(sample, PathogenTestType.SEROGROUPING, PathogenTestResultType.POSITIVE, true, TYPING_TEST, test -> {
			test.setSpecie(specie);
			testConfig.accept(test);
		});

		return caze.getUuid();
	}

	/** A case carrying exactly one culture, configured by the caller. */
	private String caseWithOneCulture(SampleMaterial material, Consumer<PathogenTestDto> testConfig) {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		SampleDto sample = fixture.specimen(caze, material, DIAGNOSTIC_TEST);

		fixture.testOn(sample, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, DIAGNOSTIC_TEST, testConfig);

		return caze.getUuid();
	}

	/**
	 * A susceptibility panel covering all five antibiotics SHIG asks for, each with a different
	 * result so that no two columns can pass by reading each other.
	 */
	private static Consumer<PathogenTestDto> fullPanel() {

		return test -> {
			DrugSusceptibilityDto panel = DrugSusceptibilityDto.build();
			panel.setAmpicillinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			panel.setCeftazidimeSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
			panel.setCefotaximeSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			panel.setCiprofloxacinSusceptibility(DrugSusceptibilityType.RESISTANT);
			panel.setTrimethoprimSulfamethoxazoleSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(panel);
		};
	}

	/** A dated stay in one country, inside the incubation window unless a caller moves it. */
	private ExposureDto travelTo(CountryReferenceDto country) {

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setStartDate(EXPOSURE_START);
		exposure.setEndDate(EXPOSURE_END);
		exposure.getLocation().setCountry(country);

		return exposure;
	}

	/**
	 * A country the export can report, which means one with a NUTS code -- {@code TestDataCreator}
	 * does not set that column, and without it the country is invisible to every place column.
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
}
