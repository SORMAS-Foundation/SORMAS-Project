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

public class PathogenSpecieYersiniosisTest {

	private static final List<PathogenSpecie> REQUIRED_SPECIES = List.of(
		PathogenSpecie.YERSINIA_ENTEROCOLITICA,
		PathogenSpecie.YERSINIA_PSEUDOTUBERCULOSIS,
		PathogenSpecie.YERSINIA_SPP,
		PathogenSpecie.OTHER,
		PathogenSpecie.UNKNOWN);

	@Test
	public void everyRequiredSpeciesIsVisibleForYersiniosis() {
		DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.YERSINIOSIS);
		for (PathogenSpecie specie : REQUIRED_SPECIES) {
			assertTrue(checker.isVisible(PathogenSpecie.class, specie.name()), specie.name() + " must be visible for Yersiniosis");
		}
	}
}
