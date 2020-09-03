import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver

import com.hzi.Helper
import com.hzi.Table
import com.hzi.TableContent
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI


WebUI.click(Helper.createTestObjectWithXPath('//table[@aria-rowcount]//a'))

'Create new Task\n'
WebUI.click(findTestObject('Tasks/button_div_New task'))

WebUI.waitForElementPresent(findTestObject('Contacts/ContactInformationView/newTask_dlg_taskType'), 2)

WebUI.click(findTestObject('Contacts/ContactInformationView/newTask_dlg_taskType'))

WebUI.click(findTestObject('Contacts/ContactInformationView/div_Aufgabentyp_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/span_Kontaktuntersuchung'))

WebUI.click(findTestObject('Contacts/ContactInformationView/newTask_dlg_Zugewiesen_dropDown'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/newTask_dlg_zugewiesen_button'), 'Surveillance SUPERVISOR')

WebUI.click(findTestObject('Contacts/ContactInformationView/span_Surveillance SUPERVISOR - berwachungsleitung (2)'))

//WebUI.setText(findTestObject('Contacts/ContactInformationView/newTask_dlg_zugewiesen_button'), 'Berlin CONTACT - Surveillance Officer, Contact Officer (41)')

if (create){
	WebUI.click(findTestObject('Contacts/ContactInformationView/newTask_dlg_save_button'))
} else {
	WebUI.click(findTestObject('Contacts/ContactInformationView/newTask_dlg_cancel_button'))
}

WebUI.click(findTestObject('Contacts/ContactInformationView/div_Contacts list'))
WebUI.delay(1)


