import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/label_Follow-up Visits'))
WebUI.delay(1)

if (!WebUI.verifyElementPresent(findTestObject('Contacts/Page_SORMAS/button_minus_8_days'), 1) 
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/Page_SORMAS/button_plus_8_days'), 1) 
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/Page_SORMAS/input_followUpVisits_showLastUntil'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/Page_SORMAS/button_new_contact_inContacts'), 1)) {
	WebUI.closeBrowser()
	throw new StepFailedException('"Follow up visits" view does not contain expected elements')
}

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/label_Contacts'))
WebUI.delay(1)

if (!WebUI.verifyElementNotPresent(findTestObject('Contacts/Page_SORMAS/button_minus_8_days'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/Page_SORMAS/button_plus_8_days'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/Page_SORMAS/input_followUpVisits_showLastUntil'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/Page_SORMAS/button_new_contact_inContacts'), 1)) {
	WebUI.closeBrowser()
	throw new StepFailedException('"Contacts" view does not contain expected elements')
}

WebUI.closeBrowser()

