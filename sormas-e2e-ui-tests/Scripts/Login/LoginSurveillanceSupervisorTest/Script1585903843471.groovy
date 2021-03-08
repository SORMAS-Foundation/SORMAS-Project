import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

try {
    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Dashboard'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Tasks'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Cases'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_mSERS'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Contacts'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Events'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Samples'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Reports'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Statistics'), 2)

	WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Persons'), 2)
	
    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_About'), 2)

    WebUI.verifyElementPresent(findTestObject('Login/MainView/menu_Logout'), 2)
}
finally { 
    WebUI.closeBrowser()
}

