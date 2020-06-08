package com.hzi
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords


class Table {

	/**
	 * Get all rows of HTML table !!! returns only visible rows !!!
	 * @param table Katalon test object represent for HTML table
	 * @param outerTagName outer tag name of TR tag, usually is TBODY
	 * @return All rows inside HTML table
	 */
	@Keyword
	def List<WebElement> getHtmlTableRows(TestObject table, String outerTagName) {
		WebElement mailList = WebUiBuiltInKeywords.findWebElement(table)
		List<WebElement> selectedRows = mailList.findElements(By.xpath("//" + outerTagName + "/tr"))
		return selectedRows
	}

	/**
	 * Get number of Rows by attribute.
	 * @param table - testObject
	 * @return
	 */
	@Keyword
	def Integer getTableRowsByAttribute(TestObject table) {
		WebElement tableElement = WebUiBuiltInKeywords.findWebElement(table)

		String attribute = tableElement.getAttribute("aria-rowcount")

		return attribute.toInteger() - 1
	}

	/**
	 * Get number of rows by attribute "aria-rowcount" for a static testObject found by the same attribute. Is a convenience method.
	 * @return
	 */	
	static Integer getNumberOfTableRows() {
		Table table = new Table()
		TestObject testObject = Helper.createTestObjectWithXPath('//table[@aria-rowcount]')
		return table.getTableRowsByAttribute(testObject)
	}

	static TableContent getVisibleTableContent() {
		TestObject tableObject = Helper.createTestObjectWithXPath('//table')

		WebElement tableElement = WebUiBuiltInKeywords.findWebElement(tableObject)

		// WebElement headElement = tableElement.findElements(By.tagName("thead"))
		WebElement bodyElement = tableElement.findElement(By.tagName("tbody"))

		List<String> headers = Table.getTableColumnNames()

		List<WebElement> rowElements = bodyElement.findElements(By.tagName("tr"))
		println('rows:' + rowElements.size())

		TableContent tc = new TableContent()
		tc.setTableHeaders(headers)
		for (int row = 0; row < rowElements.size(); row++) {
			List<String> tableData = new ArrayList<String>()
			List<WebElement> columnElements = rowElements.get(row).findElements(By.tagName('td'))

			println('columns:' + columnElements.size())

			for (int column = 0; column < columnElements.size(); column++) {
				String celltext = columnElements.get(column).getText()
				tableData.add(celltext)
			}
			tc.addRowData(tableData)
		}

		return tc
	}

	static String[] getTableColumnNames() {
		TestObject tableObject = Helper.createTestObjectWithXPath('//table')

		WebElement tableElement = WebUiBuiltInKeywords.findWebElement(tableObject)
		WebElement headElement = tableElement.findElement(By.tagName("thead"))

		List<WebElement> rowElements = headElement.findElements(By.tagName("tr"))

		List<String> columnNames = []
		for (int row = 0; row < rowElements.size(); row++) {
			List<WebElement> headerElements = rowElements.get(row).findElements(By.tagName('th'))

			println('columns:' + headerElements.size())
			for (int column = 0; column < headerElements.size(); column++) {
				String headerText = headerElements.get(column).getText()
				columnNames.add(headerText)
			}
		}
		return columnNames
	}
}
