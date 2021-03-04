import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

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

'Persons'
WebUI.click(findTestObject('Login/MainView/menu_Persons'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_PersonDirectory'), 2, FailureHandling.STOP_ON_FAILURE)

'Configuration'
WebUI.click(findTestObject('Login/MainView/menu_Configuration'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_OutbreaksConfiguration'), 2, FailureHandling.STOP_ON_FAILURE)

'About'
WebUI.click(findTestObject('Login/MainView/menu_About'))

WebUI.verifyElementPresent(findTestObject('Login/MainView/title_About'), 2, FailureHandling.STOP_ON_FAILURE)

WebUI.closeBrowser()

