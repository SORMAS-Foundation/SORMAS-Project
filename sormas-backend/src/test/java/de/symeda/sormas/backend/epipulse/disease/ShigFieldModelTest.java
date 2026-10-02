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
import static org.hamcrest.Matchers.notNullValue;
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
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseMalaPathogenRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseModeOfTransmissionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseShigPathogenRef;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Verifies that SHIG writes the file EpiPulse declares for it, with no database: a field model is a
 * list of definitions, and the file's shape is a question about that list alone.
 *
 * <p>
 * The one reference class shigellosis introduces, {@link EpipulseShigPathogenRef}, is checked
 * here rather than in its own test. It renames five SORMAS constants, so what can go wrong is a
 * constant left out or pointed at a species from another genus. It draws from the same
 * {@link PathogenSpecie} as {@link EpipulseMalaPathogenRef}, which is what makes crossing the two
 * possible.
 */
public class ShigFieldModelTest {

	/**
	 * The variables the EpiPulse metadata marks {@code Repeatable = Yes} for SHIG. The flags are
	 * per (subject code, variable) and are not generated into code, so they are named here.
	 *
	 * <p>
	 * {@code Pathogen} is deliberately absent: EpiPulse marks it repeatable for MALA and not for
	 * SHIG, and the two read the same DTO field.
	 */
	private static final List<EpipulseVariable> REPEATABLE = Arrays.asList(EpipulseVariable.PLACE_OF_INFECTION, EpipulseVariable.ISOLATE_ID);

	/**
	 * Both of them are floored, so a group no case filled still writes one column. SHIG has no
	 * group that reports a {@code NONE} of its own, which is the only reason a model skips a floor.
	 */
	private static final List<EpipulseVariable> FLOORED = REPEATABLE;

	/**
	 * The three columns SHIG declares that SORMAS writes blank, each for a reason
	 * {@code ShigFieldModel} states where it adds it.
	 */
	private static final List<EpipulseVariable> BLANK_SPEC =
		Arrays.asList(EpipulseVariable.SEROTYPE, EpipulseVariable.ISOLATE_ID, EpipulseVariable.ECOFF_AZM);

