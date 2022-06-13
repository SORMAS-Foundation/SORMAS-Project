@UI @Contacts @Epidata
Feature: Cover Epidemiological data Tab from Contacts

  @env_main
  Scenario: Cover Epidemiological data Tab from Contacts
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I am accessing the Epidemiological tab using of created contact via api
    And I check and fill all data for a new EpiData Exposure
    Then I am checking all data is saved and displayed on edit Exposure page

