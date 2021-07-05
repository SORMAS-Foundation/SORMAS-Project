@UI @Sanity @Login
Feature: Login with different type of users

  @issue=5402
  Scenario Outline: Login with <userType> user
    Given I navigate to SORMAS login page
     When I fill the username with <username>
      And I fill the password with <password>
      And I click on the Log In button
     Then I am logged in with name <userType>

    Examples:
      | userType                  | username | password     |
      | National User             | NatUser  | NatUser38118 |
      | Contact Supervisor        | ContSup  | ContSup38118 |
      | Laboratory Officer        | LabOff   | LabOff38118  |
      | Point of Entry Supervisor | PoeSup   | PoeSup38118  |
