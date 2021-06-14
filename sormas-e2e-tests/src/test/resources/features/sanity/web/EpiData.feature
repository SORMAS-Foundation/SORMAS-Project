Feature: Cover Epidemiological data Tab from Contacts

Scenario: Cover Epidemiological data Tab from Contacts
  Given I log in with the user
  When API: I create a new person
  Then API: I create a new contact
  When I am accessing the Epidemiological tab using of created contact via api
  And I check and fill all data
  Then I refresh Epidemiological data for contact page
  And I am checking all data is saved and displayed on edit Exposure page

