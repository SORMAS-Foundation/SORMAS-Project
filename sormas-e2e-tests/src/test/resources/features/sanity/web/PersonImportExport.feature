@UI @Sanity @Persons @PersonImportExport
Feature: Person import and export tests

@tmsLink=SORDEV-8044 @env_main
Scenario: Basic person export
  Given API: I create a new person
  Then API: I check that POST call body is "OK"
  And API: I check that POST call status code is 200
  Given API: I create a new case
  Then API: I check that POST call body is "OK"
  And API: I check that POST call status code is 200
  When I log in with National User
  When I click on the Persons button from navbar
  Then I fill Year of birth filter in Persons with the year of the last created person via API
  And I fill Month of birth filter in Persons with the month of the last created person via API
  And I fill Day of birth filter in Persons with the day of birth of the last created person via API
  Then I fill UUID of the last created person via API
  And I select present condition field with condition of the last created person via API
  And I choose random value of Region in Persons for the last created person by API
  And I choose random value of District in Persons for the last created person by API
  And I choose random value of Community in Persons for the last created person by API
  Then I apply on the APPLY FILTERS button
  And I click on the Export person button
  Then I click on the Basic Person Export button
  And I check if downloaded data generated by basic person export option is correct

@tmsLink=SORDEV-8044 @env_main
Scenario: Detailed person export
  Given API: I create a new person
  Then API: I check that POST call body is "OK"
  And API: I check that POST call status code is 200
  Given API: I create a new case
  Then API: I check that POST call body is "OK"
  And API: I check that POST call status code is 200
  When I log in as a Admin User
  When I click on the Persons button from navbar
  Then I fill Year of birth filter in Persons with the year of the last created person via API
  And I fill Month of birth filter in Persons with the month of the last created person via API
  And I fill Day of birth filter in Persons with the day of birth of the last created person via API
  Then I fill UUID of the last created person via API
  And I select present condition field with condition of the last created person via API
  And I choose random value of Region in Persons for the last created person by API
  And I choose random value of District in Persons for the last created person by API
  And I choose random value of Community in Persons for the last created person by API
  Then I apply on the APPLY FILTERS button
  And I click on the Export person button
  Then I click on the Detailed Person Export button
  And I check if downloaded data generated by detailed person export option is correct

  @tmsLink=SORDEV-8044 @env_main
  Scenario: Custom person export
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When I log in as a Admin User
    When I click on the Persons button from navbar
    Then I fill Year of birth filter in Persons with the year of the last created person via API
    And I fill Month of birth filter in Persons with the month of the last created person via API
    And I fill Day of birth filter in Persons with the day of birth of the last created person via API
    Then I fill UUID of the last created person via API
    And I select present condition field with condition of the last created person via API
    And I choose random value of Region in Persons for the last created person by API
    And I choose random value of District in Persons for the last created person by API
    And I choose random value of Community in Persons for the last created person by API
    Then I apply on the APPLY FILTERS button
    And I click on the Export person button
    Then I click on the Custom Person Export button
    And I click on the New Export Configuration button in Custom Person Export popup
    And I fill Configuration Name field with Test Configuration Name
    And I select specific data of person to export in Export Configuration
    When I download created custom person export file
    And I delete created custom person export file
    Then I check if downloaded data generated by custom person option is correct
