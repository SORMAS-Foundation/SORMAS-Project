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

import com.hzi.Helper

tableObject = Helper.createTestObjectWithXPath('//table[@aria-rowcount]')
int numberOfCaseContactsBefore = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println(numberOfCaseContactsBefore)

WebUI.click(findTestObject('Contacts/CasesView/NewContact/div_New contact'))

// from here - identical with NewContactViaContacts
WebUI.setText(Helper.createTestObjectWithXPath('//div[@location="firstName"]//input'), 'Aurelius')

// generate name because contacts cannot be deleted
String newContactLastName = Helper.generateString('Aurelius', 6)

WebUI.setText(Helper.createTestObjectWithXPath('//div[@location="lastName"]//input'), newContactLastName)

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Sex_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_Unknown'))

WebUI.click(findTestObject('Contacts/CasesView/NewContact/button_Date_last_contact'))

WebUI.click(findTestObject('Contacts/CasesView/NewContact/calendar_back_month_button'))

WebUI.click(findTestObject('Contacts/CasesView/NewContact/calendar_select_30'))

WebUI.click(findTestObject('Contacts/CasesView/NewContact/label_selection_direct_physical_contact'))

WebUI.click(findTestObject('Contacts/CasesView/NewContact/new_contact_dialog_save'))

// 'check if "Pick or create person" dialog is shown' ans select create-new-person
boolean checkDialog = WebUI.verifyElementPresent(findTestObject('Contacts/CasesView/NewContact/label_Select a matching person'), 2, FailureHandling.OPTIONAL)
println(checkDialog)
if (checkDialog) {
	WebUI.click(findTestObject('Contacts/CasesView/NewContact/label_Create a new person'))

	WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/pick_person_save'))
}

// check if new contact exists in case-contacts and contacts itself
WebUI.click(findTestObject('Contacts/CasesView/span_Case contacts'))

int numberOfCaseContactsAfter = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println(numberOfCaseContactsAfter)
if (numberOfCaseContactsBefore != (numberOfCaseContactsAfter - 1)) {
	throw new com.kms.katalon.core.exception.StepFailedException(((('Expected case contacts to be increased by one: ' +
	numberOfCaseContactsBefore) + ' == ') + numberOfCaseContactsAfter) + ' + 1')
}

WebUI.click(findTestObject('Contacts/MainView/menu_Contacts'))

WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), newContactLastName)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))
WebUI.delay(1)

int numberOfFilteredContacts = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println(numberOfFilteredContacts)
if (numberOfFilteredContacts != 1) {
	throw new com.kms.katalon.core.exception.StepFailedException('Expected one contact: ' + numberOfFilteredContacts)
}
