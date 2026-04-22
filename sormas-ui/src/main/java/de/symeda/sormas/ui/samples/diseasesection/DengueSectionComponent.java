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

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class DengueSectionComponent extends AbstractDiseaseSectionComponent {

	private static final List<PathogenTestType> SEROTYPE_VISIBLE_TYPES = Arrays.asList(
		PathogenTestType.NAAT,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.NEUTRALIZING_ANTIBODIES);

	private static final List<PathogenTestType> AUTO_POSITIVE_TYPES = Arrays.asList(
		PathogenTestType.NAAT,
		PathogenTestType.NEUTRALIZING_ANTIBODIES,
		PathogenTestType.PCR_RT_PCR);

	private ComboBox<Serotype> serotypeField;
	private TextField serotypeTextField;
	private Label serotypeTextSpacer;
	private TextField antibodyTitreField;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {
		serotypeField = createComboBox(PathogenTestDto.SEROTYPE);
		serotypeField.setItemCaptionGenerator(Serotype::toString);
		serotypeField.setVisible(false);
		updateComboBoxByDisease(serotypeField, Serotype.class, disease);

		serotypeTextField = createTextField(PathogenTestDto.SEROTYPE_TEXT);
		serotypeTextField.setVisible(false);

		serotypeTextSpacer = createSpacer();
		addToggleRow(serotypeField, serotypeTextField, serotypeTextSpacer);

		antibodyTitreField = createTextField(PathogenTestDto.ANTIBODY_TITRE);
		antibodyTitreField.setVisible(false);
		addRow(antibodyTitreField, createSpacer());

		binder.forField(serotypeField).bind(PathogenTestDto::getSerotype, PathogenTestDto::setSerotype);
		binder.forField(serotypeTextField).bind(PathogenTestDto::getSerotypeText, PathogenTestDto::setSerotypeText);
		binder.forField(antibodyTitreField).bind(PathogenTestDto::getAntibodyTitre, PathogenTestDto::setAntibodyTitre);
	}

	@Override
	protected void wireVisibility() {
		track(serotypeField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == Serotype.OTHER && serotypeField.isVisible();
			serotypeTextField.setVisible(showText);
			serotypeTextSpacer.setVisible(!showText);
			if (!showText) {
				serotypeTextField.clear();
			}
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();

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
		boolean showSerotype = SEROTYPE_VISIBLE_TYPES.contains(currentTestType);
		serotypeField.setVisible(showSerotype);
		if (!showSerotype) {
			serotypeField.clear();
			serotypeTextField.setVisible(false);
			serotypeTextField.clear();
		}

		boolean showAntibodyTitre = currentTestType == PathogenTestType.NEUTRALIZING_ANTIBODIES;
		antibodyTitreField.setVisible(showAntibodyTitre);
		if (!showAntibodyTitre) {
			antibodyTitreField.clear();
		}

		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSerotype(null);
		dto.setSerotypeText(null);
		dto.setAntibodyTitre(null);
	}
}
