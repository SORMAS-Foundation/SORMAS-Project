import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.scrollToElement(findTestObject('Login/MainView/menu_Settings'), 2)

WebUI.click(findTestObject('Login/MainView/menu_Settings'))

WebUI.click(findTestObject('Login/SettingsDialog/div_language_v-filterselect-button'))

WebUI.click(findTestObject('Login/SettingsDialog/td_English'))

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.delay(3)

WebUI.waitForPageLoad(20)

if (WebUI.waitForElementVisible(findTestObject('Login/SettingsDialog/warning popup'), 5, FailureHandling.OPTIONAL)) {
    WebUI.click(findTestObject('Login/SettingsDialog/warning popup'))

    WebUI.click(findTestObject('ReusableORs/div_Discard'))

    WebUI.callTestCase(findTestCase('Login/partials/ChangeLanguageToEnglish'), [:])
}

