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

WebUI.click(findTestObject('Tasks/button_div_New task'))

WebUI.click(findTestObject('Surveillance/CaseView/Task/div_TaskType_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Task/td_TaskType_caseIsolation_DDItem'))

WebUI.click(findTestObject('Surveillance/CaseView/Task/div_AssignedTo_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Task/span_Surveillance SUPERVISOR - berwachungsleitung (4)'))

WebUI.setText(findTestObject('Surveillance/CaseView/Task/textarea_Comment_TArea'), Helper.generateString('comment', 5))

WebUI.click(findTestObject('Surveillance/CaseView/Task/div_SaveTask_btn'))

WebUI.delay(1)

if (isStandalone) {
    WebUI.closeBrowser()
}

