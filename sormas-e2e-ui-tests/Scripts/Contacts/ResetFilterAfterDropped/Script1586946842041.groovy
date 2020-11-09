import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Table as Table
import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.maximizeWindow()

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(1)

int unfilteredRows = Table.getNumberOfTableRows()

//WebUI.click(findTestObject('Contacts/ContactsOverview/div_Dropped'))
WebUI.click(findTestObject('Contacts/ContactsOverview/div_Show More Less Filters'))

WebUI.click(findTestObject('Contacts/ContactsOverview/span_Only high priority contacts'))

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))

WebUI.delay(1)

int droppedRows = Table.getNumberOfTableRows()

if (droppedRows >= unfilteredRows) {
    WebUI.closeBrowser()

    throw new StepFailedException('Please consider other testdata - the number of unfiltered and dropped contacts is the same.')
}

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))

WebUI.delay(1)

int rowsAfterReset = Table.getNumberOfTableRows()

println((('rows before filter: ' + unfilteredRows) + ' rows after reset: ') + rowsAfterReset)

if (unfilteredRows != rowsAfterReset) {
    WebUI.closeBrowser()

    throw new StepFailedException((('Number of rows before filtering and after resetting the filter is not the same: ' + 
    unfilteredRows) + ' != ') + rowsAfterReset)
}

WebUI.closeBrowser()

