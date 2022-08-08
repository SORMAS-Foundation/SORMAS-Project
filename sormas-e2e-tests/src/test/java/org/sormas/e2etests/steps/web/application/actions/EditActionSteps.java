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

import static org.sormas.e2etests.pages.application.actions.EditActionPage.*;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.ACTION_CONFIRM_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_UPLOADED_TEST_FILE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NEW_DOCUMENT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.START_DATA_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.FILE_PICKER;
import static org.sormas.e2etests.steps.web.application.cases.CreateNewCaseSteps.userDirPath;

import cucumber.api.java8.En;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Action;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;

public class EditActionSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  public static Action action;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public EditActionSteps(
      WebDriverHelpers webDriverHelpers, BaseSteps baseSteps, AssertHelpers assertHelpers) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "I check that Action created from Event tab is correctly displayed in Event Actions tab",
        () -> {
          action = CreateNewActionSteps.action;
          Action collectedAction = collectActionData();
          ComparisonHelper.compareEqualEntities(action, collectedAction);
        });

    When(
        "I click on ([^\"]*) button from NEW DOCUMENT in Event Action tab",
        (String buttonName) -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_DOCUMENT_BUTTON);
          webDriverHelpers.clickWebElementByText(START_DATA_IMPORT_BUTTON, buttonName);
        });
    When(
        "I upload ([^\"]*) file to the Event Action",
        (String fileType) -> {
          webDriverHelpers.sendFile(FILE_PICKER, userDirPath + "/uploads/" + fileType);
        });
    When(
        "I check if ([^\"]*) file is available in Event Action documents",
        (String fileType) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(CASE_UPLOADED_TEST_FILE, fileType)), 5);
        });
    When(
        "I check if ([^\"]*) file is downloaded correctly from Event Action tab",
        (String filename) -> {
            FilesHelper.waitForFileToDownload(filename, 50);
            FilesHelper.deleteFile(filename);
        });
    When(
        "I download last updated document file from Event Action tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DOWNLOAD_LAST_UPDATED_DOCUMENT);
        });
    When(
        "I delete last uploaded document file from Event Action tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DELETE_LAST_UPDATED_DOCUMENT);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_POPUP_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I check if last uploaded file was deleted from document files in Event Action tab",
        () -> {
          webDriverHelpers.checkWebElementContainsText(
              NO_DOCUMENT_TEXT, "There are no documents for this Action");
        });
  }

  private Action collectActionData() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(DATE_INPUT);
    WebElement descriptionIFrame = baseSteps.getDriver().findElement(DESCRIPTION_IFRAME);
    baseSteps.getDriver().switchTo().frame(descriptionIFrame);
    String descriptionFieldData = webDriverHelpers.getTextFromWebElement(DESCRIPTION_INPUT);
    baseSteps.getDriver().switchTo().defaultContent();

    return Action.builder()
        .date(LocalDate.parse(collectedDateOfReport, DATE_FORMATTER))
        .priority(webDriverHelpers.getValueFromCombobox(PRIORITY_COMBOBOX))
        .measure(webDriverHelpers.getValueFromCombobox(MEASURE_COMBOBOX))
        .title(webDriverHelpers.getValueFromWebElement(TITLE_INPUT))
        .description(descriptionFieldData)
        .actionStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ACTION_STATUS_OPTIONS))
        .build();
  }
}
