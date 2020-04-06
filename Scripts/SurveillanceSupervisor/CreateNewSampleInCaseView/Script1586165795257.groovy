import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.hzi.Helper
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/FilterCaseByPersonName'), [('personName') : findTestData(
            'GenericUsers').getValue(3, 2)], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_NewSample_btn'))

WebUI.setText(findTestObject('Surveillance/CaseView/Sample/input_DateSampleWasCollected_date'), '01/04/2020')

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_PurposeOfTheSample_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/td_PurposeOfTheSample_ExternalLabTesting_DDItem'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_TypeOfSample_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/td_TypeOfSample_Blood_DDItem'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_Laboratory_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/span_Laboratory_FirstElement_DDItem'))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/label_PathogenTest_No_option'))

WebUI.setText(findTestObject('Surveillance/CaseView/Sample/textarea_Comment_TArea'), Helper.generateString("comment", 6))

WebUI.click(findTestObject('Surveillance/CaseView/Sample/div_SaveSample_btn'))

WebUI.delay(1)

if (isStandalone) {
	WebUI.closeBrowser()
}


