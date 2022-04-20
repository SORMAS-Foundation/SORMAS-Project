@UI @Sanity @Dashboard @#7472
Feature: Dashboard counters

  @env_main
  Scenario: Check disease and new cases counter in Surveillance Dashboard
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    When I select "COVID-19" in TabSheet of Surveillance Dashboard
    When I save value for COVID disease counter in Surveillance Dashboard
    When I save value for New Cases counter in Surveillance Dashboard
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    Then API: I check that POST call status code is 200
    When API: I create a new case
    Then API: I check that POST call body is "OK"
    Then API: I check that POST call status code is 200
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    When I select "COVID-19" in TabSheet of Surveillance Dashboard
    Then I check that previous saved Surveillance Dashboard counters for COVID-19 have been increment

  @env_main
  Scenario: Check contacts counter in Contacts Dashboard
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Contacts Dashboard
    When I save value for COVID-19 contacts counter in Contacts Dashboard
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    Then API: I check that POST call status code is 200
    When API: I create a new contact
    Then API: I check that POST call body is "OK"
    Then API: I check that POST call status code is 200
    When I click on the Dashboard button from navbar and access Contacts Dashboard
    Then I check that previous saved Contacts Dashboard contact counter for COVID-19 has been incremented

  @env_main @#7440
  Scenario: Validate Surveillance Dashboard layout
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate contacts button is clickable
    Then I validate filter components presence
    Then I validate presence of diseases metrics
    Then I validate presence of diseases slider
    Then I validate presence of Epidemiological Curve
    Then I validate presence of maps

  @env_main @#7440
  Scenario: Validate show all diseases functionality
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate show all diseases button is available and clickable
    When I validate only 6 disease categories are displayed
    Then I click on show all diseases
    Then I validate presence of all diseases

  @env_main @#7440
  Scenario: Check disease information layout
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate name of diseases is shown
    Then I validate total data of diseases is shown
    Then I validate compared data of diseases is shown
    Then I validate last report of diseases is shown
    Then I validate fatalities of diseases is shown
    Then I validate number of events of diseases is shown

  @env_main @#7440
  Scenario: Check disease burden information table
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I switch to burden information table
    Then I validate that all the headers are present in the burden information table
    Then I validate diseases presence in the data table
    Then I validate switching back to disease boxes is working

  @env_main @#7440
  Scenario: Check New Cases and Events layout on surveillance dashboard
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate all diseases are displayed in the carousel slider options
    Then I validate counter is present
    Then I validate presence of left statistics charts
    Then I validate presence of cases metrics
    Then I validate presence of fatalities counter
    Then I validate presence of events counter
    Then I validate presence of events metrics
    Then I validate presence of test results counter
    Then I validate presence of test results metrics

  @env_main @#7440
  Scenario: Check Epidemiological curve chart Alive or Dead option
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I click on legend case status
    Then I check case status chart
    Then I click on legend alive or dead
    Then I check alive or dead chart

  @env_main @#7440
  Scenario: Check Epidemiological curve layout
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate presence of legend data
    Then I validate presence of chart
    Then I validate presence of chart download button
    Then I validate chart download options
    Then I validate presence of chart buttons

  @env_main @#7440 @runonlythis
  Scenario: Check Case status map
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate presence of maps
    Then I validate presence of map options
    Then I validate presence of Map key options
    Then I validate presence of Layers options

  @env_main @#7440
  Scenario: Check components expand-collapse functionality
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I expand Epidemiological curve
    Then I verify that only epi curve chart is displayed on page
    Then I expand Case status map
    Then I verify only Case status map is displayed on page
    Then I select Difference in Number of Cases hide overview
    Then I verify that Overview data should be hidden

  @env_main @#7440
  Scenario: Overview data apply filters check
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I apply filter compare: today -> yesterday
    Then I verify filter works
    Then I apply date filter
    Then I verify filter works
    Then I apply region filter
    Then I verify filter works
    Then I click on reset filters
    Then I verify filters were reset