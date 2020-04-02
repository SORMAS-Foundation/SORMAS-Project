package com.hzi

public class TableContent {
	
	List<String> tableHeaders
	List<List<String>> tableRows
		
	def void addRowData(List<String> rowData) {
		getTableRows().add(rowData)
	}
	
	def int getNumberOfColumns() {
		return getTableHeaders().size()
	}
	
	def String getRowData(int rowIndex, int columnIndex) {
		return tableRows.get(rowIndex).get(columnIndex)
	}
	
	def List<String> getTableHeaders() {
		if (tableHeaders == null) {
			tableHeaders = new ArrayList<String>()
		}
		return tableHeaders;
	}
	
	def void setTableHeaders(List<String> tableHeaders) {
		this.tableHeaders = tableHeaders;
	}
	
	def List<List<String>> getTableRows(){
		if (tableRows == null) {
			tableRows = new ArrayList<List<String>>()
		}
		return tableRows
	}
}