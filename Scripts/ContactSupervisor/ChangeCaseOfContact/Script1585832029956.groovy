import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// PREPARE
WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

if (checkChangeableEpid) {
	WebUI.callTestCase(findTestCase('ContactSupervisor/partials/searchAndSelectContact'), [('column') : 2, ('row') : 1], FailureHandling.STOP_ON_FAILURE)
} else {
	WebUI.callTestCase(findTestCase('ContactSupervisor/partials/searchAndSelectContact'), [('column') : 2, ('row') : 7],
		FailureHandling.STOP_ON_FAILURE)
}

WebUI.delay(1)

String unchangeableEpidNumberA = findTestData('ContactTestData').getValue(2, 5)
String unchangeableEpidNumberB = findTestData('ContactTestData').getValue(2, 6)

String caseNameA = findTestData('ContactTestData').getValue(2, 3)
String caseNameB = findTestData('ContactTestData').getValue(2, 4)

String savedEpidNumber = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))
println('saved epid-number:' + savedEpidNumber)

//boolean currentIsUnchangeable = savedEpidNumber =~ '\\w{3}-\\w{3}-\\w{3}-\\w{2}-\\d{3}'

'determine the future epidNumber based on the saved one'
String caseSearchString = 'none'
if (checkChangeableEpid) {
	String savedCasePerson = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_casePerson'))
	println('saved case-person: ' + savedCasePerson)
	caseSearchString = (savedCasePerson.equalsIgnoreCase(caseNameA))? caseNameB : caseNameA
} else {
	caseSearchString = (savedEpidNumber == unchangeableEpidNumberA)? unchangeableEpidNumberB : unchangeableEpidNumberA
}
println('case search string:' + caseSearchString)

// TESTCASE - change case
WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_confirmationDlg_yes'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/changeCaseDlg_input_field'), caseSearchString)

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_search_button'))

WebUI.delay(0.5)

String oldCaseEpidNumber = WebUI.getText(findTestObject('Contacts/ContactInformationView/changeCaseDlg_epidNumber_field'))

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_selectRow_action'))

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_confirm_button'))

WebUI.delay(1)

// CHECK
String epidNumberAfterChange = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))
println('displayed epidnumber after change: ' + epidNumberAfterChange)

if (epidNumberAfterChange == savedEpidNumber) {
	WebUI.closeBrowser()

	throw new StepFailedException((('The displayed EpidNumber should not be equal. displayed: ' + epidNumberAfterChange) +
	' changed:') + caseSearchString)
}

if (checkChangeableEpid) {
	// nothing to check, because the epid-number can increase 
	int oldCaseSequence = oldCaseEpidNumber.reverse().take(3).reverse().toInteger()
	int newCaseSequence = epidNumberAfterChange.reverse().take(3).reverse().toInteger()
	println('old-sequence: ' + oldCaseSequence + ' new-sequence: '+ newCaseSequence)
	if (newCaseSequence <= oldCaseSequence) {
		throw new StepFailedException((('The Epidnumber end sequence should have increased. displayed: ' + epidNumberAfterChange) +
			' oldCaseEpidNumber:') + oldCaseEpidNumber)
	}
} else {
	if (epidNumberAfterChange != caseSearchString) {
		WebUI.closeBrowser()
	
		throw new StepFailedException((('The displayed EpidNumber does not equal the changed EpidNumber. displayed: ' + epidNumberAfterChange) +
		' changed:') + caseSearchString)
	}
}


// TESTCASE - discard changes in caseChange Dialog
WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_confirmationDlg_yes'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/changeCaseDlg_input_field'), savedEpidNumber)

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_search_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_selectRow_action'))

WebUI.click(findTestObject('Contacts/ContactInformationView/div_Discard'))

// CHECK
String epidNumberAfterDiscard = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))

println('displayed epidnumber after discard of changes: ' + epidNumberAfterDiscard)

if (epidNumberAfterDiscard != epidNumberAfterChange) {
    WebUI.closeBrowser()

    throw new StepFailedException((('The displayed EpidNumber should not have changed after . displayed: ' + epidNumberAfterDiscard) + 
    ' after-first-change:') + epidNumberAfterChange)
}

// TESTCASE - discard after first confirmation dialog
WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/div_No'))

// CHECK
epidNumberAfterDiscard = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))

println('displayed epidnumber after discard of confirm dialog: ' + epidNumberAfterDiscard)

if (epidNumberAfterDiscard != epidNumberAfterChange) {
    WebUI.closeBrowser()

    throw new StepFailedException((('The displayed EpidNumber does not equal the changed EpidNumber. displayed: ' + epidNumberAfterDiscard) + 
    ' after-first-change:') + epidNumberAfterChange)
}

