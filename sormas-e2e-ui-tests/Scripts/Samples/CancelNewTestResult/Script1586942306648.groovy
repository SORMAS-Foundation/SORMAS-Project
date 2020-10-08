import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Samples/partials/switchToSamples'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Samples/partials/searchAndSelectSample'), [:], FailureHandling.STOP_ON_FAILURE)

String dateOfFirstTestResult = WebUI.getText(findTestObject('Samples/SampleInformation/last_testResultDateTime'))

WebUI.click(findTestObject('Samples/SampleInformation/div_New test result'))

WebUI.click(findTestObject('Object Repository/Samples/NewTestResult/div_Type of test_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Samples/NewTestResult/span_Antibody detection'))

WebUI.click(findTestObject('Object Repository/Samples/NewTestResult/div_Tested disease_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Samples/NewTestResult/td_COVID-19'))

Date now = new Date()

String resultDate = now.format('MM/dd/yyyy')

String resultTime = now.format('HH:mm')

println((('Setting date-time of result to: date=' + resultDate) + ' time=') + resultTime)

WebUI.setText(findTestObject('Samples/NewTestResult/input_Date'), resultDate)

WebUI.setText(findTestObject('Object Repository/Samples/NewTestResult/input_Time'), resultTime)

WebUI.click(findTestObject('Object Repository/Samples/NewTestResult/div_Test result_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Samples/NewTestResult/td_Pending'))

WebUI.click(findTestObject('Object Repository/Samples/NewTestResult/label_No'))
WebUI.click(findTestObject('Samples/NewTestResult/div_Discard'))

WebUI.delay(1)

String dateToCheck = WebUI.getText(findTestObject('Samples/SampleInformation/last_testResultDateTime'))

if (!(dateToCheck.equals(dateOfFirstTestResult))) {
	WebUI.closeBrowser()
    throw new StepFailedException('Expected to find no changes in the testresults. expected: ' + 
    dateOfFirstTestResult + ' found: ' + dateToCheck)
}

WebUI.closeBrowser()

