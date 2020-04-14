import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Dimension as Dimension
import org.openqa.selenium.WebDriver as WebDriver

import com.hzi.Table as Table
import com.hzi.TableContent as TableContent
import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)

'userName\n'
WebUI.setText(findTestObject('Contacts/ContactsOverview/contact_search_field_name'),
	findTestData(GlobalVariable.gContactTestDataName).getValue(2, 1))

WebUI.delay(1)

int rows = Table.getNumberOfTableRows()
if (rows != 1) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected one row to process. found:' + rows)
}

// resize window so all columns of the table are visible
WebDriver driver = DriverFactory.getWebDriver()
driver.manage().window().setSize(new Dimension(2500,2500))

TableContent tableContent = Table.getVisibleTableContent()
oldTaskNumber = tableContent.getRowData(0, tableContent.getNumberOfColumns() - 1)
println('old task number:' + oldTaskNumber)

'create new Task and Save'
WebUI.callTestCase(findTestCase('ContactSupervisor/partials/createNewTaskForContact'), [('create') : true], FailureHandling.STOP_ON_FAILURE)

'check if new number of tasks is exactly one higher than the old one'
rows = Table.getNumberOfTableRows()
if (rows != 1) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected one row to process. found:' + rows)
}

tableContent = Table.getVisibleTableContent()
newTaskNumber = tableContent.getRowData(0, tableContent.getNumberOfColumns() - 1)
println('new task number:' + newTaskNumber)

if (Integer.parseInt(oldTaskNumber) != (Integer.parseInt(newTaskNumber) - 1)) {
    WebUI.closeBrowser()

    throw new StepFailedException((('The new number of tasts does not equal the old number plus one. old:' + oldTaskNumber) + 
    ' new:') + newTaskNumber)
}

WebUI.closeBrowser()

