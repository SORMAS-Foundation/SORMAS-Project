package de.symeda.sormas.ui.samples.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestCategory;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.utils.FormEventBus;

public class TestMethodComponentCountryRuleTest extends AbstractUiBeanTest {

	@SuppressWarnings("unchecked")
	private static <T> List<T> items(ComboBox<T> combo) {
		return new ArrayList<>(((ListDataProvider<T>) combo.getDataProvider()).getItems());
	}

	@SuppressWarnings("unchecked")
	private static ComboBox<PathogenTestCategory> categoryField(HasComponents parent) {
		for (Component child : parent) {
			if ((PathogenTestDto.TEST_TYPE + "_category").equals(child.getId())) {
				return (ComboBox<PathogenTestCategory>) child;
			}
			if (child instanceof HasComponents) {
				ComboBox<PathogenTestCategory> found = categoryField((HasComponents) child);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	@Test
	public void ifaIsHiddenForMalariaOnLuxembourg() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		TestMethodComponent component = new TestMethodComponent(new FormEventBus(), Date::new, Disease.MALARIA);
		assertFalse(items(component.getTestTypeField()).contains(PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY));
	}

	@Test
	public void diseaseChangeKeepsTheCountryRule() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		FormEventBus eventBus = new FormEventBus();
		TestMethodComponent component = new TestMethodComponent(eventBus, Date::new, Disease.DENGUE);
		eventBus.fire(new DiseaseChangedEvent(Disease.MALARIA));
		assertFalse(items(component.getTestTypeField()).contains(PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY));
	}

	@Test
	public void savedMethodHiddenOnLuxembourgStaysListed() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		TestMethodComponent component = new TestMethodComponent(new FormEventBus(), Date::new, Disease.MALARIA);
		PathogenTestDto test = PathogenTestDto.build((SampleReferenceDto) null, null);
		test.setTestType(PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY);
		component.setDto(test);

		assertTrue(items(component.getTestTypeField()).contains(PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY));
		assertEquals(PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY, component.getTestTypeField().getValue());
		assertEquals(PathogenTestCategory.SEROLOGICAL_TESTS, categoryField(component).getValue());
	}
}
