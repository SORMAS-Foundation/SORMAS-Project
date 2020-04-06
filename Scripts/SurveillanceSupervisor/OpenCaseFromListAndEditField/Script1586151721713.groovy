import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper as Helper
import com.hzi.TestDataConnector
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

String firstName = TestDataConnector.getValueByKey("GenericUsers", "first_name")
String lastName = TestDataConnector.getValueByKey("GenericUsers", "last_name")

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/FilterCaseByPersonName'), [('personName') : firstName + " " + lastName], FailureHandling.STOP_ON_FAILURE)

// Switch to tab "Symttoms"
WebUI.click(findTestObject('Object Repository/Surveillance/CaseView/span_Symptoms_tab'))

// Set random text in "comment" field
WebUI.setText(findTestObject('Surveillance/CaseView/SymptomsTab/input_Symptoms_Comments_inputBox'), Helper.generateString(
        'comment', 8))

// Save change
WebUI.click(findTestObject('Object Repository/Surveillance/CaseView/div_Save_btn'))

WebUI.delay(1)

if (isStandalone) {
    WebUI.closeBrowser()
}

