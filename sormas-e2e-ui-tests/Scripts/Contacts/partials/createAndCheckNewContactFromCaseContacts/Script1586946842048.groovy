import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.hzi.Helper as Helper
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

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

WebUI.click(findTestObject('ReusableORs/div_Save'))

// 'check if "Pick or create person" dialog is shown' ans select create-new-person
boolean checkDialog = WebUI.verifyElementPresent(findTestObject('ReusableORs/div_Select a matching person'), 
    2, FailureHandling.OPTIONAL)

println(checkDialog)

if (checkDialog) {
    WebUI.click(findTestObject('ReusableORs/div_Create a new person'))

    WebUI.click(findTestObject('ReusableORs/div_Save'))
}

// check if new contact exists in case-contacts and contacts itself
WebUI.click(findTestObject('Contacts/CasesView/span_Case contacts'))

int numberOfCaseContactsAfter = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)

println(numberOfCaseContactsAfter)

if (numberOfCaseContactsBefore != (numberOfCaseContactsAfter - 1)) {
    throw new StepFailedException(((('Expected case contacts to be increased by one: ' + numberOfCaseContactsBefore) + ' == ') + 
    numberOfCaseContactsAfter) + ' + 1')
}

WebUI.click(findTestObject('Login/MainView/menu_Contacts'))

WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), newContactLastName)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))

WebUI.delay(1)

int numberOfFilteredContacts = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)

println(numberOfFilteredContacts)

if (numberOfFilteredContacts != 1) {
    throw new StepFailedException('Expected one contact: ' + numberOfFilteredContacts)
}

