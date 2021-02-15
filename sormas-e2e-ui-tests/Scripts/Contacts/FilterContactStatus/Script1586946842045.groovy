import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

TestObject tableObject = Helper.createTestObjectWithXPath('//table[@aria-rowcount]')

allCases = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println('All Cases:' + allCases)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Active contact'))
WebUI.delay(2)
activeContacts = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println('activeContacts:' + activeContacts)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Converted to case'))
WebUI.delay(2)
convertedToCases = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(tableObject)
println('convertedToCases:' + convertedToCases)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Dropped'))
WebUI.delay(2)
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



