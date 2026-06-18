/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.epipulse;

import com.vaadin.ui.Button;
import com.vaadin.v7.ui.PopupDateField;

import de.symeda.sormas.api.epipulse.EpipulseExportCriteria;
import de.symeda.sormas.api.epipulse.EpipulseExportIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.UserRightFieldVisibilityChecker;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class EpipulseExportGridFilterForm extends AbstractFilterForm<EpipulseExportCriteria> {

	private static final long serialVersionUID = 8580802999654498285L;

	private static final String MORE_FILTERS_HTML = null;

	protected EpipulseExportGridFilterForm() {
		super(
			EpipulseExportCriteria.class,
			EpipulseExportIndexDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCheckers(new UserRightFieldVisibilityChecker(UiUtil::permitted)),
			JurisdictionFieldConfig.of(null, null, null));
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			EpipulseExportCriteria.SUBJECT_CODE,
			EpipulseExportCriteria.STATUS,
			EpipulseExportCriteria.REPORT_DATE_FROM,
			EpipulseExportCriteria.REPORT_DATE_TO };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML;
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.pixelSized(EpipulseExportCriteria.SUBJECT_CODE, 150));
		addField(FieldConfiguration.pixelSized(EpipulseExportCriteria.STATUS, 150));

		PopupDateField reportDateFrom = addField(getContent(), FieldConfiguration.pixelSized(EpipulseExportCriteria.REPORT_DATE_FROM, 140));
		reportDateFrom.setInputPrompt(I18nProperties.getString(Strings.promptEpipulseExportDateFrom));

		PopupDateField reoprtDateTo = addField(getContent(), FieldConfiguration.pixelSized(EpipulseExportCriteria.REPORT_DATE_TO, 140));
		reoprtDateTo.setInputPrompt(I18nProperties.getString(Strings.promptEpipulseExportDateTo));
	}

	public Button getApplyButton() {
		return getFormActionButtonsComponent().getApplyResetButtonsComponent().getApplyButton();
	}
}
