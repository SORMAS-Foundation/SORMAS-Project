package org.sormas.e2etests.steps.web.application.immunizations;

import cucumber.api.java8.En;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Immunization;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_PERSON_TAB;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPersonPage.*;

public class EditImmunizationPersonSteps implements En {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM/d/yyyy");
    private final WebDriverHelpers webDriverHelpers;
    protected Immunization aImmunization;

    @Inject
    public EditImmunizationPersonSteps(
            final WebDriverHelpers webDriverHelpers, final SoftAssertions softly) {
        this.webDriverHelpers = webDriverHelpers;

        When(
                "I check the created data is correctly displayed on Edit immunization person page",
                () -> {
                    aImmunization = collectImmunizationPersonData();
                    softly
                            .assertThat(aImmunization.getFirstName())
                            .withFailMessage("Immunization Person First Name: %s is not equal with %s", aImmunization.getFirstName(), CreateNewImmunizationSteps.immunization.getFirstName())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getFirstName());
                    softly
                            .assertThat(aImmunization.getLastName())
                            .withFailMessage("Immunization Person Last Name: %s is not equal with %s", aImmunization.getLastName(), CreateNewImmunizationSteps.immunization.getLastName())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getLastName());
                    softly
                            .assertThat(aImmunization.getPresentConditionOfPerson())
                            .withFailMessage("Immunization Presented Condition of Person: %s is not equal with %s", aImmunization.getPresentConditionOfPerson(), CreateNewImmunizationSteps.immunization.getPresentConditionOfPerson())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getPresentConditionOfPerson());
                    softly
                            .assertThat(aImmunization.getSex())
                            .withFailMessage("Immunization Person Sex: %s is not equal with %s", aImmunization.getSex(), CreateNewImmunizationSteps.immunization.getSex())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getSex());
                    softly
                            .assertThat(aImmunization.getPassportNumber())
                            .withFailMessage("Immunization Person Passport Number: %s is not equal with %s", aImmunization.getPassportNumber(), CreateNewImmunizationSteps.immunization.getPassportNumber())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getPassportNumber());
                    softly
                            .assertThat(aImmunization.getNationalHealthId())
                            .withFailMessage("Immunization Person National Health Id: %s is not equal with %s", aImmunization.getNationalHealthId(), CreateNewImmunizationSteps.immunization.getNationalHealthId())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getNationalHealthId());
                    softly
                            .assertThat(aImmunization.getPrimaryEmailAddress())
                            .withFailMessage("Immunization Primary Email Address: %s is not equal with %s", aImmunization.getPrimaryEmailAddress(), CreateNewImmunizationSteps.immunization.getPrimaryEmailAddress())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getPrimaryEmailAddress());
                    softly
                            .assertThat(aImmunization.getDateOfBirth())
                            .withFailMessage("Immunization Person Date od Birth: %s is not equal with %s", aImmunization.getDateOfBirth(), CreateNewImmunizationSteps.immunization.getDateOfBirth())
                            .isEqualTo(CreateNewImmunizationSteps.immunization.getDateOfBirth());
                    softly.assertAll();
                });
    }

    public Immunization collectImmunizationPersonData() {
        webDriverHelpers.scrollToElement(IMMUNIZATION_PERSON_TAB);
        webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_PERSON_TAB);
        return Immunization.builder()
                .firstName(webDriverHelpers.getValueFromWebElement(FIRST_NAME_INPUT))
                .lastName(webDriverHelpers.getValueFromWebElement(LAST_NAME_INPUT))
                .dateOfBirth(getUserBirthDate())
                .presentConditionOfPerson(webDriverHelpers.getValueFromWebElement(PRESENT_CONDITION_INPUT))
                .sex(webDriverHelpers.getValueFromWebElement(SEX_INPUT))
                .passportNumber(webDriverHelpers.getValueFromWebElement(PASSPORT_NUMBER_INPUT))
                .nationalHealthId(webDriverHelpers.getValueFromWebElement(NATIONAL_HEALTH_ID_INPUT))
                .primaryPhoneNumber(webDriverHelpers.getTextFromPresentWebElement(PHONE_FIELD))
                .primaryEmailAddress(webDriverHelpers.getTextFromPresentWebElement(EMAIL_FIELD))
                .build();
    }

    public LocalDate getUserBirthDate() {
        final String year = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_YEAR_INPUT);
        final String month = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_MONTH_INPUT);
        final String day = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_DAY_INPUT);
        final String date = month + "/" + day + "/" + year;
        return LocalDate.parse(date, DATE_FORMATTER);
    }
}
