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

package de.symeda.sormas.backend.info;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.info.InfoFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.XssfHelper;

@Stateless(name = "InfoFacade")
public class InfoFacadeEjb implements InfoFacade {

	public static final String DATA_PROTECTION_FILE_NAME = "DataProtectionInfo.xlsx";
	public static final int FIRST_DATA_PROTECTION_COLUMN_INDEX = 10;

	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private UserService userService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

	@Override
	public String generateDataDictionary() throws IOException {
		return generateDataDictionary(
			EnumSet.allOf(EntityColumn.class),
			FieldVisibilityCheckers.getNoop(),
			Collections.emptyList(),
			Collections.emptyMap());
	}

	@Override
	public boolean isGenerateDataProtectionDictionaryAllowed() {
		return userService.hasRight(UserRight.EXPORT_DATA_PROTECTION_DATA) && getDataProtectionFile().exists();
	}

	@Override
	public String generateDataProtectionDictionary() throws IOException {
		FieldVisibilityCheckers fieldVisibilityCheckers = FieldVisibilityCheckers.withCheckers(
			new CountryFieldVisibilityChecker(configFacade.getCountryLocale()),
			new DiseaseFieldVisibilityChecker(diseaseConfigurationFacade.getAllActiveDiseases()));

		try {
			XSSFWorkbook dataProtectionInputWorkbook = new XSSFWorkbook(getDataProtectionFile());
			XSSFSheet dataProtectionSheet = dataProtectionInputWorkbook.getSheetAt(0);

			List<ColumnData> dataProtectionColumns = getDataProtectionColumns(dataProtectionSheet);
			Map<String, List<XSSFCell>> dataProtectionData = getDataProtectionCellData(dataProtectionSheet);
			EnumSet<EntityColumn> entityColumns = EnumSet.allOf(EntityColumn.class);
			entityColumns.remove(EntityColumn.IGNORED_COUNTRIES);
			entityColumns.remove(EntityColumn.EXCLUSIVE_COUNTRIES);

			return generateDataDictionary(entityColumns, fieldVisibilityCheckers, dataProtectionColumns, dataProtectionData);

		} catch (InvalidFormatException e) {
			throw new IOException(e);
		}
	}

	private String generateDataDictionary(
		EnumSet<EntityColumn> entityColumns,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		List<ColumnData> extraColumns,
		Map<String, List<XSSFCell>> extraCells)
		throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();

