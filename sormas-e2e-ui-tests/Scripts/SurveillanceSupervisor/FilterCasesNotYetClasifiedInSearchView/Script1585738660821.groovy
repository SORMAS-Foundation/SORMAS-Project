import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/partials/SwitchToCases'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Samples/MainView/filterselect-button-classification'))

WebUI.click(findTestObject('Object Repository/Samples/MainView/span_Not yet classified'))

def attribute = WebUI.getAttribute(findTestObject('Surveillance/SearchView/table_SearchResult_table'), 'aria-rowcount')

attribute = (attribute.toInteger() - 1).toString()

WebUI.verifyMatch(attribute, '9', false, FailureHandling.OPTIONAL)

if (isStandalone) {
    WebUI.closeBrowser()
}

