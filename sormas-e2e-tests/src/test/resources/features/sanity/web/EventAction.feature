@UI @Sanity @Event @EventAction @UI
Feature: Create event actions


@issue=SORDEV-5564 @env_main
Scenario: Event - follow-up of actions: attach documents at the level of the action carried out
Given API: I create a new event
Then API: I check that POST call body is "OK"
And API: I check that POST call status code is 200
Given I log in with National User
Then I navigate to Event Action tab for created Event
And I click on New Action from Event Actions tab
And I create New Action from event tab
Then I navigate to Event Action tab for created Event
And I click on START DATA IMPORT button from NEW DOCUMENT in Event Action tab
And I upload ImportContactPrio.csv file to the Event Action
Then I check if ImportContactPrio.csv file is available in Event Action documents
Then I download last updated document file from Event Action tab
And I check if ImportContactPrio.csv file is downloaded correctly from Event Action tab
Then I delete last uploaded document file from Event Action tab
And I check if last uploaded file was deleted from document files in Event Action tab