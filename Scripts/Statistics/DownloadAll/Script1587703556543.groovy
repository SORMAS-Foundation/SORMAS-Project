import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

import com.hzi.FileHandler
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable


// PREPARE
WebUIDriverType executedBrowser = DriverFactory.getExecutedBrowser()
switch(executedBrowser) {
	case WebUIDriverType.FIREFOX_DRIVER:          // "Firefox"
		System.setProperty('webdriver.gecko.driver', DriverFactory.getGeckoDriverPath())
		FirefoxOptions options = new FirefoxOptions()
				
		options.addPreference('marionette', true)
		options.addPreference('browser.download.folderList', 2)
		options.addPreference('browser.helperApps.alwaysAsk.force', false)
		options.addPreference('browser.download.manager.showWhenStarting', false)
		// options.addPreference('browser.download.useDownloadDir', true) // local test
		options.addPreference('browser.download.dir', GlobalVariable.gDownloadPath)
		options.addPreference('browser.download.downloadDir', GlobalVariable.gDownloadPath)
		options.addPreference('browser.download.defaultFolder', GlobalVariable.gDownloadPath)
		options.addPreference('browser.helperApps.neverAsk.saveToDisk', 'application/download, application/octet-stream, application/zip')
		
		WebDriver driver = new FirefoxDriver(options);
		// let Katalon Studio to use the WebDriver created here
		DriverFactory.changeWebDriver(driver)
		break
	default:
		WebUI.openBrowser('')
}


WebUI.callTestCase(findTestCase('Login/partials/LoginAsNationalUser'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Login/MainView/menu_Statistics'))

// TESTCASE
WebUI.click(findTestObject('Object Repository/Statistics/span_Database Export'))

WebUI.click(findTestObject('Object Repository/Statistics/div_Select all'))

WebUI.click(findTestObject('Object Repository/Statistics/div_Export'))

WebUI.delay(10)

// CHECK
String dateString = new Date().format("yyyy-MM-dd")
String filename = "sormas_export_" + dateString + ".zip"
println("file path should be: " + GlobalVariable.gDownloadPath + filename)
if (!FileHandler.existFile(GlobalVariable.gDownloadPath, filename)) {
	WebUI.closeBrowser()
	throw new StepFailedException("File '" + GlobalVariable.gDownloadPath + filename + "' was not downloaded or found")
}

// CLEANUP
FileHandler.removeFile(GlobalVariable.gDownloadPath, filename)

WebUI.closeBrowser()