package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.*;
import static org.sormas.e2etests.pages.application.immunizations.CreateNewImmunizationPage.MEANS_OF_IMMUNIZATIONS_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_COMBOBOX_IMMUNIZATION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_NAME_DESCRIPTION_VALUE;
import static org.sormas.e2etests.pages.application.users.EditUserPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Immunization;
import org.sormas.e2etests.services.ImmunizationService;

public class EditImmunizationSteps implements En {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
    public static Immunization aImmunization;
    private final WebDriverHelpers webDriverHelpers;
    private final SoftAssertions softly;

    @SneakyThrows
    @Inject
    public EditImmunizationSteps(
            WebDriverHelpers webDriverHelpers,
            ImmunizationService immunizationService,
            SoftAssertions softly,
            @Named("ENVIRONMENT_URL") String environmentUrl) {
        this.webDriverHelpers = webDriverHelpers;
        this.softly = softly;

        When(
                "I check the created data is correctly displayed on Edit immunization page",
                () -> {
                    aImmunization = collectImmunizationData();

                    softly
                            .assertThat(aImmunization.getDisease())
                            .withFailMessage("Immunization Disease: %s is not equal with %s", aImmunization.getDisease(), CreateNewImmunizationSteps.immunization.getDisease())
                            .isEqualToIgnoringCase(CreateNewImmunizationSteps.immunization.getDisease());
                    softly
                            .assertThat(aImmunization.getDateOfReport())
                            .withFailMessage("Immunization Date of Report: %s is not equal with %s", aImmunization.getDateOfReport(), CreateNewImmunizationSteps.immunization.getDateOfReport())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getDateOfReport());
                    softly
                            .assertThat(aImmunization.getResponsibleRegion())
                            .withFailMessage("Immunization Responsible Region: %s is not equal with %s", aImmunization.getResponsibleRegion(), CreateNewImmunizationSteps.immunization.getResponsibleRegion())
                            .isEqualToIgnoringCase(
                                    CreateNewImmunizationSteps.immunization.getResponsibleRegion());
                    softly
                            .assertThat(aImmunization.getResponsibleDistrict())
                            .withFailMessage("Immunization Responsible District: %s is not equal with %s", aImmunization.getResponsibleDistrict(), CreateNewImmunizationSteps.immunization.getResponsibleDistrict())
                            .isEqualToIgnoringCase(
                                    CreateNewImmunizationSteps.immunization.getResponsibleDistrict());
                    softly
                            .assertThat(aImmunization.getResponsibleCommunity())
                            .withFailMessage("Immunization Responsible Community: %s is not equal with %s", aImmunization.getResponsibleCommunity(), CreateNewImmunizationSteps.immunization.getResponsibleCommunity())
                            .isEqualToIgnoringCase(
                                    CreateNewImmunizationSteps.immunization.getResponsibleCommunity());
                    softly
                            .assertThat(aImmunization.getFacilityCategory())
                            .withFailMessage("Immunization Facility Category: %s is not equal with %s", aImmunization.getFacilityCategory(), CreateNewImmunizationSteps.immunization.getFacilityCategory())
                            .isEqualToIgnoringCase(CreateNewImmunizationSteps.immunization.getFacilityCategory());
                    softly
                            .assertThat(aImmunization.getFacilityType())
                            .withFailMessage("Immunization Facility Type: %s is not equal with %s", aImmunization.getFacilityType(), CreateNewImmunizationSteps.immunization.getFacilityType())
                            .isEqualToIgnoringCase(CreateNewImmunizationSteps.immunization.getFacilityType());
                    softly
                            .assertThat(aImmunization.getFacility())
                            .withFailMessage("Immunization Facility: %s is not equal with %s", aImmunization.getFacility(), CreateNewImmunizationSteps.immunization.getFacility())
                            .isEqualToIgnoringCase(CreateNewImmunizationSteps.immunization.getFacility());
                    softly
                            .assertThat(aImmunization.getFacilityDescription())
                            .withFailMessage("Immunization Facility Description: %s is not equal with %s", aImmunization.getFacilityDescription(), CreateNewImmunizationSteps.immunization.getFacilityDescription())
                            .isEqualToIgnoringCase(
                                    CreateNewImmunizationSteps.immunization.getFacilityDescription());
                    softly.assertAll();
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
                .facilityDescription(
                        webDriverHelpers.getValueFromWebElement(FACILITY_NAME_DESCRIPTION_VALUE))
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
