package de.symeda.sormas.ui.samples;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestType;

public class PathogenTestControllerAstRuleTest {

	@Test
	public void tuberculosisKeepsItsDrugResults() {
		assertFalse(PathogenTestController.shouldClearDrugSusceptibility(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, Disease.TUBERCULOSIS));
	}

	@Test
	public void cryptosporidiosisAndLatentTbResultsAreCleared() {
		assertTrue(PathogenTestController.shouldClearDrugSusceptibility(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, Disease.CRYPTOSPORIDIOSIS));
		assertTrue(PathogenTestController.shouldClearDrugSusceptibility(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, Disease.LATENT_TUBERCULOSIS));
	}

	@Test
	public void otherMethodsAreNeverCleared() {
		assertFalse(PathogenTestController.shouldClearDrugSusceptibility(PathogenTestType.PCR_RT_PCR, Disease.CRYPTOSPORIDIOSIS));
	}
}
