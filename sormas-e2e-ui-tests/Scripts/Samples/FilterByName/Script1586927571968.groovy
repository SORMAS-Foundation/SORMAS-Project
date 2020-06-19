import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Table
import com.hzi.TestDataConnector
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Samples/partials/switchToSamples'), [:], FailureHandling.STOP_ON_FAILURE)

String caseName = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'test-filter-by-name')
println('searching for case: ' + caseName)
WebUI.setText(findTestObject('Object Repository/Samples/MainView/input_Export_caseCodeIdLike'), caseName)

WebUI.delay(1)
int numberOfRows = Table.getNumberOfTableRows()

if (numberOfRows != 1) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected to find one row. rows: ' + numberOfRows)
}

WebUI.closeBrowser()