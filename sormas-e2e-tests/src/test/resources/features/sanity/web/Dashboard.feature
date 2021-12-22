@UI @Sanity @Dashboard @#7472
Feature: Dashboard counters

  Scenario: Check disease and new cases counter in Surveillance Dashboard
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    When I select "COVID-19" in TabSheet of Surveillance Dashboard
    When I save value for COVID disease counter
    When I save value for New Cases counter
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    Then API: I check that POST call status code is 200
    When API: I create a new case
    Then API: I check that POST call body is "OK"
    Then API: I check that POST call status code is 200
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    When I select "COVID-19" in TabSheet of Surveillance Dashboard
    Then I check that previous saved counters have been increment

  Scenario: Check contacts counter in Contacts Dashboard
    Given I log in with National User
    When I click on the Dashboard button from navbar and access Contacts Dashboard
    When I save value for COVID contacts counter
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    Then API: I check that POST call status code is 200
    When API: I create a new contact
    Then API: I check that POST call body is "OK"
    Then API: I check that POST call status code is 200
    When I click on the Dashboard button from navbar and access Contacts Dashboard
    Then I check that previous saved contact counter has been incremented
