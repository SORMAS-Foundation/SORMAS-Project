package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.ExposureDetails;
import org.sormas.e2etests.pojo.web.ExposureInvestigation;
import org.sormas.e2etests.services.ExposureDetailsService;
import org.sormas.e2etests.services.ExposureInvestigationService;
import org.sormas.e2etests.state.ApiState;

public class ExposureInContactEpiDataSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static ExposureInvestigation exposureInvestigationInput;
  public static ExposureDetails exposureDetailsInput;

  @Inject
  public ExposureInContactEpiDataSteps(
      WebDriverHelpers webDriverHelpers,
      ExposureInvestigationService exposureInvestigationService,
      ExposureDetailsService exposureDetailsService,
      ApiState apiState,
      @Named("ENVIRONMENT_URL") String environmentUrl) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I am accessing the Epidemiological tab using of created contact via api",
        () -> {
          String EPIDATA_FOR_LAST_CREATED_CONTACT_URL =
              environmentUrl
                  + "/sormas-ui/#!contacts/epidata/"
                  + apiState.getCreatedContact().getUuid();
          webDriverHelpers.accessWebSite(EPIDATA_FOR_LAST_CREATED_CONTACT_URL);
          Thread.sleep(30000);
        });

    Then(
        "I check and fill all data",
        () -> {
          exposureInvestigationInput =
              exposureInvestigationService.buildInputExposureInvestigation();
          exposureIvestigationOnContact(exposureInvestigationInput);
          // exposureDetailsInput = exposureDetailsService.buildInputExposureDetails();

          Thread.sleep(30000);
        });
    Then(
        "I click on save",
        () -> {
          // webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
    Then("I am accessing the contacts", () -> {});

    When("I am checking all data is saved and displayed", () -> {});
  }

  public void exposureIvestigationOnContact(ExposureInvestigation exposureInvestigationInput) {
    webDriverHelpers.clickWebElementByText(
        EXPOSURE_DETAILS_KNOWN, exposureInvestigationInput.getExposureDetailsKnown().toString());
    webDriverHelpers.clickWebElementByText(
        HIGH_TRANSMISSION_RISK_AREA,
        exposureInvestigationInput.getHighTransmissionRiskArea().toString());
    webDriverHelpers.clickWebElementByText(
        LARGE_OUTBREAKS_AREA, exposureInvestigationInput.getLargeOutbreaksArea());
    if (exposureInvestigationInput.getExposureNewEntry()) {
      webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY);
    }
  }
}
