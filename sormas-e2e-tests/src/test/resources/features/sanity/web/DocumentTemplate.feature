@UI @Sanity @DocumentTemplates
Feature: Upload document template

  @issue=SORDEV-5497
  Scenario: Upload Case Document Template
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Case
    And I pick the case document template file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears

  @issue=SORDEV-5497
  Scenario: Upload Contact Document Template
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Contact
    And I pick the contact document template file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears

  @issue=SORDEV-5497
  Scenario: Upload Event Participant Document Template
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Event Participant
    And I pick the event participant document template file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears

  @issue=SORDEV-5497
  Scenario: Upload Travel Entry Document Template
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Travel Entry
    And I pick the travel entry document template file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears

  @issue=SORDEV-5497
  Scenario: Upload Event Document Template
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Event
    And I pick the event document template file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears

