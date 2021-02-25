/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.CSVWriter;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.CSVCommentLineValidator;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.YesNoUnknown;

@SuppressWarnings("serial")
public class GridExportStreamResource {

	public static StreamResource createStreamResource(Grid<?> grid, ExportEntityName entityName, String... excludePropertyIds) {
		return new GridExportStreamResource(grid, entityName, excludePropertyIds).getStreamResource();
	}

	public static StreamResource createStreamResource(Grid<?> grid, ExportEntityName entityName, List<String> excludePropertyIds, List<String> includePropertyIds) {
		return new GridExportStreamResource(grid, entityName, excludePropertyIds, includePropertyIds).getStreamResource();
	}

	private StreamResource streamResource;

	private GridExportStreamResource(Grid<?> grid, ExportEntityName entityName, String... excludePropertyIds) {
		this(grid, entityName, Arrays.asList(excludePropertyIds), Collections.emptyList());
	}

	private GridExportStreamResource(Grid<?> grid, ExportEntityName entityName, List<String> excludePropertyIds, List<String> includePropertyIds) {
		String filename = DownloadUtil.createFileNameWithCurrentDate(entityName, ".csv");
		GridExportStreamSource streamSource = new GridExportStreamSource(grid, excludePropertyIds, includePropertyIds);
		this.streamResource = new StreamResource(streamSource, filename);
		this.streamResource.setMIMEType("text/csv");
		this.streamResource.setCacheTime(0);
	}

	private StreamResource getStreamResource() {
		return this.streamResource;
	}
	
	private static class GridExportStreamSource implements StreamResource.StreamSource {

		private Grid<?> grid;
		private List<String> excludePropertyIds;
		private List<String> includePropertyIds;

		GridExportStreamSource(Grid<?> grid, List<String> excludePropertyIds, List<String> includePropertyIds) {
			this.grid = grid;
			this.excludePropertyIds = excludePropertyIds;
			this.includePropertyIds = includePropertyIds;
		}
		
		@Override
		public InputStream getStream() {
			List<Column> columns = getGridColumns();
			ValueProvider[] columnValueProviders = columns.stream().map(Column::getValueProvider).toArray(ValueProvider[]::new);
			String[] headerRow = columns.stream().map(Column::getId).toArray(String[]::new);
			String[]  labelsRow = columns.stream().map(Column::getCaption).toArray(String[]::new);
			labelsRow[0] = CSVCommentLineValidator.DEFAULT_COMMENT_LINE_PREFIX + labelsRow[0];
			DataProvider<?, ?> dataProvider = grid.getDataProvider();
			List<QuerySortOrder> sortOrder = getGridSortOrder();

			try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				 CSVWriter writer = createCsvWriter(byteStream)) {
					writer.writeNext(headerRow);
					writer.writeNext(labelsRow, false);

					String[] rowValues = new String[columnValueProviders.length];

					int totalRowCount = dataProvider.size(new Query());
					for (int i = 0; i < totalRowCount; i += 100) {
						dataProvider.fetch(new Query(i, 100, sortOrder, null, null)).forEach(row -> {
							for (int c = 0; c < columnValueProviders.length; c++) {
								Object value = columnValueProviders[c].apply(row);

								final String valueString;
								if (value == null) {
									valueString = "";
								} else if (value instanceof Date) {
									valueString = DateFormatHelper.formatLocalDateTime((Date) value);
								} else if (value instanceof Boolean) {
									if ((Boolean) value) {
										valueString = I18nProperties.getEnumCaption(YesNoUnknown.YES);
									} else
										valueString = I18nProperties.getEnumCaption(YesNoUnknown.NO);
								} else if (value instanceof AgeAndBirthDateDto) {
									AgeAndBirthDateDto ageAndBirthDate = (AgeAndBirthDateDto) value;
									valueString = PersonHelper.getAgeAndBirthdateString(
											ageAndBirthDate.getAge(),
											ageAndBirthDate.getAgeType(),
											ageAndBirthDate.getBirthdateDD(),
											ageAndBirthDate.getBirthdateMM(),
											ageAndBirthDate.getBirthdateYYYY(),
											I18nProperties.getUserLanguage());
								} else if (value instanceof Label) {
									valueString = ((Label) value).getValue();
								} else {
									valueString = value.toString();
								}
								rowValues[c] = valueString;
							}
							writer.writeNext(rowValues);
						});
						writer.flush();
					}
				return new ByteArrayInputStream(byteStream.toByteArray());
			} catch (IOException e) {
				// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
				// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
				new Notification(
						I18nProperties.getString(Strings.headingExportFailed),
						I18nProperties.getString(Strings.messageExportFailed),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				return null;
			}
		}

		private List<Column> getGridColumns() {
			return grid.getColumns()
					.stream()
					.filter(column -> !column.isHidden() || includePropertyIds.contains(column.getId()))
					.filter(column -> !excludePropertyIds.contains(column.getId()))
					.collect(Collectors.toList());
		}

		private List<QuerySortOrder> getGridSortOrder() {
			return grid.getSortOrder()
					.stream()
					.flatMap(
							gridSortOrder -> grid.getColumns()
									.stream()
									.filter(column -> column.getId().equals(gridSortOrder.getSorted().getId()))
									.findFirst()
									.get()
									.getSortOrder(gridSortOrder.getDirection()))
					.collect(Collectors.toList());
		}

		private CSVWriter createCsvWriter(ByteArrayOutputStream byteStream) throws UnsupportedEncodingException {
			return CSVUtils.createCSVWriter(
					new OutputStreamWriter(byteStream, StandardCharsets.UTF_8.name()),
					FacadeProvider.getConfigFacade().getCsvSeparator());
		}
	}
}
