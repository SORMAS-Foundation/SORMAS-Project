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
import java.util.Map;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseResult;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseWrapper;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayableFieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayablePartialRetrievalResponse;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Modal editor window allowing users to correct failed survey response fields and reprocess.
 */
public class SurveyResponseFailureEditor extends Window {

	private static final long serialVersionUID = 4912870523418167234L;

	public SurveyResponseFailureEditor(ExternalMessageDto externalMessage, DisplayablePartialRetrievalResponse displayData, Runnable onReprocessed) {
		setCaption(I18nProperties.getString(Strings.headingSurveyResponseCorrectAndReprocess));
		setModal(true);
		setResizable(true);
		setWidth(700, Unit.PIXELS);

		ExternalMessageSurveyResponseWrapper latest = externalMessage.getSurveyResponseData().getLatest();
		Map<String, Object> patchDictionary = latest.getRequest().getPatchDictionary();

		ExternalMessageSurveyResponseResult result = latest.getResult();
		Map<String, DataPatchFailure> failures =
			result != null && result.getPatchResponse() != null ? result.getPatchResponse().getFailures() : new HashMap<>();
		Map<String, Object> validValues =
			result != null && result.getPatchResponse() != null ? result.getPatchResponse().getValidPatchDictionary() : new HashMap<>();

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

			Map<String, TextField> fieldEditors = new HashMap<>();

			for (Map.Entry<String, DataPatchFailure> entry : failures.entrySet()) {
				String fieldPath = entry.getKey();
				DataPatchFailure failure = entry.getValue();

				String fieldLabel = resolveFieldName(fieldPath, displayData);
				String currentValue = resolveCurrentValue(fieldPath, displayData);
				String causeName = failure.getDataPatchFailureCause() != null ? failure.getDataPatchFailureCause().name() : "";

				VerticalLayout fieldContainer = new VerticalLayout();
				fieldContainer.setMargin(false);
				fieldContainer.setSpacing(false);

				Label causeLabel = new Label(I18nProperties.getCaption(Captions.surveyResponseFailureCause) + ": " + causeName);
				CssStyles.style(causeLabel, CssStyles.LABEL_SMALL, CssStyles.LABEL_SECONDARY);
				fieldContainer.addComponent(causeLabel);

				Label currentValueLabel =
					new Label(I18nProperties.getCaption(Captions.surveyResponseCurrentCaseValue) + ": " + (currentValue != null ? currentValue : ""));
				CssStyles.style(currentValueLabel, CssStyles.LABEL_SMALL, CssStyles.LABEL_SECONDARY);
				fieldContainer.addComponent(currentValueLabel);

				TextField valueField = new TextField();
				valueField.setCaption(fieldLabel);
				valueField.setWidth(100, Unit.PERCENTAGE);
				if (failure.getProvidedFieldValue() != null) {
					valueField.setValue(failure.getProvidedFieldValue().toString());
				}
				fieldContainer.addComponent(valueField);
				fieldEditors.put(fieldPath, valueField);

				failuresForm.addComponent(fieldContainer);
			}

			mainLayout.addComponent(failuresForm);

			// --- Valid fields (read-only context) ---
			if (!validValues.isEmpty()) {
				Label validHeading = new Label(I18nProperties.getCaption(Captions.surveyResponseValidFields));
				CssStyles.style(validHeading, CssStyles.H4);
				mainLayout.addComponent(validHeading);

				Panel validPanel = new Panel();
				FormLayout validForm = new FormLayout();
				validForm.setMargin(true);

				for (Map.Entry<String, Object> entry : validValues.entrySet()) {
					String fieldPath = entry.getKey();
					String fieldLabel = resolveFieldName(fieldPath, displayData);
					Label label = new Label(entry.getValue() != null ? entry.getValue().toString() : "");
					label.setCaption(fieldLabel);
					validForm.addComponent(label);
				}

				validPanel.setContent(validForm);
				validPanel.setHeight(120, Unit.PIXELS);
				mainLayout.addComponent(validPanel);
			}

			// --- Buttons ---
			HorizontalLayout buttonsLayout = new HorizontalLayout();
			buttonsLayout.setSpacing(true);

			Button saveAndReprocessButton =
				ButtonHelper.createButton(Captions.actionSaveAndReprocess, I18nProperties.getCaption(Captions.actionSaveAndReprocess), e -> {
					Map<String, Object> correctedDictionary = new HashMap<>(validValues);
					for (Map.Entry<String, TextField> editorEntry : fieldEditors.entrySet()) {
						String value = editorEntry.getValue().getValue();
						correctedDictionary.put(editorEntry.getKey(), value);
					}

					FacadeProvider.getExternalMessageFacade().reprocessSurveyResponse(externalMessage.getUuid(), correctedDictionary);

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

	private String resolveFieldName(String fieldPath, DisplayablePartialRetrievalResponse displayData) {
		if (displayData != null) {
			DisplayableFieldInfo info = displayData.getFieldInfoDictionary().get(fieldPath);
			if (info != null && info.getTranslatedFieldName() != null) {
				return info.getTranslatedFieldName();
			}
		}
		return fieldPath;
	}

	private String resolveCurrentValue(String fieldPath, DisplayablePartialRetrievalResponse displayData) {
		if (displayData != null) {
			DisplayableFieldInfo info = displayData.getFieldInfoDictionary().get(fieldPath);
			if (info != null) {
				return info.getTranslatedFieldValue();
			}
		}
		return null;
	}
}
