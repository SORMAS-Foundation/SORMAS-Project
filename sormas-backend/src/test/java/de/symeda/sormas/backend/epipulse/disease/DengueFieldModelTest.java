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
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDengueModeOfTransmissionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDengueSerotypeRef;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Verifies that DENGUE writes the file EpiPulse declares for it, with no database: a field model is a
 * list of definitions, and the file's shape is a question about that list alone.
 *
 * <p>
 * The two reference classes are checked here rather than in their own tests. Both rename a SORMAS
 * enum into EpiPulse's spelling, so the failure mode is a constant left out or matched to the wrong
 * counterpart. {@code EpipulseDengueModeOfTransmissionRef} is the one that repays more than a
 * round-trip: it folds four SORMAS constants into one code where MALA's set keeps all four apart, and
 * getting that wrong in either direction still compiles and exports.
 *
 * <p>
 * {@code SCONV} is checked here too. It is the only detection method not produced by mapping a test
 * type, so it is the one entry in that column a test of the shared aggregate would never exercise.
 */
public class DengueFieldModelTest {

	/**
	 * The variables the EpiPulse metadata marks {@code Repeatable = Yes} for DENGUE. The flags are
	 * per (subject code, variable) and are not generated into code, so they are named here.
	 */
	private static final List<EpipulseVariable> REPEATABLE =
		Arrays.asList(EpipulseVariable.PATHOGEN_DETECTION_METHOD, EpipulseVariable.PLACE_OF_INFECTION);

