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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Verifies that each syphilis reporting model emits exactly the columns EpiPulse defines for its
 * subject code. No database needed: a field model is a list of definitions, and the only question
 * is which variables it contains.
 * 
 */
public class SyphilisFieldModelsTest {

	/**
	 * The variables the EpiPulse metadata marks {@code Repeatable = Yes} for each code. The flags
	 * are per (subject code, variable) and are not generated into code, so they are named here.
	 * CONSYPH has none at all.
	 */
	private static final Map<EpipulseSubjectCode, List<EpipulseVariable>> REPEATABLE = repeatable();

	private static Map<EpipulseSubjectCode, List<EpipulseVariable>> repeatable() {

		Map<EpipulseSubjectCode, List<EpipulseVariable>> byCode = new EnumMap<>(EpipulseSubjectCode.class);
		byCode.put(EpipulseSubjectCode.SYPH, Arrays.asList(EpipulseVariable.PLACE_OF_INFECTION, EpipulseVariable.SITE_OF_INFECTION));
		byCode.put(EpipulseSubjectCode.CONSYPH, Collections.emptyList());

		return byCode;
	}

	static EpipulseSubjectCode[] subjectCodes() {
		return new EpipulseSubjectCode[] {
			EpipulseSubjectCode.SYPH,
			EpipulseSubjectCode.CONSYPH };
	}

	private static EpipulseValues valuesFor(EpipulseSubjectCode subjectCode) {
		return new EpipulseValues(EpipulseFieldModels.forSubjectCode(subjectCode));
	}

	@ParameterizedTest
	@MethodSource("subjectCodes")
	@DisplayName("each syphilis code has a model of its own rather than the shared one")
	public void eachSyphilisCodeHasAModelOfItsOwn(EpipulseSubjectCode subjectCode) {
		assertThat(EpipulseFieldModels.hasDedicatedModel(subjectCode), is(true));
	}

	@ParameterizedTest
	@MethodSource("subjectCodes")
	@DisplayName("the two codes are told apart by the syphilis presentation, not by the disease")
	public void theTwoCodesAreToldApartByTheSyphilisPresentation(EpipulseSubjectCode subjectCode) {

		// Both select Disease.SYPHILIS; only the case subset separates them, and it is applied by
		// the export strategy from the subject code rather than by either field model.
		assertThat(subjectCode.getDisease(), is(Disease.SYPHILIS));
		assertThat(subjectCode.getCaseSubset(), is(notNullValue()));
	}

	@ParameterizedTest
	@MethodSource("subjectCodes")
	@DisplayName("every column emitted is one EpiPulse defines for that code")
	public void everyColumnEmittedIsOneEpipulseDefines(EpipulseSubjectCode subjectCode) {

		EpipulseValues values = valuesFor(subjectCode);
		List<String> overReported = new ArrayList<>();

		for (EpipulseVariable variable : EpipulseVariable.values()) {
			if (values.emits(variable) && !EpipulseSubjectCodeVariables.defines(subjectCode, variable)) {
				overReported.add(variable.getVariableName());
			}
		}

		assertThat("columns emitted that SYPH does not declare", overReported, is(empty()));
	}

	@ParameterizedTest
	@MethodSource("subjectCodes")
	@DisplayName("every column EpiPulse defines is emitted, blank where SORMAS has no answer")
	public void everyColumnEpipulseDefinesIsEmitted(EpipulseSubjectCode subjectCode) {

		EpipulseValues values = valuesFor(subjectCode);
		List<String> missing = new ArrayList<>();

		for (EpipulseVariable variable : EpipulseVariable.values()) {
			if (EpipulseSubjectCodeVariables.defines(subjectCode, variable) && !values.emits(variable)) {
				missing.add(variable.getVariableName());
			}
		}

		assertThat("columns SYPH declares but does not emit", missing, is(empty()));
	}

	@ParameterizedTest
	@MethodSource("subjectCodes")
	@DisplayName("a column group is repeatable exactly where EpiPulse says it is")
	public void aColumnGroupIsRepeatableExactlyWhereEpipulseSaysItIs(EpipulseSubjectCode subjectCode) {

		EpipulseValues values = valuesFor(subjectCode);
		List<String> wrong = new ArrayList<>();

		for (EpipulseVariable variable : EpipulseVariable.values()) {
			if (!values.emits(variable)) {
				continue;
			}
			if (values.isRepeated(variable) != REPEATABLE.get(subjectCode).contains(variable)) {
				wrong.add(variable.getVariableName());
			}
		}

		assertThat("columns whose repeatability disagrees with the metadata", wrong, is(empty()));
	}

	@ParameterizedTest
	@MethodSource("subjectCodes")
	@DisplayName("a case with nothing recorded still writes every declared column")
	public void aCaseWithNothingRecordedStillWritesEveryDeclaredColumn(EpipulseSubjectCode subjectCode) {

		// A repeatable group is as wide as the most any one case needs, so an export whose cases
		// all have nothing would drop the column entirely unless the group is floored. Both of
		// SYPH's are easy to have none of - a site of infection is recorded per case, a place of
		// infection needs an exposure - which makes this the realistic export.
		FieldModel fieldModel = EpipulseFieldModels.forSubjectCode(subjectCode);
		EpipulseValues values = new EpipulseValues(fieldModel);

		List<String> header = fieldModel.layout(Collections.singletonList(new EpipulseDiseaseExportEntryDto())).columnNames();

		List<String> missing = new ArrayList<>();

		for (EpipulseVariable variable : EpipulseVariable.values()) {
			if (values.emits(variable) && !header.contains(variable.getVariableName())) {
				missing.add(variable.getVariableName());
			}
		}

		assertThat("declared columns absent from the header of an empty export", missing, is(empty()));
	}
}
