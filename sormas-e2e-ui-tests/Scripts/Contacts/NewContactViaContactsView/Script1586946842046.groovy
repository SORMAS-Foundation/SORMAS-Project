import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData

import com.hzi.Helper
import com.hzi.Table as Table
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testdata.TestData as TestData
import internal.GlobalVariable as GlobalVariable

// PREPARE
WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(1)


// TESTCASE
WebUI.click(findTestObject('Contacts/ContactsOverview/div_New contact'))

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_First name'), 'Aurelius')

// generate name because contacts cannot be deleted
String newContactLastName = Helper.generateString('Aurelius', 6)
println('generated lastname:' + newContactLastName)
WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_Last name'), newContactLastName)

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Disease of source case_v-filterselect-button'))

WebUI.click(findTestObject('Events/NewEventView/td_Disease_COVID-19'))

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Choose Case'))

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_Select Source Case_v-textfield v-widg_1dfb0a'), findTestData(GlobalVariable.gContactTestDataName).getValue(2, 2))

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Search case'))

WebUI.delay(1)

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/td_selectRow'))

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Confirm'))

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Save'))

// 'check if "Pick or create person" dialog is shown' ans select create-new-person
boolean checkDialog = WebUI.verifyElementPresent(findTestObject('Contacts/CasesView/NewContact/label_Select a matching person'), 2)
if (checkDialog) {
	WebUI.click(findTestObject('Contacts/CasesView/NewContact/label_Create a new person'))

	WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/pick_persion_save'))
}
WebUI.delay(1)

// CHECK
WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/span_Contacts list'))

WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), 
    newContactLastName)
WebUI.delay(1)

int numberOfRows = Table.getNumberOfTableRows()
if (numberOfRows != 1) {
	WebUI.closeBrowser()
	throw new com.kms.katalon.core.exception.StepFailedException('Expected one contact: ' + numberOfRows)
}

WebUI.closeBrowser()

