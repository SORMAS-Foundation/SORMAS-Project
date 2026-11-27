package de.symeda.sormas.api.caze.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

class ClassificationExposureCriteriaDtoTest {

	@Test
	void pathwayCriterionDescribesPropertyAndValues() {
		ClassificationExposureCriteriaDto criterion = new ClassificationExposureCriteriaDto(
			ExposureDto.EXPOSURE_CATEGORY,
			null,
			ExposureCategory.WATER_BORNE,
			ExposureCategory.FOOD_BORNE);

		assertEquals(
			"Exposure pathway: " + ExposureCategory.WATER_BORNE + ", " + ExposureCategory.FOOD_BORNE,
			criterion.buildDescription());
	}

	@Test
	void typeCriterionDescriptionUnchanged() {
		ClassificationExposureCriteriaDto criterion = new ClassificationExposureCriteriaDto(ExposureType.ANIMAL_CONTACT);

		assertEquals(
			I18nProperties.getString(Strings.classificationCriteriaRestrictedToExposureType) + " " + ExposureType.ANIMAL_CONTACT,
			criterion.buildDescription());
	}
}
