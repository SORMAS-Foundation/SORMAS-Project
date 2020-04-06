import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

WebUI.callTestCase(findTestCase('SurveillanceSupervisor/FilterCasesNotYetClasifiedInSearchView'), [('isStandalone') : false], FailureHandling.STOP_ON_FAILURE)
 
WebUI.click(findTestObject('Surveillance/SearchView/Filter/div_ResetFilters_btn'))
 
def attribute = WebUI.getAttribute(findTestObject('Surveillance/SearchView/table_SearchResult_table'), 'aria-rowcount')
 
attribute = (attribute.toInteger() - 1).toString()
 
WebUI.verifyNotMatch(attribute, '12', false, FailureHandling.OPTIONAL)