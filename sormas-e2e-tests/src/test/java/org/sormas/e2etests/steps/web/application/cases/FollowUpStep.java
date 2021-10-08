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

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Visit;
import org.sormas.e2etests.services.FollowUpVisitService;

public class FollowUpStep implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static Visit visit;

  @Inject
  public FollowUpStep(WebDriverHelpers webDriverHelpers, FollowUpVisitService followUpVisitService)
      throws InterruptedException {
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
          Truth.assertThat(visit).isEqualTo(actualVisit);
        });
  }

  public void selectPersonAvailable(String availableAndCooperative, By element) {
    webDriverHelpers.clickWebElementByText(element, availableAndCooperative);
  }

  public void fillDateOfVisit(LocalDate dateOfVisit) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_OF_VISIT_INPUT, DATE_FORMATTER.format(dateOfVisit));
  }

  public void fillVisitRemarks(String remarks, By element) {
    webDriverHelpers.clearAndFillInWebElement(element, remarks);
  }

  public void selectCurrentTemperature(String currentTemperature) {
    webDriverHelpers.selectFromCombobox(CURRENT_BODY_TEMPERATURE_COMBOBOX, currentTemperature);
  }

  public void selectSourceOfTemperature(String sourceTemperature) {
    webDriverHelpers.selectFromCombobox(SOURCE_OF_BODY_TEMPERATURE_COMBOBOX, sourceTemperature);
  }

  public void selectClearedToNo(String clearedToNo, By element) {
    webDriverHelpers.clickWebElementByText(element, clearedToNo);
  }

  public void selectChillsAndSweats(String chillsAndSweats, By element) {
    webDriverHelpers.clickWebElementByText(element, chillsAndSweats);
  }

  public void selectFeelingIll(String feelingIll, By element) {
    webDriverHelpers.clickWebElementByText(element, feelingIll);
  }

  public void selectFever(String fever, By element) {
    webDriverHelpers.clickWebElementByText(element, fever);
  }

  public void fillComments(String comments, By element) {
    webDriverHelpers.fillAndSubmitInWebElement(element, comments);
  }

  public void selectFirstSymptom(String firstSymptom, By element) {
    webDriverHelpers.selectFromCombobox(element, firstSymptom);
  }

  public void fillDateOfSymptoms(LocalDate dateOfSymptom) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_OF_ONSET_INPUT, DATE_FORMATTER.format(dateOfSymptom));
  }

  public Visit collectTestResultsData() {
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

  public String getPersonAvailableAndCooperative() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERSONS_AVAILABLE_OPTIONS);
  }

  public LocalDate getDateOfVisit() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_OF_VISIT_INPUT), DATE_FORMATTER);
  }

  public String getVisitRemarks() {
    return webDriverHelpers.getValueFromWebElement(VISIT_REMARKS);
  }

  public String getCurrentBodyTemperature() {
    return webDriverHelpers.getValueFromWebElement(CURRENT_BODY_TEMPERATURE_INPUT).substring(0, 4);
  }

  public String getSourceOfBodyTemperature() {
    return webDriverHelpers.getValueFromWebElement(SOURCE_OF_BODY_TEMPERATURE_INPUT);
  }

  public String getChillsAndSweats() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CHILLS_SWEATS_OPTIONS);
  }

  public String getFeelingIll() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEELING_ILL_OPTIONS);
  }

  public String getFever() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEVER_OPTIONS);
  }

  public String getComments() {
    return webDriverHelpers.getValueFromWebElement(SYMPTOMS_COMMENTS_INPUT);
  }

  public String getFirstSymptom() {
    return webDriverHelpers.getValueFromWebElement(FIRST_SYMPTOM_INPUT);
  }

  public LocalDate getDateOfSymptom() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_OF_ONSET_INPUT), DATE_FORMATTER);
  }
}
