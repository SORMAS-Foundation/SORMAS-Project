import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// PREPARE
WebUI.callTestCase(findTestCase('ContactSupervisor/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/searchAndSelectContact'), [('key') : 'userName-B'], FailureHandling.STOP_ON_FAILURE)


// TESTCASE
WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible region_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_Berlin'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible district_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_Berlin'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Relationship with case_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_Live in the same household'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Description of how contact took pl_3ad3bd'), 
    'this is a test')

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Quarantine_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_Home'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/button_Quarantine start_v-datefield-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/button_'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_27'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/button_Quarantine start_v-datefield-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/button__1'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_1'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Follow-up status comment_followUpComment'), 
    'this is a test')

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible contact officer_v-filtersel_edee33'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_Berlin CONTACT - Surveillance Officer _08c6e0'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Save'))

// CHECK
WebUI.refresh()
WebUI.delay(1)

String region = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible region_gwt-uid-207'), 'value')
String district = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible district_gwt-uid-203'), 'value')

if (!region.equalsIgnoreCase("Berlin") || !district.equalsIgnoreCase("Berlin")) {
	throw new StepFailedException('The region and/or district should equal "Berlin". region: ' + region + ' district:' + district)
}

String relation = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Relationship with case_gwt-uid-195'), 'value')
if (!relation.contains("same household")) {
	throw new StepFailedException('The relation should contain "same household". relation: ' + relation)
}

String contactDescription = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Description of how contact took pl_3ad3bd'), 'value')
if (!contactDescription.contains("this is a test")) {
	throw new StepFailedException('The description should contain "this is a test". description: ' + contactDescription)
}

String quarantine = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Quarantine_gwt-uid-205'), 'value')
if (!quarantine.contains("Home")) {
	throw new StepFailedException('The qurantine should contain "Home". quarantine: ' + quarantine)
}

String comment = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Follow-up status comment_followUpComment'), 'value')
if (!comment.contains("this is a test")) {
	throw new StepFailedException('The comment should contain "this is a test". comment: ' + comment)
}

String contact = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible contact officer_gwt-uid-199'), 'value')
if (!contact.contains("Berlin CONTACT - Surveillance Officer, Contact Officer")) {
	throw new StepFailedException('The contact should equal "Berlin CONTACT - Surveillance Officer, Contact Officer". contact: ' + contact)
}

// CLEANUP

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible region_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Relationship with case_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Description of how contact took pl_3ad3bd'), 
    '')

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Quarantine_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Follow-up status comment_followUpComment'), 
    '')

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible contact officer_v-filtersel_edee33'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Save'))



//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible region_gwt-uid-207'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible district_gwt-uid-203'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Relationship with case_gwt-uid-195'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Quarantine_gwt-uid-205'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Quarantine_v-filterselect-button'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_Home'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Quarantine start_gwt-uid-228'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Quarantine end_gwt-uid-226'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible contact officer_gwt-uid-199'))

//WebUI.closeBrowser()

