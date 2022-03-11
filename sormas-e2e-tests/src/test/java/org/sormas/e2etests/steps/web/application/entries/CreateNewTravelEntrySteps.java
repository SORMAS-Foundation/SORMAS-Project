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

package org.sormas.e2etests.steps.web.application.entries;

import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.LAST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.TRAVEL_ENTRY_PERSON_TAB;

import cucumber.api.java8.En;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.TravelEntry;
import org.sormas.e2etests.entities.services.TravelEntryService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage;
import org.sormas.e2etests.pages.application.entries.EditTravelEntryPage;
import org.sormas.e2etests.state.ApiState;

public class CreateNewTravelEntrySteps implements En {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static TravelEntry travelEntry;
  public static TravelEntry aTravelEntry;

  @Inject
  public CreateNewTravelEntrySteps(
      WebDriverHelpers webDriverHelpers, TravelEntryService travelEntryService, ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I fill the required fields in a new travel entry form$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryDE();
          fillFirstName(travelEntry.getFirstName());
          fillLastName(travelEntry.getLastName());
          selectSex(travelEntry.getSex());
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntry.getResponsibleCommunity());
          fillDisease(travelEntry.getDisease());
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });

    When(
        "^I fill the required fields in a new case travel entry form$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryDE();
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntry.getResponsibleCommunity());
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });

    When(
        "^I click on Save button from the new travel entry form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "^I navigate to person tab in Edit travel entry page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(TRAVEL_ENTRY_PERSON_TAB);
        });

    When(
        "I check the created data is correctly displayed on Edit travel entry page for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          aTravelEntry = collectTravelEntryData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              aTravelEntry,
              travelEntry,
              List.of(
                  "disease",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "pointOfEntry",
                  "pointOfEntryDetails"));
        });

    When(
        "I check the created data is correctly displayed on Edit case travel entry page for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          aTravelEntry = collectTravelEntryData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              aTravelEntry,
              travelEntry,
              List.of(
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "pointOfEntry",
                  "pointOfEntryDetails"));
        });

    When(
        "I check the created data is correctly displayed on Edit travel entry person page for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          aTravelEntry = collectTravelEntryPersonData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              aTravelEntry, travelEntry, List.of("firstName", "lastName", "sex"));
        });
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_OF_CONTACT_PERSON_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_OF_CONTACT_PERSON_INPUT, lastName);
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(CreateNewTravelEntryPage.SEX_COMBOBOX, sex);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(
        CreateNewTravelEntryPage.RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(
        CreateNewTravelEntryPage.RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(
        CreateNewTravelEntryPage.RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  private void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(CreateNewTravelEntryPage.DISEASE_COMBOBOX, disease);
  }

  private void fillPointOfEntry(String pointOfEntry) {
    webDriverHelpers.selectFromCombobox(
        CreateNewTravelEntryPage.POINT_OF_ENTRY_COMBOBOX, pointOfEntry);
  }

  private void fillPointOfEntryDetails(String pointOfEntryDetails) {
    webDriverHelpers.fillInWebElement(
        CreateNewTravelEntryPage.POINT_OF_ENTRY_DETAILS_INPUT, pointOfEntryDetails);
  }

  private TravelEntry collectTravelEntryData() {
    return TravelEntry.builder()
        .disease(webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.DISEASE_COMBOBOX))
        .responsibleRegion(
            webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(
            webDriverHelpers.getValueFromCombobox(
                EditTravelEntryPage.RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(
            webDriverHelpers.getValueFromCombobox(
                EditTravelEntryPage.RESPONSIBLE_COMMUNITY_COMBOBOX))
        .pointOfEntry(
            webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.POINT_OF_ENTRY_COMBOBOX))
        .pointOfEntryDetails(
            webDriverHelpers.getValueFromWebElement(
                EditTravelEntryPage.POINT_OF_ENTRY_DETAILS_INPUT))
        .build();
  }

  private TravelEntry collectTravelEntryPersonData() {
    return TravelEntry.builder()
        .firstName(webDriverHelpers.getValueFromWebElement(FIRST_NAME_INPUT))
        .lastName(webDriverHelpers.getValueFromWebElement(LAST_NAME_INPUT))
        .sex(webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.SEX_COMBOBOX))
        .build();
  }
}
