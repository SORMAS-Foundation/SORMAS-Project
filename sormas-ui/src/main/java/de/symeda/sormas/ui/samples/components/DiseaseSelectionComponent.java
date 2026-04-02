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

import java.util.List;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Disease/pathogen selection.
 * Vaadin 8 components with own Binder, self-managed visibility.
 * Disease variant fields are in {@link DiseaseVariantComponent}.
 */
public class DiseaseSelectionComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;
	private final Disease initialDisease;
	private final boolean create;
	private final boolean isEnvironmentSample;

	private ComboBox<Disease> diseaseField;
	private TextField diseaseDetailsField;
	private Label diseaseDetailsSpacer;
	private ComboBox<Pathogen> testedPathogenField;
	private TextField testedPathogenDetailsField;
	private Label pathogenDetailsSpacer;

	public DiseaseSelectionComponent(FormEventBus eventBus, Disease initialDisease, boolean create, boolean isEnvironmentSample) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.initialDisease = initialDisease;
		this.create = create;
		this.isEnvironmentSample = isEnvironmentSample;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		// Disease
		diseaseField = createComboBox(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.I18N_PREFIX);
		diseaseField.setItemCaptionGenerator(Disease::toString);

		List<Disease> activeDiseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		List<Disease> nonPrimary = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, false, true);
		activeDiseases.addAll(nonPrimary);
		diseaseField.setItems(activeDiseases);

		diseaseDetailsField = createTextField(PathogenTestDto.TESTED_DISEASE_DETAILS, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		diseaseDetailsField.setVisible(false);

		diseaseDetailsSpacer = createSpacer();
		addToggleRow(diseaseField, diseaseDetailsField, diseaseDetailsSpacer);

		// Pathogen
		testedPathogenField = createComboBox(PathogenTestDto.TESTED_PATHOGEN, PathogenTestDto.I18N_PREFIX);
		testedPathogenField.setItemCaptionGenerator(Pathogen::getCaption);
		testedPathogenField.setItems(FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.PATHOGEN, initialDisease));

		testedPathogenDetailsField = createTextField(PathogenTestDto.TESTED_PATHOGEN_DETAILS, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		testedPathogenDetailsField.setVisible(false);

		pathogenDetailsSpacer = createSpacer();
		addToggleRow(testedPathogenField, testedPathogenDetailsField, pathogenDetailsSpacer);

		// Environment vs human sample visibility
		if (isEnvironmentSample) {
			diseaseField.setVisible(false);
			diseaseDetailsField.setVisible(false);
			testedPathogenField.setVisible(true);
		} else {
			diseaseField.setVisible(true);
			testedPathogenField.setVisible(false);
		}

		// Auto-select default disease on creation when server has a single active disease
		if (create && !isEnvironmentSample) {
			Disease defaultDisease = FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease();
			if (defaultDisease != null) {
				diseaseField.setValue(defaultDisease);
			}
		}
	}

	private void bindFields() {
		if (isEnvironmentSample) {
			binder.forField(diseaseField).bind(PathogenTestDto::getTestedDisease, PathogenTestDto::setTestedDisease);
		} else {
			binder.forField(diseaseField).asRequired().bind(PathogenTestDto::getTestedDisease, PathogenTestDto::setTestedDisease);
		}
		binder.forField(diseaseDetailsField).bind(PathogenTestDto::getTestedDiseaseDetails, PathogenTestDto::setTestedDiseaseDetails);
		if (isEnvironmentSample) {
			binder.forField(testedPathogenField).asRequired().bind(PathogenTestDto::getTestedPathogen, PathogenTestDto::setTestedPathogen);
		} else {
			binder.forField(testedPathogenField).bind(PathogenTestDto::getTestedPathogen, PathogenTestDto::setTestedPathogen);
		}
		binder.forField(testedPathogenDetailsField).bind(PathogenTestDto::getTestedPathogenDetails, PathogenTestDto::setTestedPathogenDetails);
	}

	private void wireEvents() {
		// Disease change: show/hide details, fire event
		track(diseaseField.addValueChangeListener(e -> {
			Disease newDisease = e.getValue();

			boolean showDiseaseDetails = newDisease == Disease.OTHER;
			diseaseDetailsField.setVisible(showDiseaseDetails);
			diseaseDetailsSpacer.setVisible(!showDiseaseDetails);
			if (!showDiseaseDetails) {
				diseaseDetailsField.clear();
			}

			eventBus.fire(new DiseaseChangedEvent(newDisease));
		}));

		// Pathogen details visibility
		track(testedPathogenField.addValueChangeListener(e -> {
			Pathogen pathogen = e.getValue();
			boolean showPathogenDetails = pathogen != null && pathogen.isHasDetails();
			testedPathogenDetailsField.setVisible(showPathogenDetails);
			pathogenDetailsSpacer.setVisible(!showPathogenDetails);
			if (!showPathogenDetails) {
				testedPathogenDetailsField.clear();
			}
		}));
	}

	public ComboBox<Disease> getDiseaseField() {
		return diseaseField;
	}
}
