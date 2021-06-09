package org.sormas.e2etests.steps.web.application.contacts;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Contact;
import org.sormas.e2etests.pojo.web.ExposureInvestigation;
import org.sormas.e2etests.pojo.web.Symptoms;
import org.sormas.e2etests.services.ContactService;
import org.sormas.e2etests.services.ExposureInvestigationService;
import org.sormas.e2etests.services.SymptomService;
import org.sormas.e2etests.state.ApiState;

import javax.inject.Inject;
import javax.inject.Named;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.*;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.SAVE_BUTTON;

public class ExposureInContactEpiDataSteps implements En {

    private final WebDriverHelpers webDriverHelpers;
    public static ExposureInvestigation exposureInvestigationInput;
    @Inject
    public ExposureInContactEpiDataSteps(WebDriverHelpers webDriverHelpers,
                                         ExposureInvestigationService exposureInvestigationService,
                                         ApiState apiState,
                                         @Named("ENVIRONMENT_URL") String environmentUrl) {
        this.webDriverHelpers = webDriverHelpers;

        When (
            "I am accessing the Epidemiological tab using of created contact via api",
            () -> {
                String EPIDATA_FOR_LAST_CREATED_CONTACT_URL =
                        environmentUrl + "/sormas-ui/#!contacts/epidata/" + apiState.getCreatedContact().getUuid();
                webDriverHelpers.accessWebSite(EPIDATA_FOR_LAST_CREATED_CONTACT_URL);
            });

        Then (
                "I check and fill all data",
                () -> {
                    exposureInvestigationInput = exposureInvestigationService.buildInputExposureInvestigation();
                    checkExposureDetailsKnown(exposureInvestigationInput.getExposureDetailsKnown());
                    checkHighTransmissionRiskArea(exposureInvestigationInput.getHighTransmissionRiskArea());
                    checkLargeOutbreaksArea(exposureInvestigationInput.getLargeOutbreaksArea());
                });
        Then (
                "I click on save",
                () -> {
                    webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
                });
        Then (
                "I am accessing the contacts",
                () -> {

                });
        When (
                "I am accessing the Epidemiological tab using of created contact via api",
                () -> {

                });
        When (
                "I am checking all data is saved and displayed",
                () -> {

                });

        }

    public void checkExposureDetailsKnown(String knownStatus) {
        webDriverHelpers.clickWebElementByText(EXPOSURE_DETAILS_KNOWN, knownStatus);
    }

    public void checkHighTransmissionRiskArea(String riskArea) {
        webDriverHelpers.clickWebElementByText(HIGH_TRANSMISSION_RISK_AREA, riskArea);
    }

    public void checkLargeOutbreaksArea(String largeOutbreaks) {
        webDriverHelpers.clickWebElementByText(LARGE_OUTBREAKS_AREA, largeOutbreaks);
    }

}
