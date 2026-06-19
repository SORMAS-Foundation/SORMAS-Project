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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	public void westernBlotInterpretationOnly() {
		PathogenTestDto t = test();
		t.setWesternBlotInterpretation(WesternBlotInterpretation.POSITIVE);
		assertEquals(WesternBlotInterpretation.POSITIVE.toString(), PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void numericValueWithUnitCount() {
		// Bacterial Culture = CFU count.
		PathogenTestDto t = test();
		t.setQuantitativeValue(200000.0f);
		t.setQuantitativeUnit("CFU/mL");
		assertEquals("200000.0 CFU/mL", PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void allFieldsPopulated_orderedInterpretationGradeBooleanNumeric() {
		PathogenTestDto t = test();
		t.setWesternBlotInterpretation(WesternBlotInterpretation.NEGATIVE);
		t.setSmearGrade(SmearGrade.ONE_PLUS);
		t.setQuantitativeBoolean(YesNoUnknown.NO);
		t.setQuantitativeValue(12.0f);
		t.setQuantitativeUnit("IU/mL");
		String expected = String.join(
			": ",
			WesternBlotInterpretation.NEGATIVE.toString(),
			SmearGrade.ONE_PLUS.toString(),
			YesNoUnknown.NO.toString(),
			"12.0 IU/mL");
		assertEquals(expected, PathogenTestListEntry.formatQuantitativeResult(t));
	}

	@Test
	public void refLabIcon_shownOnlyWhenTrue() {
		assertFalse(PathogenTestListEntry.shouldShowRefLabIcon(test()));
		PathogenTestDto t = test();
		t.setPerformedByReferenceLaboratory(false);
		assertFalse(PathogenTestListEntry.shouldShowRefLabIcon(t));
		t.setPerformedByReferenceLaboratory(true);
		assertTrue(PathogenTestListEntry.shouldShowRefLabIcon(t));
	}

	@Test
	public void retestIcon_shownOnlyWhenTrue() {
		assertFalse(PathogenTestListEntry.shouldShowRetestIcon(test()));
		PathogenTestDto t = test();
		t.setRetestRequested(false);
		assertFalse(PathogenTestListEntry.shouldShowRetestIcon(t));
		t.setRetestRequested(true);
		assertTrue(PathogenTestListEntry.shouldShowRetestIcon(t));
	}

	@Test
	public void resultDetailsIcon_shownOnlyInCompactViewWhenTextPresent() {
		PathogenTestDto t = test();
		t.setTestResultText("Details");
		// Compact view (full text not shown) → icon surfaces the cue.
		assertTrue(PathogenTestListEntry.shouldShowResultDetailsIcon(t, false));
		// Full-text view → icon would be redundant.
		assertFalse(PathogenTestListEntry.shouldShowResultDetailsIcon(t, true));
		// No details → no icon.
		assertFalse(PathogenTestListEntry.shouldShowResultDetailsIcon(test(), false));
	}
}
