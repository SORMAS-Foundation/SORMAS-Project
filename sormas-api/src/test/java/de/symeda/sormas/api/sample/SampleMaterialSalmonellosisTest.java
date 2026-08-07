package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class SampleMaterialSalmonellosisTest {

	private static final List<SampleMaterial> SALM_19_MATERIALS = List.of(
		SampleMaterial.BLOOD,
		SampleMaterial.CEREBROSPINAL_FLUID,
		SampleMaterial.STOOL,
		SampleMaterial.URINE,
		SampleMaterial.OTHER,
		SampleMaterial.CLINICAL_SAMPLE,
		SampleMaterial.PUS,
		SampleMaterial.UNKNOWN);

	@Test
	public void everySalm19SpecimenIsVisibleForSalmonellosis() {
		DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.SALMONELLOSIS);
		for (SampleMaterial material : SALM_19_MATERIALS) {
			assertTrue(
				checker.isVisible(SampleMaterial.class, material.name()),
				material.name() + " must be selectable for Salmonellosis (SALM-19 specimen list)");
		}
	}
}
