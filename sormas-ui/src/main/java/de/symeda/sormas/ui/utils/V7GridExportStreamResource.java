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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVWriter;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Grid.Column;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.YesNoUnknown;

@SuppressWarnings("serial")
public class V7GridExportStreamResource extends StreamResource {

	public V7GridExportStreamResource(Indexed container, List<Column> gridColumns, String tempFilePrefix, String filename, String... ignoredPropertyIds) {
		super(new StreamSource() {
			@Override
			public InputStream getStream() {
				List<String> ignoredPropertyIdsList = Arrays.asList(ignoredPropertyIds);
				List<Column> columns = new ArrayList<>(gridColumns);
				columns.removeIf(c -> c.isHidden());
				columns.removeIf(c -> ignoredPropertyIdsList.contains(c.getPropertyId()));
				Collection<?> itemIds = container.getItemIds();

				List<String> headerRow = new ArrayList<>();
				columns.forEach(c -> {
					headerRow.add(c.getHeaderCaption());
				});

				try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
					try (CSVWriter writer = CSVUtils.createCSVWriter(new OutputStreamWriter(byteStream, StandardCharsets.UTF_8.name()), FacadeProvider.getConfigFacade().getCsvSeparator())) {
		
						writer.writeNext(headerRow.toArray(new String[headerRow.size()]));
						
						itemIds.forEach(i -> {
							List<String> row = new ArrayList<>();
							columns.forEach(c -> {
								Property<?> property = container.getItem(i).getItemProperty(c.getPropertyId());
								if (property.getValue() != null) {
									if (property.getType() == Date.class) {
										row.add(DateFormatHelper.formatLocalDateTime((Date) property.getValue()));
									} else if (property.getType() == Boolean.class) {
										if ((Boolean) property.getValue() == true) {
											row.add(I18nProperties.getEnumCaption(YesNoUnknown.YES));
										} else
											row.add(I18nProperties.getEnumCaption(YesNoUnknown.NO));
									} else {
										row.add(property.getValue().toString());
									}
								} else {
									row.add("");
								}
							});
		
							writer.writeNext(row.toArray(new String[row.size()]));
						});
		
						writer.flush();
					}					
					return new ByteArrayInputStream(byteStream.toByteArray());
				} catch (IOException e) {
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					new Notification(I18nProperties.getString(Strings.headingExportFailed), I18nProperties.getString(Strings.messageExportFailed),
							Type.ERROR_MESSAGE, false).show(Page.getCurrent());
					return null;
				}
			}
		}, filename);
		setMIMEType("text/csv");
		setCacheTime(0);
	}
	
}