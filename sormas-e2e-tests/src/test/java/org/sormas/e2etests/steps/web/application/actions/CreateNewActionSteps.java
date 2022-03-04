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

package org.sormas.e2etests.steps.web.application.actions;

import static org.sormas.e2etests.pages.application.actions.CreateNewActionPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.web.Action;
import org.sormas.e2etests.entities.services.ActionService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;

public class CreateNewActionSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  public static Action action;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public CreateNewActionSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ActionService actionService,
      ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "I create New Action from event tab",
        () -> {
          action = actionService.buildGeneratedAction();
          apiState.setCreatedAction(action);
          fillDate(action.getDate());
          selectPriority(action.getPriority());
          selectMeasure(action.getMeasure());
          fillTitle(action.getTitle());
          fillDescription(action.getDescription());
          selectActionStatus(action.getActionStatus());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
  }

  private void fillDate(LocalDate date) {
    webDriverHelpers.fillInWebElement(DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectPriority(String priority) {
    webDriverHelpers.selectFromCombobox(PRIORITY_COMBOBOX, priority);
  }

  private void selectMeasure(String measure) {
    webDriverHelpers.selectFromCombobox(MEASURE_COMBOBOX, measure);
  }

  private void fillTitle(String title) {
    webDriverHelpers.fillInWebElement(TITLE_INPUT, title);
  }

  private void fillDescription(String description) {
    WebElement iFrame = baseSteps.getDriver().findElement(DESCRIPTION_IFRAME);
    baseSteps.getDriver().switchTo().frame(iFrame);
    baseSteps.getDriver().findElement(By.cssSelector("body")).sendKeys(description);
    baseSteps.getDriver().switchTo().defaultContent();
  }

  private void selectActionStatus(String option) {
    webDriverHelpers.clickWebElementByText(ACTION_STATUS_OPTIONS, option);
  }
}
