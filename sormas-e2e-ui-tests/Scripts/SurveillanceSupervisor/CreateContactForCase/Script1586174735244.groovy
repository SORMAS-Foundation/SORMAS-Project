import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

String firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_case')

String lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_case')

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/FilterCaseByPersonName'), [('personName') : (firstName + 
        ' ') + lastName], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Surveillance/CaseView/div_Contacts_tab'))

WebUI.click(findTestObject('Contacts/CasesView/NewContact/div_New contact'))

firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_contact')

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_First name'), firstName)

// generate name because contacts cannot be deleted
String randomLastName = Helper.generateString(firstName, 6)

println('generated lastname:' + randomLastName)
//lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_contact')

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_Last name'), randomLastName)

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Sex_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/span_Male'))

WebUI.click(findTestObject('Surveillance/CaseView/Contacts/NewContact/div_RelationshipWithCase_DDBox'))

WebUI.scrollToElement(findTestObject('Surveillance/CaseView/Contacts/NewContact/span_RelationshipWithCase_LiveInTheSameHousehold_DDItem'), 1)
WebUI.click(findTestObject('Surveillance/CaseView/Contacts/NewContact/span_RelationshipWithCase_LiveInTheSameHousehold_DDItem'))

WebUI.setText(findTestObject('Surveillance/CaseView/Contacts/NewContact/textarea_DescriptionOfHowContactTookPlace_TArea'), 
    'test')

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.delay(2)

/*WebUI.click(findTestObject('Surveillance/CaseView/Contacts/NewContact/label_CreateANewPerson_option'))

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.delay(1)*/

if (isStandalone) {
    WebUI.closeBrowser()
}

