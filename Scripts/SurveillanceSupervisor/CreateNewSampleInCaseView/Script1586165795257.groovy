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

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_NewSample_btn'))

WebUI.setText(findTestObject('Surveillance/CaseView/Sample/input_DateSampleWasCollected_date'), '01/04/2020')

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_PurposeOfTheSample_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/td_PurposeOfTheSample_ExternalLabTesting_DDItem'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_TypeOfSample_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/td_TypeOfSample_Blood_DDItem'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_Laboratory_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/span_Laboratory_FirstElement_DDItem'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/label_PathogenTest_No_option'))

WebUI.setText(findTestObject('Surveillance/CaseView/Sample/textarea_Comment_TArea'), Helper.generateString('comment', 6))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_SaveSample_btn'))

WebUI.delay(1)

if (isStandalone) {
    WebUI.closeBrowser()
}

