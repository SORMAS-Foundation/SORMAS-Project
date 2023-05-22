@UI @Sanity @Login @precon
Feature: Login with different type of users

  @env_main @LoginMain
  Scenario Outline: Login with <user> user on Main Environment
    Given I navigate to SORMAS login page
    And I check that Login page is correctly displayed in English language
    Then I log in as a <user>
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in English language
    Then I click on the User Settings button from navbar
    Then I check that English language is selected in User Settings
    And I click on logout button

    Examples:
      | user                          |
      | National User                 |
      | Contact Supervisor            |
      | Surveillance Officer          |
      | Surveillance Supervisor       |
      | Laboratory Officer            |
      | Point of Entry Supervisor     |
      | Admin User                    |
      | Rest AUTOMATION               |
      | Admin Surveillance Supervisor |
      | Contact Officer               |
      | Community Officer             |
      | Hospital Informant            |
      | Clinician                     |

  @env_de @LoginDe
  Scenario Outline: Login with <user> user on German Environment
    Given I navigate to SORMAS login page
    And I check that Login page is correctly displayed in German language
    Then I log in as a <user>
    Then I am logged in
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    And I check that Surveillance Dashboard header is correctly displayed in German language
    Then I click on the User Settings button from navbar
    And I check that Deutsch language is selected in User Settings
    And I click on logout button

    Examples:
      | user                          |
      | National User                 |
      | Contact Supervisor            |
      | Surveillance Officer          |
      | Surveillance Supervisor       |
      | Laboratory Officer            |
      | Point of Entry Supervisor     |
      | Admin User                    |
      | Rest AUTOMATION               |
      | Admin Surveillance Supervisor |
      | Contact Officer               |
      | Community Officer             |
      | Hospital Informant            |
      | Clinician                     |

  @env_keycloak @LoginKeycloak
  Scenario Outline: Login with <user> user on Keycloak Environment
    Given I navigate to SORMAS login page
    Then I log in as a <user>
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in German language
    And I click on logout button

    Examples:
      | user                      |
      | Admin User                |
 #     | National User             |

  @env_keycloak @LoginKeycloak
  Scenario: Login on Keycloak Administrator Console
    Given I navigate to Keycloak Administrator Console Login page
    Then I log in as Keycloak Admin to Keycloak Administrator Console
    Then I am logged in Keycloak Administrator Console page
    And I click on logout button on Keycloak Administrator Console Page

  @tmsLink=SORQA-772 @env_main
  Scenario: Automatize Login with National Language User on international environment
    Given I navigate to SORMAS login page
    And I check that Login page is correctly displayed in English language
    Then I log in as a National Language User
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in German language
    Then I click on the User Settings button from navbar
    Then I check that Deutsch language is selected in User Settings
    And I click on logout button

  @tmsLink=SORQA-772 @env_de
  Scenario: Automatize Login with National Language User on german environment
    Given I navigate to SORMAS login page
    And I check that Login page is correctly displayed in German language
    Then I log in as a National Language User
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in English language
    Then I click on the User Settings button from navbar
    Then I check that English language is selected in User Settings
    And I click on logout button

  @env_sltest @StandardLogin
  Scenario Outline: Test <url> for default <user> credentials
    Given I navigate to address <url>
    When I try to log into current website as a <user>
    Then I check if I got logged into "<url>" as "<user>" - if so, I send an alert

    Examples:
      | url                                             | user                        |
      | https://aesculab.sormas.netzlink.com/  | Administrator               |
      | https://aesculab.sormas.netzlink.com/  | Case Supervisor             |
      | https://aesculab.sormas.netzlink.com/  | Contact Supervisor          |
      | https://aesculab.sormas.netzlink.com/  | Event Officer               |
      | https://aesculab.sormas.netzlink.com/  | Laboratory Officer          |
      | https://aesculab.sormas.netzlink.com/  | National Clinician          |
      | https://aesculab.sormas.netzlink.com/  | National User               |
      | https://aesculab.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://aesculab.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://akademie.sormas-deutschland.de/  | Administrator               |
      | https://akademie.sormas-deutschland.de/  | Case Supervisor             |
      | https://akademie.sormas-deutschland.de/  | Contact Supervisor          |
      | https://akademie.sormas-deutschland.de/  | Event Officer               |
      | https://akademie.sormas-deutschland.de/  | Laboratory Officer          |
      | https://akademie.sormas-deutschland.de/  | National Clinician          |
      | https://akademie.sormas-deutschland.de/  | National User               |
      | https://akademie.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://akademie.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://backup-test.sormas-deutschland.de/  | Administrator               |
      | https://backup-test.sormas-deutschland.de/  | Case Supervisor             |
      | https://backup-test.sormas-deutschland.de/  | Contact Supervisor          |
      | https://backup-test.sormas-deutschland.de/  | Event Officer               |
      | https://backup-test.sormas-deutschland.de/  | Laboratory Officer          |
      | https://backup-test.sormas-deutschland.de/  | National Clinician          |
      | https://backup-test.sormas-deutschland.de/  | National User               |
      | https://backup-test.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://backup-test.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://bielefeld.sormas-deutschland.de/  | Administrator               |
      | https://bielefeld.sormas-deutschland.de/  | Case Supervisor             |
      | https://bielefeld.sormas-deutschland.de/  | Contact Supervisor          |
      | https://bielefeld.sormas-deutschland.de/  | Event Officer               |
      | https://bielefeld.sormas-deutschland.de/  | Laboratory Officer          |
      | https://bielefeld.sormas-deutschland.de/  | National Clinician          |
      | https://bielefeld.sormas-deutschland.de/  | National User               |
      | https://bielefeld.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://bielefeld.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://bremerhaven.sormas-deutschland.de/  | Administrator               |
      | https://bremerhaven.sormas-deutschland.de/  | Case Supervisor             |
      | https://bremerhaven.sormas-deutschland.de/  | Contact Supervisor          |
      | https://bremerhaven.sormas-deutschland.de/  | Event Officer               |
      | https://bremerhaven.sormas-deutschland.de/  | Laboratory Officer          |
      | https://bremerhaven.sormas-deutschland.de/  | National Clinician          |
      | https://bremerhaven.sormas-deutschland.de/  | National User               |
      | https://bremerhaven.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://bremerhaven.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | Administrator               |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | Case Supervisor             |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | Contact Supervisor          |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | Event Officer               |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | Laboratory Officer          |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | National Clinician          |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | National User               |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://bundesstadt-bonn.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://congo-stable.sormas.netzlink.com/  | Administrator               |
      | https://congo-stable.sormas.netzlink.com/  | Case Supervisor             |
      | https://congo-stable.sormas.netzlink.com/  | Contact Supervisor          |
      | https://congo-stable.sormas.netzlink.com/  | Event Officer               |
      | https://congo-stable.sormas.netzlink.com/  | Laboratory Officer          |
      | https://congo-stable.sormas.netzlink.com/  | National Clinician          |
      | https://congo-stable.sormas.netzlink.com/  | National User               |
      | https://congo-stable.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://congo-stable.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://congo-staging.sormas.netzlink.com/  | Administrator               |
      | https://congo-staging.sormas.netzlink.com/  | Case Supervisor             |
      | https://congo-staging.sormas.netzlink.com/  | Contact Supervisor          |
      | https://congo-staging.sormas.netzlink.com/  | Event Officer               |
      | https://congo-staging.sormas.netzlink.com/  | Laboratory Officer          |
      | https://congo-staging.sormas.netzlink.com/  | National Clinician          |
      | https://congo-staging.sormas.netzlink.com/  | National User               |
      | https://congo-staging.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://congo-staging.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://demo.sormas.org/  | Administrator               |
      | https://demo.sormas.org/  | Case Supervisor             |
      | https://demo.sormas.org/  | Contact Supervisor          |
      | https://demo.sormas.org/  | Event Officer               |
      | https://demo.sormas.org/  | Laboratory Officer          |
      | https://demo.sormas.org/  | National Clinician          |
      | https://demo.sormas.org/  | National User               |
      | https://demo.sormas.org/  | Point of Entry Supervisor   |
      | https://demo.sormas.org/  | Surveillance Supervisor     |
      | https://demo-rc.sormas.netzlink.com/  | Administrator               |
      | https://demo-rc.sormas.netzlink.com/  | Case Supervisor             |
      | https://demo-rc.sormas.netzlink.com/  | Contact Supervisor          |
      | https://demo-rc.sormas.netzlink.com/  | Event Officer               |
      | https://demo-rc.sormas.netzlink.com/  | Laboratory Officer          |
      | https://demo-rc.sormas.netzlink.com/  | National Clinician          |
      | https://demo-rc.sormas.netzlink.com/  | National User               |
      | https://demo-rc.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://demo-rc.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://demo-sormas-stats.sormas.netzlink.com/  | Administrator               |
      | https://demo-sormas-stats.sormas.netzlink.com/  | Case Supervisor             |
      | https://demo-sormas-stats.sormas.netzlink.com/  | Contact Supervisor          |
      | https://demo-sormas-stats.sormas.netzlink.com/  | Event Officer               |
      | https://demo-sormas-stats.sormas.netzlink.com/  | Laboratory Officer          |
      | https://demo-sormas-stats.sormas.netzlink.com/  | National Clinician          |
      | https://demo-sormas-stats.sormas.netzlink.com/  | National User               |
      | https://demo-sormas-stats.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://demo-sormas-stats.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://demoversion.sormas-oegd.de/  | Administrator               |
      | https://demoversion.sormas-oegd.de/  | Case Supervisor             |
      | https://demoversion.sormas-oegd.de/  | Contact Supervisor          |
      | https://demoversion.sormas-oegd.de/  | Event Officer               |
      | https://demoversion.sormas-oegd.de/  | Laboratory Officer          |
      | https://demoversion.sormas-oegd.de/  | National Clinician          |
      | https://demoversion.sormas-oegd.de/  | National User               |
      | https://demoversion.sormas-oegd.de/  | Point of Entry Supervisor   |
      | https://demoversion.sormas-oegd.de/  | Surveillance Supervisor     |
      | https://dev-sormas-stats.sormas.netzlink.com/  | Administrator               |
      | https://dev-sormas-stats.sormas.netzlink.com/  | Case Supervisor             |
      | https://dev-sormas-stats.sormas.netzlink.com/  | Contact Supervisor          |
      | https://dev-sormas-stats.sormas.netzlink.com/  | Event Officer               |
      | https://dev-sormas-stats.sormas.netzlink.com/  | Laboratory Officer          |
      | https://dev-sormas-stats.sormas.netzlink.com/  | National Clinician          |
      | https://dev-sormas-stats.sormas.netzlink.com/  | National User               |
      | https://dev-sormas-stats.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://dev-sormas-stats.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://emsland.sormas-deutschland.de/  | Administrator               |
      | https://emsland.sormas-deutschland.de/  | Case Supervisor             |
      | https://emsland.sormas-deutschland.de/  | Contact Supervisor          |
      | https://emsland.sormas-deutschland.de/  | Event Officer               |
      | https://emsland.sormas-deutschland.de/  | Laboratory Officer          |
      | https://emsland.sormas-deutschland.de/  | National Clinician          |
      | https://emsland.sormas-deutschland.de/  | National User               |
      | https://emsland.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://emsland.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://erfurt.sormas-deutschland.de/  | Administrator               |
      | https://erfurt.sormas-deutschland.de/  | Case Supervisor             |
      | https://erfurt.sormas-deutschland.de/  | Contact Supervisor          |
      | https://erfurt.sormas-deutschland.de/  | Event Officer               |
      | https://erfurt.sormas-deutschland.de/  | Laboratory Officer          |
      | https://erfurt.sormas-deutschland.de/  | National Clinician          |
      | https://erfurt.sormas-deutschland.de/  | National User               |
      | https://erfurt.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://erfurt.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://freeipa-dns-test-client/  | Administrator               |
      | https://freeipa-dns-test-client/  | Case Supervisor             |
      | https://freeipa-dns-test-client/  | Contact Supervisor          |
      | https://freeipa-dns-test-client/  | Event Officer               |
      | https://freeipa-dns-test-client/  | Laboratory Officer          |
      | https://freeipa-dns-test-client/  | National Clinician          |
      | https://freeipa-dns-test-client/  | National User               |
      | https://freeipa-dns-test-client/  | Point of Entry Supervisor   |
      | https://freeipa-dns-test-client/  | Surveillance Supervisor     |
      | https://freeipa-dns-test-server/  | Administrator               |
      | https://freeipa-dns-test-server/  | Case Supervisor             |
      | https://freeipa-dns-test-server/  | Contact Supervisor          |
      | https://freeipa-dns-test-server/  | Event Officer               |
      | https://freeipa-dns-test-server/  | Laboratory Officer          |
      | https://freeipa-dns-test-server/  | National Clinician          |
      | https://freeipa-dns-test-server/  | National User               |
      | https://freeipa-dns-test-server/  | Point of Entry Supervisor   |
      | https://freeipa-dns-test-server/  | Surveillance Supervisor     |
      | https://ga1.sormas-oegd.de/  | Administrator               |
      | https://ga1.sormas-oegd.de/  | Case Supervisor             |
      | https://ga1.sormas-oegd.de/  | Contact Supervisor          |
      | https://ga1.sormas-oegd.de/  | Event Officer               |
      | https://ga1.sormas-oegd.de/  | Laboratory Officer          |
      | https://ga1.sormas-oegd.de/  | National Clinician          |
      | https://ga1.sormas-oegd.de/  | National User               |
      | https://ga1.sormas-oegd.de/  | Point of Entry Supervisor   |
      | https://ga1.sormas-oegd.de/  | Surveillance Supervisor     |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | Administrator               |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | Case Supervisor             |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | Contact Supervisor          |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | Event Officer               |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | Laboratory Officer          |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | National Clinician          |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | National User               |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://gesundheitsamt-bayreuth-standby.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | Administrator               |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | Case Supervisor             |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | Contact Supervisor          |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | Event Officer               |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | Laboratory Officer          |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | National Clinician          |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | National User               |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://gesundheitsamt-bremen.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | Administrator               |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | Case Supervisor             |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | Contact Supervisor          |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | Event Officer               |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | Laboratory Officer          |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | National Clinician          |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | National User               |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://gesundheitsamt-peine.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://getagu.sormas.netzlink.com/  | Administrator               |
      | https://getagu.sormas.netzlink.com/  | Case Supervisor             |
      | https://getagu.sormas.netzlink.com/  | Contact Supervisor          |
      | https://getagu.sormas.netzlink.com/  | Event Officer               |
      | https://getagu.sormas.netzlink.com/  | Laboratory Officer          |
      | https://getagu.sormas.netzlink.com/  | National Clinician          |
      | https://getagu.sormas.netzlink.com/  | National User               |
      | https://getagu.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://getagu.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://giessen.sormas-deutschland.de/  | Administrator               |
      | https://giessen.sormas-deutschland.de/  | Case Supervisor             |
      | https://giessen.sormas-deutschland.de/  | Contact Supervisor          |
      | https://giessen.sormas-deutschland.de/  | Event Officer               |
      | https://giessen.sormas-deutschland.de/  | Laboratory Officer          |
      | https://giessen.sormas-deutschland.de/  | National Clinician          |
      | https://giessen.sormas-deutschland.de/  | National User               |
      | https://giessen.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://giessen.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://gifhorn.sormas-deutschland.de/  | Administrator               |
      | https://gifhorn.sormas-deutschland.de/  | Case Supervisor             |
      | https://gifhorn.sormas-deutschland.de/  | Contact Supervisor          |
      | https://gifhorn.sormas-deutschland.de/  | Event Officer               |
      | https://gifhorn.sormas-deutschland.de/  | Laboratory Officer          |
      | https://gifhorn.sormas-deutschland.de/  | National Clinician          |
      | https://gifhorn.sormas-deutschland.de/  | National User               |
      | https://gifhorn.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://gifhorn.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://infra-sormas-stats.sormas.netzlink.com/  | Administrator               |
      | https://infra-sormas-stats.sormas.netzlink.com/  | Case Supervisor             |
      | https://infra-sormas-stats.sormas.netzlink.com/  | Contact Supervisor          |
      | https://infra-sormas-stats.sormas.netzlink.com/  | Event Officer               |
      | https://infra-sormas-stats.sormas.netzlink.com/  | Laboratory Officer          |
      | https://infra-sormas-stats.sormas.netzlink.com/  | National Clinician          |
      | https://infra-sormas-stats.sormas.netzlink.com/  | National User               |
      | https://infra-sormas-stats.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://infra-sormas-stats.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://inhp-ci.sormas.netzlink.com/  | Administrator               |
      | https://inhp-ci.sormas.netzlink.com/  | Case Supervisor             |
      | https://inhp-ci.sormas.netzlink.com/  | Contact Supervisor          |
      | https://inhp-ci.sormas.netzlink.com/  | Event Officer               |
      | https://inhp-ci.sormas.netzlink.com/  | Laboratory Officer          |
      | https://inhp-ci.sormas.netzlink.com/  | National Clinician          |
      | https://inhp-ci.sormas.netzlink.com/  | National User               |
      | https://inhp-ci.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://inhp-ci.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://international.sormas.netzlink.com/  | Administrator               |
      | https://international.sormas.netzlink.com/  | Case Supervisor             |
      | https://international.sormas.netzlink.com/  | Contact Supervisor          |
      | https://international.sormas.netzlink.com/  | Event Officer               |
      | https://international.sormas.netzlink.com/  | Laboratory Officer          |
      | https://international.sormas.netzlink.com/  | National Clinician          |
      | https://international.sormas.netzlink.com/  | National User               |
      | https://international.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://international.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://int-sormas-x.sormas.netzlink.com/  | Administrator               |
      | https://int-sormas-x.sormas.netzlink.com/  | Case Supervisor             |
      | https://int-sormas-x.sormas.netzlink.com/  | Contact Supervisor          |
      | https://int-sormas-x.sormas.netzlink.com/  | Event Officer               |
      | https://int-sormas-x.sormas.netzlink.com/  | Laboratory Officer          |
      | https://int-sormas-x.sormas.netzlink.com/  | National Clinician          |
      | https://int-sormas-x.sormas.netzlink.com/  | National User               |
      | https://int-sormas-x.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://int-sormas-x.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://isga.sormas.netzlink.com/  | Administrator               |
      | https://isga.sormas.netzlink.com/  | Case Supervisor             |
      | https://isga.sormas.netzlink.com/  | Contact Supervisor          |
      | https://isga.sormas.netzlink.com/  | Event Officer               |
      | https://isga.sormas.netzlink.com/  | Laboratory Officer          |
      | https://isga.sormas.netzlink.com/  | National Clinician          |
      | https://isga.sormas.netzlink.com/  | National User               |
      | https://isga.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://isga.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | Administrator               |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | Event Officer               |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | National Clinician          |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | National User               |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreisausschuss-rheingau-taunus.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | Administrator               |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | Event Officer               |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | National Clinician          |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | National User               |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreisgesundheitsamt-rhein-kreis-neuss.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | Administrator               |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | Event Officer               |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | National Clinician          |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | National User               |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreis-gross-gerau.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://kreis-herford.sormas-deutschland.de/  | Administrator               |
      | https://kreis-herford.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreis-herford.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreis-herford.sormas-deutschland.de/  | Event Officer               |
      | https://kreis-herford.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreis-herford.sormas-deutschland.de/  | National Clinician          |
      | https://kreis-herford.sormas-deutschland.de/  | National User               |
      | https://kreis-herford.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreis-herford.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://kreis-kleve.sormas-deutschland.de/  | Administrator               |
      | https://kreis-kleve.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreis-kleve.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreis-kleve.sormas-deutschland.de/  | Event Officer               |
      | https://kreis-kleve.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreis-kleve.sormas-deutschland.de/  | National Clinician          |
      | https://kreis-kleve.sormas-deutschland.de/  | National User               |
      | https://kreis-kleve.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreis-kleve.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://kreis-main-taunus.sormas-deutschland.de/  | Administrator               |
      | https://kreis-main-taunus.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreis-main-taunus.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreis-main-taunus.sormas-deutschland.de/  | Event Officer               |
      | https://kreis-main-taunus.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreis-main-taunus.sormas-deutschland.de/  | National Clinician          |
      | https://kreis-main-taunus.sormas-deutschland.de/  | National User               |
      | https://kreis-main-taunus.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreis-main-taunus.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://kreis-mettmann.sormas-deutschland.de/  | Administrator               |
      | https://kreis-mettmann.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreis-mettmann.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreis-mettmann.sormas-deutschland.de/  | Event Officer               |
      | https://kreis-mettmann.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreis-mettmann.sormas-deutschland.de/  | National Clinician          |
      | https://kreis-mettmann.sormas-deutschland.de/  | National User               |
      | https://kreis-mettmann.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreis-mettmann.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | Administrator               |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | Event Officer               |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | National Clinician          |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | National User               |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreisverwaltung-bad-kreuznach.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | Administrator               |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | Event Officer               |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | National Clinician          |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | National User               |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://kreisverwaltung-donnersbergkreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landkreis-barnim.sormas-deutschland.de/  | Administrator               |
      | https://landkreis-barnim.sormas-deutschland.de/  | Case Supervisor             |
      | https://landkreis-barnim.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landkreis-barnim.sormas-deutschland.de/  | Event Officer               |
      | https://landkreis-barnim.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landkreis-barnim.sormas-deutschland.de/  | National Clinician          |
      | https://landkreis-barnim.sormas-deutschland.de/  | National User               |
      | https://landkreis-barnim.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landkreis-barnim.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landkreis-celle.sormas-deutschland.de/  | Administrator               |
      | https://landkreis-celle.sormas-deutschland.de/  | Case Supervisor             |
      | https://landkreis-celle.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landkreis-celle.sormas-deutschland.de/  | Event Officer               |
      | https://landkreis-celle.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landkreis-celle.sormas-deutschland.de/  | National Clinician          |
      | https://landkreis-celle.sormas-deutschland.de/  | National User               |
      | https://landkreis-celle.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landkreis-celle.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | Administrator               |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | Case Supervisor             |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | Event Officer               |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | National Clinician          |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | National User               |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landkreis-coesfeld.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landkreis-harburg.sormas-deutschland.de/  | Administrator               |
      | https://landkreis-harburg.sormas-deutschland.de/  | Case Supervisor             |
      | https://landkreis-harburg.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landkreis-harburg.sormas-deutschland.de/  | Event Officer               |
      | https://landkreis-harburg.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landkreis-harburg.sormas-deutschland.de/  | National Clinician          |
      | https://landkreis-harburg.sormas-deutschland.de/  | National User               |
      | https://landkreis-harburg.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landkreis-harburg.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | Administrator               |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | Case Supervisor             |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | Event Officer               |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | National Clinician          |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | National User               |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landkreis-ludwigslust-parchim.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | Administrator               |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | Case Supervisor             |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | Event Officer               |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | National Clinician          |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | National User               |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landkreis-marburg-biedenkopf.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | Administrator               |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | Case Supervisor             |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | Event Officer               |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | National Clinician          |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | National User               |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landkreis-oder-spree.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | Administrator               |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | Event Officer               |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | National Clinician          |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | National User               |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landrat-rhein-erft-kreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | Administrator               |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | Event Officer               |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | National Clinician          |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | National User               |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landratsamt-enzkreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | Administrator               |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | Event Officer               |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | National Clinician          |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | National User               |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landratsamt-ilm-kreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | Administrator               |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | Event Officer               |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | National Clinician          |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | National User               |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landratsamt-kyffhaeuserkreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | Administrator               |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | Event Officer               |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | National Clinician          |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | National User               |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://landratsamt-ostalbkreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://lbds.sormas.netzlink.com/  | Administrator               |
      | https://lbds.sormas.netzlink.com/  | Case Supervisor             |
      | https://lbds.sormas.netzlink.com/  | Contact Supervisor          |
      | https://lbds.sormas.netzlink.com/  | Event Officer               |
      | https://lbds.sormas.netzlink.com/  | Laboratory Officer          |
      | https://lbds.sormas.netzlink.com/  | National Clinician          |
      | https://lbds.sormas.netzlink.com/  | National User               |
      | https://lbds.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://lbds.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | Administrator               |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | Event Officer               |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | National Clinician          |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | National User               |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://oberbergischer-kreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://octoware.sormas.netzlink.com/  | Administrator               |
      | https://octoware.sormas.netzlink.com/  | Case Supervisor             |
      | https://octoware.sormas.netzlink.com/  | Contact Supervisor          |
      | https://octoware.sormas.netzlink.com/  | Event Officer               |
      | https://octoware.sormas.netzlink.com/  | Laboratory Officer          |
      | https://octoware.sormas.netzlink.com/  | National Clinician          |
      | https://octoware.sormas.netzlink.com/  | National User               |
      | https://octoware.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://octoware.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | Administrator               |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | Case Supervisor             |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | Contact Supervisor          |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | Event Officer               |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | Laboratory Officer          |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | National Clinician          |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | National User               |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://ovh-sormas-prod-test-fha-01.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://preprod-rki-01.sormas.netzlink.com/  | Administrator               |
      | https://preprod-rki-01.sormas.netzlink.com/  | Case Supervisor             |
      | https://preprod-rki-01.sormas.netzlink.com/  | Contact Supervisor          |
      | https://preprod-rki-01.sormas.netzlink.com/  | Event Officer               |
      | https://preprod-rki-01.sormas.netzlink.com/  | Laboratory Officer          |
      | https://preprod-rki-01.sormas.netzlink.com/  | National Clinician          |
      | https://preprod-rki-01.sormas.netzlink.com/  | National User               |
      | https://preprod-rki-01.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://preprod-rki-01.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | Administrator               |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | Case Supervisor             |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | Contact Supervisor          |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | Event Officer               |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | Laboratory Officer          |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | National Clinician          |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | National User               |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://regionalverband-saarbruecken.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://release-de.sormas.netzlink.com/  | Administrator               |
      | https://release-de.sormas.netzlink.com/  | Case Supervisor             |
      | https://release-de.sormas.netzlink.com/  | Contact Supervisor          |
      | https://release-de.sormas.netzlink.com/  | Event Officer               |
      | https://release-de.sormas.netzlink.com/  | Laboratory Officer          |
      | https://release-de.sormas.netzlink.com/  | National Clinician          |
      | https://release-de.sormas.netzlink.com/  | National User               |
      | https://release-de.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://release-de.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://release-international.sormas.netzlink.com/  | Administrator               |
      | https://release-international.sormas.netzlink.com/  | Case Supervisor             |
      | https://release-international.sormas.netzlink.com/  | Contact Supervisor          |
      | https://release-international.sormas.netzlink.com/  | Event Officer               |
      | https://release-international.sormas.netzlink.com/  | Laboratory Officer          |
      | https://release-international.sormas.netzlink.com/  | National Clinician          |
      | https://release-international.sormas.netzlink.com/  | National User               |
      | https://release-international.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://release-international.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://release-sormas-x.sormas.netzlink.com/  | Administrator               |
      | https://release-sormas-x.sormas.netzlink.com/  | Case Supervisor             |
      | https://release-sormas-x.sormas.netzlink.com/  | Contact Supervisor          |
      | https://release-sormas-x.sormas.netzlink.com/  | Event Officer               |
      | https://release-sormas-x.sormas.netzlink.com/  | Laboratory Officer          |
      | https://release-sormas-x.sormas.netzlink.com/  | National Clinician          |
      | https://release-sormas-x.sormas.netzlink.com/  | National User               |
      | https://release-sormas-x.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://release-sormas-x.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | Administrator               |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | Event Officer               |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | National Clinician          |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | National User               |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://rhein-sieg-kreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://schnischu.sormas.netzlink.com/  | Administrator               |
      | https://schnischu.sormas.netzlink.com/  | Case Supervisor             |
      | https://schnischu.sormas.netzlink.com/  | Contact Supervisor          |
      | https://schnischu.sormas.netzlink.com/  | Event Officer               |
      | https://schnischu.sormas.netzlink.com/  | Laboratory Officer          |
      | https://schnischu.sormas.netzlink.com/  | National Clinician          |
      | https://schnischu.sormas.netzlink.com/  | National User               |
      | https://schnischu.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://schnischu.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | Administrator               |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | Case Supervisor             |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | Contact Supervisor          |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | Event Officer               |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | Laboratory Officer          |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | National Clinician          |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | National User               |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://schulung-sb-sormas-de.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | Administrator               |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | Event Officer               |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | National Clinician          |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | National User               |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://schwalm-eder-kreis.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://schwerin.sormas-deutschland.de/  | Administrator               |
      | https://schwerin.sormas-deutschland.de/  | Case Supervisor             |
      | https://schwerin.sormas-deutschland.de/  | Contact Supervisor          |
      | https://schwerin.sormas-deutschland.de/  | Event Officer               |
      | https://schwerin.sormas-deutschland.de/  | Laboratory Officer          |
      | https://schwerin.sormas-deutschland.de/  | National Clinician          |
      | https://schwerin.sormas-deutschland.de/  | National User               |
      | https://schwerin.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://schwerin.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://stadt-cottbus.sormas-deutschland.de/  | Administrator               |
      | https://stadt-cottbus.sormas-deutschland.de/  | Case Supervisor             |
      | https://stadt-cottbus.sormas-deutschland.de/  | Contact Supervisor          |
      | https://stadt-cottbus.sormas-deutschland.de/  | Event Officer               |
      | https://stadt-cottbus.sormas-deutschland.de/  | Laboratory Officer          |
      | https://stadt-cottbus.sormas-deutschland.de/  | National Clinician          |
      | https://stadt-cottbus.sormas-deutschland.de/  | National User               |
      | https://stadt-cottbus.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://stadt-cottbus.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://stadt-hamm.sormas-deutschland.de/  | Administrator               |
      | https://stadt-hamm.sormas-deutschland.de/  | Case Supervisor             |
      | https://stadt-hamm.sormas-deutschland.de/  | Contact Supervisor          |
      | https://stadt-hamm.sormas-deutschland.de/  | Event Officer               |
      | https://stadt-hamm.sormas-deutschland.de/  | Laboratory Officer          |
      | https://stadt-hamm.sormas-deutschland.de/  | National Clinician          |
      | https://stadt-hamm.sormas-deutschland.de/  | National User               |
      | https://stadt-hamm.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://stadt-hamm.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | Administrator               |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | Case Supervisor             |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | Contact Supervisor          |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | Event Officer               |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | Laboratory Officer          |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | National Clinician          |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | National User               |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://stadt-moenchengladbach.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://stadt-salzgitter.sormas-deutschland.de/  | Administrator               |
      | https://stadt-salzgitter.sormas-deutschland.de/  | Case Supervisor             |
      | https://stadt-salzgitter.sormas-deutschland.de/  | Contact Supervisor          |
      | https://stadt-salzgitter.sormas-deutschland.de/  | Event Officer               |
      | https://stadt-salzgitter.sormas-deutschland.de/  | Laboratory Officer          |
      | https://stadt-salzgitter.sormas-deutschland.de/  | National Clinician          |
      | https://stadt-salzgitter.sormas-deutschland.de/  | National User               |
      | https://stadt-salzgitter.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://stadt-salzgitter.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://survnet-import-test.sormas.netzlink.com/  | Administrator               |
      | https://survnet-import-test.sormas.netzlink.com/  | Case Supervisor             |
      | https://survnet-import-test.sormas.netzlink.com/  | Contact Supervisor          |
      | https://survnet-import-test.sormas.netzlink.com/  | Event Officer               |
      | https://survnet-import-test.sormas.netzlink.com/  | Laboratory Officer          |
      | https://survnet-import-test.sormas.netzlink.com/  | National Clinician          |
      | https://survnet-import-test.sormas.netzlink.com/  | National User               |
      | https://survnet-import-test.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://survnet-import-test.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test.sormas.netzlink.com/  | Administrator               |
      | https://test.sormas.netzlink.com/  | Case Supervisor             |
      | https://test.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test.sormas.netzlink.com/  | Event Officer               |
      | https://test.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test.sormas.netzlink.com/  | National Clinician          |
      | https://test.sormas.netzlink.com/  | National User               |
      | https://test.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test1401.sormas-oegd.de/  | Administrator               |
      | https://test1401.sormas-oegd.de/  | Case Supervisor             |
      | https://test1401.sormas-oegd.de/  | Contact Supervisor          |
      | https://test1401.sormas-oegd.de/  | Event Officer               |
      | https://test1401.sormas-oegd.de/  | Laboratory Officer          |
      | https://test1401.sormas-oegd.de/  | National Clinician          |
      | https://test1401.sormas-oegd.de/  | National User               |
      | https://test1401.sormas-oegd.de/  | Point of Entry Supervisor   |
      | https://test1401.sormas-oegd.de/  | Surveillance Supervisor     |
      | https://test1402.sormas.netzlink.com/  | Administrator               |
      | https://test1402.sormas.netzlink.com/  | Case Supervisor             |
      | https://test1402.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test1402.sormas.netzlink.com/  | Event Officer               |
      | https://test1402.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test1402.sormas.netzlink.com/  | National Clinician          |
      | https://test1402.sormas.netzlink.com/  | National User               |
      | https://test1402.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test1402.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-auto.sormas.netzlink.com/  | Administrator               |
      | https://test-auto.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-auto.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-auto.sormas.netzlink.com/  | Event Officer               |
      | https://test-auto.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-auto.sormas.netzlink.com/  | National Clinician          |
      | https://test-auto.sormas.netzlink.com/  | National User               |
      | https://test-auto.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-auto.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-auto-keycloak.sormas.netzlink.com/  | Administrator               |
      | https://test-auto-keycloak.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-auto-keycloak.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-auto-keycloak.sormas.netzlink.com/  | Event Officer               |
      | https://test-auto-keycloak.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-auto-keycloak.sormas.netzlink.com/  | National Clinician          |
      | https://test-auto-keycloak.sormas.netzlink.com/  | National User               |
      | https://test-auto-keycloak.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-auto-keycloak.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-de.sormas.netzlink.com/  | Administrator               |
      | https://test-de.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-de.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-de.sormas.netzlink.com/  | Event Officer               |
      | https://test-de.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-de.sormas.netzlink.com/  | National Clinician          |
      | https://test-de.sormas.netzlink.com/  | National User               |
      | https://test-de.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-de.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-de1.sormas.netzlink.com/  | Administrator               |
      | https://test-de1.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-de1.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-de1.sormas.netzlink.com/  | Event Officer               |
      | https://test-de1.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-de1.sormas.netzlink.com/  | National Clinician          |
      | https://test-de1.sormas.netzlink.com/  | National User               |
      | https://test-de1.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-de1.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-de2.sormas.netzlink.com/  | Administrator               |
      | https://test-de2.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-de2.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-de2.sormas.netzlink.com/  | Event Officer               |
      | https://test-de2.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-de2.sormas.netzlink.com/  | National Clinician          |
      | https://test-de2.sormas.netzlink.com/  | National User               |
      | https://test-de2.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-de2.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-de3.sormas.netzlink.com/  | Administrator               |
      | https://test-de3.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-de3.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-de3.sormas.netzlink.com/  | Event Officer               |
      | https://test-de3.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-de3.sormas.netzlink.com/  | National Clinician          |
      | https://test-de3.sormas.netzlink.com/  | National User               |
      | https://test-de3.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-de3.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-devops-gitops.sormas.netzlink.com/  | Administrator               |
      | https://test-devops-gitops.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-devops-gitops.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-devops-gitops.sormas.netzlink.com/  | Event Officer               |
      | https://test-devops-gitops.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-devops-gitops.sormas.netzlink.com/  | National Clinician          |
      | https://test-devops-gitops.sormas.netzlink.com/  | National User               |
      | https://test-devops-gitops.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-devops-gitops.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-fha-01.sormas.netzlink.com/  | Administrator               |
      | https://test-fha-01.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-fha-01.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-fha-01.sormas.netzlink.com/  | Event Officer               |
      | https://test-fha-01.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-fha-01.sormas.netzlink.com/  | National Clinician          |
      | https://test-fha-01.sormas.netzlink.com/  | National User               |
      | https://test-fha-01.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-fha-01.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-fr-perf-01.sormas.netzlink.com/  | Administrator               |
      | https://test-fr-perf-01.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-fr-perf-01.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-fr-perf-01.sormas.netzlink.com/  | Event Officer               |
      | https://test-fr-perf-01.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-fr-perf-01.sormas.netzlink.com/  | National Clinician          |
      | https://test-fr-perf-01.sormas.netzlink.com/  | National User               |
      | https://test-fr-perf-01.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-fr-perf-01.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-fr-perf-02.sormas.netzlink.com/  | Administrator               |
      | https://test-fr-perf-02.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-fr-perf-02.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-fr-perf-02.sormas.netzlink.com/  | Event Officer               |
      | https://test-fr-perf-02.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-fr-perf-02.sormas.netzlink.com/  | National Clinician          |
      | https://test-fr-perf-02.sormas.netzlink.com/  | National User               |
      | https://test-fr-perf-02.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-fr-perf-02.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-inhp-ci.sormas.netzlink.com/  | Administrator               |
      | https://test-inhp-ci.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-inhp-ci.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-inhp-ci.sormas.netzlink.com/  | Event Officer               |
      | https://test-inhp-ci.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-inhp-ci.sormas.netzlink.com/  | National Clinician          |
      | https://test-inhp-ci.sormas.netzlink.com/  | National User               |
      | https://test-inhp-ci.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-inhp-ci.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | Administrator               |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | Event Officer               |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | National Clinician          |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | National User               |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-muenchen-stadt.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-performance.sormas.netzlink.com/  | Administrator               |
      | https://test-performance.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-performance.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-performance.sormas.netzlink.com/  | Event Officer               |
      | https://test-performance.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-performance.sormas.netzlink.com/  | National Clinician          |
      | https://test-performance.sormas.netzlink.com/  | National User               |
      | https://test-performance.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-performance.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-pipeline.sormas.netzlink.com/  | Administrator               |
      | https://test-pipeline.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-pipeline.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-pipeline.sormas.netzlink.com/  | Event Officer               |
      | https://test-pipeline.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-pipeline.sormas.netzlink.com/  | National Clinician          |
      | https://test-pipeline.sormas.netzlink.com/  | National User               |
      | https://test-pipeline.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-pipeline.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-pp01.sormas-deutschland.de/  | Administrator               |
      | https://test-pp01.sormas-deutschland.de/  | Case Supervisor             |
      | https://test-pp01.sormas-deutschland.de/  | Contact Supervisor          |
      | https://test-pp01.sormas-deutschland.de/  | Event Officer               |
      | https://test-pp01.sormas-deutschland.de/  | Laboratory Officer          |
      | https://test-pp01.sormas-deutschland.de/  | National Clinician          |
      | https://test-pp01.sormas-deutschland.de/  | National User               |
      | https://test-pp01.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://test-pp01.sormas-deutschland.de/  | Surveillance Supervisor     |
      | https://test-rki.sormas.netzlink.com/  | Administrator               |
      | https://test-rki.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-rki.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-rki.sormas.netzlink.com/  | Event Officer               |
      | https://test-rki.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-rki.sormas.netzlink.com/  | National Clinician          |
      | https://test-rki.sormas.netzlink.com/  | National User               |
      | https://test-rki.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-rki.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-survnet-1.sormas.netzlink.com/  | Administrator               |
      | https://test-survnet-1.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-survnet-1.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-survnet-1.sormas.netzlink.com/  | Event Officer               |
      | https://test-survnet-1.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-survnet-1.sormas.netzlink.com/  | National Clinician          |
      | https://test-survnet-1.sormas.netzlink.com/  | National User               |
      | https://test-survnet-1.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-survnet-1.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-survnet-2.sormas.netzlink.com/  | Administrator               |
      | https://test-survnet-2.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-survnet-2.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-survnet-2.sormas.netzlink.com/  | Event Officer               |
      | https://test-survnet-2.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-survnet-2.sormas.netzlink.com/  | National Clinician          |
      | https://test-survnet-2.sormas.netzlink.com/  | National User               |
      | https://test-survnet-2.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-survnet-2.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-two-01.sormas.netzlink.com/  | Administrator               |
      | https://test-two-01.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-two-01.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-two-01.sormas.netzlink.com/  | Event Officer               |
      | https://test-two-01.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-two-01.sormas.netzlink.com/  | National Clinician          |
      | https://test-two-01.sormas.netzlink.com/  | National User               |
      | https://test-two-01.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-two-01.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://test-two-01-u2204.sormas.netzlink.com/  | Administrator               |
      | https://test-two-01-u2204.sormas.netzlink.com/  | Case Supervisor             |
      | https://test-two-01-u2204.sormas.netzlink.com/  | Contact Supervisor          |
      | https://test-two-01-u2204.sormas.netzlink.com/  | Event Officer               |
      | https://test-two-01-u2204.sormas.netzlink.com/  | Laboratory Officer          |
      | https://test-two-01-u2204.sormas.netzlink.com/  | National Clinician          |
      | https://test-two-01-u2204.sormas.netzlink.com/  | National User               |
      | https://test-two-01-u2204.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://test-two-01-u2204.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://verify-sormas-three.sormas.netzlink.com/  | Administrator               |
      | https://verify-sormas-three.sormas.netzlink.com/  | Case Supervisor             |
      | https://verify-sormas-three.sormas.netzlink.com/  | Contact Supervisor          |
      | https://verify-sormas-three.sormas.netzlink.com/  | Event Officer               |
      | https://verify-sormas-three.sormas.netzlink.com/  | Laboratory Officer          |
      | https://verify-sormas-three.sormas.netzlink.com/  | National Clinician          |
      | https://verify-sormas-three.sormas.netzlink.com/  | National User               |
      | https://verify-sormas-three.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://verify-sormas-three.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://verify-sormas-two.sormas.netzlink.com/  | Administrator               |
      | https://verify-sormas-two.sormas.netzlink.com/  | Case Supervisor             |
      | https://verify-sormas-two.sormas.netzlink.com/  | Contact Supervisor          |
      | https://verify-sormas-two.sormas.netzlink.com/  | Event Officer               |
      | https://verify-sormas-two.sormas.netzlink.com/  | Laboratory Officer          |
      | https://verify-sormas-two.sormas.netzlink.com/  | National Clinician          |
      | https://verify-sormas-two.sormas.netzlink.com/  | National User               |
      | https://verify-sormas-two.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://verify-sormas-two.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | Administrator               |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | Case Supervisor             |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | Contact Supervisor          |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | Event Officer               |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | Laboratory Officer          |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | National Clinician          |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | National User               |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | Point of Entry Supervisor   |
      | https://werra-meissner-kreis.sormas-deutschland.de/  | Surveillance Supervisor     |

