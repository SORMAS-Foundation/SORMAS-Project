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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.aefiinvestigationlink;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationListEntryDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

@SuppressWarnings("serial")
public class AefiInvestigationListEntry extends SideComponentField {

	public static final String SEPARATOR = ": ";

	private final AefiInvestigationListEntryDto listEntry;

	public AefiInvestigationListEntry(AefiInvestigationListEntryDto listEntry) {

		this.listEntry = listEntry;

		if (!StringUtils.isBlank(listEntry.getInvestigationCaseId())) {
			Label labelCaseId = new Label(listEntry.getInvestigationCaseId());
			CssStyles.style(labelCaseId, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			addComponentToField(labelCaseId);
		}

		Label labelInvestigationDate = new Label(
			I18nProperties.getPrefixCaption(AefiInvestigationListEntryDto.I18N_PREFIX, AefiInvestigationListEntryDto.INVESTIGATION_DATE)
				+ SEPARATOR
				+ DateFormatHelper.formatLocalDate(listEntry.getInvestigationDate()));
		addComponentToField(labelInvestigationDate);

		Label labelInvestigationStage = new Label(
			I18nProperties.getPrefixCaption(AefiInvestigationListEntryDto.I18N_PREFIX, AefiInvestigationListEntryDto.INVESTIGATION_STAGE)
				+ SEPARATOR
				+ listEntry.getInvestigationStage());
		CssStyles.style(labelInvestigationStage, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		addComponentToField(labelInvestigationStage);

		Label labelStatusOnInvestigation = new Label(
			I18nProperties.getPrefixCaption(AefiInvestigationListEntryDto.I18N_PREFIX, AefiInvestigationListEntryDto.STATUS_ON_DATE_OF_INVESTIGATION)
				+ SEPARATOR
				+ listEntry.getStatusOnDateOfInvestigation());
		CssStyles.style(labelStatusOnInvestigation, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		addComponentToField(labelStatusOnInvestigation);

		Label labelAefiClassification = new Label(
			I18nProperties.getPrefixCaption(AefiInvestigationListEntryDto.I18N_PREFIX, AefiInvestigationListEntryDto.AEFI_CLASSIFICATION)
				+ SEPARATOR
				+ ((listEntry.getAefiClassification() != null) ? listEntry.getAefiClassification() : ""));
		CssStyles.style(labelAefiClassification, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		addComponentToField(labelAefiClassification);
	}

	public AefiInvestigationListEntryDto getListEntry() {
		return listEntry;
	}
}
