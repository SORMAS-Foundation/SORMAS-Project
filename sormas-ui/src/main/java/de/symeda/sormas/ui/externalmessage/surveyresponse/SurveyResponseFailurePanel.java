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

import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayableFieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayablePartialRetrievalResponse;

/**
 * Read-only panel displaying patch failures for a survey response.
 * Shows all failures including non-correctable ones so the user can understand what happened.
 */
public class SurveyResponseFailurePanel extends VerticalLayout {

	private static final long serialVersionUID = -2309124756823178543L;

	public SurveyResponseFailurePanel(Map<String, DataPatchFailure> failures, DisplayablePartialRetrievalResponse displayData) {
		setMargin(false);
		setSpacing(true);
		setSizeFull();

		List<Map.Entry<String, DataPatchFailure>> failureEntries = failures.entrySet().stream().collect(Collectors.toList());

		Grid<Map.Entry<String, DataPatchFailure>> grid = new Grid<>();
		grid.setSizeFull();
		grid.setItems(failureEntries);

		grid.addColumn(entry -> resolveFieldName(entry.getKey(), displayData))
			.setCaption(I18nProperties.getCaption(Captions.surveyResponseField))
			.setId("field")
			.setExpandRatio(2);

		grid.addColumn(
			entry -> entry.getValue().getDataPatchFailureCause() != null
				? I18nProperties.getEnumCaption(entry.getValue().getDataPatchFailureCause())
				: "")
			.setCaption(I18nProperties.getCaption(Captions.surveyResponseFailureCause))
			.setId("cause")
			.setExpandRatio(2);

		grid.addColumn(entry -> entry.getValue().getProvidedFieldValue() != null ? entry.getValue().getProvidedFieldValue().toString() : "")
			.setCaption(I18nProperties.getCaption(Captions.surveyResponseSubmittedValue))
			.setId("submittedValue")
			.setExpandRatio(2);

		grid.addColumn(entry -> resolveCurrentValue(entry.getKey(), displayData))
			.setCaption(I18nProperties.getCaption(Captions.surveyResponseCurrentCaseValue))
			.setId("currentValue")
			.setExpandRatio(2);

		grid.addColumn(entry -> entry.getValue().getDescription() != null ? entry.getValue().getDescription() : "")
			.setCaption(I18nProperties.getCaption(Captions.surveyResponseDescription))
			.setId("description")
			.setExpandRatio(3);

		grid.setHeightByRows(Math.max(failureEntries.size(), 1));

		addComponent(grid);
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
