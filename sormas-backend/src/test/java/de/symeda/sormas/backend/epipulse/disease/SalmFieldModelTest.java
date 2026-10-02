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
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseModeOfTransmissionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseSuspectedVehicleRef;
import de.symeda.sormas.api.exposure.InfectionSource;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Verifies that SALM writes the file EpiPulse declares for it, with no database: a field model is a
 * list of definitions, and the file's shape is a question about that list alone.
 *
 * <p>
 * The column set is what a dedicated model is most likely to get wrong and what an export test is
 * least likely to notice: a test that reads one value never inspects the header, so a declared column
 * could go missing while every other SALM test still passes. This matters more here than elsewhere
 * because ten of SALM's forty-one columns are blank by construction, and a blank column looks the
 * same whether it was declared or forgotten.
 *
 * <p>
 * The two reference classes are also checked here rather than in their own test. Both rename a
 * SORMAS enum into EpiPulse's spelling, so what can go wrong is a constant left out or matched to the
 * wrong counterpart, which is a question about the same file's contract with the metadata.
 */
public class SalmFieldModelTest {

	/**
	 * The variables the EpiPulse metadata marks {@code Repeatable = Yes} for SALM. The flags are per
	 * (subject code, variable) and are not generated into code, so they are named here.
	 */
	private static final List<EpipulseVariable> REPEATABLE = Arrays.asList(EpipulseVariable.PLACE_OF_INFECTION, EpipulseVariable.ISOLATE_ID);

	private static FieldModel fieldModel() {
		return EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.SALM);
	}

	@Test
	@DisplayName("SALM has a model of its own rather than the shared one, and selects salmonellosis cases")
	public void salmHasAModelOfItsOwn() {

		assertThat(EpipulseFieldModels.hasDedicatedModel(EpipulseSubjectCode.SALM), is(true));
		assertThat(EpipulseSubjectCode.SALM.getDisease(), is(Disease.SALMONELLOSIS));
	}

	@Test
	@DisplayName("every variable EpiPulse declares for SALM is a column, and none is written twice")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.SALM);

		// One empty case rather than none.
		List<String> columns = fieldModel().layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.SALM)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns SALM writes");
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

		// IsolateId is blank by construction and PlaceOfInfection needs an exposure or a recorded
		// country of contamination, so this is the realistic export.
		List<String> columns = fieldModel().layout(Collections.singletonList(new EpipulseDiseaseExportEntryDto())).columnNames();

		for (EpipulseVariable variable : REPEATABLE) {
			assertThat(
				variable.getVariableName() + " is absent from the header of an empty export",
				columns.contains(variable.getVariableName()),
				is(true));
		}
	}

	@Test
	@DisplayName("every EpiPulse vehicle code has a SORMAS constant behind it, except the one that has none")
	public void everyVehicleCodeHasAConstantBehindIt() {

		// InfectionSource was written from this list, so the two are expected to stay in step: a
		// constant added to one belongs in the other. ZOO is the single exception and is stated in
		// EpipulseSuspectedVehicleRef.
		List<String> unmapped = new ArrayList<>();

		for (EpipulseSuspectedVehicleRef ref : EpipulseSuspectedVehicleRef.values()) {
			if (ref.getInfectionSource() == null || !EpipulseSuspectedVehicleRef.codeFor(ref.getInfectionSource()).equals(ref.name())) {
				unmapped.add(ref.name());
			}
		}

		assertThat("vehicle codes that do not round-trip through their SORMAS constant", unmapped, is(empty()));
		assertEquals(36, EpipulseSuspectedVehicleRef.values().length, "the vehicle codes SORMAS can express");
	}

	@Test
	@DisplayName("a vehicle SORMAS cannot express reports nothing rather than OTHERFOOD")
	public void aVehicleSormasCannotExpressReportsNothing() {

		// OTHERFOOD means a food outside the list, which is a claim. UNKNOWN and NOT_APPLICABLE say
		// no vehicle was established, and OTHER may be carrying an animal contact in the free text
		// beside it - none of the three is a food, so none of them is OTHERFOOD.
		assertThat(EpipulseSuspectedVehicleRef.codeFor(InfectionSource.UNKNOWN), is((String) null));
		assertThat(EpipulseSuspectedVehicleRef.codeFor(InfectionSource.NOT_APPLICABLE), is((String) null));
		assertThat(EpipulseSuspectedVehicleRef.codeFor(InfectionSource.OTHER), is((String) null));
		assertThat(EpipulseSuspectedVehicleRef.codeFor(null), is((String) null));
	}

	@Test
	@DisplayName("both SORMAS spellings of mother-to-child and of a laboratory exposure reach one EpiPulse code")
	public void bothSormasSpellingsReachOneCode() {

		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.MOTHER_TO_CHILD), is("MTCT"));
		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.FROM_MOTHER_TO_CHILD), is("MTCT"));
		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.LAB_OCCUPATIONAL_EXPOSURE), is("LAB"));
		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.BY_LAB), is("LAB"));
	}

	@Test
	@DisplayName("a mode of transmission SALM cannot express reports nothing")
	public void aModeOfTransmissionSalmCannotExpressReportsNothing() {

		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.UNKNOWN), is((String) null));
		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_BY_AIR), is((String) null));
		assertThat(EpipulseModeOfTransmissionRef.codeFor(ModeOfTransmission.TRANSFUSION_TRANSPLANT_RECIPIENT), is((String) null));
		assertThat(EpipulseModeOfTransmissionRef.codeFor(null), is((String) null));
	}
}
