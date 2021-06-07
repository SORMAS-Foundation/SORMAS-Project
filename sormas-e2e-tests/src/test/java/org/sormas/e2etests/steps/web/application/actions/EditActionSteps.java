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

package org.sormas.e2etests.steps.web.application.actions;

import static org.sormas.e2etests.pages.application.actions.EditActionPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Action;

public class EditActionSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Action action;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public EditActionSteps(WebDriverHelpers webDriverHelpers, SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check that Action created from Event tab is correctly displayed in Event Actions tab",
        () -> {
          action = CreateNewActionSteps.action;
          Action collectedAction = collectActionData();
          softly.assertThat(action.getDate().equals(collectedAction.getDate()));
          softly.assertThat(action.getPriority().equals(collectedAction.getPriority()));
          softly.assertThat(action.getMeasure().equals(collectedAction.getMeasure()));
          softly.assertThat(action.getTitle().equals(collectedAction.getTitle()));
          softly.assertThat(action.getActionStatus().equals(collectedAction.getActionStatus()));
          softly.assertAll();
        });
  }

  public Action collectActionData() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(DATE_INPUT);

    return Action.builder()
        .date(LocalDate.parse(collectedDateOfReport, DATE_FORMATTER))
        .priority(webDriverHelpers.getValueFromCombobox(PRIORITY_COMBOBOX))
        .measure(webDriverHelpers.getValueFromCombobox(MEASURE_COMBOBOX))
        .title(webDriverHelpers.getValueFromWebElement(TITLE_INPUT))
        .actionStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ACTION_STATUS_OPTIONS))
        .build();
  }
}
