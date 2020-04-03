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

WebUI.openBrowser('')

WebUI.navigateToUrl('https://sormas.symeda.de/sormas-ui/login')

WebUI.click(findTestObject('Page_SORMAS/div_Einloggen als Contact Supervisor'))

WebUI.click(findTestObject('Page_SORMAS/span_Contacts'))

WebUI.click(findTestObject('Page_SORMAS/div_New contact'))



WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Create new contactFirst name of contact_7e6168'))

WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_First name of contact person_gwt-uid-22'), 'Aurelius')

WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_Last name of contact person_gwt-uid-10'), 'something')

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Disease of source case_v-filterselect-button'))

WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_Last name of contact person_gwt-uid-10'), 'something')

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/span_COVID-19'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Choose Case'))

WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_Select Source Case_v-textfield v-widg_1dfb0a'), 
    'Hildegard von Bingen')

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Search case'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/td_Nachos Hospital'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Create new contactFirst name of contact_7e6168_1'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Confirm'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/div_Save'))



WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/span_Contacts list'))

WebUI.setText(findTestObject('Object Repository/Contacts/Page_SORMAS/input_New contact_v-textfield v-widget v-ha_73324d'), 
    'something')

WebUI.closeBrowser()

