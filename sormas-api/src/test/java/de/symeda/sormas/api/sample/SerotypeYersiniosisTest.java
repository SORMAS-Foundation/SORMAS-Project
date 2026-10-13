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

public class SerotypeYersiniosisTest {

	private static final List<Serotype> REQUIRED_SEROTYPES = List.of(
		Serotype.YERSINIOSIS_1,
		Serotype.YERSINIOSIS_2,
		Serotype.YERSINIOSIS_3,
		Serotype.YERSINIOSIS_4,
		Serotype.YERSINIOSIS_5,
		Serotype.YERSINIOSIS_5_27,
		Serotype.YERSINIOSIS_8,
		Serotype.YERSINIOSIS_9,
		Serotype.OTHER,
		Serotype.UNKNOWN);

	@Test
	public void everyRequiredSerotypeIsVisibleForYersiniosis() {
		DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.YERSINIOSIS);
		for (Serotype serotype : REQUIRED_SEROTYPES) {
			assertTrue(checker.isVisible(Serotype.class, serotype.name()), serotype.name() + " must be visible for Yersiniosis");
		}
	}
}
