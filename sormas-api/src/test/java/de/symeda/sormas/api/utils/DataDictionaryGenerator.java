package de.symeda.sormas.api.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Date;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.visit.VisitDto;

public class DataDictionaryGenerator {

	@Test
	public void generateDataDictionary() throws FileNotFoundException, IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();

		createEntitySheet(workbook, PersonDto.class, PersonDto.I18N_PREFIX);
		createEntitySheet(workbook, LocationDto.class, LocationDto.I18N_PREFIX);
		createEntitySheet(workbook, CaseDataDto.class, CaseDataDto.I18N_PREFIX).setSelected(true);
		createEntitySheet(workbook, HospitalizationDto.class, HospitalizationDto.I18N_PREFIX);
		createEntitySheet(workbook, SymptomsDto.class, SymptomsDto.I18N_PREFIX);
		createEntitySheet(workbook, EpiDataDto.class, EpiDataDto.I18N_PREFIX);
		createEntitySheet(workbook, ContactDto.class, ContactDto.I18N_PREFIX);
		createEntitySheet(workbook, VisitDto.class, VisitDto.I18N_PREFIX);
		createEntitySheet(workbook, SampleDto.class, SampleDto.I18N_PREFIX);
		createEntitySheet(workbook, SampleTestDto.class, SampleTestDto.I18N_PREFIX);
		createEntitySheet(workbook, TaskDto.class, TaskDto.I18N_PREFIX);
		createEntitySheet(workbook, FacilityDto.class, FacilityDto.I18N_PREFIX);
		createEntitySheet(workbook, UserDto.class, UserDto.I18N_PREFIX);
		createEntitySheet(workbook, RegionDto.class, RegionDto.I18N_PREFIX);
		createEntitySheet(workbook, DistrictDto.class, DistrictDto.I18N_PREFIX);
		createEntitySheet(workbook, CommunityDto.class, CommunityDto.I18N_PREFIX);

