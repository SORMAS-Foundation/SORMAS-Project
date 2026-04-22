/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
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

public class IpiSectionComponent extends AbstractDiseaseSectionComponent {

	private static final List<PathogenTestType> SEROTYPE_EXTENDED_TYPES = Arrays.asList(
		PathogenTestType.WHOLE_GENOME_SEQUENCING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SEROGROUPING);

	private static final List<PathogenTestType> AUTO_POSITIVE_TYPES = Arrays.asList(
		PathogenTestType.SEROGROUPING,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.WHOLE_GENOME_SEQUENCING,
		PathogenTestType.SEQUENCING,
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY);

	private TextField serotypeField;
	private ComboBox<SerotypingMethod> serotypingMethodField;
	private TextField serotypingMethodTextField;
	private DrugSusceptibilityForm drugSusceptibilityField;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {
		serotypeField = createTextField(PathogenTestDto.SEROTYPE_TEXT);
		serotypeField.setVisible(false);

		serotypingMethodField = createComboBox(PathogenTestDto.SEROTYPING_METHOD);
		serotypingMethodField.setItems(SerotypingMethod.values());
		serotypingMethodField.setItemCaptionGenerator(SerotypingMethod::toString);
		serotypingMethodField.setVisible(false);

		addRow(serotypeField, serotypingMethodField);

		serotypingMethodTextField = createTextField(PathogenTestDto.SERO_TYPING_METHOD_TEXT);
		serotypingMethodTextField.setVisible(false);
		addRow(serotypingMethodTextField);

		binder.forField(serotypeField).bind(PathogenTestDto::getSerotypeText, PathogenTestDto::setSerotypeText);
		binder.forField(serotypingMethodField).bind(PathogenTestDto::getSeroTypingMethod, PathogenTestDto::setSeroTypingMethod);
		binder.forField(serotypingMethodTextField).bind(PathogenTestDto::getSeroTypingMethodText, PathogenTestDto::setSeroTypingMethodText);

		// DrugSusceptibilityForm — legacy v7, bound via parent FieldGroup
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
		track(serotypingMethodField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == SerotypingMethod.OTHER;
			serotypingMethodTextField.setVisible(showText);
			if (!showText) {
				serotypingMethodTextField.clear();
			}
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();

			// Drug susceptibility visibility
			if (drugSusceptibilityField != null) {
				boolean visible = drugSusceptibilityField.updateFieldsVisibility(disease, currentTestType);
				setDrugSusceptibilityRowVisible(visible);
			}

			if (currentTestType != null && AUTO_POSITIVE_TYPES.contains(currentTestType)) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.POSITIVE));
			} else if (currentTestType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));

		track(eventBus.on(TestResultChangedEvent.class, event -> {
			currentResult = event.getTestResult();
			updateVisibility();
		}));

	}

	private void updateVisibility() {
		boolean isPositive = currentResult == PathogenTestResultType.POSITIVE;

		// Serotype visible on extended test types + positive
		boolean showSerotype = isPositive && SEROTYPE_EXTENDED_TYPES.contains(currentTestType);
		serotypeField.setVisible(showSerotype);
		if (!showSerotype) {
			serotypeField.clear();
		}

		// Serotyping method visible on SEROGROUPING + positive
		boolean showMethod = isPositive && currentTestType == PathogenTestType.SEROGROUPING;
		serotypingMethodField.setVisible(showMethod);
		if (!showMethod) {
			serotypingMethodField.clear();
			serotypingMethodTextField.setVisible(false);
			serotypingMethodTextField.clear();
		}
		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSerotypeText(null);
		dto.setSeroTypingMethod(null);
		dto.setSeroTypingMethodText(null);
		dto.setDrugSusceptibility(null);
	}

	@Override
	protected void unbindLegacyFields() {
		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}
}
