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
WebUI.click(findTestObject('Contacts/Page_SORMAS/button_kontakt_newTask'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/newTask_dlg_taskType'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Aufgabentyp_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/span_Kontaktuntersuchung'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/newTask_dlg_Zugewiesen_dropDown'))

WebUI.setText(findTestObject('Contacts/Page_SORMAS/newTask_dlg_zugewiesen_button'), 'berlin')

WebUI.click(findTestObject('Contacts/Page_SORMAS/newTask_dlg_zugewiesen_dropDown_selection_berlin'))

WebUI.setText(findTestObject('Contacts/Page_SORMAS/newTask_dlg_zugewiesen_button'), 'Berlin CONTACT - Surveillance Officer, Contact Officer (41)')

if (create){
	WebUI.click(findTestObject('Contacts/Page_SORMAS/newTask_dlg_save_button'))
} else {
	WebUI.click(findTestObject('Contacts/Page_SORMAS/newTask_dlg_cancel_button'))
}

WebUI.click(findTestObject('Contacts/Page_SORMAS/contact_view_kontaktliste_link'))
WebUI.delay(1)


