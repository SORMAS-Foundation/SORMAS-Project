/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NEW_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.NEW_CONTACT_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.NEW_CONTACT_PAGE_BUTTON;
import static org.sormas.e2etests.pages.application.dashboard.Contacts.ContactsDashboardPage.CONTACTS_DASHBOARD_NAME;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CONTACTS_BUTTON;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.SURVEILLANCE_DASHBOARD_NAME;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.NEW_EVENT_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.ADD_NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.SEARCH_PERSON_BY_FREE_TEXT;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.GENERAL_SEARCH_INPUT;

import cucumber.api.java8.En;
import customreport.data.TableDataManager;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.CASES_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });

    When(
        "^I click on the Cases button from navbar and start timer$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.CASES_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
        });

    When(
        "^I click on the Contacts button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.CONTACTS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEW_CONTACT_BUTTON);
        });

    When(
        "^I click on the Contacts button from navbar and start timer$",
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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });

    When(
        "^I click on the Events button from navbar and start timer$",
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
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(NavBarPage.TASKS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.TASKS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });

    When(
        "^I click on the Tasks button from navbar and start timer$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(NavBarPage.TASKS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.TASKS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I click on the Persons button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.PERSONS_BUTTON);
        });

    When(
        "^I click on the Persons button from navbar and start timer$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.PERSONS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the About button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.ABOUT_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the User Settings button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.USER_SETTINGS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Configuration button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.CONFIGURATION_BUTTON);
        });

    When(
        "^I click on the Dashboard button from navbar and access Surveillance Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Persons button then Dashboard button from navbar and access Surveillance Dashboard$",
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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONTACTS_BUTTON);
          TimeUnit.SECONDS.sleep(10); // mandatory due to loading time issue
          webDriverHelpers.clickOnWebElementBySelector(CONTACTS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONTACTS_DASHBOARD_NAME, 10);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Sample button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.SAMPLE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });

    When(
        "^I click on the Sample button from navbar and start timer$",
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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });

    When(
        "^I click on the Immunizations button from navbar and start timer$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.IMMUNIZATIONS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Reports button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.REPORTS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "^I click on the Users from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.USERS_BUTTON);
          startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        });

    When(
        "I click on logout button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.LOGOUT_BUTTON);
        });

    When(
        "^I click on the Entries button from navbar$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.ENTRIES_BUTTON);
        });

    Then(
        "I wait for {string} page to load and calculate elapsed time",
        (String page) -> {
          try {
            switch (page) {
              case ("Surveillance Dashboard"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    SURVEILLANCE_DASHBOARD_NAME);
                break;
              case ("Contacts Dashboard"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    CONTACTS_DASHBOARD_NAME);
                break;
              case ("Tasks"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    GENERAL_SEARCH_INPUT);
                break;
              case ("Persons"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    SEARCH_PERSON_BY_FREE_TEXT);
                break;
              case ("Cases"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    NEW_CASE_BUTTON);
                break;
              case ("Contacts"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    NEW_CONTACT_PAGE_BUTTON);
                break;
              case ("Events"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    NEW_EVENT_BUTTON);
                break;
              case ("Samples"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    SAMPLE_SEARCH_INPUT);
                break;
              case ("Immunizations"):
                webDriverHelpers.isElementDisplayedAndNoLoadingSpinnerOrThrowException(
                    ADD_NEW_IMMUNIZATION_BUTTON);
                break;
            }
            endTime = ZonedDateTime.now().toInstant().toEpochMilli();
            long diff = endTime - startTime;
            String totalTime = new SimpleDateFormat("s:SS").format(diff).replace(":", ".");
            elapsedTime = totalTime;
          } catch (Exception exception) {
            elapsedTime = "Couldn't load page under 60s";
          }
          log.info("Adding page [ {} ] loading results to report", page);
          TableDataManager.addPagesRowEntity(page + " page", elapsedTime);
        });
  }
}
