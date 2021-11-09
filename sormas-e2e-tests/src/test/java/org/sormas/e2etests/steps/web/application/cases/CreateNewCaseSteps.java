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

import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.*;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.pojo.web.Case;
import org.sormas.e2etests.services.CaseService;

public class CreateNewCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Case caze;

  @Inject
  public CreateNewCaseSteps(WebDriverHelpers webDriverHelpers, CaseService caseService) {
    this.webDriverHelpers = webDriverHelpers;

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
          fillNationalHealthId(caze.getNationalHealthId());
          fillPassportNumber(caze.getPassportNumber());
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
        "^I create a new case with specific data using line listing feature$",
        () -> {
          caze = caseService.buildCaseForLineListingFeature();

          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth());
          selectSex(caze.getSex());
          fillNationalHealthId(caze.getNationalHealthId());
          fillPassportNumber(caze.getPassportNumber());
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
  }

  public void selectCaseOrigin(String caseOrigin) {
    webDriverHelpers.clickWebElementByText(CASE_ORIGIN_OPTIONS, caseOrigin);
  }

  public void fillDateOfReport(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  public void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }

  public void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  public void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  public void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  public void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  public void selectPlaceOfStay(String placeOfStay) {
    webDriverHelpers.clickWebElementByText(PLACE_OF_STAY, placeOfStay);
  }

  public void fillPlaceDescription(String placeDescription) {
    webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, placeDescription);
  }

  public void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  public void fillDateOfBirth(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  public void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  public void fillNationalHealthId(String nationalHealthId) {
    webDriverHelpers.fillInWebElement(NATIONAL_HEALTH_ID_INPUT, nationalHealthId);
  }

  public void fillPassportNumber(String passportNumber) {
    webDriverHelpers.fillInWebElement(PASSPORT_NUMBER_INPUT, passportNumber);
  }

  public void selectPresentConditionOfPerson(String presentConditionOfPerson) {
    webDriverHelpers.selectFromCombobox(
        PRESENT_CONDITION_OF_PERSON_COMBOBOX, presentConditionOfPerson);
  }

  public void fillDateOfSymptomOnset(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_ONSET_INPUT, formatter.format(date));
  }

  public void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
  }

  public void fillPrimaryEmailAddress(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryPhoneNumber);
  }
}
