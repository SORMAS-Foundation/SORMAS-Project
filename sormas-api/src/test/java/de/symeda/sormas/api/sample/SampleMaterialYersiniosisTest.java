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
package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class SampleMaterialYersiniosisTest {

	private static final List<SampleMaterial> REQUIRED_MATERIALS =
		List.of(SampleMaterial.BLOOD, SampleMaterial.STOOL, SampleMaterial.PUS, SampleMaterial.URINE, SampleMaterial.CLINICAL_SAMPLE);

	@Test
	public void everyRequiredMaterialIsVisibleForYersiniosis() {
		DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.YERSINIOSIS);
		for (SampleMaterial material : REQUIRED_MATERIALS) {
			assertTrue(checker.isVisible(SampleMaterial.class, material.name()), material.name() + " must be selectable for Yersiniosis");
		}
	}
}
