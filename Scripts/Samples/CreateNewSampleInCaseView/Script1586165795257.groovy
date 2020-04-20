import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper as Helper
import com.hzi.Table
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Samples/partials/switchToSamples'), [:], FailureHandling.STOP_ON_FAILURE)
int numberOfSamples = Table.getNumberOfTableRows()

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

// String firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_case')
// String lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_case')

String searchName = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'test-filter-by-name')
println('search-name: ' + searchName)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/FilterCaseByPersonName'), [('personName') : searchName], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_NewSample_btn'))
WebUI.waitForElementPresent(findTestObject('Surveillance/CaseView/Sample/input_DateSampleWasCollected_date'), 2)

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

WebUI.callTestCase(findTestCase('Samples/partials/switchToSamples'), [:], FailureHandling.STOP_ON_FAILURE)
int newNumberOfSamples = Table.getNumberOfTableRows()

if (newNumberOfSamples != (numberOfSamples + 1 )) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected that the number of samples is increased by one. expected: ' + (numberOfSamples + 1) + ' found: ' + newNumberOfSamples)
}

if (isStandalone) {
    WebUI.closeBrowser()
}

