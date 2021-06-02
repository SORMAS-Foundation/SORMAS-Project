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

package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.EditContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.CONTACT_PERSON_TAB;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Contact;

public class EditContactSteps implements En {
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static Contact aContact;

  @Inject
  public EditContactSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit Contact page",
        () -> {
          aContact = collectContactData();
          SoftAssertions softly = new SoftAssertions();
          softly
              .assertThat(aContact.getFirstName())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getFirstName());
          softly
              .assertThat(aContact.getLastName())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getLastName());
          softly
              .assertThat(aContact.getReturningTraveler())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getReturningTraveler());
          softly
              .assertThat(aContact.getReportDate())
              .isEqualTo(CreateNewContactSteps.contact.getReportDate());
          softly
              .assertThat(aContact.getDiseaseOfSourceCase())
              .isEqualTo(CreateNewContactSteps.contact.getDiseaseOfSourceCase());
          softly
              .assertThat(aContact.getCaseIdInExternalSystem())
              .isEqualTo(CreateNewContactSteps.contact.getCaseIdInExternalSystem());
          softly
              .assertThat(aContact.getDateOfLastContact())
              .isEqualTo(CreateNewContactSteps.contact.getDateOfLastContact());
          softly
              .assertThat(aContact.getCaseOrEventInformation())
              .isEqualTo(CreateNewContactSteps.contact.getCaseOrEventInformation());
          softly
              .assertThat(aContact.getResponsibleRegion())
              .isEqualTo(CreateNewContactSteps.contact.getResponsibleRegion());
          softly
              .assertThat(aContact.getResponsibleDistrict())
              .isEqualTo(CreateNewContactSteps.contact.getResponsibleDistrict());
          softly
              .assertThat(aContact.getResponsibleCommunity())
              .isEqualTo(CreateNewContactSteps.contact.getResponsibleCommunity());
          softly
              .assertThat(
                  aContact
                      .getAdditionalInformationOnContactType()
                      .equalsIgnoreCase(
                          CreateNewContactSteps.contact.getAdditionalInformationOnContactType()))
              .isTrue();
          softly
              .assertThat(aContact.getTypeOfContact())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getTypeOfContact());
          softly
              .assertThat(aContact.getContactCategory())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getContactCategory());
          softly
              .assertThat(aContact.getRelationshipWithCase())
              .isEqualTo(CreateNewContactSteps.contact.getRelationshipWithCase());
          softly
              .assertThat(aContact.getDescriptionOfHowContactTookPlace())
              .isEqualTo(CreateNewContactSteps.contact.getDescriptionOfHowContactTookPlace());
          softly.assertAll();
        });

    When(
        "I open Contact Person tab",
        () -> {
          webDriverHelpers.scrollToElement(CONTACT_PERSON_TAB);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_PERSON_TAB);
        });

    When(
        "I delete the contact",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
        });
  }

  public Contact collectContactData() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatter);
    String collectedLastDateOfContact =
        webDriverHelpers.getValueFromWebElement(DATE_OF_LAST_CONTACT_INPUT);
    LocalDate parsedLastDateOfContact = LocalDate.parse(collectedLastDateOfContact, formatter);
    Contact contactInfo = getContactInformation();

    return Contact.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .returningTraveler(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RETURNING_TRAVELER_OPTIONS))
        .reportDate(parsedDateOfReport)
        .diseaseOfSourceCase(webDriverHelpers.getValueFromWebElement(DISEASE_COMBOBOX))
        .caseIdInExternalSystem(
            webDriverHelpers.getValueFromWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT))
        .dateOfLastContact(parsedLastDateOfContact)
        .caseOrEventInformation(
            webDriverHelpers.getValueFromWebElement(CASE_OR_EVENT_INFORMATION_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(
            webDriverHelpers.getValueFromWebElement(RESPONSIBLE_COMMUNITY_COMBOBOX))
        .additionalInformationOnContactType(
            webDriverHelpers.getValueFromWebElement(
                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        .typeOfContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TYPE_OF_CONTACT_OPTIONS))
        .contactCategory(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(
            webDriverHelpers.getValueFromWebElement(RELATIONSHIP_WITH_CASE_COMBOBOX))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }

  public Contact getContactInformation() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    String contactInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] contactInfos = contactInfo.split(" ");
    LocalDate localDate = LocalDate.parse(contactInfos[3].replace(")", ""), formatter);
    return Contact.builder()
        .firstName(contactInfos[0])
        .lastName(contactInfos[1])
        .dateOfBirth(localDate)
        .build();
  }
}
