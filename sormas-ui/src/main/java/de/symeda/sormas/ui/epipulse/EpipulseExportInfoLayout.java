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

package de.symeda.sormas.ui.epipulse;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.AbstractInfoLayout;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldAccessHelper;

public class EpipulseExportInfoLayout extends AbstractInfoLayout<EpipulseExportDto> {

	private EpipulseExportDto epipulseExportDto;
	private EpipulseExportView epipulseExportView;
	private Language userLanguage;

	public EpipulseExportInfoLayout(EpipulseExportDto epipulseExportDto, EpipulseExportView epipulseExportView) {
		super(EpipulseExportDto.class, FieldAccessHelper.getFieldAccessCheckers(epipulseExportDto));
		this.epipulseExportDto = epipulseExportDto;
		this.epipulseExportView = epipulseExportView;
		userLanguage = I18nProperties.getUserLanguage();

		setSpacing(true);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);

		updateEpipulseExportInfo();
	}

	/**
	 * Shows the export in two columns; for a failed export, the reason follows below in one full-width row in red, e.g.
	 * "No EpiPulse subject code is configured for Diphtheria. Enter it in the disease configuration of Diphtheria…".
	 */
	private void updateEpipulseExportInfo() {
		this.removeAllComponents();

		VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setMargin(false);
		contentLayout.setSpacing(true);
		contentLayout.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout columnsLayout = new HorizontalLayout();
		columnsLayout.setMargin(false);
		columnsLayout.setSpacing(true);
		columnsLayout.setWidth(100, Unit.PERCENTAGE);

		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(false);
		leftColumnLayout.setSpacing(true);

		addDescLabel(
			leftColumnLayout,
			EpipulseExportDto.SUBJECT_CODE,
			epipulseExportDto.getSubjectCode(),
			I18nProperties.getPrefixCaption(EpipulseExportDto.I18N_PREFIX, EpipulseExportDto.SUBJECT_CODE))
			.setDescription(epipulseExportDto.getSubjectCode().toString());

		String startDate = DateHelper.formatLocalDate(epipulseExportDto.getStartDate(), userLanguage);
		addDescLabel(
			leftColumnLayout,
			EpipulseExportDto.START_DATE,
			startDate,
			I18nProperties.getPrefixCaption(EpipulseExportDto.I18N_PREFIX, EpipulseExportDto.START_DATE)).setDescription(startDate);

		String creationDate = DateHelper.formatLocalDateTime(epipulseExportDto.getCreationDate(), userLanguage);
		addDescLabel(
			leftColumnLayout,
			EpipulseExportDto.CREATION_DATE,
			creationDate,
			I18nProperties.getPrefixCaption(EpipulseExportDto.I18N_PREFIX, EpipulseExportDto.CREATION_DATE)).setDescription(creationDate);

		columnsLayout.addComponent(leftColumnLayout);

		VerticalLayout rightColumnLayout = new VerticalLayout();
		rightColumnLayout.setMargin(false);
		rightColumnLayout.setSpacing(true);

		addDescLabel(rightColumnLayout, EpipulseExportDto.UUID, "", "");

		String endDate = DateHelper.formatLocalDate(epipulseExportDto.getEndDate(), userLanguage);
		addDescLabel(
			rightColumnLayout,
			EpipulseExportDto.END_DATE,
			endDate,
			I18nProperties.getPrefixCaption(EpipulseExportDto.I18N_PREFIX, EpipulseExportDto.END_DATE)).setDescription(endDate);

		String status = epipulseExportDto.getStatus().toString();
		addDescLabel(
			rightColumnLayout,
			EpipulseExportDto.STATUS,
			status,
			I18nProperties.getPrefixCaption(EpipulseExportDto.I18N_PREFIX, EpipulseExportDto.STATUS)).setDescription(status);

		columnsLayout.addComponent(rightColumnLayout);
		contentLayout.addComponent(columnsLayout);

		// why a failed export failed, e.g. "No EpiPulse subject code is configured for Diphtheria…", in one full-width row in
		// red, so the user sees at once what to correct
		String statusReason = epipulseExportDto.getStatusReason();
		if (epipulseExportDto.getStatus() == EpipulseExportStatus.FAILED && statusReason != null && !statusReason.trim().isEmpty()) {
			Label statusReasonLabel = addDescLabel(
				contentLayout,
				EpipulseExportDto.STATUS_REASON,
				statusReason.trim(),
				I18nProperties.getPrefixCaption(EpipulseExportDto.I18N_PREFIX, EpipulseExportDto.STATUS_REASON));
			if (statusReasonLabel != null) {
				statusReasonLabel.setWidth(100, Unit.PERCENTAGE);
				statusReasonLabel.setDescription(statusReason.trim());
				statusReasonLabel.addStyleName(CssStyles.LABEL_CRITICAL);
			}
		}

		this.addComponent(contentLayout);
	}
}
