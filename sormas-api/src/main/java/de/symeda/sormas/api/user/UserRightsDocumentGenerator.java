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

package de.symeda.sormas.api.user;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.XssfHelper;

public class UserRightsDocumentGenerator {

	public static void generate(OutputStream outStream) throws IOException {
		generate(FacadeProvider.getUserRoleConfigFacade().getAllAsMap(), outStream);
	}

	public static void generateDefault(OutputStream outStream) throws IOException {
		generate(new HashMap<UserRole, Set<UserRight>>(0), outStream);
	}

	private static void generate(Map<UserRole, Set<UserRight>> userRoleRights, OutputStream outStream) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create User Rights sheet
		String safeName = WorkbookUtil.createSafeSheetName(I18nProperties.getCaption(Captions.userRights));
		XSSFSheet sheet = workbook.createSheet(safeName);

		// Initialize cell styles
		// Authorized style
		XSSFCellStyle authorizedStyle = workbook.createCellStyle();
		authorizedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		authorizedStyle.setFillForegroundColor(new XSSFColor(new Color(0, 153, 0)));
		authorizedStyle.setBorderBottom(BorderStyle.THIN);
		authorizedStyle.setBorderLeft(BorderStyle.THIN);
		authorizedStyle.setBorderTop(BorderStyle.THIN);
		authorizedStyle.setBorderRight(BorderStyle.THIN);
		authorizedStyle.setBorderColor(BorderSide.BOTTOM, new XSSFColor(Color.BLACK));
		authorizedStyle.setBorderColor(BorderSide.LEFT, new XSSFColor(Color.BLACK));
		authorizedStyle.setBorderColor(BorderSide.TOP, new XSSFColor(Color.BLACK));
		authorizedStyle.setBorderColor(BorderSide.RIGHT, new XSSFColor(Color.BLACK));
		// Unauthorized style
		XSSFCellStyle unauthorizedStyle = workbook.createCellStyle();
		unauthorizedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		unauthorizedStyle.setFillForegroundColor(new XSSFColor(Color.RED));
		unauthorizedStyle.setBorderBottom(BorderStyle.THIN);
		unauthorizedStyle.setBorderLeft(BorderStyle.THIN);
		unauthorizedStyle.setBorderTop(BorderStyle.THIN);
		unauthorizedStyle.setBorderRight(BorderStyle.THIN);
		unauthorizedStyle.setBorderColor(BorderSide.BOTTOM, new XSSFColor(Color.BLACK));
		unauthorizedStyle.setBorderColor(BorderSide.LEFT, new XSSFColor(Color.BLACK));
		unauthorizedStyle.setBorderColor(BorderSide.TOP, new XSSFColor(Color.BLACK));
		unauthorizedStyle.setBorderColor(BorderSide.RIGHT, new XSSFColor(Color.BLACK));
		// Bold style
		XSSFFont boldFont = workbook.createFont();
		boldFont.setBold(true);
		XSSFCellStyle boldStyle = workbook.createCellStyle();
		boldStyle.setFont(boldFont);

		int rowCounter = 0;

		// Header
		Row headerRow = sheet.createRow(rowCounter++);
		Cell userRightHeadlineCell = headerRow.createCell(0);
		userRightHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.userRight));
		userRightHeadlineCell.setCellStyle(boldStyle);
		Cell descHeadlineCell = headerRow.createCell(1);
		descHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRight_description));
		descHeadlineCell.setCellStyle(boldStyle);
		sheet.setColumnWidth(0, 256 * 35);
		sheet.setColumnWidth(1, 256 * 50);
		for (UserRole userRole : UserRole.values()) {
			String columnCaption = userRole.toString();
			Cell headerCell = headerRow.createCell(userRole.ordinal() + 2);
			headerCell.setCellValue(columnCaption);
			headerCell.setCellStyle(boldStyle);
			sheet.setColumnWidth(userRole.ordinal() + 2, 256 * 14);
		}

		// Jurisdiction row (header)
		final Row jurisdictionRow = sheet.createRow(rowCounter++);
		final Cell jurisdictionHeadlineCell = jurisdictionRow.createCell(0);
		jurisdictionHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRight_jurisdiction));
		jurisdictionHeadlineCell.setCellStyle(boldStyle);
		final Cell jurDescHeadlineCell = jurisdictionRow.createCell(1);
		jurDescHeadlineCell.setCellValue(I18nProperties.getCaption(Captions.UserRight_jurisdictionOfRole));
		jurDescHeadlineCell.setCellStyle(boldStyle);
		for (UserRole userRole : UserRole.values()) {
			final String columnCaption = userRole.getJurisdictionLevel().toString();
			final Cell headerCell = jurisdictionRow.createCell(userRole.ordinal() + 2);
			headerCell.setCellValue(columnCaption);
			headerCell.setCellStyle(boldStyle);
		}

		// User right rows
		for (UserRight userRight : UserRight.values()) {
			Row row = sheet.createRow(rowCounter++);

			// User right name
			Cell nameCell = row.createCell(0);
			nameCell.setCellValue(userRight.name());
			nameCell.setCellStyle(boldStyle);

			// User right description
			Cell descCell = row.createCell(1);
			descCell.setCellValue(userRight.toString());

			// Add styled cells for all user roles
			for (UserRole userRole : UserRole.values()) {
				Cell roleRightCell = row.createCell(userRole.ordinal() + 2);
				if (userRoleRights.containsKey(userRole) && userRoleRights.get(userRole).contains(userRight) || userRole.hasDefaultRight(userRight)) {
					roleRightCell.setCellStyle(authorizedStyle);
					roleRightCell.setCellValue(I18nProperties.getString(Strings.yes));
				} else {
					roleRightCell.setCellStyle(unauthorizedStyle);
					roleRightCell.setCellValue(I18nProperties.getString(Strings.no));
				}
			}
		}

		XssfHelper.addAboutSheet(workbook);

		workbook.write(outStream);
		workbook.close();
	}
}
