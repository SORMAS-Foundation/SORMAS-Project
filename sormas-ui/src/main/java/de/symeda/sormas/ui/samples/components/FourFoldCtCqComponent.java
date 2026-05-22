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

import com.vaadin.ui.CheckBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Four-fold antibody increase checkbox and CT/CQ value fields.
 * as a standalone composable component
 */
public class FourFoldCtCqComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;
	private final int caseSampleCount;
	private final CtCqValueComponent ctCqValueComponent;

	private CheckBox fourFoldIncrease;

	private Disease currentDisease;
	private PathogenTestType currentTestType;
	private PathogenTestResultType currentTestResult;

	public FourFoldCtCqComponent(FormEventBus eventBus, int caseSampleCount, boolean isLuxembourg, Disease initialDisease) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.caseSampleCount = caseSampleCount;
		this.currentDisease = initialDisease;
		this.ctCqValueComponent = new CtCqValueComponent(isLuxembourg);
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		fourFoldIncrease = createCheckBox(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, PathogenTestDto.I18N_PREFIX);
		CssStyles.style(fourFoldIncrease, CssStyles.VSPACE_3, CssStyles.VSPACE_TOP_4);
		fourFoldIncrease.setVisible(false);
		fourFoldIncrease.setEnabled(false);
		addComponent(fourFoldIncrease);

		addComponent(ctCqValueComponent);
	}

	private void bindFields() {
		binder.forField(fourFoldIncrease).bind(PathogenTestDto::isFourFoldIncreaseAntibodyTiter, PathogenTestDto::setFourFoldIncreaseAntibodyTiter);
	}

	private void wireEvents() {
		// Listen for test type changes
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			PathogenTestType testType = event.getTestType();
			currentTestType = testType;

			if (testType != null) {
				updateFourFoldIncrease(testType);

				ctCqValueComponent.updateCtVisibility(currentDisease, testType);
			} else {
				ctCqValueComponent.updateCtVisibility(currentDisease, null);
			}

			ctCqValueComponent.updateCqVisibility(currentDisease, currentTestType, currentTestResult);
			syncSelfVisibility();
		}));

		// Listen for test result changes
		track(eventBus.on(TestResultChangedEvent.class, event -> {
			currentTestResult = event.getTestResult();
			if (currentTestType != null) {
				updateFourFoldIncrease(currentTestType);
			}
			ctCqValueComponent.updateCqVisibility(currentDisease, currentTestType, currentTestResult);
			syncSelfVisibility();
		}));

		// Listen for disease changes
		track(eventBus.on(DiseaseChangedEvent.class, event -> {
			currentDisease = event.getDisease();
			if (currentTestType != null) {
				updateFourFoldIncrease(currentTestType);
			}
			ctCqValueComponent.updateCtVisibility(currentDisease, currentTestType);
			ctCqValueComponent.updateCqVisibility(currentDisease, currentTestType, currentTestResult);
			syncSelfVisibility();
		}));
	}

	private void updateFourFoldIncrease(PathogenTestType testType) {
		boolean antibodyTest = testType == PathogenTestType.IGM_SERUM_ANTIBODY || testType == PathogenTestType.IGG_SERUM_ANTIBODY;
		boolean positive = currentTestResult == PathogenTestResultType.POSITIVE;
		if (antibodyTest && positive) {
			fourFoldIncrease.setVisible(true);
			fourFoldIncrease.setEnabled(true);
		} else {
			fourFoldIncrease.setVisible(false);
			fourFoldIncrease.setEnabled(false);
			fourFoldIncrease.clear();
		}
		fourFoldIncrease.setCaption(
			currentDisease == Disease.DENGUE
				? I18nProperties.getCaption(Captions.PathogenTest_fourFoldIncreaseAntibodyTiter_DENGUE)
				: I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER));
	}

	private void syncSelfVisibility() {
		setVisible(fourFoldIncrease.isVisible() || ctCqValueComponent.isVisible());
	}

	@Override
	public void setDto(PathogenTestDto dto) {
		super.setDto(dto);
		ctCqValueComponent.setDto(dto);
		// Sync visibility to the loaded DTO values, since events are not fired on initial load
		currentTestType = dto != null ? dto.getTestType() : null;
		currentTestResult = dto != null ? dto.getTestResult() : null;
		ctCqValueComponent.updateCtVisibility(currentDisease, currentTestType);
		ctCqValueComponent.updateCqVisibility(currentDisease, currentTestType, currentTestResult);
		if (currentTestType != null) {
			updateFourFoldIncrease(currentTestType);
		}
		syncSelfVisibility();
	}

	@Override
	public void validate() {
		super.validate();
		ctCqValueComponent.validate();
	}

	@Override
	public void applyVisibility(FieldVisibilityCheckers checkers, Class<?> dtoClass) {
		super.applyVisibility(checkers, dtoClass);
		ctCqValueComponent.applyVisibility(checkers, dtoClass);
		syncSelfVisibility();
	}

	@Override
	public void applyAccess(UiFieldAccessCheckers checkers, Class<?> dtoClass) {
		super.applyAccess(checkers, dtoClass);
		ctCqValueComponent.applyAccess(checkers, dtoClass);
	}
}
