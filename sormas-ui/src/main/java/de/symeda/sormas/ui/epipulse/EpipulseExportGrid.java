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

import java.util.Date;

import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiIndexDto;
import de.symeda.sormas.api.epipulse.EpipulseExportCriteria;
import de.symeda.sormas.api.epipulse.EpipulseExportIndexDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import elemental.json.JsonValue;

public class EpipulseExportGrid extends FilteredGrid<EpipulseExportIndexDto, EpipulseExportCriteria> {

	private DataProviderListener<EpipulseExportIndexDto> dataProviderListener;

	public static final String TOTAL_RECORDS_FORMATTED = "totalRecordsFormatted";
	public static final String EXPORT_FILE_SIZE_FORMATTED = "exportFileSizeFormatted";
	public static final String DOWNLOAD_LINK_COLUMN = "downloadLinkColumn";

	@SuppressWarnings("unchecked")
	public EpipulseExportGrid(EpipulseExportCriteria criteria) {
		super(EpipulseExportIndexDto.class);
		setSizeFull();

		setLazyDataProvider();
		setCriteria(criteria);

		Language userLanguage = I18nProperties.getUserLanguage();

		Column<EpipulseExportIndexDto, String> totalRecordsColumn = addColumn(entry -> {
			if (entry.getTotalRecords() == null) {
				return "-";
			} else {
				return String.format(userLanguage.getLocale(), "%,d", entry.getTotalRecords());
			}
		});
		totalRecordsColumn.setId(TOTAL_RECORDS_FORMATTED);
		totalRecordsColumn.setSortable(true);
		totalRecordsColumn.setCaption(I18nProperties.getCaption(Captions.epipulseTotalRecordsColumnCaption));

		Column<EpipulseExportIndexDto, String> fileSizeColumn = addColumn(entry -> {
			if (entry.getExportFileSize() == null) {
				return "-";
			} else {
				Long bytes = entry.getExportFileSize();

				if (bytes == null) {
					return "";
				}

				if (bytes < 1024) {
					return bytes + " B";
				} else if (bytes < 1024 * 1024) {
					double kb = bytes / 1024.0;
					return String.format(userLanguage.getLocale(), "%.2f KB", kb);
				} else {
					double mb = bytes / (1024.0 * 1024);
					return String.format(userLanguage.getLocale(), "%.2f MB", mb);
				}
			}
		});
		fileSizeColumn.setId(EXPORT_FILE_SIZE_FORMATTED);
		fileSizeColumn.setSortable(true);
		fileSizeColumn.setCaption(I18nProperties.getCaption(Captions.epipulseFileSizeColumnCaption));

		Column<EpipulseExportIndexDto, String> downloadColumn = addColumn(entry -> {
			if (entry.getStatus() != null && entry.getStatus() == EpipulseExportStatus.COMPLETED) {
				return I18nProperties.getString(Strings.epipulseDownloadLinkText);
			} else {
				return "-";
			}
		});
		downloadColumn.setId(DOWNLOAD_LINK_COLUMN);
		downloadColumn.setRenderer(new HtmlRenderer() {

			@Override
			public JsonValue encode(String value) {
				if ("-".equals(value)) {
					return super.encode(value);
				} else {
					return super.encode(HtmlHelper.buildHyperlinkTitle(value, value));
				}
			}
		});

		downloadColumn.setSortable(false);
		downloadColumn.setCaption(I18nProperties.getCaption(Captions.epipulseDownloadColumnCaption));

		setColumns(
			EpipulseExportIndexDto.UUID,
			EpipulseExportIndexDto.SUBJECT_CODE,
			EpipulseExportIndexDto.START_DATE,
			EpipulseExportIndexDto.END_DATE,
			EpipulseExportIndexDto.CREATION_DATE,
			EpipulseExportIndexDto.STATUS,
			TOTAL_RECORDS_FORMATTED,
			EXPORT_FILE_SIZE_FORMATTED,
			DOWNLOAD_LINK_COLUMN);

		((Column<EpipulseExportIndexDto, String>) getColumn(EpipulseExportIndexDto.UUID)).setRenderer(new UuidRenderer());
		addItemClickListener(
			new ShowDetailsListener<>(AefiIndexDto.UUID, e -> ControllerProvider.getEpipulseExportController().view(e, this::reload)));

		((Column<EpipulseExportIndexDto, Date>) getColumn(EpipulseExportIndexDto.START_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));

		((Column<EpipulseExportIndexDto, Date>) getColumn(EpipulseExportIndexDto.END_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));

		((Column<EpipulseExportIndexDto, Date>) getColumn(EpipulseExportIndexDto.CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		Column<EpipulseExportIndexDto, EpipulseExportStatus> exportStatusColumn =
			(Column<EpipulseExportIndexDto, EpipulseExportStatus>) getColumn(EpipulseExportIndexDto.STATUS);
		exportStatusColumn.setStyleGenerator(item -> {
			if (item.getStatus() != null) {
				switch (item.getStatus()) {
				case PENDING:
					return CssStyles.GRID_CELL_STATUS_PENDING;
				case IN_PROGRESS:
					return CssStyles.GRID_CELL_STATUS_IN_PROGRESS;
				case COMPLETED:
					return CssStyles.GRID_CELL_STATUS_COMPLETED;
				case FAILED:
					return CssStyles.GRID_CELL_STATUS_FAILED;
				case CANCELLED:
					return CssStyles.GRID_CELL_STATUS_CANCELLED;
				default:
					return null;
				}
			}
			return null;
		});

		Column<EpipulseExportIndexDto, String> downloadColumnRef = (Column<EpipulseExportIndexDto, String>) getColumn(DOWNLOAD_LINK_COLUMN);
		downloadColumnRef.setStyleGenerator(item -> {
			if (item.getStatus() != null && !item.getStatus().equals(EpipulseExportStatus.COMPLETED)) {
				return CssStyles.GRID_CELL_LINK_DISABLED;
			}
			return null;
		});

		addItemClickListener(event -> {
			if (event.getItem() != null
				&& event.getColumn() != null
				&& DOWNLOAD_LINK_COLUMN.equals(event.getColumn().getId())
				&& event.getItem().getStatus() == EpipulseExportStatus.COMPLETED) {
				ControllerProvider.getEpipulseExportController().download(event.getItem());
			}
		});

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(EpipulseExportIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getEpipulseExportFacade()::getIndexList, FacadeProvider.getEpipulseExportFacade()::count);
	}
}
