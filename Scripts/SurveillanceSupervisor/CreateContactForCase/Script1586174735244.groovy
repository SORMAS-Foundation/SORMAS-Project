import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

String firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_case')

String lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_case')

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/FilterCaseByPersonName'), [('personName') : (firstName + 
        ' ') + lastName], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Surveillance/CaseView/Case/span_Contacts_tab'))

WebUI.click(findTestObject('Contacts/CasesView/NewContact/div_New contact'))

firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_contact')

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_First name'), firstName)

lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_contact')

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_Last name'), lastName)

WebUI.click(findTestObject('Surveillance/CaseView/Contacts/NewContact/div_RelationshipWithCase_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Contacts/NewContact/span_RelationshipWithCase_LiveInTheSameHousehold_DDItem'))

WebUI.setText(findTestObject('Surveillance/CaseView/Contacts/NewContact/textarea_DescriptionOfHowContactTookPlace_TArea'), 
    'test')

WebUI.click(findTestObject('Surveillance/CaseView/Contacts/NewContact/div_SaveContact_btn'))

WebUI.delay(2)

WebUI.click(findTestObject('Surveillance/CaseView/Contacts/NewContact/label_CreateANewPerson_option'))

WebUI.click(findTestObject('Surveillance/CaseView/Contacts/NewContact/div_SaveContact_btn'))

WebUI.delay(1)

if (isStandalone) {
    WebUI.closeBrowser()
}

