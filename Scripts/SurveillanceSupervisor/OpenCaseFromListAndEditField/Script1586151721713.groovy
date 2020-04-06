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

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

// Search for Person
String testPerson = findTestData('GenericUsers').getValue(3, 1) + " " + findTestData('GenericUsers').getValue(4, 1)
WebUI.setText(findTestObject('Surveillance/SearchView/Filter/input_search_person_inputBox'), testPerson)

// Open first entry
WebUI.click(findTestObject('Surveillance/SearchView/a_Search_Entry_link'))

// Switch to tab "Symttoms"
WebUI.click(findTestObject('Object Repository/Surveillance/CaseView/span_Symptoms_tab'))

// Set random text in "comment" field
WebUI.setText(findTestObject('Surveillance/CaseView/SymptomsTab/input_Symptoms_Comments_inputBox'), Helper.generateString("comment", 8))

// Save change
WebUI.click(findTestObject('Object Repository/Surveillance/CaseView/div_Save_btn'))

if (isStandalone) {
	WebUI.closeBrowser()
}
