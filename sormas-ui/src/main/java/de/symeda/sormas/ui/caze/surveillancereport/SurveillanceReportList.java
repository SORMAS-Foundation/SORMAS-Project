/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze.surveillancereport;

import java.util.List;

import javax.validation.constraints.Null;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportCriteria;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.PaginationList;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class SurveillanceReportList extends PaginationList<SurveillanceReportDto> {

	private final SurveillanceReportCriteria criteria = new SurveillanceReportCriteria();
	private final boolean isEditAllowed;
	private final UserRight editRight;

	public SurveillanceReportList(CaseReferenceDto caze, UserRight editRight, boolean isEditAllowed) {
		super(5);
		criteria.caze(caze);
		this.editRight = editRight;
		this.isEditAllowed = isEditAllowed;
	}

	@Override
	public void reload() {
		List<SurveillanceReportDto> reports = FacadeProvider.getSurveillanceReportFacade().getIndexList(criteria, 0, maxDisplayedEntries * 20, null);

		setEntries(reports);
		if (!reports.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			Label noSamplesLabel = new Label(I18nProperties.getCaption(Captions.surveillanceReportNoReportsForCase));
			listLayout.addComponent(noSamplesLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<SurveillanceReportDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			SurveillanceReportDto report = displayedEntries.get(i);
			SurveillanceReportListEntry listEntry = new SurveillanceReportListEntry(report);

			boolean isEditable = UiUtil.permitted(isEditAllowed, UserRight.CASE_EDIT)
				&& !report.isOwnershipHandedOver()
				&& (report.getSormasToSormasOriginInfo() == null || report.getSormasToSormasOriginInfo().isOwnershipHandedOver());

			listEntry.addActionButton(
				report.getUuid(),
				(Button.ClickListener) event -> ControllerProvider.getSurveillanceReportController()
					.editSurveillanceReport(listEntry.getReport(), this::reload, isEditable),
				isEditable);

			listEntry.setEnabled(isEditable);
			if (UiUtil.getUserRights().contains(UserRight.EXTERNAL_MESSAGE_VIEW)) {
				addViewExternalMessageButton(listEntry);
			}
			listLayout.addComponent(listEntry);
		}
	}

	private void addViewExternalMessageButton(SurveillanceReportListEntry listEntry) {
		if (UiUtil.permitted(UserRight.EXTERNAL_MESSAGE_VIEW)) {
			ExternalMessageDto externalMessage =
				FacadeProvider.getExternalMessageFacade().getForSurveillanceReport(listEntry.getReport().toReference());
			if (externalMessage != null) {
				listEntry.addAssociatedMessageListener(
					clickEvent -> ControllerProvider.getExternalMessageController().showExternalMessage(externalMessage.getUuid(), false, null));
			}
		}
	}

	public class SurveillanceReportListEntry extends SideComponentField {

		private final SurveillanceReportDto report;

		private Button editButton;

		private Button associatedMessageButton;
		private UiFieldAccessCheckers fieldAccessCheckers;

		public SurveillanceReportListEntry(SurveillanceReportDto report) {
			this.report = report;
			this.fieldAccessCheckers = UiFieldAccessCheckers
				.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(report.isInJurisdiction()), report.isPseudonymized());

			VerticalLayout mainLayout = new VerticalLayout();
			mainLayout.setWidth(100, Unit.PERCENTAGE);
			mainLayout.setMargin(false);
			mainLayout.setSpacing(false);
			addComponentToField(mainLayout);

			Language userLanguage = UiUtil.getUser().getLanguage();
			mainLayout.addComponent(createRow(null, report.getReportingType(), SurveillanceReportDto.REPORTING_TYPE));
			mainLayout.addComponent(
				createRow(
					I18nProperties.getPrefixCaption(SurveillanceReportDto.I18N_PREFIX, SurveillanceReportDto.REPORT_DATE),
					DateHelper.formatLocalDate(report.getReportDate(), userLanguage),
					SurveillanceReportDto.REPORT_DATE));

			if (report.getDateOfDiagnosis() != null) {
				mainLayout.addComponent(
					createRow(
						I18nProperties.getPrefixCaption(SurveillanceReportDto.I18N_PREFIX, SurveillanceReportDto.DATE_OF_DIAGNOSIS),
						DateHelper.formatLocalDate(report.getDateOfDiagnosis(), userLanguage),
						SurveillanceReportDto.DATE_OF_DIAGNOSIS));
			}

			FacilityReferenceDto facility = report.getFacility();
			if (facility != null) {
				boolean isOtherFacility = facility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				String facilityName = isOtherFacility ? report.getFacilityDetails() : facility.getCaption();
				String propertyId = isOtherFacility ? SurveillanceReportDto.FACILITY_DETAILS : SurveillanceReportDto.FACILITY;

				mainLayout.addComponent(
					createRow(
						I18nProperties.getPrefixCaption(SurveillanceReportDto.I18N_PREFIX, propertyId),
						facilityName,
						SurveillanceReportDto.FACILITY_DETAILS));
			}
		}

		public SurveillanceReportDto getReport() {
			return report;
		}

		public void addEditListener(int rowIndex, Button.ClickListener clickListener) {
			if (editButton == null) {
				editButton = ButtonHelper.createIconButtonWithCaption(
					"edit-sample-" + rowIndex,
					null,
					VaadinIcons.PENCIL,
					null,
					ValoTheme.BUTTON_LINK,
					CssStyles.BUTTON_COMPACT);

				addComponent(editButton);
				setComponentAlignment(editButton, Alignment.TOP_RIGHT);
				setExpandRatio(editButton, 0);
			}

			editButton.addClickListener(clickListener);
		}

		private HorizontalLayout createRow(@Null String label, Object value, String propertyId) {
			HorizontalLayout row = new HorizontalLayout();
			row.setMargin(false);
			row.setSpacing(false);

			if (label != null) {
				Label rowLabel = new Label(DataHelper.toStringNullable(label) + ":");
				CssStyles.style(rowLabel, CssStyles.HSPACE_RIGHT_4);
				row.addComponent(rowLabel);
			}

			Label rowValue = new Label(DataHelper.toStringNullable(value));
			if (!fieldAccessCheckers.isAccessible(SurveillanceReportDto.class, propertyId)) {
				rowValue.addStyleName(CssStyles.INACCESSIBLE_LABEL);
			}
			if (label == null) {
				rowValue.addStyleName(CssStyles.LABEL_BOLD);
			}
			row.addComponent(rowValue);

			return row;
		}

		public void addAssociatedMessageListener(Button.ClickListener associatedMessageClickListener) {
			if (associatedMessageButton == null) {
				associatedMessageButton = ButtonHelper.createIconButtonWithCaption(
					"see-associated-message-" + report.getUuid(),
					null,
					VaadinIcons.NOTEBOOK,
					associatedMessageClickListener,
					ValoTheme.BUTTON_LINK,
					CssStyles.BUTTON_COMPACT);

				addComponent(associatedMessageButton);
				setComponentAlignment(associatedMessageButton, Alignment.TOP_RIGHT);
				setExpandRatio(associatedMessageButton, 0);
				associatedMessageButton.setDescription(I18nProperties.getDescription(Descriptions.SurveillanceReport_associatedMessage));
			}
		}
	}
}
