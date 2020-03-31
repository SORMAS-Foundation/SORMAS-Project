import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.Keys as Keys

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/LoginAsSuveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Page_SORMAS/span_Cases'))

WebUI.click(findTestObject('Object Repository/Page_SORMAS/div_New case_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Page_SORMAS/span_Not yet classified'))

not_run: WebUI.click(findTestObject('Object Repository/Page_SORMAS/div_Case IDEPID numberDiseaseCase classific_28592f'))

//result = CustomKeywords.'com.hzi.Test.getHtmlTableRows'(findTestObject('Page_SORMAS/div_Case IDEPID numberDiseaseCase classific_28592f'), 
//    'tbody')


TestObject to = new TestObject().addProperty('xpath', ConditionType.EQUALS, './/div[@class="v-grid-tablewrapper"]')

result = CustomKeywords.'com.hzi.Test.getHtmlTableRows'(to,
	'tbody')
println(result.size())

if (result.size() == 11) {
    WebUI.closeBrowser()
} else {
    throw new com.kms.katalon.core.exception.StepFailedException('Result number does not equal expected number')
}


