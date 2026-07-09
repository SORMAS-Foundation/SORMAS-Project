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
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;

public class ImiSectionComponent extends AbstractDiseaseSectionComponent {

	private static final List<PathogenTestType> IMI_TEST_TYPES = Arrays.asList(
		PathogenTestType.SEROGROUPING,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.WHOLE_GENOME_SEQUENCING);

	private static final List<PathogenTestType> AUTO_POSITIVE_TYPES = Arrays.asList(
		PathogenTestType.SEROGROUPING,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.WHOLE_GENOME_SEQUENCING,
		PathogenTestType.SEQUENCING);

	private ComboBox<SeroGroupSpecification> seroGroupSpecField;
	private TextField seroGroupSpecTextField;
	private Label seroGroupSpecTextSpacer;
	private DrugSusceptibilityForm drugSusceptibilityField;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {

		seroGroupSpecField = createComboBox(PathogenTestDto.SERO_GROUP_SPECIFICATION);
		seroGroupSpecField.setItems(SeroGroupSpecification.values());
		seroGroupSpecField.setItemCaptionGenerator(SeroGroupSpecification::toString);
		seroGroupSpecField.setVisible(false);

		seroGroupSpecTextField = createTextField(PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT);
		seroGroupSpecTextField.setVisible(false);

		seroGroupSpecTextSpacer = createSpacer();
		addToggleRow(seroGroupSpecField, seroGroupSpecTextField, seroGroupSpecTextSpacer);

		binder.forField(seroGroupSpecField).bind(PathogenTestDto::getSeroGroupSpecification, PathogenTestDto::setSeroGroupSpecification);
		binder.forField(seroGroupSpecTextField).bind(PathogenTestDto::getSeroGroupSpecificationText, PathogenTestDto::setSeroGroupSpecificationText);

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
		// seroGroupSpecText visible only when OTHER
		track(seroGroupSpecField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == SeroGroupSpecification.OTHER;
			seroGroupSpecTextField.setVisible(showText);
			seroGroupSpecTextSpacer.setVisible(!showText);
			if (!showText) {
				seroGroupSpecTextField.clear();
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

			if (currentTestType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.NOT_APPLICABLE));
			} else if (currentTestType != null && AUTO_POSITIVE_TYPES.contains(currentTestType)) {
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
		boolean visible = currentResult == PathogenTestResultType.POSITIVE && IMI_TEST_TYPES.contains(currentTestType);
		seroGroupSpecField.setVisible(visible);
		if (!visible) {
			seroGroupSpecField.clear();
			seroGroupSpecTextField.setVisible(false);
			seroGroupSpecTextField.clear();
		}
		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSeroGroupSpecification(null);
		dto.setSeroGroupSpecificationText(null);
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
