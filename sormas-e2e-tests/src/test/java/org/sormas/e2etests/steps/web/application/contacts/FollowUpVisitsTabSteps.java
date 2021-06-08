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

package org.sormas.e2etests.steps.web.application.contacts;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pojo.web.FollowUpVisit;
import org.sormas.e2etests.services.FollowUpVisitService;
import org.sormas.e2etests.state.ApiState;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.sormas.e2etests.pages.application.contacts.CreateNewVisitPage.*;
import static org.sormas.e2etests.pages.application.contacts.FollowUpVisitsPage.*;

public class FollowUpVisitsTabSteps implements En {

    private final WebDriverHelpers webDriverHelpers;
    public static FollowUpVisit followUpVisit;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    @Inject
    public FollowUpVisitsTabSteps(
            WebDriverHelpers webDriverHelpers,
            FollowUpVisitService followUpVisitService,
            ApiState apiState,
            @Named("ENVIRONMENT_URL") String environmentUrl) {
        this.webDriverHelpers = webDriverHelpers;

        When(
                "I am checking all data is saved and displayed",
                () -> {
                });

        When("^I am accessing the Follow-up visits tab using of created contact via api$", () -> {
            webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                    NavBarPage.SAMPLE_BUTTON);
            String caseLinkPath = "/sormas-ui/#!contacts/visits/";
            String uuid = apiState.getCreatedContact().getUuid();
            webDriverHelpers.accessWebSite(environmentUrl + caseLinkPath + uuid);
        });
    }

    public FollowUpVisit collectFollowUpData() {
        return FollowUpVisit.builder()
                .personAvailableAndCooperative(
                        webDriverHelpers.getValueFromWebElement(PERSON_AVAILABLE_AND_COOPERATIVE))
                .firstSymptom(webDriverHelpers.getValueFromCombobox(FIRST_SYMPTOM_COMBOBOX))
                .build();
    }

    private LocalDate getDateOfVisit() {
        String dateOfReport = webDriverHelpers.getValueFromWebElement(DATE_AND_TIME_OF_VISIT_INPUT);
        return LocalDate.parse(dateOfReport, DATE_FORMATTER);
    }

    public void selectSourceOfBodyTemperature(String sourceOfBodyTemperature) {
        webDriverHelpers.selectFromCombobox(
                SOURCE_OF_BODY_TEMPERATURE_COMBOBOX, sourceOfBodyTemperature);
    }

    public void selectChillsOrSweats(String chillsOrSweats) {
        webDriverHelpers.clickWebElementByText(CHILLS_OR_SWATS_LABEL, chillsOrSweats);
    }

    public void selectHeadache(String headache) {
        webDriverHelpers.clickWebElementByText(HEADACHE_LABEL, headache);
    }

    public void selectFeelingIll(String feelingIll) {
        webDriverHelpers.clickWebElementByText(FEELING_ILL_LABEL, feelingIll);
    }

    public void selectMusclePain(String musclePain) {
        webDriverHelpers.clickWebElementByText(MUSCLE_PAIN_LABEL, musclePain);
    }

//    public void selectFever(String fever) {
//        webDriverHelpers.clickWebElementByText(, fever);
//    }

    public void selectShivering(String shivering) {
        webDriverHelpers.clickWebElementByText(SHIVERING_LABEL, shivering);
    }

//    public void selectAcuteRespiratoryDistressSyndrome(String acuteRespiratoryDistressSyndrome) {
//        webDriverHelpers.clickWebElementByText(
//                , acuteRespiratoryDistressSyndrome);
//    }

    public void selectOxygenSaturationLower94(String oxygenSaturationLower94) {
        webDriverHelpers.clickWebElementByText(
                OXIGEN_SATURANTION_LABEL, oxygenSaturationLower94);
    }

    public void selectPneumoniaClinicalOrRadiologic(String pneumoniaClinicalOrRadiologic) {
        webDriverHelpers.clickWebElementByText(
                PNEUMONIA_LABEL, pneumoniaClinicalOrRadiologic);
    }

//    public void selectDifficultyBreathing(String difficultyBreathing) {
//        webDriverHelpers.clickWebElementByText(, difficultyBreathing);
//    }

    public void selectRapidBreathing(String rapidBreathing) {
        webDriverHelpers.clickWebElementByText(RAPID_BREATHING_LABEL, rapidBreathing);
    }

    public void selectRespiratoryDiseaseVentilation(String respiratoryDiseaseVentilation) {
        webDriverHelpers.clickWebElementByText(
                RESPIRATORY_DISEASE_REQUIRING_VENTILATION_LABEL, respiratoryDiseaseVentilation);
    }

    public void selectRunnyNose(String runnyNose) {
        webDriverHelpers.clickWebElementByText(RUNNY_NOSE_LABEL, runnyNose);
    }

    public void fillDateOfSymptomOnset(LocalDate dateOfSymptomOnset) {
        webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_ONSET_INPUT, DATE_FORMATTER.format(dateOfSymptomOnset));
    }
}
