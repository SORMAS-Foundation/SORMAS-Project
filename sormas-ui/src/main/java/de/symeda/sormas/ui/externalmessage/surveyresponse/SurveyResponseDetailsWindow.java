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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseRequest;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseResult;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseWrapper;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayableFieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayablePartialRetrievalResponse;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Popup window displaying full details of a SURVEY_RESPONSE external message.
 */
public class SurveyResponseDetailsWindow {

	public SurveyResponseDetailsWindow(ExternalMessageDto externalMessage, Runnable onFormActionPerformed) {
		String uuid = externalMessage.getUuid();

		DisplayablePartialRetrievalResponse displayData;
		displayData = FacadeProvider.getExternalMessageFacade().retrieveSurveyResponseFieldsForDisplay(uuid);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setWidth(100, Sizeable.Unit.PERCENTAGE);

		Window window = new Window(I18nProperties.getString(Strings.headingSurveyResponseDetails));
		window.setModal(true);
		window.setResizable(true);
		window.setWidth(850, Sizeable.Unit.PIXELS);

		ExternalMessageSurveyResponseWrapper latest = externalMessage.getSurveyResponseData().getLatest();
		ExternalMessageSurveyResponseRequest request = latest.getRequest();
		ExternalMessageSurveyResponseResult result = latest.getResult();

		// --- Metadata section ---
		Label metadataHeading = new Label(I18nProperties.getCaption(Captions.surveyResponseMetadata));
		CssStyles.style(metadataHeading, CssStyles.H3);
		layout.addComponent(metadataHeading);

		addReadOnlyField(layout, "External Survey ID", request.getExternalSurveyId());
		addReadOnlyField(layout, "Token", request.getToken());
		addReadOnlyField(layout, "Respondent ID", request.getExternalRespondentId());
		addReadOnlyField(layout, "Response Received", request.getResponseReceivedDate() != null ? request.getResponseReceivedDate().toString() : "");
		addReadOnlyField(layout, "Replacement Strategy", request.getReplacementStrategy() != null ? request.getReplacementStrategy().name() : "");
		addReadOnlyField(layout, "Empty Value Behavior", request.getEmptyValueBehavior() != null ? request.getEmptyValueBehavior().name() : "");
		addReadOnlyField(layout, "Patched in Case of Failures", String.valueOf(request.isPatchedInCaseOfFailures()));

		// --- Patch Dictionary section ---
		Label dictionaryHeading = new Label(I18nProperties.getCaption(Captions.surveyResponsePatchDictionary));
		CssStyles.style(dictionaryHeading, CssStyles.H3);
		layout.addComponent(dictionaryHeading);

		Map<String, Object> patchDictionary = request.getPatchDictionary();
		if (patchDictionary != null && !patchDictionary.isEmpty()) {
			List<Map.Entry<String, Object>> entries = patchDictionary.entrySet().stream().collect(Collectors.toList());

			final DisplayablePartialRetrievalResponse finalDisplayData = displayData;

			Grid<Map.Entry<String, Object>> dictionaryGrid = new Grid<>();
			dictionaryGrid.setSizeFull();
			dictionaryGrid.setItems(entries);
			dictionaryGrid.setHeightByRows(Math.max(entries.size(), 1));

			VaadinUiUtil.showWarningPopup(String.format("Entries: [%s]", entries));
			VaadinUiUtil.showWarningPopup(String.format("finalDisplayData: [%s]", finalDisplayData));

			dictionaryGrid.addColumn(entry -> resolveFieldName(entry.getKey(), finalDisplayData))
				.setCaption(I18nProperties.getCaption(Captions.surveyResponseField))
				.setExpandRatio(2);

			dictionaryGrid.addColumn(entry -> entry.getValue() != null ? entry.getValue().toString() : "")
				.setCaption(I18nProperties.getCaption(Captions.surveyResponseSubmittedValue))
				.setExpandRatio(2);

			dictionaryGrid.addColumn(entry -> resolveCurrentValue(entry.getKey(), finalDisplayData))
				.setCaption(I18nProperties.getCaption(Captions.surveyResponseCurrentCaseValue))
				.setExpandRatio(2);

			layout.addComponent(dictionaryGrid);
		}

		// --- Processing Result section ---
		if (result != null) {
			Label resultHeading = new Label(I18nProperties.getCaption(Captions.surveyResponseProcessingResult));
			CssStyles.style(resultHeading, CssStyles.H3);
			layout.addComponent(resultHeading);

			if (result.getCaseUuid() != null) {
				Link caseLink = new Link(
					I18nProperties.getCaption(Captions.surveyResponseCaseLink) + ": " + result.getCaseUuid(),
					new ExternalResource("#!" + CaseDataView.VIEW_NAME + "/" + result.getCaseUuid()));
				layout.addComponent(caseLink);
			}

			DataPatchResponse patchResponse = result.getPatchResponse();
			if (patchResponse != null) {
				addReadOnlyField(layout, I18nProperties.getCaption(Captions.surveyResponseApplied), patchResponse.isApplied() ? "Yes" : "No");

				if (patchResponse.hasFailures()) {
					Label failuresHeading = new Label(I18nProperties.getString(Strings.headingSurveyResponseFailures));
					CssStyles.style(failuresHeading, CssStyles.H4);
					layout.addComponent(failuresHeading);

					layout.addComponent(new SurveyResponseFailurePanel(patchResponse.getFailures(), displayData));
				} else {
					Label successLabel = new Label(I18nProperties.getString(Strings.messageSurveyResponseAllFieldsApplied));
					CssStyles.style(successLabel, ValoTheme.LABEL_SUCCESS);
					layout.addComponent(successLabel);
				}
			}
		} else {
			Label notProcessedLabel = new Label(I18nProperties.getString(Strings.messageSurveyResponseNotYetProcessed));
			layout.addComponent(notProcessedLabel);
		}

		// --- Actions bar (if processable and has failures) ---
		boolean canProcess = UiUtil.permitted(UserRight.EXTERNAL_MESSAGE_SURVEY_RESPONSE_PROCESS);
		boolean hasFailures = result != null
			&& result.getPatchResponse() != null
			&& result.getPatchResponse().hasFailures()
			&& !result.getPatchResponse().getFailures().isEmpty();

		if (canProcess && hasFailures) {
			final DisplayablePartialRetrievalResponse finalDisplayDataForEditor = displayData;
			Button correctButton =
				ButtonHelper.createButton(Captions.actionCorrectAndReprocess, I18nProperties.getCaption(Captions.actionCorrectAndReprocess), e -> {
					ExternalMessageDto refreshedDto = FacadeProvider.getExternalMessageFacade().getByUuid(uuid);
					SurveyResponseFailureEditor editor = new SurveyResponseFailureEditor(refreshedDto, finalDisplayDataForEditor, () -> {
						window.close();
						onFormActionPerformed.run();
					});
					UI.getCurrent().addWindow(editor);
				}, ValoTheme.BUTTON_PRIMARY);

			layout.addComponent(correctButton);
		}

		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}

	private void addReadOnlyField(VerticalLayout layout, String caption, String value) {
		Label label = new Label(value != null ? value : "");
		label.setCaption(caption);
		layout.addComponent(label);
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
			if (info != null && info.getTranslatedFieldValue() != null) {
				return info.getTranslatedFieldValue();
			}
		}
		return "";
	}
}
