import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// PREPARE
WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/searchAndSelectContact'), [('key') : 'userName-B'], FailureHandling.STOP_ON_FAILURE)

String quarantineStart = '02/02/2020'
String quarantineEnd = '03/05/2020'

//WebDriver driver = DriverFactory.getWebDriver()
//driver.manage().window().setSize(new Dimension(2500, 2500))

// TESTCASE
WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible region_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_reg1'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible district_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_dist11'))

WebUI.scrollToElement(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Relationship with case_v-filterselect-button'), 2)
WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Relationship with case_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_Live in the same household'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Description of how contact took place'),
    'this is a test')
WebUI.delay(0.5)

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Quarantine_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_Home'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Quarantine start'), quarantineStart)

// WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Quarantine end'), quarantineEnd)

WebUI.scrollToElement(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Follow-up status comment_followUpComment'), 1)
WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Follow-up status comment_followUpComment'),
    'this is a test')
WebUI.delay(0.5)

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible contact officer_v-filterselect'))

//WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/span_Berlin CONTACT - Surveillance Officer'))

WebUI.click(findTestObject('ReusableORs/div_Save'))

// CHECK
WebUI.refresh()
//driver.manage().window().setSize(new Dimension(2500, 2500))
WebUI.delay(1)

String region = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible region'), 'value')
String district = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible district'), 'value')

if (!region.equalsIgnoreCase("reg1") || !district.equalsIgnoreCase("dist11")) {
	WebUI.closeBrowser()
	throw new StepFailedException('The region and/or district should equal "reg1". region: ' + region + ' district:' + district)
}

String relation = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Relationship with case'), 'value')
if (!relation.contains("same household")) {
	WebUI.closeBrowser()
	throw new StepFailedException('The relation should contain "same household". relation: ' + relation)
}

String contactDescription = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Description of how contact took place'), 'value')
if (!contactDescription.contains("this is a test")) {
	WebUI.closeBrowser()
	throw new StepFailedException('The description should contain "this is a test". description: ' + contactDescription)
}

String quarantine = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Quarantine'), 'value')
if (!quarantine.contains("Home")) {
	WebUI.closeBrowser()
	throw new StepFailedException('The qurantine should contain "Home". quarantine: ' +quarantine)
}

/*String startDate = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Quarantine start'), 'value')
if (!startDate.contains(quarantineStart)) {
	WebUI.closeBrowser()
	throw new StepFailedException('The qurantine start-Date is wrong. Expected:' +quarantineStart+ 'found:' +startDate)
}*/

String comment = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Follow-up status comment_followUpComment'), 'value')
if (!comment.contains("this is a test")) {
	WebUI.closeBrowser()
	throw new StepFailedException('The comment should contain "this is a test". comment: ' + comment)
}

/*String contact = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactData/input_Responsible contact officer'), 'value')
if (!contact.contains("Berlin CONTACT - Surveillance Officer, Contact Officer")) {
	WebUI.closeBrowser()
	throw new StepFailedException('The contact should equal "Berlin CONTACT - Surveillance Officer, Contact Officer". contact: ' + contact)
}*/

// CLEANUP
WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible region_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_'))

WebUI.scrollToElement(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Relationship with case_v-filterselect-button'), 2)
WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Relationship with case_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_'))

WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Description of how contact took place'),
    '')

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Quarantine_v-filterselect-button'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_'))

WebUI.scrollToElement(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Follow-up status comment_followUpComment'), 1)
WebUI.setText(findTestObject('Contacts/ContactInformationView/ChangeContactData/textarea_Follow-up status comment_followUpComment'),
    '')

/*WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/div_Responsible contact officer_v-filterselect'))

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactData/td_'))*/

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.closeBrowser()
