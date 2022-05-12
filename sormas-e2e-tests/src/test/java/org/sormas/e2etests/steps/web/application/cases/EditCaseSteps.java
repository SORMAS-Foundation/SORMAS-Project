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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.enums.CaseOutcome.DECEASED;
import static org.sormas.e2etests.enums.CaseOutcome.FACILITY_OTHER;
import static org.sormas.e2etests.enums.CaseOutcome.INVESTIGATION_DISCARDED;
import static org.sormas.e2etests.enums.CaseOutcome.INVESTIGATION_DONE;
import static org.sormas.e2etests.enums.CaseOutcome.INVESTIGATION_PENDING;
import static org.sormas.e2etests.enums.CaseOutcome.PLACE_OF_STAY_FACILITY;
import static org.sormas.e2etests.enums.CaseOutcome.PLACE_OF_STAY_HOME;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_HOME;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_INSTITUTIONAL;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_NONE;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_OTHER;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_UNKNOWN;
import static org.sormas.e2etests.enums.CaseOutcome.RECOVERED;
import static org.sormas.e2etests.enums.CaseOutcome.SEQUELAE_NO;
import static org.sormas.e2etests.enums.CaseOutcome.SEQUELAE_UNKNOWN;
import static org.sormas.e2etests.enums.CaseOutcome.SEQUELAE_YES;
import static org.sormas.e2etests.enums.CaseOutcome.UNKNOWN;
import static org.sormas.e2etests.enums.CaseOutcome.VACCINATED_STATUS_UNKNOWN;
import static org.sormas.e2etests.enums.CaseOutcome.VACCINATED_STATUS_UNVACCINATED;
import static org.sormas.e2etests.enums.CaseOutcome.VACCINATED_STATUS_VACCINATED;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_CLASSIFICATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_CLOSE_WINDOW_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_INFO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONTACTS_DATA_TAB;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.EPIDEMIOLOGICAL_DATA_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CANCEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BLOOD_ORGAN_TISSUE_DONATION_IN_THE_LAST_6_MONTHS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_CLASSIFICATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_IDENTIFICATION_SOURCE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_PERSON_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CLINICAL_CONFIRMATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CLINICAL_COURSE_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_COMBOBOX_BY_PLACE_OF_STAY;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_TEMPLATES;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_QUARANTINE_ORDER_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CURRENT_HOSPITALIZATION_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OFFICIAL_QUARANTINE_ORDER_WAS_SENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OF_OUTCOME;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OF_OUTCOME_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OF_THE_VERBAL_ORDER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_RECEIVED_AT_DISTRICT_LEVEL_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_RECEIVED_AT_NATIONAL_LEVEL_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_RECEIVED_AT_REGION_LEVEL_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISCARD_BUTTON_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_VARIANT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_TRAVEL_ENTRY_FROM_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTERNAL_TOKEN_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTRA_COMMENT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_HEALTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FOLLOW_UP_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERAL_COMMENT_TEXTAREA;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERATED_DOCUMENT_NAME;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.HOME_BASED_QUARANTINE_POSSIBLE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.HOSPITALIZATION_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.INVESTIGATED_DATE_FIELD;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.INVESTIGATION_STATUS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.LABORATORY_DIAGNOSTIC_CONFIRMATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_SAMPLE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_TRAVEL_ENTRY_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.OUTCOME_OF_CASE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_CHECKBOX_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_CHECKBOX_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_SELECTED_VALUE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.POPUPS_INPUTS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PROHIBITION_TO_WORK_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_CHANGE_COMMENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_DATE_FROM;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_DATE_FROM_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_DATE_TO;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_DATE_TO_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_BY_DOCUMENT_DATE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_VERBALLY_CHECKBOX_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_VERBALLY_CHECKBOX_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_POPUP_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_POPUP_MESSAGE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_POPUP_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_TYPE_DETAILS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REFERENCE_DEFINITION_TEXT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REINFECTION_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_GPS_ACCURACY_IN_M_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_GPS_LATITUDE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_GPS_LONGITUDE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_SURVEILLANCE_OFFICER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_AND_OPEN_HOSPITALIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SEQUELAE_DETAILS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SEQUELAE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SYMPTOMS_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UPLOAD_DOCUMENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACT_TO_BODY_FLUIDS_OPTONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACT_TO_CASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.COUNTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.END_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_ROLE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.HANDLING_SAMPLES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.INDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.LONG_FACE_TO_FACE_CONTACT_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OPEN_SAVED_EXPOSURE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OTHER_PROTECTIVE_MEASURES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OUTDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.PERCUTANEOUS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RISK_AREA_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SHORT_DISTANCE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.START_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SUBCONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_PLACE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_MASK_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_PPE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.SymptomsTabPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_WINDOW_CONTACT_DE;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.FOLLOW_UP_UNTIL_DATE;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_FIRST_RESULT_OPTION;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EVENT_PARTICIPANTS_DATA_TAB;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.contacts.ContactDirectorySteps.exposureData;

