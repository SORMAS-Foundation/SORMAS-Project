@UI @Sanity @Vaccination @Filters
Feature: Vaccination tests

  @issue=SORDEV-9585 @env_de
  Scenario: Test Add reduced vaccination module to document creation for Contact
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Contact
    And I pick the "VaccinationGenerationTest_Contacts.docx" file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears
    And I click to close UPLOAD TEMPLATE popup
    When I click on the Contacts button from navbar
    Then I search after last created contact via API by UUID and open
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    Then I click on Create button in Document Templates box for DE
    And I select "VaccinationGenerationTest_Contacts.docx" template in Document Template form
    And I click on Create button in Document Templates popup for DE
    Then I check if generated document for Contact based on "VaccinationGenerationTest_Contacts.docx" was downloaded properly
    And I check if generated document for contact based on "VaccinationGenerationTest_Contacts.docx" contains all required fields
    Then I delete downloaded file created from "VaccinationGenerationTest_Contacts.docx" Document Template for Contact

  @issue=SORDEV-9585 @env_de
  Scenario: Test Add reduced vaccination module to document creation for Case
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Case
    And I pick the "VaccinationGenerationTest_Cases.docx" file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears
    And I click to close UPLOAD TEMPLATE popup
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    Then I click on Create button in Document Templates box for DE
    And I select "VaccinationGenerationTest_Cases.docx" template in Document Template form
    And I click on Create button in Document Templates popup for DE
    Then I check if generated document for Case based on "VaccinationGenerationTest_Cases.docx" was downloaded properly
    And I check if generated document for Case based on "VaccinationGenerationTest_Cases.docx" contains all required fields
    Then I delete downloaded file created from "VaccinationGenerationTest_Cases.docx" Document Template

  @issue=SORDEV-9585 @env_de
  Scenario: Test Add reduced vaccination module to document creation for Event
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Event Participant
    And I pick the "VaccinationGenerationTest_EventParticipants.docx" file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears
    And I click to close UPLOAD TEMPLATE popup
    And I click on the Events button from navbar
    And I open the last created event via api
    And I navigate to EVENT PARTICIPANT from edit event page
    Then I add only required data for event participant creation for DE
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    Then I click on Create button in Document Templates box for DE
    And I select "VaccinationGenerationTest_EventParticipants.docx" template in Document Template form
    And I click on Create button in Document Templates popup for DE
    Then I check if generated document for Event Participant based on "VaccinationGenerationTest_EventParticipants.docx" was downloaded properly
    And I check if generated document for Event Participant based on "VaccinationGenerationTest_EventParticipants.docx" contains all required fields
    Then I delete downloaded file created from "VaccinationGenerationTest_EventParticipants.docx" Document Template for Event Participant