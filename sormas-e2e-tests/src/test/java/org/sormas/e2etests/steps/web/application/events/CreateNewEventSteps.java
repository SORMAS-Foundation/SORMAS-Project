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

import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.*;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.ADD_PARTICIPANT_BUTTON;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;

import javax.inject.Inject;

import org.sormas.e2etests.helpers.WebDriverHelpers;

public class CreateNewEventSteps implements En {

    private final WebDriverHelpers webDriverHelpers;

    @Inject
    public CreateNewEventSteps(WebDriverHelpers webDriverHelpers) {
        this.webDriverHelpers = webDriverHelpers;

        When(
                "^I create a new event",
                () -> {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    webDriverHelpers.fillInWebElement(TITLE_INPUT, "EVENT_AUTOMATION" + timestamp + Faker.instance().name().name());
                    webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(ADD_PARTICIPANT_BUTTON);
                });

        When(
                "^I validate create a new event popup",
                () -> {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    webDriverHelpers.fillInWebElement(TITLE_INPUT, "EVENT_AUTOMATION_" + timestamp);
                    selectEventStatus("SIGNAL");
                    selectEventStatus("EVENT");
                    selectEventStatus("SCREENING");
                    selectEventStatus("CLUSTER");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(PRIMARY_MODE_OF_TRANSMISSION);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(NOSOCOMIAL);
                    selectEventStatus("DROPPED");
                    selectEventManagementStatusOption("PENDING");
                    selectEventManagementStatusOption("ONGOING");
                    selectEventManagementStatusOption("DONE");
                    selectEventManagementStatusOption("CLOSED");
                    selectRiskLevel("Low risk");
                    selectRiskLevel("Moderate risk");
                    selectRiskLevel("High risk");
                    selectRiskLevel("Unknown");
                    webDriverHelpers.scrollToElement(DISEASE_INPUT);
                    selectEventInvestigationStatusOptions("INVESTIGATION PENDING");
                    selectEventInvestigationStatusOptions("ONGOING INVESTIGATION");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_INVESTIGATION_START_DATE);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_INVESTIGATION_END_DATE);
                    selectEventInvestigationStatusOptions("INVESTIGATION DONE");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_INVESTIGATION_START_DATE);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_INVESTIGATION_END_DATE);
                    selectEventInvestigationStatusOptions("INVESTIGATION DISCARDED");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_INVESTIGATION_START_DATE);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_INVESTIGATION_END_DATE);
                    selectDisease("Acute Flaccid Paralysis");
                    selectDisease("Anthrax");
                    selectDisease("COVID-19");
                    selectDisease("Cholera");
                    selectDisease("Congenital Rubella");
                    selectDisease("Dengue Fever");
                    selectDisease("Ebola Virus Disease");
                    selectDisease("Guinea Worm");
                    selectDisease("Human Rabies");
                    selectDisease("Influenza (New subtype)");
                    selectDisease("Lassa");
                    selectDisease("Measles");
                    selectDisease("Meningitis (CSM)");
                    selectDisease("Monkeypox");
                    selectDisease("Not Yet Defined");
                    selectDisease("Other Epidemic Disease");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(DISEASE_NAME);
                    selectDisease("Plague");
                    selectDisease("Poliomyelitis");
                    selectDisease("Unspecified VHF");
                    selectDisease("Yellow Fever");
                    selectSourceType("Not applicable");
                    selectSourceType("Mathematical model");
                    selectSourceType("Media/News");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_MEDIA_WEBSITE);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_MEDIA_NAME);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_MEDIA_DETAILS);
                    selectSourceType("Hotline/Person");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_FIRST_NAME);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_LAST_NAME);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_TEL_NO);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_EMAIL);
                    selectSourceType("Institutional partner");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_FIRST_NAME);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_LAST_NAME);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_TEL_NO);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_EMAIL);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_INSTITUTIONAL_PARTNER);
                    selectSourceInstitutionalPartner("Other");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_INSTITUTIONAL_PARTNER_DETAILS);
                    selectTypeOfPlace("Facility");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_CATEGORY);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_TYPE);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY);
                    selectTypeOfPlace("Festivities");
                    selectTypeOfPlace("Home");
                    selectTypeOfPlace("Means of transport");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(MEANS_OF_TRANSPORT);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONNECTION_NUMBER);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(TRAVEL_DATE);
                    selectMeansOfTransport("Local public transport");
                    selectMeansOfTransport("Bus");
                    selectMeansOfTransport("Ship/Ferry");
                    selectMeansOfTransport("Plane");
                    selectMeansOfTransport("Train");
                    selectMeansOfTransport("Other");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(MEANS_OF_TRANSPORT_DETAILS);
                    selectTypeOfPlace("Public place");
                    selectTypeOfPlace("Scattered");
                    selectTypeOfPlace("Unknown");
                    selectTypeOfPlace("Other");
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(SPECIFY_OTHER_EVENT_PLACE);
                    webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON);
                });
    }

    public void selectEventStatus(String eventStatus) {
        webDriverHelpers.clickWebElementByText(EVENT_STATUS_OPTIONS, eventStatus);
    }

    public void selectRiskLevel(String riskLevel) {
        webDriverHelpers.selectFromCombobox(RISK_LEVEL_COMBOBOX, riskLevel);
    }

    public void selectEventManagementStatusOption(String eventManagementStatusOption) {
        webDriverHelpers.clickWebElementByText(EVENT_MANAGEMENT_STATUS_OPTIONS, eventManagementStatusOption);
    }

    public void fillStartData(String startData) {
        webDriverHelpers.clickWebElementByText(START_DATA_INPUT, startData);
    }

    public void fillSignalEvolutionDate(String signalEvolutionDate) {
        webDriverHelpers.clickWebElementByText(SIGNAL_EVOLUTION_DATE_INPUT, signalEvolutionDate);
    }

    public void selectEventInvestigationStatusOptions(String eventInvestigationStatusOption) {
        webDriverHelpers.clickWebElementByText(EVENT_INVESTIGATION_STATUS_OPTIONS, eventInvestigationStatusOption);
    }

    public void selectDisease(String disease) {
        webDriverHelpers.selectFromCombobox(DISEASE_INPUT, disease);
    }

    public void fillExternalIdInput(String externalId) {
        webDriverHelpers.clickWebElementByText(EXTERNAL_ID_INPUT, externalId);
    }

    public void fillInternalIdInput(String internalIdInput) {
        webDriverHelpers.fillInWebElement(INTERNAL_ID_INPUT, internalIdInput);
    }

    public void fillExternalTokenInput(String externalToken) {
        webDriverHelpers.fillInWebElement(EXTERNAL_TOKEN_INPUT, externalToken);
    }

    public void fillTitle(String title) {
        webDriverHelpers.fillInWebElement(TITLE_INPUT, title);
    }

    public void fillDescriptionInput(String description) {
        webDriverHelpers.fillInWebElement(DESCRIPTION_INPUT, description);
    }

    public void selectSourceType(String sourceType) {
        webDriverHelpers.selectFromCombobox(SOURCE_TYPE_COMBOBOX, sourceType);
    }

    public void selectSourceInstitutionalPartner(String institutionalPartner) {
        webDriverHelpers.selectFromCombobox(SOURCE_INSTITUTIONAL_PARTNER, institutionalPartner);
    }

    public void selectTypeOfPlace(String typeOfPlace) {
        webDriverHelpers.selectFromCombobox(TYPE_OF_PLACE_COMBOBOX, typeOfPlace);
    }

    public void selectMeansOfTransport(String meansOfTransport) {
        webDriverHelpers.selectFromCombobox(MEANS_OF_TRANSPORT, meansOfTransport);
    }

    public void selectCountryCombobox(String country) {
        webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, country);
    }

    public void selectRegion(String region) {
        webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, region);
    }
}
