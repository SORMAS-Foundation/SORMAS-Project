import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Helper
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.setText(findTestObject('Contacts/Page_SORMAS/contact_search_field_name'),
	findTestData('ContactTestData').getValue(2, 1))

WebUI.click(Helper.createTestObjectWithXPath('//table[@aria-rowcount]//a'))