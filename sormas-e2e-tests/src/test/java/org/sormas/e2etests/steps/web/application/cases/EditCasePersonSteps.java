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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_PERSON_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePersonPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.pojo.web.Case;

public class EditCasePersonSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Case collectedCase;
  protected Case createdCase;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM/d/yyyy");

  @Inject
  public EditCasePersonSteps(final WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit case person page",
        () -> {
          collectedCase = collectCasePersonData();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              createdCase,
              collectedCase,
              List.of(
                  "firstName",
                  "lastName",
                  "presentConditionOfPerson",
                  "sex",
                  "passportNumber",
                  "nationalHealthId",
                  "primaryEmailAddress",
                  "dateOfBirth"));
        });
  }

  private Case collectCasePersonData() {
    webDriverHelpers.scrollToElement(CASE_PERSON_TAB);
    webDriverHelpers.clickOnWebElementBySelector(CASE_PERSON_TAB);
    return Case.builder()
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

  private LocalDate getUserBirthDate() {
    final String year = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_YEAR_INPUT);
    final String month = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_MONTH_INPUT);
    final String day = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_DAY_INPUT);
    final String date = month + "/" + day + "/" + year;
    return LocalDate.parse(date, DATE_FORMATTER);
  }
}
