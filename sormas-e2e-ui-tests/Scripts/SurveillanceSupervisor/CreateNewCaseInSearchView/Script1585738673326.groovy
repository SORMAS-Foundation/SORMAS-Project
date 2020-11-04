import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import org.openqa.selenium.Keys as Keys
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Surveillance/NewCaseView/div_NewCase_btn'))

WebUI.click(findTestObject('Surveillance/NewCaseView/div_Disease_v-filterselect-button'))

WebUI.click(findTestObject('Surveillance/NewCaseView/td_COVID-19'))

WebUI.click(findTestObject('Surveillance/NewCaseView/div_Region_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCaseView/td_Voreingestellte Region'))

WebUI.click(findTestObject('Surveillance/NewCaseView/div_District_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCaseView/td_Voreingestellter Landkreis'))

WebUI.click(findTestObject('Surveillance/NewCaseView/div_FacilityorHome'))

WebUI.check(findTestObject('Surveillance/NewCaseView/span_Home'))

WebUI.setText(findTestObject('Surveillance/NewCaseView/input_Place description_healthFacilityDetails'), 'own home')

String firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_case')

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_First name'), firstName)

String lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_case')

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_Last name'), lastName)

WebUI.setText(findTestObject('Surveillance/NewCaseView/dateOfReport_inputBox'), '01/04/2020')

WebUI.setText(findTestObject('Surveillance/NewCaseView/dateOfSymptomOnset_inputBox'), '03/26/2020')

WebUI.setText(findTestObject('Surveillance/NewCaseView/div_DateOfBirthYear_inputBox'), '2000')

WebUI.sendKeys(findTestObject('Surveillance/NewCaseView/div_DateOfBirthYear_inputBox'), Keys.chord(Keys.ENTER))

WebUI.setText(findTestObject('Surveillance/NewCaseView/div_DateOfBirthMonth_inputBox'), 'June')

WebUI.setText(findTestObject('Surveillance/NewCaseView/div_DateOfBirthMonth_inputBox'), Keys.chord(Keys.ENTER))

WebUI.setText(findTestObject('Surveillance/NewCaseView/div_DateOfBirthDay_inputBox'), '7')

WebUI.setText(findTestObject('Surveillance/NewCaseView/div_DateOfBirthDay_inputBox'), Keys.chord(Keys.ENTER))

WebUI.click(findTestObject('Surveillance/NewCaseView/div_Sex_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCaseView/td_Female'))

WebUI.click(findTestObject('Surveillance/NewCaseView/div_PresentConditionOfPerson_DDBox'))

WebUI.click(findTestObject('Surveillance/NewCaseView/div_PresentConditionOfPerson_alive_DDItem'))

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.delay(3)

if (WebUI.verifyElementPresent(findTestObject('Surveillance/NewCaseView/Comfirmation/div_check_confimation_lbl'), 5)) {
    
    WebUI.click(findTestObject('ReusableORs/div_Select a matching person'))
    //WebUI.click(findTestObject('Surveillance/NewCaseView/Comfirmation/select_firstcell'))

    WebUI.click(findTestObject('Surveillance/NewCaseView/Comfirmation/div_confirm_btn'))

    WebUI.delay(3)
}

if (isStandalone) {
    WebUI.closeBrowser()
}

