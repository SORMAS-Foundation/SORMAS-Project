import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.hzi.Helper as Helper
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), findTestData(GlobalVariable.gContactTestDataName).getValue(
        2, 1))

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))

WebUI.delay(1)

WebUI.click(Helper.createTestObjectWithXPath('//table[@aria-rowcount]//a'))

WebUI.click(findTestObject('Surveillance/CaseView/div_Contacts_tab'))

WebUI.callTestCase(findTestCase('Contacts/partials/createAndCheckNewContactFromCaseContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.closeBrowser()