		createEntitySheet(workbook, PersonDto.class, PersonDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			PersonContactDetailDto.class,
			PersonContactDetailDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, LocationDto.class, LocationDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, CaseDataDto.class, CaseDataDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			ActivityAsCaseDto.class,
			ActivityAsCaseDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(
			workbook,
			HospitalizationDto.class,
			HospitalizationDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(
			workbook,
			PreviousHospitalizationDto.class,
			PreviousHospitalizationDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(
			workbook,
			SurveillanceReportDto.class,
			SurveillanceReportDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, SymptomsDto.class, SymptomsDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, EpiDataDto.class, EpiDataDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, ExposureDto.class, ExposureDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			HealthConditionsDto.class,
			HealthConditionsDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(
			workbook,
			PrescriptionDto.class,
			PrescriptionDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, TreatmentDto.class, TreatmentDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			ClinicalVisitDto.class,
			ClinicalVisitDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, ContactDto.class, ContactDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, VisitDto.class, VisitDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, SampleDto.class, SampleDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			PathogenTestDto.class,
			PathogenTestDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(
			workbook,
			AdditionalTestDto.class,
			AdditionalTestDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, TaskDto.class, TaskDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, EventDto.class, EventDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			EventParticipantDto.class,
			EventParticipantDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, ActionDto.class, ActionDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(
			workbook,
			VaccinationDto.class,
			VaccinationDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(
			workbook,
			TravelEntryDto.class,
			TravelEntryDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, ContinentDto.class, ContinentDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			SubcontinentDto.class,
			SubcontinentDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, CountryDto.class, CountryDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, RegionDto.class, RegionDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, DistrictDto.class, DistrictDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, CommunityDto.class, CommunityDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, FacilityDto.class, FacilityDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(
			workbook,
			PointOfEntryDto.class,
			PointOfEntryDto.I18N_PREFIX,
			entityColumns,
			fieldVisibilityCheckers,
			extraColumns,
			extraCells);
		createEntitySheet(workbook, UserDto.class, UserDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, LabMessageDto.class, LabMessageDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);
		createEntitySheet(workbook, TestReportDto.class, TestReportDto.I18N_PREFIX, entityColumns, fieldVisibilityCheckers, extraColumns, extraCells);

		XssfHelper.addAboutSheet(workbook);

		Path documentPath = generateDocumentTempPath();
		try (OutputStream fos = Files.newOutputStream(documentPath)) {
			workbook.write(fos);
			workbook.close();
		} catch (IOException e) {
			Files.deleteIfExists(documentPath);
			throw e;
		}

		return documentPath.toString();
	}

	private void createEntitySheet(
		XSSFWorkbook workbook,
		Class<? extends EntityDto> entityClass,
		String i18nPrefix,
		EnumSet<EntityColumn> entityColumns,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		List<ColumnData> extraColumns,
		Map<String, List<XSSFCell>> extraCells) {

		String name = I18nProperties.getCaption(i18nPrefix);
		String safeName = WorkbookUtil.createSafeSheetName(name);
		XSSFSheet sheet = workbook.createSheet(safeName);

		int columnCount = entityColumns.size() + extraColumns.size();
		int rowNumber = 0;

		// header
		XSSFRow headerRow = sheet.createRow(rowNumber++);
		entityColumns.forEach(column -> {
			int colIndex = Math.max(headerRow.getLastCellNum(), 0);
			headerRow.createCell(colIndex).setCellValue(column.toString());
			sheet.setColumnWidth(colIndex, column.getWidth());
		});

		extraColumns.forEach(c -> {
			short colIndex = headerRow.getLastCellNum();
			headerRow.createCell(colIndex).setCellValue(c.header);
			sheet.setColumnWidth(colIndex, c.width);
		});

		CellStyle defaultCellStyle = workbook.createCellStyle();
		defaultCellStyle.setWrapText(true);

		List<Class<Enum<?>>> usedEnums = new ArrayList<>();
		boolean usesFacilityReference = false;

		for (Field field : entityClass.getDeclaredFields()) {
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || !fieldVisibilityCheckers.isVisible(entityClass, field.getName())) {
				continue;
			}

			FieldData fieldData = new FieldData(field, entityClass, i18nPrefix);

			XSSFRow row = sheet.createRow(rowNumber++);

			for (EntityColumn c : entityColumns) {
				XSSFCell newCell = row.createCell(Math.max(row.getLastCellNum(), 0));

				String fieldValue = c.getGetValueFromField(fieldData);
				if (fieldValue != null) {
					newCell.setCellValue(fieldValue);
				}

				if (c.hasDefaultStyle()) {
					newCell.setCellStyle(defaultCellStyle);
				}

				Class<?> fieldType = field.getType();
				if (fieldType.isEnum()) {
					if (!usedEnums.contains(fieldType)) {
						@SuppressWarnings("unchecked")
						Class<Enum<?>> enumType = (Class<Enum<?>>) fieldType;
						usedEnums.add(enumType);
					}
				} else if (Map.class.isAssignableFrom(fieldType)) {
					getEnumGenericsOf(field, Map.class).filter(e -> !usedEnums.contains(e)).collect(Collectors.toCollection(() -> usedEnums));
				} else if (Collection.class.isAssignableFrom(fieldType)) {
					getEnumGenericsOf(field, Collection.class).filter(e -> !usedEnums.contains(e)).collect(Collectors.toCollection(() -> usedEnums));
				} else if (FacilityReferenceDto.class.isAssignableFrom(fieldType)) {
					usesFacilityReference = true;
				}
			}

			String fieldId = EntityColumn.FIELD_ID.getGetValueFromField(fieldData);
			if (extraCells.containsKey(fieldId)) {
				extraCells.get(fieldId).forEach((extraCell) -> {
					XSSFCell newCell = row.createCell(row.getLastCellNum());
					if (extraCell != null) {
						newCell.copyCellFrom(extraCell, new CellCopyPolicy.Builder().cellValue(true).cellStyle(false).cellFormula(false).build());
					}
				});
			}
		}

		// Configure table
		AreaReference reference =
			workbook.getCreationHelper().createAreaReference(new CellReference(0, 0), new CellReference(rowNumber - 1, columnCount - 1));
		XssfHelper.configureTable(reference, getSafeTableName(safeName), sheet, XssfHelper.TABLE_STYLE_PRIMARY);

		// constant facilities
		if (usesFacilityReference) {
			rowNumber = createFacilityTable(sheet, rowNumber + 1, defaultCellStyle);
		}

		// enums
		for (Class<Enum<?>> usedEnum : usedEnums) {
			rowNumber = createEnumTable(sheet, rowNumber + 1, usedEnum, fieldVisibilityCheckers);
		}

	}

	private Stream<Class<Enum<?>>> getEnumGenericsOf(Field field, Class<?> toClass) {
		return TypeUtils.getTypeArguments(field.getGenericType(), toClass)
			.values()
			.stream()
			.distinct()
			.map(cls -> (Class<?>) cls)
			.filter(Class::isEnum)
			.map(cls -> {
				@SuppressWarnings("unchecked")
				Class<Enum<?>> enumType = (Class<Enum<?>>) cls;
				return enumType;
			});

	}

	private int createFacilityTable(XSSFSheet sheet, int startRow, CellStyle defaultCellStyle) {

		int columnCount = EnumColumn.values().length - 1;
		int rowNumber = startRow;

		// header
		XSSFRow headerRow = sheet.createRow(rowNumber++);
		for (EnumColumn column : EnumColumn.values()) {
			if (EnumColumn.SHORT.equals(column)) {
				continue;
			}
			String columnCaption = column.toString();
			columnCaption = columnCaption.charAt(0) + columnCaption.substring(1).toLowerCase();
			headerRow.createCell(column.ordinal()).setCellValue(columnCaption);
		}

		List<String> constantFacilities = Arrays.asList(FacilityDto.OTHER_FACILITY, FacilityDto.NO_FACILITY, FacilityDto.CONFIGURED_FACILITY);
		for (String constantFacility : constantFacilities) {
			XSSFRow row = sheet.createRow(rowNumber++);
			XSSFCell cell;

			cell = row.createCell(EnumColumn.TYPE.ordinal());
			if (constantFacility.equals(constantFacilities.get(0))) {
				cell.setCellValue(DataHelper.getHumanClassName(FacilityReferenceDto.class));
			}

			cell = row.createCell(EnumColumn.VALUE.ordinal());
			cell.setCellValue(FacilityDto.CONFIGURED_FACILITY.equals(constantFacility) ? "<text>" : constantFacility);

			cell = row.createCell(EnumColumn.CAPTION.ordinal());
			String caption = I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, constantFacility);
			cell.setCellValue(caption);

			cell = row.createCell(EnumColumn.DESCRIPTION.ordinal());
			cell.setCellStyle(defaultCellStyle);
			String desc = I18nProperties.getPrefixDescription(FacilityDto.I18N_PREFIX, constantFacility);
			cell.setCellValue(DataHelper.equal(caption, desc) ? "" : desc);
		}

		// Configure table
		AreaReference reference =
			new AreaReference(new CellReference(startRow, 0), new CellReference(rowNumber - 1, columnCount - 1), SpreadsheetVersion.EXCEL2007);
		String safeTableName = getSafeTableName(sheet.getSheetName() + DataHelper.getHumanClassName(FacilityReferenceDto.class));
		XssfHelper.configureTable(reference, safeTableName, sheet, XssfHelper.TABLE_STYLE_SECONDARY);

		return rowNumber;
	}

	private enum EnumColumn {

		TYPE,
		VALUE,
		CAPTION,
		DESCRIPTION,
		SHORT;

		public String toString() {
			return I18nProperties.getEnumCaption(this);
		}

	}

	private int createEnumTable(XSSFSheet sheet, int startRow, Class<Enum<?>> enumType, FieldVisibilityCheckers fieldVisibilityCheckers) {

		int columnCount = EnumColumn.values().length;
		int rowNumber = startRow;

		// header
		XSSFRow headerRow = sheet.createRow(rowNumber++);
		for (EnumColumn column : EnumColumn.values()) {
			String columnCaption = column.toString();
			columnCaption = columnCaption.charAt(0) + columnCaption.substring(1).toLowerCase();
			headerRow.createCell(column.ordinal()).setCellValue(columnCaption);
		}

		Enum<?>[] enumValues = enumType.getEnumConstants();
		for (Enum<?> enumValue : enumValues) {
			if(!fieldVisibilityCheckers.isVisible(enumType, enumValue.name())){
				continue;
			}

			XSSFRow row = sheet.createRow(rowNumber++);
			XSSFCell cell;

			cell = row.createCell(EnumColumn.TYPE.ordinal());
			if (enumValue == enumValues[0]) {
				cell.setCellValue(enumType.getSimpleName());
			}

			cell = row.createCell(EnumColumn.VALUE.ordinal());
			cell.setCellValue(enumValue.name());

			cell = row.createCell(EnumColumn.CAPTION.ordinal());
			String caption = enumValue.toString();
			cell.setCellValue(caption);

			cell = row.createCell(EnumColumn.DESCRIPTION.ordinal());
			String desc = I18nProperties.getEnumDescription(enumValue);
			cell.setCellValue(DataHelper.equal(caption, desc) ? "" : desc);

			cell = row.createCell(EnumColumn.SHORT.ordinal());
			String shortCaption = I18nProperties.getEnumCaptionShort(enumValue);
			cell.setCellValue(DataHelper.equal(caption, shortCaption) ? "" : shortCaption);
		}

		// Configure table
		AreaReference reference =
			new AreaReference(new CellReference(startRow, 0), new CellReference(rowNumber - 1, columnCount - 1), SpreadsheetVersion.EXCEL2007);
		String safeTableName = getSafeTableName(sheet.getSheetName() + enumType.getSimpleName());
		XssfHelper.configureTable(reference, safeTableName, sheet, XssfHelper.TABLE_STYLE_SECONDARY);

		return rowNumber;
	}

	private File getDataProtectionFile() {
		return new File(configFacade.getCustomFilesPath(), DATA_PROTECTION_FILE_NAME);
	}

	private Map<String, List<XSSFCell>> getDataProtectionCellData(XSSFSheet dataProtectionSheet) {
		int numRows = dataProtectionSheet.getPhysicalNumberOfRows();
		Map<String, List<XSSFCell>> dataProtectionData = new HashMap<>(numRows - 1);

		for (int rowNum = 1; rowNum < numRows; rowNum++) {
			XSSFRow row = dataProtectionSheet.getRow(rowNum);

			String fieldId = row.getCell(0).getStringCellValue();

			List<XSSFCell> dataProtectionCells = new ArrayList<>();
			for (short colNum = FIRST_DATA_PROTECTION_COLUMN_INDEX; colNum < row.getLastCellNum(); colNum++) {
				XSSFCell rowCell = row.getCell(colNum);
				dataProtectionCells.add(rowCell);
			}

			dataProtectionData.put(fieldId, dataProtectionCells);
		}

		return dataProtectionData;
	}

	private List<ColumnData> getDataProtectionColumns(XSSFSheet dataProtectionSheet) {
		XSSFRow headerRow = dataProtectionSheet.getRow(0);
		short lastCellNum = headerRow.getLastCellNum();

		List<ColumnData> dataProtectionColumns = new ArrayList<>();
		for (short colNum = FIRST_DATA_PROTECTION_COLUMN_INDEX; colNum < lastCellNum; colNum++) {
			dataProtectionColumns.add(new ColumnData(headerRow.getCell(colNum).getStringCellValue(), dataProtectionSheet.getColumnWidth(colNum)));
		}

		return dataProtectionColumns;
	}

	private Path generateDocumentTempPath() {

		Path path = Paths.get(configFacade.getTempFilesPath());
		String fileName = ImportExportUtils.TEMP_FILE_PREFIX + "_datadictionary_" + DateHelper.formatDateForExport(new Date()) + "_"
			+ new Random().nextInt(Integer.MAX_VALUE) + ".xlsx";

		return path.resolve(fileName);
	}

	private String getSafeTableName(String name) {
		return name.replaceAll("\\s|\\p{Punct}", "_");
	}

	@LocalBean
	@Stateless
	public static class InfoFacadeEjbLocal extends InfoFacadeEjb {

	}
	private static class ColumnData {

		private String header;
		private int width;

		public ColumnData(String header, int width) {
			this.header = header;
			this.width = width;
		}
	}
}
