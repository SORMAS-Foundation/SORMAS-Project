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

import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.Contact;
import org.sormas.e2etests.steps.web.application.contacts.CreateNewContactSteps;

public class EditContactPersonSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Contact aContact;

  @Inject
  public EditContactPersonSteps(WebDriverHelpers webDriverHelpers, final SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit Contact Person page",
        () -> {
          aContact = collectContactData();
          softly
              .assertThat(aContact.getFirstName())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getFirstName());
          softly
              .assertThat(aContact.getLastName())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getLastName());
          softly
              .assertThat(aContact.getDateOfBirth())
              .isEqualTo(CreateNewContactSteps.contact.getDateOfBirth());
          softly.assertThat(aContact.getSex()).isEqualTo(CreateNewContactSteps.contact.getSex());
          softly
              .assertThat(aContact.getNationalHealthId())
              .isEqualTo(CreateNewContactSteps.contact.getNationalHealthId());
          softly
              .assertThat(aContact.getPassportNumber())
              .isEqualTo(CreateNewContactSteps.contact.getPassportNumber());
          softly
              .assertThat(aContact.getPrimaryEmailAddress())
              .isEqualTo(CreateNewContactSteps.contact.getPrimaryEmailAddress());
          softly
              .assertThat(aContact.getPrimaryPhoneNumber())
              .isEqualTo(CreateNewContactSteps.contact.getPrimaryPhoneNumber());
          softly.assertAll();
        });
  }

  public Contact collectContactData() {
    webDriverHelpers.scrollToElement(CONTACT_PERSON_TAB);
    webDriverHelpers.clickOnWebElementBySelector(CONTACT_PERSON_TAB);
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(PERSON_UUID_INPUT);
    Contact contactInfo = getContactInformation();

    return Contact.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .dateOfBirth(contactInfo.getDateOfBirth())
        .sex(webDriverHelpers.getValueFromWebElement(SEX_INPUT))
        .nationalHealthId(webDriverHelpers.getValueFromWebElement(NATIONAL_HEALTH_ID_INPUT))
        .passportNumber(webDriverHelpers.getValueFromWebElement(PASSPORT_NUMBER_INPUT))
        .primaryEmailAddress(webDriverHelpers.getTextFromPresentWebElement(EMAIL_FIELD))
        .primaryPhoneNumber(webDriverHelpers.getTextFromPresentWebElement(PHONE_FIELD))
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
