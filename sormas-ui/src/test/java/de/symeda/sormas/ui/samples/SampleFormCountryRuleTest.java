package de.symeda.sormas.ui.samples;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.samples.humansample.SampleCreateForm;

public class SampleFormCountryRuleTest extends AbstractUiBeanTest {

	private static Collection<?> materials(SampleCreateForm form) {
		return ((ComboBox) form.getField(SampleDto.SAMPLE_MATERIAL)).getItemIds();
	}

	private static SampleDto sampleWith(SampleMaterial material) {
		SampleDto sample = new SampleDto();
		sample.setSampleMaterial(material);
		return sample;
	}

	@Test
	public void lungTissueIsHiddenForNewInfluenzaOnLuxembourg() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SampleCreateForm luxembourg = new SampleCreateForm(Disease.NEW_INFLUENZA);
		assertFalse(materials(luxembourg).contains(SampleMaterial.LUNG_TISSUE));
		luxembourg.setValue(sampleWith(null));
		assertFalse(materials(luxembourg).contains(SampleMaterial.LUNG_TISSUE));

		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_GERMANY);
		SampleCreateForm germany = new SampleCreateForm(Disease.NEW_INFLUENZA);
		germany.setValue(sampleWith(null));
		assertTrue(materials(germany).contains(SampleMaterial.LUNG_TISSUE));
	}

	@Test
	public void savedMaterialHiddenOnLuxembourgStaysListed() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SampleCreateForm form = new SampleCreateForm(Disease.NEW_INFLUENZA);
		form.setValue(sampleWith(SampleMaterial.LUNG_TISSUE));
		assertTrue(materials(form).contains(SampleMaterial.LUNG_TISSUE));
	}

	@Test
	public void retiredMethodIsNotOfferedAsRequestedTest() {
		SampleCreateForm form = new SampleCreateForm(Disease.CORONAVIRUS);
		Collection<?> requestable = ((OptionGroup) form.getField(SampleDto.REQUESTED_PATHOGEN_TESTS)).getItemIds();
		assertFalse(requestable.contains(PathogenTestType.IMMUNOFLUORESCENCE_ASSAY));
		assertFalse(requestable.contains(PathogenTestType.OTHER));
	}

	@Test
	public void savedRequestedTestRoundTrips() {
		SampleCreateForm form = new SampleCreateForm(Disease.CORONAVIRUS);
		SampleDto sample = sampleWith(null);
		sample.setRequestedPathogenTests(Set.of(PathogenTestType.IMMUNOFLUORESCENCE_ASSAY));
		form.setValue(sample);
		Collection<?> value = (Collection<?>) form.getField(SampleDto.REQUESTED_PATHOGEN_TESTS).getValue();
		assertTrue(value.contains(PathogenTestType.IMMUNOFLUORESCENCE_ASSAY));
	}
}
