
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
 * Verifies that {@link DiseaseFieldVisibilityChecker} exposes exactly the {@link SampleMaterial}
 * values approved for Diphtheria in the sample material selection, and hides all others
 * (deprecated and {@link SampleMaterial#UNKNOWN} materials are excluded from both checks).
 */
public class SampleMaterialDiphtheriaTest {

    private static final List<SampleMaterial> APPROVED = List.of(
        SampleMaterial.OROPHARYNGEAL_SWAB,
        SampleMaterial.NP_SWAB,
        SampleMaterial.SKIN,
        SampleMaterial.TISSUE,
        SampleMaterial.CLINICAL_SAMPLE,
        SampleMaterial.BLOOD);

    private final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.DIPHTHERIA);

    /**
     * Every material in {@link #APPROVED} must be selectable (visible) for Diphtheria and must not be deprecated.
     */
    @Test
    public void everyApprovedSpecimenIsSelectableForCongenitalRubella() {
        for (SampleMaterial material : APPROVED) {
            assertFalse(material.isDeprecated(), material.name() + " is retired and cannot be mapped to Diphtheria");
            assertTrue(checker.isVisible(SampleMaterial.class, material.name()), material.name() + " must be selectable for Diphtheria");
        }
    }

    /**
     * Every non-deprecated, non-{@link SampleMaterial#UNKNOWN} material not in {@link #APPROVED} must not be
     * selectable (hidden) for Diphtheria.
     */
    @Test
    public void noUnapprovedSpecimenIsSelectableForCongenitalRubella() {
        for (SampleMaterial material : SampleMaterial.values()) {
            if (APPROVED.contains(material) || material.isDeprecated() || material == SampleMaterial.UNKNOWN) {
                continue;
            }
            assertFalse(checker.isVisible(SampleMaterial.class, material.name()), material.name() + " leaks into the Diphtheria");
        }
    }
}
