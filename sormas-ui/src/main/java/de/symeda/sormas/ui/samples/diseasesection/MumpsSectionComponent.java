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

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class MumpsSectionComponent extends AbstractDiseaseSectionComponent {

	private ComboBox<GenoType> genoTypeField;
	private Label genoTypeTextSpacer;
	private TextField genoTypeTextField;
	private TextField sequenceIdTextField;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {
		genoTypeField = createComboBox(PathogenTestDto.GENOTYPE);
		genoTypeField.setItemCaptionGenerator(GenoType::toString);
		genoTypeField.setVisible(false);

		updateGenoTypeItems();

		genoTypeTextField = createTextField(PathogenTestDto.GENOTYPE_TEXT);
		genoTypeTextField.setVisible(false);
		genoTypeTextSpacer = createSpacer();

		addToggleRow(genoTypeField, genoTypeTextField, genoTypeTextSpacer);

		sequenceIdTextField = createTextField(PathogenTestDto.SEQUENCE_ID);
		sequenceIdTextField.setVisible(false);

		addRow(sequenceIdTextField);

		binder.forField(genoTypeField).bind(PathogenTestDto::getGenoType, PathogenTestDto::setGenoType);
		binder.forField(genoTypeTextField).bind(PathogenTestDto::getGenoTypeText, PathogenTestDto::setGenoTypeText);
		binder.forField(sequenceIdTextField).bind(PathogenTestDto::getSequenceId, PathogenTestDto::setSequenceId);
	}

	@Override
	protected void wireVisibility() {
		// genoTypeResultText visible only when OTHER selected
		track(genoTypeField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == GenoType.OTHER && genoTypeField.isVisible();
			genoTypeTextField.setVisible(showText);
			genoTypeTextSpacer.setVisible(!showText);
			if (!showText) {
				genoTypeTextField.clear();
			}
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();
			updateGenoTypeItems();

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

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setGenoType(null);
		dto.setGenoTypeText(null);
		dto.setSequenceId(null);
	}

	private void updateGenoTypeItems() {
		updateComboBoxByDisease(genoTypeField, GenoType.class, disease);
	}

	private void updateVisibility() {
		boolean visible = currentTestType == PathogenTestType.GENOTYPING && currentResult == PathogenTestResultType.POSITIVE;
		genoTypeField.setVisible(visible);
		sequenceIdTextField.setVisible(visible);
		if (!visible) {
			setVisibleClear(visible, genoTypeField, genoTypeTextField, sequenceIdTextField);
		}
		updateRowAndSelfVisibility();
	}

}
