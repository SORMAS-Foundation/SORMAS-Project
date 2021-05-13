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

package org.sormas.e2etests.steps.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID;

import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import cucumber.api.java8.En;
import org.sormas.e2etests.pojo.Contact;
import org.sormas.e2etests.services.ContactService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class CreateNewContactSteps implements En {
    private final WebDriverHelpers webDriverHelpers;
    protected static Contact contact;

    @Inject
    public CreateNewContactSteps(WebDriverHelpers webDriverHelpers, ContactService contactService) {
        this.webDriverHelpers = webDriverHelpers;

        When(
                "^I create a new contact$",
                () -> {
                    contact = contactService.buildGeneratedContact();
                    fillFirstName(contact.getFirstName());
                    fillLastName(contact.getLastName());
                    fillDateOfBirth(contact.getDateOfBirth());
                    selectSex(contact.getSex());
                    fillNationalHealthId(contact.getNationalHealthId());
                    fillPassportNumber(contact.getPassportNumber());
                    fillPrimaryPhoneNumber(contact.getPrimaryPhoneNumber());
                    fillPrimaryEmailAddress(contact.getPrimaryEmailAddress());
                    selectReturningTraveler(contact.getReturningTraveler());
                    fillDateOfReport(contact.getReportDate());
                    fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
                    fillCaseIdInExternalSystem(contact.getCaseIdInExternalSystem());
                    fillDateOfLastContact(contact.getDateOfLastContact());
                    fillCaseOrEventInformation(contact.getCaseOrEventInformation());
                    selectResponsibleRegion(contact.getResponsibleRegion());
                    selectResponsibleDistrict(contact.getResponsibleDistrict());
                    selectResponsibleCommunity(contact.getResponsibleCommunity());
                    selectTypeOfContact("any");
                    fillAdditionalInformationOnTheTypeOfContact(contact.getAdditionalInformationOnContactType());
                    selectContactCategory(contact.getContactCategory());
                    fillRelationshipWithCase(contact.getRelationshipWithCase());
                    fillDescriptionOfHowContactTookPlace(contact.getDescriptionOfHowContactTookPlace());
                    webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
                    webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID);
                });
    }

    public void fillFirstName(String firstName) {
        webDriverHelpers.fillInWebElement(FIRST_NAME_OF_CONTACT_PERSON_INPUT, firstName);
    }

    public void fillLastName(String lastName) {
        webDriverHelpers.fillInWebElement(LAST_NAME_OF_CONTACT_PERSON_INPUT, lastName);
    }

    public void fillDateOfBirth(LocalDate localDate) {
        webDriverHelpers.selectFromCombobox(DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
        webDriverHelpers.selectFromCombobox(DATE_OF_BIRTH_MONTH_COMBOBOX, localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        webDriverHelpers.selectFromCombobox(DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
    }

    public void selectSex(String sex) {
        webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
    }

    public void fillNationalHealthId(String nationalHealthId) {
        webDriverHelpers.fillInWebElement(NATIONAL_HEALTH_ID_INPUT, nationalHealthId);
    }

    public void fillPassportNumber(String passportNumber) {
        webDriverHelpers.fillInWebElement(PASSPORT_NUMBER_INPUT, passportNumber);
    }

    public void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
        webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
    }

    public void fillPrimaryEmailAddress(String primaryPhoneNumber) {
        webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryPhoneNumber);
    }

    public void selectReturningTraveler(String option) {
        String optionBox = "//div[@id='returningTraveler']//label[contains(text(),'" + option + "')]";
        By optionBoxXpath = By.xpath(optionBox);
        webDriverHelpers.clickOnWebElementBySelector(optionBoxXpath);
    }

    public void fillDateOfReport(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
        webDriverHelpers.clearWebElement(DATE_OF_REPORT_INPUT);  // TO BE DISCUSSED
        webDriverHelpers.fillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
    }

    public void fillDiseaseOfSourceCase(String diseaseOrCase) {
        webDriverHelpers.selectFromCombobox(DISEASE_OF_SOURCE_CASE_COMBOBOX, diseaseOrCase);
    }
    public void fillCaseIdInExternalSystem(String externalId) {
        webDriverHelpers.fillInWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT, externalId);
    }

    public void fillDateOfLastContact(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
        webDriverHelpers.fillInWebElement(DATE_OF_LAST_CONTACT_INPUT, formatter.format(date));
    }

    public void fillCaseOrEventInformation(String caseOrEventInfo) {
        webDriverHelpers.fillInWebElement(CASE_OR_EVENT_INFORMATION_INPUT, caseOrEventInfo);
    }

    public void selectResponsibleRegion(String selectResponsibleRegion) {
        webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
    }

    public void selectResponsibleDistrict(String responsibleDistrict) {
        webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
    }

    public void selectResponsibleCommunity(String responsibleCommunity) {
        webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
    }

    public void selectTypeOfContact(String type){
        Random random = new Random();
        HashMap<String, String> GENERAL_CONTACT_TYPES = new HashMap<String, String>();
        HashMap<String, String> COVID19_CONTACT_TYPES = new HashMap<String, String>();

        GENERAL_CONTACT_TYPES.put("TFOSC", "Touched fluid of source case");
        GENERAL_CONTACT_TYPES.put("DPCWSC", "Direct physical contact with source case");
        GENERAL_CONTACT_TYPES.put("MOCOOOOSC", "Manipulation of clothes or other objects of source case");
        GENERAL_CONTACT_TYPES.put("WICPWSC", "Was in close proximity (1 meter) with source case");
        GENERAL_CONTACT_TYPES.put("WISROHWSC", "Was in same room or house with source case");

        COVID19_CONTACT_TYPES.put("FTFCOAL15M", "Face-to-face contact of at least 15 minutes");
        COVID19_CONTACT_TYPES.put("TFOSC", "Touched fluid of source case");
        COVID19_CONTACT_TYPES.put("PETAPA", "Persons exposed to aerosol producing activities");
        COVID19_CONTACT_TYPES.put("MPWAHROE", "Medical personnel with a high risk of exposure, e.g. unprotected relevant exposure to secretions, exposure to aerosols from COVID-19 cases");
        COVID19_CONTACT_TYPES.put("WISROHWSC", "Was in same room or house with source case");
        COVID19_CONTACT_TYPES.put("FTFCOLT15M", "Face-to-face contact of less than 15 minutes");
        COVID19_CONTACT_TYPES.put("MPTWISROHWSC", "Medical personnel that was in same room or house with source case");
        COVID19_CONTACT_TYPES.put("MPASP>2MOWPE", "Medical personnel at save proximity (> 2 meter) or with protective equipment");
        COVID19_CONTACT_TYPES.put("MPASP>2MWDC", "Medical personnel at save proximity (> 2 meter), without direct contact with secretions or excretions of the patient and without aerosol exposure");

        String selectedDisease = contact.getDiseaseOfSourceCase();
        StringBuilder typeOfContact = new StringBuilder();
        if(selectedDisease.equalsIgnoreCase("COVID-19")){
            if(type.equalsIgnoreCase("any") || type.equalsIgnoreCase("random") || type == null)
                typeOfContact.append("//label[contains(text(), '" + COVID19_CONTACT_TYPES.values().toArray()[random.nextInt(COVID19_CONTACT_TYPES.size())] +"')]");
            else
                typeOfContact.append("//label[contains(text(), '" + COVID19_CONTACT_TYPES.get(type) +"')]");
        }
        else{
            if(type.equalsIgnoreCase("any") || type.equalsIgnoreCase("random") || type == null)
                typeOfContact.append("//label[contains(text(), '" + GENERAL_CONTACT_TYPES.values().toArray()[random.nextInt(GENERAL_CONTACT_TYPES.size())] +"')]");
            else
                typeOfContact.append("//label[contains(text(), '" + GENERAL_CONTACT_TYPES.get(type) +"')]");
        }


        By typeOfContactXpath = By.xpath(typeOfContact.toString());
        webDriverHelpers.clickOnWebElementBySelector(typeOfContactXpath);
    }

    public void selectContactCategory(String categoryOption){
        String typeOfContact = "//label[contains(text(), '" + categoryOption + "')]";
        By typeOfContactXpath = By.xpath(typeOfContact);
        webDriverHelpers.clickOnWebElementBySelector(typeOfContactXpath);
    }

    public void fillAdditionalInformationOnTheTypeOfContact(String description) {
        webDriverHelpers.fillInWebElement(ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT, description);
    }

    public void fillRelationshipWithCase(String relationshipWithCase) {
        webDriverHelpers.selectFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX, relationshipWithCase);
    }

    public void fillDescriptionOfHowContactTookPlace(String descriptionOfHowContactTookPlace){
        webDriverHelpers.fillInWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT, descriptionOfHowContactTookPlace);
    }
}
