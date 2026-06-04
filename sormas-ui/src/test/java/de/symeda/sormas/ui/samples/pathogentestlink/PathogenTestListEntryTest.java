/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.samples.pathogentestlink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SmearGrade;
import de.symeda.sormas.api.sample.WesternBlotInterpretation;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Unit tests for {@link PathogenTestListEntry#formatQuantitativeResult(PathogenTestDto)} — the inline
 * quantitative-result label shown in the pathogen test list. Covers every value-type field and the
 * combinations a single method can populate at once.
 */
public class PathogenTestListEntryTest {

	private static PathogenTestDto test() {
		return new PathogenTestDto();
	}

	@Test
	public void noQuantitativeFields_returnsNull() {
		assertNull(PathogenTestListEntry.formatQuantitativeResult(test()));
	}

	@Test
	public void numericValueOnly() {
		PathogenTestDto t = test();
		t.setQuantitativeValue(24.0f);
		assertEquals("24.0", PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void numericValueWithUnit() {
		PathogenTestDto t = test();
		t.setQuantitativeValue(5000.0f);
		t.setQuantitativeUnit("copies/mL");
		assertEquals("5000.0 copies/mL", PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void unitWithoutValueIsIgnored() {
		// The unit is meaningless without a value; only a present numeric value produces output.
		PathogenTestDto t = test();
		t.setQuantitativeUnit("copies/mL");
		assertNull(PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void textOnly() {
		PathogenTestDto t = test();
		t.setQuantitativeText("M. tuberculosis L4 Euro-American");
		assertEquals("M. tuberculosis L4 Euro-American", PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void blankTextIsIgnored() {
		PathogenTestDto t = test();
		t.setQuantitativeText("   ");
		assertNull(PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void booleanOnly() {
		PathogenTestDto t = test();
		t.setQuantitativeBoolean(YesNoUnknown.YES);
		assertEquals(YesNoUnknown.YES.toString(), PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void smearGradeOnly() {
		PathogenTestDto t = test();
		t.setSmearGrade(SmearGrade.THREE_PLUS);
		assertEquals(SmearGrade.THREE_PLUS.toString(), PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void westernBlotInterpretationAndBandText_combinedInOrder() {
		// Western Blot stores both an interpretation and a band-pattern text; both are shown, with the
		// interpretation first.
		PathogenTestDto t = test();
		t.setWesternBlotInterpretation(WesternBlotInterpretation.POSITIVE);
		t.setQuantitativeText("OspC+, VlsE+");
		assertEquals(WesternBlotInterpretation.POSITIVE + ": OspC+, VlsE+", PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void bacterialCulture_textAndCount_combined() {
		// Bacterial Culture = organism text + CFU count.
		PathogenTestDto t = test();
		t.setQuantitativeText("E. coli");
		t.setQuantitativeValue(200000.0f);
		t.setQuantitativeUnit("CFU/mL");
		// Numeric is appended before free text per the fixed field order.
		assertEquals("200000.0 CFU/mL: E. coli", PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void allFieldsPopulated_orderedInterpretationGradeBooleanNumericText() {
		PathogenTestDto t = test();
		t.setWesternBlotInterpretation(WesternBlotInterpretation.NEGATIVE);
		t.setSmearGrade(SmearGrade.ONE_PLUS);
		t.setQuantitativeBoolean(YesNoUnknown.NO);
		t.setQuantitativeValue(12.0f);
		t.setQuantitativeUnit("IU/mL");
		t.setQuantitativeText("free text");
		String expected = String.join(
			": ",
			WesternBlotInterpretation.NEGATIVE.toString(),
			SmearGrade.ONE_PLUS.toString(),
			YesNoUnknown.NO.toString(),
			"12.0 IU/mL",
			"free text");
		assertEquals(expected, PathogenTestListEntry.formatQuantitativeResult(t));
	}
}