	private static FieldModel fieldModel() {
		return EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.DENGUE);
	}

	@Test
	@DisplayName("DENGUE has a model of its own rather than the shared one, and selects dengue cases")
	public void dengueHasAModelOfItsOwn() {

		assertThat(EpipulseFieldModels.hasDedicatedModel(EpipulseSubjectCode.DENGUE), is(true));
		assertThat(EpipulseSubjectCode.DENGUE.getDisease(), is(Disease.DENGUE));
	}

	@Test
	@DisplayName("every variable EpiPulse declares for DENGUE is a column, and none is written twice")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.DENGUE);

		// One empty case rather than none.
		List<String> columns = fieldModel().layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.DENGUE)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns DENGUE writes");
		assertEquals(declared.size(), columns.size(), "no variable is written twice");
	}

	@Test
	@DisplayName("AgeMonth is not among them, which is why the shared block is filtered")
	public void ageMonthIsNotAmongThem() {

		// The one column that would appear if this model took CommonFields.defs() wholesale as PERT
		// and MEAS do. Named on its own because the set assertion above would report it alongside
		// anything else and not say what it means.
		assertThat(new EpipulseValues(fieldModel()).emits(EpipulseVariable.AGE_MONTH), is(false));
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
	@DisplayName("both repeatable groups survive an export in which no case has a value")
	public void bothRepeatableGroupsSurviveAnEmptyExport() {

		// The realistic export rather than a corner case: PlaceOfInfection needs dated travel
		// inside the incubation window and PathogenDetectionMethod needs a verified positive test
		// of a type EpiPulse has a code for. An unfloored group would vanish from the header and
		// the submission would be the wrong shape.
		List<String> columns = fieldModel().layout(Collections.singletonList(new EpipulseDiseaseExportEntryDto())).columnNames();

		for (EpipulseVariable variable : REPEATABLE) {
			assertThat(
				variable.getVariableName() + " is absent from the header of an empty export",
				columns.contains(variable.getVariableName()),
				is(true));
		}
	}

	// ---------------------------------------------------------------------------------------
	// ModeOfTransmission
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("every mosquito constant is one MOSQ, because dengue does not ask where the bite happened")
	public void everyMosquitoConstantIsOneCode() {

		// The four constants were written for malaria, where telling them apart is the whole point
		// of the variable. Dengue draws none of those distinctions, so folding them is correct
		// here and would be a data loss in MALA's set - which is why there are two classes.
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_FROM_ENDEMIC_COUNTRY), is("MOSQ"));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_BY_AIR), is("MOSQ"));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_WITH_STRONG_EPI_EVIDENCE), is("MOSQ"));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_WITHOUT_EVIDENCE), is("MOSQ"));
	}

	@Test
	@DisplayName("all three substance-of-human-origin routes reach SOHO, including the one SALM cannot place")
	public void allThreeSubstanceRoutesReachSoho() {

		// TRANSFUSION_TRANSPLANT_RECIPIENT does not say which of the two happened, which is why
		// EpipulseModeOfTransmissionRef leaves it unmapped - SALM splits TRANSFU from ORGAN. DENGUE
		// has one code covering both, so the same constant is answerable here.
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.TRANSFUSION_TRANSPLANT_RECIPIENT), is("SOHO"));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.TRANSFUSION_RECIPIENT), is("SOHO"));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.ORGAN_RECIPIENT), is("SOHO"));

		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.SEXUAL), is("SEX"));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.OTHER), is("OTH"));
	}

	@Test
	@DisplayName("a route dengue cannot express reports nothing rather than OTH")
	public void aRouteDengueCannotExpressReportsNothing() {

		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.UNKNOWN), is((String) null));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.FROM_MOTHER_TO_CHILD), is((String) null));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(ModeOfTransmission.FOOD_OR_WATER), is((String) null));
		assertThat(EpipulseDengueModeOfTransmissionRef.codeFor(null), is((String) null));
	}

	// ---------------------------------------------------------------------------------------
	// Serotype
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("each of the four dengue serotypes has a code, and an unplaceable one is DENVOTH")
	public void eachDengueSerotypeHasACode() {

		assertThat(EpipulseDengueSerotypeRef.codeFor(Serotype.DENV_1), is("DENV1"));
		assertThat(EpipulseDengueSerotypeRef.codeFor(Serotype.DENV_2), is("DENV2"));
		assertThat(EpipulseDengueSerotypeRef.codeFor(Serotype.DENV_3), is("DENV3"));
		assertThat(EpipulseDengueSerotypeRef.codeFor(Serotype.DENV_4), is("DENV4"));

		// SORMAS's OTHER means "outside the four" and EpiPulse's DENVOTH means "not specified".
		// Dengue has exactly four serotypes, so in practice they are the same statement.
		assertThat(EpipulseDengueSerotypeRef.codeFor(Serotype.OTHER), is("DENVOTH"));
		assertEquals(5, EpipulseDengueSerotypeRef.values().length, "the serotypes DENGUE names");
	}

	@Test
	@DisplayName("a serotype that was never established is not DENVOTH")
	public void anUnestablishedSerotypeIsNotDenvoth() {

		// The same distinction drawn everywhere else in this package: not established is not the
		// same claim as established and outside the list.
		assertThat(EpipulseDengueSerotypeRef.codeFor(Serotype.UNKNOWN), is((String) null));
		// and another disease's serotype is not a dengue serotype
		assertThat(EpipulseDengueSerotypeRef.codeFor(Serotype.YERSINIOSIS_1), is((String) null));
		assertThat(EpipulseDengueSerotypeRef.codeFor(null), is((String) null));
	}

	// ---------------------------------------------------------------------------------------
	// SCONV
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("SCONV is added to the detection methods, and only when a rise was recorded")
	public void sconvIsAddedOnlyWhenARiseWasRecorded() {

		// The one detection method that is not a kind of test, so no mapping of test types can
		// produce it. A case with nothing else reports SCONV alone, which is the point of deriving
		// it separately - it would otherwise leave the column empty for a case that was diagnosed.
		assertThat(EpipulseMapping.dengueDetectionMethods(withRise(Boolean.TRUE)), contains("SCONV"));
		assertThat(EpipulseMapping.dengueDetectionMethods(withRise(Boolean.FALSE)), is(empty()));
		assertThat(EpipulseMapping.dengueDetectionMethods(withRise(null)), is(empty()));
	}

	private static EpipulseDiseaseExportEntryDto withRise(Boolean rise) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setSubjectCode(EpipulseSubjectCode.DENGUE);
		entry.setSeroconversionOrTitreRise(rise);

		return entry;
	}
}
