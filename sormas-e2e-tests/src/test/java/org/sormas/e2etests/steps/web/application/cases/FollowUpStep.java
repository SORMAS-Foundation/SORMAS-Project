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
import static org.sormas.e2etests.pages.application.cases.HospitalizationTabPage.BLUE_ERROR_EXCLAMATION_MARK;
import static org.sormas.e2etests.pages.application.cases.HospitalizationTabPage.BLUE_ERROR_EXCLAMATION_MARK_TEXT;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.pojo.web.FollowUpVisit;
import org.sormas.e2etests.pojo.web.Visit;
import org.sormas.e2etests.services.FollowUpVisitService;
import org.testng.asserts.SoftAssert;

public class FollowUpStep implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  private final WebDriverHelpers webDriverHelpers;
  public static Visit visit;
  public static Visit specificVisit;
  public static FollowUpVisit followUpVisit;
  public static FollowUpVisit expectedVisitResults;
  public static Faker faker;

  @Inject
  public FollowUpStep(
      WebDriverHelpers webDriverHelpers,
      FollowUpVisitService followUpVisitService,
      Faker faker,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;

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
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "^I validate all fields from Visit",
        () -> {
          final Visit actualVisit = collectTestResultsData();
          ComparisonHelper.compareEqualEntities(visit, actualVisit);
        });

    When(
        "I set Person available and cooperative to ([^\"]*)",
        (String status) -> {
          webDriverHelpers.clickWebElementByText(PERSON_AVAILABLE_AND_COOPERATIVE, status);
          specificVisit = Visit.builder().personAvailableAndCooperative(status).build();
        });

    When(
        "I set Date and time of visit",
        () -> {
          LocalTime time = LocalTime.of(faker.number().numberBetween(10, 23), 30);
          LocalDate date = LocalDate.now().minusDays(faker.number().numberBetween(1, 10));
          fillDateOfVisit(date);
          fillTimeOfVisit(time);
          specificVisit = specificVisit.toBuilder().dateOfVisit(date).timeOfVisit(time).build();
        });

    When(
        "I save the Visit data",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "I save the Symptoms data",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "I check last Person status and date with time",
        () -> {
          visit = collectSpecificResultDataForPersonDateAndTime();
          ComparisonHelper.compareEqualFieldsOfEntities(
              visit,
              specificVisit,
              List.of("personAvailableAndCooperative", "dateOfVisit", "timeOfVisit"));
        });

    When(
        "I fill the specific data of visit with ([^\"]*) option to all symptoms",
        (String parameter) -> {
          visit = followUpVisitService.buildSpecifiedFollowUpVisitForAvailableAndCooperative();
          selectPersonAvailable(
              visit.getPersonAvailableAndCooperative(), AVAILABLE_AND_COOPERATIVE);
          fillDateOfVisit(visit.getDateOfVisit());
          fillTimeOfVisit(visit.getTimeOfVisit());
          fillVisitRemarks(visit.getVisitRemarks(), VISIT_REMARKS);
          selectCurrentTemperature(visit.getCurrentBodyTemperature());
          selectSourceOfTemperature(visit.getSourceOfBodyTemperature());
          webDriverHelpers.clickOnWebElementBySelector(CLEAR_ALL);
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.clickWebElementByText(OPTION_FOR_SET_BUTTONS, parameter);
        });

    When(
        "I fill specific data of symptoms with ([^\"]*) option to all Clinical Signs and Symptoms",
        (String parameter) -> {
          visit = followUpVisitService.buildTemperatureOnlySymptoms("36.6");
          selectCurrentTemperature(visit.getCurrentBodyTemperature());
          selectSourceOfTemperature(visit.getSourceOfBodyTemperature());
          webDriverHelpers.clickOnWebElementBySelector(CLEAR_ALL);
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.clickWebElementByText(OPTION_FOR_SET_BUTTONS, parameter);
          TimeUnit.SECONDS.sleep(2);
        });

    When(
        "I set Other clinican symptomps to ([^\"]*)",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(OTHER_OPTIONS, option);
        });

    When(
        "I check if Specify Other Symptoms field is available and I fill it",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SPECIFY_OTHER_SYMPTOMS);
          webDriverHelpers.fillInWebElement(SPECIFY_OTHER_SYMPTOMS, faker.book().title());
        });

    When(
        "I set Feeling Ill Symptoms to ([^\"]*)",
        (String parameter) -> setFeelingIllSymptoms(parameter));

    When(
        "I set Chills and Sweats Symptoms to ([^\"]*)",
        (String parameter) -> setChillsSweatsSymptoms(parameter));

    When("I set Fever Symptoms to ([^\"]*)", (String parameter) -> setFeverSymptoms(parameter));

    When(
        "I set First Symptom as ([^\"]*)",
        (String parameter) ->
            webDriverHelpers.selectFromCombobox(FIRST_SYMPTOM_COMBOBOX, parameter));

    When(
        "I set Date of symptom onset",
        () -> {
          fillDateOfSymptoms(LocalDate.now());
        });

    When(
        "I set Maximum body temperature as a ([^\"]*)",
        (String temperature) -> {
          visit = followUpVisitService.buildTemperatureOnlySymptoms(temperature);
          selectCurrentTemperature(visit.getCurrentBodyTemperature());
          selectSourceOfTemperature(visit.getSourceOfBodyTemperature());
        });

    When(
        "I check if popup is displayed next to Fever in Symptoms if temperature is >=38",
        () -> {
          String expected =
              "A body temperature of at least 38 C has been specified. It is recommended to also set Fever to \"Yes\".";
          webDriverHelpers.hoverToElement(BLUE_ERROR_EXCLAMATION_MARK);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(BLUE_ERROR_EXCLAMATION_MARK_TEXT);
          softly.assertEquals(expected, (displayedText.replaceAll("\\u00B0", "")));
          softly.assertAll();
        });

    When(
        "I check if popup is displayed next to Fever in Symptoms if temperature is <=38",
        () -> {
          String expected =
              "A body temperature of less than 38 C has been specified. It is recommended to also set Fever to \"No\".";
          webDriverHelpers.hoverToElement(BLUE_ERROR_EXCLAMATION_MARK);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(BLUE_ERROR_EXCLAMATION_MARK_TEXT);
          softly.assertEquals(expected, (displayedText.replaceAll("\\u00B0", "")));
          softly.assertAll();
        });

    When(
        "I check if created data is correctly displayed in Symptoms tab for Set cleared to ([^\"]*)",
        (String parameter) -> {
          followUpVisit = collectDataFromSymptomsTab();
          expectedVisitResults = fillFollowUpVisitDataWithParameter(parameter);
          ComparisonHelper.compareEqualFieldsOfEntities(
              followUpVisit,
              expectedVisitResults,
              List.of(
                  "chillsOrSweats",
                  "feelingIll",
                  "fever",
                  "headache",
                  "musclePain",
                  "shivering",
                  "acuteRespiratoryDistressSyndrome",
                  "cough",
                  "difficultyBreathing",
                  "oxygenSaturation94",
                  "pneumoniaClinicalRadiologic",
                  "rapidBreathing",
                  "respiratoryDiseaseRequiringVentilation",
                  "runnyNose",
                  "soreThroatPharyngitis",
                  "fastHeartRate",
                  "diarrhea",
                  "nausea",
                  "newLossOfSmell",
                  "newLossOfTaste",
                  "otherClinicalSymptoms",
                  "comments",
                  "firstSymptom"));
        });

    When(
        "I clear Clinical Signs and Symptoms list",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CLEAR_ALL);
        });

    When(
        "I am saving clear Clinical Signs and Symptoms list",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM);
        });
  }

  private void selectPersonAvailable(String availableAndCooperative, By element) {
    webDriverHelpers.clickWebElementByText(element, availableAndCooperative);
  }

  private void fillDateOfVisit(LocalDate dateOfVisit) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_OF_VISIT_INPUT, DATE_FORMATTER.format(dateOfVisit));
  }

  private void fillTimeOfVisit(LocalTime timeOfVisit) {
    webDriverHelpers.selectFromCombobox(TIME_OF_VISIT_INPUT, TIME_FORMATTER.format(timeOfVisit));
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

  private void setFeelingIllSymptoms(String parameter) {
    webDriverHelpers.clickWebElementByText(FEELING_ILL_OPTIONS, parameter.toUpperCase());
  }

  private void setChillsSweatsSymptoms(String parameter) {
    webDriverHelpers.clickWebElementByText(CHILLS_SWEATS_OPTIONS, parameter.toUpperCase());
  }

  private void setFeverSymptoms(String parameter) {
    webDriverHelpers.clickWebElementByText(FEVER_OPTIONS, parameter.toUpperCase());
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

  private Visit collectSpecificResultDataForPersonDateAndTime() {
    return Visit.builder()
        .personAvailableAndCooperative(getPersonAvailableAndCooperative())
        .dateOfVisit(getDateOfVisit())
        .timeOfVisit(getTimeOfVisit())
        .build();
  }

  private FollowUpVisit collectDataFromSymptomsTab() {
    return FollowUpVisit.builder()
        .chillsOrSweats(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CHILLS_SWEATS_OPTIONS))
        .feelingIll(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEELING_ILL_OPTIONS))
        .fever(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEVER_OPTIONS))
        .headache(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HEADACHE_OPTIONS))
        .musclePain(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(MUSCLE_PAIN_OPTIONS))
        .shivering(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHIVERING_OPTIONS))
        .acuteRespiratoryDistressSyndrome(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ACUTE_RESPIRATORY_OPTIONS))
        .cough(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COUGH_OPTIONS))
        .difficultyBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                DIFFICULTY_BREATHING_OPTIONS))
        .oxygenSaturation94(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OXYGEN_SATURATION_OPTIONS))
        .pneumoniaClinicalRadiologic(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PNEUMONIA_OPTIONS))
        .rapidBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RAPID_BREATHING_OPTIONS))
        .respiratoryDiseaseRequiringVentilation(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RESPIRATORY_DISEASE_OPTIONS))
        .runnyNose(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RUNNY_NOSE_OPTIONS))
        .soreThroatPharyngitis(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SORE_THROAT_OPTIONS))
        .fastHeartRate(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FAST_HEART_OPTIONS))
        .diarrhea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIARRHEA_OPTIONS))
        .nausea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(NAUSEA_OPTIONS))
        .newLossOfSmell(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_SMELL_OPTIONS))
        .newLossOfTaste(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_TASTE_OPTIONS))
        .otherClinicalSymptoms(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OTHER_OPTIONS))
        .build();
  }

  private FollowUpVisit fillFollowUpVisitDataWithParameter(String parameter) {
    return FollowUpVisit.builder()
        .chillsOrSweats(parameter)
        .feelingIll(parameter)
        .fever(parameter)
        .headache(parameter)
        .musclePain(parameter)
        .shivering(parameter)
        .acuteRespiratoryDistressSyndrome(parameter)
        .cough(parameter)
        .difficultyBreathing(parameter)
        .oxygenSaturation94(parameter)
        .pneumoniaClinicalRadiologic(parameter)
        .rapidBreathing(parameter)
        .respiratoryDiseaseRequiringVentilation(parameter)
        .runnyNose(parameter)
        .soreThroatPharyngitis(parameter)
        .fastHeartRate(parameter)
        .diarrhea(parameter)
        .nausea(parameter)
        .newLossOfSmell(parameter)
        .newLossOfTaste(parameter)
        .otherClinicalSymptoms(parameter)
        .build();
  }

  private String getPersonAvailableAndCooperative() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERSONS_AVAILABLE_OPTIONS);
  }

  private LocalDate getDateOfVisit() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_OF_VISIT_INPUT), DATE_FORMATTER);
  }

  private LocalTime getTimeOfVisit() {
    return LocalTime.parse(
        webDriverHelpers.getValueFromCombobox(TIME_OF_VISIT_INPUT), TIME_FORMATTER);
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
