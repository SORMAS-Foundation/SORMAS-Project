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

package org.sormas.e2etests.comparators;

import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.pojo.web.Person;

public class PersonComparator {

  private final SoftAssertions softly;

  @Inject
  public PersonComparator(SoftAssertions softly) {
    this.softly = softly;
  }

  public void comparePersonsAreEqual(Person person1, Person person2) {
    softly.assertThat(person1.getUuid()).isEqualToIgnoringCase(person2.getUuid());
    softly.assertThat(person1.getFirstName()).isEqualToIgnoringCase(person2.getFirstName());
    softly.assertThat(person1.getLastName()).isEqualToIgnoringCase(person2.getLastName());
    softly.assertThat(person1.getDateOfBirth()).isEqualTo(person2.getDateOfBirth());
    softly.assertThat(person1.getSalutation()).isEqualToIgnoringCase(person2.getSalutation());
    softly.assertThat(person1.getSex()).isEqualToIgnoringCase(person2.getSex());
    softly
        .assertThat(person1.getPresentConditionOfPerson())
        .isEqualToIgnoringCase(person2.getPresentConditionOfPerson());
    softly
        .assertThat(person1.getPassportNumber())
        .isEqualToIgnoringCase(person2.getPassportNumber());
    softly
        .assertThat(person1.getNationalHealthId())
        .isEqualToIgnoringCase(person2.getNationalHealthId());
    softly.assertThat(person1.getExternalId()).isEqualToIgnoringCase(person2.getExternalId());
    softly.assertThat(person1.getExternalToken()).isEqualToIgnoringCase(person2.getExternalToken());
    softly
        .assertThat(person1.getTypeOfOccupation())
        .isEqualToIgnoringCase(person2.getTypeOfOccupation());
    softly
        .assertThat(person1.getStaffOfArmedForces())
        .isEqualToIgnoringCase(person2.getStaffOfArmedForces());
    softly.assertThat(person1.getEducation()).isEqualToIgnoringCase(person2.getEducation());
    softly.assertThat(person1.getRegion()).isEqualToIgnoringCase(person2.getRegion());
    softly.assertThat(person1.getDistrict()).isEqualToIgnoringCase(person2.getDistrict());
    softly.assertThat(person1.getCommunity()).isEqualToIgnoringCase(person2.getCommunity());
    softly
        .assertThat(person1.getFacilityCategory())
        .isEqualToIgnoringCase(person2.getFacilityCategory());
    softly.assertThat(person1.getFacilityType()).isEqualToIgnoringCase(person2.getFacilityType());
    softly.assertThat(person1.getFacility()).isEqualToIgnoringCase(person2.getFacility());
    softly
        .assertThat(person1.getFacilityNameAndDescription())
        .isEqualToIgnoringCase(person2.getFacilityNameAndDescription());
    softly.assertThat(person1.getStreet()).isEqualToIgnoringCase(person2.getStreet());
    softly.assertThat(person1.getHouseNumber()).isEqualToIgnoringCase(person2.getHouseNumber());
    softly
        .assertThat(person1.getAdditionalInformation())
        .isEqualToIgnoringCase(person2.getAdditionalInformation());
    softly.assertThat(person1.getPostalCode()).isEqualToIgnoringCase(person2.getPostalCode());
    softly.assertThat(person1.getCity()).isEqualToIgnoringCase(person2.getCity());
    softly.assertThat(person1.getAreaType()).isEqualToIgnoringCase(person2.getAreaType());

    softly
        .assertThat(person1.getContactPersonFirstName())
        .isEqualToIgnoringCase(person2.getContactPersonFirstName());
    softly
        .assertThat(person1.getContactPersonLastName())
        .isEqualToIgnoringCase(person2.getContactPersonLastName());
    softly
        .assertThat(person1.getCommunityContactPerson())
        .isEqualToIgnoringCase(person2.getCommunityContactPerson());
    softly.assertThat(person1.getBirthName()).isEqualToIgnoringCase(person2.getBirthName());
    softly.assertThat(person1.getNickname()).isEqualToIgnoringCase(person2.getNickname());
    softly
        .assertThat(person1.getMotherMaidenName())
        .isEqualToIgnoringCase(person2.getMotherMaidenName());
    softly.assertThat(person1.getMotherName()).isEqualToIgnoringCase(person2.getMotherName());
    softly.assertThat(person1.getFatherName()).isEqualToIgnoringCase(person2.getFatherName());
    softly
        .assertThat(person1.getNameOfGuardians())
        .isEqualToIgnoringCase(person2.getNameOfGuardians());
    softly
        .assertThat(person1.getPersonContactDetailsContactInformation())
        .isEqualToIgnoringCase(person2.getPersonContactDetailsContactInformation());
    softly
        .assertThat(person1.getPersonContactDetailsTypeOfContactDetails())
        .isEqualToIgnoringCase(person2.getPersonContactDetailsTypeOfContactDetails());
    softly.assertAll();
  }

  public void checkPersonAreDifferent(Person person1, Person person2) {
    softly.assertThat(person1.getFirstName()).isNotEqualToIgnoringCase(person2.getFirstName());
    softly.assertThat(person1.getLastName()).isNotEqualToIgnoringCase(person2.getLastName());
    softly
        .assertThat(person1.getPassportNumber())
        .isNotEqualToIgnoringCase(person2.getPassportNumber());
    softly
        .assertThat(person1.getNationalHealthId())
        .isNotEqualToIgnoringCase(person2.getNationalHealthId());
    softly.assertThat(person1.getExternalId()).isNotEqualToIgnoringCase(person2.getExternalId());
    softly
        .assertThat(person1.getExternalToken())
        .isNotEqualToIgnoringCase(person2.getExternalToken());
    softly.assertThat(person1.getStreet()).isNotEqualToIgnoringCase(person2.getStreet());
    softly.assertThat(person1.getHouseNumber()).isNotEqualToIgnoringCase(person2.getHouseNumber());
    softly.assertThat(person1.getPostalCode()).isNotEqualToIgnoringCase(person2.getPostalCode());
    softly.assertThat(person1.getCity()).isNotEqualToIgnoringCase(person2.getCity());
    softly
        .assertThat(person1.getContactPersonFirstName())
        .isNotEqualToIgnoringCase(person2.getContactPersonFirstName());
    softly
        .assertThat(person1.getContactPersonLastName())
        .isNotEqualToIgnoringCase(person2.getContactPersonLastName());
    softly
        .assertThat(person1.getCommunityContactPerson())
        .isNotEqualToIgnoringCase(person2.getCommunityContactPerson());
    softly.assertThat(person1.getBirthName()).isNotEqualToIgnoringCase(person2.getBirthName());
    softly.assertThat(person1.getNickname()).isNotEqualToIgnoringCase(person2.getNickname());
    softly
        .assertThat(person1.getMotherMaidenName())
        .isNotEqualToIgnoringCase(person2.getMotherMaidenName());
    softly.assertThat(person1.getMotherName()).isNotEqualToIgnoringCase(person2.getMotherName());
    softly.assertThat(person1.getFatherName()).isNotEqualToIgnoringCase(person2.getFatherName());
    softly
        .assertThat(person1.getNameOfGuardians())
        .isNotEqualToIgnoringCase(person2.getNameOfGuardians());
    softly.assertAll();
  }
}
