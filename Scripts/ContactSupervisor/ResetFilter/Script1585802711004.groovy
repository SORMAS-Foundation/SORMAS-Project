import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Table
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)

int unfilteredRows = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Contacts/Page_SORMAS/dropped_contacts_filter'))
WebUI.delay(1)

int droppedRows = Table.getNumberOfTableRows()
if (droppedRows >= unfilteredRows) {
	throw new StepFailedException('Please consider other testdata - the number of unfiltered and dropped contacts is the same.')
}

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Reset filters'))
WebUI.delay(1)

int rowsAfterReset = Table.getNumberOfTableRows()
println('rows before filter: ' + unfilteredRows + ' rows after reset: ' + rowsAfterReset)
if (unfilteredRows != rowsAfterReset) {
	throw new StepFailedException('Number of rows before filtering and after resetting the filter is not the same: ' + unfilteredRows + ' != ' + rowsAfterReset)
}

WebUI.closeBrowser()


