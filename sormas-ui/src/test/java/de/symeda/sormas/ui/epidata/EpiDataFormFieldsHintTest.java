package de.symeda.sormas.ui.epidata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.ui.AbstractUiBeanTest;

class EpiDataFormFieldsHintTest extends AbstractUiBeanTest {

	@Override
	public void init() {
		super.init();
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);

		mockCurrentUiWithFullDataAccess();
	}

	@Test
	void hintIsHiddenForGonococcalInfection() {
		assertFalse(form(Disease.GONOCOCCAL_INFECTION).epiDataFieldsHint().isVisible());
	}

	@Test
	void hintIsHiddenForRubella() {
		assertFalse(form(Disease.RUBELLA).epiDataFieldsHint().isVisible());
	}

	@Test
	void hintIsShownWhenHealthcareProfessionalIsAsked() {
		assertTrue(form(Disease.MALARIA).epiDataFieldsHint().isVisible());
	}

	private static EpiDataForm form(Disease disease) {
		return new EpiDataForm(disease, CaseDataDto.class, false, true, null, true, null, Collections.emptyList(), Collections.emptyMap());
	}
}
