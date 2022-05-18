package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.*;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_NAME_DESCRIPTION_VALUE;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Immunization;
import org.sormas.e2etests.entities.services.ImmunizationService;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class EditImmunizationSteps implements En {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Immunization collectedImmunization;
  private static Immunization createdImmunization;
  private final WebDriverHelpers webDriverHelpers;

  @SneakyThrows
  @Inject
  public EditImmunizationSteps(
      WebDriverHelpers webDriverHelpers, ImmunizationService immunizationService) {
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
