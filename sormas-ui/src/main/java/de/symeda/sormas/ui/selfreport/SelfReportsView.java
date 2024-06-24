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

package de.symeda.sormas.ui.selfreport;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportExportDto;
import de.symeda.sormas.api.selfreport.SelfReportIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.selfreport.importer.SelfReportImportLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SelfReportsView extends AbstractView {

	private static final long serialVersionUID = -6229689625299341177L;
	public static final String VIEW_NAME = "selfreports";
	private final ViewConfiguration viewConfiguration;
	private final SelfReportCriteria gridCriteria;
	private final SelfReportGridComponent gridComponent;

	public SelfReportsView() {
		super(VIEW_NAME);

		setSizeFull();

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		gridCriteria = ViewModelProviders.of(getClass())
			.getOrDefault(SelfReportCriteria.class, () -> new SelfReportCriteria().relevanceStatus(EntityRelevanceStatus.ACTIVE));

		gridComponent = new SelfReportGridComponent(gridCriteria, viewConfiguration, () -> navigateTo(gridCriteria, true), () -> {
			ViewModelProviders.of(getClass()).remove(SelfReportCriteria.class);
			navigateTo(null, true);
		});
		addComponent(gridComponent);

		if (UiUtil.permitted(UserRight.SELF_REPORT_IMPORT)) {
			Button importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window popupWindow = VaadinUiUtil.showPopupWindow(new SelfReportImportLayout());
				popupWindow.setCaption(I18nProperties.getString(Strings.headingImportSelfReports));
				popupWindow.addCloseListener(c -> gridComponent.reload());
			}, ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(importButton);
		}

		if (UiUtil.permitted(UserRight.SELF_REPORT_EXPORT)) {
			addExportButton();
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			gridCriteria.fromUrlParams(params);
		}

		setApplyingCriteria(true);
		gridComponent.updateFilterComponents(gridCriteria);
		setApplyingCriteria(false);

		gridComponent.reload();
	}

	private void addExportButton() {
		VerticalLayout exportLayout = new VerticalLayout();
		exportLayout.setSpacing(true);
		exportLayout.setMargin(true);
		exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		exportLayout.setWidth(200, Unit.PIXELS);

		PopupButton exportButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
		addHeaderComponent(exportButton);

		Button basicExportButton = ButtonHelper.createIconButton(Captions.exportBasic, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
		basicExportButton.setDescription(I18nProperties.getString(Strings.infoBasicExport));
		basicExportButton.setWidth(100, Unit.PERCENTAGE);

		exportLayout.addComponent(basicExportButton);

		SelfReportGrid grid = gridComponent.getGrid();
		StreamResource streamResource =
			GridExportStreamResource.createStreamResourceWithSelectedItems(grid, this::getSelectedRows, ExportEntityName.SELF_REPORTS);
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(basicExportButton);

		StreamResource extendedExportStreamResource = DownloadUtil.createCsvExportStreamResource(
			SelfReportExportDto.class,
			null,
			(Integer start, Integer max) -> FacadeProvider.getSelfReportFacade()
				.getExportList(
					grid.getCriteria(),
					getSelectedRows().stream().map(SelfReportIndexDto::getUuid).collect(Collectors.toList()),
					start,
					max),
			(propertyId, type) -> {
				String caption = I18nProperties.findPrefixCaption(
					propertyId,
					SelfReportExportDto.I18N_PREFIX,
					SelfReportDto.I18N_PREFIX,
					DistrictDto.I18N_PREFIX,
					LocationDto.I18N_PREFIX);

				if (Date.class.isAssignableFrom(type)) {
					caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
				}
				return caption;
			},
			ExportEntityName.SELF_REPORTS,
			null);

		addExportButton(
			extendedExportStreamResource,
			exportButton,
			exportLayout,
			VaadinIcons.FILE_TEXT,
			Captions.exportDetailed,
			Strings.infoDetailedExport);
	}

	private Set<SelfReportIndexDto> getSelectedRows() {
		return this.viewConfiguration.isInEagerMode() ? gridComponent.getSelectedItems() : Collections.emptySet();
	}
}
