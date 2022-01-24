@UI @Sanity @Mock

  #Created for jenkins check, to be deleted after
Scenario: Check NON ASCII handle
Given I log in with National User
And I click on the Tasks button from navbar
And I click on the NEW TASK button
When I fill comments on task with NON ASCI
