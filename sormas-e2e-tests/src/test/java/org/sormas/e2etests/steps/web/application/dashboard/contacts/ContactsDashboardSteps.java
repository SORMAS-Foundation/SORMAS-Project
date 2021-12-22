package org.sormas.e2etests.steps.web.application.dashboard.contacts;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.ContactsDashboardPage;

public class ContactsDashboardSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final SoftAssertions softly;
  private Integer covid19ContactsCounterIntValueAfter;
  private Integer covid19ContactsCounterIntValueBefore;

  @Inject
  public ContactsDashboardSteps(WebDriverHelpers webDriverHelpers, SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.softly = softly;

    When(
        "^I save value for COVID contacts counter$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
          String covid19ContactsCounterRawValue =
              webDriverHelpers.getTextFromWebElement(
                  ContactsDashboardPage.CONTACTS_COVID19_COUNTER);
          covid19ContactsCounterIntValueBefore = Integer.valueOf(covid19ContactsCounterRawValue);
        });

    Then(
        "^I check that previous saved contact counter has been incremented$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);

          String covid19ContactsCounterRawValueAfter =
              webDriverHelpers.getTextFromWebElement(
                  ContactsDashboardPage.CONTACTS_COVID19_COUNTER);
          covid19ContactsCounterIntValueAfter =
              Integer.valueOf(covid19ContactsCounterRawValueAfter);

          softly
              .assertThat(covid19ContactsCounterIntValueBefore)
              .isLessThan(covid19ContactsCounterIntValueAfter);
          softly.assertAll();
        });
  }
}
