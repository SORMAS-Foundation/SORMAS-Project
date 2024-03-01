/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.steps.web.application.users;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CANCEL;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.*;
import static org.sormas.e2etests.pages.application.users.EditUserPage.SAVE_BUTTON_EDIT_USER;
import static org.sormas.e2etests.pages.application.users.UserManagementPage.*;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.ENABLED_USER_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EXPORT_USER_ROLES;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.JURISDICTION_LEVEL_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_RIGHTS_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_RIGHTS_INPUT;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_TABLE_GRID;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.getJurisdictionLevelCaptionByText;

import cucumber.api.java8.En;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.users.UserManagementPage;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class UserManagementSteps implements En {
  public static int numberOfUsers;
  protected WebDriverHelpers webDriverHelpers;
  public static int numberOfRows;

  @Inject
  public UserManagementSteps(
      WebDriverHelpers webDriverHelpers, AssertHelpers assertHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I click on the NEW USER button$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEW_USER_BUTTON);
          webDriverHelpers.clickWhileOtherButtonIsDisplayed(
              NEW_USER_BUTTON, FIRST_NAME_OF_USER_INPUT);
        });

    Then(
        "^I set active inactive filter to ([^\"]*) in User Management directory$",
        (String activeInactive) -> {
          webDriverHelpers.selectFromCombobox(ACTIVE_INACTIVE_COMBOBOX, activeInactive);
          TimeUnit.SECONDS.sleep(2); // needed for table to refresh
        });

    When(
        "^I select first user from list$",
        () -> {
          TimeUnit.SECONDS.sleep(1);
          selectFirstElementFromList();
        });

    When(
        "^I search for created user$",
        () -> {
          searchForUser(CreateNewUserSteps.user.getUserName());
          selectFirstElementFromList();
        });

    When(
        "I search for created user in the User Management Page",
        () -> {
          searchForUser(CreateNewUserSteps.user.getUserName());
        });

    When(
        "^I search for recently edited user$",
        () -> {
          searchForUser(CreateNewUserSteps.editUser.getUserName());
          selectFirstElementFromList();
        });

    When(
        "^I count the number of users displayed in User Directory$",
        () ->
            numberOfUsers = Integer.parseInt(webDriverHelpers.getTextFromWebElement(USER_NUMBER)));

    When(
        "^I click on Sync Users button$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SYNC_USERS_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SYNC_POPUP_BUTTON);
        });
    When(
        "^I click on Sync button from Sync Users popup$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SYNC_POPUP_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ACTION_CANCEL);
        });
    When(
        "^I check if sync message is correct in German$",
        () -> {
          TimeUnit.SECONDS.sleep(45);
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertTrue(
                      webDriverHelpers.isElementVisibleWithTimeout(SYNC_SUCCESS_DE, 15),
                      "Sync of users failed"),
              10);
        });

    When(
        "^I verify that the Active value is ([^\"]*) in the User Management Page",
        (String option) -> {
          switch (option) {
            case "Checked":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  ACTIVE_CHECKBOX_USER_MANAGEMENT);
              break;
            case "Unchecked":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  INACTIVE_CHECKBOX_USER_MANAGEMENT);
              break;
            default:
              throw new IllegalArgumentException("No valid Switch options were provided");
          }
        });

    Then(
        "I Verify the number of Active, Inactive and Total users in the User Management Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              USERS_COUNTER_USER_MANAGEMENT);

          Integer numberOfTotalUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));

          webDriverHelpers.selectFromCombobox(ACTIVE_INACTIVE_COMBOBOX, "Active");
          TimeUnit.SECONDS.sleep(2); // waiting for page loaded
          Integer numberOfActiveUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));

          webDriverHelpers.selectFromCombobox(ACTIVE_INACTIVE_COMBOBOX, "Inactive");
          TimeUnit.SECONDS.sleep(2); // waiting for page loaded
          Integer numberOfInactiveUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));

          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertTrue(
                      numberOfTotalUsers == numberOfActiveUsers + numberOfInactiveUsers,
                      "Sync of users failed in User Management Page"),
              10);
        });

    Then(
        "I Verify The User Role filter in the User Management Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              USERS_COUNTER_USER_MANAGEMENT);
          Integer numberOfTotalUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(USER_ROLES_COMBOBOX, "National User");
          //
          // webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOADING_INDICATOR);
          //          webDriverHelpers.waitUntilIdentifiedElementDisappear(LOADING_INDICATOR);
          Integer numberOfSpecificUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(USER_ROLES_COMBOBOX, "");
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertFalse(
                      numberOfTotalUsers == numberOfSpecificUsers,
                      "User Roles Filer ComboBox failed in User Management Page"),
              10);
        });

    Then(
        "I Verify Region filter in the User Management Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              USERS_COUNTER_USER_MANAGEMENT);
          Integer numberOfTotalUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(REGION_COMBOBOX_USER_MANAGEMENT, "Bayern");
          //
          // webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOADING_INDICATOR);
          //          webDriverHelpers.waitUntilIdentifiedElementDisappear(LOADING_INDICATOR);
          Integer numberOfSpecificUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(REGION_COMBOBOX_USER_MANAGEMENT, "");
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertFalse(
                      numberOfTotalUsers == numberOfSpecificUsers,
                      "User Roles Filer ComboBox failed in User Management Page"),
              10);
        });

    And(
        "^I click on User roles tab from Users Page$",
        () -> {
          webDriverHelpers.scrollToElement(USER_ROLES_TAB);
          webDriverHelpers.clickOnWebElementBySelector(USER_ROLES_TAB);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(USER_RIGHTS_INPUT);
          TimeUnit.SECONDS.sleep(4);
        });

    And(
        "I check that {string} error popup message is appear in Management Page",
        (String errorMessage) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ERROR_USER_MANAGEMENT_POPUP);
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(ERROR_USER_MANAGEMENT_POPUP),
              errorMessage,
              "Error popup message not appear!");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(ERROR_USER_MANAGEMENT_POPUP);
        });

    And(
        "Validate user can see User roles tab from Users directory Page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(USER_ROLES_TAB);
        });

    And(
        "^I check that \"([^\"]*)\" is not available in the user role filter$",
        (String userRole) -> {
          softly.assertFalse(
              webDriverHelpers.checkIfElementExistsInCombobox(USER_ROLES_COMBOBOX, userRole),
              "Provided user role is available in the user role template dropdown menu!");
          softly.assertAll();
        });

    And(
        "^I check that \"([^\"]*)\" is available in the user role filter in User management Page$",
        (String userRole) -> {
          softly.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(USER_ROLES_COMBOBOX, userRole),
              "Provided user role is not available in the user role template dropdown menu!");
          softly.assertAll();
        });

    And(
        "^I filter users by \"([^\"]*)\" user role$",
        (String userRole) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(USER_ROLES_COMBOBOX);
          webDriverHelpers.selectFromCombobox(USER_ROLES_COMBOBOX, userRole);
        });

    And(
        "I check that number of displayed users results is {int} in User Management tab",
        (Integer number) -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(
                          webDriverHelpers.getTextFromPresentWebElement(TOTAL_USERS_COUNTER)),
                      number.intValue(),
                      "Number of displayed users is not correct"));
        });

    Then(
        "I check that all row in table has right jurisdiction level for User Roles tab",
        () -> {
          Integer numberOfChosenRows =
              Integer.parseInt(
                      webDriverHelpers.getAttributeFromWebElement(
                          USER_ROLE_TABLE_GRID, "aria-rowcount"))
                  - 1;
          String jurisdictionLevel =
              webDriverHelpers.getValueFromCombobox(JURISDICTION_LEVEL_COMBOBOX);
          Integer jurisdictionLevelRows =
              webDriverHelpers.getNumberOfElements(
                  getJurisdictionLevelCaptionByText(jurisdictionLevel));
          softly.assertEquals(numberOfChosenRows, jurisdictionLevelRows);
          softly.assertAll();
        });

    Then(
        "I get count for all row number for User Roles tab",
        () -> {
          numberOfRows =
              Integer.parseInt(
                  webDriverHelpers.getAttributeFromWebElement(
                      USER_ROLE_TABLE_GRID, "aria-rowcount"));
        });

    Then(
        "I check that current row counter for chosen option is less then row number without filtering",
        () ->
            Assert.assertTrue(
                Integer.parseInt(
                        webDriverHelpers.getAttributeFromWebElement(
                            USER_ROLE_TABLE_GRID, "aria-rowcount"))
                    < numberOfRows,
                "Filter do not hide mismatched values"));

    Then(
        "^I set user role filter to ([^\"]*) in User Roles tab$",
        (String filter) -> {
          webDriverHelpers.selectFromCombobox(USER_RIGHTS_COMBOBOX, filter);
          TimeUnit.SECONDS.sleep(2); // needed for table to refresh
        });

    Then(
        "^I set jurisdiction level filter to ([^\"]*) in User Roles tab$",
        (String filter) -> {
          webDriverHelpers.selectFromCombobox(JURISDICTION_LEVEL_COMBOBOX, filter);
          TimeUnit.SECONDS.sleep(2); // needed for table to refresh
        });

    Then(
        "I export user roles xlsx file",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EXPORT_USER_ROLES);
          TimeUnit.SECONDS.sleep(10);
        });

    Then(
        "^I set enabled filter to ([^\"]*) in User Roles tab$",
        (String filter) -> {
          webDriverHelpers.selectFromCombobox(ENABLED_USER_COMBOBOX, filter);
          TimeUnit.SECONDS.sleep(2); // needed for table to refresh
        });

    When(
        "I check if downloaded xlsx file is correct",
        () -> {
          String file =
              String.format(
                  "./downloads/sormas_benutzerrollen_%s_.xlsx", java.time.LocalDate.now());
          String firstRow = "";
          FileInputStream xlsx = new FileInputStream(file);
          Workbook document = new XSSFWorkbook(xlsx);
          Sheet dataSheet = document.getSheetAt(0);
          Iterator<Row> iterator = dataSheet.iterator();
          Row currentRow = iterator.next();
          Iterator<Cell> cellIterator = currentRow.iterator();
          while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            firstRow = firstRow.concat(currentCell.getStringCellValue() + " ");
          }
          Assert.assertTrue(
              firstRow.contains(
                  "Benutzerrolle Zust\u00E4ndigkeitsebene Beschreibung UUID Einreise Benutzer Hat verkn\u00FCpfter Landkreisbenutzer Hat optionale Gesundheitseinrichtung Aktiviert Benutzerrechte CASE_VIEW CASE_CREATE CASE_EDIT CASE_ARCHIVE CASE_DELETE CASE_IMPORT CASE_EXPORT CASE_INVESTIGATE CASE_CLASSIFY CASE_CHANGE_DISEASE CASE_CHANGE_EPID_NUMBER CASE_TRANSFER CASE_REFER_FROM_POE CASE_MERGE CASE_SHARE CASE_RESPONSIBLE GRANT_SPECIAL_CASE_ACCESS IMMUNIZATION_VIEW IMMUNIZATION_CREATE IMMUNIZATION_EDIT IMMUNIZATION_ARCHIVE IMMUNIZATION_DELETE PERSON_VIEW PERSON_EDIT PERSON_DELETE PERSON_EXPORT PERSON_CONTACT_DETAILS_DELETE PERSON_MERGE SAMPLE_VIEW SAMPLE_CREATE SAMPLE_EDIT SAMPLE_DELETE SAMPLE_EXPORT SAMPLE_TRANSFER SAMPLE_EDIT_NOT_OWNED PATHOGEN_TEST_CREATE PATHOGEN_TEST_EDIT PATHOGEN_TEST_DELETE ADDITIONAL_TEST_VIEW ADDITIONAL_TEST_CREATE ADDITIONAL_TEST_EDIT ADDITIONAL_TEST_DELETE CONTACT_VIEW CONTACT_CREATE CONTACT_EDIT CONTACT_ARCHIVE CONTACT_DELETE CONTACT_IMPORT CONTACT_EXPORT CONTACT_CONVERT CONTACT_REASSIGN_CASE CONTACT_MERGE CONTACT_RESPONSIBLE VISIT_CREATE VISIT_EDIT VISIT_DELETE VISIT_EXPORT TASK_VIEW TASK_CREATE TASK_EDIT TASK_DELETE TASK_EXPORT TASK_ASSIGN TASK_ARCHIVE ACTION_CREATE ACTION_DELETE ACTION_EDIT EVENT_VIEW EVENT_CREATE EVENT_EDIT EVENT_ARCHIVE EVENT_DELETE EVENT_IMPORT EVENT_EXPORT EVENT_RESPONSIBLE EVENTPARTICIPANT_VIEW EVENTPARTICIPANT_CREATE EVENTPARTICIPANT_EDIT EVENTPARTICIPANT_ARCHIVE EVENTPARTICIPANT_DELETE EVENTPARTICIPANT_IMPORT EVENTGROUP_CREATE EVENTGROUP_EDIT EVENTGROUP_ARCHIVE EVENTGROUP_DELETE EVENTGROUP_LINK USER_VIEW USER_CREATE USER_EDIT USER_ROLE_VIEW USER_ROLE_EDIT USER_ROLE_DELETE STATISTICS_ACCESS STATISTICS_EXPORT INFRASTRUCTURE_VIEW INFRASTRUCTURE_CREATE INFRASTRUCTURE_EDIT INFRASTRUCTURE_ARCHIVE INFRASTRUCTURE_IMPORT INFRASTRUCTURE_EXPORT POPULATION_MANAGE DASHBOARD_SURVEILLANCE_VIEW DASHBOARD_CONTACT_VIEW DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS DASHBOARD_CAMPAIGNS_VIEW DASHBOARD_SAMPLES_VIEW CASE_CLINICIAN_VIEW THERAPY_VIEW PRESCRIPTION_CREATE PRESCRIPTION_EDIT PRESCRIPTION_DELETE TREATMENT_CREATE TREATMENT_EDIT TREATMENT_DELETE CLINICAL_COURSE_VIEW CLINICAL_COURSE_EDIT CLINICAL_VISIT_CREATE CLINICAL_VISIT_EDIT CLINICAL_VISIT_DELETE PORT_HEALTH_INFO_VIEW PORT_HEALTH_INFO_EDIT WEEKLYREPORT_VIEW WEEKLYREPORT_CREATE AGGREGATE_REPORT_VIEW AGGREGATE_REPORT_EDIT AGGREGATE_REPORT_EXPORT SEE_PERSONAL_DATA_IN_JURISDICTION SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION SEE_SENSITIVE_DATA_IN_JURISDICTION SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION CAMPAIGN_VIEW CAMPAIGN_EDIT CAMPAIGN_ARCHIVE CAMPAIGN_DELETE CAMPAIGN_FORM_DATA_VIEW CAMPAIGN_FORM_DATA_EDIT CAMPAIGN_FORM_DATA_ARCHIVE CAMPAIGN_FORM_DATA_DELETE CAMPAIGN_FORM_DATA_EXPORT TRAVEL_ENTRY_MANAGEMENT_ACCESS TRAVEL_ENTRY_VIEW TRAVEL_ENTRY_CREATE TRAVEL_ENTRY_EDIT TRAVEL_ENTRY_ARCHIVE TRAVEL_ENTRY_DELETE ENVIRONMENT_VIEW ENVIRONMENT_CREATE ENVIRONMENT_EDIT ENVIRONMENT_ARCHIVE ENVIRONMENT_DELETE ENVIRONMENT_IMPORT ENVIRONMENT_EXPORT ENVIRONMENT_SAMPLE_VIEW ENVIRONMENT_SAMPLE_CREATE ENVIRONMENT_SAMPLE_EDIT ENVIRONMENT_SAMPLE_EDIT_DISPATCH ENVIRONMENT_SAMPLE_EDIT_RECEIVAL ENVIRONMENT_SAMPLE_DELETE ENVIRONMENT_SAMPLE_IMPORT ENVIRONMENT_SAMPLE_EXPORT ENVIRONMENT_PATHOGEN_TEST_CREATE ENVIRONMENT_PATHOGEN_TEST_EDIT ENVIRONMENT_PATHOGEN_TEST_DELETE DOCUMENT_VIEW DOCUMENT_UPLOAD DOCUMENT_DELETE PERFORM_BULK_OPERATIONS PERFORM_BULK_OPERATIONS_PSEUDONYM QUARANTINE_ORDER_CREATE SORMAS_REST SORMAS_UI DATABASE_EXPORT_ACCESS EXPORT_DATA_PROTECTION_DATA BAG_EXPORT SEND_MANUAL_EXTERNAL_MESSAGES MANAGE_EXTERNAL_SYMPTOM_JOURNAL EXTERNAL_VISITS SORMAS_TO_SORMAS_CLIENT SORMAS_TO_SORMAS_SHARE SORMAS_TO_SORMAS_PROCESS EXTERNAL_SURVEILLANCE_SHARE EXTERNAL_SURVEILLANCE_DELETE EXTERNAL_MESSAGE_VIEW EXTERNAL_MESSAGE_PROCESS EXTERNAL_MESSAGE_PUSH EXTERNAL_MESSAGE_DELETE OUTBREAK_VIEW OUTBREAK_EDIT MANAGE_PUBLIC_EXPORT_CONFIGURATION DOCUMENT_TEMPLATE_MANAGEMENT LINE_LISTING_CONFIGURE DEV_MODE EMAIL_TEMPLATE_MANAGEMENT EXTERNAL_EMAIL_SEND EXTERNAL_EMAIL_ATTACH_DOCUMENTS CUSTOMIZABLE_ENUM_MANAGEMENT Benachrichtigungen CASE_CLASSIFICATION_CHANGED CASE_INVESTIGATION_DONE CASE_LAB_RESULT_ARRIVED CASE_DISEASE_CHANGED CONTACT_LAB_RESULT_ARRIVED CONTACT_SYMPTOMATIC CONTACT_VISIT_COMPLETED EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS EVENT_PARTICIPANT_LAB_RESULT_ARRIVED EVENT_GROUP_CREATED EVENT_ADDED_TO_EVENT_GROUP EVENT_REMOVED_FROM_EVENT_GROUP LAB_SAMPLE_SHIPPED TASK_START TASK_DUE TASK_UPDATED_ASSIGNEE "));
          document.close();
          Files.delete(Paths.get(file));
        });

    And(
        "^I check if there is any user with the \"([^\"]*)\" role and change his role$",
        (String userRole) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SEARCH_USER_INPUT);

          if (webDriverHelpers.checkIfElementExistsInCombobox(USER_ROLES_COMBOBOX, userRole)) {
            if (webDriverHelpers.isElementPresent(UserManagementPage.RESULT_IN_GRID)) {
              webDriverHelpers.selectFromCombobox(USER_ROLE_COMBOBOX, userRole);
              while (webDriverHelpers.getNumberOfElements(UserManagementPage.RESULT_IN_GRID) > 0) {
                webDriverHelpers.waitUntilIdentifiedElementIsPresent(
                    UserManagementPage.getEditButtonByIndex(1));
                webDriverHelpers.doubleClickOnWebElementBySelector(
                    UserManagementPage.getEditButtonByIndex(1));
                webDriverHelpers.scrollToElement(USER_ROLE_CHECKBOX);
                webDriverHelpers.clickWebElementByText(USER_ROLE_CHECKBOX, userRole);
                webDriverHelpers.clickWebElementByText(USER_ROLE_CHECKBOX, "National User");
                webDriverHelpers.scrollToElementUntilIsVisible(SAVE_BUTTON_EDIT_USER);
                webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_EDIT_USER);
                TimeUnit.SECONDS.sleep(2);
              }
            }
          }
        });
  }

  private void searchForUser(String userName) {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SEARCH_USER_INPUT);
    webDriverHelpers.fillAndSubmitInWebElement(SEARCH_USER_INPUT, userName);
  }

  private void selectFirstElementFromList() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(FIRST_EDIT_BUTTON_FROM_LIST);
    webDriverHelpers.clickOnWebElementBySelector(FIRST_EDIT_BUTTON_FROM_LIST);
  }
}
