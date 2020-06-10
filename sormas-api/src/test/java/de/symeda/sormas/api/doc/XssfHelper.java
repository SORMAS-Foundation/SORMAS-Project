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
package de.symeda.sormas.api.doc;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.symeda.sormas.api.utils.InfoProvider;

public final class XssfHelper {

	public static void styleTable(XSSFTable table, int styleNumber) {

		// Style the table - can this be simplified?
		table.getCTTable().addNewTableStyleInfo();
		String tableStyleName = "TableStyleLight" + styleNumber;
		table.getCTTable().getTableStyleInfo().setName(tableStyleName);
		XSSFTableStyleInfo style = (XSSFTableStyleInfo) table.getStyle();
		style.setName(tableStyleName);
		style.setFirstColumn(false);
		style.setLastColumn(false);
		style.setShowRowStripes(true);
		style.setShowColumnStripes(false);
	}

	public static void addAboutSheet(XSSFWorkbook workbook) {

		XSSFSheet sheet = workbook.createSheet("About");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("SORMAS Version");

		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(InfoProvider.get().getVersion());
	}
}
