import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.hzi.Table
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

// PREPARE
WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)

'userName\n'
WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'),
	findTestData(GlobalVariable.gContactTestDataName).getValue(2, 1))
WebUI.delay(1)

WebUI.click(Helper.createTestObjectWithXPath('//table[@aria-rowcount]//a'))


// TESTCASE CREATE
WebUI.click(findTestObject('Contacts/ContactInformationView/div_Follow-up visits'))
WebUI.delay(1)
int numberOfCalls = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Contacts/ContactInformationView/div_New visit'))

WebUI.click(findTestObject('Contacts/ContactInformationView/newCall_dlg_label_nichtKooperativ'))

WebUI.click(findTestObject('Contacts/ContactInformationView/button_div_Save'))
int numberOfCallsAfterSave = Table.getNumberOfTableRows()

// TESTCASE CANCEL
WebUI.click(findTestObject('Contacts/ContactInformationView/div_New visit'))

WebUI.click(findTestObject('Contacts/ContactInformationView/newCall_dlg_label_nichtKooperativ'))

WebUI.click(findTestObject('Contacts/ContactInformationView/button_div_Discard'))


// CHECK
int numberOfCallsAfterCancel = Table.getNumberOfTableRows()
if (numberOfCalls != numberOfCallsAfterSave -1 && numberOfCallsAfterCancel == numberOfCallsAfterSave){
	WebUI.closeBrowser()
	throw new StepFailedException('Expected different number of rows. initial count:' + numberOfCalls + ' after save:' + numberOfCallsAfterSave + ' after cancel:' + numberOfCallsAfterCancel)
}

WebUI.closeBrowser()

