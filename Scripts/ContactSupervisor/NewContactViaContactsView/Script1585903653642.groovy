import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.hzi.Table as Table
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// PREPARE
WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(1)


// TESTCASE
WebUI.click(findTestObject('Contacts/Page_SORMAS/conctactView_newContact_button'))

WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_First name of contact person_gwt-uid-22'), 'Aurelius')

// generate name because contacts cannot be deleted
String newContactLastName = Helper.generateString('Aurelius', 6)
println('generated lastname:' + newContactLastName)
WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_Last name of contact person_gwt-uid-10'), newContactLastName)

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Disease of source case_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/span_COVID-19'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Choose Case'))

WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_Select Source Case_v-textfield v-widg_1dfb0a'),
	'Hildegard von Bingen')

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Search case'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/td_Nachos Hospital'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Confirm'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Save'))

// 'check if "Pick or create person" dialog is shown' ans select create-new-person
boolean checkDialog = WebUI.verifyElementPresent(findTestObject('Contacts/Page_SORMAS/button_Find_matching_persons'), 2)
if (checkDialog) {
	WebUI.click(findTestObject('Contacts/Page_SORMAS/label_Create a new person'))

	WebUI.click(findTestObject('Contacts/Page_SORMAS/pick_persion_save'))
}
WebUI.delay(1)

// CHECK
WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/span_Contacts list'))

WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_New contact_v-textfield v-widget v-ha_73324d'), 
    newContactLastName)
WebUI.delay(1)

int numberOfRows = Table.getNumberOfTableRows()
if (numberOfRows != 1) {
	WebUI.closeBrowser()
	throw new com.kms.katalon.core.exception.StepFailedException('Expected one contact: ' + numberOfRows)
}

WebUI.closeBrowser()

