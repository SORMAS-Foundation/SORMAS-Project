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
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.validation.constraints.Null;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class GridExportStreamResourceXLSX extends StreamResource {

    public GridExportStreamResourceXLSX(Grid<?> grid,
                                        String tempFilePrefix,
                                        String filename,
                                        String... ignoredPropertyIds) {

        super(new StreamSource() {
            private void writeDataRow(XSSFRow xssfRow,
                                      ValueProvider[] columnValueProviders,
                                      Object row,
                                      int i)
            {
                for (int c = 0; c < columnValueProviders.length; c++) {
                    Object value = columnValueProviders[c].apply(row);
                    XSSFCell xssfCell = xssfRow.createCell(c);
                    if (value == null) {
                        xssfCell.setCellValue("");
                    } else if (value instanceof Date) {
                        xssfCell.setCellValue((Date) value);
                    } else if (value instanceof Boolean) {
                        xssfCell.setCellValue((Boolean) value);
                    } else if (value instanceof AgeAndBirthDateDto) {
                        AgeAndBirthDateDto ageAndBirthDate = (AgeAndBirthDateDto) value;
                        xssfCell.setCellValue(PersonHelper.getAgeAndBirthdateString(
                                ageAndBirthDate.getAge(),
                                ageAndBirthDate.getAgeType(),
                                ageAndBirthDate.getBirthdateDD(),
                                ageAndBirthDate.getBirthdateMM(),
                                ageAndBirthDate.getBirthdateYYYY(),
                                I18nProperties.getUserLanguage()));
                    } else if (value instanceof Label) {
                        xssfCell.setCellValue(((Label) value).getValue());
                    } else {
                        xssfCell.setCellValue(value.toString());
                    }
                }
            }

            private void writeDataRows(XSSFSheet sheet,
                                       DataProvider<?, ?> dataProvider,
                                       ValueProvider[] columnValueProviders,
                                       List<QuerySortOrder> sortOrder) {
                int totalRowCount = dataProvider.size(new Query());
                for (int i = 0; i < totalRowCount; i += 100) {
                    int finalI = i;
                    dataProvider.fetch(new Query(i, 100, sortOrder, null, null)).forEach(row ->
                    {
                        writeDataRow(sheet.createRow(finalI), columnValueProviders, row, finalI);
                    }
                    );
                }
            }

            @Null
            private ByteArrayInputStream getByteArrayInputStream(
                    ValueProvider[] columnValueProviders,
                    String[] headerRow,
                    DataProvider<?, ?> dataProvider,
                    List<QuerySortOrder> sortOrder) {
                XSSFWorkbook workbook = null;
                try {
                    workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet();
                    XSSFTable table = sheet.createTable();
                    // write header rows
                    XSSFRow xssfRow = sheet.createRow(0);
                    for (int i = 0; i < headerRow.length; i++) {
                        table.addColumn();
                        xssfRow.createCell(i).setCellValue(headerRow[i]);
                    }
                    // write data rows
                    writeDataRows(sheet, dataProvider, columnValueProviders, sortOrder);
                    ByteArrayOutputStream documentInMemory = new ByteArrayOutputStream();
                    workbook.write(documentInMemory);
                    workbook.close();
                    return new ByteArrayInputStream(documentInMemory.toByteArray());
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

            @SuppressWarnings({
                    "rawtypes"})
            @Override
            public InputStream getStream() {

                ValueProvider[] columnValueProviders;
                String[] headerRow;
                {
                    List<String> ignoredPropertyIdsList = Arrays.asList(ignoredPropertyIds);
                    List<Column> columns = grid.getColumns()
                            .stream()
                            .filter(c -> !c.isHidden())
                            .filter(c -> !ignoredPropertyIdsList.contains(c.getId()))
                            .collect(Collectors.toList());

                    columnValueProviders = columns.stream().map(Column::getValueProvider).toArray(ValueProvider[]::new);

                    headerRow = columns.stream().map(c -> c.getCaption()).toArray(String[]::new);
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
                return getByteArrayInputStream(columnValueProviders, headerRow, dataProvider, sortOrder);
            }
        }, filename);

        setMIMEType(MimeTypes.XSLX.mimeType);

        setCacheTime(0);
    }
}
