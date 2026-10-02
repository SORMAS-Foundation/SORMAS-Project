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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseClusterSettingRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseMeaslesGenotypeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseRubellaGenotypeRef;
import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Verifies that RUBE writes the file EpiPulse declares for it, with no database: a field model is a
 * list of definitions, and the file's shape is a question about that list alone.
 *
 * <p>
 * The one reference class rubella introduces is checked here rather than in its own test.
 * {@code EpipulseRubellaGenotypeRef} renames thirteen SORMAS constants, so what can go wrong is one
 * left out or pointed at a measles counterpart. Both enums draw from the same {@link GenoType}, which
 * is what makes crossing them possible.
 */
public class RubeFieldModelTest {

	/**
	 * The variables the EpiPulse metadata marks {@code Repeatable = Yes} for RUBE. The flags are
	 * per (subject code, variable) and are not generated into code, so they are named here.
	 */
	private static final List<EpipulseVariable> REPEATABLE = Arrays.asList(
		EpipulseVariable.COMPLICATION_DIAGNOSIS,
		EpipulseVariable.CLUSTER_SETTING,
		EpipulseVariable.PLACE_OF_INFECTION,
		EpipulseVariable.SPECIMEN_VIR_DETECT,
		EpipulseVariable.SPECIMEN_SERO);

	/**
	 * The four of those that are floored, so a group no case filled still writes one column.
	 * {@code ComplicationDiagnosis} is not among them: it reports a {@code NONE} of its own and is
	 * never empty, so a floor would be dead code.
	 */
	private static final List<EpipulseVariable> FLOORED = Arrays.asList(
		EpipulseVariable.CLUSTER_SETTING,
		EpipulseVariable.PLACE_OF_INFECTION,
		EpipulseVariable.SPECIMEN_VIR_DETECT,
		EpipulseVariable.SPECIMEN_SERO);

