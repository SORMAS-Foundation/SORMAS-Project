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
    Then I validate contacts button is clickable on Surveillance Dashboard Page
    Then I validate filter components presence on Surveillance Dashboard Page
    Then I validate presence of diseases metrics on Surveillance Dashboard Page
    Then I validate presence of diseases slider on Surveillance Dashboard Page
    Then I validate presence of Epidemiological Curve on Surveillance Dashboard Page
    Then I validate presence of maps on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Validate show all diseases functionality
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate show all diseases button is available and clickable on Surveillance Dashboard Page
    When I validate only 6 disease categories are displayed on Surveillance Dashboard Page
    Then I click on show all diseases on Surveillance Dashboard Page
    Then I validate presence of all diseases on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Check disease information layout
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate name of diseases is shown on Surveillance Dashboard Page
    Then I validate total data of diseases is shown on Surveillance Dashboard Page
    Then I validate compared data of diseases is shown on Surveillance Dashboard Page
    Then I validate last report of diseases is shown on Surveillance Dashboard Page
    Then I validate fatalities of diseases is shown on Surveillance Dashboard Page
    Then I validate number of events of diseases is shown on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Check disease burden information table
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I switch to burden information table on Surveillance Dashboard Page
    Then I validate that all the headers are present in the burden information table on Surveillance Dashboard Page
    Then I validate diseases presence in the data table on Surveillance Dashboard Page
    Then I validate switching back to disease boxes is working on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Check New Cases and Events layout on surveillance dashboard
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate all diseases are displayed in the carousel slider options on Surveillance Dashboard Page
    Then I validate counter is present on Surveillance Dashboard Page
    Then I validate presence of left statistics charts on Surveillance Dashboard Page
    Then I validate presence of cases metrics on Surveillance Dashboard Page
    Then I validate presence of fatalities counter on Surveillance Dashboard Page
    Then I validate presence of events counter on Surveillance Dashboard Page
    Then I validate presence of events metrics on Surveillance Dashboard Page
    Then I validate presence of test results counter on Surveillance Dashboard Page
    Then I validate presence of test results metrics on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Check Epidemiological curve chart Alive or Dead option
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I click on legend case status on Surveillance Dashboard Page
    Then I check case status chart on Surveillance Dashboard Page
    Then I click on legend alive or dead on Surveillance Dashboard Page
    Then I check alive or dead chart on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Check Epidemiological curve layout
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate presence of legend data on Surveillance Dashboard Page
    Then I validate presence of chart on Surveillance Dashboard Page
    Then I validate presence of chart download button on Surveillance Dashboard Page
    Then I validate chart download options on Surveillance Dashboard Page
    Then I validate presence of chart buttons on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Check Case status map
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate presence of maps on Surveillance Dashboard Page
    Then I validate presence of map options on Surveillance Dashboard Page
    Then I validate presence of Map key options on Surveillance Dashboard Page
    Then I validate presence of Layers options on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Check components expand-collapse functionality
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I expand Epidemiological curve on Surveillance Dashboard Page
    Then I verify that only epi curve chart is displayed on Surveillance Dashboard Page
    Then I expand Case status map on Surveillance Dashboard Page
    Then I verify only Case status map is displayed on Surveillance Dashboard Page
    Then I select Difference in Number of Cases hide overview on Surveillance Dashboard Page
    Then I verify that Overview data is hidden on Surveillance Dashboard Page

  @env_main @#7440
  Scenario: Overview data apply filters check
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I apply filter compare: today -> yesterday on Surveillance Dashboard Page
    Then I verify filter works on Surveillance Dashboard Page
    Then I apply date filter on Surveillance Dashboard Page
    Then I verify filter works on Surveillance Dashboard Page
    Then I apply region filter on Surveillance Dashboard Page
    Then I verify filter works on Surveillance Dashboard Page
    Then I click on reset filters on Surveillance Dashboard Page
    Then I verify that filters were reset on Surveillance Dashboard Page

    @tmsLink=SORDEV-6604 @env_main
    Scenario: Differentiate strings for confirmed cases and confirmed contacts on dashboard
      Given I log in as a Admin User
      And I click on the Users from navbar
      And I click on the NEW USER button
      And I create new National User for test
      Then I click on logout button from navbar
      And As a new created user I log in
      Then I click on the User Settings button from navbar
      And I select "English" language from Combobox in User settings
      When I click on the Dashboard button from navbar and access Surveillance Dashboard
      Then I get Confirmed labels and value from Surveillance Dashboard with English language
      When I click on the Dashboard button from navbar and access Contacts Dashboard
      Then I get Confirmed Contact labels and value from Contact Dashboard with English language
      Then I click on the User Settings button from navbar
      And I select "Deutsch" language from Combobox in User settings
      When I click on the Dashboard button from navbar and access Surveillance Dashboard
      Then I get Confirmed labels and value from Surveillance Dashboard with Deutsch language
      When I click on the Dashboard button from navbar and access Contacts Dashboard with Deutsch language
      Then I get Confirmed Contact labels and value from Contact Dashboard with Deutsch language
      And I compare English and German confirmed counter
      And I compare English and German confirmed contacts counter

  @env_de @tmsLink=SORDEV-6137
  Scenario: Test if "not a case" is excluded from the total case count
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    And I select "COVID-19" in TabSheet of Surveillance Dashboard
    Then I check that the Total number of COVID-19 cases excludes those marked "not a case" in German

  @issue=SORDEV-6142 @env_main
  Scenario: Check that number of cases on the dashboard map is rendered by the time filter
      Given I log in with National User
      When I click on the Dashboard button from navbar
      And I expand Case status map on Surveillance Dashboard Page
      And I click on the Time Period combobox from Surveillance Dashboard
      And I choose yesterday from the Surveillance Dashboard Time Period combobox
      And I click on the APPLY FILTERS button
      And I choose "Monkeypox" in a disease filter on Surveillance Dashboard
      And I click the zoom out button 4 times on the Case Status Map
      And I count the number of cases displayed on the Case Status Map
      And I click on the Time Period combobox from Surveillance Dashboard
      And I choose today from the Surveillance Dashboard Time Period combobox
      And I click on the APPLY FILTERS button
      And I count the number of cases displayed on the Case Status Map
      And I click on the Cases button from navbar
      And I click on the NEW CASE button
      And I fill new case form with specific data
      And I click on save case button
      And I collect uuid of the case
      And I change disease to "Monkeypox" in the case tab
      And I confirm changes in selected Case
      And I navigate to case person tab
      And I fill specific address data in Case Person tab
      And I click on Geocode button to get GPS coordinates in Case Person Tab
      And I click on save button to Save Person data in Case Person Tab
      And I click on the Dashboard button from navbar
      And I expand Case status map on Surveillance Dashboard Page
      And I click on the Time Period combobox from Surveillance Dashboard
      And I choose yesterday from the Surveillance Dashboard Time Period combobox
      And I click on the APPLY FILTERS button
      And I choose "Monkeypox" in a disease filter on Surveillance Dashboard
      And I click the zoom out button 4 times on the Case Status Map
      And I count the number of cases displayed on the Case Status Map
      Then I check that number of cases on the Case Status Map for yesterday has increased by "1"
      And I click on the Time Period combobox from Surveillance Dashboard
      And I choose today from the Surveillance Dashboard Time Period combobox
      And I click on the APPLY FILTERS button
      And I count the number of cases displayed on the Case Status Map
      Then I check that number of cases on the Case Status Map for today has not changed
      And I click on the Cases button from navbar
      And I filter with last created case using case UUID
      And I click on the first Case ID from Case Directory
      And I delete the case

  @issue=SORDEV-6142 @env_main
  Scenario: Check that number of contacts on the dashboard map is rendered by the time filter
    Given I log in with National User
    When I click on the Dashboard button from navbar
    And I expand Case status map on Surveillance Dashboard Page
    And I click Layers button on Surveillance Dashboard Page
    And I click checkbox to select Show contacts from Layers on the Case Status Map
    And I click checkbox to unselect Show cases from Layers on the Case Status Map
    And I click on the Time Period combobox from Surveillance Dashboard
    And I choose yesterday from the Surveillance Dashboard Time Period combobox
    And I click on the APPLY FILTERS button
    And I choose "Cholera" in a disease filter on Surveillance Dashboard
    And I click the zoom out button 4 times on the Case Status Map
    And I count the number of contacts displayed on the Case Status Map
    And I click on the Time Period combobox from Surveillance Dashboard
    And I choose today from the Surveillance Dashboard Time Period combobox
    And I click on the APPLY FILTERS button
    And I count the number of contacts displayed on the Case Status Map
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a mandatory fields for a new contact and date of report to yesterday
    And I click on SAVE new contact button and choose create new person in duplication detection
    And I collect contact UUID displayed on Edit Contact Page
    And I change disease to "Cholera" in the Edit contact page
    And I click SAVE button on Edit Contact Page
    And I open Contact Person tab
    And I fill specific address data in Case Person tab
    And I click on Geocode button to get GPS coordinates in Case Person Tab
    And I click on save button to Save Person data in Case Person Tab
    And I click on the Dashboard button from navbar
    And I expand Case status map on Surveillance Dashboard Page
    And I click Layers button on Surveillance Dashboard Page
    And I click checkbox to select Show contacts from Layers on the Case Status Map
    And I click checkbox to unselect Show cases from Layers on the Case Status Map
    And I click on the Time Period combobox from Surveillance Dashboard
    And I choose yesterday from the Surveillance Dashboard Time Period combobox
    And I click on the APPLY FILTERS button
    And I choose "Cholera" in a disease filter on Surveillance Dashboard
    And I click the zoom out button 4 times on the Case Status Map
    And I count the number of contacts displayed on the Case Status Map
    Then I check that number of contacts on the Case Status Map for yesterday has increased by "1"
    And I click on the Time Period combobox from Surveillance Dashboard
    And I choose today from the Surveillance Dashboard Time Period combobox
    And I click on the APPLY FILTERS button
    And I count the number of contacts displayed on the Case Status Map
    Then I check that number of contacts on the Case Status Map for today has not changed
    And I click on the Contacts button from navbar
    And I click on first created contact in Contact directory page by UUID
    And I delete the contact

