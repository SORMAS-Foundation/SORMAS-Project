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

import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.pojo.web.Visit;
import org.sormas.e2etests.services.FollowUpVisitService;

public class FollowUpStep implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static Visit visit;

  @Inject
  public FollowUpStep(
      WebDriverHelpers webDriverHelpers, FollowUpVisitService followUpVisitService) {
    this.webDriverHelpers = webDriverHelpers;

    And(
        "I click on new Visit button",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_VISIT_BUTTON));

    And(
        "I click on edit Visit button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_VISIT_BUTTON);
        });

    When(
        "^I create a new Visit with specific data$",
        () -> {
          visit = followUpVisitService.buildVisit();
          selectPersonAvailable(
              visit.getPersonAvailableAndCooperative(), AVAILABLE_AND_COOPERATIVE);
          fillDateOfVisit(visit.getDateOfVisit());
          fillVisitRemarks(visit.getVisitRemarks(), VISIT_REMARKS);
          selectCurrentTemperature(visit.getCurrentBodyTemperature());
          selectSourceOfTemperature(visit.getSourceOfBodyTemperature());
          selectClearedToNo(visit.getSetClearToNo(), SET_CLEARED_TO_NO_BUTTON);
          selectChillsAndSweats(visit.getChillsAndSweats(), CHILLS_SWEATS_YES_BUTTON);
          selectFeelingIll(visit.getFeelingIll(), FEELING_ILL_YES_BUTTON);
          selectFever(visit.getFever(), FEVER_YES_BUTTON);
          fillComments(visit.getComments(), SYMPTOMS_COMMENTS_INPUT);
          selectFirstSymptom(visit.getFirstSymptom(), FIRST_SYMPTOM_COMBOBOX);
          fillDateOfSymptoms(visit.getDateOfSymptom());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_VISIT_BUTTON);
        });

    When(
        "^I validate all fields from Visit",
        () -> {
          final Visit actualVisit = collectTestResultsData();
          ComparisonHelper.compareEqualEntities(visit, actualVisit);
        });
  }

  private void selectPersonAvailable(String availableAndCooperative, By element) {
    webDriverHelpers.clickWebElementByText(element, availableAndCooperative);
  }

  private void fillDateOfVisit(LocalDate dateOfVisit) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_OF_VISIT_INPUT, DATE_FORMATTER.format(dateOfVisit));
  }

  private void fillVisitRemarks(String remarks, By element) {
    webDriverHelpers.clearAndFillInWebElement(element, remarks);
  }

  private void selectCurrentTemperature(String currentTemperature) {
    webDriverHelpers.selectFromCombobox(CURRENT_BODY_TEMPERATURE_COMBOBOX, currentTemperature);
  }

  private void selectSourceOfTemperature(String sourceTemperature) {
    webDriverHelpers.selectFromCombobox(SOURCE_OF_BODY_TEMPERATURE_COMBOBOX, sourceTemperature);
  }

  private void selectClearedToNo(String clearedToNo, By element) {
    webDriverHelpers.clickWebElementByText(element, clearedToNo);
  }

  private void selectChillsAndSweats(String chillsAndSweats, By element) {
    webDriverHelpers.clickWebElementByText(element, chillsAndSweats);
  }

  private void selectFeelingIll(String feelingIll, By element) {
    webDriverHelpers.clickWebElementByText(element, feelingIll);
  }

  private void selectFever(String fever, By element) {
    webDriverHelpers.clickWebElementByText(element, fever);
  }

  private void fillComments(String comments, By element) {
    webDriverHelpers.fillAndSubmitInWebElement(element, comments);
  }

  private void selectFirstSymptom(String firstSymptom, By element) {
    webDriverHelpers.selectFromCombobox(element, firstSymptom);
  }

  private void fillDateOfSymptoms(LocalDate dateOfSymptom) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_OF_ONSET_INPUT, DATE_FORMATTER.format(dateOfSymptom));
  }

  private Visit collectTestResultsData() {
    return Visit.builder()
        .personAvailableAndCooperative(getPersonAvailableAndCooperative())
        .dateOfVisit(getDateOfVisit())
        .timeOfVisit(visit.getTimeOfVisit())
        .visitRemarks(getVisitRemarks())
        .currentBodyTemperature(getCurrentBodyTemperature())
        .sourceOfBodyTemperature(getSourceOfBodyTemperature())
        .setClearToNo(visit.getSetClearToNo())
        .chillsAndSweats(getChillsAndSweats())
        .feelingIll(getFeelingIll())
        .fever(getFever())
        .comments(getComments())
        .firstSymptom(getFirstSymptom())
        .dateOfSymptom(getDateOfSymptom())
        .build();
  }

  private String getPersonAvailableAndCooperative() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERSONS_AVAILABLE_OPTIONS);
  }

  private LocalDate getDateOfVisit() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_OF_VISIT_INPUT), DATE_FORMATTER);
  }

  private String getVisitRemarks() {
    return webDriverHelpers.getValueFromWebElement(VISIT_REMARKS);
  }

  private String getCurrentBodyTemperature() {
    return webDriverHelpers.getValueFromWebElement(CURRENT_BODY_TEMPERATURE_INPUT).substring(0, 4);
  }

  private String getSourceOfBodyTemperature() {
    return webDriverHelpers.getValueFromWebElement(SOURCE_OF_BODY_TEMPERATURE_INPUT);
  }

  private String getChillsAndSweats() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CHILLS_SWEATS_OPTIONS);
  }

  private String getFeelingIll() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEELING_ILL_OPTIONS);
  }

  private String getFever() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEVER_OPTIONS);
  }

  private String getComments() {
    return webDriverHelpers.getValueFromWebElement(SYMPTOMS_COMMENTS_INPUT);
  }

  private String getFirstSymptom() {
    return webDriverHelpers.getValueFromWebElement(FIRST_SYMPTOM_INPUT);
  }

  private LocalDate getDateOfSymptom() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_OF_ONSET_INPUT), DATE_FORMATTER);
  }
}
