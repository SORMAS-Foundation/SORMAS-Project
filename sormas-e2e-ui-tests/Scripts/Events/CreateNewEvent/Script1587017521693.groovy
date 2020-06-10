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

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Events/partials/switchToEvents'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Events/NewEventView/div_New event_btn'))

WebUI.setText(findTestObject('Events/NewEventView/input_DateOfEvent_inputBox'), '05/15/2020')

WebUI.click(findTestObject('Events/NewEventView/label_Event_option'))

WebUI.click(findTestObject('Events/NewEventView/div_Disease_v-filterselect-button'))

WebUI.click(findTestObject('Events/NewEventView/td_Disease_COVID-19'))

WebUI.setText(findTestObject('Events/NewEventView/textarea_EventDescription_TArea'), 'Sensless Event')

WebUI.setText(findTestObject('Events/NewEventView/input_SourceFirstName_inputBox'), 'Karl')

WebUI.setText(findTestObject('Events/NewEventView/input_SourceLastName_inputBox'), 'Klammer')

WebUI.setText(findTestObject('Events/NewEventView/input_SourceTelephoneNo_inputBox'), '555-35271')

WebUI.click(findTestObject('Events/NewEventView/div_TypeOfPlace_v-filterselect-button'))

WebUI.click(findTestObject('Events/NewEventView/td_TypeOfPlace_Festivities_DDItem'))

//WebUI.setText(findTestObject('Events/NewEventView/input_SourceTelephoneNo_inputBox'), '555-35271')
WebUI.click(findTestObject('Events/NewEventView/textarea_AddressOrLandmark_TArea'))
WebUI.setText(findTestObject('Events/NewEventView/textarea_AddressOrLandmark_TArea'), 'Am Klammerberg 3')

WebUI.setText(findTestObject('Events/NewEventView/input_PostalCode_inptBox'), '12345')

WebUI.setText(findTestObject('Events/NewEventView/input_City_inputBox'), 'Klammhausen')

/*WebUI.click(findTestObject('Events/NewEventView/div_AreaType_v-filterselect-button'))

WebUI.click(findTestObject('Events/NewEventView/td_AreaType_Urban_DDItem'))*/

WebUI.click(findTestObject('Events/NewEventView/div_District_v-filterselect-button'))

WebUI.click(findTestObject('Events/NewEventView/td_Voreingestellter Landkreis'))

/*WebUI.click(findTestObject('Events/NewEventView/div_Community_v-filterselect-button'))

WebUI.click(findTestObject('Events/NewEventView/span_Community_CharlottenburgWilmersdorf-DDItem')) */

WebUI.click(findTestObject('Events/NewEventView/div_Save_btn'))

WebUI.delay(1)

if (isStandalone) {
    WebUI.closeBrowser()
}

