import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Events/partials/switchToEvents'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Events/partials/FilterEventBySignalOption'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(2)

String firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_event_community')

String lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_event_community')

WebUI.setText(findTestObject('Events/NewEventView/input_Title_eventTitle'), 'VFX design Event')

WebUI.setText(findTestObject('Events/NewEventView/textarea_EventDescription_TArea'), 'This event is Edited')

WebUI.scrollToElement(findTestObject('Events/EditEventView/input_CommunityContactPerson_inputBox'), 2)

WebUI.click(findTestObject('Events/EditEventView/input_CommunityContactPerson_inputBox'))
WebUI.setText(findTestObject('Events/EditEventView/input_CommunityContactPerson_inputBox'), (firstName + ' ') + lastName)

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.delay(1)

WebUI.scrollToElement(findTestObject('Events/EditEventView/div_Event participants'), 2)

WebUI.click(findTestObject('Events/EditEventView/div_Event participants'))

WebUI.click(findTestObject('Events/EditEventView/div_AddPerson_btn'))

WebUI.setText(findTestObject('Events/EditEventView/input_AddPerson_InvolvementDescription_inputBox'), 'Troublemaker')

firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_event_person')

lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_event_person')

WebUI.setText(findTestObject('Events/EditEventView/input_AddPerson_FirstName_inputBox'), firstName)

WebUI.setText(findTestObject('Events/EditEventView/input_AddPerson_LastName_inputBox'), lastName)

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.delay(1)

if(WebUI.verifyElementPresent(findTestObject('Object Repository/Events/EditEventView/div_PickOrCreatePerson_lbl'), 2)) {
	WebUI.click(findTestObject('ReusableORs/div_Save'))
}

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.delay(2)

if (isStandalone) {
	WebUI.closeBrowser()
}
