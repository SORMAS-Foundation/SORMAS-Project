package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.LineListingPopup.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Case;
import org.sormas.e2etests.services.CaseService;

public class LineListingSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Case caze;

  @Inject
  public LineListingSteps(WebDriverHelpers webDriverHelpers, CaseService caseService) {
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
          Thread.sleep(3000);
          caze = caseService.buildCaseForLineListingFeature();
          fillSecondFirstName(caze.getFirstName(), 1);
          fillSecondLastName(caze.getLastName(), 1);
          fillSecondDateOfBirth(caze.getDateOfBirth());
          selectSecondSex(caze.getSex());
          fillSecondDateOfSymptom(caze.getDateOfSymptomOnset());
        });

    When(
        "^I save the new case using line listing feature$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_SAVE_BUTTON));
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
}
