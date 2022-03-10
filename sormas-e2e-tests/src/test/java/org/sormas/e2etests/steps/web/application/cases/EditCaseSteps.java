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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.*;
import static org.sormas.e2etests.pages.application.cases.SymptomsTabPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.FOLLOW_UP_UNTIL_DATE;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.QuarantineOrder;
import org.sormas.e2etests.entities.services.CaseDocumentService;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
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
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I fill the specific Date of outcome",
        () -> {
          webDriverHelpers.fillInWebElement(
              DATE_OF_OUTCOME_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(1)));
        });

    //    When(
    //        "I check if Date of outcome for specified case is correct",
    //        () -> {
    //          String dateOfOutcome = webDriverHelpers.getValueFromWebElement(DATE_OF_OUTCOME);
    //          softly.assertEquals(
    //              LocalDate.parse(dateOfOutcome, DATE_FORMATTER),
    //              DATE_FORMATTER.format(LocalDate.now().minusDays(1)),
    //              "Date of outcome is invalid");
    //          softly.assertAll();
    //        });

    When(
        "I select ([^\"]*) as Outcome Of Case Status",
        (String caseStatus) -> {
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, CaseOutcome.getValueFor(caseStatus).toUpperCase());
          TimeUnit.SECONDS.sleep(1);
        });

    And(
        "I click on save button from Edit Case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
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
          webDriverHelpers.waitForPageLoaded();
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
        "I check if date of outcome filed is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_OUTCOME));

    When(
        "I click on ([^\"]*) option in Sequelae",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(
              SEQUELAE_OPTIONS, CaseOutcome.getValueFor(option).toUpperCase());
          editedCase = editedCase.toBuilder().sequelae(option).build();
          webDriverHelpers.waitForPageLoaded();
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
        "I click on New Task from Case page",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_TASK_BUTTON));

    When(
        "I click on first edit Task",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_TASK_BUTTON));

    When(
        "I click on New Sample",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_SAMPLE_BUTTON));

    When(
        "I click on edit Sample",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_SAMPLE_BUTTON));

    When(
        "I click on the Create button from Case Document Templates",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_BUTTON));

    When(
        "I create and download a case document from template",
        () -> {
          aQuarantineOrder = caseDocumentService.buildQuarantineOrder();
          aQuarantineOrder = aQuarantineOrder.toBuilder().build();
          selectQuarantineOrderTemplate(aQuarantineOrder.getDocumentTemplate());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXTRA_COMMENT_INPUT);
          fillExtraComment(aQuarantineOrder.getExtraComment());
          webDriverHelpers.clickOnWebElementBySelector(CREATE_QUARANTINE_ORDER_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP);
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
              environmentManager.getEnvironmentUrlForMarket(locale) + caseLinkPath + uuid);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(REPORT_DATE_INPUT);
        });

    When(
        "I open last edited case by API via URL navigation",
        () -> {
          String caseLinkPath = "/sormas-ui/#!cases/data/";
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(
              environmentManager.getEnvironmentUrlForMarket(locale) + caseLinkPath + uuid);
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
    webDriverHelpers.selectFromCombobox(EditCasePage.QUARANTINE_ORDER_COMBOBOX, templateName);
  }

  private void fillExtraComment(String extraComment) {
    webDriverHelpers.fillInAndLeaveWebElement(EditCasePage.EXTRA_COMMENT_TEXTAREA, extraComment);
  }
}
