package de.symeda.sormas.app.pathogentest.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestType;

public class TestTypeOnDiseaseChangeTest {

	@Test
	public void savedMethodIsKeptWhileTheDiseaseIsUnchanged() {
		assertEquals(
			PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
			TestTypeOnDiseaseChange.methodToKeep(Disease.MALARIA, Disease.MALARIA, PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY));
	}

	@Test
	public void methodIsDroppedWhenTheDiseaseChanges() {
		assertNull(TestTypeOnDiseaseChange.methodToKeep(Disease.TUBERCULOSIS, Disease.LATENT_TUBERCULOSIS, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
	}
}
