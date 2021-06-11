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

package org.sormas.e2etests.steps.web.application.events;

import static org.sormas.e2etests.pages.application.actions.EditActionPage.EDIT_ACTION_POPUP;
import static org.sormas.e2etests.pages.application.events.EventActionsPage.EDIT_SPECIFIC_EVENT_BUTTON;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Action;
import org.sormas.e2etests.steps.web.application.actions.CreateNewActionSteps;

public class EventActionsSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Action createdAction;

  @Inject
  public EventActionsSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I open the Action recently created from Event tab",
        () -> {
          createdAction = CreateNewActionSteps.action;
          clickOnActionEditButtonByTitle(createdAction.getTitle());
        });
  }

  private void clickOnActionEditButtonByTitle(String actionTitle) {
    By actionEditButton = By.xpath(String.format(EDIT_SPECIFIC_EVENT_BUTTON, actionTitle));
    webDriverHelpers.clickOnWebElementBySelector(actionEditButton);
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(EDIT_ACTION_POPUP);
  }
}
