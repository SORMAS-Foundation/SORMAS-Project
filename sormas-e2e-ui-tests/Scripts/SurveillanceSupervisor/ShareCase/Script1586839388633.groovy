import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.WebElement

import com.hzi.Helper
import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

// THIS TEST IS OBSOLETE BECAUSE THE TESTED FUNCTIONALITY WAS REMOVED
// THE TEST WAS REMOVED FROM ALL SUITES AND COLLECTIONS

// PREPARE
WebUI.openBrowser(GlobalVariable.gUrl)

WebUI.waitForPageLoad(3)

// Test needs to run twice to check both directions of sharing a case (activation and deactivation)
// because we do not have control over the test data we do not know if the sharing is activated or not
for (int i=0; i < 2; i++){
	println('starting test run: ' + (i + 1))
	String numberOfCases = WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/DashboardNumberOfCasesInYear'), [:], FailureHandling.STOP_ON_FAILURE)
	
	println('NumberOfCases: ' + numberOfCases)
	
	WebUI.click(findTestObject('Login/span_Logout_link'))
		
	// TESTCASE
	'Login as another Surveillance Supervisor'
	WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)
	
	WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)
	
	String firstName = TestDataConnector.getValueByKey('GenericUsers', 'first_name_case')
	
	String lastName = TestDataConnector.getValueByKey('GenericUsers', 'last_name_case')
	
	WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/FilterCaseByPersonName'), [('personName') : (firstName + 
	        ' ') + lastName], FailureHandling.STOP_ON_FAILURE)
	
	WebElement checkBox = Helper.findChildElement(findTestObject('Surveillance/CaseView/span_shareCase'), 'input')
	String checked = checkBox.getAttribute("checked")
	boolean wasChecked = true
	if (checked == null) {
		wasChecked = false
	}
	println('sharing case (before): ' + wasChecked)
	
	WebUI.click(findTestObject('Surveillance/CaseView/checkBox_shareCase'))
	
	WebUI.click(findTestObject('Surveillance/CaseView/div_Save_btn'))
	
	
	// CHECK
	WebUI.click(findTestObject('Login/span_Logout_link'))
	
	String newNumberOfCases = WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/DashboardNumberOfCasesInYear'), [:], 
	    FailureHandling.STOP_ON_FAILURE)
	
	println('new number of cases: ' + newNumberOfCases)
	
	if (wasChecked && ((numberOfCases.toInteger() -1) != newNumberOfCases.toInteger())) {
		throw new StepFailedException('After unchecking share-Case the number of new cases should be lower by one. oldCases: ' + numberOfCases + ' newCases: ' + newNumberOfCases)
	} else if (!wasChecked && ((numberOfCases.toInteger() +1) != newNumberOfCases.toInteger())) {
		throw new StepFailedException('After checking share-Case the number of new cases should be higher by one. oldCases: ' + numberOfCases + ' newCases: ' + newNumberOfCases)
	}
	
	WebUI.click(findTestObject('Login/span_Logout_link'))
	println('test run finished')
}

WebUI.closeBrowser()

