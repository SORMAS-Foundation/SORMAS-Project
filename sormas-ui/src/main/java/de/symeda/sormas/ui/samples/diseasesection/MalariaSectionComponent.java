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

import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class MalariaSectionComponent extends AbstractDiseaseSectionComponent {

	private static final List<PathogenTestType> SPECIE_VISIBLE_TYPES = Arrays.asList(
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.ANTIGEN_DETECTION,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY);

	private static final List<PathogenTestType> AUTO_POSITIVE_TYPES = Arrays.asList(
		PathogenTestType.ANTIGEN_DETECTION,
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.OTHER_SEROLOGICAL_TEST,
		PathogenTestType.OTHER_MOLECULAR_ASSAY);

	private static final List<PathogenTestType> RESULT_DETAILS_VISIBLE_TYPES =
		Arrays.asList(PathogenTestType.THIN_BLOOD_SMEAR, PathogenTestType.Q_PCR);

	private ComboBox<PathogenSpecie> specieField;
	private TextField specieTextField;
	private Label specieTextSpacer;
	private TextField resultDetailsField;

	private PathogenTestType currentTestType;

	@Override
	protected void buildLayout() {
		specieField = createComboBox(PathogenTestDto.SPECIE);
		specieField.setItemCaptionGenerator(PathogenSpecie::toString);
		specieField.setVisible(false);
		updateComboBoxByDiseaseAndTestType(specieField, PathogenSpecie.class, disease, currentTestType);

		specieTextField = createTextField(PathogenTestDto.SPECIE_TEXT);
		specieTextField.setVisible(false);

		specieTextSpacer = createSpacer();
		addToggleRow(specieField, specieTextField, specieTextSpacer);

		resultDetailsField = createTextField(PathogenTestDto.RESULT_DETAILS);
		resultDetailsField.setVisible(false);
		addRow(resultDetailsField, createSpacer());

		binder.forField(specieField).bind(PathogenTestDto::getSpecie, PathogenTestDto::setSpecie);
		binder.forField(specieTextField).bind(PathogenTestDto::getSpecieText, PathogenTestDto::setSpecieText);
		binder.forField(resultDetailsField).bind(PathogenTestDto::getResultDetails, PathogenTestDto::setResultDetails);
	}

	@Override
	protected void wireVisibility() {
		track(specieField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == PathogenSpecie.OTHER && specieField.isVisible();
			specieTextField.setVisible(showText);
			specieTextSpacer.setVisible(!showText);
			if (!showText) {
				specieTextField.clear();
			}
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateComboBoxByDiseaseAndTestType(specieField, PathogenSpecie.class, disease, currentTestType);
			updateVisibility();

			if (currentTestType != null && AUTO_POSITIVE_TYPES.contains(currentTestType)) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.POSITIVE));
			} else if (currentTestType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));
	}

	private void updateVisibility() {
		boolean showSpecie = SPECIE_VISIBLE_TYPES.contains(currentTestType);
		specieField.setVisible(showSpecie);
		if (!showSpecie) {
			specieField.clear();
			specieTextField.setVisible(false);
			specieTextField.clear();
		}

		boolean showResultDetails = RESULT_DETAILS_VISIBLE_TYPES.contains(currentTestType);
		resultDetailsField.setVisible(showResultDetails);
		if (!showResultDetails) {
			resultDetailsField.clear();
		}

		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSpecie(null);
		dto.setSpecieText(null);
		dto.setResultDetails(null);
	}
}
