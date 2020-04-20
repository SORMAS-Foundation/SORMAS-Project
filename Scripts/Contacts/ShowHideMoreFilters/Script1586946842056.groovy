import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(1)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_MoreLessFilters'))

WebUI.delay(1)

if (((((((((!(WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/div_Apply date filter'), 1)) || !(WebUI.verifyElementPresent(
    findTestObject('Contacts/ContactsOverview/input_ContactCaseDistrict'), 1))) || !(WebUI.verifyElementPresent(findTestObject(
        'Contacts/ContactsOverview/input_ResponsibleContact'), 1))) || !(WebUI.verifyElementPresent(findTestObject(
        'Contacts/ContactsOverview/input_ReportedBy'), 1))) || !(WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_FollowUpUntil'), 
    1))) || !(WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_Quarantine'), 1))) || 
!(WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/input_ByDate'), 1))) || !(WebUI.verifyElementPresent(
    findTestObject('Contacts/ContactsOverview/input_ContactDate'), 1))) || !(WebUI.verifyElementPresent(findTestObject(
        'Contacts/ContactsOverview/input_NewContactsFrom'), 1))) || !(WebUI.verifyElementPresent(findTestObject(
        'Contacts/ContactsOverview/input_NewContactsTo'), 1))) {
    WebUI.closeBrowser()

    throw new StepFailedException('Missing expected input field(s) in "More Filters" view')
}

WebUI.click(findTestObject('Contacts/ContactsOverview/div_MoreLessFilters'))

WebUI.delay(1)

if (((((((((!(WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/div_Apply date filter'), 1)) || !(WebUI.verifyElementNotPresent(
    findTestObject('Contacts/ContactsOverview/input_ContactCaseDistrict'), 1))) || !(WebUI.verifyElementNotPresent(findTestObject(
        'Contacts/ContactsOverview/input_ResponsibleContact'), 1))) || !(WebUI.verifyElementNotPresent(findTestObject('Contacts/ContactsOverview/input_ReportedBy'), 1))) || !(WebUI.verifyElementNotPresent(findTestObject(
        'Contacts/ContactsOverview/input_FollowUpUntil'), 1))) || !(WebUI.verifyElementNotPresent(findTestObject(
        'Contacts/ContactsOverview/input_Quarantine'), 1))) || !(WebUI.verifyElementNotPresent(findTestObject(
        'Contacts/ContactsOverview/input_ByDate'), 1))) || !(WebUI.verifyElementNotPresent(findTestObject(
        'Contacts/ContactsOverview/input_ContactDate'), 1))) || !(WebUI.verifyElementNotPresent(findTestObject(
        'Contacts/ContactsOverview/input_NewContactsFrom'), 1))) || !(WebUI.verifyElementNotPresent(findTestObject(
        'Contacts/ContactsOverview/input_NewContactsTo'), 1))) {
    WebUI.closeBrowser()

    throw new StepFailedException('Unexpected input fields from "More Filters" are present')
}

WebUI.closeBrowser()

