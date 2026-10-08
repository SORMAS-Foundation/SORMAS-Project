package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class SampleMaterialSalmonellosisTest {

	private static final List<SampleMaterial> MATRIX_MATERIALS = List.of(
		SampleMaterial.ABSCESS_SWAB,
		SampleMaterial.BLOOD,
		SampleMaterial.BONE_MARROW,
		SampleMaterial.CEREBROSPINAL_FLUID,
		SampleMaterial.CLINICAL_SAMPLE,
		SampleMaterial.RECTAL_SWAB,
		SampleMaterial.STOOL,
		SampleMaterial.URINE);

	@Test
	public void everyMatrixSpecimenIsVisibleForSalmonellosis() {
		DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.SALMONELLOSIS);
		for (SampleMaterial material : MATRIX_MATERIALS) {
			assertFalse(material.isDeprecated(), material.name() + " is a retired sample material and must not be a Salmonellosis mapping target");
			assertTrue(
				checker.isVisible(SampleMaterial.class, material.name()),
				material.name() + " must be selectable for Salmonellosis (sample/test matrix)");
		}
	}
}
