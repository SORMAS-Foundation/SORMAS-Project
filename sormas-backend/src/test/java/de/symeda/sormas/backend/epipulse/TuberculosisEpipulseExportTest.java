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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.BORN_REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COUNTRY_OF_BIRTH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COUNTRY_OF_NATIONALITY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ENTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DIAGNOSED_ANTE_MORTEM;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.ENROLLED_TO_TREATMENT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HIV_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.ISOLATE_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MAJOR_SITE_OF_TB;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MINOR_SITE_OF_TB;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONALITY_REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME12_MONTHS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME24_MONTHS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME36_MONTHS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME6_MONTHS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PREV_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PREV_DIAGNOSIS_YEAR;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PREV_TREATMENT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PREV_TREATMENT_COMPLETION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_CULTURE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_MICROSCOPY_AFB;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_OTHER_TEST;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_AMK;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_BDQ;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CLF;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_DLM;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_ETH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_ETI;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_INH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_LNZ;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_LVX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_MFX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_PTM;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_PZA;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_RIF;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_STR;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
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
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ComplianceWithTreatment;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.InfectionSite;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Tests tuberculosis against a real database, column by column.
 *
 * <p>
 * Covers TUBE-specific behavior in real SQL output: {@code NOTEST} flooring, family-level result
 * summaries, origin fallback, and narrowed value sets (e.g. no intermediate SIR, no minor
 * pulmonary code).
 */
