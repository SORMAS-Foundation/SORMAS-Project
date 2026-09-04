package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class SampleMaterialGonococcalInfectionTest {

	@Test
	public void genitalSwabIsSelectable() {
		DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.GONOCOCCAL_INFECTION);
		assertTrue(checker.isVisible(SampleMaterial.class, SampleMaterial.GENITAL_SWAB.name()));
	}
}
