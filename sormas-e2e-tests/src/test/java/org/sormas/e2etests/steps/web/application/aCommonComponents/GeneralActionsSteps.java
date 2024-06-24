/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.steps.web.application.aCommonComponents;

import static org.sormas.e2etests.pages.application.aCommonComponents.GeneralActions.READ_ONLY_FIELDS;
import static org.sormas.e2etests.pages.application.aCommonComponents.GeneralActions.READ_ONLY_FIELDS_SURVNET_DETAILS;
import static org.sormas.e2etests.pages.application.aCommonComponents.SideCards.*;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class GeneralActionsSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Faker faker;
  private final BaseSteps baseSteps;

  /**
   * This class contains general methods applicable anywhere Please don't add a view location in
   * their name as they should be general
   */
  @SneakyThrows
  @Inject
  public GeneralActionsSteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, BaseSteps baseSteps, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;
    this.baseSteps = baseSteps;
    When(
        "Total number of read only fields should be {int}",
        (Integer number) -> {
          TimeUnit.SECONDS.sleep(4); // waiting for page loaded
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(READ_ONLY_FIELDS),
              number.intValue(),
              " text is not present in handover component");
          softly.assertAll();
        });

    When(
        "Total number of read only fields in Survnet details section should be {int}",
        (Integer number) -> {
          TimeUnit.SECONDS.sleep(3); // waiting for page loaded
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(READ_ONLY_FIELDS_SURVNET_DETAILS),
              number.intValue(),
              " text is not present in handover component");
          softly.assertAll();
        });
    When("I refresh current page", () -> webDriverHelpers.refreshCurrentPage());
  }
}
