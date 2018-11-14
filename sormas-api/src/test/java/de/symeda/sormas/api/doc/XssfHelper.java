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
