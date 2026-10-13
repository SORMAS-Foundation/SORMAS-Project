/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.exposure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;

class ExposureDiseaseFilteringTest {

	@Test
	void shouldApplyDiseaseFilteringToSubSettings() {
		List<ExposureSubSetting> salmonellosisSubSettings =
			ExposureSubSetting.getValuesForCategoryOnly(ExposureCategory.FOOD_BORNE, true, Disease.SALMONELLOSIS);
		List<ExposureSubSetting> malariaSubSettings = ExposureSubSetting.getValuesForCategoryOnly(ExposureCategory.FOOD_BORNE, true, Disease.MALARIA);

		assertTrue(salmonellosisSubSettings.contains(ExposureSubSetting.SHOPPING_FOR_FOOD));
		assertFalse(malariaSubSettings.contains(ExposureSubSetting.SHOPPING_FOR_FOOD));
	}

	@Test
	void shouldApplyDiseaseFilteringToProtectiveMeasures() {
		List<ExposureProtectiveMeasure> yersiniosisMeasures =
			ExposureProtectiveMeasure.getValues(ExposureCategory.FOOD_BORNE, ExposureSetting.EATING_OUTSIDE, false, Disease.YERSINIOSIS);
		List<ExposureProtectiveMeasure> malariaMeasures =
			ExposureProtectiveMeasure.getValues(ExposureCategory.FOOD_BORNE, ExposureSetting.EATING_OUTSIDE, false, Disease.MALARIA);

		assertTrue(yersiniosisMeasures.contains(ExposureProtectiveMeasure.EXCLUSION_DIET_YERSINIOSIS));
		assertFalse(malariaMeasures.contains(ExposureProtectiveMeasure.EXCLUSION_DIET_YERSINIOSIS));
	}

	@Test
	void shouldKeepUnannotatedSettingResultsWithDiseaseOverload() {
		List<ExposureSetting> expected = ExposureSetting.getValues(ExposureCategory.RESPIRATORY, false);
		List<ExposureSetting> actual = ExposureSetting.getValues(ExposureCategory.RESPIRATORY, false, Disease.MALARIA);

		assertEquals(expected, actual);
	}

	@Test
	void shouldKeepUnannotatedContactFactorResultsWithDiseaseOverload() {
		List<ExposureContactFactor> expected = ExposureContactFactor.getValues(ExposureCategory.RESPIRATORY, ExposureSetting.WORKPLACE, false);
		List<ExposureContactFactor> actual =
			ExposureContactFactor.getValues(ExposureCategory.RESPIRATORY, ExposureSetting.WORKPLACE, false, Disease.MALARIA);

		assertEquals(expected, actual);
	}

	@Test
	void shouldNotBleedThroughCategoryWideContactFactorsForOtherSetting() {
		List<ExposureContactFactor> factors =
			ExposureContactFactor.getValues(ExposureCategory.BLOOD_PARENTERAL, ExposureSetting.OTHER, false, Disease.MALARIA);

		assertFalse(factors.contains(ExposureContactFactor.INJECTION));
		assertFalse(factors.contains(ExposureContactFactor.NEEDLESTICK));
		assertTrue(factors.contains(ExposureContactFactor.UNKNOWN));
		assertTrue(factors.contains(ExposureContactFactor.OTHER));
	}

	@Test
	void shouldNotBleedThroughCategoryWideProtectiveMeasuresForUnknownSetting() {
		List<ExposureProtectiveMeasure> measures =
			ExposureProtectiveMeasure.getValues(ExposureCategory.BLOOD_PARENTERAL, ExposureSetting.UNKNOWN, false, Disease.MALARIA);

		assertFalse(measures.contains(ExposureProtectiveMeasure.GLOVES));
		assertFalse(measures.contains(ExposureProtectiveMeasure.WEARING_PPE));
		assertTrue(measures.contains(ExposureProtectiveMeasure.UNKNOWN));
		assertTrue(measures.contains(ExposureProtectiveMeasure.OTHER));
	}

	@Test
	void shouldStillReturnCategoryWideKeysForConcreteSetting() {
		List<ExposureContactFactor> factors =
			ExposureContactFactor.getValues(ExposureCategory.BLOOD_PARENTERAL, ExposureSetting.TRANSFUSION, false, Disease.MALARIA);

		assertTrue(factors.contains(ExposureContactFactor.INJECTION));
		assertTrue(factors.contains(ExposureContactFactor.NEEDLESTICK));
	}
}
