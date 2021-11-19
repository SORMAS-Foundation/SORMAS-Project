package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.cases.LineListingPopup.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Case;
import org.sormas.e2etests.services.CaseService;

public class CaseLineListingSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Case caze;
  protected static Case secondCaze;

  @Inject
  public CaseLineListingSteps(
      WebDriverHelpers webDriverHelpers, CaseService caseService, final SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new case using line listing feature$",
        () -> {
          caze = caseService.buildCaseForLineListingFeature();
          selectDisease(caze.getDisease());
          selectRegion(caze.getRegion());
          selectDistrict(caze.getDistrict());
          selectFacilityCategory(caze.getFacilityCategory());
          selectFacilityType(caze.getFacilityType());
          fillDateOfReport(caze.getDateOfReport());
          selectCommunity(caze.getCommunity());
          selectFacility();
          fillFacilityName(caze.getPlaceDescription());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth());
          selectSex(caze.getSex());
          fillDateOfSymptom(caze.getDateOfSymptomOnset());
        });

    When(
        "^I click on add line button$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_ADD_LINE_BUTTON));

    When(
        "^I create the second case using listing feature in new line$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SECOND_DATE_OF_SYMPTOM_INPUT);
          secondCaze = caseService.buildCaseForLineListingFeature();
          fillSecondFirstName(secondCaze.getFirstName(), 1);
          fillSecondLastName(secondCaze.getLastName(), 1);
          fillSecondDateOfBirth(secondCaze.getDateOfBirth());
          selectSecondSex(secondCaze.getSex());
          fillSecondDateOfSymptom(secondCaze.getDateOfSymptomOnset());
        });

    When(
        "^I save the new case using line listing feature$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
        });

    When(
        "I am checking all data created from Case Line Listing option is saved and displayed",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          softly
              .assertThat(getCaseDiseaseFromGridResults())
              .isEqualToIgnoringCase(caze.getDisease());
          softly.assertThat(getCaseFirstNameFromGridResults()).isEqualTo(secondCaze.getFirstName());
          softly.assertThat(getCaseLastNameFromGridResults()).isEqualTo(secondCaze.getLastName());
          softly.assertThat(getCaseDistrictFromGridResults()).isEqualTo(caze.getDistrict());
          softly
              .assertThat(getCaseHealthFacilityFromGridResults())
              .isEqualToIgnoringCase("Other Facility - " + caze.getPlaceDescription());
          softly.assertAll();
        });
  }

  public void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  public void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  public void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  public void selectFacilityCategory(String facilityCategory) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facilityCategory);
  }

  public void selectFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  public void fillDateOfReport(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT, formatter.format(date));
  }

  public void selectCommunity(String community) {
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, community);
  }

  public void selectFacility() {
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, "Other facility");
  }

  public void fillFacilityName(String facilityName) {
    webDriverHelpers.fillInWebElement(FACILITY_NAME_INPUT, facilityName);
  }

  public void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  public void fillSecondFirstName(String firstName, int index) {
    webDriverHelpers.fillValueOfListElement(FIRST_NAME_INPUT, index, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  public void fillSecondLastName(String lastName, int index) {
    webDriverHelpers.fillValueOfListElement(LAST_NAME_INPUT, index, lastName);
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

  public void fillSecondDateOfBirth(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        SECOND_BIRTHDATE_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        SECOND_BIRTHDATE_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    webDriverHelpers.selectFromCombobox(
        SECOND_BIRTHDATE_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  public void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX, sex);
  }

  public void selectSecondSex(String sex) {
    webDriverHelpers.selectFromCombobox(SECOND_SEX, sex);
  }

  public void fillDateOfSymptom(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_INPUT, formatter.format(date));
  }

  public void fillSecondDateOfSymptom(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(SECOND_DATE_OF_SYMPTOM_INPUT, formatter.format(date));
  }

  public String getCaseDiseaseFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_DISEASE, 0);
  }

  public String getCaseFirstNameFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_FIRST_NAME, 0);
  }

  public String getCaseLastNameFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_LAST_NAME, 0);
  }

  public String getCaseDistrictFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_DISTRICT, 0);
  }

  public String getCaseHealthFacilityFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_HEALTH_FACILITY, 0);
  }
}
