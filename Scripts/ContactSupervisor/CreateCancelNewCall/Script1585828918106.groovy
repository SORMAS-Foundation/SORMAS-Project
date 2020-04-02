import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.hzi.Table
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// PREPARE
WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)

'userName\n'
WebUI.setText(findTestObject('Contacts/Page_SORMAS/contact_search_field_name'),
	findTestData('ContactTestData').getValue(2, 1))
WebUI.delay(1)

WebUI.click(Helper.createTestObjectWithXPath('//table[@aria-rowcount]//a'))


// TESTCASE CREATE
WebUI.click(findTestObject('Contacts/Page_SORMAS/contact_view_followUpCalls'))
WebUI.delay(1)
int numberOfCalls = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Contacts/Page_SORMAS/contact_view_followUpCalls_newCall'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/newCall_dlg_label_nichtKooperativ'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/newCall_dlg_save_button'))
int numberOfCallsAfterSave = Table.getNumberOfTableRows()

// TESTCASE CANCEL
WebUI.click(findTestObject('Contacts/Page_SORMAS/contact_view_followUpCalls_newCall'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/newCall_dlg_label_nichtKooperativ'))

WebUI.click(findTestObject('Contacts/Page_SORMAS/newCall_dlg_cancel_button'))


// CHECK
int numberOfCallsAfterCancel = Table.getNumberOfTableRows()
if (numberOfCalls != numberOfCallsAfterSave -1 && numberOfCallsAfterCancel == numberOfCallsAfterSave){
	WebUI.closeBrowser()
	throw new StepFailedException('Expected different number of rows. initial count:' + numberOfCalls + ' after save:' + numberOfCallsAfterSave + ' after cancel:' + numberOfCallsAfterCancel)
}

WebUI.closeBrowser()

