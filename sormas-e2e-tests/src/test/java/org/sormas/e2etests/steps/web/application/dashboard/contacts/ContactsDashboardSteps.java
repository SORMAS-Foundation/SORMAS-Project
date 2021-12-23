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
          String covid19ContactsCounterRawValue =
              webDriverHelpers.getTextFromWebElement(
                  ContactsDashboardPage.CONTACTS_COVID19_COUNTER);
          covid19ContactsCounterBefore = Integer.parseInt(covid19ContactsCounterRawValue);
        });

    Then(
        "^I check that previous saved Contacts Dashboard contact counter for COVID-19 has been incremented$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);

          String covid19ContactsCounterRawValueAfter =
              webDriverHelpers.getTextFromWebElement(
                  ContactsDashboardPage.CONTACTS_COVID19_COUNTER);
          covid19ContactsCounterAfter = Integer.parseInt(covid19ContactsCounterRawValueAfter);

          softly.assertThat(covid19ContactsCounterBefore).isLessThan(covid19ContactsCounterAfter);
          softly.assertAll();
        });
  }
}
