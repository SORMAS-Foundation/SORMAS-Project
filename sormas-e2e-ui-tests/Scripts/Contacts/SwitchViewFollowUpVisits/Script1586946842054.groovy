import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)
WebUI.click(findTestObject('Contacts/ContactsOverview/label_Follow-up Visits'))
WebUI.delay(1)
WebUI.maximizeWindow()

if (!WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/button_div_Previous'), 1) 
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/button_div_Next'), 1) 
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/div_From_date'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/div_To_date'), 1)) {
	WebUI.closeBrowser()
	throw new StepFailedException('"Follow up visits" view does not contain expected elements')
}

WebUI.click(findTestObject('Contacts/ContactsOverview/label_Contacts'))
WebUI.delay(1)

if (!WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/button_div_Previous'),2)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/button_div_Next'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/div_From_date'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/div_To_date'), 1)) {
	WebUI.closeBrowser()
	throw new StepFailedException('"Contacts" view does not contain expected elements')
}

WebUI.closeBrowser()

