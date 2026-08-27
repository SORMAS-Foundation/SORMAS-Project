package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class SampleMaterialCongenitalRubellaTest {

	private static final List<SampleMaterial> APPROVED = List.of(
		SampleMaterial.DRY_BLOOD,
		SampleMaterial.EDTA_WHOLE_BLOOD,
		SampleMaterial.NP_SWAB,
		SampleMaterial.CLINICAL_SAMPLE,
		SampleMaterial.SALIVA,
		SampleMaterial.URINE,
		SampleMaterial.OTHER);

	private final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.CONGENITAL_RUBELLA);

	@Test
	public void everyApprovedSpecimenIsSelectableForCongenitalRubella() {
		for (SampleMaterial material : APPROVED) {
			assertTrue(
				checker.isVisible(SampleMaterial.class, material.name()),
				material.name() + " must be selectable for Congenital Rubella Syndrome (#14293 specimen list)");
		}
	}

	@Test
	public void noUnapprovedSpecimenIsSelectableForCongenitalRubella() {
		for (SampleMaterial material : SampleMaterial.values()) {
			if (APPROVED.contains(material) || material.isDeprecated() || material == SampleMaterial.UNKNOWN) {
				continue;
			}
			assertFalse(
				checker.isVisible(SampleMaterial.class, material.name()),
				material.name() + " leaks into the Congenital Rubella Syndrome specimen picker — hide it (#14293)");
		}
	}
}
