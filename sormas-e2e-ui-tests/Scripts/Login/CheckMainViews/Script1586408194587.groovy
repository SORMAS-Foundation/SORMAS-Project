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
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Login/partials/LoginAsNationalClinician'), [:], FailureHandling.STOP_ON_FAILURE)

'Dashboard'
WebUI.click(findTestObject('Login/MainView/menu_Dashboard'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_SurveillanceDashboard'), 2, FailureHandling.STOP_ON_FAILURE)

'Tasks'
WebUI.click(findTestObject('Login/MainView/menu_Tasks'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_TaskManagement'), 2, FailureHandling.STOP_ON_FAILURE)

'Cases'
WebUI.click(findTestObject('Login/MainView/menu_Cases'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_CaseDirectory'), 2, FailureHandling.STOP_ON_FAILURE)

'mSers'
WebUI.click(findTestObject('Login/MainView/menu_mSERS'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_AggregateReporting'), 2, FailureHandling.STOP_ON_FAILURE)

'Contacts'
WebUI.click(findTestObject('Login/MainView/menu_Contacts'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_ContactDirectory'), 2, FailureHandling.STOP_ON_FAILURE)

'Events'
WebUI.click(findTestObject('Login/MainView/menu_Events'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_EventDirectory'), 2, FailureHandling.STOP_ON_FAILURE)

'Samples'
WebUI.click(findTestObject('Login/MainView/menu_Samples'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_SampleDirectory'), 2, FailureHandling.STOP_ON_FAILURE)

'Reports'
WebUI.click(findTestObject('Login/MainView/menu_Reports'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_WeeklyReports'), 2, FailureHandling.STOP_ON_FAILURE)

'Statistics'
WebUI.click(findTestObject('Login/MainView/menu_Statistics'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_Statistics'), 2, FailureHandling.STOP_ON_FAILURE)

'Configuration'
WebUI.click(findTestObject('Login/MainView/menu_Configuration'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_OutbreaksConfiguration'), 2, FailureHandling.STOP_ON_FAILURE)

'About'
WebUI.click(findTestObject('Login/MainView/menu_About'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_About'), 2, FailureHandling.STOP_ON_FAILURE)

WebUI.closeBrowser()