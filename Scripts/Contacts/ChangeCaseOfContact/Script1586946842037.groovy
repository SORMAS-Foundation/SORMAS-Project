import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import com.hzi.TestDataConnector as TestDataConnector

// PREPARE
WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

if (checkChangeableEpid) {
    WebUI.callTestCase(findTestCase('Contacts/partials/searchAndSelectContact'), [('key') : 'userName-A'], FailureHandling.STOP_ON_FAILURE)
} else {
    WebUI.callTestCase(findTestCase('Contacts/partials/searchAndSelectContact'), [('key') : 'userName-B'], FailureHandling.STOP_ON_FAILURE)
}

WebUI.delay(1)

String unchangeableEpidNumberA = TestDataConnector.getValueByKey(GlobalVariable.gContactTestDataName, 'unchangable-epid-number-A')

String unchangeableEpidNumberB = TestDataConnector.getValueByKey(GlobalVariable.gContactTestDataName, 'unchangable-epid-number-B')

String caseNameA = TestDataConnector.getValueByKey(GlobalVariable.gContactTestDataName, 'caseName-A')

String caseNameB = TestDataConnector.getValueByKey(GlobalVariable.gContactTestDataName, 'caseName-B')

WebUI.maximizeWindow()
//String savedEpidNumber = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))
String savedEpidNumber = WebUI.getText(findTestObject('Contacts/ContactInformationView/div_CaseID info'))
println('saved epid-number in contact: ' +savedEpidNumber)

'determine the future epidNumber based on the saved one'
String caseSearchString = 'none'

if (checkChangeableEpid) {
    String savedCasePerson = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_casePerson'))

    println('saved case-person in contact: ' + savedCasePerson)

    caseSearchString = savedCasePerson.equalsIgnoreCase(caseNameA) ? caseNameB : caseNameA
} else {
    caseSearchString = savedEpidNumber == unchangeableEpidNumberA ? unchangeableEpidNumberB : unchangeableEpidNumberA
}

println('case search string: ' + caseSearchString)

// TESTCASE - change case
WebUI.click(findTestObject('Contacts/ContactInformationView/button_div_Change Case'))

WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_confirmationDlg_yes'))

// WebUI.waitForElementPresent(findTestObject('Contacts/ContactInformationView/changeCaseDlg_input_field'), 2)
WebUI.delay(1)
// WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_input_field'))
WebUI.setText(findTestObject('Contacts/ContactInformationView/changeCaseDlg_input_field'), caseSearchString)


WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_search_button'))

WebUI.delay(0.5)

//String oldCaseEpidNumber = WebUI.getText(findTestObject('Contacts/ContactInformationView/changeCaseDlg_epidNumber_field'))
String oldCaseEpidNumber = WebUI.getText(findTestObject('Contacts/ContactInformationView/caseID_field'))

println('new case - old-epid-number (from case search):' +oldCaseEpidNumber)

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_selectRow_action'))

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_confirm_button'))

WebUI.delay(1)

// CHECK
String epidNumberAfterChange = WebUI.getText(findTestObject('Contacts/ContactInformationView/div_CaseID info'))

println('displayed epidnumber after change in contact: ' + epidNumberAfterChange)

if (epidNumberAfterChange == savedEpidNumber) {
    WebUI.closeBrowser()

    throw new StepFailedException((('The displayed EpidNumber should not be equal. displayed:' + epidNumberAfterChange) + 
    ' changed:') + caseSearchString) 
}

if (checkChangeableEpid) {
    // nothing to check, because the epid-number can increase 
    int oldCaseSequence = oldCaseEpidNumber.reverse().take(3).reverse().toInteger()

    int newCaseSequence = epidNumberAfterChange.reverse().take(3).reverse().toInteger()

    println((('old-sequence: ' + oldCaseSequence) + ' new-sequence: ') + newCaseSequence)

    if (newCaseSequence <= oldCaseSequence) {
        throw new StepFailedException((('The Epidnumber end sequence should have increased. displayed: ' + epidNumberAfterChange) + 
        ' oldCaseEpidNumber:') + oldCaseEpidNumber)
    }
} else {
    if (epidNumberAfterChange != caseSearchString) {
        WebUI.closeBrowser()

       throw new StepFailedException((('The displayed EpidNumber does not equal the changed EpidNumber. displayed: ' + 
        epidNumberAfterChange) + ' changed:') + caseSearchString) 
    }
}

// TESTCASE - discard changes in caseChange Dialog
WebUI.click(findTestObject('Contacts/ContactInformationView/button_div_Change Case'))

WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_confirmationDlg_yes'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/changeCaseDlg_input_field'), savedEpidNumber)

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_search_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_selectRow_action'))

WebUI.click(findTestObject('Contacts/ContactInformationView/div_Discard'))

// CHECK
String epidNumberAfterDiscard = WebUI.getText(findTestObject('Contacts/ContactInformationView/div_CaseID info'))

println('displayed epidnumber after discard of changes: ' + epidNumberAfterDiscard)

if (epidNumberAfterDiscard != epidNumberAfterChange) {
    WebUI.closeBrowser()

    throw new StepFailedException((('The displayed EpidNumber should not have changed after . displayed: ' + epidNumberAfterDiscard) + 
    ' after-first-change:') + epidNumberAfterChange)
}

// TESTCASE - discard after first confirmation dialog
WebUI.click(findTestObject('Contacts/ContactInformationView/button_div_Change Case'))

WebUI.click(findTestObject('Contacts/ContactInformationView/div_No'))

// CHECK
epidNumberAfterDiscard = WebUI.getText(findTestObject('Contacts/ContactInformationView/div_CaseID info'))

println('displayed epidnumber after discard of confirm dialog: ' + epidNumberAfterDiscard)

if (epidNumberAfterDiscard != epidNumberAfterChange) {
    WebUI.closeBrowser()

    throw new StepFailedException((('The displayed EpidNumber does not equal the changed EpidNumber. displayed: ' + epidNumberAfterDiscard) + 
    ' after-first-change:') + epidNumberAfterChange)
}

WebUI.closeBrowser()
