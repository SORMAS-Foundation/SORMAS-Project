import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Keys

import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

// PREPARE
WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Samples/partials/switchToSamples'), [:], FailureHandling.STOP_ON_FAILURE)

String caseName = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'case-with-samples')

println('searching for case: ' + caseName)

WebUI.setText(findTestObject('Object Repository/Samples/MainView/input_Export_caseCodeIdLike'), caseName)

WebUI.delay(1)

println('test-files-db: ' + GlobalVariable.gSamplesTestDataName)
String sampleTypeColumnName = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'sample-type-column')
String typeBlood = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'sample-type-blood')
String typeNasalSwab = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'sample-type-nasal')


'select sample to edit'
WebUI.click(findTestObject('Samples/MainView/edit_sample_from_table'))

String typeOfSample = WebUI.getAttribute(findTestObject('Object Repository/Samples/SampleInformation/input_TypeOfSample'), 'value')

String futureTypeOfSample = typeOfSample.equalsIgnoreCase(typeBlood) ? typeNasalSwab : typeBlood

println('type of sample found: ' + typeOfSample + ' will change to: ' + futureTypeOfSample)

// TESTCASE
WebUI.click(findTestObject('Object Repository/Samples/SampleInformation/div_TypeOfSample_select-button'))

WebUI.click(findTestObject('Object Repository/Samples/SampleInformation/typeOfSample_dropDown_empty'))

WebUI.setText(findTestObject('Object Repository/Samples/SampleInformation/input_TypeOfSample'), futureTypeOfSample)

WebUI.sendKeys(findTestObject('Samples/SampleInformation/input_TypeOfSample'), Keys.chord(Keys.ENTER))

// the sendKeys-Tab adds some strange characters to the comment field - this cleans it up
WebUI.click(findTestObject('Samples/SampleInformation/textarea_Comment_comment'))
WebUI.setText(findTestObject('Samples/SampleInformation/textarea_Comment_comment'), '')

WebUI.click(findTestObject('Object Repository/Samples/SampleInformation/save_Sample'))

WebUI.click(findTestObject('Object Repository/Samples/SampleInformation/link_SamplesList'))

// CHECK
WebUI.click(findTestObject('Samples/MainView/edit_sample_from_table'))

String currentTypeOfSample = WebUI.getAttribute(findTestObject('Object Repository/Samples/SampleInformation/input_TypeOfSample'), 'value')

if (!(currentTypeOfSample.equals(futureTypeOfSample))) {
    WebUI.closeBrowser()
    throw new StepFailedException('Expected that the type of sample would have changed. expected: ' + futureTypeOfSample + 
    ' found: ' + currentTypeOfSample)
}

WebUI.closeBrowser()

