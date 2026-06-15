/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples.diseasesection;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;

public class ShigellosisSectionComponent extends AbstractDiseaseSectionComponent {

	private static final long serialVersionUID = 1L;

	private DrugSusceptibilityForm drugSusceptibilityField;
	private ComboBox<PathogenSpecie> specie;
	private TextField specieTextField;
	private ComboBox<SerotypingMethod> serotypingMethodCBF;
	private TextField serotypingMethodTF;
	private TextField serotypeTF;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {

		serotypeTF = createTextField(PathogenTestDto.SEROTYPE);
		serotypeTF.setVisible(false);
		addRow(serotypeTF);

		serotypingMethodCBF = createComboBox(PathogenTestDto.SEROTYPING_METHOD);
		serotypingMethodCBF.setItems(SerotypingMethod.values());
		serotypingMethodCBF.setItemCaptionGenerator(SerotypingMethod::toString);
		serotypingMethodCBF.setVisible(false);

		serotypingMethodTF = createTextField(PathogenTestDto.SERO_TYPING_METHOD_TEXT);
		serotypingMethodTF.setVisible(false);
		addRow(serotypingMethodCBF, serotypingMethodTF);

		// Shigellosis species
		specie = createComboBox(PathogenTestDto.SPECIE);
		specie.setItemCaptionGenerator(PathogenSpecie::toString);
		specie.setVisible(false);
		updateComboBoxByDiseaseAndTestType(specie, PathogenSpecie.class, disease, currentTestType);

		specieTextField = createTextField(PathogenTestDto.SPECIE_TEXT);
		specieTextField.setVisible(false);

		addRow(specie, specieTextField);

		binder.forField(specie).bind(PathogenTestDto::getSpecie, PathogenTestDto::setSpecie);
		binder.forField(serotypingMethodCBF).bind(PathogenTestDto::getSeroTypingMethod, PathogenTestDto::setSeroTypingMethod);
		binder.forField(serotypingMethodTF).bind(PathogenTestDto::getSeroTypingMethodText, PathogenTestDto::setSeroTypingMethodText);
		binder.forField(specieTextField).bind(PathogenTestDto::getSpecieText, PathogenTestDto::setSpecieText);
		binder.forField(serotypeTF).bind(PathogenTestDto::getSerotypeText, PathogenTestDto::setSerotypeText);

		// DrugSusceptibilityForm
		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);

		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);

		addDrugSusceptibilityField(drugSusceptibilityField);
	}

	@Override
	protected void wireVisibility() {

		// serotypingMethodText visible only when OTHER
		track(serotypingMethodCBF.addValueChangeListener(e -> {
			boolean showText = e.getValue() == SerotypingMethod.OTHER;
			serotypingMethodTF.setVisible(showText);
			if (!showText) {
				serotypingMethodTF.clear();
			}
		}));

		// Species text visible only when OTHER
		track(specie.addValueChangeListener(e -> {
			boolean showText = e.getValue() == PathogenSpecie.OTHER;
			specieTextField.setVisible(showText);
			if (!showText) {
				specieTextField.clear();
			}
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			// Resetting the current result so that the next time the event is fired,
			eventBus.fire(new SetTestResultEvent(null));
			currentTestType = event.getTestType();
			updateDrugSusceptibility(currentTestType);
			updateComboBoxByDiseaseAndTestType(specie, PathogenSpecie.class, disease, currentTestType);
			updateComboBoxByDiseaseAndTestType(serotypingMethodCBF, SerotypingMethod.class, disease, currentTestType);
			if (currentTestType != null && currentTestType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.NOT_APPLICABLE));
			} else if (currentTestType != null
				&& Arrays.asList(PathogenTestType.SEROGROUPING, PathogenTestType.SEROTYPING).contains(currentTestType)) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.POSITIVE));
			} else {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));

		track(eventBus.on(TestResultChangedEvent.class, event -> {
			currentResult = event.getTestResult();
			if (config.isLuxembourg) {
				updateFieldVisibility();
			}
		}));
	}

	private void updateDrugSusceptibility(PathogenTestType testType) {
		if (drugSusceptibilityField != null) {
			boolean visible = drugSusceptibilityField.updateFieldsVisibility(disease, testType);
			setDrugSusceptibilityRowVisible(visible);
		}
	}

	private void updateFieldVisibility() {
		boolean isPositive = currentResult == PathogenTestResultType.POSITIVE;
		// Serogrouping test: only show specie, hide drug susceptibility and serotyping method
		boolean isSerogrouping = isPositive && currentTestType == PathogenTestType.SEROGROUPING;
		// SEROGroup : species only
		// boolean isCultureTest = isPositive && currentTestType == PathogenTestType.BACTERIAL_CULTURE;
		boolean isSerotype = isPositive && currentTestType == PathogenTestType.SEROTYPING;
		//  SEROtype : test method + species + serotype:
		boolean showDrugSusceptibilityForm = currentTestType != null && currentTestType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY;

		if (!isSerogrouping) {
			setVisibleClear(specie, serotypeTF, serotypingMethodCBF);
		}
		if (!isSerotype) {
			setVisibleClear(specieTextField, specie, serotypingMethodCBF, serotypeTF);
		}
		if (!showDrugSusceptibilityForm) {
			setVisibleClear(serotypingMethodCBF);
		}
		serotypingMethodCBF.setVisible(isSerotype || showDrugSusceptibilityForm);
		specie.setVisible(isSerotype || isSerogrouping);
		serotypeTF.setVisible(isSerotype);
		updateRowAndSelfVisibility();
	}

	@Override
	public void cleanup() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setDrugSusceptibility(null);
		dto.setSeroTypingMethod(null);
		dto.setSeroTypingMethodText(null);
		dto.setSpecie(null);
		dto.setSpecieText(null);
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setDrugSusceptibility(null);
		dto.setSeroTypingMethod(null);
		dto.setSeroTypingMethodText(null);
		dto.setSpecie(null);
		dto.setSpecieText(null);

	}

	@Override
	protected void unbindLegacyFields() {
		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}
}
