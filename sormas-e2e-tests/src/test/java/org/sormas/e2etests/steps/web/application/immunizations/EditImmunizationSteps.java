package org.sormas.e2etests.steps.web.application.immunizations;

import cucumber.api.java8.En;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Immunization;
import org.sormas.e2etests.entities.services.ImmunizationService;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SAVE_EDIT_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.ACTION_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.ARCHIVE_DEARCHIVE_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.BUTTONS_IN_VACCINATIONS_LOCATION;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DELETE_VACCINATION_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_CATEGORY_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_COMBOBOX_IMMUNIZATION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_NAME_DESCRIPTION_VALUE;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_MANAGEMENT_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_PERSON_TAB;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.MEANS_OF_IMMUNIZATIONS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.NUMBER_OF_DOSES;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.POPUP_MESSAGE;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_REGION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.UUID;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_DATE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_DOSE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_ID_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_MANUFACTURER_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_NAME_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_TYPE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.getVaccinationByIndex;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_SAMPLE_REASON_POPUP;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SAMPLE_DELETION_POPUP_YES_BUTTON;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SAVE_EDIT_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.BUTTONS_IN_VACCINATIONS_LOCATION;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DELETE_VACCINATION_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_CATEGORY_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_COMBOBOX_IMMUNIZATION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_NAME_DESCRIPTION_VALUE;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_MANAGEMENT_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.MEANS_OF_IMMUNIZATIONS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.NUMBER_OF_DOSES;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.POPUP_MESSAGE;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_REGION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.UUID;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_DATE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_DOSE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_ID_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_MANUFACTURER_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_NAME_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_TYPE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.getVaccinationByIndex;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_SAMPLE_REASON_POPUP;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SAMPLE_DELETION_POPUP_YES_BUTTON;

public class EditImmunizationSteps implements En {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Immunization collectedImmunization;
  private static Immunization createdImmunization;
  private final WebDriverHelpers webDriverHelpers;

