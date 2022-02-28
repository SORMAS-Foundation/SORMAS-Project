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

package de.symeda.sormas.backend.user;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

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

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRightsFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.util.XssfHelper;

@Stateless(name = "UserRightsFacade")
public class UserRightsFacadeEjb implements UserRightsFacade {

	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public String generateUserRightsDocument(boolean withDbOverrides) throws IOException {
		Path documentPath = generateUserRightsDocumentTempPath();

		if (Files.exists(documentPath)) {
			throw new IOException("File already exists: " + documentPath);
		}

		try (OutputStream fos = Files.newOutputStream(documentPath)) {
			generateUserRightsDocument(withDbOverrides ? userRoleConfigFacade.getAllAsMap() : new HashMap<>(0), fos);
		} catch (IOException e) {
			Files.deleteIfExists(documentPath);
			throw e;
		}

		return documentPath.toString();
	}

	private void generateUserRightsDocument(Map<UserRole, Set<UserRight>> userRoleRights, OutputStream outStream) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create User Rights sheet
		String safeName = WorkbookUtil.createSafeSheetName(I18nProperties.getCaption(Captions.userRights));
		XSSFSheet sheet = workbook.createSheet(safeName);

		// Define colors
		final XSSFColor green = XssfHelper.createColor(0, 153, 0);
		final XSSFColor red = XssfHelper.createColor(255, 0, 0);
		final XSSFColor black = XssfHelper.createColor(0, 0, 0);

		// Initialize cell styles
		// Authorized style
		XSSFCellStyle authorizedStyle = workbook.createCellStyle();
		authorizedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		authorizedStyle.setFillForegroundColor(green);
		authorizedStyle.setBorderBottom(BorderStyle.THIN);
		authorizedStyle.setBorderLeft(BorderStyle.THIN);
		authorizedStyle.setBorderTop(BorderStyle.THIN);
		authorizedStyle.setBorderRight(BorderStyle.THIN);
		authorizedStyle.setBorderColor(BorderSide.BOTTOM, black);
		authorizedStyle.setBorderColor(BorderSide.LEFT, black);
		authorizedStyle.setBorderColor(BorderSide.TOP, black);
		authorizedStyle.setBorderColor(BorderSide.RIGHT, black);
		// Unauthorized style
		XSSFCellStyle unauthorizedStyle = workbook.createCellStyle();
		unauthorizedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		unauthorizedStyle.setFillForegroundColor(red);
		unauthorizedStyle.setBorderBottom(BorderStyle.THIN);
		unauthorizedStyle.setBorderLeft(BorderStyle.THIN);
		unauthorizedStyle.setBorderTop(BorderStyle.THIN);
		unauthorizedStyle.setBorderRight(BorderStyle.THIN);
		unauthorizedStyle.setBorderColor(BorderSide.BOTTOM, black);
		unauthorizedStyle.setBorderColor(BorderSide.LEFT, black);
		unauthorizedStyle.setBorderColor(BorderSide.TOP, black);
		unauthorizedStyle.setBorderColor(BorderSide.RIGHT, black);
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

	private Path generateUserRightsDocumentTempPath() {

		Path path = Paths.get(configFacade.getTempFilesPath());
		String fileName = ImportExportUtils.TEMP_FILE_PREFIX + "_userrights_" + DateHelper.formatDateForExport(new Date()) + "_"
			+ new Random().nextInt(Integer.MAX_VALUE) + ".xlsx";

		return path.resolve(fileName);
	}

	@LocalBean
	@Stateless
	public static class UserRightsFacadeEjbLocal extends UserRightsFacadeEjb {

	}
}
