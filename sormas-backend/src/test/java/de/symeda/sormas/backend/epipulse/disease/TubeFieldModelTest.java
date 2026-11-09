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

package de.symeda.sormas.backend.epipulse.disease;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.clinicalcourse.ComplianceWithTreatment;
import de.symeda.sormas.api.clinicalcourse.HivStatus;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStiHivStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubeHivStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubePathogenRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubePrevTreatmentCompletionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubeSiteOfTbRef;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.symptoms.InfectionSite;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Verifies that TUBE writes the file EpiPulse declares for it, with no database.
 *
 * <p>
 * Includes reference-value checks to ensure TUBE-only value sets are respected and no unsupported
 * codes leak into the export.
 */
public class TubeFieldModelTest {

	/** The variables the EpiPulse metadata marks {@code Repeatable = Yes} for TUBE. */
	private static final List<EpipulseVariable> REPEATABLE = Collections.singletonList(EpipulseVariable.ISOLATE_ID);

	/** Floored group keeps at least one column in empty exports. */
	private static final List<EpipulseVariable> FLOORED = REPEATABLE;

	/** Declared TUBE columns intentionally exported as blank. */
	private static final List<EpipulseVariable> BLANK_SPEC = Arrays.asList(
		EpipulseVariable.ENROLLED_TO_TREATMENT,
		EpipulseVariable.OUTCOME6_MONTHS,
		EpipulseVariable.OUTCOME12_MONTHS,
		EpipulseVariable.OUTCOME24_MONTHS,
		EpipulseVariable.OUTCOME36_MONTHS,
		EpipulseVariable.ISOLATE_ID);

