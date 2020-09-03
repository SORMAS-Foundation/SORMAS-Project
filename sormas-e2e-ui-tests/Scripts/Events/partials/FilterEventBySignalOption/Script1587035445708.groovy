import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.click(findTestObject('Events/SearchView/div_Signal_option'))

WebUI.delay(2)

// Open first entry
WebUI.click(findTestObject('Events/SearchView/a_FirstResultEntry_link'))