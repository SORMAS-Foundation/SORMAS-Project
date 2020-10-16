import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.TestDataConnector as TestDataConnector
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

String caseName = TestDataConnector.getValueByKey(GlobalVariable.gSamplesTestDataName, 'case-with-samples')

println('searching for case: ' + caseName)

WebUI.setText(findTestObject('Object Repository/Samples/MainView/input_Export_caseCodeIdLike'), caseName)

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Apply filters'))

WebUI.delay(1)

WebUI.click(findTestObject('Samples/MainView/edit_sample_from_table'))