import cucumber.api.java8.En;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.QuarantineOrder;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.entities.services.CaseDocumentService;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.enums.CaseClassification;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class EditCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Case aCase;
  private static Case createdCase;
  private static Case editedCase;
  public static QuarantineOrder aQuarantineOrder;
  private static Case specificCaseData;
  private static LocalDate dateFollowUp;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  public static final String userDirPath = System.getProperty("user.dir");

  @SneakyThrows
  @Inject
  public EditCaseSteps(
      WebDriverHelpers webDriverHelpers,
      CaseService caseService,
      CaseDocumentService caseDocumentService,
      SoftAssert softly,
      AssertHelpers assertHelpers,
      ApiState apiState,
      DataOperations dataOperations,
      RunningConfiguration runningConfiguration) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I fill the Date of outcome to yesterday",
        () -> {
          webDriverHelpers.fillInWebElement(
              DATE_OF_OUTCOME_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(1)));
        });

    When(
        "I select ([^\"]*) as Outcome Of Case Status",
        (String caseStatus) -> {
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, CaseOutcome.getValueFor(caseStatus).toUpperCase());
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I fill the specific Date of outcome",
        () -> {
          webDriverHelpers.fillInWebElement(
              DATE_OF_OUTCOME_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(1)));
        });

    And(
        "I click on save button from Edit Case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "I click only on save button from Edit Case page",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON));

    And(
        "I check if Current Hospitalization popup is displayed",
        () -> webDriverHelpers.isElementVisibleWithTimeout(CURRENT_HOSPITALIZATION_POPUP, 10));

    When(
        "I click on Save and open hospitalization in current hospitalization popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_AND_OPEN_HOSPITALIZATION_BUTTON));

    Then(
        "I click on Clinical Course tab from Edit Case page",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLINICAL_COURSE_TAB));

    When(
        "I click on save button from Edit Case page with current hospitalization",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on INFO button on Case Edit page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_INFO_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CASE_CLOSE_WINDOW_BUTTON);
        });

    When(
        "I change Epidemiological confirmation Combobox to {string} option",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX, option);
        });

    When(
        "I check that Case Classification has {string} value",
        (String caseClassificationValue) -> {
          String caseClassificationComboboxValue =
              (webDriverHelpers.getValueFromCombobox(CASE_CLASSIFICATION_COMBOBOX));
          softly.assertEquals(
              caseClassificationValue,
              caseClassificationComboboxValue,
              "The case classification field has unexpected value ");
          softly.assertAll();
        });

    And(
        "I navigate to follow-up tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(FOLLOW_UP_TAB));

    And(
        "I navigate to symptoms tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(SYMPTOMS_TAB));

    When(
        "I navigate to Hospitalization tab in Cases",
        () -> webDriverHelpers.clickOnWebElementBySelector(HOSPITALIZATION_TAB));

    And(
        "I navigate to case person tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(CASE_PERSON_TAB));
    And(
        "I click on Create button in Document Templates box in Edit Case directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_TEMPLATES));
    And(
        "I click on checkbox to upload generated document to entity in Create Quarantine Order form in Edit Case directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(UPLOAD_DOCUMENT_CHECKBOX));
    When(
        "I select {string} Quarantine Order in Create Quarantine Order form in Edit Case directory",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
          webDriverHelpers.waitUntilANumberOfElementsAreVisibleAndClickable(POPUPS_INPUTS, 5);
        });
    When(
        "I select {string} Quarantine Order in Create Quarantine Order form in Case directory",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
        });
    When(
        "I check if downloaded file is correct for {string} Quarantine Order in Edit Case directory",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          Path path =
              Paths.get(
                  userDirPath + "/downloads/" + uuid.substring(0, 6).toUpperCase() + "-" + name);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertTrue(
                      Files.exists(path),
                      "Quarantine order document was not downloaded. Path used for check: "
                          + path.toAbsolutePath()));
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab for API created case in Edit Case directory",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME)),
              120);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab for UI created case in Edit Case directory",
        (String name) -> {
          String uuid = EditCaseSteps.aCase.getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME)),
              120);
        });
    When(
        "I delete downloaded file created from {string} Document Template",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          File toDelete =
              new File(
                  userDirPath + "/downloads/" + uuid.substring(0, 6).toUpperCase() + "-" + name);
          toDelete.deleteOnExit();
        });
    And(
        "I click on Create button in Create Quarantine Order form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_QUARANTINE_ORDER_BUTTON);
        });

    When(
        "I check the created data is correctly displayed on Edit case page",
        () -> {
          aCase = collectCasePersonData();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    When(
        "I check the created data for existing person is correctly displayed on Edit case page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
          aCase = collectCasePersonDataForExistingPerson();
          createdCase =
              CreateNewCaseSteps.caze.toBuilder()
                  .firstName(apiState.getLastCreatedPerson().getFirstName())
                  .lastName(apiState.getLastCreatedPerson().getLastName())
                  .dateOfBirth(
                      LocalDate.of(
                          apiState.getLastCreatedPerson().getBirthdateYYYY(),
                          apiState.getLastCreatedPerson().getBirthdateMM(),
                          apiState.getLastCreatedPerson().getBirthdateDD()))
                  .build();

          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    When(
        "I check the created data is correctly displayed on Edit case page for DE version",
        () -> {
          aCase = collectCasePersonDataDE();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    When(
        "I select Investigation Status ([^\"]*)",
        (String investigationStatus) -> {
          webDriverHelpers.clickWebElementByText(
              INVESTIGATION_STATUS_OPTIONS,
              CaseOutcome.getValueFor("INVESTIGATION " + investigationStatus).toUpperCase());
          editedCase =
              Case.builder()
                  .investigationStatus("Investigation " + investigationStatus)
                  .build(); // TODO: Create POJO updater class
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I select German Investigation Status ([^\"]*)",
        (String option) -> {
          String investigationStatus = new String();
          switch (option) {
            case "Done":
              investigationStatus = INVESTIGATION_DONE.getNameDE();
              break;
            case "Pending":
              investigationStatus = INVESTIGATION_PENDING.getNameDE();
              break;
            case "Discarded":
              investigationStatus = INVESTIGATION_DISCARDED.getNameDE();
              break;
          }
          webDriverHelpers.clickWebElementByText(
              INVESTIGATION_STATUS_OPTIONS, investigationStatus.toUpperCase());
          editedCase =
              Case.builder()
                  .investigationStatus(investigationStatus)
                  .build(); // TODO: Create POJO updater class
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if date of investigation filed is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(INVESTIGATED_DATE_FIELD));

    When(
        "I select Outcome Of Case Status ([^\"]*)",
        (String caseStatus) -> {
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, CaseOutcome.getValueFor(caseStatus).toUpperCase());
          editedCase = editedCase.toBuilder().outcomeOfCase(caseStatus).build();
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I select German Outcome Of Case Status ([^\"]*)",
        (String option) -> {
          String outcomeOfCaseStatus = new String();
          switch (option) {
            case "Deceased":
              outcomeOfCaseStatus = DECEASED.getNameDE();
              break;
            case "Recovered":
              outcomeOfCaseStatus = RECOVERED.getNameDE();
              break;
            case "Unknown":
              outcomeOfCaseStatus = UNKNOWN.getNameDE();
              break;
          }
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, outcomeOfCaseStatus.toUpperCase());
          editedCase = editedCase.toBuilder().outcomeOfCase(outcomeOfCaseStatus).build();
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I check if date of outcome filed is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_OUTCOME));

    When(
        "I click on ([^\"]*) option in Sequelae",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(
              SEQUELAE_OPTIONS, CaseOutcome.getValueFor(option).toUpperCase());
          editedCase = editedCase.toBuilder().sequelae(option).build();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the German option for ([^\"]*) in Sequelae",
        (String option) -> {
          String sequelaeStatus = new String();
          switch (option) {
            case "Yes":
              sequelaeStatus = SEQUELAE_YES.getNameDE();
              break;
            case "No":
              sequelaeStatus = SEQUELAE_NO.getNameDE();
              break;
            case "Unknown":
              sequelaeStatus = SEQUELAE_UNKNOWN.getNameDE();
              break;
          }
          webDriverHelpers.clickWebElementByText(SEQUELAE_OPTIONS, sequelaeStatus.toUpperCase());
          editedCase = editedCase.toBuilder().sequelae(sequelaeStatus).build();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if Sequelae Details field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEQUELAE_DETAILS));

    When(
        "I click on Place of stay of this case differs from its responsible jurisdiction",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PLACE_OF_STAY_CHECKBOX_LABEL);
          editedCase =
              editedCase.toBuilder()
                  .differentPlaceOfStayJurisdiction(
                      webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(
                          PLACE_OF_STAY_CHECKBOX_INPUT))
                  .build();
        });

    When(
        "I check if region combobox is available and I select Responsible Region",
        () -> {
          aCase = caseService.buildEditGeneratedCase();
          webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX, aCase.getRegion());
          editedCase = editedCase.toBuilder().region(aCase.getRegion()).build();
        });

    When(
        "I check if district combobox is available and i select Responsible District",
        () -> {
          aCase = caseService.buildEditGeneratedCase();
          webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX, aCase.getDistrict());
          editedCase = editedCase.toBuilder().district(aCase.getDistrict()).build();
        });

    When(
        "I check if community combobox is available",
        () ->
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                COMMUNITY_COMBOBOX_BY_PLACE_OF_STAY));

    When(
        "I click on ([^\"]*) as place of stay",
        (String placeOfStay) -> {
          webDriverHelpers.clickWebElementByText(
              PLACE_OF_STAY_OPTIONS, CaseOutcome.getValueFor(placeOfStay).toUpperCase());
          editedCase = editedCase.toBuilder().placeOfStay(placeOfStay).build();
        });

    When(
        "I click on ([^\"]*) as place of stay in Case Edit tab",
        (String placeOfStay) -> {
          webDriverHelpers.clickWebElementByText(
              PLACE_OF_STAY_OPTIONS, CaseOutcome.getValueFor(placeOfStay).toUpperCase());
        });

    When(
        "I click on ([^\"]*) as German place of stay",
        (String option) -> {
          String placeOfStay = new String();
          switch (option) {
            case "Facility":
              placeOfStay = PLACE_OF_STAY_FACILITY.getNameDE();
              break;
            case "Home":
              placeOfStay = PLACE_OF_STAY_HOME.getNameDE();
              break;
          }
          webDriverHelpers.clickWebElementByText(PLACE_OF_STAY_OPTIONS, placeOfStay.toUpperCase());
          editedCase = editedCase.toBuilder().placeOfStay(placeOfStay).build();
        });

    When(
        "I check if Facility Category combobox is available",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_CATEGORY_COMBOBOX);
          editedCase =
              editedCase.toBuilder()
                  .facilityCategory(
                      webDriverHelpers.getValueFromCombobox(FACILITY_CATEGORY_COMBOBOX))
                  .build();
        });

    When(
        "I check if Facility Type combobox is available",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_TYPE_COMBOBOX);
          editedCase =
              editedCase.toBuilder()
                  .facilityType(webDriverHelpers.getValueFromCombobox(FACILITY_TYPE_COMBOBOX))
                  .build();
        });

    When(
        "I set Facility as a ([^\"]*)",
        (String facility) -> {
          webDriverHelpers.selectFromCombobox(
              FACILITY_HEALTH_COMBOBOX, CaseOutcome.getValueFor(facility));
          editedCase = editedCase.toBuilder().facility(facility).build();
        });

    When(
        "In Case Edit tab I set Facility as a ([^\"]*)",
        (String facility) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_TYPE_COMBOBOX);
          webDriverHelpers.selectFromCombobox(FACILITY_HEALTH_COMBOBOX, facility);
        });

    When(
        "I set Facility in German as a ([^\"]*)",
        (String option) -> {
          String facility = new String();
          switch (option) {
            case "Other facility":
              facility = FACILITY_OTHER.getNameDE();
              break;
          }
          webDriverHelpers.selectFromCombobox(FACILITY_HEALTH_COMBOBOX, facility);
          editedCase = editedCase.toBuilder().facility(facility).build();
        });

    When(
        "I set Facility to {string} from New Entry popup",
        (String facility) -> {
          webDriverHelpers.selectFromCombobox(FACILITY_ACTIVITY_COMBOBOX, facility);
        });
    When(
        "And I click on Discard button from New Entry popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON_POPUP);
        });

    When(
        "I set Facility Type to {string} from New Entry popup",
        (String facilityType) -> {
          webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
        });

    When(
        "I fill Facility name and description filed by ([^\"]*)",
        (String description) -> {
          webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, description);
          editedCase = editedCase.toBuilder().facilityNameAndDescription(description).build();
        });

    When(
        "I check if Facility name and description field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(PLACE_DESCRIPTION_INPUT));

    When(
        "I set Quarantine ([^\"]*)",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, CaseOutcome.getValueFor(option));
          editedCase = editedCase.toBuilder().quarantine(option).build();
        });

    When(
        "I set German Quarantine ([^\"]*)",
        (String option) -> {
          String quarantine = new String();
          switch (option) {
            case "Home":
              quarantine = QUARANTINE_HOME.getNameDE();
              break;
            case "Institutional":
              quarantine = QUARANTINE_INSTITUTIONAL.getNameDE();
              break;
            case "None":
              quarantine = QUARANTINE_NONE.getNameDE();
              break;
            case "Unknown":
              quarantine = QUARANTINE_UNKNOWN.getNameDE();
              break;
            case "Other":
              quarantine = QUARANTINE_OTHER.getNameDE();
              break;
          }
          webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, quarantine);
          editedCase = editedCase.toBuilder().quarantine(quarantine).build();
        });

    When(
        "I set place for Quarantine as ([^\"]*)",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, CaseOutcome.getValueFor(option));
        });

    When(
        "I set Start date of Quarantine ([^\"]*) days ago",
        (Integer days) -> {
          webDriverHelpers.scrollToElement(QUARANTINE_DATE_TO_INPUT);
          webDriverHelpers.fillInWebElement(
              QUARANTINE_DATE_FROM_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(days)));
        });

    When(
        "I set End date of Quarantine to ([^\"]*) days",
        (Integer days) -> {
          webDriverHelpers.scrollToElement(QUARANTINE_DATE_TO_INPUT);
          webDriverHelpers.fillInWebElement(
              QUARANTINE_DATE_TO_INPUT, DATE_FORMATTER.format(LocalDate.now().plusDays(days)));
        });

    When(
        "I check if ([^\"]*) quarantine popup is displayed",
        (String option) -> {
          String quarantineText;
          String expectedTextReduce = "Are you sure you want to reduce the quarantine?";
          String expectedTextExtend = "Are you sure you want to extend the quarantine?";
          webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_ORDERED_VERBALLY_CHECKBOX_LABEL);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(QUARANTINE_POPUP_MESSAGE);
          quarantineText = webDriverHelpers.getTextFromWebElement(QUARANTINE_POPUP_MESSAGE);
          if (option.equals("Reduce")) softly.assertEquals(quarantineText, expectedTextReduce);
          else if (option.equals("Extend")) softly.assertEquals(quarantineText, expectedTextExtend);
          softly.assertAll();
        });

    When(
        "I check if Quarantine End date stayed reduce to ([^\"]*) days",
        (Integer days) -> {
          String date = webDriverHelpers.getValueFromWebElement(QUARANTINE_DATE_TO_INPUT);
          LocalDate endDate = LocalDate.now().plusDays(days);
          softly.assertEquals(DATE_FORMATTER.format(endDate), date);
          softly.assertAll();
        });
    When(
        "I check if Quarantine Follow up until date was extended to ([^\"]*) day",
        (Integer days) -> {
          String date = webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE);
          softly.assertEquals(DATE_FORMATTER.format(dateFollowUp.plusDays(days)), date);
          softly.assertAll();
        });

    When(
        "I set the quarantine end to a date ([^\"]*) day after the Follow-up until date",
        (Integer days) -> {
          dateFollowUp =
              LocalDate.parse(
                  webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE), DATE_FORMATTER);
          webDriverHelpers.scrollToElement(QUARANTINE_DATE_TO_INPUT);
          webDriverHelpers.fillInWebElement(
              QUARANTINE_DATE_TO_INPUT, DATE_FORMATTER.format(dateFollowUp.plusDays(days)));
          webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_DATE_FROM_INPUT);
        });

    When(
        "I fill Quarantine change comment field",
        () -> {
          webDriverHelpers.scrollToElement(QUARANTINE_CHANGE_COMMENT);
          webDriverHelpers.fillInWebElement(QUARANTINE_CHANGE_COMMENT, dateFollowUp.toString());
        });

    When(
        "I check if Quarantine change comment field was saved correctly",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          String commentText = webDriverHelpers.getValueFromWebElement(QUARANTINE_CHANGE_COMMENT);
          softly.assertEquals(commentText, dateFollowUp.toString());
          softly.assertAll();
        });

    When(
        "I click on yes quarantine popup button",
        () -> webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_POPUP_SAVE_BUTTON));

    When(
        "I click on yes Extend follow up period popup button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_POPUP_SAVE_BUTTON);
        });

    When(
        "I discard changes in quarantine popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_POPUP_DISCARD_BUTTON));

    When(
        "I check if Quarantine start field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(QUARANTINE_DATE_FROM));

    When(
        "I check if Quarantine end field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(QUARANTINE_DATE_TO));

    When(
        "I select Quarantine ordered verbally checkbox",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_ORDERED_VERBALLY_CHECKBOX_LABEL);
          editedCase =
              editedCase.toBuilder()
                  .quarantineOrderedVerbally(
                      webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(
                          QUARANTINE_ORDERED_VERBALLY_CHECKBOX_INPUT))
                  .build();
        });

    When(
        "I check if Date of verbal order field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_THE_VERBAL_ORDER));

    When(
        "I select Quarantine ordered by official document checkbox",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_LABEL);
          editedCase =
              editedCase.toBuilder()
                  .quarantineOrderedByDocument(
                      webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(
                          QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_INPUT))
                  .build();
        });

    When(
        "I check if Date of the official document ordered field is available",
        () ->
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                QUARANTINE_ORDERED_BY_DOCUMENT_DATE));

    When(
        "I select Official quarantine order sent",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_LABEL);
          editedCase =
              editedCase.toBuilder()
                  .quarantineOrderSet(
                      webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(
                          OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_INPUT))
                  .build();
        });
    When(
        "I check if Date official quarantine order was sent field is available",
        () ->
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                DATE_OFFICIAL_QUARANTINE_ORDER_WAS_SENT));

    When(
        "I check if Quarantine details field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(QUARANTINE_TYPE_DETAILS));

    When(
        "I set Vaccination Status as ([^\"]*)",
        (String vaccinationStatus) -> {
          webDriverHelpers.selectFromCombobox(
              VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX,
              CaseOutcome.getValueFor(vaccinationStatus));
          editedCase = editedCase.toBuilder().vaccinationStatus(vaccinationStatus).build();
        });

    When(
        "I set German Vaccination Status as ([^\"]*)",
        (String option) -> {
          String vaccinationStatus = new String();
          switch (option) {
            case "vaccinated":
              vaccinationStatus = VACCINATED_STATUS_VACCINATED.getNameDE();
              break;
            case "unvaccinated":
              vaccinationStatus = VACCINATED_STATUS_UNVACCINATED.getNameDE();
              break;
            case "unknown":
              vaccinationStatus = VACCINATED_STATUS_UNKNOWN.getNameDE();
              break;
          }
          webDriverHelpers.selectFromCombobox(
              VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX, vaccinationStatus);
          editedCase = editedCase.toBuilder().vaccinationStatus(vaccinationStatus).build();
        });

    When(
        "I check if the specific data is correctly displayed",
        () -> {
          specificCaseData = collectSpecificData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              specificCaseData,
              editedCase,
              List.of(
                  "investigationStatus",
                  "outomeOfCase",
                  "sequelae",
                  "differentPlaceOfStayJurisdiction",
                  "placeOfStay",
                  "region",
                  "district",
                  "facilityNameAndDescription",
                  "facility",
                  "facilityCategory",
                  "facilityType",
                  "quarantine",
                  "vaccinationStatus"));
        });

    When(
        "I collect the case person UUID displayed on Edit case page",
        () -> aCase = collectCasePersonUuid());

    When(
        "I check case created from created contact is correctly displayed on Edit Case page",
        () -> {
          aCase = collectCasePersonData();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription"));
        });

    When(
        "I check case created from created contact is correctly displayed on Edit Case page for DE",
        () -> {
          aCase = collectCasePersonDataDE();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription"));
        });

    When(
        "I am checking all Exposure data created by UI is saved and displayed in Cases",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(START_OF_EXPOSURE_INPUT);
          String contactToCaseUIvalue =
              (webDriverHelpers.getValueFromCombobox(CONTACT_TO_CASE_COMBOBOX)).toUpperCase();
          String contactToCase =
              apiState.getLastCreatedPerson().getFirstName().toUpperCase()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName().toUpperCase()
                  + " "
                  + "("
                  + dataOperations.getPartialUuidFromAssociatedLink(
                      apiState.getCreatedContact().getUuid().toUpperCase())
                  + ")";

          softly.assertEquals(
              contactToCase,
              contactToCaseUIvalue,
              "The First Name,Last Name and Contact ID for CONTACT TO SOURCE CASE field in Exposure form is different than data filled in Contact to case");
          softly.assertAll();
          Exposure actualExposureData = collectExposureDataCase();
          ComparisonHelper.compareEqualEntities(exposureData, actualExposureData);
        });

    When(
        "I click on New Task from Case page",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_TASK_BUTTON));

    When(
        "I click on first edit Task",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_TASK_BUTTON));

    When(
        "I click on New Sample",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_SAMPLE_BUTTON));

    When(
        "I click on New Sample in German",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_SAMPLE_BUTTON_DE));

    When(
        "I click on edit Sample",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_SAMPLE_BUTTON));

    When(
        "I click on the Create button from Case Document Templates",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_BUTTON));

    When(
        "I change the Case Classification field for {string} value",
        (String caseClassificationValue) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_CLASSIFICATION_FILTER_COMBOBOX,
              CaseClassification.getCaptionValueFor(caseClassificationValue));
        });

    When(
        "I create and download a case document from template",
        () -> {
          aQuarantineOrder = caseDocumentService.buildQuarantineOrder();
          aQuarantineOrder = aQuarantineOrder.toBuilder().build();
          selectQuarantineOrderTemplate(aQuarantineOrder.getDocumentTemplate());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXTRA_COMMENT_INPUT);
          fillExtraComment(aQuarantineOrder.getExtraComment());
          webDriverHelpers.clickOnWebElementBySelector(CREATE_QUARANTINE_ORDER_BUTTON);
          //  webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP);
        });

    And(
        "I verify that the case document is downloaded and correctly named",
        () -> {
          String uuid = webDriverHelpers.getValueFromWebElement(UUID_INPUT);
          Path path =
              Paths.get(
                  userDirPath
                      + "/downloads/"
                      + uuid.substring(0, 6).toUpperCase()
                      + "-"
                      + aQuarantineOrder.getDocumentTemplate());
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertTrue(
                      Files.exists(path),
                      String.format(
                          "Case document was not downloaded. Searching path was: %s",
                          path.toAbsolutePath())),
              120);
        });

    When(
        "I open last edited case by link",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseLinkPath = "/sormas-ui/#!cases/data/";
          String uuid = aCase.getUuid();
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + caseLinkPath + uuid);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(REPORT_DATE_INPUT);
        });

    When(
        "I open last edited case by API via URL navigation",
        () -> {
          String caseLinkPath = "/sormas-ui/#!cases/data/";
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + caseLinkPath + uuid);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
        });

    When(
        "I change all Case fields and save",
        () -> {
          aCase = caseService.buildEditGeneratedCase();
          aCase =
              aCase.toBuilder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
          fillDateOfReport(aCase.getDateOfReport());
          selectCaseClassification(aCase.getCaseClassification());
          selectClinicalConfirmation(aCase.getClinicalConfirmation());
          selectEpidemiologicalConfirmation(aCase.getEpidemiologicalConfirmation());
          selectLaboratoryDiagnosticConfirmation(aCase.getLaboratoryDiagnosticConfirmation());
          selectInvestigationStatus(aCase.getInvestigationStatus());
          fillExternalId(aCase.getExternalId());
          fillExternalToken(aCase.getExternalToken());
          selectDisease(aCase.getDisease());
          selectReinfection(aCase.getReinfection());
          selectOutcomeOfCase(aCase.getOutcomeOfCase());
          selectSequelae(aCase.getSequelae());
          selectCaseIdentificationSource(aCase.getCaseIdentificationSource());
          selectRegion(aCase.getRegion());
          selectDistrict(aCase.getDistrict());
          selectCommunity(aCase.getCommunity());
          fillPlaceDescription(aCase.getPlaceDescription());
          selectResponsibleRegion(aCase.getResponsibleRegion());
          selectResponsibleDistrict(aCase.getResponsibleDistrict());
          selectResponsibleCommunity(aCase.getResponsibleCommunity());
          selectProhibitionToWork(aCase.getProhibitionToWork());
          selectHomeBasedQuarantinePossible(aCase.getHomeBasedQuarantinePossible());
          selectQuarantine(aCase.getQuarantine());
          fillReportGpsLatitude(aCase.getReportGpsLatitude());
          fillReportGpsLongitude(aCase.getReportGpsLongitude());
          fillReportGpsAccuracyInM(aCase.getReportGpsAccuracyInM());
          selectBloodOrganTissueDonationInTheLast6Months(
              aCase.getBloodOrganTissueDonationInTheLast6Months());
          selectVaccinationStatusForThisDisease(aCase.getVaccinationStatusForThisDisease());
          selectResponsibleSurveillanceOfficer(aCase.getResponsibleSurveillanceOfficer());
          fillDateReceivedAtDistrictLevel(aCase.getDateReceivedAtDistrictLevel());
          fillDateReceivedAtRegionLevel(aCase.getDateReceivedAtRegionLevel());
          fillDateReceivedAtNationalLevel(aCase.getDateReceivedAtNationalLevel());
          fillGeneralComment(aCase.getGeneralComment());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I check the edited data is correctly displayed on Edit case page",
        () -> {
          editedCase = collectCaseData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              editedCase,
              aCase,
              List.of(
                  "dateOfReport",
                  "caseClassification",
                  "clinicalConfirmation",
                  "epidemiologicalConfirmation",
                  "laboratoryDiagnosticConfirmation",
                  "investigationStatus",
                  "externalId",
                  "externalToken",
                  "disease",
                  "reinfection",
                  "outcomeOfCase",
                  "sequelae",
                  "caseIdentificationSource",
                  "region",
                  "district",
                  "community",
                  "placeDescription",
                  "responsibleJurisdiction",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "prohibitionToWork",
                  "homeBasedQuarantinePossible",
                  "quarantine",
                  "reportGpsLatitude",
                  "reportGpsLongitude",
                  "reportGpsAccuracyInM",
                  "bloodOrganTissueDonationInTheLast6Months",
                  "vaccinationStatusForThisDisease",
                  "responsibleSurveillanceOfficer",
                  "dateReceivedAtDistrictLevel",
                  "dateReceivedAtRegionLevel",
                  "dateReceivedAtNationalLevel",
                  "generalComment"));
        });

    When(
        "I delete the case",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CASE_APPLY_FILTERS_BUTTON);
        });

    When(
        "I navigate to epidemiological data tab in Edit case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPIDEMIOLOGICAL_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I navigate to Event Participants tab in Edit case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I navigate to Contacts tab in Edit case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACTS_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the New Travel Entry button from Edit case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_TRAVEL_ENTRY_BUTTON_DE);
        });

    When(
        "I click on edit travel entry button form case epidemiological tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_TRAVEL_ENTRY_FROM_CASE_BUTTON);
        });

    When(
        "I check that case classification is set to not yet classified in German on Edit case page",
        () -> {
          String caseClassification =
              webDriverHelpers.getValueFromCombobox(CASE_CLASSIFICATION_COMBOBOX);
          softly.assertEquals(
              caseClassification,
              "0. Nicht klassifiziert",
              "The case classification is incorrect!");
          softly.assertAll();
        });

    When(
        "I check that case reference definition is not editable on Edit case page",
        () -> {
          String referenceReadOnlyAttribute =
              webDriverHelpers.getAttributeFromWebElement(REFERENCE_DEFINITION_TEXT, "readonly");
          softly.assertNotNull(
              referenceReadOnlyAttribute,
              "The case reference definition shouldn't be editable, but it is!");
          softly.assertAll();
        });

    When(
        "I check that case reference definition is set to not fulfilled in German on Edit case page",
        () -> {
          String caseReference = webDriverHelpers.getValueFromWebElement(REFERENCE_DEFINITION_TEXT);
          softly.assertEquals(
              caseReference, "Nicht erf\u00FCllt", "The case reference definition is incorrect!");
          softly.assertAll();
        });

    When(
        "I check that case reference definition is set to fulfilled in German on Edit case page",
        () -> {
          String caseReference = webDriverHelpers.getValueFromWebElement(REFERENCE_DEFINITION_TEXT);
          softly.assertEquals(
              caseReference, "Erf\u00FCllt", "The case reference definition is incorrect!");
          softly.assertAll();
        });

    When(
        "I search and chose the last case uuid created via UI in the CHOOSE CASE Contact window",
        () -> {
          webDriverHelpers.fillInWebElement(
              SOURCE_CASE_WINDOW_CONTACT_DE, EditCaseSteps.aCase.getUuid());
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.clickOnWebElementBySelector(
              SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitForRowToBeSelected(SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE);
        });

    When(
        "I check that case classification is set to one of the confirmed classifications in German on Edit case page",
        () -> {
          TimeUnit.SECONDS.sleep(
              3); // Required to ensure that the value we're asserting is refreshed after saving
          // sample
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          String caseClassification =
              webDriverHelpers.getValueFromCombobox(CASE_CLASSIFICATION_COMBOBOX);
          softly.assertTrue(
              Arrays.asList(
                      "A. Klinisch diagnostiziert",
                      "B. Klinisch-epidemiologisch best\u00E4tigt",
                      "C. Klinisch-labordiagnostisch best\u00E4tigt",
                      "D. Labordiagnostisch bei nicht erf\u00FCllter Klinik",
                      "E. Labordiagnostisch bei unbekannter Klinik")
                  .contains(caseClassification),
              "The case classification is incorrect!");
          softly.assertAll();
        });
  }

  private Case collectCasePersonUuid() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 40);
    return Case.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  private Case collectCasePersonData() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .build();
  }

  private Case collectCasePersonDataDE() {
    Case userInfo = getUserInformationDE();

    return Case.builder()
        .dateOfReport(getDateOfReportDE())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .diseaseVariant(webDriverHelpers.getValueFromWebElement(DISEASE_VARIANT_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .build();
  }

  private Case collectCaseData() {
    return Case.builder()
        .dateOfReport(getDateOfReport())
        .caseClassification(webDriverHelpers.getValueFromCombobox(CASE_CLASSIFICATION_COMBOBOX))
        .clinicalConfirmation(webDriverHelpers.getValueFromCombobox(CLINICAL_CONFIRMATION_COMBOBOX))
        .epidemiologicalConfirmation(
            webDriverHelpers.getValueFromCombobox(EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX))
        .laboratoryDiagnosticConfirmation(
            webDriverHelpers.getValueFromCombobox(LABORATORY_DIAGNOSTIC_CONFIRMATION_COMBOBOX))
        .investigationStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                INVESTIGATION_STATUS_OPTIONS))
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .externalToken(webDriverHelpers.getValueFromWebElement(EXTERNAL_TOKEN_INPUT))
        .disease(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        .reinfection(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(REINFECTION_OPTIONS))
        .outcomeOfCase(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTCOME_OF_CASE_OPTIONS))
        .sequelae(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SEQUELAE_OPTIONS))
        .caseIdentificationSource(
            webDriverHelpers.getValueFromCombobox(CASE_IDENTIFICATION_SOURCE_COMBOBOX))
        .region(webDriverHelpers.getValueFromCombobox(REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(DISTRICT_COMBOBOX))
        .community(webDriverHelpers.getValueFromCombobox(COMMUNITY_COMBOBOX))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        .prohibitionToWork(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PROHIBITION_TO_WORK_OPTIONS))
        .homeBasedQuarantinePossible(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                HOME_BASED_QUARANTINE_POSSIBLE_OPTIONS))
        .quarantine(webDriverHelpers.getValueFromCombobox(QUARANTINE_COMBOBOX))
        .reportGpsLatitude(webDriverHelpers.getValueFromWebElement(REPORT_GPS_LATITUDE_INPUT))
        .reportGpsLongitude(webDriverHelpers.getValueFromWebElement(REPORT_GPS_LONGITUDE_INPUT))
        .reportGpsAccuracyInM(
            webDriverHelpers.getValueFromWebElement(REPORT_GPS_ACCURACY_IN_M_INPUT))
        .bloodOrganTissueDonationInTheLast6Months(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                BLOOD_ORGAN_TISSUE_DONATION_IN_THE_LAST_6_MONTHS_OPTIONS))
        .dateReceivedAtDistrictLevel(getDateReceivedAtDistrictLevel())
        .dateReceivedAtRegionLevel(getDateReceivedAtRegionLevel())
        .dateReceivedAtNationalLevel(getDateReceivedAtNationalLevel())
        .generalComment(webDriverHelpers.getValueFromWebElement(GENERAL_COMMENT_TEXTAREA))
        .vaccinationStatusForThisDisease(
            webDriverHelpers.getValueFromCombobox(VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX))
        .responsibleSurveillanceOfficer(
            webDriverHelpers.getValueFromCombobox(RESPONSIBLE_SURVEILLANCE_OFFICER_COMBOBOX))
        .build();
  }

  public Exposure collectExposureDataCase() {
    return Exposure.builder()
        .startOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(START_OF_EXPOSURE_INPUT), DATE_FORMATTER))
        .endOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(END_OF_EXPOSURE_INPUT), DATE_FORMATTER))
        .exposureDescription(webDriverHelpers.getValueFromWebElement(EXPOSURE_DESCRIPTION_INPUT))
        .typeOfActivity(
            TypeOfActivityExposure.fromString(
                webDriverHelpers.getValueFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX)))
        .exposureDetailsRole(
            ExposureDetailsRole.fromString(
                webDriverHelpers.getValueFromCombobox(EXPOSURE_DETAILS_ROLE_COMBOBOX)))
        .riskArea(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RISK_AREA_OPTIONS)))
        .indoors(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(INDOORS_OPTIONS)))
        .outdoors(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTDOORS_OPTIONS)))
        .wearingMask(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_MASK_OPTIONS)))
        .wearingPpe(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_PPE_OPTIONS)))
        .otherProtectiveMeasures(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    OTHER_PROTECTIVE_MEASURES_OPTIONS)))
        .shortDistance(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHORT_DISTANCE_OPTIONS)))
        .longFaceToFaceContact(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    LONG_FACE_TO_FACE_CONTACT_OPTIONS)))
        .percutaneous(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERCUTANEOUS_OPTIONS)))
        .contactToBodyFluids(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    CONTACT_TO_BODY_FLUIDS_OPTONS)))
        .handlingSamples(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    HANDLING_SAMPLES_OPTIONS)))
        .typeOfPlace(
            TypeOfPlace.fromString(webDriverHelpers.getValueFromCombobox(TYPE_OF_PLACE_COMBOBOX)))
        .continent(webDriverHelpers.getValueFromCombobox(CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .build();
  }

  private Case collectCasePersonDataForExistingPerson() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .build();
  }

  private Case collectSpecificData() {
    return Case.builder()
        .investigationStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                INVESTIGATION_STATUS_OPTIONS))
        .outcomeOfCase(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTCOME_OF_CASE_OPTIONS))
        .sequelae(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SEQUELAE_OPTIONS))
        .differentPlaceOfStayJurisdiction(
            webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(PLACE_OF_STAY_CHECKBOX_INPUT))
        .placeOfStay(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PLACE_OF_STAY_OPTIONS))
        .facilityCategory(webDriverHelpers.getValueFromCombobox(FACILITY_CATEGORY_COMBOBOX))
        .facilityType(webDriverHelpers.getValueFromCombobox(FACILITY_TYPE_COMBOBOX))
        .facility(webDriverHelpers.getValueFromCombobox(FACILITY_HEALTH_COMBOBOX))
        .facilityNameAndDescription(
            webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .quarantine(webDriverHelpers.getValueFromCombobox(QUARANTINE_COMBOBOX))
        .vaccinationStatus(
            webDriverHelpers.getValueFromCombobox(VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX))
        .region(webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX))
        .build();
  }

  private LocalDate getDateOfReport() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalDate getDateOfReportDE() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
  }

  private LocalDate getDateReceivedAtDistrictLevel() {
    String dateOfReport =
        webDriverHelpers.getValueFromWebElement(DATE_RECEIVED_AT_DISTRICT_LEVEL_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalDate getDateReceivedAtRegionLevel() {
    String dateOfReport =
        webDriverHelpers.getValueFromWebElement(DATE_RECEIVED_AT_REGION_LEVEL_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalDate getDateReceivedAtNationalLevel() {
    String dateOfReport =
        webDriverHelpers.getValueFromWebElement(DATE_RECEIVED_AT_NATIONAL_LEVEL_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private Case getUserInformation() {
    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    LocalDate localDate = LocalDate.parse(userInfos[3].replace(")", ""), DATE_FORMATTER);
    return Case.builder()
        .firstName(userInfos[0])
        .lastName(userInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  private Case getUserInformationDE() {
    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    LocalDate localDate = LocalDate.parse(userInfos[3].replace(")", ""), DATE_FORMATTER_DE);
    return Case.builder()
        .firstName(userInfos[0])
        .lastName(userInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  private void fillDateOfReport(LocalDate date) {
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectCaseClassification(String caseClassification) {
    webDriverHelpers.selectFromCombobox(CASE_CLASSIFICATION_COMBOBOX, caseClassification);
  }

  private void selectClinicalConfirmation(String clinicalConfirmation) {
    webDriverHelpers.selectFromCombobox(CLINICAL_CONFIRMATION_COMBOBOX, clinicalConfirmation);
  }

  private void selectEpidemiologicalConfirmation(String epidemiologicalConfirmation) {
    webDriverHelpers.selectFromCombobox(
        EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX, epidemiologicalConfirmation);
  }

  private void selectLaboratoryDiagnosticConfirmation(String laboratoryDiagnosticConfirmation) {
    webDriverHelpers.selectFromCombobox(
        LABORATORY_DIAGNOSTIC_CONFIRMATION_COMBOBOX, laboratoryDiagnosticConfirmation);
  }

  private void selectInvestigationStatus(String investigationStatus) {
    webDriverHelpers.clickWebElementByText(INVESTIGATION_STATUS_OPTIONS, investigationStatus);
  }

  private void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }

  private void fillExternalToken(String externalToken) {
    webDriverHelpers.fillInWebElement(EXTERNAL_TOKEN_INPUT, externalToken);
  }

  private void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void selectReinfection(String reinfection) {
    webDriverHelpers.clickWebElementByText(REINFECTION_OPTIONS, reinfection);
  }

  private void selectOutcomeOfCase(String outcomeOfCase) {
    webDriverHelpers.clickWebElementByText(OUTCOME_OF_CASE_OPTIONS, outcomeOfCase);
  }

  private void selectCaseIdentificationSource(String caseIdentificationSource) {
    webDriverHelpers.selectFromCombobox(
        CASE_IDENTIFICATION_SOURCE_COMBOBOX, caseIdentificationSource);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  private void selectCommunity(String community) {
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, community);
  }

  private void fillPlaceDescription(String placeDescription) {
    webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, placeDescription);
  }

  private void selectResponsibleRegion(String responsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, responsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  private void selectSequelae(String sequelae) {
    webDriverHelpers.clickWebElementByText(SEQUELAE_OPTIONS, sequelae);
  }

  private void selectProhibitionToWork(String prohibitionToWork) {
    webDriverHelpers.clickWebElementByText(PROHIBITION_TO_WORK_OPTIONS, prohibitionToWork);
  }

  private void selectHomeBasedQuarantinePossible(String homeBasedQuarantinePossible) {
    webDriverHelpers.clickWebElementByText(
        HOME_BASED_QUARANTINE_POSSIBLE_OPTIONS, homeBasedQuarantinePossible);
  }

  private void selectQuarantine(String quarantine) {
    webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, quarantine);
  }

  private void fillReportGpsLatitude(String reportGpsLatitude) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_LATITUDE_INPUT, reportGpsLatitude);
  }

  private void fillReportGpsLongitude(String reportGpsLongitude) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_LONGITUDE_INPUT, reportGpsLongitude);
  }

  private void fillReportGpsAccuracyInM(String reportGpsAccuracyInM) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_ACCURACY_IN_M_INPUT, reportGpsAccuracyInM);
  }

  private void selectBloodOrganTissueDonationInTheLast6Months(
      String bloodOrganTissueDonationInTheLast6Months) {
    webDriverHelpers.clickWebElementByText(
        BLOOD_ORGAN_TISSUE_DONATION_IN_THE_LAST_6_MONTHS_OPTIONS,
        bloodOrganTissueDonationInTheLast6Months);
  }

  private void selectVaccinationStatusForThisDisease(String vaccinationStatusForThisDisease) {
    webDriverHelpers.selectFromCombobox(
        VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX, vaccinationStatusForThisDisease);
  }

  private void selectResponsibleSurveillanceOfficer(String responsibleSurveillanceOfficer) {
    webDriverHelpers.selectFromCombobox(
        RESPONSIBLE_SURVEILLANCE_OFFICER_COMBOBOX, responsibleSurveillanceOfficer);
  }

  private void fillDateReceivedAtDistrictLevel(LocalDate dateReceivedAtDistrictLevel) {
    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_DISTRICT_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtDistrictLevel));
  }

  private void fillDateReceivedAtRegionLevel(LocalDate dateReceivedAtRegionLevel) {
    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_REGION_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtRegionLevel));
  }

  private void fillDateReceivedAtNationalLevel(LocalDate dateReceivedAtNationalLevel) {
    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_NATIONAL_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtNationalLevel));
  }

  private void fillGeneralComment(String generalComment) {
    webDriverHelpers.fillInWebElement(GENERAL_COMMENT_TEXTAREA, generalComment);
  }

  private void selectQuarantineOrderTemplate(String templateName) {
    webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, templateName);
  }

  private void fillExtraComment(String extraComment) {
    webDriverHelpers.fillInAndLeaveWebElement(EditCasePage.EXTRA_COMMENT_TEXTAREA, extraComment);
  }
}
