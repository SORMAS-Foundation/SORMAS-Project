package org.sormas.e2etests.steps.web.application.survnet;

import cucumber.api.java8.En;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.WebDriverHelpers;

import javax.inject.Inject;

@Slf4j
public class SurvNetSteps implements En {

  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public SurvNetSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    Then(
        "I check if date of report in generated XML file is correct",
        () -> {

        });
    }
}
