/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.samples.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * The tested disease field of a pathogen test lists the same diseases as the case disease field (active primary diseases
 * only), and an existing test still shows its saved disease when that disease is not in this list.
 */
class DiseaseSelectionComponentTest extends AbstractUiBeanTest {

	@Test
	void listsTheSameDiseasesAsTheCaseDiseaseField() {
		configure(Disease.DENGUE, true, false);

		DiseaseSelectionComponent component = humanSampleComponent(false);

		// SormasFieldGroupFieldFactory#populateWithDiseaseData fills the case disease field from this call
		assertEquals(getDiseaseConfigurationFacade().getAllDiseases(true, true, true), items(component.getDiseaseField()));
	}

	@Test
	void activeNonPrimaryDiseaseIsNotListed() {
		configure(Disease.DENGUE, true, false);

		DiseaseSelectionComponent component = humanSampleComponent(false);

		assertFalse(items(component.getDiseaseField()).contains(Disease.DENGUE));
	}

	@Test
	void activePrimaryDiseaseIsListed() {
		configure(Disease.DENGUE, true, true);

		DiseaseSelectionComponent component = humanSampleComponent(false);

		assertTrue(items(component.getDiseaseField()).contains(Disease.DENGUE));
	}

	@Test
	void savedNonPrimaryDiseaseIsShownButNotListed() {
		configure(Disease.DENGUE, true, false);
		DiseaseSelectionComponent component = humanSampleComponent(false);

		component.setDto(pathogenTest(Disease.DENGUE));

		assertEquals(Disease.DENGUE, component.getDiseaseField().getValue());
		assertFalse(items(component.getDiseaseField()).contains(Disease.DENGUE));
	}

	@Test
	void savedInactiveDiseaseIsShownButNotListed() {
		configure(Disease.DENGUE, false, true);
		DiseaseSelectionComponent component = humanSampleComponent(false);

		component.setDto(pathogenTest(Disease.DENGUE));

		assertEquals(Disease.DENGUE, component.getDiseaseField().getValue());
		assertFalse(items(component.getDiseaseField()).contains(Disease.DENGUE));
	}

	@Test
	void clearingTheDiseaseKeepsTheList() {
		DiseaseSelectionComponent component = humanSampleComponent(false);
		List<Disease> before = items(component.getDiseaseField());

		component.getDiseaseField().setValue(null);

		assertNull(component.getDiseaseField().getValue());
		assertEquals(before, items(component.getDiseaseField()));
	}

	@Test
	void newTestPreselectsTheOnlyActivePrimaryDisease() {
		for (Disease disease : getDiseaseConfigurationFacade().getAllDiseases(true, true, true)) {
			if (disease != Disease.DENGUE) {
				configure(disease, false, true);
			}
		}
		configure(Disease.DENGUE, true, true);

		DiseaseSelectionComponent component = humanSampleComponent(true);

		assertEquals(Disease.DENGUE, component.getDiseaseField().getValue());
		assertTrue(items(component.getDiseaseField()).contains(Disease.DENGUE));
	}

	@Test
	void newTestPresetsActivePrimaryDisease() {
		configure(Disease.DENGUE, true, true);
		DiseaseSelectionComponent component = humanSampleComponent(true);

		component.presetDiseaseForNewTest(Disease.DENGUE);

		assertEquals(Disease.DENGUE, component.getDiseaseField().getValue());
	}

	@Test
	void newTestDoesNotPresetNonPrimaryDisease() {
		configure(Disease.DENGUE, true, false);
		DiseaseSelectionComponent component = humanSampleComponent(true);
		component.getDiseaseField().setValue(null);

		component.presetDiseaseForNewTest(Disease.DENGUE);

		assertNull(component.getDiseaseField().getValue());
	}

	@Test
	void newTestDoesNotPresetInactiveDisease() {
		configure(Disease.DENGUE, false, true);
		DiseaseSelectionComponent component = humanSampleComponent(true);
		component.getDiseaseField().setValue(null);

		component.presetDiseaseForNewTest(Disease.DENGUE);

		assertNull(component.getDiseaseField().getValue());
	}

	@Test
	void newTestPresetWithoutDiseaseKeepsTheValue() {
		configure(Disease.DENGUE, true, true);
		DiseaseSelectionComponent component = humanSampleComponent(true);
		component.getDiseaseField().setValue(Disease.DENGUE);

		component.presetDiseaseForNewTest(null);

		assertEquals(Disease.DENGUE, component.getDiseaseField().getValue());
	}

	private DiseaseSelectionComponent humanSampleComponent(boolean create) {
		return new DiseaseSelectionComponent(new FormEventBus(), null, create, false);
	}

	private PathogenTestDto pathogenTest(Disease testedDisease) {
		PathogenTestDto pathogenTest = PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), null);
		pathogenTest.setTestedDisease(testedDisease);
		return pathogenTest;
	}

	private void configure(Disease disease, boolean active, boolean primary) {
		DiseaseConfigurationDto configuration = getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		configuration.setActive(active);
		configuration.setPrimaryDisease(primary);
		configuration.setCaseSurveillanceEnabled(true);
		getDiseaseConfigurationFacade().saveDiseaseConfiguration(configuration);
		// saveDiseaseConfiguration does not refresh the cached disease sets that getAllDiseases reads
		getDiseaseConfigurationFacade().loadData();
	}

	private static List<Disease> items(ComboBox<Disease> field) {
		DataProvider<Disease, ?> dataProvider = field.getDataProvider();
		if (!(dataProvider instanceof ListDataProvider)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(((ListDataProvider<Disease>) dataProvider).getItems());
	}
}
