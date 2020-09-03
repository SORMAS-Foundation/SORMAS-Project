import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

String caseName = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'case-with-samples')

println('searching for case: ' + caseName)

WebUI.setText(findTestObject('Object Repository/Samples/MainView/input_Export_caseCodeIdLike'), caseName)

WebUI.delay(1)

WebUI.click(findTestObject('Samples/MainView/edit_sample_from_table'))

