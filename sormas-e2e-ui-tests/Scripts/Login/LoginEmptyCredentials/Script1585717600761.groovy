import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginActions'), [('Password') : '', ('Username') : ''], FailureHandling.STOP_ON_FAILURE)

try {
    WebUI.verifyElementNotPresent(findTestObject('Login/div_Logout'), 2)
}
finally { 
    WebUI.closeBrowser()
}

