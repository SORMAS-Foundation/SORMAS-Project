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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_RECEIPT_REFERENCE_LAB;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_RECEIPT_SOURCE_LAB;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.ESBL;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HOSPITALISATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.IMPORTED;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.ISOLATE_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MODE_OF_TRANSMISSION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_CARBAPENEMASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SEQUENCE_TYPE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SEROTYPE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_AMP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CAZ;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CHL;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CIP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CTX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_GEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_MEM;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_NAL;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_SMX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_SXT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_TCY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_TMP;
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
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * 
 * Tests Salmonellosis data against a real database, column by column.
 * 
 * <p>
 * SALM is the first subject code whose file is mostly laboratory characterisation of an isolate.
 * 
 * It is also the first with a column pair -- {@code Specimen} and {@code DateOfReceiptReferenceLab}
 * that must describe <em>one</em> specimen out of the several belonging to a case.
 * 
 * <p>
 * {@link #assertEveryDeclaredColumn} takes the expectation for a whole file and first checks that
 * it names <em>every</em> variable the metadata declares for SALM and no other. If a variable is
 * added in a later metadata revision, the test fails here with a list of what is unaccounted for,
 * rather than letting the variable be exported untested.
 *
 * <p>
 * The dates are fixed rather than relative to today, so every expected value is a literal that can
 * be checked by reading.
 */
public class SalmonellosisEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	/**
	 * Inside the incubation window: SORMAS gives salmonellosis 1 to 3 days, so an exposure counts
	 * when it overlaps {@code [2024-06-07, 2024-06-09]}. A far shorter window than the syphilis
	 * suite's, which is the point of stating it rather than reusing a date.
	 */
	private static final Date EXPOSURE_START = DateHelper.getDateZero(2024, 5, 7);
	private static final Date EXPOSURE_END = DateHelper.getDateZero(2024, 5, 8);

	private static final Date FIRST_LOCAL_TEST = DateHelper.getDateZero(2024, 5, 11);
	private static final Date REFERENCE_TEST = DateHelper.getDateZero(2024, 5, 13);
	private static final Date LATER_REFERENCE_TEST = DateHelper.getDateZero(2024, 5, 17);

	private static final Date REFERENCE_RECEIPT = DateHelper.getDateZero(2024, 5, 12);
	private static final Date LATER_REFERENCE_RECEIPT = DateHelper.getDateZero(2024, 5, 16);

	private TestDataCreator.RDCF rdcf;
	private CaseFixture fixture;
	private UserReferenceDto reportingUser;
	private CountryReferenceDto france;
	private CountryReferenceDto italy;

	@BeforeEach
	public void setUpSalmonellosisExport() {

		configureLuxembourgFor(EpipulseSubjectCode.SALM);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto surveillanceSupervisor = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR);
		reportingUser = surveillanceSupervisor.toReference();
		fixture = fixtureFor(Disease.SALMONELLOSIS, rdcf, surveillanceSupervisor);

		france = countryWithNutsCode("France", "FRA", "FR");
		italy = countryWithNutsCode("Italy", "ITA", "IT");
	}

	// ---------------------------------------------------------------------------------------
	// the whole file
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("a fully populated case fills every one of the 41 columns SALM declares")
	public void aFullyPopulatedCaseFillsEveryColumnSalmDeclares() {

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
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.EGG_PRODUCTS));
		});

		referenceLaboratoryTest(caze, SampleMaterial.STOOL, REFERENCE_RECEIPT, REFERENCE_TEST);
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, REFERENCE_TEST, fullPanel());

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		Map<EpipulseVariable, List<String>> expected = new LinkedHashMap<>();

		// from the configuration rather than from the case
		expected.put(DISEASE, value("SALM"));
		expected.put(SUBJECT_CODE, value("SALM"));
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
		expected.put(DATE_OF_DIAGNOSIS, value("2024-06-13"));
		expected.put(DATE_USED_FOR_STATISTICS, value("2024-06-13"));

		// from the person
		expected.put(AGE, value("33"));
		expected.put(GENDER, value("F"));
		expected.put(PLACE_OF_RESIDENCE, value("LU1010"));
		expected.put(PLACE_OF_NOTIFICATION, value("LU1010"));

		// the epidemiology SALM owns
		expected.put(IMPORTED, value("true"));
		expected.put(PLACE_OF_INFECTION, value("IT"));
		expected.put(MODE_OF_TRANSMISSION, value("FOOD"));
		expected.put(SUSPECTED_VEHICLE, value("EGG"));

		// the reference laboratory's specimen answers both of these, and only that one
		expected.put(SPECIMEN, value("FAECES"));
		expected.put(DATE_OF_RECEIPT_REFERENCE_LAB, value("2024-06-12"));

		// the five antibiotics drugsusceptibility has a column for
		expected.put(SIR_AMP, value("S"));
		expected.put(SIR_CAZ, value("I"));
		expected.put(SIR_CIP, value("R"));
		expected.put(SIR_CTX, value("S"));
		expected.put(SIR_SXT, value("R"));

		// the seven it does not: declared, so they are columns, and empty because nothing in SORMAS
		// records them. See SalmFieldModel.addSusceptibilities.
		expected.put(SIR_CHL, blank());
		expected.put(SIR_GEN, blank());
		expected.put(SIR_NAL, blank());
		expected.put(SIR_MEM, blank());
		expected.put(SIR_SMX, blank());
		expected.put(SIR_TCY, blank());
		expected.put(SIR_TMP, blank());

		// the six characterisation columns SORMAS has no field for
		expected.put(SEROTYPE, blank());
		expected.put(ISOLATE_ID, blank());
		expected.put(SEQUENCE_TYPE, blank());
		expected.put(DATE_OF_RECEIPT_SOURCE_LAB, blank());
		expected.put(ESBL, blank());
		expected.put(RESULT_CARBAPENEMASE, blank());

		assertEveryDeclaredColumn(export, entry, expected);
	}

	@Test
	@DisplayName("a case with nothing recorded still writes every declared column")
	public void aCaseWithNothingRecordedStillWritesEveryDeclaredColumn() {

		// This is a realistic export rather than a corner case. Ten of SALM's columns are blank by
		// construction, and both of its repeatable groups can easily have no values.
		fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);
		assertThat(export.all(), hasSize(1));

		for (EpipulseVariable variable : EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.SALM)) {
			assertThat("SALM dropped " + variable.getVariableName(), export.header(), hasItem(variable.getVariableName()));
		}
	}

	// ---------------------------------------------------------------------------------------
	// the reference laboratory's specimen
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("Specimen and DateOfReceiptReferenceLab describe one specimen, the earliest the reference laboratory confirmed")
	public void bothColumnsDescribeTheEarliestConfirmingSpecimen() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		// a later confirmation of the same case is a repeat: EpiPulse asks when the reference
		// laboratory received the specimen, not when it last saw one
		referenceLaboratoryTest(caze, SampleMaterial.BLOOD, LATER_REFERENCE_RECEIPT, LATER_REFERENCE_TEST);
		referenceLaboratoryTest(caze, SampleMaterial.STOOL, REFERENCE_RECEIPT, REFERENCE_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);

		// both columns come from the earlier specimen, which is the whole point of them coming from
		// one CTE row rather than two independent selections
		assertThat(export.value(export.only(), SPECIMEN), is("FAECES"));
		assertThat(export.value(export.only(), DATE_OF_RECEIPT_REFERENCE_LAB), is("2024-06-12"));
	}

	@Test
	@DisplayName("a local, unverified or negative test is not the reference laboratory's confirmation")
	public void onlyAVerifiedPositiveReferenceTestCounts() {

		// each case carries exactly one test, failing one of the four conditions
		String local = caseWithOneTest(SampleMaterial.STOOL, test -> {
			test.setPerformedByReferenceLaboratory(null);
			test.setTestResult(PathogenTestResultType.POSITIVE);
			test.setTestResultVerified(true);
		});

		String unverified = caseWithOneTest(SampleMaterial.STOOL, test -> {
			test.setPerformedByReferenceLaboratory(true);
			test.setTestResult(PathogenTestResultType.POSITIVE);
			test.setTestResultVerified(false);
		});

		String negative = caseWithOneTest(SampleMaterial.STOOL, test -> {
			test.setPerformedByReferenceLaboratory(true);
			test.setTestResult(PathogenTestResultType.NEGATIVE);
			test.setTestResultVerified(true);
		});

		String confirmed = caseWithOneTest(SampleMaterial.STOOL, test -> {
			test.setPerformedByReferenceLaboratory(true);
			test.setTestResult(PathogenTestResultType.POSITIVE);
			test.setTestResultVerified(true);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);

		// a flag nobody set is not the reference laboratory: the column names it specifically, and
		// a local result is not a weaker answer to the question
		assertThat(export.value(export.entryFor(local), SPECIMEN), is(""));
		assertThat(export.value(export.entryFor(unverified), SPECIMEN), is(""));
		assertThat(export.value(export.entryFor(negative), SPECIMEN), is(""));
		assertThat(export.value(export.entryFor(confirmed), SPECIMEN), is("FAECES"));

		assertThat(export.value(export.entryFor(local), DATE_OF_RECEIPT_REFERENCE_LAB), is(""));
		assertThat(export.value(export.entryFor(confirmed), DATE_OF_RECEIPT_REFERENCE_LAB), is("2024-06-12"));
	}

	@Test
	@DisplayName("Specimen names the six enteric materials and calls everything else Other")
	public void specimenNamesTheSixEntericMaterials() {

		// This value set has only six values, compared with the sixty-odd values SampleMaterial carries,
		// so OTH is the usual result rather than the exception. See
		// EpipulseLaboratoryMapper.mapSampleMaterialToEntericSpecimenCode for why this mapper falls
		// through to OTH while the other two mappers fall through to nothing.
		Map<SampleMaterial, String> expected = new LinkedHashMap<>();
		expected.put(SampleMaterial.STOOL, "FAECES");
		expected.put(SampleMaterial.BLOOD, "BLOOD");
		expected.put(SampleMaterial.EDTA_WHOLE_BLOOD, "BLOOD");
		expected.put(SampleMaterial.CEREBROSPINAL_FLUID, "CSF");
		expected.put(SampleMaterial.URINE, "URINE");
		expected.put(SampleMaterial.PUS, "PUS");
		// recorded, and outside the six - which is what OTH means
		expected.put(SampleMaterial.RECTAL_SWAB, "OTH");
		expected.put(SampleMaterial.SERA, "OTH");
		// not established, which is not a specimen type
		expected.put(SampleMaterial.UNKNOWN, null);

		Map<String, String> cases = new LinkedHashMap<>();
		for (Map.Entry<SampleMaterial, String> material : expected.entrySet()) {
			cases.put(caseWithOneTest(material.getKey(), confirmingReferenceTest()), material.getValue());
		}

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);

		for (Map.Entry<String, String> expectation : cases.entrySet()) {
			assertThat(
				"Specimen for " + expectation.getKey(),
				export.value(export.entryFor(expectation.getKey()), SPECIMEN),
				is(expectation.getValue() == null ? "" : expectation.getValue()));
		}
	}

	// ---------------------------------------------------------------------------------------
	// susceptibility
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("the susceptibility columns come from the newest panel")
	public void theSusceptibilityColumnsComeFromTheNewestPanel() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		fixture
			.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, LATER_REFERENCE_TEST, fullPanel());
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, FIRST_LOCAL_TEST, test -> {
			DrugSusceptibilityDto earlier = DrugSusceptibilityDto.build();
			earlier.setAmpicillinSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setCeftazidimeSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setCefotaximeSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setCiprofloxacinSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setTrimethoprimSulfamethoxazoleSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(earlier);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		assertThat(export.value(entry, SIR_AMP), is("S"));
		assertThat(export.value(entry, SIR_CAZ), is("I"));
		assertThat(export.value(entry, SIR_CTX), is("S"));
		assertThat(export.value(entry, SIR_CIP), is("R"));
		assertThat(export.value(entry, SIR_SXT), is("R"));
	}

	@Test
	@DisplayName("a case with no susceptibility panel reports every SIR column empty rather than dropping it")
	public void aCaseWithNoPanelReportsEverySirColumnEmpty() {

		fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		// the five that are derived and the seven that are blank by construction look identical
		// here, which is the point.
		for (EpipulseVariable sir : new EpipulseVariable[] {
			SIR_AMP,
			SIR_CAZ,
			SIR_CHL,
			SIR_CIP,
			SIR_CTX,
			SIR_GEN,
			SIR_NAL,
			SIR_MEM,
			SIR_SMX,
			SIR_SXT,
			SIR_TCY,
			SIR_TMP }) {
			assertThat(sir.getVariableName(), export.value(entry, sir), is(""));
		}
	}

	// ---------------------------------------------------------------------------------------
	// epidemiology
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("Imported reports nothing when the answer was not established")
	public void importedReportsNothingWhenNotEstablished() {

		String yes = caseWith(c -> c.getEpiData().setImportedCase(YesNoUnknown.YES)).getUuid();
		String no = caseWith(c -> c.getEpiData().setImportedCase(YesNoUnknown.NO)).getUuid();
		String unknown = caseWith(c -> c.getEpiData().setImportedCase(YesNoUnknown.UNKNOWN)).getUuid();
		String unanswered = fixture.caze(newPerson(), ONSET).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(yes), IMPORTED), is("true"));
		assertThat(export.value(export.entryFor(no), IMPORTED), is("false"));
		// an EpiPulse BOOL has two states, and "not established" is neither of them
		assertThat(export.value(export.entryFor(unknown), IMPORTED), is(""));
		assertThat(export.value(export.entryFor(unanswered), IMPORTED), is(""));
	}

	@Test
	@DisplayName("ModeOfTransmission and SuspectedVehicle report the EpiPulse code for what SORMAS recorded")
	public void theTwoEpidemiologyCodesAreReported() {

		// These fields are annotated with @Diseases for giardiasis, cryptosporidiosis, and shigellosis,
		// so the salmonellosis case form does not display them today. The columns still persist either way,
		// and this test asserts the derivation. Whether the form should offer them is a question about the
		// case form, not about this export; see SalmFieldModel.
		String animal = caseWith(c -> {
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.ANIMAL_TO_HUMAN);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.FARM_ANIMAL_CONTACT));
		}).getUuid();

		String food = caseWith(c -> {
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.FOOD_OR_WATER);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.confectionery));
		}).getUuid();

		// UNKNOWN is not OTH, and OTHER is not OTHERFOOD - both would turn a missing answer into a
		// positive claim
		String notEstablished = caseWith(c -> {
			c.getEpiData().setModeOfTransmission(ModeOfTransmission.UNKNOWN);
			c.getEpiData().setInfectionSource(EnumSet.of(InfectionSource.OTHER));
		}).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(animal), MODE_OF_TRANSMISSION), is("ANIMAL"));
		assertThat(export.value(export.entryFor(animal), SUSPECTED_VEHICLE), is("FARMANIMAL"));

		assertThat(export.value(export.entryFor(food), MODE_OF_TRANSMISSION), is("FOOD"));
		assertThat(export.value(export.entryFor(food), SUSPECTED_VEHICLE), is("SWEETSCHOC"));

		assertThat(export.value(export.entryFor(notEstablished), MODE_OF_TRANSMISSION), is(""));
		assertThat(export.value(export.entryFor(notEstablished), SUSPECTED_VEHICLE), is(""));
	}

	@Test
	@DisplayName("PlaceOfInfection prefers a stated country of contamination over travel, and ignores exposures outside the incubation window")
	public void placeOfInfectionPrefersTheStatedCountryOfContamination() {

		String travelled = caseWith(c -> c.getEpiData().getExposures().add(travelTo(france))).getUuid();

		// EpiData.country is "Country of contamination" and is enabled for salmonellosis, unlike
		// the two codes that reported this variable before SALM - so for the first time the branch
		// is exercised by a real field rather than one that is null for every exported case
		String stated = caseWith(c -> {
			c.getEpiData().setCountry(italy);
			c.getEpiData().getExposures().add(travelTo(france));
		}).getUuid();

		// salmonellosis incubates 1 to 3 days, so a stay a fortnight before onset cannot be where
		// the infection was acquired
		String tooEarly = caseWith(c -> {
			ExposureDto exposure = travelTo(france);
			exposure.setStartDate(DateHelper.subtractDays(ONSET, 20));
			exposure.setEndDate(DateHelper.subtractDays(ONSET, 14));
			c.getEpiData().getExposures().add(exposure);
		}).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SALM, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(travelled), PLACE_OF_INFECTION), contains("FR"));
		assertThat(export.values(export.entryFor(stated), PLACE_OF_INFECTION), contains("IT"));
		// the group is floored, so a case with no place keeps one blank column rather than none
		assertThat(export.values(export.entryFor(tooEarly), PLACE_OF_INFECTION), is(blank()));
	}

	// ---------------------------------------------------------------------------------------
	// assertions
	// ---------------------------------------------------------------------------------------

	/**
	 * Asserts one exported case column by column, after first confirming that the expectation covers
	 * SALM's entire declared column set.
	 *
	 * <p>
	 * The set check is what turns the caller's map into a contract rather than a sample. If a variable
	 * is added to or removed from the metadata for this code, this test fails and names the difference.
	 * That way the failure lands in the test responsible for stating what every column reports, instead
	 * of the variable being exported with nothing asserting its value.
	 */
	private void assertEveryDeclaredColumn(
		ExportedEntries export,
		EpipulseDiseaseExportEntryDto entry,
		Map<EpipulseVariable, List<String>> expected) {

		assertThat(
			"the expectations must name every variable EpiPulse declares for SALM, and no other",
			expected.keySet(),
			is(EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.SALM)));

		for (Map.Entry<EpipulseVariable, List<String>> column : expected.entrySet()) {
			assertThat(column.getKey().getVariableName(), export.values(entry, column.getKey()), is(column.getValue()));
		}
	}

	private static List<String> value(String value) {
		return Collections.singletonList(value);
	}

	/** One column, present and empty. */
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

	/**
	 * A specimen the reference laboratory received and confirmed, the pair of rows both
	 * {@code Specimen} and {@code DateOfReceiptReferenceLab} are read from.
	 *
	 * <p>
	 * {@code CaseFixture.specimen} does not set a receipt date so the sample is built here.
	 */
	private void referenceLaboratoryTest(CaseDataDto caze, SampleMaterial material, Date receivedDate, Date testDate) {

		SampleDto sample = creator.createSample(caze.toReference(), reportingUser, rdcf.facility, s -> {
			s.setSampleMaterial(material);
			s.setSampleDateTime(receivedDate);
			s.setReceived(true);
			s.setReceivedDate(receivedDate);
		});

		fixture.testOn(sample, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, testDate, confirmingReferenceTest());
	}

	/** A case carrying exactly one specimen and one test on it, configured by the caller. */
	private String caseWithOneTest(SampleMaterial material, Consumer<PathogenTestDto> testConfig) {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		SampleDto sample = creator.createSample(caze.toReference(), reportingUser, rdcf.facility, s -> {
			s.setSampleMaterial(material);
			s.setSampleDateTime(REFERENCE_RECEIPT);
			s.setReceived(true);
			s.setReceivedDate(REFERENCE_RECEIPT);
		});

		fixture.testOn(sample, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, REFERENCE_TEST, testConfig);

		return caze.getUuid();
	}

	/** The reference-laboratory flag. */
	private static Consumer<PathogenTestDto> confirmingReferenceTest() {
		return test -> test.setPerformedByReferenceLaboratory(true);
	}

	/**
	 * A susceptibility panel covering the five antibiotics SORMAS records of SALM's twelve, each
	 * with a different result.
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
	 * A country the export can report, means one with a NUTS code - {@code TestDataCreator}
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
