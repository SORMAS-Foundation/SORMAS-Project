import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.exception.WebElementNotFoundException as WebElementNotFoundException
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable

WebUI.setText(findTestObject('Login/input_username_username'), GlobalVariable.gUsernameSecondSurveillanceSupervisor)

WebUI.setText(findTestObject('Login/input_password_password'), GlobalVariable.gPasswordSecondSurveillanceSupervisor)

WebUI.click(findTestObject('Login/div_Log in'))

WebUI.click(findTestObject('Login/MainView/menu_Dashboard'))

WebUI.waitForElementPresent(findTestObject('Surveillance/Dashboard/caseRangeWeek'), 3)

WebUI.click(findTestObject('Surveillance/Dashboard/caseRangeWeek'))

WebUI.click(findTestObject('Surveillance/Dashboard/caseRangeYear'))

numberOfCases = WebUI.getText(findTestObject('Surveillance/Dashboard/numberOfCases'))

// WebUI.click(findTestObject('Login/span_Logout_link'))
return numberOfCases

