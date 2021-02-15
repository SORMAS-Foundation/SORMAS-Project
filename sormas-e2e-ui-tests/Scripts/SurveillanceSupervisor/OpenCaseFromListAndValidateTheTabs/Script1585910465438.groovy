import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(3)
WebUI.click(findTestObject('Surveillance/SearchView/a_Search_Entry_link'))

WebUI.verifyElementPresent(findTestObject('Surveillance/CaseView/Case/input_Case_CaseIdUuid_inputBox'), 3)

WebUI.delay(1)

WebUI.click(findTestObject('Surveillance/CaseView/div_Case Person_tab'))

WebUI.verifyElementPresent(findTestObject('Surveillance/CaseView/Person/input_Person_PassportNumber_inputBox'), 3)

WebUI.click(findTestObject('Surveillance/CaseView/div_Hospitalization_tab'))

WebUI.verifyElementPresent(findTestObject('Surveillance/CaseView/Hospitatation/div_Date of visit or admission'), 3)

WebUI.click(findTestObject('Surveillance/CaseView/div_Symptoms_tab'))

WebUI.verifyElementPresent(findTestObject('Surveillance/CaseView/SymptomsTab/input_Comments_symptomsComments'), 3)

WebUI.click(findTestObject('Surveillance/CaseView/div_Epidemiological data_tab'))

WebUI.verifyElementPresent(findTestObject('Surveillance/CaseView/Epidemiological data/span_Exposure Investigation'), 3)

WebUI.click(findTestObject('Surveillance/CaseView/div_Follow-up_tab'))

WebUI.verifyElementPresent(findTestObject('Surveillance/CaseView/div_Export'), 3)

WebUI.click(findTestObject('Surveillance/CaseView/div_Contacts_tab'))

WebUI.verifyElementPresent(findTestObject('Contacts/CasesView/NewContact/div_New contact'), 3)

if (isStandalone) {
    WebUI.closeBrowser()
}

