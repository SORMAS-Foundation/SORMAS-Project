/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.steps.web.application.dashboard.contacts;

import static org.sormas.e2etests.pages.application.dashboard.Surveillance.ContactsDashboardPage.CONTACTS_COVID19_COUNTER;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;

public class ContactsDashboardSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private int covid19ContactsCounterAfter;
  private int covid19ContactsCounterBefore;

  @Inject
  public ContactsDashboardSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I save value for COVID-19 contacts counter in Contacts Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONTACTS_COVID19_COUNTER, 40);
          covid19ContactsCounterBefore =
              Integer.parseInt(webDriverHelpers.getTextFromWebElement(CONTACTS_COVID19_COUNTER));
        });

    Then(
        "^I check that previous saved Contacts Dashboard contact counter for COVID-19 has been incremented$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CONTACTS_COVID19_COUNTER);
          // TODO check if this sleep helps for Jenkins execution, otherwise remove it and create
          // proper handle
          TimeUnit.SECONDS.sleep(5);
          covid19ContactsCounterAfter =
              Integer.parseInt(webDriverHelpers.getTextFromWebElement(CONTACTS_COVID19_COUNTER));
          Assert.assertTrue(
              covid19ContactsCounterBefore < covid19ContactsCounterAfter,
              "COVID-19 contacts counter in Contacts dashboard hasn't  been incremented");
        });
  }
}
