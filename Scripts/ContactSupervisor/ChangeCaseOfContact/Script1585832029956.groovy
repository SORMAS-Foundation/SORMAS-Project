import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// PREPARE
WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/searchAndSelectContact'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(1)

String epidNumberA = findTestData('ContactTestData').getValue(2, 3)
String epidNumberB = findTestData('ContactTestData').getValue(2, 4)


String savedEpidNumber = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))
println('saved epid-number:' + savedEpidNumber)

'determine the future epidNumber based on the saved one'
String futureEpidNumber = (savedEpidNumber == epidNumberA)? epidNumberB : epidNumberA
println('future epid-number:' + futureEpidNumber)

// TESTCASE - change case
WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_confirmationDlg_yes'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/changeCaseDlg_input_field'), 
    futureEpidNumber)

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_search_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_selectRow_action'))

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_confirm_button'))
WebUI.delay(1)

// CHECK
String epidNumberAfterChange = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))
println('displayed epidnumber after change: ' + epidNumberAfterChange )
if (epidNumberAfterChange != futureEpidNumber){
	WebUI.closeBrowser()
	throw new StepFailedException('The displayed EpidNumber does not equal the changed EpidNumber. displayed: ' + epidNumberAfterChange + ' saved:' + futureEpidNumber)
}

// TESTCASE - discard changes in caseChange Dialog
WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_confirmationDlg_yes'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/changeCaseDlg_input_field'), 
    savedEpidNumber)

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_search_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/changeCaseDlg_selectRow_action'))

WebUI.click(findTestObject('Contacts/ContactInformationView/div_Discard'))

// CHECK
epidNumberAfterChange = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))
println('displayed epidnumber after discard of changes: ' + epidNumberAfterChange )
if (epidNumberAfterChange != futureEpidNumber){
	WebUI.closeBrowser()
	throw new StepFailedException('The displayed EpidNumber should not have changed after . displayed: ' + epidNumberAfterChange + ' saved:' + futureEpidNumber)
}

// TESTCASE - discard after first confirmation dialog
WebUI.click(findTestObject('Contacts/ContactInformationView/contactView_changeCase_button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/div_No'))

// CHECK
epidNumberAfterChange = WebUI.getText(findTestObject('Contacts/ContactInformationView/contact_view_epidNumber'))
println('displayed epidnumber after discard of confirm dialog: ' + epidNumberAfterChange )
if (epidNumberAfterChange != futureEpidNumber){
	WebUI.closeBrowser()
	throw new StepFailedException('The displayed EpidNumber does not equal the changed EpidNumber. displayed: ' + epidNumberAfterChange + ' saved:' + futureEpidNumber)
}
