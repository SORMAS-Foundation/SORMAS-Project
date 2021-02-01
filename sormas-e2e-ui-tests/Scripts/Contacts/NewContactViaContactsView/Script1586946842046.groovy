import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper as Helper
import com.hzi.Table as Table
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

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

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Sex_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_Unknown'))

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Disease of source case_v-filterselect-button'))

WebUI.click(findTestObject('Events/NewEventView/td_Disease_COVID-19'))

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Choose Case'))

WebUI.setText(findTestObject('Contacts/ContactsOverview/NewContact/input_Select Source Case_v-textfield_search'), findTestData(
        GlobalVariable.gContactTestDataName).getValue(2, 2))

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Search case'))

WebUI.delay(1)

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/td_selectRow'))

WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/div_Confirm'))

WebUI.click(findTestObject('ReusableORs/div_Save'))

// 'check if "Pick or create person" dialog is shown' ans select create-new-person
boolean checkDialog = WebUI.verifyElementPresent(findTestObject('ReusableORs/div_Select a matching person'), 2, FailureHandling.OPTIONAL)

if (checkDialog) {
    WebUI.click(findTestObject('ReusableORs/div_Create a new person'))

    WebUI.click(findTestObject('ReusableORs/div_Save'))
}

WebUI.delay(1)

// CHECK
WebUI.click(findTestObject('Contacts/ContactsOverview/NewContact/span_Contacts list'))

WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), newContactLastName)

/*WebUI.click(findTestObject('Contacts/ContactsOverview/div_Show More Less Filters'))

WebUI.click(findTestObject('ReusableORs/Filters/span_Include contacts from other jurisdictions'))*/

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))

WebUI.delay(1)

int numberOfRows = Table.getNumberOfTableRows()

if (numberOfRows != 1) {
	WebUI.click(findTestObject('Contacts/ContactsOverview/div_Show More Less Filters'))
	
	WebUI.click(findTestObject('ReusableORs/Filters/span_Include contacts from other jurisdictions'))
	
	WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))
	int numberOfUpdatedRows = Table.getNumberOfTableRows()
	
	if (numberOfUpdatedRows != 1){
    WebUI.closeBrowser()
    throw new StepFailedException('Expected one contact: ' + numberOfRows)
}
}

WebUI.closeBrowser()

