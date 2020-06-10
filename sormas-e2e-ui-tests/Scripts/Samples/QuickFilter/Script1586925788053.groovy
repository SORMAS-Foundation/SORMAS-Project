import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Table
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Samples/partials/switchToSamples'), [:], FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)
int allRows = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Samples/MainView/div_Not shipped'))
WebUI.delay(1)
int notShipped = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Samples/MainView/div_Shipped'))
WebUI.delay(1)
int shipped = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Samples/MainView/div_Received'))
WebUI.delay(1)
int received = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Samples/MainView/div_Referred to other lab'))
WebUI.delay(1)
int referredToOtherLab = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Show More Less Filters'))
WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply date filter'))
WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))
WebUI.delay(1)
int afterReset = Table.getNumberOfTableRows()

if (allRows != (notShipped + shipped)) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected the sum of shipped and not shipped rows to equal number of all rows. all: ' + allRows + ' shipped: ' + shipped + ' not-shipped: ' + notShipped)
}

if (received > shipped) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected the number of received samples to be less or equal to the number of shipped samples. received: ' + received + ' shipped: ' + shipped)
}

if (allRows != afterReset) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected the same number of rows before and after filtering. before: ' + allRows + ' after: ' + afterReset)
}

WebUI.closeBrowser()