	private static FieldModel fieldModel() {
		return EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.RUBE);
	}

	@Test
	@DisplayName("RUBE has a model of its own rather than the shared one, and selects rubella cases")
	public void rubeHasAModelOfItsOwn() {

		assertThat(EpipulseFieldModels.hasDedicatedModel(EpipulseSubjectCode.RUBE), is(true));
		assertThat(EpipulseSubjectCode.RUBE.getDisease(), is(Disease.RUBELLA));
	}

	@Test
	@DisplayName("every variable EpiPulse declares for RUBE is a column, and none is written twice")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.RUBE);

		// One empty case rather than none.
		List<String> columns = fieldModel().layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.RUBE)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns RUBE writes");
		assertEquals(declared.size(), columns.size(), "no variable is written twice");
	}

	@Test
	@DisplayName("AgeMonth is among them, which is why the shared block is added whole")
	public void ageMonthIsAmongThem() {

		// The column that decides whether a model takes CommonFields.defs() wholesale or filtered.
		// RUBE declares it, as MEAS and PERT do and as the enteric and STI codes do not, so the
		// filter those models apply would be wrong here.
		assertThat(new EpipulseValues(fieldModel()).emits(EpipulseVariable.AGE_MONTH), is(true));
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
	@DisplayName("the floored groups survive an export in which no case has a value")
	public void theFlooredGroupsSurviveAnEmptyExport() {

		// The realistic export rather than a corner case: PlaceOfInfection needs an imported case
		// with dated travel inside the incubation window, SpecimenVirDetect needs a verified
		// detection test, SpecimenSero a specimen carrying serology, and ClusterSetting a cluster
		// type SORMAS does not offer on the rubella form yet.
		List<String> columns = fieldModel().layout(Collections.singletonList(new EpipulseDiseaseExportEntryDto())).columnNames();

		for (EpipulseVariable variable : FLOORED) {
			assertThat(
				variable.getVariableName() + " is absent from the header of an empty export",
				columns.contains(variable.getVariableName()),
				is(true));
		}
	}

	@Test
	@DisplayName("a case with no complications reports NONE rather than an empty group")
	public void aCaseWithNoComplicationsReportsNone() {

		// Which is why ComplicationDiagnosis is the one repeatable group with no floor: it is never
		// empty, so a floor would be dead code claiming otherwise.
		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		assertThat(EpipulseMapping.complicationDiagnosis(entry), contains("NONE"));

		entry.setComplicationDiagnosis(Collections.singletonList("ARTH"));
		assertThat(EpipulseMapping.complicationDiagnosis(entry), contains("ARTH"));
	}

	// ---------------------------------------------------------------------------------------
	// Genotype
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("each of the thirteen rubella genotypes has a code, and the set is complete both ways")
	public void eachRubellaGenotypeHasACode() {

		assertThat(EpipulseRubellaGenotypeRef.codeFor(GenoType.GENOTYPE_1A), is("RUBEV_1A"));
		assertThat(EpipulseRubellaGenotypeRef.codeFor(GenoType.GENOTYPE_1J), is("RUBEV_1J"));
		assertThat(EpipulseRubellaGenotypeRef.codeFor(GenoType.GENOTYPE_2C), is("RUBEV_2C"));

		assertEquals(13, EpipulseRubellaGenotypeRef.values().length, "the genotypes RUBE names");

		// Unlike the measles set, nothing here is declared without a SORMAS constant behind it -
		// EpipulseMeaslesGenotypeRef.MEASV_B1 is the one that is, and this asserts RUBE has no
		// counterpart to it rather than assuming so.
		for (EpipulseRubellaGenotypeRef ref : EpipulseRubellaGenotypeRef.values()) {
			assertThat(ref.name() + " has no SORMAS constant", ref.getGenoType(), is(org.hamcrest.Matchers.notNullValue()));
		}
	}

	@Test
	@DisplayName("a measles genotype is not a rubella genotype, and neither enum answers for the other")
	public void aMeaslesGenotypeIsNotARubellaGenotype() {

		// The two sets draw from one GenoType enum, which is the whole reason they can be crossed.
		// EpiPulse declares Genotype separately for MEAS, MUMP and RUBE with no value in common, so
		// a case reporting MEASV_D8 under RUBE would be rejected - and would compile.
		assertThat(EpipulseRubellaGenotypeRef.codeFor(GenoType.GENOTYPE_D8), is((String) null));
		assertThat(EpipulseRubellaGenotypeRef.codeFor(GenoType.MUMPV_A), is((String) null));
		assertThat(EpipulseMeaslesGenotypeRef.getByGenoType(GenoType.GENOTYPE_1A), is(org.hamcrest.Matchers.nullValue()));
	}

	@Test
	@DisplayName("a genotype that was never established reports nothing")
	public void anUnestablishedGenotypeReportsNothing() {

		// EpiPulse lists thirteen sequenced genotypes and no value for "not applicable" or "not
		// determined", so these are not a code outside the list - they are the absence of a
		// laboratory finding.
		assertThat(EpipulseRubellaGenotypeRef.codeFor(GenoType.GENOTYPE_NA), is((String) null));
		assertThat(EpipulseRubellaGenotypeRef.codeFor(GenoType.GENOTYPE_UNK), is((String) null));
		assertThat(EpipulseRubellaGenotypeRef.codeFor(GenoType.UNKNOWN), is((String) null));
		assertThat(EpipulseRubellaGenotypeRef.codeFor(null), is((String) null));
	}

	@Test
	@DisplayName("the two columns SORMAS cannot answer are declared and blank, not missing")
	public void theTwoColumnsSormasCannotAnswerAreDeclaredAndBlank() {

		// EpiPulse validates against the declared column set, so these columns must be present but
		// must not carry a value. IgGAvidityTest has no SORMAS field. WeekOfGestation's near neighbour,
		// PersonDto.gestationAgeAtBirth, measures a different pregnancy, and reporting it would assert a
		// gestational age at infection the record does not support. The set assertion above proves the
		// columns are declared; this proves they stay empty when every other value is filled.
		EpipulseValues values = new EpipulseValues(fieldModel());

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setSubjectCode(EpipulseSubjectCode.RUBE);
		entry.setPregnancy(Boolean.TRUE);

		assertThat(values.emits(EpipulseVariable.WEEK_OF_GESTATION), is(true));
		assertThat(values.emits(EpipulseVariable.IG_G_AVIDITY_TEST), is(true));
		assertThat(values.value(entry, EpipulseVariable.WEEK_OF_GESTATION), is(""));
		assertThat(values.value(entry, EpipulseVariable.IG_G_AVIDITY_TEST), is(""));

		// and the column that shares its block with WeekOfGestation is derived, so the blank above
		// is this one column's property and not the whole pregnancy block being unwired
		assertThat(values.value(entry, EpipulseVariable.PREGNANCY), is("true"));
	}

	// ---------------------------------------------------------------------------------------
	// ClusterSetting
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("rubella's eight cluster settings are the eight measles already had")
	public void rubellasClusterSettingsAreTheOnesMeaslesHad() {

		// Asserted rather than assumed: EpipulseClusterSettingRef is shared between the two codes, and
		// only because the EpiPulse metadata lists the identical eight values for both. A diverging value
		// set would need its own class, as ModeOfTransmission did for MALA and DENGUE.
		assertEquals(8, EpipulseClusterSettingRef.values().length, "the cluster settings RUBE names");
		assertThat(EpipulseClusterSettingRef.codeFor(ClusterType.KINDERGARTEN_OR_CHILDCARE), is("CHILDCARE"));
		assertThat(EpipulseClusterSettingRef.codeFor(ClusterType.OTHER), is("OTH"));

		// and a setting that was never established is not OTH, which means a setting outside the list
		assertThat(EpipulseClusterSettingRef.codeFor(ClusterType.UNKNOWN), is((String) null));
		assertThat(EpipulseClusterSettingRef.codeFor(null), is((String) null));
	}
}
