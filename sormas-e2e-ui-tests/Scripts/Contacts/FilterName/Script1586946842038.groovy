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
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.hzi.Helper
import com.hzi.TestDataConnector

WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

'userName\n'
//WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), 
//    findTestData('defaultContactTestData').getValue(2, 1))
WebUI.setText(findTestObject('Contacts/ContactsOverview/input_New contact_nameUuidCaseLike'), TestDataConnector.getValueByKey(GlobalVariable.gContactTestDataName, "userName-B"))

WebUI.delay(1)

int rows = CustomKeywords.'com.hzi.Table.getTableRowsByAttribute'(Helper.createTestObjectWithXPath('//table[@aria-rowcount]'))

if (rows == 1) {
    WebUI.closeBrowser()
} else {
    WebUI.closeBrowser()
    throw new com.kms.katalon.core.exception.StepFailedException('Expected one row but found ' + rows)
}

