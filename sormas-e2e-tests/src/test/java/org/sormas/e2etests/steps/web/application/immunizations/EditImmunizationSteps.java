package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.*;
import static org.sormas.e2etests.pages.application.immunizations.CreateNewImmunizationPage.MEANS_OF_IMMUNIZATIONS_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_COMBOBOX_IMMUNIZATION_INPUT;
import static org.sormas.e2etests.pages.application.users.EditUserPage.FACILITY_CATEGORY_COMBOBOX_INPUT;
import static org.sormas.e2etests.pages.application.users.EditUserPage.FACILITY_TYPE_COMBOBOX_INPUT;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Immunization;
import org.sormas.e2etests.services.ImmunizationService;

public class EditImmunizationSteps implements En {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Immunization aImmunization;
  private final WebDriverHelpers webDriverHelpers;

  @SneakyThrows
  @Inject
  public EditImmunizationSteps(
      WebDriverHelpers webDriverHelpers,
      ImmunizationService immunizationService,
      @Named("ENVIRONMENT_URL") String environmentUrl) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit immunization page",
        () -> {
          aImmunization = collectImmunizationData();
          Truth.assertThat(aImmunization.getDisease())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getDisease());
          Truth.assertThat(aImmunization.getDateOfReport())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getDateOfReport());
          Truth.assertThat(aImmunization.getResponsibleRegion())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getResponsibleRegion());
          Truth.assertThat(aImmunization.getResponsibleDistrict())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getResponsibleDistrict());
          Truth.assertThat(aImmunization.getResponsibleCommunity())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getResponsibleCommunity());
          Truth.assertThat(aImmunization.getFacilityCategory())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getFacilityCategory());
          Truth.assertThat(aImmunization.getFacilityType())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getFacilityType());
          Truth.assertThat(aImmunization.getFacility())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getFacility());
          Truth.assertThat(aImmunization.getFacilityDescription())
              .isEqualTo(CreateNewImmunizationSteps.immunization.getFacilityDescription());
        });
  }

  public Immunization collectImmunizationData() {
    return Immunization.builder()
        .dateOfReport(getDateOfReport())
        .disease(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        .meansOfImmunization(webDriverHelpers.getValueFromCombobox(MEANS_OF_IMMUNIZATIONS_COMBOBOX))
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        .facilityCategory(webDriverHelpers.getValueFromWebElement(FACILITY_CATEGORY_COMBOBOX_INPUT))
        .facilityType(webDriverHelpers.getValueFromWebElement(FACILITY_TYPE_COMBOBOX_INPUT))
        .facility(webDriverHelpers.getValueFromWebElement(FACILITY_COMBOBOX_IMMUNIZATION_INPUT))
        .build();
  }

  private LocalDate getDateOfReport() {

    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }
}
