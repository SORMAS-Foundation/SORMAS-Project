import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.hzi.Table as Table
import com.hzi.TableContent as TableContent
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import org.openqa.selenium.Keys

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

// PREPARE
WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Samples/partials/switchToSamples'), [:], FailureHandling.STOP_ON_FAILURE)

String caseName = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'case-with-samples')

println('searching for case: ' + caseName)

WebUI.setText(findTestObject('Object Repository/Samples/MainView/input_searchName'), caseName)

WebUI.delay(1)

println(GlobalVariable.gSamplesTestDataName)
String sampleTypeColumnName = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'sample-type-column')
println('other test:' + TestDataConnector.getValueByKey('defaultSamplesTestData', 'sample-type-column'))
String typeBlood = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'sample-type-blood')

String typeNasalSwab = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'sample-type-nasal')

TableContent content = Table.getVisibleTableContent()
println(content.getTableRows())
String typeOfSample = content.getRowData(0, sampleTypeColumnName)

String futureTypeOfSample = typeOfSample.equalsIgnoreCase(typeBlood) ? typeNasalSwab : typeBlood

println('type of sample found: ' + typeOfSample + ' will change to: ' + futureTypeOfSample)

'select sample to edit'
WebUI.click(findTestObject('Samples/MainView/edit_sample_from_table'))

// TESTCASE
WebUI.click(findTestObject('Object Repository/Samples/SampleInformation/div_TypeOfSample_select-button'))

WebUI.click(findTestObject('Object Repository/Samples/SampleInformation/typeOfSample_dropDown_empty'))

WebUI.setText(findTestObject('Object Repository/Samples/SampleInformation/input_TypeOfSample'), futureTypeOfSample)

WebUI.sendKeys(findTestObject('Samples/SampleInformation/input_TypeOfSample'), Keys.chord(Keys.TAB))

// the sendKeys-Tab adds some strange characters to the comment field - this cleans it up
WebUI.setText(findTestObject('Samples/SampleInformation/textarea_Comment_comment'), '')

WebUI.click(findTestObject('Object Repository/Samples/SampleInformation/save_Sample'))

WebUI.click(findTestObject('Object Repository/Samples/SampleInformation/link_SamplesList'))

// CHECK
content = Table.getVisibleTableContent()

String currentTypeOfSample = content.getRowData(0, sampleTypeColumnName)

if (!(currentTypeOfSample.equals(futureTypeOfSample))) {
    WebUI.closeBrowser()
    throw new StepFailedException('Expected that the type of sample would have changed. expected: ' + futureTypeOfSample + 
    ' found: ' + currentTypeOfSample)
}

WebUI.closeBrowser()

