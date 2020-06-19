import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Table
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Login/MainView/menu_Tasks'))
WebUI.delay(1)

int oldNumberOfRows = Table.getNumberOfTableRows()

WebUI.click(findTestObject('Object Repository/Tasks/button_div_New task'))

WebUI.click(findTestObject('Object Repository/Tasks/div_Task type_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Tasks/span_generate weekly report'))

WebUI.click(findTestObject('Object Repository/Tasks/div_Assigned to_v-filterselect-button'))
//WebUI.setText(findTestObject('Object Repository/Tasks/div_Assigned to_v-filterselect-button'), 'Surveillance SUPERVISOR')
WebUI.click(findTestObject('Object Repository/Tasks/span_Surveillance SUPERVISOR - berwachungsleitung (4)'))

WebUI.click(findTestObject('Object Repository/Tasks/div_Save'))
WebUI.delay(1)

int newNumberOfRows = Table.getNumberOfTableRows()

if (newNumberOfRows != oldNumberOfRows +1) {
	WebUI.closeBrowser()
	throw new StepFailedException('Expected to find one row more than before. before: ' + oldNumberOfRows + ' after: ' + newNumberOfRows)
}

WebUI.closeBrowser()

