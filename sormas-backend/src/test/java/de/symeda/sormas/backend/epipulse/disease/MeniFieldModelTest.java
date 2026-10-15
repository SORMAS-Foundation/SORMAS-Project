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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;

/**
 * Verifies that MENI writes the file EpiPulse declares for it, with no database: a field model is a
 * list of definitions, and the file's shape is a question about that list alone.
 *
 * <p>
 * The column set is what {@link MeniFieldModel} is most likely to get wrong and what an export
 * test is least likely to notice: a test that reads one value never inspects the header, so a
 * declared column could go missing while every other MENI test still passes. That had happened:
 * {@code IsolateId}, {@code ReportedEMERTII}, and {@code ResultMLST} were all absent from the file
 * with nothing failing.
 */
public class MeniFieldModelTest {

	@Test
	@DisplayName("every variable EpiPulse declares for MENI is a column, and none is written twice")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.MENI);

		// One empty case rather than none.
		List<String> columns =
			EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.MENI).layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.MENI)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns MENI writes");
		assertEquals(declared.size(), columns.size(), "no variable is written twice");
	}

	@Test
	@DisplayName("the two detection-method mappers refuse to answer rather than reporting the wrong specimen")
	public void theDetectionMethodMappersRefuseToAnswer() {

		// Kept as failing stubs, not getters: the pair is split by specimen and SORMAS records
		// nothing that says which specimen is primary and nothing was confirmed about these columns. 
		// Wiring either into a ValueDef has to fail at once rather than fill the column from whatever 
		// the old CTE selected.
		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();

		assertThrows(UnsupportedOperationException.class, () -> EpipulseMapping.mainPathogenDetectionMethods(entry));
		assertThrows(UnsupportedOperationException.class, () -> EpipulseMapping.secondPathogenDetectionMethods(entry));
	}
}
