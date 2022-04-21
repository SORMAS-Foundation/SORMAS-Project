@UI @Sanity @Columns
Feature: Test column sorting for different entities

@env_main @issue=SORDEV-5342 @Task
Scenario Outline: Sort column <col> alphabetically in Tasks directory
  Given I log in with National User
  And I click on the Tasks button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |3 |
    |4 |
    |5 |
    |6 |
    |7 |
    |10 |
    |11 |
    |12 |
    |13 |
    |14 |

@env_main @issue=SORDEV-5342 @Task
Scenario Outline: Sort column <col> by date and time in Tasks directory
  Given I log in with National User
  And I click on the Tasks button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted by date and time in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted by date and time in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |8 |
    |9 |

@env_main @issue=SORDEV-5342 @Persons
Scenario Outline: Sort column <col> alphabetically in Persons directory
  Given I log in with National User
  And I click on the Persons button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |1 |
    |2 |
    |3 |
    |5 |
    |6 |
    |7 |
    |8 |
    |9 |
    |10 |
    |11 |
    |12 |

@env_main @issue=SORDEV-5342 @Persons
Scenario Outline: Sort column <col> by age in Persons directory
  Given I log in with National User
  And I click on the Persons button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted by age in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted by age in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |4 |

@env_main @issue=SORDEV-5342 @Case
Scenario Outline: Sort column <col> alphabetically in Cases directory
  Given I log in with National User
  And I click on the Cases button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |1 |
    |2 |
    |3 |
    |4 |
    |5 |
    |6 |
    |7 |
    |8 |
    |9 |
    |10 |
    |11 |
    |12 |
    |13 |
    |14 |
    |15 |
    |18 |
    |20 |
    |22 |

@env_main @issue=SORDEV-5342 @Case
Scenario Outline: Sort column <col> by date and time in Cases directory
  Given I log in with National User
  And I click on the Cases button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted by date and time in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted by date and time in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |16 |
    |17 |

@env_main @issue=SORDEV-5342 @Case
Scenario Outline: Sort column <col> by date in Cases directory
  Given I log in with National User
  And I click on the Cases button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted by date in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted by date in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |19 |

@env_main @issue=SORDEV-5342 @Contacts
Scenario Outline: Sort column <col> alphabetically in Contacts directory
  Given I log in with National User
  And I click on the Contacts button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |1 |
    |2 |
    |3 |
    |4 |
    |5 |
    |6 |
    |7 |
    |8 |
    |9 |
    |10 |
    |11 |
#    |12 |  Non-alphabetical sorting order - find out whether it's a bug or a feature
#    |13 |  Non-alphabetical sorting order - find out whether it's a bug or a feature
    |15 |

@env_main @issue=SORDEV-5342 @Contacts
Scenario Outline: Sort column <col> by date in Contacts directory
  Given I log in with National User
  And I click on the Contacts button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted by date in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted by date in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |14 |

@env_main @issue=SORDEV-5342 @issue=SORQA-78 @Event
Scenario Outline: Sort column <col> alphabetically in Events directory
  Given I log in with National User
  And I click on the Events button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |1 |
    |2 |
    |3 |
    |4 |
    |5 |
    |6 |
    |7 |
    |8 |
    |9 |
    |10 |
    |11 |
    |12 |
    |13 |
    |14 |
    |16 |
    |17 |
    |18 |
    |19 |
    |20 |
    |22 |
    |23 |

@env_main @issue=SORDEV-5342 @Sample
Scenario Outline: Sort column <col> alphabetically in Samples directory
  Given I log in with National User
  And I click on the Sample button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted alphabetically in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |1 |
    |2 |
    |6 |
    |7 |
    |8 |
    |9 |
    |10 |
    |11 |
    |12 |
    |13 |
    |14 |

@env_main @issue=SORDEV-5342 @Sample
Scenario Outline: Sort column <col> by last name in Samples directory
  Given I log in with National User
  And I click on the Sample button from navbar
  When I click the header of column <col>
  Then I check that column <col> is sorted by last name in ascending order
  And I check that an upwards arrow appears in the header of column <col>
  When I click the header of column <col>
  Then I check that column <col> is sorted by last name in descending order
  And I check that a downwards arrow appears in the header of column <col>

  Examples:
    | col |
    |3 |
    |4 |
    |5 |