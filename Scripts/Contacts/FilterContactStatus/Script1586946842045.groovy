import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import com.hzi.Helper

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

TestObject tableObject = Helper.createTestObjectWithXPath('//table[@aria-rowcount]')

allCases = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println('All Cases:' + allCases)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Active contact'))
activeContacts = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println('activeContacts:' + activeContacts)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Converted to case'))
convertedToCases = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println('convertedToCases:' + convertedToCases)

WebUI.click(findTestObject('Contacts/ContactsOverview/dropped_contacts_filter'))
droppedCases = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println('droppedCases:' + droppedCases)


sumFilter = activeContacts + convertedToCases + droppedCases
println('Sum of all Filters:' + sumFilter)
if (allCases == sumFilter) {
	WebUI.closeBrowser()
} else {
	WebUI.closeBrowser()
	throw new com.kms.katalon.core.exception.StepFailedException('Expected sum of different filter rows to be equal to all-filter rows: ' + allCases +  ' != ' + sumFilter)
}



