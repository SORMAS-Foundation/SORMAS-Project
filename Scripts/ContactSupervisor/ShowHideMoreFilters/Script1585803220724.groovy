import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/LoginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/SwitchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Show More Filters'))

if (!WebUI.verifyElementPresent(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Apply date filter'), 1)){
	WebUI.closeBrowser()
	throw new StepFailedException('"Apply Filter" button is not present after "More Filters" is clicked.')	
}

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Show Less Filters'))

if (!WebUI.verifyElementNotPresent(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Apply date filter'), 1)){
	WebUI.closeBrowser()
	throw new StepFailedException('"Apply Filter" button is still present after "Less Filters" is clicked.')
}

WebUI.closeBrowser()