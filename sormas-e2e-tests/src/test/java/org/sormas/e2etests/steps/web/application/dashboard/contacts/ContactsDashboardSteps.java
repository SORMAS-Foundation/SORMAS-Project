package org.sormas.e2etests.steps.web.application.dashboard.contacts;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.ContactsDashboardPage;

public class ContactsDashboardSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final SoftAssertions softly;
  private int covid19ContactsCounterAfter;
  private int covid19ContactsCounterBefore;

  @Inject
  public ContactsDashboardSteps(WebDriverHelpers webDriverHelpers, SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.softly = softly;

    When(
        "^I save value for COVID-19 contacts counter in Contacts Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);

          covid19ContactsCounterBefore =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      ContactsDashboardPage.CONTACTS_COVID19_COUNTER));
        });

    Then(
        "^I check that previous saved Contacts Dashboard contact counter for COVID-19 has been incremented$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);

          covid19ContactsCounterAfter =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      ContactsDashboardPage.CONTACTS_COVID19_COUNTER));

          softly
              .assertThat(covid19ContactsCounterBefore)
              .withFailMessage(
                  "COVID-19 contacts counter in Contacts dashboard has not been increased")
              .isLessThan(covid19ContactsCounterAfter);
          softly.assertAll();
        });
  }
}
