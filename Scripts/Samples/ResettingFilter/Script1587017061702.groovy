import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.hzi.Table as Table
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// PREPARE
WebUI.callTestCase(findTestCase('Login/partials/LoginAsSurveillanceSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Samples/partials/switchToSamples'), [:], FailureHandling.STOP_ON_FAILURE)

int numberOfRows = Table.getNumberOfTableRows()

// TESTCASE
WebUI.click(findTestObject('Samples/MainView/filterselect-button-testResult'))

WebUI.click(findTestObject('Object Repository/Samples/MainView/span_Pending'))

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))

checkNumberOfRows(numberOfRows, Table.getNumberOfTableRows(), 'Test Result')

WebUI.click(findTestObject('Samples/MainView/filterselect-button-condition'))

WebUI.click(findTestObject('Object Repository/Samples/MainView/td_Adequate'))

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))

checkNumberOfRows(numberOfRows, Table.getNumberOfTableRows(), 'Specimen Condition')


WebUI.click(findTestObject('Samples/MainView/filterselect-button-classification'))

WebUI.click(findTestObject('Object Repository/Samples/MainView/span_Not yet classified'))

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))

checkNumberOfRows(numberOfRows, Table.getNumberOfTableRows(), 'Case Classification')	

WebUI.click(findTestObject('Samples/MainView/filterselect-button-disease'))

WebUI.click(findTestObject('Object Repository/Samples/MainView/span_COVID-19'))

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))

checkNumberOfRows(numberOfRows, Table.getNumberOfTableRows(), 'Disease')

WebUI.click(findTestObject('Samples/MainView/filterselect-button-district'))

WebUI.click(findTestObject('Object Repository/Samples/MainView/span_Voreingestellter Landkreis'))

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))

checkNumberOfRows(numberOfRows, Table.getNumberOfTableRows(), 'District')

WebUI.click(findTestObject('Samples/MainView/filterselect-button-laboratory'))

WebUI.click(findTestObject('Object Repository/Samples/MainView/span_Voreingestelltes Labor'))

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))

checkNumberOfRows(numberOfRows, Table.getNumberOfTableRows(), 'Laboratory')

WebUI.setText(findTestObject('Samples/MainView/input_Export_caseCodeIdLike'), 'test')

WebUI.click(findTestObject('Contacts/ContactsOverview/div_Reset filters'))

checkNumberOfRows(numberOfRows, Table.getNumberOfTableRows(), 'Free Text filter')

WebUI.closeBrowser()

def checkNumberOfRows(int expectedRows, int countedRows, String filterName) {
    if (expectedRows != countedRows) {
		WebUI.closeBrowser()
        throw new StepFailedException((((('Expected the number of rows after reset filter to be the same. expected: ' + 
        expectedRows) + ' counted: ') + countedRows) + ' used Filter: ') + filterName)
    }
}

