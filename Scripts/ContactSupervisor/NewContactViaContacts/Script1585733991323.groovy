import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/LoginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/SwitchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.setText(findTestObject('Contacts/Page_SORMAS/contact_search_field_name'), findTestData('ContactTestData').getValue(
        2, 1))

Thread.sleep(1000)

WebUI.click(Helper.createTestObjectWithXPath('//table[@aria-rowcount]//a'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/span_Case contacts'))


int numberOfCaseContactsBefore = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(Helper.createTestObjectWithXPath('//table[@aria-rowcount]'))
println(numberOfCaseContactsBefore)

// create new contact
WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_New contact'))

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

WebUI.click(findTestObject('Contacts/Page_SORMAS/span_Contacts'))

WebUI.setText(findTestObject('Contacts/Page_SORMAS/contact_search_field_name'), newContactLastName)

int numberOfFilteredContacts = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println(numberOfFilteredContacts)
if (numberOfFilteredContacts != 1) {
    throw new com.kms.katalon.core.exception.StepFailedException('Expected one contact: ' + numberOfFilteredContacts)
}

