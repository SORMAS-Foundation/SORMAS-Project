import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.exception.StepFailedException as StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

try {
    //WebUI.verifyElementNotPresent(findTestObject('Surveillance/SearchView/Filter/newCasesDate_inputBox'), 3)

    WebUI.verifyElementNotPresent(findTestObject('Surveillance/SearchView/Filter/span_Only cases without geo coordinates'), 
        3)
}
catch (Exception e) {
	WebUI.closeBrowser()
	
	throw new StepFailedException("Specific fields shouldn't be present before 'more filters' is clicked")
} 
finally { 
}

if (WebUI.verifyElementPresent(findTestObject('Contacts/ContactsOverview/div_Show More Less Filters'), 3)) {
    WebUI.click(findTestObject('Contacts/ContactsOverview/div_Show More Less Filters'))

	
	try {
		//WebUI.verifyElementPresent(findTestObject('Surveillance/SearchView/Filter/newCasesDate_inputBox'), 3)
	
		WebUI.verifyElementPresent(findTestObject('Surveillance/SearchView/Filter/span_Only cases without geo coordinates'), 3)
	}
	catch (Exception e) {
		WebUI.closeBrowser()
		
		throw new StepFailedException("Specific fields should be present after 'more filters' is clicked")
	}
	finally {
	}
}

WebUI.closeBrowser()

