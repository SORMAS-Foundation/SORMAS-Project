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
import static org.sormas.e2etests.pages.application.users.UserManagementPage.*;

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
import org.testng.Assert;

public class UserManagementSteps implements En {
  public static int numberOfUsers;
  public static int numberOfRows;
  protected WebDriverHelpers webDriverHelpers;

  @Inject
  public UserManagementSteps(WebDriverHelpers webDriverHelpers, AssertHelpers assertHelpers) {
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
        "^I set enabled filter to ([^\"]*) in User Roles tab$",
        (String filter) -> {
          webDriverHelpers.selectFromCombobox(ENABLED_USER_COMBOBOX, filter);
          TimeUnit.SECONDS.sleep(2); // needed for table to refresh
        });

    When("^I select first user from list$", () -> selectFirstElementFromList());

    When(
        "^I search for created user$",
        () -> {
          searchForUser(CreateNewUserSteps.user.getUserName());
          selectFirstElementFromList();
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
        () ->
            assertHelpers.assertWithPoll(
                () ->
                    Assert.assertTrue(
                        webDriverHelpers.isElementVisibleWithTimeout(SYNC_SUCCESS_DE, 5),
                        "Sync of users failed"),
                10));
    When(
        "I go to USER ROLES tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(USER_ROLES_TAB));

    Then(
        "I export user roles xlsx file",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EXPORT_USER_ROLES);
          TimeUnit.SECONDS.sleep(10);
        });

