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

WebUI.click(findTestObject('Contacts/Page_SORMAS/div_New contact'))

// from here - identical with NewContactViaContacts
WebUI.setText(Helper.createTestObjectWithXPath('//div[@location="firstName"]//input'), 'Aurelius')

// generate name because contacts cannot be deleted
String newContactLastName = Helper.generateString('Aurelius', 6)

WebUI.setText(Helper.createTestObjectWithXPath('//div[@location="lastName"]//input'), newContactLastName)

WebUI.click(findTestObject('Contacts/Page_SORMAS/button_Date_last_contact'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/calendar_back_month_button'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/calendar_select_30'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/label_selection_direct_physical_contact'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/new_contact_dialog_save'))

// 'check if "Pick or create person" dialog is shown' ans select create-new-person
boolean checkDialog = WebUI.verifyElementPresent(findTestObject('Contacts/Page_SORMAS/button_Find_matching_persons'), 2)
if (checkDialog) {
	WebUI.click(findTestObject('Contacts/Page_SORMAS/label_Create a new person'))

	WebUI.click(findTestObject('Contacts/Page_SORMAS/pick_persion_save'))
}

// check if new contact exists in case-contacts and contacts itself
WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/span_Case contacts'))

int numberOfCaseContactsAfter = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println(numberOfCaseContactsAfter)
if (numberOfCaseContactsBefore != (numberOfCaseContactsAfter - 1)) {
	throw new com.kms.katalon.core.exception.StepFailedException(((('Expected case contacts to be increased by one: ' +
	numberOfCaseContactsBefore) + ' == ') + numberOfCaseContactsAfter) + ' + 1')
}

WebUI.click(findTestObject('Contacts/Page_SORMAS/menu_Contacts'))

WebUI.setText(findTestObject('Contacts/Page_SORMAS/contact_search_field_name'), newContactLastName)
Thread.sleep(1000)

int numberOfFilteredContacts = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println(numberOfFilteredContacts)
if (numberOfFilteredContacts != 1) {
	throw new com.kms.katalon.core.exception.StepFailedException('Expected one contact: ' + numberOfFilteredContacts)
}
