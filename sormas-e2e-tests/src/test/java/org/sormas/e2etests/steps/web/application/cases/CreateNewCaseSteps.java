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

import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.*;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CONTACT_CASE_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.cases.EditCasePage;

public class CreateNewCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Case caze;

  @Inject
  public CreateNewCaseSteps(WebDriverHelpers webDriverHelpers, CaseService caseService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I create a new case with specific data for positive pathogen test result",
        () -> {
          caze = caseService.buildEditGeneratedCaseForPositivePathogenTestResult();
          fillDateOfReport(caze.getDateOfReport());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          webDriverHelpers.scrollToElement(CONTACT_CASE_SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CASE_SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "^I create a new case with specific data$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth());
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset());
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport());
          fillPlaceDescription(caze.getPlaceDescription());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    When(
        "^I fill new case form with specific data$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth());
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset());
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport());
          fillPlaceDescription(caze.getPlaceDescription());
        });

    When(
        "^I create a new case with specific data using line listing feature$",
        () -> {
          caze = caseService.buildCaseForLineListingFeature();

          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth());
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset());
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport());
          fillPlaceDescription(caze.getPlaceDescription());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    When(
        "^I create a new case for contact with specific data$",
        () -> {
          caze = caseService.buildGeneratedCase();
          fillDateOfReport(caze.getDateOfReport());
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());

          selectPlaceOfStay(caze.getPlaceOfStay());
          fillPlaceDescription(caze.getPlaceDescription());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset());

          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CASE_SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    Then(
        "^I click on save case button$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "^I fill all fields for a new case created for event participant$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());

          fillDateOfBirth(caze.getDateOfBirth());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset());
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport());
          fillPlaceDescription(caze.getPlaceDescription());
        });
  }

  private void selectCaseOrigin(String caseOrigin) {
    webDriverHelpers.clickWebElementByText(CASE_ORIGIN_OPTIONS, caseOrigin);
  }

  private void fillDateOfReport(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  private void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }

  private void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  private void selectPlaceOfStay(String placeOfStay) {
    webDriverHelpers.clickWebElementByText(PLACE_OF_STAY, placeOfStay);
  }

  private void fillPlaceDescription(String placeDescription) {
    webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, placeDescription);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  private void fillDateOfBirth(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  private void selectPresentConditionOfPerson(String presentConditionOfPerson) {
    webDriverHelpers.selectFromCombobox(
        PRESENT_CONDITION_OF_PERSON_COMBOBOX, presentConditionOfPerson);
  }

  private void fillDateOfSymptomOnset(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_ONSET_INPUT, formatter.format(date));
  }

  private void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
  }

  private void fillPrimaryEmailAddress(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryPhoneNumber);
  }
}
