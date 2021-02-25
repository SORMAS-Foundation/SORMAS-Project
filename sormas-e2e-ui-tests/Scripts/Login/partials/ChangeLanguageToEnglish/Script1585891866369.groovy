import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.scrollToElement(findTestObject('Login/MainView/menu_Settings'), 2)

WebUI.click(findTestObject('Login/MainView/menu_Settings'))

WebUI.click(findTestObject('Login/SettingsDialog/div_language_v-filterselect-button'))

WebUI.click(findTestObject('Login/SettingsDialog/td_English'))

WebUI.click(findTestObject('ReusableORs/div_Save'))

WebUI.delay(2)