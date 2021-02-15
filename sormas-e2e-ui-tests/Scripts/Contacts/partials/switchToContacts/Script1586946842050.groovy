import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.delay(2)
WebUI.click(findTestObject('Login/MainView/menu_Contacts'))
