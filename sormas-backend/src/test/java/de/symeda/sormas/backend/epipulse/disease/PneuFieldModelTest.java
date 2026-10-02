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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.sample.SerotypingMethod;

/**
 * Covers the contract between {@code PneuSql.buildPneuSerotypingMethodCte} and the reader that
 * unflattens its output.
 *
 * <p>
 * No database needed: the separator is a contract between two files that no compiler checks. What
 * the techniques then report as EpiPulse codes is covered by
 * {@code EpipulseMapping.serotypingMethods} in {@code EpipulseMappingTest}, and the export feeding
 * both is covered in {@code PneumococcalEpipulseExportTest}.
 *
 * <p>
 * This class also checks the column set, which likewise needs no database and is checked nowhere
 * else.
 */
public class PneuFieldModelTest {

	@Test
	@DisplayName("the aggregate splits into the techniques it holds")
	public void theAggregateSplitsIntoTechniques() {

		assertEquals(
			Arrays.asList(SerotypingMethod.COAGGLUTINATION, SerotypingMethod.QUELLUNG_REACTION),
			PneuFieldModel.parseSerotypingMethods("COAGGLUTINATION#QUELLUNG_REACTION"));
		assertTrue(PneuFieldModel.parseSerotypingMethods(null).isEmpty());
		assertTrue(PneuFieldModel.parseSerotypingMethods("").isEmpty());
	}

	@Test
	@DisplayName("a technique this version does not know is dropped rather than failing the export")
	public void anUnknownTechniqueIsDropped() {

		// a constant removed from the enum while rows still hold it must not take the whole export
		// down over one laboratory field
		assertEquals(Collections.singletonList(SerotypingMethod.PNEUMOTEST), PneuFieldModel.parseSerotypingMethods("RETIRED_TECHNIQUE#PNEUMOTEST"));
	}

	/**
	 * Verifies that every variable EpiPulse declares for PNEU reaches the file, including the eighteen
	 * that are declared and blank.
	 *
	 * <p>
	 * EpiPulse validates a submission against the exact column set the subject code declares, so a
	 * variable nothing fills must be a blank column, not a missing one. The seventeen per-dose
	 * vaccination columns and {@code Serotype} were previously absent, which is the mistake this pins:
	 * nothing else compares the model against the metadata, and a header is not something an export test
	 * reading one value would notice.
	 *
	 * <p>
	 * Uses one empty case rather than no case, because a repeatable variable's width comes from the
	 * rows; with no rows at all, {@code PathogenDetectionMethod} would be absent for a reason unrelated
	 * to the model.
	 *
	 * <p>
	 * PNEU only, deliberately. Other subject codes also have declared variables they never write;
	 * turning those into failures is a separate decision.
	 */
	@Test
	@DisplayName("every variable EpiPulse declares for PNEU is a column in the file")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.PNEU);

		List<String> columns =
			EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.PNEU).layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.PNEU)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns PNEU writes");
		assertEquals(declared.size(), columns.size(), "no variable is written twice");
	}
}
