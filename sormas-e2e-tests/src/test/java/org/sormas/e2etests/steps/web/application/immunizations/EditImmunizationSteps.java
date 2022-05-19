package org.sormas.e2etests.steps.web.application.immunizations;

import cucumber.api.java8.En;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Immunization;
import org.sormas.e2etests.entities.services.ImmunizationService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.ACTION_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.ARCHIVE_DEARCHIVE_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_CATEGORY_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_COMBOBOX_IMMUNIZATION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_NAME_DESCRIPTION_VALUE;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_PERSON_TAB;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.MEANS_OF_IMMUNIZATIONS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_REGION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.UUID;

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
          //          softly.assertAll();
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
