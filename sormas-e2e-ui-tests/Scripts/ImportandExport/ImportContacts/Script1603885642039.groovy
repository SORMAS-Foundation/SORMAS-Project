import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Login/partials/LoginAsImportUser'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(1)

WebUI.click(findTestObject('Imports/div_More'))

WebUI.click(findTestObject('Imports/div_Import'))

WebUI.waitForElementPresent(findTestObject('Imports/div_Download Import Template'), 1)

//fetch file from the downloaded path
String dateString = new Date().format('yyyy-MM-dd')

String filename = ('sormas_contacts_' + dateString) + '.csv'

println(('file path should be: ' + GlobalVariable.gDownloadPath) + filename)

String filepath = (GlobalVariable.gDownloadPath + '//') + filename

WebUI.uploadFile(findTestObject('Imports/input_Choose File'), filepath)

WebUI.click(findTestObject('Imports/div_Start Data Import'))

WebUI.delay(2)

WebUI.verifyElementPresent(findTestObject('Imports/Import successful'), 1)

WebUI.click(findTestObject('Imports/div_Close'))

WebUI.closeBrowser()