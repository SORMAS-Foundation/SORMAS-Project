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

WebUI.click(findTestObject('Surveillance/CaseView/Task/div_NewTask_btn'))

WebUI.click(findTestObject('Surveillance/CaseView/Task/div_TaskType_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Task/td_TaskType_caseIsolation_DDItem'))

WebUI.click(findTestObject('Surveillance/CaseView/Task/div_AssignedTo_DDBox'))

WebUI.click(findTestObject('Surveillance/CaseView/Task/span_AssignedTo_FirstEntry_DDItem'))

WebUI.setText(findTestObject('Surveillance/CaseView/Task/textarea_Comment_TArea'), Helper.generateString("comment", 5))

WebUI.click(findTestObject('Surveillance/CaseView/Task/div_SaveTask_btn'))

WebUI.delay(1)

if (isStandalone) {
    WebUI.closeBrowser()
}

