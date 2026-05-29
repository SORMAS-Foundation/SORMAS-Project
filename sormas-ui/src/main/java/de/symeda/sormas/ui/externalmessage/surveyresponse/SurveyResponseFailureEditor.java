/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.externalmessage.surveyresponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseResult;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseWrapper;
import de.symeda.sormas.api.externalmessage.survey.PatchDictionary;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayablePartialRetrievalResponse;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Modal editor window allowing users to correct failed survey response fields and reprocess.
 * Each failed field can be ignored (excluded from reprocessing) or have its key renamed.
 */
public class SurveyResponseFailureEditor extends Window {

	private static final long serialVersionUID = 4912870523418167234L;

	public SurveyResponseFailureEditor(ExternalMessageDto externalMessage, DisplayablePartialRetrievalResponse displayData, Runnable onReprocessed) {
		setCaption(I18nProperties.getString(Strings.headingSurveyResponseCorrectAndReprocess));
		setModal(true);
		setResizable(true);
		setWidth(700, Unit.PIXELS);

		ExternalMessageSurveyResponseWrapper latest = externalMessage.getSurveyResponseData().getLatest();

		ExternalMessageSurveyResponseResult result = latest.getResult();
		LinkedHashMap<PatchField, DataPatchFailure> failures =
			result != null && result.getPatchResponse() != null ? result.getPatchResponse().getFailures() : new LinkedHashMap<>();
		LinkedHashMap<PatchField, Object> validValues = result != null && result.getPatchResponse() != null
			? result.getPatchResponse().getValidPatchDictionary().getDictionary()
			: new LinkedHashMap<>();

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		// --- Failed fields (editable) ---
		if (!failures.isEmpty()) {
			Label failuresHeading = new Label(I18nProperties.getString(Strings.headingSurveyResponseFailures));
			CssStyles.style(failuresHeading, CssStyles.H3);
			mainLayout.addComponent(failuresHeading);

			FormLayout failuresForm = new FormLayout();
			failuresForm.setMargin(false);

			Map<PatchField, CheckBox> ignoreCheckboxes = new HashMap<>();
			Map<PatchField, TextField> pathEditors = new HashMap<>();
			Map<PatchField, TextField> valueEditors = new HashMap<>();

			for (Map.Entry<PatchField, DataPatchFailure> entry : failures.entrySet()) {
				PatchField patchField = entry.getKey();
				DataPatchFailure failure = entry.getValue();

				String fieldPath = patchField.getField();
				String fieldLabel = SurveyResponseDisplayUtils.resolveFieldName(patchField, displayData);
				String currentValue = SurveyResponseDisplayUtils.resolveCurrentValue(patchField, displayData);
				String causeName =
					failure.getDataPatchFailureCause() != null ? I18nProperties.getEnumCaption(failure.getDataPatchFailureCause()) : "";

				VerticalLayout fieldContainer = new VerticalLayout();
				fieldContainer.setMargin(false);
				fieldContainer.setSpacing(false);

				// Ignore checkbox
				CheckBox ignoreCheckbox = new CheckBox(I18nProperties.getCaption(Captions.surveyResponseIgnoreField));
				ignoreCheckboxes.put(patchField, ignoreCheckbox);
				fieldContainer.addComponent(ignoreCheckbox);

				Label causeLabel = new Label(I18nProperties.getCaption(Captions.surveyResponseFailureCause) + ": " + causeName);
				CssStyles.style(causeLabel, CssStyles.LABEL_SMALL, CssStyles.LABEL_SECONDARY);
				fieldContainer.addComponent(causeLabel);

				Label currentValueLabel =
					new Label(I18nProperties.getCaption(Captions.surveyResponseCurrentCaseValue) + ": " + (currentValue != null ? currentValue : ""));
				CssStyles.style(currentValueLabel, CssStyles.LABEL_SMALL, CssStyles.LABEL_SECONDARY);
				fieldContainer.addComponent(currentValueLabel);

				// Key rename field
				TextField keyField = new TextField(I18nProperties.getCaption(Captions.surveyResponseKeyName));
				keyField.setValue(fieldPath);
				keyField.setWidth(100, Unit.PERCENTAGE);
				pathEditors.put(patchField, keyField);
				fieldContainer.addComponent(keyField);

				// Value field
				TextField valueField = new TextField(fieldLabel);
				valueField.setWidth(100, Unit.PERCENTAGE);
				if (failure.getProvidedFieldValue() != null) {
					valueField.setValue(failure.getProvidedFieldValue().toString());
				}
				valueEditors.put(patchField, valueField);
				fieldContainer.addComponent(valueField);

				// Wire ignore checkbox to disable key/value fields
				ignoreCheckbox.addValueChangeListener(event -> {
					boolean ignored = Boolean.TRUE.equals(event.getValue());
					keyField.setEnabled(!ignored);
					valueField.setEnabled(!ignored);
				});

				failuresForm.addComponent(fieldContainer);
			}

			mainLayout.addComponent(failuresForm);

			// --- Valid fields (read-only context) ---§
			if (!validValues.isEmpty()) {
				Label validHeading = new Label(I18nProperties.getCaption(Captions.surveyResponseValidFields));
				CssStyles.style(validHeading, CssStyles.H4);
				mainLayout.addComponent(validHeading);

				List<Map.Entry<PatchField, Object>> validEntries = validValues.entrySet().stream().collect(Collectors.toList());

				Grid<Map.Entry<PatchField, Object>> validGrid = new Grid<>();
				validGrid.setSizeFull();
				validGrid.setItems(validEntries);
				validGrid.setHeightByRows(Math.max(validEntries.size(), 1));

				validGrid.addColumn(entry -> SurveyResponseDisplayUtils.resolveFieldName(entry.getKey(), displayData))
					.setCaption(I18nProperties.getCaption(Captions.surveyResponseField))
					.setExpandRatio(2);

				validGrid.addColumn(entry -> entry.getValue() != null ? entry.getValue().toString() : "")
					.setCaption(I18nProperties.getCaption(Captions.surveyResponseSubmittedValue))
					.setExpandRatio(2);

				validGrid.addColumn(entry -> SurveyResponseDisplayUtils.resolveCurrentValue(entry.getKey(), displayData))
					.setCaption(I18nProperties.getCaption(Captions.surveyResponseCurrentCaseValue))
					.setExpandRatio(2);

				mainLayout.addComponent(validGrid);
			}

			// --- Buttons ---
			HorizontalLayout buttonsLayout = new HorizontalLayout();
			buttonsLayout.setSpacing(true);

			Button saveAndReprocessButton =
				ButtonHelper.createButton(Captions.actionSaveAndReprocess, I18nProperties.getCaption(Captions.actionSaveAndReprocess), e -> {
					LinkedHashMap<PatchField, Object> correctedDictionary = new LinkedHashMap<>(validValues);
					for (PatchField patchField : valueEditors.keySet()) {
						CheckBox ignoreCheckbox = ignoreCheckboxes.get(patchField);
						if (ignoreCheckbox != null && Boolean.TRUE.equals(ignoreCheckbox.getValue())) {
							continue;
						}
						String actualPath = pathEditors.get(patchField).getValue();
						if (actualPath == null || actualPath.trim().isEmpty()) {
							actualPath = patchField.getField();
						}
						correctedDictionary.put(
							new PatchField().setField(actualPath).setGroupIndex(patchField.getGroupIndex()),
							valueEditors.get(patchField).getValue());
					}

					FacadeProvider.getExternalMessageFacade()
						.overwriteSurveyResponse(externalMessage.getUuid(), new PatchDictionary().setDictionary(correctedDictionary));

					Notification.show(I18nProperties.getString(Strings.messageSurveyResponseReprocessed), Notification.Type.HUMANIZED_MESSAGE);
					close();
					onReprocessed.run();
				}, ValoTheme.BUTTON_PRIMARY);

			Button cancelButton = ButtonHelper.createButton(Captions.actionCancel, I18nProperties.getCaption(Captions.actionCancel), e -> close());

			buttonsLayout.addComponent(saveAndReprocessButton);
			buttonsLayout.addComponent(cancelButton);
			mainLayout.addComponent(buttonsLayout);
		}

		setContent(mainLayout);
	}

}
