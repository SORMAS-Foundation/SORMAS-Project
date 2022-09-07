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
    Given I log in as a National User
    When I am accessing the Epidemiological tab using of created contact via api
    And I check and fill all data for a new EpiData Exposure
    Then I am checking all data is saved and displayed on edit Exposure page

  @tmsLink=SORDEV-5204 @env_main
  Scenario: Test continent and subcontinent in location entry in exposure
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I click on the Contacts button from navbar
    And I am accessing the Epidemiological tab using of created contact via api
    And I click on Exposure details known with YES option
    And I click on New Entry in Exposure Details Known
    And I check if the continent combobox is available in the location section in Exposure form
    And I check if the subcontinent combobox is available in the location section in Exposure form
    And I select "Malaysia" as a country in Exposure form
    And I check that continent is automatically selected as "Asia"
    And I check that subcontinent is automatically selected as "Southeast Asia"