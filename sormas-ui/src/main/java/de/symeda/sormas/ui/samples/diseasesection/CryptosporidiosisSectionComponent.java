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
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.GenoTypeResult;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class CryptosporidiosisSectionComponent extends AbstractDiseaseSectionComponent {

	private ComboBox<GenoTypeResult> genoTypeResultField;
	private TextField genoTypeResultTextField;
	private Label genoTypeResultTextFieldSpacer;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {

		genoTypeResultField = createComboBox(PathogenTestDto.GENOTYPE_RESULT);
		genoTypeResultField.setItemCaptionGenerator(GenoTypeResult::toString);
		genoTypeResultField.setVisible(false);
		updateGenoTypeItems(disease);

		genoTypeResultTextField = createTextField(PathogenTestDto.GENOTYPE_RESULT_TEXT);
		genoTypeResultTextField.setVisible(false);

		genoTypeResultTextFieldSpacer = createSpacer();
		addToggleRow(genoTypeResultField, genoTypeResultTextField, genoTypeResultTextFieldSpacer);

		binder.forField(genoTypeResultField).bind(PathogenTestDto::getGenoTypeResult, PathogenTestDto::setGenoTypeResult);
		binder.forField(genoTypeResultTextField).bind(PathogenTestDto::getGenoTypeResultText, PathogenTestDto::setGenoTypeResultText);
	}

	@Override
	protected void wireVisibility() {
		track(genoTypeResultField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == GenoTypeResult.OTHER && genoTypeResultField.isVisible();
			genoTypeResultTextField.setVisible(showText);
			genoTypeResultTextFieldSpacer.setVisible(!showText);
			if (!showText) {
				genoTypeResultTextField.clear();
			}
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();
			updateGenoTypeItems(disease);

			// Auto-set test result
			if (currentTestType == PathogenTestType.GENOTYPING) {
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
		boolean visible = currentTestType == PathogenTestType.GENOTYPING && currentResult == PathogenTestResultType.POSITIVE;
		genoTypeResultField.setVisible(visible);
		if (!visible) {
			genoTypeResultField.clear();
			genoTypeResultTextField.setVisible(false);
			genoTypeResultTextField.clear();
		}
		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setGenoTypeResult(null);
		dto.setGenoTypeResultText(null);
	}

	private void updateGenoTypeItems(Disease disease) {
		List<GenoTypeResult> filtered = Arrays.stream(GenoTypeResult.values()).filter(value -> {
			try {
				java.lang.reflect.Field enumField = GenoTypeResult.class.getField(value.name());
				return FieldVisibilityCheckers.withDisease(disease).isVisible(GenoTypeResult.class, enumField);
			} catch (NoSuchFieldException e) {
				return true;
			}
		}).collect(Collectors.toList());
		GenoTypeResult current = genoTypeResultField.getValue();
		genoTypeResultField.setItems(filtered);
		if (current != null && filtered.contains(current)) {
			genoTypeResultField.setValue(current);
		}
	}

}
