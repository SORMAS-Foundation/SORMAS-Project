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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;

import cucumber.api.java8.En;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.pojo.web.Case;
import org.sormas.e2etests.pojo.web.QuarantineOrder;
import org.sormas.e2etests.services.CaseDocumentService;
import org.sormas.e2etests.services.CaseService;

public class EditCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Case aCase;
  private static Case createdCase;
  private static Case editedCase;
  public static QuarantineOrder aQuarantineOrder;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final String userDirPath = System.getProperty("user.dir");

  @SneakyThrows
  @Inject
  public EditCaseSteps(
      WebDriverHelpers webDriverHelpers,
      CaseService caseService,
      CaseDocumentService caseDocumentService,
      @Named("ENVIRONMENT_URL") String environmentUrl) {
    this.webDriverHelpers = webDriverHelpers;

    And(
        "I navigate to follow-up tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(FOLLOW_UP_BUTTON));

    And(
        "I navigate to symptoms tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(SYMPTOMS_BUTTON));

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
        "I collect the case person UUID displayed on Edit case page",
        () -> aCase = collectCasePersonUuid());

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
        "I create a case document from template",
        () -> {
          File dir = new File(userDirPath + "\\downloads");
          FileUtils.deleteDirectory(dir);
          aQuarantineOrder = caseDocumentService.buildQuarantineOrder();
          aQuarantineOrder = aQuarantineOrder.toBuilder().build();
          selectQuarantineOrderTemplate(aQuarantineOrder.getDocumentTemplate());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXTRA_COMMENT_INPUT);
          fillExtraComment(aQuarantineOrder.getExtraComment());
          webDriverHelpers.clickOnWebElementBySelector(CREATE_QUARANTINE_ORDER_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP);
        });

    Then(
        "I verify that the generated case document is downloaded",
        () -> {
          Stream<Path> list = Files.list(Paths.get(userDirPath + "\\downloads"));
          Truth.assertThat(list.findFirst().isPresent()).isTrue();
        });

    And(
        "I verify that the downloaded case document is correctly named",
        () -> {
          String uuid = webDriverHelpers.getValueFromWebElement(UUID_INPUT);
          Path path =
              Paths.get(
                  userDirPath
                      + "\\downloads\\"
                      + uuid.substring(0, 6)
                      + "-"
                      + aQuarantineOrder.getDocumentTemplate());
          Truth.assertThat(Files.exists(path)).isTrue();
        });

    When(
        "I open last edited case by link",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseLinkPath = "/sormas-webdriver/#!cases/data/";
          String uuid = aCase.getUuid();
          webDriverHelpers.accessWebSite(environmentUrl + caseLinkPath + uuid);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(REPORT_DATE_INPUT);
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

  private LocalDate getDateOfReport() {

    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
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
