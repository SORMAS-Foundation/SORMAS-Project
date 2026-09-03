/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.caze.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ClassificationCriteriaDescriptionTest {

	@BeforeEach
	public void setUp() {
		I18nProperties.setUserLanguage(Language.EN);
	}

	@Test
	public void permittedPropertyValuesAreRendered() {

		ClassificationPathogenTestCriteriaDto criteria = new ClassificationPathogenTestCriteriaDto(
			PathogenTestDto.GENOTYPE,
			Collections.singletonList(PathogenTestType.GENOTYPING),
			GenoType.GENOTYPE_1B,
			GenoType.GENOTYPE_2C);

		String description = criteria.buildDescription();

		assertTrue(
			description.startsWith(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.GENOTYPE)),
			description);
		assertTrue(description.contains(GenoType.GENOTYPE_1B.toString()), description);
		assertTrue(description.contains(GenoType.GENOTYPE_2C.toString()), description);
		assertTrue(description.contains(PathogenTestType.GENOTYPING.toString()), description);
	}

	@Test
	public void booleanFlagCriteriaRenderWithoutTheirValue() {

		ClassificationPathogenTestCriteriaDto criteria = new ClassificationPathogenTestCriteriaDto(
			PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
			Arrays.asList(PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestType.IGG_SERUM_ANTIBODY),
			true);

		String description = criteria.buildDescription();

		assertTrue(
			description.startsWith(
				I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER)),
			description);
		assertFalse(description.contains("true"), description);
		assertTrue(description.contains(PathogenTestType.IGM_SERUM_ANTIBODY.toString()), description);
		assertTrue(description.contains(PathogenTestType.IGG_SERUM_ANTIBODY.toString()), description);
	}

	@Test
	public void theRubellaArthritisCriterionDoesNotReadAsSepticArthritis() {

		String sharedCaption = new ClassificationSymptomsCriteriaDto(SymptomsDto.ARTHRITIS).buildDescription();
		String rubellaCaption = new ClassificationSymptomsCriteriaDto(SymptomsDto.ARTHRITIS, Disease.RUBELLA.name()).buildDescription();

		assertEquals("Septic arthritis", sharedCaption);
		assertEquals("Arthritis", rubellaCaption);
	}

	@Test
	public void symptomCriteriaRenderWithoutALoneYesNoUnknownValue() {

		ClassificationSymptomsCriteriaDto criteria =
			new ClassificationSymptomsCriteriaDto(SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH, YesNoUnknown.YES);

		String description = criteria.buildDescription();

		assertFalse(description.contains(YesNoUnknown.YES.toString()), description);
		assertTrue(
			description.contains(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH)),
			description);
	}
}
