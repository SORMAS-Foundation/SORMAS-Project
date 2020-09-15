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

	def String getRowData(int rowIndex, String columnName) {
		// int columnIndex = getTableHeaders().indexOf(columnName)
		int columnIndex = -1
		for (int i = 0; i < getNumberOfColumns(); i++) {
			String name = getTableHeaders().get(i)
			println(name)
			println(columnName)
			if (columnName.equalsIgnoreCase(name)) {
				columnIndex = i
				break
			}
		}
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