		String filePath = "../sormas-base/doc/SormasDictionary.xlsx";
		try (OutputStream fileOut = new FileOutputStream(filePath)) {
			workbook.write(fileOut);
		}
		Desktop.getDesktop().open(new File(filePath));
	}

	private enum Column {
		FIELD, VALUES, CAPTION, DESCRIPTION, REQUIRED, DISEASES, OUTBREAKS,
	}

	private XSSFSheet createEntitySheet(XSSFWorkbook workbook, Class<? extends EntityDto> entityClass,
			String i18nPrefix) {
		String name = I18nProperties.getFieldCaption(i18nPrefix);
		String safeName = WorkbookUtil.createSafeSheetName(name);
		XSSFSheet sheet = workbook.createSheet(safeName);		

		// Create
		XSSFTable table = sheet.createTable();
		String safeTableName = safeName.replaceAll("\\s", "_");
		table.setName(safeTableName);
		table.setDisplayName(safeTableName);

		// For now, create the initial style in a low-level way
		table.getCTTable().addNewTableStyleInfo();
		String tableStyleName = "TableStyleMedium" + workbook.getNumberOfSheets();
		table.getCTTable().getTableStyleInfo().setName(tableStyleName);

		// Style the table
		XSSFTableStyleInfo style = (XSSFTableStyleInfo) table.getStyle();
		style.setName(tableStyleName);
		style.setFirstColumn(false);
		style.setLastColumn(false);
		style.setShowRowStripes(true);
		style.setShowColumnStripes(false);

		int columnCount = Column.values().length;

		int rowCounter = 0;
		// header
		XSSFRow headerRow = sheet.createRow(rowCounter++);
		for (Column column : Column.values()) {
			table.addColumn();
			String columnCaption = column.toString();
			columnCaption = columnCaption.substring(0, 1) + columnCaption.substring(1).toLowerCase();
			headerRow.createCell(column.ordinal()).setCellValue(columnCaption);
		}

		// column width
		sheet.setColumnWidth(Column.FIELD.ordinal(), 256 * 30);
		sheet.setColumnWidth(Column.VALUES.ordinal(), 256 * 60);
		sheet.setColumnWidth(Column.CAPTION.ordinal(), 256 * 30);
		sheet.setColumnWidth(Column.DESCRIPTION.ordinal(), 256 * 60);
		sheet.setColumnWidth(Column.REQUIRED.ordinal(), 256 * 10);
		sheet.setColumnWidth(Column.DISEASES.ordinal(), 256 * 45);
		sheet.setColumnWidth(Column.OUTBREAKS.ordinal(), 256 * 10);
		
		CellStyle defaultCellStyle = workbook.createCellStyle(); 
		defaultCellStyle.setWrapText(true);
		
		for (Field field : entityClass.getDeclaredFields()) {
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers()))
				continue;
			XSSFRow row = sheet.createRow(rowCounter++);

			// field name
			XSSFCell fieldNameCell = row.createCell(Column.FIELD.ordinal());
			fieldNameCell.setCellValue(field.getName());

			// value range
			XSSFCell fieldValueCell = row.createCell(Column.VALUES.ordinal());
			fieldValueCell.setCellStyle(defaultCellStyle); 
			Class fieldType = field.getType();
			if (fieldType.isEnum()) {
				// enum
				Object[] enumValues = fieldType.getEnumConstants();
				StringBuilder valuesString = new StringBuilder();
				for (Object enumValue : enumValues) {
					if (valuesString.length() > 0)
						valuesString.append(", ");
					valuesString.append(((Enum) enumValue).name());
				}
				fieldValueCell.setCellValue(valuesString.toString());
			} else if (EntityDto.class.isAssignableFrom(fieldType)) {
				// entity
				fieldValueCell.setCellValue(fieldType.getSimpleName().replaceAll("Dto", ""));
			} else if (ReferenceDto.class.isAssignableFrom(fieldType)) {
				// reference
				fieldValueCell.setCellValue(fieldType.getSimpleName().replaceAll("Dto", ""));
			} else if (String.class.isAssignableFrom(fieldType)) {
				// string
				fieldValueCell.setCellValue("Text");
			} else if (Date.class.isAssignableFrom(fieldType)) {
				// date
				fieldValueCell.setCellValue("Date");
			} else if (Number.class.isAssignableFrom(fieldType)) {
				// date
				fieldValueCell.setCellValue("Number");
			} else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
				// date
				fieldValueCell.setCellValue(Boolean.TRUE.toString() + ", " + Boolean.FALSE.toString());
			}

			// caption
			XSSFCell captionCell = row.createCell(Column.CAPTION.ordinal());
			captionCell.setCellValue(I18nProperties.getPrefixFieldCaption(i18nPrefix, field.getName(), ""));

			// description
			XSSFCell descriptionCell = row.createCell(Column.DESCRIPTION.ordinal());
			descriptionCell.setCellStyle(defaultCellStyle); 
			descriptionCell.setCellValue(I18nProperties.getPrefixFieldDescription(i18nPrefix, field.getName(), ""));

			// required
			XSSFCell requiredCell = row.createCell(Column.REQUIRED.ordinal());
			if (field.getAnnotation(Required.class) != null)
				requiredCell.setCellValue(true);

			// diseases
			XSSFCell diseasesCell = row.createCell(Column.DISEASES.ordinal());
			diseasesCell.setCellStyle(defaultCellStyle); 
			Diseases diseases = field.getAnnotation(Diseases.class);
			if (diseases != null) {
				StringBuilder diseasesString = new StringBuilder();
				for (Disease disease : diseases.value()) {
					if (diseasesString.length() > 0)
						diseasesString.append(", ");
					diseasesString.append(disease.toShortString());
				}
				diseasesCell.setCellValue(diseasesString.toString());
			} else {
				diseasesCell.setCellValue("All");
			}

			// outbreak
			XSSFCell outbreakCell = row.createCell(Column.OUTBREAKS.ordinal());
			if (field.getAnnotation(Outbreaks.class) != null)
				outbreakCell.setCellValue(true);
		}

		AreaReference reference = workbook.getCreationHelper().createAreaReference(new CellReference(0, 0),
				new CellReference(rowCounter - 1, columnCount - 1));
		table.setCellReferences(reference);
		table.getCTTable().addNewAutoFilter();

		return sheet;
	}
}
