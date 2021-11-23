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

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pojo.web.Case;
import org.sormas.e2etests.services.CaseService;
import org.sormas.e2etests.state.ApiState;

public class EditCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Case aCase;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @SneakyThrows
  @Inject
  public EditCaseSteps(
      WebDriverHelpers webDriverHelpers,
      CaseService caseService,
      ApiState apiState,
      @Named("ENVIRONMENT_URL") String environmentUrl) {
    this.webDriverHelpers = webDriverHelpers;

    And(
        "I navigate to fallow-up tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(FOLLOW_UP_BUTTON));

    And(
        "I navigate to symptoms tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(SYMPTOMS_BUTTON));

    When(
        "I check the created data is correctly displayed on Edit case page",
        () -> {
          aCase = collectCasePersonData();
          Truth.assertThat(aCase.getDateOfReport())
              .isEqualTo(CreateNewCaseSteps.caze.getDateOfReport());
          Truth.assertThat(aCase.getExternalId())
              .isEqualTo(CreateNewCaseSteps.caze.getExternalId());
          Truth.assertThat(aCase.getDisease()).isEqualTo(CreateNewCaseSteps.caze.getDisease());
          Truth.assertThat(aCase.getResponsibleRegion())
              .isEqualTo(CreateNewCaseSteps.caze.getResponsibleRegion());
          Truth.assertThat(aCase.getResponsibleDistrict())
              .isEqualTo(CreateNewCaseSteps.caze.getResponsibleDistrict());
          Truth.assertThat(aCase.getResponsibleCommunity())
              .isEqualTo(CreateNewCaseSteps.caze.getResponsibleCommunity());
          Truth.assertThat(aCase.getPlaceOfStay())
              .isEqualTo(CreateNewCaseSteps.caze.getPlaceOfStay());
          Truth.assertThat(aCase.getPlaceDescription())
              .isEqualTo(CreateNewCaseSteps.caze.getPlaceDescription());
          Truth.assertThat(aCase.getFirstName()).isEqualTo(CreateNewCaseSteps.caze.getFirstName());
          Truth.assertThat(
                  aCase.getLastName().equalsIgnoreCase(CreateNewCaseSteps.caze.getLastName()))
              .isTrue();
          Truth.assertThat(aCase.getDateOfBirth())
              .isEqualTo(CreateNewCaseSteps.caze.getDateOfBirth());
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
        "I open last edited case by link",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseLinkPath = "/sormas-ui/#!cases/data/";
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
          Case editCase = collectCaseData();
          Truth.assertThat(editCase.getDateOfReport()).isEqualTo(aCase.getDateOfReport());
          Truth.assertThat(editCase.getCaseClassification())
              .isEqualTo(aCase.getCaseClassification());
          Truth.assertThat(editCase.getClinicalConfirmation())
              .isEqualTo(aCase.getClinicalConfirmation());
          Truth.assertThat(editCase.getEpidemiologicalConfirmation())
              .isEqualTo(aCase.getEpidemiologicalConfirmation());
          Truth.assertThat(editCase.getLaboratoryDiagnosticConfirmation())
              .isEqualTo(aCase.getLaboratoryDiagnosticConfirmation());
          Truth.assertThat(editCase.getInvestigationStatus())
              .isEqualTo(aCase.getInvestigationStatus());
          Truth.assertThat(editCase.getExternalId()).isEqualTo(aCase.getExternalId());
          Truth.assertThat(editCase.getExternalToken()).isEqualTo(aCase.getExternalToken());
          Truth.assertThat(editCase.getDisease()).isEqualTo(aCase.getDisease());
          Truth.assertThat(editCase.getReinfection()).isEqualTo(aCase.getReinfection());
          Truth.assertThat(editCase.getOutcomeOfCase()).isEqualTo(aCase.getOutcomeOfCase());
          Truth.assertThat(editCase.getSequelae()).isEqualTo(aCase.getSequelae());
          Truth.assertThat(editCase.getCaseIdentificationSource())
              .isEqualTo(aCase.getCaseIdentificationSource());
          Truth.assertThat(editCase.getRegion()).isEqualTo(aCase.getRegion());
          Truth.assertThat(editCase.getDistrict()).isEqualTo(aCase.getDistrict());
          Truth.assertThat(editCase.getCommunity()).isEqualTo(aCase.getCommunity());
          Truth.assertThat(editCase.getPlaceDescription()).isEqualTo(aCase.getPlaceDescription());
          Truth.assertThat(editCase.getResponsibleJurisdiction())
              .isEqualTo(aCase.getResponsibleJurisdiction());
          Truth.assertThat(editCase.getResponsibleRegion()).isEqualTo(aCase.getResponsibleRegion());
          Truth.assertThat(editCase.getResponsibleDistrict())
              .isEqualTo(aCase.getResponsibleDistrict());
          Truth.assertThat(editCase.getResponsibleCommunity())
              .isEqualTo(aCase.getResponsibleCommunity());
          Truth.assertThat(editCase.getProhibitionToWork()).isEqualTo(aCase.getProhibitionToWork());
          Truth.assertThat(editCase.getHomeBasedQuarantinePossible())
              .isEqualTo(aCase.getHomeBasedQuarantinePossible());
          Truth.assertThat(editCase.getQuarantine()).isEqualTo(aCase.getQuarantine());
          Truth.assertThat(editCase.getReportGpsLatitude()).isEqualTo(aCase.getReportGpsLatitude());
          Truth.assertThat(editCase.getReportGpsLongitude())
              .isEqualTo(aCase.getReportGpsLongitude());
          Truth.assertThat(editCase.getReportGpsAccuracyInM())
              .isEqualTo(aCase.getReportGpsAccuracyInM());
          Truth.assertThat(editCase.getBloodOrganTissueDonationInTheLast6Months())
              .isEqualTo(aCase.getBloodOrganTissueDonationInTheLast6Months());
          Truth.assertThat(editCase.getVaccinationStatusForThisDisease())
              .isEqualTo(aCase.getVaccinationStatusForThisDisease());
          Truth.assertThat(editCase.getResponsibleSurveillanceOfficer())
              .isEqualTo(aCase.getResponsibleSurveillanceOfficer());
          Truth.assertThat(editCase.getDateReceivedAtDistrictLevel())
              .isEqualTo(aCase.getDateReceivedAtDistrictLevel());
          Truth.assertThat(editCase.getDateReceivedAtRegionLevel())
              .isEqualTo(aCase.getDateReceivedAtRegionLevel());
          Truth.assertThat(editCase.getDateReceivedAtNationalLevel())
              .isEqualTo(aCase.getDateReceivedAtNationalLevel());
          Truth.assertThat(editCase.getGeneralComment()).isEqualTo(aCase.getGeneralComment());
        });

    When(
        "I delete the case",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CASE_APPLY_FILTERS_BUTTON);
        });
  }

  public Case collectCasePersonUuid() {
    return Case.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  public Case collectCasePersonData() {
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

  public Case collectCaseData() {
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

  public Case getUserInformation() {

    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    LocalDate localDate = LocalDate.parse(userInfos[3].replace(")", ""), DATE_FORMATTER);
    return Case.builder()
        .firstName(userInfos[0])
        .lastName(userInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  public void fillDateOfReport(LocalDate date) {
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  public void selectCaseClassification(String caseClassification) {
    webDriverHelpers.selectFromCombobox(CASE_CLASSIFICATION_COMBOBOX, caseClassification);
  }

  public void selectClinicalConfirmation(String clinicalConfirmation) {
    webDriverHelpers.selectFromCombobox(CLINICAL_CONFIRMATION_COMBOBOX, clinicalConfirmation);
  }

  public void selectEpidemiologicalConfirmation(String epidemiologicalConfirmation) {
    webDriverHelpers.selectFromCombobox(
        EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX, epidemiologicalConfirmation);
  }

  public void selectLaboratoryDiagnosticConfirmation(String laboratoryDiagnosticConfirmation) {
    webDriverHelpers.selectFromCombobox(
        LABORATORY_DIAGNOSTIC_CONFIRMATION_COMBOBOX, laboratoryDiagnosticConfirmation);
  }

  public void selectInvestigationStatus(String investigationStatus) {
    webDriverHelpers.clickWebElementByText(INVESTIGATION_STATUS_OPTIONS, investigationStatus);
  }

  public void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }

  public void fillExternalToken(String externalToken) {
    webDriverHelpers.fillInWebElement(EXTERNAL_TOKEN_INPUT, externalToken);
  }

  public void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  public void selectReinfection(String reinfection) {
    webDriverHelpers.clickWebElementByText(REINFECTION_OPTIONS, reinfection);
  }

  public void selectOutcomeOfCase(String outcomeOfCase) {
    webDriverHelpers.clickWebElementByText(OUTCOME_OF_CASE_OPTIONS, outcomeOfCase);
  }

  public void selectCaseIdentificationSource(String caseIdentificationSource) {
    webDriverHelpers.selectFromCombobox(
        CASE_IDENTIFICATION_SOURCE_COMBOBOX, caseIdentificationSource);
  }

  public void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  public void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  public void selectCommunity(String community) {
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, community);
  }

  public void fillPlaceDescription(String placeDescription) {
    webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, placeDescription);
  }

  public void selectResponsibleRegion(String responsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, responsibleRegion);
  }

  public void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  public void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  public void selectSequelae(String sequelae) {
    webDriverHelpers.clickWebElementByText(SEQUELAE_OPTIONS, sequelae);
  }

  public void selectProhibitionToWork(String prohibitionToWork) {
    webDriverHelpers.clickWebElementByText(PROHIBITION_TO_WORK_OPTIONS, prohibitionToWork);
  }

  public void selectHomeBasedQuarantinePossible(String homeBasedQuarantinePossible) {
    webDriverHelpers.clickWebElementByText(
        HOME_BASED_QUARANTINE_POSSIBLE_OPTIONS, homeBasedQuarantinePossible);
  }

  public void selectQuarantine(String quarantine) {
    webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, quarantine);
  }

  public void fillReportGpsLatitude(String reportGpsLatitude) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_LATITUDE_INPUT, reportGpsLatitude);
  }

  public void fillReportGpsLongitude(String reportGpsLongitude) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_LONGITUDE_INPUT, reportGpsLongitude);
  }

  public void fillReportGpsAccuracyInM(String reportGpsAccuracyInM) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_ACCURACY_IN_M_INPUT, reportGpsAccuracyInM);
  }

  public void selectBloodOrganTissueDonationInTheLast6Months(
      String bloodOrganTissueDonationInTheLast6Months) {
    webDriverHelpers.clickWebElementByText(
        BLOOD_ORGAN_TISSUE_DONATION_IN_THE_LAST_6_MONTHS_OPTIONS,
        bloodOrganTissueDonationInTheLast6Months);
  }

  public void selectVaccinationStatusForThisDisease(String vaccinationStatusForThisDisease) {
    webDriverHelpers.selectFromCombobox(
        VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX, vaccinationStatusForThisDisease);
  }

  public void selectResponsibleSurveillanceOfficer(String responsibleSurveillanceOfficer) {
    webDriverHelpers.selectFromCombobox(
        RESPONSIBLE_SURVEILLANCE_OFFICER_COMBOBOX, responsibleSurveillanceOfficer);
  }

  public void fillDateReceivedAtDistrictLevel(LocalDate dateReceivedAtDistrictLevel) {
    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_DISTRICT_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtDistrictLevel));
  }

  public void fillDateReceivedAtRegionLevel(LocalDate dateReceivedAtRegionLevel) {

    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_REGION_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtRegionLevel));
  }

  public void fillDateReceivedAtNationalLevel(LocalDate dateReceivedAtNationalLevel) {
    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_NATIONAL_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtNationalLevel));
  }

  public void fillGeneralComment(String generalComment) {
    webDriverHelpers.fillInWebElement(GENERAL_COMMENT_TEXTAREA, generalComment);
  }
}
