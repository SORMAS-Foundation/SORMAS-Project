import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Show More Filters'))
WebUI.delay(1)

if (!WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/div_Apply date filter'), 1) 
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_Landkreis'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_EinrichtungLandkreis'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_gemeldetVon'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_KontaktDatum'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_KontakteBis'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_KontakteVon'), 1)	
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_nachDatum'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_quarantine_end'), 1)
	|| !WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_Verantwortliche'), 1)){
	WebUI.closeBrowser()
	throw new StepFailedException('Missing expected input field(s) in "More Filters" view')	
}

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Show Less Filters'))
WebUI.delay(1)
if (!WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/div_Apply date filter'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_Landkreis'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_EinrichtungLandkreis'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_gemeldetVon'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_KontaktDatum'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_KontakteBis'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_KontakteVon'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_nachDatum'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_quarantine_end'), 1)
	|| !WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_moreFilter_Verantwortliche'), 1)){
	WebUI.closeBrowser()
	throw new StepFailedException('Unexpected input fields from "More Filters" are present')
}

WebUI.closeBrowser()