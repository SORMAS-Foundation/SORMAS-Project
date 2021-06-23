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

import static java.util.function.Predicate.not;
import static org.sormas.e2etests.pages.application.actions.EditActionPage.EDIT_ACTION_POPUP;
import static org.sormas.e2etests.pages.application.events.EventActionsPage.EDIT_SPECIFIC_EVENT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.*;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Action;
import org.sormas.e2etests.pojo.web.EventActionTableEntry;
import org.sormas.e2etests.pojo.web.Task;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.actions.CreateNewActionSteps;
import org.sormas.e2etests.steps.web.application.tasks.ColumnHeaders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EventActionsSteps implements En {

    private final WebDriverHelpers webDriverHelpers;
    public static Action createdAction;
    private ApiState apiState;
    private final BaseSteps baseSteps;
    private List<EventActionTableEntry> actionsTableRows;

    @Inject
    public EventActionsSteps(
        WebDriverHelpers webDriverHelpers,
        BaseSteps baseSteps,
        ApiState apiState,
        SoftAssertions softly,
        Properties properties) {
            this.webDriverHelpers = webDriverHelpers;
            this.baseSteps = baseSteps;

        When(
                "I open the Action recently created from Event tab",
                () -> {
                    createdAction = CreateNewActionSteps.action;
                    clickOnActionEditButtonByTitle(createdAction.getTitle());
                });


        Then(
                "I search last created Event by API using EVENT UUID and wait for (\\d+) entries in the table",
                (Integer expectedEntries) ->

                {
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FILTER_BY_GENERAL_INPUT);
                    webDriverHelpers.fillAndSubmitInWebElement(
                            FILTER_BY_GENERAL_INPUT, apiState.getCreatedEvent().getUuid());
                    webDriverHelpers.waitUntilNumberOfElementsIsReduceToGiven(EVENT_ACTIONS_TABLE_ROW, expectedEntries);
                });

        When(
                "^I collect the task column objects$",
                () -> {
                    List<Map<String, String>> tableRowsData = getTableRowsData();
                    actionsTableRows = new ArrayList<>();
                    tableRowsData.forEach(
                            tableRow ->
                                    actionsTableRows.add(
                                            EventActionTableEntry.builder()
                                             ///////--todo---------------------------------------------------------------------
                                                    .build()));
                });

        Then(
                "^I am checking if all the fields are correctly displayed in the Task Management table$",
                () -> {
                    org.sormas.e2etests.pojo.api.Task expectedTask = apiState.getCreatedTask();
                    EventActionTableEntry actualEventAction =
                            actionsTableRows.stream()
                                    .findFirst()
                                    .orElseThrow();
                    softly
                            .assertThat(apiState.getCreatedEvent().getUuid())
                            .containsIgnoringCase(
                                    getPartialUuidFromAssociatedLink(actualEventAction.getEventId()));
                            //////////-----------------todo-----------------------------------------------------------------------------
                    softly.assertAll();
                });

    }

        private void clickOnActionEditButtonByTitle (String actionTitle){
            By actionEditButton = By.xpath(String.format(EDIT_SPECIFIC_EVENT_BUTTON, actionTitle));
            webDriverHelpers.clickOnWebElementBySelector(actionEditButton);
            webDriverHelpers.waitUntilIdentifiedElementIsPresent(EDIT_ACTION_POPUP);
        }

        private List<Map<String, String>> getTableRowsData () {
            Map<String, Integer> headers = extractColumnHeadersHashMap();
            List<WebElement> tableRows = getTableRows();
            List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
            tableRows.forEach(
                    table -> {
                        HashMap<Integer, String> indexWithData = new HashMap<>();
                        AtomicInteger atomicInt = new AtomicInteger();
                        List<WebElement> tableData = table.findElements(EVENT_ACTIONS_TABLE_DATA);
                        tableData.forEach(
                                dataText -> {
                                    webDriverHelpers.scrollToElementUntilIsVisible(dataText);
                                    indexWithData.put(atomicInt.getAndIncrement(), dataText.getText());
                                });
                        tableDataList.add(indexWithData);
                    });
            List<Map<String, String>> tableObjects = new ArrayList<>();
            tableDataList.forEach(
                    row -> {
                        ConcurrentHashMap<String, String> objects = new ConcurrentHashMap<>();
                        headers.forEach((headerText, index) -> objects.put(headerText, row.get(index)));
                        tableObjects.add(objects);
                    });
            return tableObjects;
        }

    private List<WebElement> getTableRows() {
        webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(COLUMN_HEADERS_TEXT);
        return baseSteps.getDriver().findElements(TABLE_ROW);
    }

    private Map<String, Integer> extractColumnHeadersHashMap() {
        AtomicInteger atomicInt = new AtomicInteger();
        HashMap<String, Integer> headerHashmap = new HashMap<>();
        webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(COLUMN_HEADERS_TEXT);
        webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(COLUMN_HEADERS_TEXT);
        baseSteps
                .getDriver()
                .findElements(COLUMN_HEADERS_TEXT)
                .forEach(
                        webElement -> {
                            webDriverHelpers.scrollToElementUntilIsVisible(webElement);
                            headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
                        });
        return headerHashmap;
    }

    private LocalDateTime getLocalDateTimeFromColumns(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
        return LocalDateTime.parse(date, formatter);
    }

    private String getPartialUuidFromAssociatedLink(String associatedLink) {
        return StringUtils.left(associatedLink, 6);
    }
}


