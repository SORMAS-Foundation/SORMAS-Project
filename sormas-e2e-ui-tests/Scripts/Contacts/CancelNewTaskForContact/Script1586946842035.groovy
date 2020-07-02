import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver

import com.hzi.Helper
import com.hzi.Table
import com.hzi.TableContent
import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

'userName\n'
WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), 
    findTestData(GlobalVariable.gContactTestDataName).getValue(2, 7))

WebUI.delay(1)

int rows = Table.getNumberOfTableRows()
if (rows != 1) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected one row to process. found:' + rows)
}

// resize window so all columns of the table are visible
WebUI.maximizeWindow()

// This is necessary for JENKINS or there will be no "oldTaskNumber" and the test fails
WebUI.click(Helper.createTestObjectWithXPath('//table[@aria-rowcount]//a'))
WebUI.delay(1)
WebUI.click(findTestObject('Contacts/ContactInformationView/div_Contacts list'))
WebUI.delay(1)

TableContent tableContent = Table.getVisibleTableContent()
oldTaskNumber = tableContent.getRowData(0, tableContent.getNumberOfColumns() - 1)
println('old task number:' + oldTaskNumber)

'create new Task and Cancel'
WebUI.callTestCase(findTestCase('Contacts/partials/createNewTaskForContact'), [('create') : false], FailureHandling.STOP_ON_FAILURE)

'check if new number of tasks is the same than the old one'
rows = Table.getNumberOfTableRows()
if (rows != 1) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected one row to process. found:' + rows)
}

tableContent = Table.getVisibleTableContent()
newTaskNumber = tableContent.getRowData(0, tableContent.getNumberOfColumns() - 1)
println('new task number:' + newTaskNumber)

if (Integer.parseInt(oldTaskNumber) == (Integer.parseInt(newTaskNumber) - 1)) {
    WebUI.closeBrowser()

    throw new StepFailedException((('The new number of tasks does not equal the old number. old:' + oldTaskNumber) + ' new:') + 
    newTaskNumber)
}

WebUI.closeBrowser()

