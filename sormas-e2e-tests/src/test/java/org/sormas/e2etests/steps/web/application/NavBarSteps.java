/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NEW_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.NEW_CONTACT_BUTTON;
import static org.sormas.e2etests.pages.application.dashboard.Contacts.ContactsDashboardPage.CONTACTS_DASHBOARD_NAME;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.SURVEILLANCE_DASHBOARD_NAME;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.NEW_EVENT_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.ADD_NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.SEARCH_PERSON_BY_FREE_TEXT;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.GENERAL_SEARCH_INPUT;

import cucumber.api.java8.En;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;
import org.sormas.e2etests.pages.application.events.EventDirectoryPage;

@Slf4j
public class NavBarSteps implements En {

  private long startTime;
  private long endTime;
  public static String elapsedTime;

  @Inject
  public NavBarSteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I click on the Cases button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.CASES_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Contacts button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.CONTACTS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Events button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.EVENTS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Actions button from Events view switcher$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(
              EventDirectoryPage.EVENT_ACTIONS_RADIOBUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Tasks button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.TASKS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Persons button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.PERSONS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Dashboard button from navbar and access Surveillance Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.PERSONS_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Dashboard button from navbar and access Contacts Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CONTACTS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.CONTACTS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.CONTACTS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Sample button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.SAMPLE_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Immunizations button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.IMMUNIZATIONS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Users from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.USERS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    Then(
        "I wait for {string} page to load and calculate elapsed time",
        (String page) -> {
          try {
            switch (page) {
              case ("Surveillance Dashboard"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
                    SURVEILLANCE_DASHBOARD_NAME);
                break;
              case ("Contacts Dashboard"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
                    CONTACTS_DASHBOARD_NAME);
                break;
              case ("Tasks"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
                    GENERAL_SEARCH_INPUT);
                break;
              case ("Persons"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
                    SEARCH_PERSON_BY_FREE_TEXT);
                break;
              case ("Cases"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(NEW_CASE_BUTTON);
                break;
              case ("Contacts"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(NEW_CONTACT_BUTTON);
                break;
              case ("Events"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(NEW_EVENT_BUTTON);
                break;
              case ("Samples"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(SAMPLE_SEARCH_INPUT);
                break;
              case ("Immunizations"):
                webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
                    ADD_NEW_IMMUNIZATION_BUTTON);
                break;
            }
            endTime = ZonedDateTime.now().toInstant().toEpochMilli();
            long diff = endTime - startTime;
            String totalTime = new SimpleDateFormat("s:SS").format(diff).replace(":", ".");
            elapsedTime = totalTime;
          } catch (Exception exception) {
            elapsedTime = "22";
          }
        });
  }
}
