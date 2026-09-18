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

public class PathogenTestTypeYersiniosisTest {

	private static final List<PathogenTestType> REQUIRED_METHODS = List.of(
		PathogenTestType.CULTURE,
		PathogenTestType.ISOLATION,
		PathogenTestType.NAAT,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.IGM_SERUM_ANTIBODY,
		PathogenTestType.IGG_SERUM_ANTIBODY);

	@Test
	public void everyRequiredMethodIsVisibleAndSelectableForYersiniosis() {
		DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.YERSINIOSIS);
		for (PathogenTestType type : REQUIRED_METHODS) {
			assertTrue(checker.isVisible(PathogenTestType.class, type.name()), type.name() + " must be visible for Yersiniosis");
			assertTrue(PathogenTestType.isSelectableForNewTests(type), type.name() + " must be selectable for new tests");
		}
	}
}
