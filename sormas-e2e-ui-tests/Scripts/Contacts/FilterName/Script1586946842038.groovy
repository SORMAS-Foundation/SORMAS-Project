import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.hzi.TestDataConnector
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

'userName\n'
//WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), 
//    findTestData('defaultContactTestData').getValue(2, 1))
WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), TestDataConnector.getValueByKey(GlobalVariable.gContactTestDataName, "userName-B"))

WebUI.delay(1)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))

WebUI.delay(2)

int rows = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(Helper.createTestObjectWithXPath('//table[@aria-rowcount]'))

if (rows == 1) {
    WebUI.closeBrowser()
} else {
    WebUI.closeBrowser()
    throw new com.kms.katalon.core.exception.StepFailedException('Expected one row but found ' + rows)
}

