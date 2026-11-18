package de.symeda.sormas.ui.epidata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.ui.AbstractUiBeanTest;

class EpiDataFormResidenceAtOnsetTest extends AbstractUiBeanTest {

	@Override
	public void init() {
		super.init();
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		creator.createCountry("France", "FRA", "250");

		mockCurrentUiWithFullDataAccess();
	}

	@ParameterizedTest
	@EnumSource(value = Disease.class, names = {
		"DENGUE",
		"SHIGELLOSIS",
		"RUBELLA" })
	void residenceAtOnsetIsACountryDropdown(Disease disease) {
		Field<?> field = form(disease).getField(EpiDataDto.RESIDENCE_AT_ONSET_COUNTRY);

		assertTrue(field.isVisible());
		assertInstanceOf(ComboBox.class, field);
		assertFalse(((ComboBox) field).getItemIds().isEmpty());
	}

	@Test
	void residenceDetailsAreHiddenWhenEmpty() {
		EpiDataForm form = form(Disease.RUBELLA);
		form.setValue(EpiDataDto.build());

		assertFalse(form.getField(EpiDataDto.RESIDENCE_AT_ONSET).isVisible());
	}

	@Test
	void residenceDetailsAreShownWhenFilled() {
		EpiDataForm form = form(Disease.RUBELLA);
		EpiDataDto epiData = EpiDataDto.build();
		epiData.setResidenceAtOnset("Esch-sur-Alzette");
		form.setValue(epiData);

		assertTrue(form.getField(EpiDataDto.RESIDENCE_AT_ONSET).isVisible());
		assertEquals("Esch-sur-Alzette", form.getField(EpiDataDto.RESIDENCE_AT_ONSET).getValue());
	}

	private static EpiDataForm form(Disease disease) {
		return new EpiDataForm(disease, CaseDataDto.class, false, true, null, true, null, Collections.emptyList(), Collections.emptyMap());
	}
}
