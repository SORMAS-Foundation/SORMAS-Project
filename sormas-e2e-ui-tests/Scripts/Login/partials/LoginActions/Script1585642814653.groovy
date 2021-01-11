import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

// check if browser is already opened, if a testcase needs to change some profile properties it automatically would open a browser
/*try {
	WebUI.navigateToUrl(GlobalVariable.gUrl)
} catch(BrowserNotOpenedException){
	WebUI.openBrowser('')
	WebUI.navigateToUrl(GlobalVariable.gUrl)
}*/

WebUI.openBrowser('')
WebUI.navigateToUrl(GlobalVariable.gUrl)

WebUI.maximizeWindow()
WebUI.setText(findTestObject('Login/input_username_username'), Username)

WebUI.setText(findTestObject('Login/input_password_password'), Password)

WebUI.click(findTestObject('Login/div_Log in'))