    When(
        "I check if downloaded xlsx file is correct",
        () -> {
          String file =
              String.format(
                  "./downloads/sormas_benutzerrollen_%s_.xlsx", java.time.LocalDate.now());
          System.out.println(java.time.LocalDate.now());
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
          System.out.println(firstRow);
          Assert.assertTrue(
              firstRow.contains(
                  "Benutzerrolle Zust\u00E4ndigkeitsebene Beschreibung CASE_VIEW CASE_CREATE CASE_EDIT CASE_ARCHIVE CASE_DELETE CASE_IMPORT CASE_EXPORT CASE_INVESTIGATE CASE_CLASSIFY CASE_CHANGE_DISEASE CASE_CHANGE_EPID_NUMBER CASE_TRANSFER CASE_REFER_FROM_POE CASE_MERGE CASE_SHARE CASE_RESPONSIBLE IMMUNIZATION_VIEW IMMUNIZATION_CREATE IMMUNIZATION_EDIT IMMUNIZATION_ARCHIVE IMMUNIZATION_DELETE PERSON_VIEW PERSON_EDIT PERSON_DELETE PERSON_EXPORT PERSON_CONTACT_DETAILS_DELETE SAMPLE_VIEW SAMPLE_CREATE SAMPLE_EDIT SAMPLE_DELETE SAMPLE_EXPORT SAMPLE_TRANSFER SAMPLE_EDIT_NOT_OWNED PERFORM_BULK_OPERATIONS_CASE_SAMPLES PATHOGEN_TEST_CREATE PATHOGEN_TEST_EDIT PATHOGEN_TEST_DELETE ADDITIONAL_TEST_VIEW ADDITIONAL_TEST_CREATE ADDITIONAL_TEST_EDIT ADDITIONAL_TEST_DELETE CONTACT_VIEW CONTACT_CREATE CONTACT_EDIT CONTACT_ARCHIVE CONTACT_DELETE CONTACT_IMPORT CONTACT_EXPORT CONTACT_CONVERT CONTACT_REASSIGN_CASE CONTACT_MERGE CONTACT_RESPONSIBLE VISIT_CREATE VISIT_EDIT VISIT_DELETE VISIT_EXPORT TASK_VIEW TASK_CREATE TASK_EDIT TASK_DELETE TASK_EXPORT TASK_ASSIGN ACTION_CREATE ACTION_DELETE ACTION_EDIT EVENT_VIEW EVENT_CREATE EVENT_EDIT EVENT_ARCHIVE EVENT_DELETE EVENT_IMPORT EVENT_EXPORT PERFORM_BULK_OPERATIONS_EVENT EVENT_RESPONSIBLE EVENTPARTICIPANT_VIEW EVENTPARTICIPANT_CREATE EVENTPARTICIPANT_EDIT EVENTPARTICIPANT_ARCHIVE EVENTPARTICIPANT_DELETE EVENTPARTICIPANT_IMPORT PERFORM_BULK_OPERATIONS_EVENTPARTICIPANT EVENTGROUP_CREATE EVENTGROUP_EDIT EVENTGROUP_ARCHIVE EVENTGROUP_DELETE EVENTGROUP_LINK USER_VIEW USER_CREATE USER_EDIT USER_ROLE_VIEW USER_ROLE_EDIT USER_ROLE_DELETE STATISTICS_ACCESS STATISTICS_EXPORT INFRASTRUCTURE_VIEW INFRASTRUCTURE_CREATE INFRASTRUCTURE_EDIT INFRASTRUCTURE_ARCHIVE INFRASTRUCTURE_IMPORT INFRASTRUCTURE_EXPORT POPULATION_MANAGE DASHBOARD_SURVEILLANCE_VIEW DASHBOARD_CONTACT_VIEW DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS DASHBOARD_CAMPAIGNS_VIEW CASE_CLINICIAN_VIEW THERAPY_VIEW PRESCRIPTION_CREATE PRESCRIPTION_EDIT PRESCRIPTION_DELETE TREATMENT_CREATE TREATMENT_EDIT TREATMENT_DELETE CLINICAL_COURSE_VIEW CLINICAL_COURSE_EDIT CLINICAL_VISIT_CREATE CLINICAL_VISIT_EDIT CLINICAL_VISIT_DELETE PORT_HEALTH_INFO_VIEW PORT_HEALTH_INFO_EDIT WEEKLYREPORT_VIEW WEEKLYREPORT_CREATE AGGREGATE_REPORT_VIEW AGGREGATE_REPORT_EDIT AGGREGATE_REPORT_EXPORT SEE_PERSONAL_DATA_IN_JURISDICTION SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION SEE_SENSITIVE_DATA_IN_JURISDICTION SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION CAMPAIGN_VIEW CAMPAIGN_EDIT CAMPAIGN_ARCHIVE CAMPAIGN_DELETE CAMPAIGN_FORM_DATA_VIEW CAMPAIGN_FORM_DATA_EDIT CAMPAIGN_FORM_DATA_ARCHIVE CAMPAIGN_FORM_DATA_DELETE CAMPAIGN_FORM_DATA_EXPORT TRAVEL_ENTRY_MANAGEMENT_ACCESS TRAVEL_ENTRY_VIEW TRAVEL_ENTRY_CREATE TRAVEL_ENTRY_EDIT TRAVEL_ENTRY_ARCHIVE TRAVEL_ENTRY_DELETE DOCUMENT_VIEW DOCUMENT_UPLOAD DOCUMENT_DELETE PERFORM_BULK_OPERATIONS PERFORM_BULK_OPERATIONS_PSEUDONYM QUARANTINE_ORDER_CREATE SORMAS_REST SORMAS_UI DATABASE_EXPORT_ACCESS EXPORT_DATA_PROTECTION_DATA BAG_EXPORT SEND_MANUAL_EXTERNAL_MESSAGES MANAGE_EXTERNAL_SYMPTOM_JOURNAL EXTERNAL_VISITS SORMAS_TO_SORMAS_CLIENT SORMAS_TO_SORMAS_SHARE SORMAS_TO_SORMAS_PROCESS EXTERNAL_MESSAGE_VIEW EXTERNAL_MESSAGE_PROCESS EXTERNAL_MESSAGE_DELETE PERFORM_BULK_OPERATIONS_EXTERNAL_MESSAGES OUTBREAK_VIEW OUTBREAK_EDIT MANAGE_PUBLIC_EXPORT_CONFIGURATION DOCUMENT_TEMPLATE_MANAGEMENT LINE_LISTING_CONFIGURE DEV_MODE UUID Einreise Benutzer Hat verkn\u00FCpfter Landkreisbenutzer Hat optionale Gesundheitseinrichtung Aktiviert"));
          Files.delete(Paths.get(file));
        });

    Then(
        "I get row count from User Roles tab",
        () ->
            numberOfRows =
                Integer.parseInt(
                    webDriverHelpers.getAttributeFromWebElement(
                        USER_ROLE_TABLE_GRID, "aria-rowcount")));
    Then(
        "I compare that actual row coutner is less than first one",
        () ->
            Assert.assertTrue(
                Integer.parseInt(
                        webDriverHelpers.getAttributeFromWebElement(
                            USER_ROLE_TABLE_GRID, "aria-rowcount"))
                    < numberOfRows));
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