	private static FieldModel fieldModel() {
		return EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.SHIG);
	}

	@Test
	@DisplayName("SHIG has a model of its own rather than the shared one, and selects shigellosis cases")
	public void shigHasAModelOfItsOwn() {

		assertThat(EpipulseFieldModels.hasDedicatedModel(EpipulseSubjectCode.SHIG), is(true));
		assertThat(EpipulseSubjectCode.SHIG.getDisease(), is(Disease.SHIGELLOSIS));
	}

	@Test
	@DisplayName("every variable EpiPulse declares for SHIG is a column, and none is written twice")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.SHIG);

		// One empty case rather than none.
		List<String> columns = fieldModel().layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.SHIG)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns SHIG writes");
		assertEquals(declared.size(), columns.size(), "no variable is written twice");
	}

	@Test
	@DisplayName("the three columns the shared block would add unasked are filtered out")
	public void theSharedBlockIsAddedFiltered() {

		EpipulseValues values = new EpipulseValues(fieldModel());

		for (EpipulseVariable variable : EnumSet
			.of(EpipulseVariable.AGE_MONTH, EpipulseVariable.DATE_OF_LAST_VACCINATION, EpipulseVariable.VACCINATION_STATUS)) {

			assertThat(
				variable.getVariableName() + " is not declared for SHIG",
				EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.SHIG, variable),
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
	@DisplayName("Pathogen is one column even though MALA reports the same variable as a group")
	public void pathogenIsASingleColumn() {

		assertThat(new EpipulseValues(fieldModel()).isRepeated(EpipulseVariable.PATHOGEN), is(false));

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		assertThat(EpipulseMapping.pathogenCode(entry), is(nullValue()));

		entry.setPathogen(Collections.singletonList("SHISON"));
		assertThat(EpipulseMapping.pathogenCode(entry), is("SHISON"));
	}

	@Test
	@DisplayName("the floored groups survive an export in which no case has a value")
	public void theFlooredGroupsSurviveAnEmptyExport() {

		// The realistic export rather than a corner case: IsolateId is blank by construction, and
		// PlaceOfInfection needs an exposure or a recorded country of contamination
		List<String> columns = fieldModel().layout(Collections.singletonList(new EpipulseDiseaseExportEntryDto())).columnNames();

		for (EpipulseVariable variable : FLOORED) {
			assertThat(
				variable.getVariableName() + " is absent from the header of an empty export",
				columns.contains(variable.getVariableName()),
				is(true));
		}
	}

	@Test
	@DisplayName("the three columns SORMAS cannot answer are declared and blank")
	public void theThreeColumnsSormasCannotAnswerAreDeclaredAndBlank() {

		EpipulseValues values = new EpipulseValues(fieldModel());

		for (EpipulseVariable variable : BLANK_SPEC) {
			assertThat(
				variable.getVariableName() + " is not declared for SHIG",
				EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.SHIG, variable),
				is(true));
			assertThat(variable.getVariableName() + " is missing from the file", values.emits(variable), is(true));
		}
	}

	@Test
	@DisplayName("the susceptibility block has no blank antibiotic, unlike SALM's")
	public void everyAntibioticShigAsksForIsRecorded() {

		// SHIG's five SIR_* columns are exactly the five drugsusceptibility records, which is why
		// it is the one enteric code with nothing blank among them - SALM asks for the same five and
		// seven more and declares those seven blank. If SHIG ever gains one of SALM's seven, this
		// fails and the new column needs a derivation or a stated reason for being blank.
		Set<EpipulseVariable> shigSir = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.SHIG)
			.stream()
			.filter(variable -> variable.getVariableName().startsWith("SIR_"))
			.collect(Collectors.toCollection(() -> EnumSet.noneOf(EpipulseVariable.class)));

		assertEquals(
			EnumSet
				.of(EpipulseVariable.SIR_AMP, EpipulseVariable.SIR_CAZ, EpipulseVariable.SIR_CIP, EpipulseVariable.SIR_CTX, EpipulseVariable.SIR_SXT),
			shigSir,
			"the antibiotics SHIG asks for");
	}

	// ---------------------------------------------------------------------------------------
	// Pathogen
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("each of the five Shigella species has a code, and the set is complete both ways")
	public void eachShigellaSpeciesHasACode() {

		assertThat(EpipulseShigPathogenRef.codeFor(PathogenSpecie.BOYDII), is("SHIBOY"));
		assertThat(EpipulseShigPathogenRef.codeFor(PathogenSpecie.DYSENTERIAE), is("SHIDYS"));
		assertThat(EpipulseShigPathogenRef.codeFor(PathogenSpecie.FLEXNERI), is("SHIFLE"));
		assertThat(EpipulseShigPathogenRef.codeFor(PathogenSpecie.SONNEI), is("SHISON"));
		assertThat(EpipulseShigPathogenRef.codeFor(PathogenSpecie.SHIGELLA_SPP), is("SHISPP"));

		assertEquals(5, EpipulseShigPathogenRef.values().length, "the species SHIG names");

		for (EpipulseShigPathogenRef ref : EpipulseShigPathogenRef.values()) {
			assertThat(ref.name() + " has no SORMAS constant", ref.getSpecie(), is(notNullValue()));
		}
	}

	@Test
	@DisplayName("a Plasmodium species is not a Shigella species, and neither enum answers for the other")
	public void aPlasmodiumSpeciesIsNotAShigellaSpecies() {

		// The two sets draw from one PathogenSpecie enum, which is the whole reason they can be
		// crossed. A case reporting PLASFALCI under SHIG would be rejected - and would compile.
		assertThat(EpipulseShigPathogenRef.codeFor(PathogenSpecie.FALCIPARUM), is(nullValue()));
		assertThat(EpipulseMalaPathogenRef.codeFor(PathogenSpecie.SONNEI), is(nullValue()));
	}

	@Test
	@DisplayName("a species from another genus does not become SHISPP")
	public void aSpeciesFromAnotherGenusDoesNotBecomeShispp() {

		// SHISPP says the Shigella species was not determined, which is a claim about Shigella. A
		// test naming a Salmonella or a Yersinia has not made it, and folding those onto SHISPP
		// would report a determination nobody made.
		assertThat(EpipulseShigPathogenRef.codeFor(PathogenSpecie.YERSINIA_ENTEROCOLITICA), is(nullValue()));
		assertThat(EpipulseShigPathogenRef.codeFor(PathogenSpecie.SPP), is(nullValue()));
		assertThat(EpipulseShigPathogenRef.codeFor(null), is(nullValue()));
	}

	// ---------------------------------------------------------------------------------------
	// ModeOfTransmission
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("SHIG and SALM share one ModeOfTransmission value set")
	public void shigAndSalmShareOneModeOfTransmissionValueSet() {

		assertEquals(12, EpipulseModeOfTransmissionRef.values().length, "the codes the shared set names");

		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.FOOD_OR_WATER), is("FOOD"));
		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.PERSON_TO_PERSON), is("PTP"));
		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.RECREATIONAL_WATER), is("RECRWATER"));
	}
}
