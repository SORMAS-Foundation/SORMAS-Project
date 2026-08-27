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
package de.symeda.sormas.ui.samples.components;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.RadioButtonGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Additional test information fields: performed by reference laboratory and retest requested.
 * Applicable to all diseases.
 */
public class AdditionalTestInfoComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private static final List<Disease> REFERENCE_LAB_DEFAULT_DISEASES =
		Arrays.asList(Disease.MUMPS, Disease.RUBELLA, Disease.CONGENITAL_RUBELLA);

	private RadioButtonGroup<Boolean> performedByReferenceLaboratory;
	private RadioButtonGroup<Boolean> retestRequested;

	private final FormEventBus eventBus;
	private Disease currentDisease;
	private PathogenTestType currentTestType;

	public AdditionalTestInfoComponent(FormEventBus eventBus, Disease disease) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.currentDisease = disease;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		performedByReferenceLaboratory = createBooleanRadioGroup(PathogenTestDto.PERFORMED_BY_REFERENCE_LABORATORY, PathogenTestDto.I18N_PREFIX);
		retestRequested = createBooleanRadioGroup(PathogenTestDto.RETEST_REQUESTED, PathogenTestDto.I18N_PREFIX);
		addRow(performedByReferenceLaboratory, retestRequested);
	}

	private void bindFields() {
		binder.forField(performedByReferenceLaboratory)
			.bind(PathogenTestDto::getPerformedByReferenceLaboratory, PathogenTestDto::setPerformedByReferenceLaboratory);
		binder.forField(retestRequested).bind(PathogenTestDto::getRetestRequested, PathogenTestDto::setRetestRequested);
	}

	private void wireEvents() {
		// Listen for test type changes
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			PathogenTestType testType = event.getTestType();
			currentTestType = testType;
			if (currentTestType != null) {
				updateReferenceLaboratory(testType, currentDisease);
			}
		}));

		track(eventBus.on(DiseaseChangedEvent.class, event -> {
			currentDisease = event.getDisease();
			updateReferenceLaboratory(currentTestType, currentDisease);
		}));
	}

	/**
	 * Updating the reference laboratory value
	 * 
	 * @param testType
	 * @param currentDisease
	 */
	private void updateReferenceLaboratory(PathogenTestType testType, Disease currentDisease) {
		boolean isReferenceLabTargetTest = REFERENCE_LAB_DEFAULT_DISEASES.contains(currentDisease)
			&& Arrays.asList(PathogenTestType.ISOLATION, PathogenTestType.GENOTYPING).contains(testType);
		performedByReferenceLaboratory.setValue(isReferenceLabTargetTest ? true : null);
		if (!isReferenceLabTargetTest) {
			performedByReferenceLaboratory.clear();
		}
	}

	@Override
	public void setDto(PathogenTestDto dto) {
		super.setDto(dto);
		currentTestType = dto != null ? dto.getTestType() : null;
		updateReferenceLaboratory(currentTestType, currentDisease);
	}
}
