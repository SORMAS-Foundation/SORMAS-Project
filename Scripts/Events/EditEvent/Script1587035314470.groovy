import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Events/partials/switchToEvents'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Events/partials/FilterEventBySignalOption'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(2)

String firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_event_community')

String lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_event_community')

WebUI.click(findTestObject('Events/EditEventView/input_CommunityContactPerson_inputBox'))
WebUI.setText(findTestObject('Events/EditEventView/input_CommunityContactPerson_inputBox'), (firstName + ' ') + lastName)

WebUI.click(findTestObject('Events/NewEventView/div_Save_btn'))

WebUI.delay(1)

WebUI.click(findTestObject('Events/EditEventView/div_Event participants'))

WebUI.click(findTestObject('Events/EditEventView/div_AddPerson_btn'))

WebUI.setText(findTestObject('Events/EditEventView/input_AddPerson_InvolvementDescription_inputBox'), 'Troublemaker')

firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_event_person')

lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_event_person')

WebUI.setText(findTestObject('Events/EditEventView/input_AddPerson_FirstName_inputBox'), firstName)

WebUI.setText(findTestObject('Events/EditEventView/input_AddPerson_LastName_inputBox'), lastName)

WebUI.click(findTestObject('Events/NewEventView/div_Save_btn'))

WebUI.delay(1)

if(WebUI.verifyElementPresent(findTestObject('Object Repository/Events/EditEventView/div_PickOrCreatePerson_lbl'), 2)) {
	WebUI.click(findTestObject('Events/NewEventView/div_Save_btn'))
}

WebUI.click(findTestObject('Events/NewEventView/div_Save_btn'))

WebUI.delay(2)

if (isStandalone) {
	WebUI.closeBrowser()
}
