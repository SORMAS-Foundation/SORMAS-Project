package de.symeda.sormas.ui.therapy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.ui.AbstractUiBeanTest;

public class DrugSusceptibilityFormTest extends AbstractUiBeanTest {

	private static boolean showsDrugGrid(Disease disease) {
		return new DrugSusceptibilityForm(FieldVisibilityCheckers.withDisease(disease), UiFieldAccessCheckers.getNoop())
			.updateFieldsVisibility(disease, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY);
	}

	@Test
	public void tuberculosisShowsTheDrugGridOutsideLuxembourg() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_GERMANY);
		assertTrue(showsDrugGrid(Disease.TUBERCULOSIS));
	}

	@Test
	public void latentTbHasNoDrugGrid() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_GERMANY);
		assertFalse(showsDrugGrid(Disease.LATENT_TUBERCULOSIS));
	}
}