  @SneakyThrows
  @Inject
  public EditImmunizationSteps(
      WebDriverHelpers webDriverHelpers,
      ImmunizationService immunizationService,
      AssertHelpers assertHelpers,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit immunization page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID);
          collectedImmunization = collectImmunizationData();
          createdImmunization = CreateNewImmunizationSteps.immunization;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedImmunization,
              createdImmunization,
              List.of(
                  "disease",
                  "dateOfReport",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "facilityCategory",
                  "facilityType",
                  "facility",
                  "facilityDescription"));
        });
    When(
        "I click on New Entry button in Vaccination tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_ENTRY_BUTTON));
    When(
        "I set Number of doses to {int} on Edit Immunization Page",
        (Integer number) ->
            webDriverHelpers.fillInWebElement(NUMBER_OF_DOSES, String.valueOf(number)));
    When(
        "^I click SAVE button on Edit Immunization Page$",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_EDIT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_EDIT_BUTTON);
          TimeUnit.SECONDS.sleep(3);
        });
    When(
        "^I check if exceeded number of doses error popup message appeared$",
        () -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      webDriverHelpers.getTextFromWebElement(POPUP_MESSAGE),
                      "Vaccine dose should be a number between 1 and 10",
                      "Exceeded number of doses message is incorrect"));
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    When(
        "I check if Immunization management status is set to {string}",
        (String expected) -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(CASE_SAVED_POPUP, 5))
            webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      webDriverHelpers.getValueFromWebElement(IMMUNIZATION_MANAGEMENT_STATUS_INPUT),
                      expected,
                      "Immunization Management status is different than expected"));
        });
    When(
        "I check if Immunization status is set to {string}",
        (String expected) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getValueFromWebElement(IMMUNIZATION_STATUS_INPUT),
                        expected,
                        "Immunization status is different than expected")));
    When(
        "I check that number of added Vaccinations is {int}",
        (Integer expected) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(BUTTONS_IN_VACCINATIONS_LOCATION) - 1,
                        (int) expected,
                        "Number of vaccinations is different than expected")));
    When(
        "I click to edit {int} vaccination on Edit Immunization page",
        (Integer index) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getVaccinationByIndex(String.valueOf(index + 1)));
          webDriverHelpers.clickOnWebElementBySelector(
              getVaccinationByIndex(String.valueOf(index + 1)));
        });
    When(
        "I click Delete button in Vaccination form",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DELETE_VACCINATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_VACCINATION_BUTTON);
        });
    When(
        "I choose the reason of deletion in popup for Vaccination",
        () -> {
          webDriverHelpers.selectFromCombobox(
              DELETE_SAMPLE_REASON_POPUP, "Entity created without legal reason");
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_DELETION_POPUP_YES_BUTTON);
        });
    When(
        "I click the header of column {int} of Vaccination table",
        (Integer col) -> {
          webDriverHelpers.clickOnWebElementBySelector(
              By.xpath("//tr//td[" + col.toString() + "]"));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
        });
    When(
        "I check that ([^\"]*) is visible in Vaccinations tab on Edit Immunization Page",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          boolean elementVisible = true;
          switch (option) {
            case "Vaccination ID":
              selector = VACCINATION_ID_HEADER;
              break;
            case "Vaccination date":
              selector = VACCINATION_DATE_HEADER;
              break;
            case "Vaccine name":
              selector = VACCINATION_NAME_HEADER;
              break;
            case "Vaccine manufacturer":
              selector = VACCINATION_MANUFACTURER_HEADER;
              break;
            case "Vaccine type":
              selector = VACCINATION_TYPE_HEADER;
              break;
            case "Vaccine dose":
              selector = VACCINATION_DOSE_HEADER;
              break;
          }
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(selector);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          Assert.assertTrue(elementVisible, option + " is not visible!");
        });

    When(
        "^I click on archive button from immunization tab$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ARCHIVE_DEARCHIVE_BUTTON);
          webDriverHelpers.scrollToElement(ARCHIVE_DEARCHIVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_DEARCHIVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_BUTTON);
        });

    When(
        "^I check if editable fields are read only for an archived immunization$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          TimeUnit.SECONDS.sleep(15);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(IMMUNIZATION_PERSON_TAB);
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISEASE_INPUT),
              false,
              "Disease input is not in read only state!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(MEANS_OF_IMMUNIZATIONS_INPUT),
              false,
              "Means of immunization input is not in read only state!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_REGION_INPUT),
              false,
              "Responsible region input is not in read only state!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_DISTRICT_INPUT),
              false,
              "Responsible district input is not in read only state!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_COMMUNITY_INPUT),
              false,
              "Responsible community input is not in read only state!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DATE_OF_REPORT_INPUT),
              false,
              "Date of report input is not in read only state!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISCARD_BUTTON),
              false,
              "Discard button is not in read only state!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(COMMIT_BUTTON),
              false,
              "Commit button is not in read only state!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DELETE_BUTTON),
              false,
              "Delete button is not in read only state!");
          softly.assertAll();
        });
  }

  private Immunization collectImmunizationData() {
    return Immunization.builder()
        .dateOfReport(getDateOfReport())
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .meansOfImmunization(webDriverHelpers.getValueFromWebElement(MEANS_OF_IMMUNIZATIONS_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_COMMUNITY_INPUT))
        .facilityDescription(
            webDriverHelpers.getValueFromWebElement(FACILITY_NAME_DESCRIPTION_VALUE))
        .facilityCategory(webDriverHelpers.getValueFromWebElement(FACILITY_CATEGORY_INPUT))
        .facilityType(webDriverHelpers.getValueFromWebElement(FACILITY_TYPE_INPUT))
        .facility(webDriverHelpers.getValueFromWebElement(FACILITY_COMBOBOX_IMMUNIZATION_INPUT))
        .build();
  }

  private LocalDate getDateOfReport() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(DATE_OF_REPORT_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }
}
