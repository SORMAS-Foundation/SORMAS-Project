import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.hzi.Helper as Helper
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
if (create) {
    WebUI.click(findTestObject('ReusableORs/div_Save_modalWindow'))
} else {
    WebUI.click(findTestObject('Contacts/ContactInformationView/newTask_dlg_cancel_button'))
}

WebUI.click(findTestObject('Contacts/ContactInformationView/div_Contacts list'))

WebUI.delay(1)