	private static FieldModel fieldModel() {
		return EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.TUBE);
	}

	@Test
	@DisplayName("TUBE has a model of its own rather than the shared one, and selects tuberculosis cases")
	public void tubeHasAModelOfItsOwn() {

		assertThat(EpipulseFieldModels.hasDedicatedModel(EpipulseSubjectCode.TUBE), is(true));
		assertThat(EpipulseSubjectCode.TUBE.getDisease(), is(Disease.TUBERCULOSIS));
		// Latent TB is a different SORMAS disease and has no subject code.
		assertThat(EpipulseSubjectCode.TUBE.getCaseSubset(), is(nullValue()));
	}

	@Test
	@DisplayName("every variable EpiPulse declares for TUBE is a column, and none is written twice")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.TUBE);

		List<String> columns = fieldModel().layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.TUBE)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns TUBE writes");
		assertEquals(declared.size(), columns.size(), "no variable is written twice");
	}

	@Test
	@DisplayName("the shared columns TUBE does not declare are filtered out")
	public void theSharedBlockIsAddedFiltered() {

		EpipulseValues values = new EpipulseValues(fieldModel());

		for (EpipulseVariable variable : EnumSet.of(
			EpipulseVariable.AGE_MONTH,
			EpipulseVariable.CASE_CLASSIFICATION,
			EpipulseVariable.OUTCOME,
			EpipulseVariable.DATE_OF_ONSET,
			EpipulseVariable.HOSPITALISATION,
			EpipulseVariable.VACCINATION_STATUS)) {

			assertThat(
				variable.getVariableName() + " is not declared for TUBE",
				EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.TUBE, variable),
				is(false));
			assertThat(variable.getVariableName() + " is emitted anyway", values.emits(variable), is(false));
		}

		assertThat(values.emits(EpipulseVariable.AGE), is(true));
	}

	@Test
	@DisplayName("a column group is repeatable exactly where EpiPulse says it is")
	public void aColumnGroupIsRepeatableExactlyWhereEpipulseSaysItIs() {

		EpipulseValues values = new EpipulseValues(fieldModel());
		List<String> wrong = new ArrayList<>();

		for (EpipulseVariable variable : EpipulseVariable.values()) {
			if (values.emits(variable) && values.isRepeated(variable) != REPEATABLE.contains(variable)) {
				wrong.add(variable.getVariableName());
			}
		}

		assertThat("columns whose repeatability disagrees with the metadata", wrong, is(empty()));
	}

	@Test
	@DisplayName("the floored group survives an export in which no case has a value")
	public void theFlooredGroupSurvivesAnEmptyExport() {

		List<String> columns = fieldModel().layout(Collections.singletonList(new EpipulseDiseaseExportEntryDto())).columnNames();

		for (EpipulseVariable variable : FLOORED) {
			assertThat(
				variable.getVariableName() + " is absent from the header of an empty export",
				columns.contains(variable.getVariableName()),
				is(true));
		}
	}

	@Test
	@DisplayName("the columns SORMAS cannot answer are declared and blank")
	public void theColumnsSormasCannotAnswerAreDeclaredAndBlank() {

		EpipulseValues values = new EpipulseValues(fieldModel());

		for (EpipulseVariable variable : BLANK_SPEC) {
			assertThat(
				variable.getVariableName() + " is not declared for TUBE",
				EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.TUBE, variable),
				is(true));
			assertThat(variable.getVariableName() + " is missing from the file", values.emits(variable), is(true));
			// IsolateId is grouped, so assert per emitted column value.
			for (String written : values.values(new EpipulseDiseaseExportEntryDto(), variable)) {
				assertThat(variable.getVariableName() + " carries a value", written, is(""));
			}
		}
	}

	@Test
	@DisplayName("all fourteen antibiotics TUBE asks for have a column")
	public void everyAntibioticTubeAsksForHasAColumn() {

		EpipulseValues values = new EpipulseValues(fieldModel());

		Set<EpipulseVariable> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.TUBE)
			.stream()
			.filter(variable -> variable.getVariableName().startsWith("SIR_"))
			.collect(Collectors.toCollection(() -> EnumSet.noneOf(EpipulseVariable.class)));

		assertEquals(14, declared.size(), "the antibiotics TUBE asks for");
		for (EpipulseVariable variable : declared) {
			assertThat(variable.getVariableName() + " has no column", values.emits(variable), is(true));
		}
	}

	// ---------------------------------------------------------------------------------------
	// Value sets TUBE does not share with the subject codes it borrows its sources from
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("susceptibility reports no Intermediate, which TUBE does not declare, and NOTEST for an untested drug")
	public void susceptibilityReportsNoIntermediate() {

		assertThat(EpipulseLaboratoryMapper.mapDrugSusceptibilityToTbSIR("SUSCEPTIBLE"), is("S"));
		assertThat(EpipulseLaboratoryMapper.mapDrugSusceptibilityToTbSIR("RESISTANT"), is("R"));
		assertThat(EpipulseLaboratoryMapper.mapDrugSusceptibilityToTbSIR("INTERMEDIATE"), is(nullValue()));
		assertThat(EpipulseLaboratoryMapper.mapDrugSusceptibilityToTbSIR("UNKNOWN"), is(nullValue()));
		assertThat(EpipulseLaboratoryMapper.mapDrugSusceptibilityToTbSIR(null), is("NOTEST"));
		assertThat(EpipulseLaboratoryMapper.mapDrugSusceptibilityToTbSIR(""), is("NOTEST"));

		// Shared mapping includes INTERMEDIATE; TUBE mapping intentionally does not.
		assertThat(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR("INTERMEDIATE"), is("I"));
	}

	@Test
	@DisplayName("the other-test code covers every combination of the two families EpiPulse can express")
	public void otherTestResultCoversEveryCombination() {

		// Histopathology-positive branches.
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("POS", "POS"), is("NUCLAHISTOL"));
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("POS", "NEG"), is("NONUCLAHISTOL"));
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("POS", "NOTEST"), is("HISTOL"));

		// Otherwise nucleic-acid result decides.
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("NEG", "POS"), is("NUCLA"));
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("NOTEST", "POS"), is("NUCLA"));
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("NEG", "NEG"), is("NONUCLA"));
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("NOTEST", "NEG"), is("NONUCLA"));

		// Negative histopathology alone maps to NOTEST.
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("NEG", "NOTEST"), is("NOTEST"));
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult("NOTEST", "NOTEST"), is("NOTEST"));

		// Null input behaves like no test data.
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult(null, null), is("NOTEST"));
		assertThat(EpipulseLaboratoryMapper.mapOtherTestResult(null, "POS"), is("NUCLA"));
	}

	@Test
	@DisplayName("HIV status reads the co-morbidity question and reports only NEG and POS")
	public void hivStatusReportsOnlyNegAndPos() {

		assertThat(EpipulseTubeHivStatusRef.codeFor(YesNoUnknown.NO), is("NEG"));
		assertThat(EpipulseTubeHivStatusRef.codeFor(YesNoUnknown.YES), is("POS"));
		assertThat(EpipulseTubeHivStatusRef.codeFor(YesNoUnknown.UNKNOWN), is(nullValue()));
		assertThat(EpipulseTubeHivStatusRef.codeFor(null), is(nullValue()));

		// STI mapping uses different source/value set and must not be reused for TUBE.
		assertThat(EpipulseStiHivStatusRef.codeFor(HivStatus.KNOWN_POSITIVE), is("POSKNOWN"));
		assertThat(EpipulseStiHivStatusRef.codeFor(HivStatus.NEW_DIAGNOSIS), is("POSNEW"));
	}

	@Test
	@DisplayName("every site of infection tuberculosis records has a code, and the minor site is never pulmonary")
	public void everySiteOfInfectionHasACode() {

		assertThat(EpipulseTubeSiteOfTbRef.codeForMajorSite(InfectionSite.LUNG), is("PULMONARY"));
		assertThat(EpipulseTubeSiteOfTbRef.codeForMinorSite(InfectionSite.LUNG), is(nullValue()));

		assertThat(EpipulseTubeSiteOfTbRef.codeForMajorSite(InfectionSite.VERTEBRAE), is("SPINE"));
		assertThat(EpipulseTubeSiteOfTbRef.codeForMinorSite(InfectionSite.VERTEBRAE), is("SPINE"));

		// Two SORMAS constants collapse to one EpiPulse code.
		assertThat(EpipulseTubeSiteOfTbRef.codeForMajorSite(InfectionSite.GENITO_URINARY), is("UROGEN"));
		assertThat(EpipulseTubeSiteOfTbRef.codeForMajorSite(InfectionSite.UROGENITAL_SYSTEM), is("UROGEN"));

		assertThat(EpipulseTubeSiteOfTbRef.codeForMajorSite(InfectionSite.UNKNOWN), is(nullValue()));
		assertThat(EpipulseTubeSiteOfTbRef.codeForMajorSite(InfectionSite.NOT_APPLICABLE), is(nullValue()));
		assertThat(EpipulseTubeSiteOfTbRef.codeForMajorSite(null), is(nullValue()));

		// Every TB site except UNKNOWN/NOT_APPLICABLE should map.
		List<String> unmapped = InfectionSite.filter(Disease.TUBERCULOSIS, true, false)
			.stream()
			.filter(site -> site != InfectionSite.UNKNOWN && site != InfectionSite.NOT_APPLICABLE)
			.filter(site -> EpipulseTubeSiteOfTbRef.codeForMajorSite(site) == null)
			.map(InfectionSite::name)
			.collect(Collectors.toList());

		assertThat("tuberculosis sites with no EpiPulse code", unmapped, is(empty()));
	}

	@Test
	@DisplayName("the four species SORMAS records have a code; M. caprae has no species to map from")
	public void eachMycobacteriumSpeciesHasACode() {

		assertThat(EpipulseTubePathogenRef.codeFor(PathogenSpecie.MYCOBATERIUM_AFRICANUM), is("MAFRICA"));
		assertThat(EpipulseTubePathogenRef.codeFor(PathogenSpecie.MYCOBATERIUM_BOVIS), is("MBOVIS"));
		assertThat(EpipulseTubePathogenRef.codeFor(PathogenSpecie.MYCOBATERIUM_TUBERCULOSIS), is("MTUBE"));
		assertThat(EpipulseTubePathogenRef.codeFor(PathogenSpecie.OTHER_MTBC_MEMBER), is("MTUBECOMPOTH"));

		// Non-MTBC species remain unmapped.
		assertThat(EpipulseTubePathogenRef.codeFor(PathogenSpecie.SONNEI), is(nullValue()));

		// TUBE exports Pathogen as non-repeatable.
		assertThat(new EpipulseValues(fieldModel()).isRepeated(EpipulseVariable.PATHOGEN), is(false));
	}

	@Test
	@DisplayName("previous treatment completion never reports defaulted, which SORMAS cannot express")
	public void previousTreatmentCompletionNeverReportsDefaulted() {

		assertThat(EpipulseTubePrevTreatmentCompletionRef.codeFor(ComplianceWithTreatment.TREATMENT_COMPLETED), is("PREVTTCOMPL"));
		assertThat(EpipulseTubePrevTreatmentCompletionRef.codeFor(ComplianceWithTreatment.TREATMENT_FAILED), is("PREVTTFAIL"));
		assertThat(EpipulseTubePrevTreatmentCompletionRef.codeFor(ComplianceWithTreatment.TREATMENT_NOT_COMPLETED), is("PREVTTNOCOMPL"));
		assertThat(EpipulseTubePrevTreatmentCompletionRef.codeFor(ComplianceWithTreatment.NO_COMPLIANCE), is("PREVTTNOCOMPL"));
		assertThat(EpipulseTubePrevTreatmentCompletionRef.codeFor(ComplianceWithTreatment.UNKNOWN), is(nullValue()));
		assertThat(EpipulseTubePrevTreatmentCompletionRef.codeFor(ComplianceWithTreatment.NOT_APPLICABLE), is(nullValue()));
	}

	// ---------------------------------------------------------------------------------------
	// Derivations that read more than one field
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("origin falls back from place of birth to citizenship, and stays blank where neither is known")
	public void originFallsBackFromBirthCountryToCitizenship() {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setServerCountryNutsCode("LU");

		assertThat("nothing recorded", EpipulseMapping.bornReportingCountry(entry), is(""));
		assertThat("nothing recorded", EpipulseMapping.nationalityReportingCountry(entry), is(""));

		entry.setCitizenshipCountryNutsCode("LU");
		assertThat("citizenship stands in for an unknown place of birth", EpipulseMapping.bornReportingCountry(entry), is("true"));
		assertThat(EpipulseMapping.nationalityReportingCountry(entry), is("true"));

		entry.setBirthCountryNutsCode("PT");
		assertThat("place of birth decides where it is known", EpipulseMapping.bornReportingCountry(entry), is("false"));
		assertThat("citizenship is read on its own", EpipulseMapping.nationalityReportingCountry(entry), is("true"));
	}

	@Test
	@DisplayName("a previous diagnosis is only one that precedes the reporting year")
	public void aPreviousDiagnosisPrecedesTheReportingYear() {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setReportDate(java.sql.Date.valueOf("2026-03-01"));

		assertThat("no year recorded", EpipulseMapping.prevDiagnosis(entry), is(""));
		assertThat("no year recorded", EpipulseMapping.prevDiagnosisYear(entry), is(""));

		entry.setTuberculosisInfectionYear(2020);
		assertThat(EpipulseMapping.prevDiagnosis(entry), is("true"));
		assertThat(EpipulseMapping.prevDiagnosisYear(entry), is("2020"));

		// The year of this episode is not a previous diagnosis, and EpiPulse rejects a later one.
		entry.setTuberculosisInfectionYear(2026);
		assertThat(EpipulseMapping.prevDiagnosis(entry), is(""));
		assertThat(EpipulseMapping.prevDiagnosisYear(entry), is(""));

		// The column is int8 DEFAULT 0, so zero is an unanswered question rather than a year.
		entry.setTuberculosisInfectionYear(0);
		assertThat(EpipulseMapping.prevDiagnosis(entry), is(""));
		assertThat(EpipulseMapping.prevDiagnosisYear(entry), is(""));
	}

	@Test
	@DisplayName("an ante-mortem diagnosis is the absence of a post-mortem one, so the column is never blank")
	public void anteMortemIsTheNegationOfPostMortem() {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();

		assertThat("a case nobody answered the question for", EpipulseMapping.diagnosedAnteMortem(entry), is(""));

		entry.setDiagnosedAnteMortem(true);
		assertThat(EpipulseMapping.diagnosedAnteMortem(entry), is("true"));

		entry.setDiagnosedAnteMortem(false);
		assertThat(EpipulseMapping.diagnosedAnteMortem(entry), is("false"));
	}
}
