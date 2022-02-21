@UI @Sanity @Dashboard @#7472
Feature: Dashboard counters

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

  Scenario: Validate Surveillance Dashboard layout
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate contacts button is clickable
    Then I validate filter components presence
    Then I validate presence of diseases metrics
    Then I validate presence of diseases slider
    Then I validate presence of Epidemiological Curve
    Then I validate presence of maps

  Scenario: Validate show all diseases functionality
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate show all diseases button is available and clickable
    Then I validate only 6 disease categories are displayed
    Then I click on show all diseases
    Then I validate presence of all diseases

  Scenario: Check disease information layout
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I validate name of diseases is shown
    Then I validate total data of diseases is shown
    Then I validate compared data of diseases is shown
    Then I validate last report of diseases is shown
    Then I validate fatalities of diseases is shown
    Then I validate number of events of diseases is shown

  Scenario: Check disease burden information table
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I switch to burden information table
    Then I validate that all the headers are present in the burden information table
    Then I validate diseases presence in the data table
    Then I validate switching back to disease boxes is working
