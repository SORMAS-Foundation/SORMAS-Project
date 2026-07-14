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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayablePartialRetrievalResponse;

/**
 * Read-only panel displaying patch failures for a survey response.
 * Shows all failures including non-correctable ones so the user can understand what happened.
 */
public class SurveyResponseFailurePanel extends VerticalLayout {

	private static final long serialVersionUID = -2309124756823178543L;

	public SurveyResponseFailurePanel(Map<PatchField, DataPatchFailure> failures, DisplayablePartialRetrievalResponse displayData) {
		setMargin(false);
		setSpacing(true);
		setSizeFull();

		List<Map.Entry<PatchField, DataPatchFailure>> failureEntries = new ArrayList<>(failures.entrySet());

		Grid<Map.Entry<PatchField, DataPatchFailure>> grid = new Grid<>();
		grid.setSizeFull();
		grid.setItems(failureEntries);

		grid.addColumn(entry -> SurveyResponseDisplayUtils.resolveFieldName(entry.getKey(), displayData))
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

		grid.addColumn(entry -> SurveyResponseDisplayUtils.resolveCurrentValue(entry.getKey(), displayData))
			.setCaption(I18nProperties.getCaption(Captions.surveyResponseCurrentCaseValue))
			.setId("currentValue")
			.setExpandRatio(2);

		grid.setHeightByRows(Math.max(failureEntries.size(), 1));

		addComponent(grid);
	}

}
