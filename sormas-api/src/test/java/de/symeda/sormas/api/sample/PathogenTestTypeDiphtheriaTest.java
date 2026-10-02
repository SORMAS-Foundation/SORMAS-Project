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

package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

/**
 * Verifies that {@link DiseaseFieldVisibilityChecker} exposes exactly the {@link PathogenTestType}
 * values approved for Diphtheria in the pathogen test type selection, and hides all others.
 */
public class PathogenTestTypeDiphtheriaTest {

    private static final List<PathogenTestType> APPROVED = List.of(
        PathogenTestType.CULTURE,
        PathogenTestType.PCR_RT_PCR,
        PathogenTestType.Q_PCR,
        PathogenTestType.WHOLE_GENOME_SEQUENCING,
        PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY,
        PathogenTestType.ELEK_TEST);

    private final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.DIPHTHERIA);

    /**
     * Every test type in {@link #APPROVED} must be selectable (visible) for Diphtheria.
     */
    @Test
    public void everyApprovedTestTypeIsSelectableForDiphtheria() {
        for (PathogenTestType testType : APPROVED) {
            assertTrue(checker.isVisible(PathogenTestType.class, testType.name()), testType.name());
        }
    }

    /**
     * Every test type not in {@link #APPROVED} must not be selectable (hidden) for Diphtheria.
     */
    @Test
    public void noUnapprovedTestTypeIsSelectableForDiphtheria() {
        for (PathogenTestType testType : PathogenTestType.values()) {
            if (APPROVED.contains(testType)) {
                continue;
            }
            assertFalse(checker.isVisible(PathogenTestType.class, testType.name()), testType.name());
        }
    }
}
