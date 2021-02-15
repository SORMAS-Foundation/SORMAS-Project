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
import java.io.OutputStreamWriter;
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
public class GridExportStreamResource extends StreamResource {

	public GridExportStreamResource(Grid<?> grid, String filename, String... excludePropertyIds) {
		this(grid, filename, Arrays.asList(excludePropertyIds), Collections.emptyList());
	}

	public GridExportStreamResource(Grid<?> grid, String filename, List<String> excludePropertyIds, List<String> includePropertyIds) {

		super((StreamSource) () -> {

			ValueProvider[] columnValueProviders;
			String[] headerRow;
			String[] labelsRow;
			{
				List<Column> columns = grid.getColumns()
					.stream()
					.filter(c -> !c.isHidden() || includePropertyIds.contains(c.getId()))
					.filter(c -> !excludePropertyIds.contains(c.getId()))
					.collect(Collectors.toList());

				columnValueProviders = columns.stream().map(Column::getValueProvider).toArray(ValueProvider[]::new);

				headerRow = columns.stream().map(Column::getId).toArray(String[]::new);
				labelsRow = columns.stream().map(Column::getCaption).toArray(String[]::new);
				labelsRow[0] = CSVCommentLineValidator.DEFAULT_COMMENT_LINE_PREFIX + labelsRow[0];
			}

			DataProvider<?, ?> dataProvider = grid.getDataProvider();

			List<QuerySortOrder> sortOrder = grid.getSortOrder()
				.stream()
				.flatMap(
					gridSortOrder -> grid.getColumns()
						.stream()
						.filter(column -> column.getId().equals(gridSortOrder.getSorted().getId()))
						.findFirst()
						.get()
						.getSortOrder(gridSortOrder.getDirection()))
				.collect(Collectors.toList());

			try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
				try (CSVWriter writer = CSVUtils.createCSVWriter(
					new OutputStreamWriter(byteStream, StandardCharsets.UTF_8.name()),
					FacadeProvider.getConfigFacade().getCsvSeparator())) {

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
									if ((Boolean) value == true) {
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
		}, filename);
		setMIMEType("text/csv");
		setCacheTime(0);
	}
}
