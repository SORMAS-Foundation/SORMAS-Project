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
import org.openqa.selenium.Keys as Keys

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Surveillance/NewCase/div_NewCase_btn'))

WebUI.click(findTestObject('Surveillance/NewCase/div_Region_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCase/div_Region_Berlin_DDItem'))

WebUI.click(findTestObject('Surveillance/NewCase/div_District_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCase/div_District_Berlin_DDItem'))

WebUI.click(findTestObject('Surveillance/NewCase/div_HealthFacility_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCase/div_HealthFacility_BerlinNationHospital_DDItem'))

WebUI.setText(findTestObject('Surveillance/NewCase/first_name_inputBox'), GlobalVariable.gSurveillanceTestUserFirstname)

WebUI.setText(findTestObject('Surveillance/NewCase/last_name_inputBox'), GlobalVariable.gSurveillanceTestUserLastname)

WebUI.setText(findTestObject('Surveillance/NewCase/dateOfReport_inputBox'), '01/04/2020')

WebUI.setText(findTestObject('Surveillance/NewCase/dateOfSymptomOnset_inputBox'), '26/03/2020')

WebUI.setText(findTestObject('Surveillance/NewCase/div_DateOfBirthYear_inputBox'), '2000')

WebUI.setText(findTestObject('Surveillance/NewCase/div_DateOfBirthMonth_inputBox'), 'June')

WebUI.setText(findTestObject('Surveillance/NewCase/div_DateOfBirthDay_inputBox'), '7')

WebUI.click(findTestObject('Surveillance/NewCase/div_Sex_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCase/div_Sex_Female_DDItem'))

WebUI.click(findTestObject('Surveillance/NewCase/div_PresentConditionOfPerson_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCase/div_PresentConditionOfPerson_alive_DDItem'))

WebUI.click(findTestObject('Surveillance/NewCase/div_Save_btn'))

WebUI.delay(3)

if (WebUI.verifyElementPresent(findTestObject('Object Repository/Surveillance/NewCase/Comfirmation/div_check_confimation_lbl'), 
    5)) {
    WebUI.click(findTestObject('Object Repository/Surveillance/NewCase/Comfirmation/chooseExistingCase_radio'))

    WebUI.click(findTestObject('Object Repository/Surveillance/NewCase/Comfirmation/div_confirm_btn'))

    WebUI.delay(3)
}

if (isStandalone) {
    WebUI.closeBrowser()
}

