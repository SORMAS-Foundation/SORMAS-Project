import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// PREPARE
WebUI.callTestCase(findTestCase('Contacts/partials/loginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/switchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Contacts/partials/searchAndSelectContact'), [('key') : 'userName-B'], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/div_Contact person'))

WebUI.waitForElementPresent(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Sex_v-filterselect-button'), 2)

// TESTCASE
//WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Sex_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Sex_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/span_Male'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Present condition of person_v-filtersel_844814'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_Alive'))

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Passport number_passportNumber'), 
    '1234')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_National health ID_nationalHealthId'), 
    'abc')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Type of occupation_v-filterselect-button'))

//WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_National health ID_nationalHealthId'), 
//    'abc')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_Farmer'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Education_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_Nursery'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/textarea_Address or landmark_address'))
WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/textarea_Address or landmark_address'), 
    'this is a test')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Postal code_postalCode'), 
    '456')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_City_city'), 
    'city')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Area type (urbanrural)_gwt-uid-122'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Area type (urbanrural)_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_Rural'))

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Nickname_nickname'), 
    'Pete')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Mother  s name)_mothersName'), 
    'mom')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Father  s name)_fathersName'), 
    'dad')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Phone number_phone'), 
    '12345')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Owner of phone_phoneOwner'), 
    'myself')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Email address_emailAddress'), 
    'somemail')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_General practitioner name and contact_e27f79'), 
    'practitioner')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Mother  s maiden name)_mothers_787cb7'), 
    'maiden')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Save'))

// CHECK
String sex = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Sex_gwt-uid-136'), 'value')
if (!sex.equalsIgnoreCase('male')) {
	throw new StepFailedException('The sex should be "male". sex: ' + sex )
}

String condition = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Present condition of person'), 'value')
if (!condition.equalsIgnoreCase('Alive')) {
	throw new StepFailedException('The condition should be "Alive". condition: ' + condition )
}

String ppnr = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Passport number_passportNumber'), 'value')
if (!ppnr.equalsIgnoreCase('1234')) {
	throw new StepFailedException('The passport-number should be "1234". ppnr: ' + ppnr )
}

String nhid = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_National health ID_nationalHealthId'), 'value')
if (!nhid.equalsIgnoreCase('abc')) {
	throw new StepFailedException('The national-health-id should be "abc". nhid: ' + nhid )
}

String occupation = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Type of occupation'), 'value')
if (!occupation.equalsIgnoreCase('Farmer')) {
	throw new StepFailedException('The occupation should be "Farmer". occupation: ' + occupation )
}

String education = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Education'), 'value')
if (!education.equalsIgnoreCase('Nursery')) {
	throw new StepFailedException('The occupation should be "Nursery". education: ' + education )
}

String landmark = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/textarea_Address or landmark_address'), 'value')
if (!landmark.contains('this is a test')) {
	throw new StepFailedException('The address or landmark should be "This is a test". landmark: ' + landmark )
}

String postalCode = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Postal code_postalCode'), 'value')
if (!postalCode.contains('456')) {
	throw new StepFailedException('The postal code should be "456". postalCode: ' + postalCode )
}

String city = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_City_city'), 'value')
if (!city.contains('city')) {
	throw new StepFailedException('The postal code should be "city". city: ' + city )
}

String areaType = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Area type (urbanrural)_gwt-uid-122'), 'value')
if (!areaType.contains('Rural')) {
	throw new StepFailedException('The area type should be "Rural". areaType: ' + areaType )
}

String contactNickname = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Nickname_nickname'), 'value')
if (!contactNickname.contains('Pete')) {
	throw new StepFailedException('The contact nickname should be "Pete". contactNickname: ' + contactNickname )
}

String mothersName = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Mother  s name)_mothersName'), 'value')
if (!mothersName.contains('mom')) {
	throw new StepFailedException('The contact mothers name should be "mom". mothersName: ' + mothersName )
}

String fathersName = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Father  s name)_fathersName'), 'value')
if (!fathersName.contains('dad')) {
	throw new StepFailedException('The contact fathers name should be "dad". fathersName: ' + fathersName )
}

String maidenName = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Mother  s maiden name)_mothers_787cb7'), 'value')
if (!maidenName.contains('maiden')) {
	throw new StepFailedException('The contact mothers maiden name should be "maiden". maidenName: ' + maidenName )
}

String phoneNumber = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Phone number_phone'), 'value')
if (!phoneNumber.contains('12345')) {
	throw new StepFailedException('The contact phone number should be "12345". phoneNumber: ' + phoneNumber )
}

String phoneOwner = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Owner of phone_phoneOwner'), 'value')
if (!phoneOwner.contains('myself')) {
	throw new StepFailedException('The phone owner should be "myself". phoneOwner: ' + phoneOwner )
}

String email = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_Email address_emailAddress'), 'value')
if (!email.contains('somemail')) {
	throw new StepFailedException('The email should be "somemail". email: ' + email )
}

String practitioner = WebUI.getAttribute(findTestObject('Contacts/ContactInformationView/ChangeContactPersonData/input_General practitioner name and contact_e27f79'), 'value')
if (!practitioner.contains('practitioner')) {
	throw new StepFailedException('The practitioner should be "practitioner". practitioner: ' + practitioner )
}

// CLEANUP

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Sex_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Present condition of person_v-filtersel_844814'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_'))

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Passport number_passportNumber'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_National health ID_nationalHealthId'), 
    '')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Type of occupation_v-filterselect-button'))

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_National health ID_nationalHealthId'), 
    '')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Education_v-filterselect-button'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_'))

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/textarea_Address or landmark_address'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Postal code_postalCode'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_City_city'), 
    '')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Area type (urbanrural)_v-filterselect-button'))

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_City_city'), 
    '')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/td_'))

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Nickname_nickname'))
WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Nickname_nickname'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Mother  s maiden name)_mothers_787cb7'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Father  s name)_fathersName'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_concat(Mother  s name)_mothersName'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Phone number_phone'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Owner of phone_phoneOwner'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_Email address_emailAddress'), 
    '')

WebUI.setText(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/input_General practitioner name and contact_e27f79'), 
    '')

WebUI.click(findTestObject('Object Repository/Contacts/ContactInformationView/ChangeContactPersonData/div_Save'))

WebUI.closeBrowser()