public class TuberculosisEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	/** Culture confirming the case. */
	private static final Date DIAGNOSTIC_TEST = DateHelper.getDateZero(2024, 5, 11);

	/** Molecular test, one day later. */
	private static final Date MOLECULAR_TEST = DateHelper.getDateZero(2024, 5, 12);

	/** Spoligotyping and susceptibility panel date. */
	private static final Date TYPING_TEST = DateHelper.getDateZero(2024, 5, 14);

	private static final Date LATER_TEST = DateHelper.getDateZero(2024, 5, 17);

	/** Far before reporting period, i.e. prior entry event. */
	private static final Date ENTRY_DATE = DateHelper.getDateZero(2015, 2, 1);

	/** Earlier than DateUsedForStatistics year; therefore previous episode. */
	private static final int PREVIOUS_EPISODE_YEAR = 2019;

	private TestDataCreator.RDCF rdcf;
	private CaseFixture fixture;
	private CountryReferenceDto portugal;
	private CountryReferenceDto luxembourg;

	@BeforeEach
	public void setUpTuberculosisExport() {

		configureLuxembourgFor(EpipulseSubjectCode.TUBE);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto surveillanceSupervisor = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR);
		fixture = fixtureFor(Disease.TUBERCULOSIS, rdcf, surveillanceSupervisor);

		portugal = countryWithNutsCode("Portugal", "PRT", "PT");
		// Reuse configured reporting-country row to avoid duplicate-NUTS false positives.
		luxembourg = getCountryFacade().getCountryByIsoCode(SERVER_COUNTRY_ISO3);
	}

	// ---------------------------------------------------------------------------------------
	// the whole file
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("a fully populated case fills every one of the 50 columns TUBE declares")
	public void aFullyPopulatedCaseFillsEveryColumnTubeDeclares() {

		assignNutsCodes(rdcf, "LU10", "LU101", "LU1010");

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15, p -> {
			p.getAddress().setRegion(rdcf.region);
			p.getAddress().setDistrict(rdcf.district);
			p.getAddress().setCommunity(rdcf.community);
			p.setBirthCountry(portugal);
			p.setCitizenship(luxembourg);
			p.setEntryDate(ENTRY_DATE);
		}).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.getSymptoms().setMajorSite(InfectionSite.LUNG);
			c.getSymptoms().setMinorSite(InfectionSite.INTRATHORACIC_LYMPH_NODES);
			c.getHealthConditions().setHiv(YesNoUnknown.YES);
			c.getHealthConditions().setTuberculosisInfectionYear(PREVIOUS_EPISODE_YEAR);
			c.getHealthConditions().setPreviousTuberculosisTreatment(YesNoUnknown.YES);
			c.getHealthConditions().setComplianceWithTreatment(ComplianceWithTreatment.TREATMENT_FAILED);
		});

		fixture.pathogenTest(caze, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, DIAGNOSTIC_TEST);
		fixture.pathogenTest(caze, PathogenTestType.MICROSCOPY, PathogenTestResultType.NEGATIVE, true, DIAGNOSTIC_TEST);
		nucleicAcidTest(caze, PathogenTestResultType.POSITIVE, MOLECULAR_TEST);
		spoligotyping(caze, PathogenSpecie.MYCOBATERIUM_BOVIS, TYPING_TEST);
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, TYPING_TEST, panel());

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		Map<EpipulseVariable, List<String>> expected = new LinkedHashMap<>();

		// Configuration-derived columns
		expected.put(DISEASE, value("TUBE"));
		expected.put(SUBJECT_CODE, value("TUBE"));
		expected.put(REPORTING_COUNTRY, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(DATA_SOURCE, value(SERVER_DATA_SOURCE));
		expected.put(STATUS, value("NEW/UPDATE"));

		// Case-derived core columns
		expected.put(NATIONAL_RECORD_ID, value(caze.getUuid()));
		expected.put(DATE_OF_NOTIFICATION, value("2024-06-10"));
		// No doctor notification date: first positive test date is used.
		expected.put(DATE_OF_DIAGNOSIS, value("2024-06-11"));
		expected.put(DATE_USED_FOR_STATISTICS, value("2024-06-11"));

		// Person/address columns
		expected.put(AGE, value("33"));
		expected.put(GENDER, value("F"));
		expected.put(PLACE_OF_RESIDENCE, value("LU1010"));
		expected.put(PLACE_OF_NOTIFICATION, value("LU1010"));

		// Born abroad, citizen here: validates birth-first with citizenship fallback.
		expected.put(BORN_REPORTING_COUNTRY, value("false"));
		expected.put(COUNTRY_OF_BIRTH, value("PT"));
		expected.put(NATIONALITY_REPORTING_COUNTRY, value("true"));
		expected.put(COUNTRY_OF_NATIONALITY, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(DATE_OF_ENTRY, value("2015-03-01"));

		// Clinical columns
		expected.put(MAJOR_SITE_OF_TB, value("PULMONARY"));
		expected.put(MINOR_SITE_OF_TB, value("LYMPHINTHOR"));
		// HIV from co-morbidity question, not STI status enum.
		expected.put(HIV_STATUS, value("POS"));
		expected.put(DIAGNOSED_ANTE_MORTEM, value("true"));
		expected.put(PREV_DIAGNOSIS, value("true"));
		expected.put(PREV_DIAGNOSIS_YEAR, value(String.valueOf(PREVIOUS_EPISODE_YEAR)));
		expected.put(PREV_TREATMENT, value("true"));
		expected.put(PREV_TREATMENT_COMPLETION, value("PREVTTFAIL"));

		// Laboratory columns
		expected.put(PATHOGEN, value("MBOVIS"));
		expected.put(RESULT_CULTURE, value("POS"));
		expected.put(RESULT_MICROSCOPY_AFB, value("NEG"));
		expected.put(RESULT_OTHER_TEST, value("NUCLA"));

		// Intermediate ethambutol is intentionally blank for TUBE.
		expected.put(SIR_AMK, value("S"));
		expected.put(SIR_INH, value("R"));
		expected.put(SIR_RIF, value("R"));
		expected.put(SIR_ETH, blank());
		for (EpipulseVariable untested : new EpipulseVariable[] {
			SIR_BDQ,
			SIR_CLF,
			SIR_DLM,
			SIR_ETI,
			SIR_LNZ,
			SIR_LVX,
			SIR_MFX,
			SIR_PTM,
			SIR_PZA,
			SIR_STR }) {
			expected.put(untested, value("NOTEST"));
		}

		// Declared-but-unanswerable columns stay blank.
		expected.put(ENROLLED_TO_TREATMENT, blank());
		expected.put(OUTCOME6_MONTHS, blank());
		expected.put(OUTCOME12_MONTHS, blank());
		expected.put(OUTCOME24_MONTHS, blank());
		expected.put(OUTCOME36_MONTHS, blank());
		expected.put(ISOLATE_ID, blank());

		assertEveryDeclaredColumn(export, entry, expected);
	}

	@Test
	@DisplayName("a case with nothing recorded still writes every column")
	public void aCaseWithNothingRecordedStillWritesEveryDeclaredColumn() {

		fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);
		assertThat(export.all(), hasSize(1));

		for (EpipulseVariable variable : EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.TUBE)) {
			assertThat("TUBE dropped " + variable.getVariableName(), export.header(), hasItem(variable.getVariableName()));
		}
	}

	@Test
	@DisplayName("a case with no laboratory work reports NOTEST rather than blank")
	public void aCaseWithNoLaboratoryWorkReportsNotTested() {

		fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		// No lab work should still export NOTEST for result families and SIR columns.
		assertThat(export.value(entry, RESULT_CULTURE), is("NOTEST"));
		assertThat(export.value(entry, RESULT_MICROSCOPY_AFB), is("NOTEST"));
		assertThat(export.value(entry, RESULT_OTHER_TEST), is("NOTEST"));

		for (EpipulseVariable variable : EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.TUBE)) {
			if (variable.getVariableName().startsWith("SIR_")) {
				assertThat(variable.getVariableName(), export.value(entry, variable), is("NOTEST"));
			}
		}

		// This bool is never blank due to primitive source field.
		assertThat(export.value(entry, DIAGNOSED_ANTE_MORTEM), is("true"));
		// MajorSiteOfTB may still be blank if source is missing.
		assertThat(export.value(entry, MAJOR_SITE_OF_TB), is(""));
	}

	// ---------------------------------------------------------------------------------------
	// culture and microscopy
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("one positive among several tests reports POS, the test that came last")
	public void onePositiveAmongSeveralTestsReportsPositive() {

		// Family-level summary must keep POS despite later NEG.
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		fixture.pathogenTest(caze, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, DIAGNOSTIC_TEST);
		fixture.pathogenTest(caze, PathogenTestType.CULTURE, PathogenTestResultType.NEGATIVE, true, LATER_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), RESULT_CULTURE), is("POS"));
	}

	@Test
	@DisplayName("only negative results report NEG, and an unverified positive is not a result at all")
	public void onlyNegativeResultsReportNegative() {

		CaseDataDto negativeOnly = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(negativeOnly, PathogenTestType.MICROSCOPY, PathogenTestResultType.NEGATIVE, true, DIAGNOSTIC_TEST);

		CaseDataDto unverified = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(unverified, PathogenTestType.MICROSCOPY, PathogenTestResultType.POSITIVE, false, DIAGNOSTIC_TEST);

		CaseDataDto pending = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(pending, PathogenTestType.MICROSCOPY, PathogenTestResultType.PENDING, true, DIAGNOSTIC_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(negativeOnly.getUuid()), RESULT_MICROSCOPY_AFB), is("NEG"));
		// Unverified positive is ignored.
		assertThat(export.value(export.entryFor(unverified.getUuid()), RESULT_MICROSCOPY_AFB), is("NOTEST"));
		// Pending result is also ignored.
		assertThat(export.value(export.entryFor(pending.getUuid()), RESULT_MICROSCOPY_AFB), is("NOTEST"));
	}

	// ---------------------------------------------------------------------------------------
	// Case inclusion
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("confirmed, probable and possible cases are notified, and nothing else is")
	public void onlyNotifiableClassificationsAreExported() {

		String confirmed = classifiedCase(CaseClassification.CONFIRMED);
		String confirmedNoSymptoms = classifiedCase(CaseClassification.CONFIRMED_NO_SYMPTOMS);
		String confirmedUnknownSymptoms = classifiedCase(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS);
		String probable = classifiedCase(CaseClassification.PROBABLE);
		String possible = classifiedCase(CaseClassification.SUSPECT);

		// Neither unclassified nor ruled-out case is notifiable.
		classifiedCase(CaseClassification.NOT_CLASSIFIED);
		classifiedCase(CaseClassification.NO_CASE);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(exportedCases(export), containsInAnyOrder(confirmed, confirmedNoSymptoms, confirmedUnknownSymptoms, probable, possible));
	}

	// ---------------------------------------------------------------------------------------
	// ResultOtherTest
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("the other-test code combines the nucleic-acid and histopathology results")
	public void theOtherTestCodeCombinesTheTwoFamilies() {

		CaseDataDto nucleicAcid = fixture.caze(newPerson(), ONSET);
		nucleicAcidTest(nucleicAcid, PathogenTestResultType.POSITIVE, MOLECULAR_TEST);

		CaseDataDto noNucleicAcid = fixture.caze(newPerson(), ONSET);
		nucleicAcidTest(noNucleicAcid, PathogenTestResultType.NEGATIVE, MOLECULAR_TEST);

		CaseDataDto histology = fixture.caze(newPerson(), ONSET);
		histopathologyTest(histology, PathogenTestResultType.POSITIVE, MOLECULAR_TEST);

		CaseDataDto both = fixture.caze(newPerson(), ONSET);
		nucleicAcidTest(both, PathogenTestResultType.POSITIVE, MOLECULAR_TEST);
		histopathologyTest(both, PathogenTestResultType.POSITIVE, MOLECULAR_TEST);

		CaseDataDto histologyOnly = fixture.caze(newPerson(), ONSET);
		nucleicAcidTest(histologyOnly, PathogenTestResultType.NEGATIVE, MOLECULAR_TEST);
		histopathologyTest(histologyOnly, PathogenTestResultType.POSITIVE, MOLECULAR_TEST);

		CaseDataDto noGranulomata = fixture.caze(newPerson(), ONSET);
		histopathologyTest(noGranulomata, PathogenTestResultType.NEGATIVE, MOLECULAR_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(nucleicAcid.getUuid()), RESULT_OTHER_TEST), is("NUCLA"));
		assertThat(export.value(export.entryFor(noNucleicAcid.getUuid()), RESULT_OTHER_TEST), is("NONUCLA"));
		assertThat(export.value(export.entryFor(histology.getUuid()), RESULT_OTHER_TEST), is("HISTOL"));
		assertThat(export.value(export.entryFor(both.getUuid()), RESULT_OTHER_TEST), is("NUCLAHISTOL"));
		assertThat(export.value(export.entryFor(histologyOnly.getUuid()), RESULT_OTHER_TEST), is("NONUCLAHISTOL"));
		// Histopathology NEG alone maps to NOTEST.
		assertThat(export.value(export.entryFor(noGranulomata.getUuid()), RESULT_OTHER_TEST), is("NOTEST"));
	}

	@Test
	@DisplayName("the other-test code sumarises every test of each family rather than the newest")
	public void theOtherTestCodeSummarisesEveryTest() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		nucleicAcidTest(caze, PathogenTestResultType.POSITIVE, DIAGNOSTIC_TEST);
		nucleicAcidTest(caze, PathogenTestResultType.NEGATIVE, MOLECULAR_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		// Later NEG does not cancel earlier POS.
		assertThat(export.value(export.entryFor(caze.getUuid()), RESULT_OTHER_TEST), is("NUCLA"));
	}

	// ---------------------------------------------------------------------------------------
	// Pathogen
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("the species comes from the newest spoligotyping, so a refined identification gets exported")
	public void theSpeciesComesFromTheNewestSpoligotyping() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		spoligotyping(caze, PathogenSpecie.OTHER_MTBC_MEMBER, TYPING_TEST);
		spoligotyping(caze, PathogenSpecie.MYCOBATERIUM_AFRICANUM, LATER_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), PATHOGEN), is("MAFRICA"));
	}

	@Test
	@DisplayName("a species named by a culture rather than a spoligotyping is not read")
	public void aSpeciesOutsideSpoligotypingIsNotRead() {

		// Species is read from SPOLIGOTYPING only.
		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		fixture.pathogenTest(
			caze,
			PathogenTestType.CULTURE,
			PathogenTestResultType.POSITIVE,
			true,
			DIAGNOSTIC_TEST,
			test -> test.setSpecie(PathogenSpecie.MYCOBATERIUM_TUBERCULOSIS));

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), PATHOGEN), is(""));
	}

	// ---------------------------------------------------------------------------------------
	// origin
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("citizenship stands in for an unrecorded place of birth, and nothing for neither")
	public void citizenshipStandsInForAnUnrecordedPlaceOfBirth() {

		CaseDataDto bornHere = fixture.caze(personFrom(luxembourg, null), ONSET);
		CaseDataDto naturalised = fixture.caze(personFrom(portugal, luxembourg), ONSET);
		CaseDataDto citizenshipOnly = fixture.caze(personFrom(null, portugal), ONSET);
		CaseDataDto nothingKnown = fixture.caze(newPerson(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(bornHere.getUuid()), BORN_REPORTING_COUNTRY), is("true"));
		// Birth country has priority when present.
		assertThat(export.value(export.entryFor(naturalised.getUuid()), BORN_REPORTING_COUNTRY), is("false"));
		assertThat(export.value(export.entryFor(naturalised.getUuid()), NATIONALITY_REPORTING_COUNTRY), is("true"));
		// Without birth country, citizenship is fallback.
		assertThat(export.value(export.entryFor(citizenshipOnly.getUuid()), BORN_REPORTING_COUNTRY), is("false"));
		assertThat(export.value(export.entryFor(nothingKnown.getUuid()), BORN_REPORTING_COUNTRY), is(""));
		assertThat(export.value(export.entryFor(nothingKnown.getUuid()), NATIONALITY_REPORTING_COUNTRY), is(""));
	}

	// ---------------------------------------------------------------------------------------
	// site of disease
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("a lung recorded as the minor site reports nothing, because EpiPulse has no minor PULMONARY")
	public void aLungRecordedAsTheMinorSiteReportsNothing() {

		// Minor lung site is intentionally suppressed (no declared code).
		CaseDataDto caze = fixture.caze(newPerson(), ONSET, c -> {
			c.getSymptoms().setMajorSite(InfectionSite.DISSEMINATED_FORM);
			c.getSymptoms().setMinorSite(InfectionSite.LUNG);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.entryFor(caze.getUuid());

		assertThat(export.value(entry, MAJOR_SITE_OF_TB), is("DISSEM"));
		assertThat(export.value(entry, MINOR_SITE_OF_TB), is(""));
	}

	// ---------------------------------------------------------------------------------------
	// previous episodes
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("a previous diagnosis is one from an earlier year, and this year's is not one")
	public void aPreviousDiagnosisIsOneFromAnEarlierYear() {

		CaseDataDto earlier = fixture.caze(newPerson(), ONSET, c -> c.getHealthConditions().setTuberculosisInfectionYear(PREVIOUS_EPISODE_YEAR));
		// Current-year value is not a previous episode.
		CaseDataDto thisYear = fixture.caze(newPerson(), ONSET, c -> c.getHealthConditions().setTuberculosisInfectionYear(2024));
		// int8 default 0 means unanswered, not a valid year.
		CaseDataDto neverAnswered = fixture.caze(newPerson(), ONSET, c -> c.getHealthConditions().setTuberculosisInfectionYear(0));

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(earlier.getUuid()), PREV_DIAGNOSIS), is("true"));
		assertThat(export.value(export.entryFor(earlier.getUuid()), PREV_DIAGNOSIS_YEAR), is(String.valueOf(PREVIOUS_EPISODE_YEAR)));

		assertThat(export.value(export.entryFor(thisYear.getUuid()), PREV_DIAGNOSIS), is(""));
		assertThat(export.value(export.entryFor(thisYear.getUuid()), PREV_DIAGNOSIS_YEAR), is(""));

		assertThat(export.value(export.entryFor(neverAnswered.getUuid()), PREV_DIAGNOSIS), is(""));
		assertThat(export.value(export.entryFor(neverAnswered.getUuid()), PREV_DIAGNOSIS_YEAR), is(""));
	}

	@Test
	@DisplayName("non-compliance and an uncompleted course report the same code, and unknown reports none")
	public void nonComplianceReportsTheUncompletedCode() {

		CaseDataDto notCompleted = fixture
			.caze(newPerson(), ONSET, c -> c.getHealthConditions().setComplianceWithTreatment(ComplianceWithTreatment.TREATMENT_NOT_COMPLETED));
		CaseDataDto noCompliance =
			fixture.caze(newPerson(), ONSET, c -> c.getHealthConditions().setComplianceWithTreatment(ComplianceWithTreatment.NO_COMPLIANCE));
		CaseDataDto unknown =
			fixture.caze(newPerson(), ONSET, c -> c.getHealthConditions().setComplianceWithTreatment(ComplianceWithTreatment.UNKNOWN));

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(notCompleted.getUuid()), PREV_TREATMENT_COMPLETION), is("PREVTTNOCOMPL"));
		// PREVTTDEF is never exported because no SORMAS value encodes that case.
		assertThat(export.value(export.entryFor(noCompliance.getUuid()), PREV_TREATMENT_COMPLETION), is("PREVTTNOCOMPL"));
		assertThat(export.value(export.entryFor(unknown.getUuid()), PREV_TREATMENT_COMPLETION), is(""));
	}

	// ---------------------------------------------------------------------------------------
	// susceptibility
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("the panel reports S and R, nothing for intermediate, and NOTEST for the drugs it leaves out")
	public void thePanelReportsOnlySusceptibleAndResistant() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);
		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, TYPING_TEST, panel());

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		assertThat(export.value(entry, SIR_AMK), is("S"));
		assertThat(export.value(entry, SIR_INH), is("R"));
		assertThat(export.value(entry, SIR_RIF), is("R"));
		// Intermediate is recorded but intentionally not reportable for TUBE.
		assertThat(export.value(entry, SIR_ETH), is(""));
		// Untested drug remains NOTEST even when a panel exists.
		assertThat(export.value(entry, SIR_PZA), is("NOTEST"));
	}

	@Test
	@DisplayName("the newest panel wins, and an older one does not show through the drugs it left out")
	public void theNewestPanelWins() {

		CaseDataDto caze = fixture.caze(newPerson(), ONSET);

		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, DIAGNOSTIC_TEST, test -> {
			DrugSusceptibilityDto earlier = DrugSusceptibilityDto.build();
			earlier.setIsoniazidSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			earlier.setPyrazinamideSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			test.setDrugSusceptibility(earlier);
		});

		fixture.pathogenTest(caze, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, LATER_TEST, test -> {
			DrugSusceptibilityDto repeat = DrugSusceptibilityDto.build();
			repeat.setIsoniazidSusceptibility(DrugSusceptibilityType.RESISTANT);
			test.setDrugSusceptibility(repeat);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.TUBE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		assertThat("resistance developed since the first panel", export.value(entry, SIR_INH), is("R"));
		// Newest panel defines the row; older panel values are not merged.
		assertThat(export.value(entry, SIR_PZA), is("NOTEST"));
	}

	// ---------------------------------------------------------------------------------------
	// fixture
	// ---------------------------------------------------------------------------------------

	private PersonReferenceDto newPerson() {
		return creator.createPerson().toReference();
	}

	private PersonReferenceDto personFrom(CountryReferenceDto birthCountry, CountryReferenceDto citizenship) {

		return creator.createPerson("Test", "Person", p -> {
			p.setBirthCountry(birthCountry);
			p.setCitizenship(citizenship);
		}).toReference();
	}

	private String classifiedCase(CaseClassification classification) {
		return fixture.caze(newPerson(), ONSET, c -> c.setCaseClassification(classification)).getUuid();
	}

	/** Exported case UUIDs ({@code NationalRecordId}). */
	private static List<String> exportedCases(ExportedEntries export) {
		return export.all().stream().map(EpipulseDiseaseExportEntryDto::getNationalRecordId).collect(Collectors.toList());
	}

	/** Nucleic-acid detection test used for TB in SORMAS. */
	private void nucleicAcidTest(CaseDataDto caze, PathogenTestResultType result, Date testDate) {
		fixture.pathogenTest(caze, PathogenTestType.PCR_RT_PCR, result, true, testDate);
	}

	private void histopathologyTest(CaseDataDto caze, PathogenTestResultType result, Date testDate) {
		fixture.pathogenTest(caze, PathogenTestType.HISTOPATHOLOGY, result, true, testDate);
	}

	/** Test type used for TB species recording in SORMAS. */
	private void spoligotyping(CaseDataDto caze, PathogenSpecie specie, Date testDate) {

		fixture.pathogenTest(caze, PathogenTestType.SPOLIGOTYPING, PathogenTestResultType.POSITIVE, true, testDate, test -> test.setSpecie(specie));
	}

	/**
	 * Susceptibility panel covering four TUBE antibiotics with distinct outcomes, including one
	 * intermediate value.
	 */
	private static Consumer<PathogenTestDto> panel() {

		return test -> {
			DrugSusceptibilityDto panel = DrugSusceptibilityDto.build();
			panel.setAmikacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			panel.setIsoniazidSusceptibility(DrugSusceptibilityType.RESISTANT);
			panel.setRifampicinSusceptibility(DrugSusceptibilityType.RESISTANT);
			panel.setEthambutolSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
			test.setDrugSusceptibility(panel);
		};
	}

	/**
	 * Creates a reportable country row with NUTS code; required for place/origin columns.
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

	private void assertEveryDeclaredColumn(
		ExportedEntries export,
		EpipulseDiseaseExportEntryDto entry,
		Map<EpipulseVariable, List<String>> expected) {

		assertThat(
			"the expectation must name every variable EpiPulse declares for TUBE, and no other",
			expected.keySet(),
			is(EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.TUBE)));

		for (Map.Entry<EpipulseVariable, List<String>> column : expected.entrySet()) {
			assertThat(column.getKey().getVariableName(), export.values(entry, column.getKey()), is(column.getValue()));
		}
	}

	private static List<String> value(String value) {
		return Collections.singletonList(value);
	}

	/** One declared column with an empty value. */
	private static List<String> blank() {
		return Collections.singletonList("");
	}
}
