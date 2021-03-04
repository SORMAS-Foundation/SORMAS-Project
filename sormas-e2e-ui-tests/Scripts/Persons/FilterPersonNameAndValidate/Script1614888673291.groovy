import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper as Helper
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Login/partials/LoginAsNationalUser'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Persons/partials/switchToPersons'), [:], FailureHandling.STOP_ON_FAILURE)

'userName\n'
WebUI.setText(findTestObject('Persons/input_nameAddressPhoneEmailLike'), TestDataConnector.getValueByKey(GlobalVariable.gContactTestDataName, 
        'caseName-A'))

def enteredPersonName = TestDataConnector.getValueByKey(GlobalVariable.gContactTestDataName, 'caseName-A')

WebUI.delay(1)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))

WebUI.delay(2)

int rows = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(Helper.createTestObjectWithXPath('//table[@aria-rowcount]'))

if (rows == 1) {
    WebUI.comment('Found 1 related search in Person directory')

    WebUI.click(findTestObject('Surveillance/SearchView/a_Search_Entry_link'))

    WebUI.waitForElementVisible(findTestObject('Persons/div_Person information'), 10)

    WebUI.click(findTestObject('Persons/div_See cases for this person'))

    WebUI.verifyElementPresent(findTestObject('Login/MainView/title_CaseDirectory'), 10)

    WebUI.click(findTestObject('Surveillance/SearchView/a_Search_Entry_link'))

    WebUI.waitForElementVisible(findTestObject('Surveillance/CaseView/div_CasePerson_tab'), 10)

    WebUI.click(findTestObject('Surveillance/CaseView/div_CasePerson_tab'))

    def firstName = WebUI.getAttribute(findTestObject('Events/EditEventView/input_AddPerson_FirstName_inputBox'), 'value')

    def lastName = WebUI.getAttribute(findTestObject('Events/EditEventView/input_AddPerson_LastName_inputBox'), 'value')

    def personName = "$firstName  $lastName"
    
	enteredPersonName.equals(personName)
	
	WebUI.closeBrowser()
} else {
    WebUI.closeBrowser()

    throw new com.kms.katalon.core.exception.StepFailedException('Expected one row but found ' + rows)
}

