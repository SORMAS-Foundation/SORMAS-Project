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
}
