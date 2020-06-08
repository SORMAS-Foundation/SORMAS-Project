import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.hzi.Helper as Helper
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

String firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_case')

String lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_case')

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/FilterCaseByPersonName'), [('personName') : (firstName + 
        ' ') + lastName], FailureHandling.STOP_ON_FAILURE)

// Switch to tab "Symttoms"
WebUI.click(findTestObject('Object Repository/Surveillance/CaseView/span_Symptoms_tab'))

// Set random text in "comment" field
WebUI.click(findTestObject('Surveillance/CaseView/SymptomsTab/input_Comments_symptomsComments'))
WebUI.setText(findTestObject('Surveillance/CaseView/SymptomsTab/input_Comments_symptomsComments'), Helper.generateString(
        'comment', 8))

// Save change
WebUI.click(findTestObject('Object Repository/Surveillance/CaseView/div_Save_btn'))

WebUI.delay(1)

if (isStandalone) {
    WebUI.closeBrowser()
}

