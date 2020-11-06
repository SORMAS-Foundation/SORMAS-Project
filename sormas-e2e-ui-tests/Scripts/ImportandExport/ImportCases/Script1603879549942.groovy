import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Login/partials/LoginAsImportUser'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(1)

WebUI.click(findTestObject('Imports/div_Import'))

WebUI.click(findTestObject('Imports/div_Line Listing Import'))

WebUI.waitForElementPresent(findTestObject('Imports/div_Download Import Template'), 1)

//fetch file from the downloaded path
String dateString = new Date().format('yyyy-MM-dd')

String filename = ('sormas_cases_' + dateString) + '.csv'

println(('file path should be: ' + GlobalVariable.gDownloadPath) + filename)

String filepath = (GlobalVariable.gDownloadPath + '//') + filename

WebUI.uploadFile(findTestObject('Imports/input_Choose File'), filepath)

WebUI.click(findTestObject('Imports/div_Start Data Import'))

WebUI.delay(2)

// 'check if "Pick or create person" dialog is shown' and select create-new-person
boolean checkDialog = WebUI.verifyElementPresent(findTestObject('ReusableORs/div_Select a matching person'), 
    2, FailureHandling.OPTIONAL)

if (checkDialog) {
    WebUI.click(findTestObject('ReusableORs/div_Create a new person'))

    WebUI.click(findTestObject('ReusableORs/div_Save'))
}

WebUI.verifyElementPresent(findTestObject('Imports/Import successful'), 1)

WebUI.click(findTestObject('Imports/div_Close'))

WebUI.closeBrowser()

